package com.google.play.crawler;

import java.net.*;

public class MyCrawler extends Thread {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			MyCrawler myCrawler = new MyCrawler();
			myCrawler.pushPageQueue(new URL("https://play.google.com/store?hl=en-us"));

			// debug
			System.out.println("init size of m_page_que is " + myCrawler.pageUrlQueue.size());

			myCrawler.setMaxURLProcThreads(10);
			myCrawler.setMaxDwonloadThreads(5);
			myCrawler.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected URLQueue pageUrlQueue;
	protected URLQueue m_file_que;
	protected URLProcessManager m_proc_mgr;
	protected DownloadManager m_dnld_mgr;
	protected ThreadMessage m_proc_msg;
	protected ThreadMessage m_dnld_msg;

	/**
	 * 构造函数
	 */
	public MyCrawler() {
		// 变量初始化
		pageUrlQueue = new URLQueue("pageUrlQueue.log");
		m_file_que = new URLQueue("fileUrlQueue.log");
		m_proc_msg = new ThreadMessage();
		m_dnld_msg = new ThreadMessage();
		m_proc_mgr = new URLProcessManager(pageUrlQueue, m_file_que, m_proc_msg);
		m_dnld_mgr = new DownloadManager(m_file_que, m_dnld_msg);
	}

	/**
	 * 向网页URL队列中添加元素
	 */
	public void pushPageQueue(URL url) {
		pageUrlQueue.put(url);
	}

	/**
	 * 设置最大网址分析线程数
	 */
	public void setMaxURLProcThreads(int maxThreads) {
		m_proc_mgr.setMaxThreads(maxThreads);
	}

	/**
	 * 设置最大文件下载线程数
	 */
	public void setMaxDwonloadThreads(int maxThreads) {
		m_dnld_mgr.setMaxThreads(maxThreads);
	}

	/**
	 * 执行进程
	 */
	public void run() {
		// 启动调度进程
		m_proc_mgr.start();
		m_dnld_mgr.start();

		// 进程自动终止
		/*
		 * try { sleep(1000*60*5); //运行5分钟，自动停止 terminate(); } catch
		 * (InterruptedException e) { e.printStackTrace(); }
		 * 
		 * //debug
		 * System.out.println("all processes will terminate a short time later."
		 * );
		 */}

	/**
	 * 终止进程命令，使程序所包含所有进程终止
	 */
	public void terminate() {
		m_proc_msg.setStopOrder(true);
		m_dnld_msg.setStopOrder(true);
	}
}
