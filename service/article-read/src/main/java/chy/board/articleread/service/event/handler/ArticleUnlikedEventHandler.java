package chy.board.articleread.service.event.handler;

import org.springframework.stereotype.Component;

import chy.board.articleread.repository.ArticleQueryModelRepository;
import chy.board.common.event.Event;
import chy.board.common.event.EventType;
import chy.board.common.event.payload.ArticleUnlikedEventPayload;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleUnlikedEventHandler implements EventHandler<ArticleUnlikedEventPayload> {
	private final ArticleQueryModelRepository articleQueryModelRepository;

	@Override
	public void handle(Event<ArticleUnlikedEventPayload> event) {
		ArticleUnlikedEventPayload payload = event.getPayload();

		articleQueryModelRepository.read(payload.getArticleId())
			.ifPresent(articleQueryModel -> {
				articleQueryModel.updateBy(payload);
				articleQueryModelRepository.update(articleQueryModel);
			});
	}

	@Override
	public boolean supports(Event<ArticleUnlikedEventPayload> event) {
		return EventType.ARTICLE_UNLIKED == event.getType();
	}
}
