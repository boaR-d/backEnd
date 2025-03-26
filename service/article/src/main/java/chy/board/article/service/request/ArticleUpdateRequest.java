package chy.board.article.service.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ArticleUpdateRequest {
	private String title;
	private String content;
	private MultipartFile image;
}
