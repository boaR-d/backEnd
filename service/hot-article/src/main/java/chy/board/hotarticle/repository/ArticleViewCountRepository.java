package chy.board.hotarticle.repository;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArticleViewCountRepository {
	private final StringRedisTemplate redisTemplate;

	private static final String KEY_FORMAT = "hot-article::article::%s::view-count";

	public void createOrUpdate(Long articleId, Long viewCount, Duration ttl) {
		redisTemplate.opsForValue().set(generateKey(articleId), String.valueOf(viewCount), ttl);
	}

	public Long read(Long articleId) {
		String viewCount = redisTemplate.opsForValue().get(generateKey(articleId));
		return viewCount == null ? 0L : Long.valueOf(viewCount);
	}

	private String generateKey(Long articleId) {
		return KEY_FORMAT.formatted(articleId);
	}
}
