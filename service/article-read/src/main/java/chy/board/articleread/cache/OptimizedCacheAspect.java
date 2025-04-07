package chy.board.articleread.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class OptimizedCacheAspect {
	private final OptimizedCacheManager optimizedCacheManager;

	@Around("@annotation(OptimizedCacheable)")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		OptimizedCacheable cacheable = findAnnotation(joinPoint);
		return optimizedCacheManager.proceed(
			cacheable.type(),
			cacheable.ttlSeconds(),
			joinPoint.getArgs(),
			findReturnType(joinPoint),
			() -> joinPoint.proceed()
		);
	}

	private OptimizedCacheable findAnnotation(ProceedingJoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature)signature;
		return methodSignature.getMethod().getAnnotation(OptimizedCacheable.class);
	}

	private Class<?> findReturnType(ProceedingJoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature)signature;
		return methodSignature.getReturnType();
	}
}
