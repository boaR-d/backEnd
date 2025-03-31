package chy.board.hotarticle.service.eventHandler;

import org.springframework.stereotype.Component;

import chy.board.common.event.Event;
import chy.board.common.event.EventType;
import chy.board.common.event.payload.ArticleUnlikedEventPayload;
import chy.board.hotarticle.repository.ArticleLikeCountRepository;
import chy.board.hotarticle.utils.TimeCalculatorUtils;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleUnlikedEventHandler implements EventHandler<ArticleUnlikedEventPayload> {
	private final ArticleLikeCountRepository articleLikeCountRepository;

	@Override
	public void handle(Event<ArticleUnlikedEventPayload> event) {
		ArticleUnlikedEventPayload payload = event.getPayload();
		articleLikeCountRepository.createOrUpdate(
			payload.getArticleId(),
			payload.getArticleLikeCount(),
			TimeCalculatorUtils.calculateDurationToMidnight()
		);
	}

	@Override
	public boolean supports(Event<ArticleUnlikedEventPayload> event) {
		return EventType.ARTICLE_UNLIKED == event.getType();
	}

	@Override
	public Long findArticleId(Event<ArticleUnlikedEventPayload> event) {
		return event.getPayload().getArticleId();
	}
}
