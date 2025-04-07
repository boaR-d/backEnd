package chy.board.articleread.service.event.handler;

import org.springframework.stereotype.Component;

import chy.board.articleread.repository.ArticleIdListRepository;
import chy.board.articleread.repository.ArticleQueryModelRepository;
import chy.board.articleread.repository.BoardArticleCountRepository;
import chy.board.common.event.Event;
import chy.board.common.event.EventType;
import chy.board.common.event.payload.ArticleDeletedEventPayload;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleDeletedEventHandler implements EventHandler<ArticleDeletedEventPayload> {
	private final ArticleIdListRepository articleIdListRepository;
	private final ArticleQueryModelRepository articleQueryModelRepository;
	private final BoardArticleCountRepository boardArticleCountRepository;

	@Override
	public void handle(Event<ArticleDeletedEventPayload> event) {
		ArticleDeletedEventPayload payload = event.getPayload();

		articleIdListRepository.delete(payload.getBoardId(), payload.getArticleId());
		articleQueryModelRepository.delete(payload.getArticleId());
		boardArticleCountRepository.createOrUpdate(payload.getBoardId(), payload.getBoardArticleCount());
	}

	@Override
	public boolean supports(Event<ArticleDeletedEventPayload> event) {
		return EventType.ARTICLE_DELETED == event.getType();
	}
}
