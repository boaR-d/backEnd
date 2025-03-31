package chy.board.hotarticle.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import chy.board.hotarticle.service.HotArticleService;
import chy.board.hotarticle.service.response.HotArticleResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HotArticleController {
	private final HotArticleService hotArticleService;

	@GetMapping("/v1/hot-articles/articles/date/{dateStr}")
	public List<HotArticleResponse> readAll(
		@PathVariable String dateStr
	) {
		return hotArticleService.readAll(dateStr);
	}
}
