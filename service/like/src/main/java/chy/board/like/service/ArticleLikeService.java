package chy.board.like.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import chy.board.like.entity.ArticleLike;
import chy.board.like.entity.ArticleLikeCount;
import chy.board.like.repository.ArticleLikeCountRepository;
import chy.board.like.repository.ArticleLikeRepository;
import chy.board.like.service.response.ArticleLikeResponse;
import kuke.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
	private final Snowflake snowflake = new Snowflake();
	private final ArticleLikeRepository articleLikeRepository;
	private final ArticleLikeCountRepository articleLikeCountRepository;

	public ArticleLikeResponse read(Long articleId, Long userId) {
		return articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
			.map(ArticleLikeResponse::from)
			.orElseThrow();
	}

	@Transactional
	public void like(Long articleId, Long userId) {
		ArticleLike articleLike = articleLikeRepository.save(
			ArticleLike.create(
				snowflake.nextId(),
				articleId,
				userId
			)
		);

		int result = articleLikeCountRepository.increase(articleId);
		if (result == 0) {
			articleLikeCountRepository.save(
				ArticleLikeCount.init(articleId, 1L)
			);
		}

		// TODO 좋아요 이벤트 발생 시키기
	}

	@Transactional
	public void unlike(Long articleId, Long userId) {
		articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
			.ifPresent(articleLike -> {
					articleLikeRepository.delete(articleLike);
					articleLikeCountRepository.decrease(articleId);
					// TODO 좋아요 취소 이벤트 발행하기
				}
			);
	}

	public Long count(Long articleId) {
		return articleLikeCountRepository.findById(articleId)
			.map(ArticleLikeCount::getLikeCount)
			.orElse(0L);
	}
}
