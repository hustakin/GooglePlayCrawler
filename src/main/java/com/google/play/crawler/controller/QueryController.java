package com.google.play.crawler.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.google.play.crawler.bean.GooglePlayApp;
import com.google.play.crawler.dao.IGooglePlayAppDao;

/**
 * @author fanjie
 * @date 2013-10-22
 */
@Controller
@RequestMapping("/android/*")
public class QueryController {

	private static final int PAGE_NUM = 40;

	@Qualifier("appDao")
	@Autowired
	private IGooglePlayAppDao appDao;

	private String lastGenre = null;

	@RequestMapping(value = "view")
	public String view(
			Model model,
			@RequestParam(value = "genre", required = false) String genre,
			@RequestParam(value = "downloadTimesFrom", required = false) String downloadTimesFrom,
			@RequestParam(value = "downloadTimesTo", required = false) String downloadTimesTo,
			@RequestParam(value = "pageNo", required = false) String pageNo) {
		model.addAttribute("genres", appDao.groupGenres());
		if (downloadTimesFrom != null && downloadTimesFrom.trim().equals(""))
			downloadTimesFrom = null;
		if (downloadTimesTo != null && downloadTimesTo.trim().equals(""))
			downloadTimesTo = null;
		if (genre == null && downloadTimesFrom == null
				&& downloadTimesTo == null) {
			model.addAttribute("appsNum", 0);
			model.addAttribute("pageNum", 1);
			model.addAttribute("currentPageNo", 1);
			model.addAttribute("pageNo", 1);
			lastGenre = null;
			return "search";
		}
		model.addAttribute("genre", genre);
		model.addAttribute("downloadTimesFrom", downloadTimesFrom);
		model.addAttribute("downloadTimesTo", downloadTimesTo);
		int intPageNo = 1;
		if (pageNo != null && !pageNo.trim().equals("")) {
			intPageNo = Integer.parseInt(pageNo);
		}

		List<GooglePlayApp> apps = appDao.findGooglePlayApps(genre,
				downloadTimesFrom, downloadTimesTo);
		List<GooglePlayApp> pageApps = retievePageList(apps, PAGE_NUM,
				intPageNo);
		if ((lastGenre == null && genre != null)
				|| (lastGenre != null && !lastGenre.equals(genre))
				&& intPageNo > 1) {
			intPageNo = 1;
			pageApps = retievePageList(apps, PAGE_NUM, intPageNo);
		}
		model.addAttribute("appsNum", apps.size());
		model.addAttribute("pageNum", apps.size() / PAGE_NUM + 1);
		model.addAttribute("currentPageNo", intPageNo);
		model.addAttribute("pageNo", intPageNo);
		model.addAttribute("apps", pageApps);
		lastGenre = genre;
		return "search";
	}

	private List<GooglePlayApp> retievePageList(List<GooglePlayApp> apps,
			int limit, int pageNo) {
		List<GooglePlayApp> sortPageApps = new ArrayList<GooglePlayApp>();
		Collections.sort(apps);
		int startNo = limit * (pageNo - 1);
		for (int i = startNo; i < apps.size() && i < startNo + limit; i++) {
			sortPageApps.add(apps.get(i));
		}
		return sortPageApps;
	}
}
