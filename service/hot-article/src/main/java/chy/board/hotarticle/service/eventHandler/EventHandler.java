package chy.board.hotarticle.service.eventHandler;

import chy.board.common.event.Event;
import chy.board.common.event.EventPayload;

public interface EventHandler<T extends EventPayload> {
	void handle(Event<T> event);

	boolean supports(Event<T> event);

	Long findArticleId(Event<T> event);
}
