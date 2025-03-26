package chy.board.article.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "article")
@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
	@Id
	private Long articleId;
	private String title;
	private String content;
	private Long boardId;
	private Long writerId;
	private String imageUrl;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;

	public static Article create(Long articleId, String title, String content, Long boardId, Long writerId, String imageUrl) {
		Article article = new Article();
		article.articleId = articleId;
		article.title = title;
		article.content = content;
		article.boardId = boardId;
		article.writerId = writerId;
		article.imageUrl = imageUrl;
		article.createdAt = LocalDateTime.now();
		article.modifiedAt = article.createdAt;
		return article;
	}

	public void update(String title, String content, String imageUrl) {
		this.title = title;
		this.content = content;
		this.imageUrl = imageUrl;
		modifiedAt = LocalDateTime.now();
	}
}
