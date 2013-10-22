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
	 * ���캯��
	 */
	public MyCrawler() {
		// ������ʼ��
		pageUrlQueue = new URLQueue("pageUrlQueue.log");
		m_file_que = new URLQueue("fileUrlQueue.log");
		m_proc_msg = new ThreadMessage();
		m_dnld_msg = new ThreadMessage();
		m_proc_mgr = new URLProcessManager(pageUrlQueue, m_file_que, m_proc_msg);
		m_dnld_mgr = new DownloadManager(m_file_que, m_dnld_msg);
	}

	/**
	 * ����ҳURL���������Ԫ��
	 */
	public void pushPageQueue(URL url) {
		pageUrlQueue.put(url);
	}

	/**
	 * ���������ַ�����߳���
	 */
	public void setMaxURLProcThreads(int maxThreads) {
		m_proc_mgr.setMaxThreads(maxThreads);
	}

	/**
	 * ��������ļ������߳���
	 */
	public void setMaxDwonloadThreads(int maxThreads) {
		m_dnld_mgr.setMaxThreads(maxThreads);
	}

	/**
	 * ִ�н���
	 */
	public void run() {
		// �������Ƚ���
		m_proc_mgr.start();
		m_dnld_mgr.start();

		// �����Զ���ֹ
		/*
		 * try { sleep(1000*60*5); //����5���ӣ��Զ�ֹͣ terminate(); } catch
		 * (InterruptedException e) { e.printStackTrace(); }
		 * 
		 * //debug
		 * System.out.println("all processes will terminate a short time later."
		 * );
		 */}

	/**
	 * ��ֹ�������ʹ�������������н�����ֹ
	 */
	public void terminate() {
		m_proc_msg.setStopOrder(true);
		m_dnld_msg.setStopOrder(true);
	}
}
