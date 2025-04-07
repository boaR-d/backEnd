package chy.board.articleread.service.event.handler;

import org.springframework.stereotype.Component;

import chy.board.articleread.repository.ArticleQueryModelRepository;
import chy.board.common.event.Event;
import chy.board.common.event.EventType;
import chy.board.common.event.payload.ArticleLikedEventPayload;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleLikedEventHandler implements EventHandler<ArticleLikedEventPayload> {
	private final ArticleQueryModelRepository articleQueryModelRepository;

	@Override
	public void handle(Event<ArticleLikedEventPayload> event) {
		ArticleLikedEventPayload payload = event.getPayload();

		articleQueryModelRepository.read(payload.getArticleId())
			.ifPresent(articleQueryModel -> {
					articleQueryModel.updateBy(payload);
					articleQueryModelRepository.update(articleQueryModel);
				}
			);
	}

	@Override
	public boolean supports(Event<ArticleLikedEventPayload> event) {
		return EventType.ARTICLE_LIKED == event.getType();
	}
}
