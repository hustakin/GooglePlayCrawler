package com.google.play.crawler;

import java.net.URL;

import com.google.play.crawler.sitesolver.GooglePlaySolver;
import com.google.play.crawler.sitesolver.SiteSolver;

/**
 * 输入一个URL和一个存储队列， 调用URLSolver类，将由输入URL解析出的其他URL存入存储队列
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

		// 注册进程
		threadMessage.addThread();

		// 解析URL，得到相应URL集合
		SiteSolver siteSolver = new GooglePlaySolver(url, pageUrlQueue, fileQueue);

		// 分析网页
		siteSolver.analyze();

		// 注销进程
		threadMessage.decThread();
	}
}
