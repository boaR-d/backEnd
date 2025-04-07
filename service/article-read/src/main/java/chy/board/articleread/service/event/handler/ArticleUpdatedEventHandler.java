package chy.board.articleread.service.event.handler;

import org.springframework.stereotype.Component;

import chy.board.articleread.repository.ArticleQueryModelRepository;
import chy.board.common.event.Event;
import chy.board.common.event.EventType;
import chy.board.common.event.payload.ArticleUpdatedEventPayload;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleUpdatedEventHandler implements EventHandler<ArticleUpdatedEventPayload> {
	private final ArticleQueryModelRepository articleQueryModelRepository;

	@Override
	public void handle(Event<ArticleUpdatedEventPayload> event) {
		ArticleUpdatedEventPayload payload = event.getPayload();

		articleQueryModelRepository.read(payload.getArticleId())
			.ifPresent(articleQueryModel -> {
				articleQueryModel.updateBy(payload);
				articleQueryModelRepository.update(articleQueryModel);
			});
	}

	@Override
	public boolean supports(Event<ArticleUpdatedEventPayload> event) {
		return EventType.ARTICLE_UPDATED == event.getType();
	}
}
