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

		// 创建日志文件
		String logPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "log";
		String logFile = logPath + System.getProperty("file.separator") + "dealingPageUrl.log";
		try {
			File commonFile = new File(logPath);
			if (!commonFile.isDirectory()) // 生成日志文件夹
				commonFile.mkdir();
			commonFile = new File(logFile);
			if (commonFile.exists()) // 删除原日志文件
				commonFile.delete();
			commonFile.createNewFile(); // 创建新日志文件
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

			// 判断是否有进程终止命令
			if (threadMessage.getStopOrder()) {
				if (logFileAccess == null)
					break;

				// 写入剩余的未处理网址，关闭日志文件
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

			// 从网页URL队列中取出一个URL，新开一个进程，执行URL处理程序
			if (threadMessage.getThreadNumber() < maxThreads && !pageUrlQueue.isEmpty()) {
				// 开启新进程
				URL pageUrl = pageUrlQueue.take();
				new URLProcessor(pageUrl, pageUrlQueue, fileQueue, threadMessage).start();

				// 记录下载日志
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

			// 进程休眠一段时间
			try {
				sleep(5000); // note:在此设置网页处理进程等待时间
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
