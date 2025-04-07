package chy.board.articleread.service.event.handler;

import chy.board.common.event.Event;
import chy.board.common.event.EventPayload;

public interface EventHandler<T extends EventPayload> {
	void handle(Event<T> event);

	boolean supports(Event<T> event);
}
