package chy.board.common.event;

import chy.board.common.event.payload.ArticleCreatedEventPayload;
import chy.board.common.event.payload.ArticleDeletedEventPayload;
import chy.board.common.event.payload.ArticleLikedEventPayload;
import chy.board.common.event.payload.ArticleUnlikedEventPayload;
import chy.board.common.event.payload.ArticleUpdatedEventPayload;
import chy.board.common.event.payload.ArticleViewedEventPayload;
import chy.board.common.event.payload.CommentCreatedEventPayload;
import chy.board.common.event.payload.CommentDeletedEventPayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum EventType {
	ARTICLE_CREATED(ArticleCreatedEventPayload.class, Topic.CHY_BOARD_ARTICLE),
	ARTICLE_UPDATED(ArticleUpdatedEventPayload.class, Topic.CHY_BOARD_ARTICLE),
	ARTICLE_DELETED(ArticleDeletedEventPayload.class, Topic.CHY_BOARD_ARTICLE),
	COMMENT_CREATED(CommentCreatedEventPayload.class, Topic.CHY_BOARD_COMMENT),
	COMMENT_DELETED(CommentDeletedEventPayload.class, Topic.CHY_BOARD_COMMENT),
	ARTICLE_LIKED(ArticleLikedEventPayload.class, Topic.CHY_BOARD_LIKE),
	ARTICLE_UNLIKED(ArticleUnlikedEventPayload.class, Topic.CHY_BOARD_LIKE),
	ARTICLE_VIEWED(ArticleViewedEventPayload.class, Topic.CHY_BOARD_VIEW);

	private final Class<? extends EventPayload> payloadClass;
	private final String topic;

	public static EventType from(String type) {
		try {
			return valueOf(type);
		} catch (Exception e) {
			log.error("[EventType.from] type={}", type, e);
			return null;
		}
	}

	public static class Topic {
		public static final String CHY_BOARD_ARTICLE = "chy-board-article";
		public static final String CHY_BOARD_COMMENT = "chy-board-comment";
		public static final String CHY_BOARD_LIKE = "chy-board-like";
		public static final String CHY_BOARD_VIEW = "chy-board-view";
	}
}
