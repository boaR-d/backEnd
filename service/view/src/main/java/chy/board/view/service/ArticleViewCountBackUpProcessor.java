package chy.board.view.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import chy.board.common.event.EventType;
import chy.board.common.event.payload.ArticleViewedEventPayload;
import chy.board.common.outboxmessagerelay.OutboxEventPublisher;
import chy.board.view.entity.ArticleViewCount;
import chy.board.view.repository.ArticleViewCountBackUpRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleViewCountBackUpProcessor {
	private final OutboxEventPublisher outboxEventPublisher;
	private final ArticleViewCountBackUpRepository articleViewCountBackUpRepository;

	@Transactional
	public void backUp(Long articleId, Long viewCount) {
		int result = articleViewCountBackUpRepository.updateViewCount(articleId, viewCount);
		if (result == 0) {
			articleViewCountBackUpRepository.findById(articleId)
				.ifPresentOrElse(
					ignored -> { },
					() -> articleViewCountBackUpRepository.save(ArticleViewCount.init(articleId, viewCount))
				);
		}

		outboxEventPublisher.publish(
			EventType.ARTICLE_VIEWED,
			ArticleViewedEventPayload.builder()
				.articleId(articleId)
				.articleViewCount(viewCount)
				.build(),
			articleId
		);
	}
}
