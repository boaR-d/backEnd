package chy.board.common.outboxmessagerelay;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import chy.board.common.event.Event;
import chy.board.common.event.EventPayload;
import chy.board.common.event.EventType;
import chy.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {
	private final Snowflake outboxIdSnowflake = new Snowflake();
	private final Snowflake eventIdSnowflake = new Snowflake();
	private final ApplicationEventPublisher applicationEventPublisher;

	public void publish(EventType type, EventPayload payload, Long shardKey) {
		Outbox outbox = Outbox.create(
			outboxIdSnowflake.nextId(),
			type,
			Event.of(
				eventIdSnowflake.nextId(), type, payload
			).toJson(),
			shardKey % MessageRelayConstants.SHARD_COUNT
		);
		applicationEventPublisher.publishEvent(OutboxEvent.of(outbox));
	}
}
