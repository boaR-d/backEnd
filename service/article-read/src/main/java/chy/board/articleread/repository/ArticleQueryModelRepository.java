package chy.board.articleread.repository;

import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import chy.board.common.dataserializer.DataSerializer;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArticleQueryModelRepository {
	private final StringRedisTemplate redisTemplate;

	private static final String KET_FORMAT = "article-read::article::%s";

	public void create(ArticleQueryModel articleQueryModel, Duration ttl) {
		redisTemplate.opsForValue()
			.set(generateKey(articleQueryModel), DataSerializer.serialize(articleQueryModel), ttl);
	}

	public Optional<ArticleQueryModel> read(Long articleId) {
		return Optional.ofNullable(
			redisTemplate.opsForValue().get(generateKey(articleId))
		).map(json -> DataSerializer.deserialize(json, ArticleQueryModel.class));
	}

	public void update(ArticleQueryModel articleQueryModel) {
		redisTemplate.opsForValue()
			.setIfPresent(generateKey(articleQueryModel), DataSerializer.serialize(articleQueryModel));
	}

	public void delete(Long articleId) {
		redisTemplate.delete(generateKey(articleId));
	}

	private String generateKey(ArticleQueryModel articleQueryModel) {
		return generateKey(articleQueryModel.getArticleId());
	}

	private String generateKey(Long articleId) {
		return KET_FORMAT.formatted(articleId);
	}

	public Map<Long, ArticleQueryModel> readAll(List<Long> articleIds) {
		List<String> keyList = articleIds.stream().map(this::generateKey).toList();
		return redisTemplate.opsForValue().multiGet(keyList).stream()
			.filter(Objects::nonNull)
			.map(json -> DataSerializer.deserialize(json, ArticleQueryModel.class))
			.collect(toMap(ArticleQueryModel::getArticleId, identity()));
	}
}
