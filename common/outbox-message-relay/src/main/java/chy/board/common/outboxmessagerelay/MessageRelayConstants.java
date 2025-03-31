package chy.board.common.outboxmessagerelay;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageRelayConstants {
	public static final int SHARD_COUNT = 4; // DB의 샤드 4개 있다고 가정.
}
