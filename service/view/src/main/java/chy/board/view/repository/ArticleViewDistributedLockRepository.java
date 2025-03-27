package chy.board.view.repository;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArticleViewDistributedLockRepository {
	private final StringRedisTemplate redisTemplate;
	private static final String KEY_FORMAT = "view::article::%s::user::%s::lock";

	public boolean lock(Long articleId, Long userId, Duration ttl) {
		String key = generateKey(articleId, userId);
		return redisTemplate.opsForValue().setIfAbsent(key, "", ttl);
	}

	public String generateKey(Long articleId, Long userId) {
		return KEY_FORMAT.formatted(articleId, userId);
	}
}
