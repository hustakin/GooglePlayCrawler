package com.google.play.crawler.sitesolver;

import java.net.URL;
import java.util.Vector;

import com.google.play.crawler.URLQueue;

/*
 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import java.util.Vector;
 import src.URLQueue;
 */

/**
 * 输入GooglePlay应用的URL，获得其中包含的其他GooglePlay应用网页
 */
public class GooglePlaySolver extends SiteSolver {

	public GooglePlaySolver(URL url, URLQueue pageQueue, URLQueue fileQueue) {
		super(url, pageQueue, fileQueue);
	}

	public void analyze() {
		// 检验是否为GooglePlay应用URL
		if (!url.toString().toLowerCase().startsWith("https://play.google.com/store"))
			return;

		// debug
		System.out.println("GooglePlaySolver start working...");

		// 如果网页为GooglePlay网页，分析之，退出程序
		if (analyzeFileUrl(url)) {
			// debug
			System.out.println("GooglePlaySolver end work, with input URL a file URL.");
			System.out.println("two storages' sizes are " + pageUrlQueue.size() + " and " + fileQueue.size());

			return;
		}

		// 否则，在网页中GooglePlay应用网页链接，加入page队列
		Vector<URL> pageUrls = super.getMatchedUrls(url, "", "https://play.google.com/store/apps/details?id=\\S+", "");
		for (int i = 0; i < pageUrls.size(); ++i) {
			pageUrlQueue.put(pageUrls.get(i));
			fileQueue.put(pageUrls.get(i));
		}

		// debug
		System.out.println("GooglePlaySolver end work, with input URL a page URL.");
		System.out.println("two storages' sizes are " + pageUrlQueue.size() + " and " + fileQueue.size());
	}

	public boolean analyzeFileUrl(URL fileUrl) {
		// 判断是否为文件URL
		if (!super.checkUrl(fileUrl, "https://play.google.com/store/apps/details?id=\\S+"))
			return false;

		// 读取网页内容
		StringBuffer contentBuffer = super.getContent(fileUrl);

		// 分析网页内满足正则表达式的子串
		Vector<URL> sub1Urls = super.getMatchedUrls(contentBuffer, "", "https://play.google.com/store/apps/details?id=\\S+", "");

		// 解析匹配到的网页
		for (int i = 0; i < sub1Urls.size(); ++i) {
			Vector<URL> sub2Urls = super.getMatchedUrls(sub1Urls.get(i), "", "https://play.google.com/store/apps/details?id=\\S+", "");
			for (int j = 0; j < sub2Urls.size(); ++j)
				pageUrlQueue.put(sub2Urls.get(j));
		}

		// 返回真
		return true;
	}
}