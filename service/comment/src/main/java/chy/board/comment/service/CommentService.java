package chy.board.comment.service;

import static java.util.function.Predicate.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import chy.board.comment.entity.ArticleCommentCount;
import chy.board.comment.entity.Comment;
import chy.board.comment.repository.ArticleCommentCountRepository;
import chy.board.comment.repository.CommentRepository;
import chy.board.comment.service.request.CommentCreateRequest;
import chy.board.comment.service.response.CommentPageResponse;
import chy.board.comment.service.response.CommentResponse;
import chy.board.common.event.EventType;
import chy.board.common.event.payload.CommentCreatedEventPayload;
import chy.board.common.event.payload.CommentDeletedEventPayload;
import chy.board.common.outboxmessagerelay.OutboxEventPublisher;
import chy.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final Snowflake snowflake = new Snowflake();
	private final CommentRepository commentRepository;
	private final OutboxEventPublisher outboxEventPublisher;
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
		int result = articleCommentCountRepository.increase(request.getArticleId());
		if (result == 0) {
			articleCommentCountRepository.save(
				ArticleCommentCount.init(request.getArticleId(), 1L)
			);
		}

		outboxEventPublisher.publish(
			EventType.COMMENT_CREATED,
			CommentCreatedEventPayload.builder()
				.commentId(comment.getCommentId())
				.content(comment.getContent())
				.parentCommentId(comment.getParentCommentId())
				.articleId(comment.getArticleId())
				.writerId(comment.getWriterId())
				.deleted(comment.getDeleted())
				.createdAt(comment.getCreatedAt())
				.articleCommentCount(count(comment.getArticleId()))
				.build(),
			comment.getArticleId()
		);

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
				outboxEventPublisher.publish(
					EventType.COMMENT_DELETED,
					CommentDeletedEventPayload.builder()
						.commentId(comment.getCommentId())
						.content(comment.getContent())
						.parentCommentId(comment.getParentCommentId())
						.articleId(comment.getArticleId())
						.writerId(comment.getWriterId())
						.deleted(comment.getDeleted())
						.createdAt(comment.getCreatedAt())
						.articleCommentCount(count(comment.getArticleId()))
						.build(),
					comment.getArticleId()
				);
			});
	}

	private void delete(Comment comment) {
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

	private boolean hasChildren(Comment comment) {
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

	public Long count(Long articleId) {
		return articleCommentCountRepository.findById(articleId)
			.map(ArticleCommentCount::getCommentCount)
			.orElse(0L);
	}
}
