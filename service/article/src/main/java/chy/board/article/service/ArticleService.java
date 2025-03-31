package chy.board.article.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import chy.board.article.entity.Article;
import chy.board.article.entity.BoardArticleCount;
import chy.board.article.repository.ArticleRepository;
import chy.board.article.repository.BoardArticleCountRepository;
import chy.board.article.service.request.ArticleCreateRequest;
import chy.board.article.service.request.ArticleUpdateRequest;
import chy.board.article.service.response.ArticlePageResponse;
import chy.board.article.service.response.ArticleResponse;
import chy.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {
	private final Snowflake snowflake = new Snowflake();
	private final ArticleRepository articleRepository;
	private final BoardArticleCountRepository boardArticleCountRepository;

	@Transactional
	public ArticleResponse create(ArticleCreateRequest request) {
		String imageUrl = "Ang~butter"; // TODO S3에 이미지 업로드하는 코드 작성하기

		Article article = articleRepository.save(
			Article.create(
				snowflake.nextId(),
				request.getTitle(),
				request.getContent(),
				request.getBoardId(),
				request.getWriterId(),
				imageUrl
			)
		);

		int result = boardArticleCountRepository.increase(request.getBoardId());
		if (result == 0) {
			boardArticleCountRepository.save(
				BoardArticleCount.init(request.getBoardId(), String.valueOf(article.getArticleId()), 1L)
				// TODO 이 코드가 동작하는 일이 없게 게시판별 카테고리를 미리 만들어 놓자
			);
		}

		// TODO 게시글 생성 이벤트 발생시키는 코드 구현하기

		return ArticleResponse.from(article);
	}

	@Transactional
	public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
		String imageUrl = "R!"; // TODO S3에 이미지 업로드하는 코드 작성하기
		Article article = articleRepository.findById(articleId).orElseThrow(); // TODO 예외처리 코드 나중에 구현하기
		article.update(request.getTitle(), request.getContent(), imageUrl);
		// TODO 업데이트 이벤트 발생시키기
		return ArticleResponse.from(article);
	}

	public ArticleResponse read(Long articleId) {
		return ArticleResponse.from(articleRepository.findById(articleId).orElseThrow()); // TODO 예외처리 코드 나중에 구현하기
	}

	@Transactional
	public void delete(Long articleId) {
		Article article = articleRepository.findById(articleId).orElseThrow();
		articleRepository.delete(article);
		boardArticleCountRepository.decrease(article.getBoardId());
		// TODO 삭제 이벤트 발행
	}

	public ArticlePageResponse readAll(Long boardId, Long page, Long pageSize) {
		return ArticlePageResponse.of(
			articleRepository.findAll(boardId, pageSize, (page - 1) * pageSize).stream()
				.map(ArticleResponse::from)
				.toList(),
			articleRepository.count(
				boardId,
				PageLimitCalculator.calculatePageLimit(page, pageSize, 10L)
			)
		);
	}

	public List<ArticleResponse> readAllInfiniteScroll(Long boardId, Long pageSize, Long lastArticleId) {
		List<Article> articles = lastArticleId == null ?
			articleRepository.findAllInfiniteScroll(boardId, pageSize) :
			articleRepository.findAllInfiniteScroll(boardId, lastArticleId, pageSize);

		return articles.stream().map(ArticleResponse::from).toList();
	}

	public Long count(Long boardId) {
		return boardArticleCountRepository.findById(boardId)
			.map(BoardArticleCount::getArticleCount)
			.orElse(0L);
	}
}
