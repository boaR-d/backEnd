package chy.board.articleread.service.event.handler;

import org.springframework.stereotype.Component;

import chy.board.articleread.repository.ArticleQueryModelRepository;
import chy.board.common.event.Event;
import chy.board.common.event.EventType;
import chy.board.common.event.payload.CommentDeletedEventPayload;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommentDeletedEventHandler implements EventHandler<CommentDeletedEventPayload> {
	private final ArticleQueryModelRepository articleQueryModelRepository;

	@Override
	public void handle(Event<CommentDeletedEventPayload> event) {
		CommentDeletedEventPayload payload = event.getPayload();

		articleQueryModelRepository.read(payload.getArticleId())
			.ifPresent(articleQueryModel -> {
				articleQueryModel.updateBy(payload);
				articleQueryModelRepository.update(articleQueryModel);
			});
	}

	@Override
	public boolean supports(Event<CommentDeletedEventPayload> event) {
		return EventType.COMMENT_DELETED == event.getType();
	}
}
