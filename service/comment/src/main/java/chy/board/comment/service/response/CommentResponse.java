package chy.board.comment.service.response;

import java.time.LocalDateTime;

import chy.board.comment.entity.Comment;
import lombok.Getter;

@Getter
public class CommentResponse {
	private Long commentId;
	private String content;
	private Long articleId;
	private Long parentCommentId;
	private Long writerId;
	private Boolean deleted;
	private LocalDateTime createdAt;

	public static CommentResponse from(Comment comment) {
		CommentResponse response = new CommentResponse();
		response.commentId = comment.getCommentId();
		response.content = comment.getContent();
		response.articleId = comment.getArticleId();
		response.parentCommentId = comment.getParentCommentId();
		response.writerId = comment.getWriterId();
		response.deleted = comment.getDeleted();
		response.createdAt = comment.getCreatedAt();
		return response;
	}
}
