package chy.board.common.outboxmessagerelay;

import java.time.LocalDateTime;

import chy.board.common.event.EventType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "outbox")
@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbox {
	@Id
	private Long outboxId;
	@Enumerated(EnumType.STRING)
	private EventType eventType;
	private String payload;
	private Long shardKey;
	private LocalDateTime createdAt;

	public static Outbox create(Long outboxId, EventType eventType, String payload, Long shardKey) {
		Outbox outbox = new Outbox();
		outbox.outboxId = outboxId;
		outbox.eventType = eventType;
		outbox.payload = payload;
		outbox.shardKey = shardKey;
		outbox.createdAt = LocalDateTime.now();
		return outbox;
	}
}
