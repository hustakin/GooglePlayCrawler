package com.google.play.crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class URLProcessManager extends Thread {

	protected URLQueue pageUrlQueue;
	protected URLQueue fileQueue;
	protected int maxThreads;
	protected ThreadMessage threadMessage;
	protected RandomAccessFile logFileAccess;

	public URLProcessManager(URLQueue pageUrls, URLQueue fileUrls, ThreadMessage threadMsg) {
		pageUrlQueue = pageUrls;
		fileQueue = fileUrls;
		maxThreads = 0;
		threadMessage = threadMsg;

		// ������־�ļ�
		String logPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "log";
		String logFile = logPath + System.getProperty("file.separator") + "dealingPageUrl.log";
		try {
			File commonFile = new File(logPath);
			if (!commonFile.isDirectory()) // ������־�ļ���
				commonFile.mkdir();
			commonFile = new File(logFile);
			if (commonFile.exists()) // ɾ��ԭ��־�ļ�
				commonFile.delete();
			commonFile.createNewFile(); // ��������־�ļ�
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			logFileAccess = new RandomAccessFile(logFile, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logFileAccess = null;
		}
	}

	public URLProcessManager(URLQueue pageUrls, URLQueue fileUrls, ThreadMessage threadMsg, int maxThreads) {
		this(pageUrls, fileUrls, threadMsg);
		setMaxThreads(maxThreads);
	}

	public void run() {
		while (true) {
			// debug
			System.out.println("hello URLProcessManager, with pageUrlQueue.size() = " + pageUrlQueue.size() + " and thread number is " + threadMessage.getThreadNumber() + " of "
					+ maxThreads);

			// �ж��Ƿ��н�����ֹ����
			if (threadMessage.getStopOrder()) {
				if (logFileAccess == null)
					break;

				// д��ʣ���δ������ַ���ر���־�ļ�
				try {
					logFileAccess.write("\n#not measured URLs:\n".getBytes());
					while (!pageUrlQueue.isEmpty())
						logFileAccess.write((pageUrlQueue.take().toString() + "\n").getBytes());
					logFileAccess.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}

			// ����ҳURL������ȡ��һ��URL���¿�һ�����̣�ִ��URL�������
			if (threadMessage.getThreadNumber() < maxThreads && !pageUrlQueue.isEmpty()) {
				// �����½���
				URL pageUrl = pageUrlQueue.take();
				new URLProcessor(pageUrl, pageUrlQueue, fileQueue, threadMessage).start();

				// ��¼������־
				try {
					if (logFileAccess != null) {
						GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
						logFileAccess.writeBytes("" + calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " "
								+ calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "."
								+ calendar.get(Calendar.MILLISECOND) + "\t");
						logFileAccess.write((pageUrl.toString() + "\n").getBytes());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// ��������һ��ʱ��
			try {
				sleep(5000); // note:�ڴ�������ҳ������̵ȴ�ʱ��
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	public int getMaxThreads() {
		return maxThreads;
	}
}
