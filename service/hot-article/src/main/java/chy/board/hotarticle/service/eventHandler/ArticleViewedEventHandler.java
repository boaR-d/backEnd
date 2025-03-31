package chy.board.hotarticle.service.eventHandler;

import org.springframework.stereotype.Component;

import chy.board.common.event.Event;
import chy.board.common.event.EventType;
import chy.board.common.event.payload.ArticleViewedEventPayload;
import chy.board.hotarticle.repository.ArticleViewCountRepository;
import chy.board.hotarticle.utils.TimeCalculatorUtils;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleViewedEventHandler implements EventHandler<ArticleViewedEventPayload> {
	private final ArticleViewCountRepository articleViewCountRepository;

	@Override
	public void handle(Event<ArticleViewedEventPayload> event) {
		ArticleViewedEventPayload payload = event.getPayload();
		articleViewCountRepository.createOrUpdate(
			payload.getArticleId(),
			payload.getArticleViewCount(),
			TimeCalculatorUtils.calculateDurationToMidnight()
		);
	}

	@Override
	public boolean supports(Event<ArticleViewedEventPayload> event) {
		return EventType.ARTICLE_VIEWED == event.getType();
	}

	@Override
	public Long findArticleId(Event<ArticleViewedEventPayload> event) {
		return event.getPayload().getArticleId();
	}
}
