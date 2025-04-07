package chy.board.articleread.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import chy.board.articleread.service.ArticleReadService;
import chy.board.common.event.Event;
import chy.board.common.event.EventPayload;
import chy.board.common.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleReadEventConsumer {
	private final ArticleReadService articleReadService;

	@KafkaListener(topics = {
		EventType.Topic.CHY_BOARD_ARTICLE,
		EventType.Topic.CHY_BOARD_COMMENT,
		EventType.Topic.CHY_BOARD_LIKE
	})
	public void listen(String message, Acknowledgment ack) {
		log.info("[ArticleReadEventConsumer.listen] message={}", message);
		Event<EventPayload> event = Event.fromJson(message);
		if (event != null) {
			articleReadService.handleEvent(event);
		}
		ack.acknowledge();
	}
}
