package chy.board.hotarticle.repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HotArticleListRepository {
	private final StringRedisTemplate redisTemplate;
	private static final String KEY_FORMAT = "hot-article::list::%s";
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

	public void add(Long articleId, LocalDateTime time, Long score, Long limit, Duration ttl) {
		redisTemplate.executePipelined((RedisCallback<?>)action -> {
			StringRedisConnection conn = (StringRedisConnection)action;
			String key = generateKey(time);
			conn.zAdd(key, score, String.valueOf(articleId));
			conn.zRemRange(key, 0, -limit - 1);
			conn.expire(key, ttl.toSeconds());
			return null;
		});
	}

	public void remove(Long articleId, LocalDateTime time) {
		redisTemplate.opsForZSet().remove(generateKey(time), String.valueOf(articleId));
	}

	public String generateKey(LocalDateTime time) {
		return generateKey(TIME_FORMATTER.format(time));
	}

	public String generateKey(String dateStr) {
		return KEY_FORMAT.formatted(dateStr);
	}

	public List<Long> readAll(String dateStr) {
		return redisTemplate.opsForZSet()
			.reverseRangeWithScores(generateKey(dateStr), 0, -1).stream()
			.peek(tuple ->
				log.info("[HotArticleListRepository.readAll] articleId={}, score={}", tuple.getValue(),
					tuple.getScore()))
			.map(ZSetOperations.TypedTuple::getValue)
			.map(Long::valueOf)
			.toList();
	}
}
