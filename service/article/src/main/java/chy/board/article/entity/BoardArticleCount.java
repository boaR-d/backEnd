package chy.board.article.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "board_article_count")
@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardArticleCount {
	@Id
	private Long boardId;
	private String boardName;
	private Long articleCount;

	public static BoardArticleCount init(Long boardId, String boardName, Long articleCount) {
		BoardArticleCount boardArticleCount = new BoardArticleCount();
		boardArticleCount.boardId = boardId;
		boardArticleCount.boardName = boardName;
		boardArticleCount.articleCount = articleCount;
		return boardArticleCount;
	}
}
