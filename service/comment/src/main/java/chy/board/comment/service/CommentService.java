package chy.board.comment.service;

import static java.util.function.Predicate.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import chy.board.comment.entity.Comment;
import chy.board.comment.repository.ArticleCommentCountRepository;
import chy.board.comment.repository.CommentRepository;
import chy.board.comment.service.request.CommentCreateRequest;
import chy.board.comment.service.response.CommentPageResponse;
import chy.board.comment.service.response.CommentResponse;
import chy.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final Snowflake snowflake = new Snowflake();
	private final CommentRepository commentRepository;
	private final ArticleCommentCountRepository articleCommentCountRepository;

	@Transactional
	public CommentResponse create(CommentCreateRequest request) {
		Comment parent = findParent(request);
		Comment comment = commentRepository.save(
			Comment.create(
				snowflake.nextId(),
				request.getContent(),
				parent == null ? null : parent.getCommentId(),
				request.getArticleId(),
				request.getWriterId()
			)
		);
		articleCommentCountRepository.increase(request.getArticleId());
		return CommentResponse.from(comment);
	}

	public CommentResponse read(Long commentId) {
		return CommentResponse.from(commentRepository.findById(commentId).orElseThrow());
	}

	@Transactional
	public void delete(Long commentId) {
		commentRepository.findById(commentId)
			.filter(not(Comment::getDeleted))
			.ifPresent(comment -> {
				if (hasChildren(comment)) {
					comment.delete();
				} else {
					delete(comment);
				}
			});
	}

	public void delete(Comment comment) {
		commentRepository.delete(comment);
		articleCommentCountRepository.decrease(comment.getArticleId());
		if (!comment.isRoot()) {
			commentRepository.findById(comment.getParentCommentId())
				.filter(Comment::getDeleted)
				.filter(not(this::hasChildren))
				.ifPresent(this::delete);
		}
	}

	public CommentPageResponse readAll(Long articleId, Long page, Long pageSize) {
		return CommentPageResponse.of(
			commentRepository.findAll(articleId, (page - 1) * pageSize, pageSize).stream()
				.map(CommentResponse::from)
				.toList(),
			commentRepository.count(articleId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L))
		);
	}

	public List<CommentResponse> readAll(Long articleId, Long lastParentCommentId, Long lastCommentId, Long limit) {
		List<Comment> comments = lastParentCommentId == null || lastCommentId == null ?
			commentRepository.findAllInfiniteScroll(articleId, limit) :
			commentRepository.findAllInfiniteScroll(articleId, lastParentCommentId, lastCommentId, limit);
		return comments.stream()
			.map(CommentResponse::from)
			.toList();
	}

	public boolean hasChildren(Comment comment) {
		return commentRepository.countBy(comment.getArticleId(), comment.getCommentId(), 2L) == 2L;
	}

	private Comment findParent(CommentCreateRequest request) {
		Long parentCommentId = request.getParentCommentId();
		if (parentCommentId == null) {
			return null;
		}
		return commentRepository.findById(parentCommentId)
			.filter(not(Comment::getDeleted))
			.filter(Comment::isRoot)
			.orElseThrow(); // TODO 예외 핸들 처리하기
	}

}
