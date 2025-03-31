package chy.board.hotarticle.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import chy.board.common.event.Event;
import chy.board.common.event.EventPayload;
import chy.board.common.event.EventType;
import chy.board.hotarticle.client.ArticleClient;
import chy.board.hotarticle.repository.HotArticleListRepository;
import chy.board.hotarticle.service.eventHandler.EventHandler;
import chy.board.hotarticle.service.response.HotArticleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotArticleService {
	private final ArticleClient articleClient;
	private final List<EventHandler> eventHandlers;
	private final HotArticleScoreUpdater hotArticleScoreUpdater;
	private final HotArticleListRepository hotArticleListRepository;

	public void handleEvent(Event<EventPayload> event) {
		EventHandler eventHandler = findEventHandler(event);
		if (eventHandlers == null) {
			return;
		}
		if (isArticleCreatedOrDeleted(event)) {
			eventHandler.handle(event);
		}
		hotArticleScoreUpdater.update(event, eventHandler);
	}

	public List<HotArticleResponse> readAll(String dateStr) {
		return hotArticleListRepository.readAll(dateStr).stream()
			.map(articleClient::read)
			.filter(Objects::nonNull)
			.map(HotArticleResponse::from)
			.toList();
	}

	private EventHandler<EventPayload> findEventHandler(Event<EventPayload> event) {
		return eventHandlers.stream()
			.filter(eventHandler -> eventHandler.supports(event))
			.findAny()
			.orElse(null);
	}

	private boolean isArticleCreatedOrDeleted(Event<EventPayload> event) {
		return EventType.ARTICLE_CREATED == event.getType() || EventType.ARTICLE_DELETED == event.getType();
	}
}
