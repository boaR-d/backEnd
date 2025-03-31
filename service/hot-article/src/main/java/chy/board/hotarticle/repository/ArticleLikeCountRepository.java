package chy.board.hotarticle.repository;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArticleLikeCountRepository {
	private final StringRedisTemplate redisTemplate;

	private static final String KEY_FORMAT = "hot-article::article::%s::like-count";

	public void createOrUpdate(Long articleId, Long likeCount, Duration ttl) {
		redisTemplate.opsForValue().set(generateKey(articleId), String.valueOf(likeCount), ttl);
	}

	public Long read(Long articleId) {
		String likeCount = redisTemplate.opsForValue().get(generateKey(articleId));
		return likeCount == null ? 0L : Long.valueOf(likeCount);
	}

	private String generateKey(Long articleId) {
		return KEY_FORMAT.formatted(articleId);
	}
}
