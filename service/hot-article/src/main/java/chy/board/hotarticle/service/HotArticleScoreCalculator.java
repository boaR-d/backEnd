package chy.board.hotarticle.service;

import org.springframework.stereotype.Component;

import chy.board.hotarticle.repository.ArticleCommentCountRepository;
import chy.board.hotarticle.repository.ArticleLikeCountRepository;
import chy.board.hotarticle.repository.ArticleViewCountRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HotArticleScoreCalculator {
	private final ArticleLikeCountRepository articleLikeCountRepository;
	private final ArticleCommentCountRepository articleCommentCountRepository;
	private final ArticleViewCountRepository articleViewCountRepository;

	private static final long ARTICLE_LIKE_COUNT_WEIGHT = 3;
	private static final long ARTICLE_COMMENT_COUNT_WEIGHT = 2;
	private static final long ARTICLE_VIEW_COUNT_WEIGHT = 1;

	public long calculate(Long articleId) {
		Long articleLikeCount = articleLikeCountRepository.read(articleId);
		Long articleViewCount = articleViewCountRepository.read(articleId);
		Long articleCommentCount = articleCommentCountRepository.read(articleId);

		return articleLikeCount * ARTICLE_LIKE_COUNT_WEIGHT
			+ articleViewCount * ARTICLE_VIEW_COUNT_WEIGHT
			+ articleCommentCount * ARTICLE_COMMENT_COUNT_WEIGHT;
	}
}
