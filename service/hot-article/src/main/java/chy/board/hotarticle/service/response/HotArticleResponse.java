package chy.board.hotarticle.service.response;

import java.time.LocalDateTime;

import chy.board.hotarticle.client.ArticleClient;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class HotArticleResponse {
	private Long articleId;
	private String title;
	private LocalDateTime createdAt;

	public static HotArticleResponse from(ArticleClient.ArticleResponse articleResponse) {
		HotArticleResponse response = new HotArticleResponse();
		response.articleId = articleResponse.getArticleId();
		response.title = articleResponse.getTitle();
		response.createdAt = articleResponse.getCreatedAt();
		return response;
	}
}
