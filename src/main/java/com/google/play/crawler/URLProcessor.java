package com.google.play.crawler;

import java.net.URL;

import com.google.play.crawler.sitesolver.GooglePlaySolver;
import com.google.play.crawler.sitesolver.SiteSolver;

/**
 * ����һ��URL��һ���洢���У� ����URLSolver�࣬��������URL������������URL����洢����
 */
public class URLProcessor extends Thread {

	protected URL url;
	protected URLQueue pageUrlQueue;
	protected URLQueue fileQueue;
	protected ThreadMessage threadMessage;

	public URLProcessor(URL url, URLQueue pageUrls, URLQueue fileUrls, ThreadMessage threadMsg) {
		// debug
		System.out.println("initializing URLProcesser with URL: " + url.toString());

		this.url = url;
		pageUrlQueue = pageUrls;
		fileQueue = fileUrls;
		threadMessage = threadMsg;
	}

	public void run() {
		// debug
		System.out.println("URLProcesser now dealing with URL: " + url.toString());

		// ע�����
		threadMessage.addThread();

		// ����URL���õ���ӦURL����
		SiteSolver siteSolver = new GooglePlaySolver(url, pageUrlQueue, fileQueue);

		// ������ҳ
		siteSolver.analyze();

		// ע������
		threadMessage.decThread();
	}
}
