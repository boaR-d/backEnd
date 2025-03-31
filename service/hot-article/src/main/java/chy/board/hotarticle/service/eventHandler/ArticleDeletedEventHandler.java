package chy.board.hotarticle.service.eventHandler;

import org.springframework.stereotype.Component;

import chy.board.common.event.Event;
import chy.board.common.event.EventType;
import chy.board.common.event.payload.ArticleDeletedEventPayload;
import chy.board.hotarticle.repository.ArticleCreatedTimeRepository;
import chy.board.hotarticle.repository.HotArticleListRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleDeletedEventHandler implements EventHandler<ArticleDeletedEventPayload> {
	private final HotArticleListRepository hotArticleListRepository;
	private final ArticleCreatedTimeRepository articleCreatedTimeRepository;

	@Override
	public void handle(Event<ArticleDeletedEventPayload> event) {
		ArticleDeletedEventPayload payload = event.getPayload();
		articleCreatedTimeRepository.delete(payload.getArticleId());
		hotArticleListRepository.remove(payload.getArticleId(), payload.getCreatedAt());
	}

	@Override
	public boolean supports(Event<ArticleDeletedEventPayload> event) {
		return EventType.ARTICLE_DELETED == event.getType();
	}

	@Override
	public Long findArticleId(Event<ArticleDeletedEventPayload> event) {
		return event.getPayload().getArticleId();
	}
}
