package chy.board.view.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import chy.board.view.entity.ArticleViewCount;
import chy.board.view.repository.ArticleViewCountBackUpRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleViewCountBackUpProcessor {
	private final ArticleViewCountBackUpRepository articleViewCountBackUpRepository;

	@Transactional
	public void backUp(Long articleId, Long viewCount) {
		int result = articleViewCountBackUpRepository.updateViewCount(articleId, viewCount);
		if (result == 0) {
			articleViewCountBackUpRepository.findById(articleId)
				.ifPresentOrElse(
					ignored -> { },
					() -> articleViewCountBackUpRepository.save(ArticleViewCount.init(articleId, viewCount))
				);
		}

		// TODO ViewCount 배치 저장 이벤트 발행 (인기글 계산에 사용)
	}
}
