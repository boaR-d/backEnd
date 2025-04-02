package chy.board.like.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import chy.board.common.event.EventType;
import chy.board.common.event.payload.ArticleLikedEventPayload;
import chy.board.common.event.payload.ArticleUnlikedEventPayload;
import chy.board.common.outboxmessagerelay.OutboxEventPublisher;
import chy.board.common.snowflake.Snowflake;
import chy.board.like.entity.ArticleLike;
import chy.board.like.entity.ArticleLikeCount;
import chy.board.like.repository.ArticleLikeCountRepository;
import chy.board.like.repository.ArticleLikeRepository;
import chy.board.like.service.response.ArticleLikeResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
	private final Snowflake snowflake = new Snowflake();
	private final OutboxEventPublisher outboxEventPublisher;
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

		outboxEventPublisher.publish(
			EventType.ARTICLE_LIKED,
			ArticleLikedEventPayload.builder()
				.articleLikeId(articleLike.getArticleLikeId())
				.articleId(articleLike.getArticleId())
				.userId(articleLike.getUserId())
				.createdAt(articleLike.getCreatedAt())
				.articleLikeCount(count(articleLike.getArticleId()))
				.build(),
			articleLike.getArticleId()
		);
	}

	@Transactional
	public void unlike(Long articleId, Long userId) {
		articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
			.ifPresent(articleLike -> {
					articleLikeRepository.delete(articleLike);
					articleLikeCountRepository.decrease(articleId);
					outboxEventPublisher.publish(
						EventType.ARTICLE_UNLIKED,
						ArticleUnlikedEventPayload.builder()
							.articleLikeId(articleLike.getArticleLikeId())
							.articleId(articleLike.getArticleId())
							.userId(articleLike.getUserId())
							.createdAt(articleLike.getCreatedAt())
							.articleLikeCount(count(articleLike.getArticleId()))
							.build(),
						articleLike.getArticleId()
					);
				}
			);
	}

	public Long count(Long articleId) {
		return articleLikeCountRepository.findById(articleId)
			.map(ArticleLikeCount::getLikeCount)
			.orElse(0L);
	}
}
