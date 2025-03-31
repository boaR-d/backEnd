package chy.board.hotarticle.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import chy.board.common.event.Event;
import chy.board.common.event.EventPayload;
import chy.board.hotarticle.repository.ArticleCreatedTimeRepository;
import chy.board.hotarticle.repository.HotArticleListRepository;
import chy.board.hotarticle.service.eventHandler.EventHandler;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HotArticleScoreUpdater {
	private final HotArticleListRepository hotArticleListRepository;
	private final HotArticleScoreCalculator hotArticleScoreCalculator;
	private final ArticleCreatedTimeRepository articleCreatedTimeRepository;

	private static final long HOT_ARTICLE_COUNT = 10;
	private static final Duration HOT_ARTICLE_TTL = Duration.ofDays(10);

	public void update(Event<EventPayload> event, EventHandler eventHandler) {
		Long articleId = eventHandler.findArticleId(event);
		LocalDateTime createdAt = articleCreatedTimeRepository.read(articleId);

		if (!isCreatedToday(createdAt)) {
			return;
		}

		eventHandler.handle(event);

		long score = hotArticleScoreCalculator.calculate(articleId);
		hotArticleListRepository.add(
			articleId,
			createdAt,
			score,
			HOT_ARTICLE_COUNT,
			HOT_ARTICLE_TTL
		);
	}

	public boolean isCreatedToday(LocalDateTime createdAt) {
		return createdAt != null && createdAt.toLocalDate().equals(LocalDate.now());
	}
}
