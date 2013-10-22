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
 * ����GooglePlayӦ�õ�URL��������а���������GooglePlayӦ����ҳ
 */
public class GooglePlaySolver extends SiteSolver {

	public GooglePlaySolver(URL url, URLQueue pageQueue, URLQueue fileQueue) {
		super(url, pageQueue, fileQueue);
	}

	public void analyze() {
		// �����Ƿ�ΪGooglePlayӦ��URL
		if (!url.toString().toLowerCase().startsWith("https://play.google.com/store"))
			return;

		// debug
		System.out.println("GooglePlaySolver start working...");

		// �����ҳΪGooglePlay��ҳ������֮���˳�����
		if (analyzeFileUrl(url)) {
			// debug
			System.out.println("GooglePlaySolver end work, with input URL a file URL.");
			System.out.println("two storages' sizes are " + pageUrlQueue.size() + " and " + fileQueue.size());

			return;
		}

		// ��������ҳ��GooglePlayӦ����ҳ���ӣ�����page����
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
		// �ж��Ƿ�Ϊ�ļ�URL
		if (!super.checkUrl(fileUrl, "https://play.google.com/store/apps/details?id=\\S+"))
			return false;

		// ��ȡ��ҳ����
		StringBuffer contentBuffer = super.getContent(fileUrl);

		// ������ҳ������������ʽ���Ӵ�
		Vector<URL> sub1Urls = super.getMatchedUrls(contentBuffer, "", "https://play.google.com/store/apps/details?id=\\S+", "");

		// ����ƥ�䵽����ҳ
		for (int i = 0; i < sub1Urls.size(); ++i) {
			Vector<URL> sub2Urls = super.getMatchedUrls(sub1Urls.get(i), "", "https://play.google.com/store/apps/details?id=\\S+", "");
			for (int j = 0; j < sub2Urls.size(); ++j)
				pageUrlQueue.put(sub2Urls.get(j));
		}

		// ������
		return true;
	}
}