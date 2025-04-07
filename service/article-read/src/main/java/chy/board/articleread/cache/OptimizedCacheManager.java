package chy.board.articleread.cache;

import static java.util.stream.Collectors.*;

import java.util.Arrays;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import chy.board.common.dataserializer.DataSerializer;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OptimizedCacheManager {
	private final StringRedisTemplate redisTemplate;
	private final OptimizedCacheLockProvider optimizedCacheLockProvider;

	private static final String DELIMITER = "::";

	public Object proceed(String type, long ttlSeconds, Object[] args, Class<?> returnType, OptimizedCacheOriginDataSupplier<?> originDataSupplier) throws Throwable {
		String key = generateKey(type, args);

		String cachedData = redisTemplate.opsForValue().get(key);

		if (cachedData == null) {
			return refresh(key, ttlSeconds, originDataSupplier);
		}

		OptimizedCache optimizedCache = DataSerializer.deserialize(cachedData, OptimizedCache.class);

		if (optimizedCache == null) {
			return refresh(key, ttlSeconds, originDataSupplier);
		}

		if (!optimizedCache.isExpired()) {
			return optimizedCache.parseData(returnType);
		}

		if (!optimizedCacheLockProvider.lock(key)) {
			return optimizedCache.parseData(returnType);
		}

		try {
			return refresh(key, ttlSeconds, originDataSupplier);
		} finally {
			optimizedCacheLockProvider.unlock(key);
		}
	}

	private Object refresh(String key, long ttlSeconds, OptimizedCacheOriginDataSupplier<?> originDataSupplier) throws Throwable {
		Object object = originDataSupplier.get();
		OptimizedCacheTTL optimizedCacheTTL = OptimizedCacheTTL.of(ttlSeconds);
		OptimizedCache optimizedCache = OptimizedCache.of(object, optimizedCacheTTL.getLogicalTTL());
		redisTemplate.opsForValue()
			.set(
				key,
				DataSerializer.serialize(optimizedCache),
				optimizedCacheTTL.getPhysicalTTL()
			);

		return object;
	}

	private String generateKey(String prefix, Object[] args) {
		return prefix + DELIMITER +
			Arrays.stream(args)
				.map(String::valueOf)
				.collect(joining(DELIMITER));
	}
}
