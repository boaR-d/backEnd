package chy.board.hotarticle.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import chy.board.common.event.Event;
import chy.board.common.event.EventPayload;
import chy.board.common.event.EventType;
import chy.board.hotarticle.service.HotArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotArticleEventConsumer {
	private final HotArticleService hotArticleService;

	@KafkaListener(topics = {
		EventType.Topic.CHY_BOARD_ARTICLE,
		EventType.Topic.CHY_BOARD_COMMENT,
		EventType.Topic.CHY_BOARD_LIKE,
		EventType.Topic.CHY_BOARD_VIEW,
	})
	public void listen(String message, Acknowledgment ack) {
		log.info("[HotArticleEventConsumer.listen] received message={}", message);
		Event<EventPayload> event = Event.fromJson(message);
		if (event != null) {
			hotArticleService.handleEvent(event);
		}
		ack.acknowledge();
	}
}
