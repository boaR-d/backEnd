package chy.board.hotarticle.service.eventHandler;

import org.springframework.stereotype.Component;

import chy.board.common.event.Event;
import chy.board.common.event.EventType;
import chy.board.common.event.payload.ArticleLikedEventPayload;
import chy.board.hotarticle.repository.ArticleLikeCountRepository;
import chy.board.hotarticle.utils.TimeCalculatorUtils;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleLikedEventHandler implements EventHandler<ArticleLikedEventPayload> {
	private final ArticleLikeCountRepository articleLikeCountRepository;

	@Override
	public void handle(Event<ArticleLikedEventPayload> event) {
		ArticleLikedEventPayload payload = event.getPayload();
		articleLikeCountRepository.createOrUpdate(
			payload.getArticleId(),
			payload.getArticleLikeCount(),
			TimeCalculatorUtils.calculateDurationToMidnight()
		);
	}

	@Override
	public boolean supports(Event<ArticleLikedEventPayload> event) {
		return EventType.ARTICLE_LIKED == event.getType();
	}

	@Override
	public Long findArticleId(Event<ArticleLikedEventPayload> event) {
		return event.getPayload().getArticleId();
	}
}
