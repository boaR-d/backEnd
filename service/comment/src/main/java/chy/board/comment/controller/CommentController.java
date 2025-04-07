package chy.board.comment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import chy.board.comment.service.CommentService;
import chy.board.comment.service.request.CommentCreateRequest;
import chy.board.comment.service.response.CommentPageResponse;
import chy.board.comment.service.response.CommentResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentController {
	private final CommentService commentService;

	@PostMapping("/v1/comments")
	public CommentResponse create(@RequestBody CommentCreateRequest request) {
		return commentService.create(request);
	}

	@GetMapping("/v1/comments/{commentId}")
	public CommentResponse read(@PathVariable Long commentId) {
		return commentService.read(commentId);
	}

	@DeleteMapping("/v1/comments/{commentId}")
	public void delete(@PathVariable Long commentId) {
		commentService.delete(commentId);
	}

	@GetMapping("/v1/comments")
	public CommentPageResponse readAll(
		@RequestParam Long articleId,
		@RequestParam Long page,
		@RequestParam Long pageSize
	) {
		return commentService.readAll(articleId, page, pageSize);
	}

	@GetMapping("/v1/comments/infinite-scroll")
	public List<CommentResponse> readAll(
		@RequestParam Long articleId,
		@RequestParam(required = false) Long lastParentCommentId,
		@RequestParam(required = false) Long lastCommentId,
		@RequestParam Long pageSize
	) {
		return commentService.readAll(articleId, lastParentCommentId, lastCommentId, pageSize);
	}

	@GetMapping("/v1/comments/articles/{articleId}/count")
	public Long count(@PathVariable Long articleId) {
		return commentService.count(articleId);
	}
}
