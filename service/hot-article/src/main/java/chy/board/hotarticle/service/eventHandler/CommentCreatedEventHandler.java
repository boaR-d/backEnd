package chy.board.hotarticle.service.eventHandler;

import org.springframework.stereotype.Component;

import chy.board.common.event.Event;
import chy.board.common.event.EventType;
import chy.board.common.event.payload.CommentCreatedEventPayload;
import chy.board.hotarticle.repository.ArticleCommentCountRepository;
import chy.board.hotarticle.utils.TimeCalculatorUtils;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommentCreatedEventHandler implements EventHandler<CommentCreatedEventPayload> {
	private final ArticleCommentCountRepository articleCommentCountRepository;

	@Override
	public void handle(Event<CommentCreatedEventPayload> event) {
		CommentCreatedEventPayload payload = event.getPayload();
		articleCommentCountRepository.createOrUpdate(
			payload.getArticleId(),
			payload.getArticleCommentCount(),
			TimeCalculatorUtils.calculateDurationToMidnight()
		);
	}

	@Override
	public boolean supports(Event<CommentCreatedEventPayload> event) {
		return EventType.COMMENT_CREATED == event.getType();
	}

	@Override
	public Long findArticleId(Event<CommentCreatedEventPayload> event) {
		return event.getPayload().getArticleId();
	}
}
