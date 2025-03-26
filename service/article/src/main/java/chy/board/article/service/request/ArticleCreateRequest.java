package chy.board.article.service.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ArticleCreateRequest {
	private String title;
	private String content;
	private Long boardId;
	private Long writerId;
	private MultipartFile image;
}
