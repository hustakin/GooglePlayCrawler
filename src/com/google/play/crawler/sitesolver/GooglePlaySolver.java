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
		if (!m_url.toString().toLowerCase().startsWith("https://play.google.com/store"))
			return;

		// debug
		System.out.println("GooglePlaySolver start working...");

		// �����ҳΪ��Ƶ��ҳ������֮���˳�����
		if (analyzeFileUrl(m_url)) {
			// debug
			System.out.println("GooglePlaySolver end work, with input URL a file URL.");
			System.out.println("two storages' sizes are " + m_page_que.size() + " and " + m_file_que.size());

			return;
		}

		// ��������ҳ�в�����Ƶ��ҳ���ӣ�����page����
		Vector<URL> pageUrls = super.getMatchedUrls(m_url, "", "http://v.ku6.com/\\S+.html", "");
		for (int i = 0; i < pageUrls.size(); ++i)
			m_page_que.put(pageUrls.get(i));

		// debug
		System.out.println("ku6Solver end work, with input URL a page URL.");
		System.out.println("two storages' sizes are " + m_page_que.size() + " and " + m_file_que.size());
	}

	public boolean analyzeFileUrl(URL fileUrl) {
		// �ж��Ƿ�Ϊ�ļ�URL
		if (!super.checkUrl(fileUrl, "https://play.google.com/store"))
			return false;

		// ��ȡ��ҳ����
		StringBuffer contentBuffer = super.getContent(fileUrl);

		// ������ҳ������������ʽ���Ӵ�
		Vector<URL> sub1Urls = super.getMatchedUrls(contentBuffer, "", "https://play.google.com/store/apps/details?id=\\S+", "");

		// ����ƥ�䵽����ҳ
		for (int i = 0; i < sub1Urls.size(); ++i) {
			Vector<URL> sub2Urls = super.getMatchedUrls(sub1Urls.get(i), "", "https://play.google.com/store/apps/details?id=\\S+", "");
			for (int j = 0; j < sub2Urls.size(); ++j)
				m_page_que.put(sub2Urls.get(j));
		}

		// ������
		return true;
	}
}