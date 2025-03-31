package chy.board.common.outboxmessagerelay;

import java.util.List;
import java.util.stream.LongStream;

import lombok.Getter;

@Getter
public class AssignedShard {
	private List<Long> shards;

	// 이거 생성 of
	public static AssignedShard of(String appId, List<String> appIds, long shardCount) {
		AssignedShard assignedShard = new AssignedShard();
		assignedShard.shards = assign(appId, appIds, shardCount);
		return assignedShard;
	}

	// assign
	private static List<Long> assign(String appId, List<String> appIds, long shardCount) {
		int appIndex = findAppIndex(appId, appIds);
		if (appIndex == -1) {
			return List.of();
		}

		long start = appIndex * shardCount / appIds.size();
		long end = (appIndex + 1) * shardCount / appIds.size() - 1;

		return LongStream.rangeClosed(start, end).boxed().toList();
	}

	// find 서비스 인덱스
	private static int findAppIndex(String appId, List<String> appIds) {
		for (int i = 0; i < appIds.size(); i++) {
			if (appIds.get(i).equals(appId)) {
				return i;
			}
		}
		return -1;
	}
}
