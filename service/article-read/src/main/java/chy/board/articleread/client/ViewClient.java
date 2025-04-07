package chy.board.articleread.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewClient {
	private RestClient restClient;

	@Value("${endpoints.chy-board-view-service.url}")
	private String viewServiceUrl;

	@PostConstruct
	public void initRestClient() {
		restClient = RestClient.create(viewServiceUrl);
	}

	@Cacheable(key = "#articleId", value = "articleViewCount")
	public long count(Long articleId) {
		try {
			return restClient.get()
				.uri("/v1/article-views/articles/{articleId}/count", articleId)
				.retrieve()
				.body(Long.class);
		} catch (Exception e) {
			log.error("[ViewClient.count] articleId={}", articleId, e);
			return 0;
		}
	}
}
