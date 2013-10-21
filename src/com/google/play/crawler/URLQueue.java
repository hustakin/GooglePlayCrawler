package com.google.play.crawler;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.HashSet;
import java.util.TimeZone;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.URL;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 存储队列，存储URL（在队列生存期内检验重复）
 */
public class URLQueue {
	protected Set<URL> urlSet;
	protected LinkedBlockingQueue<URL> urlQueue;
	protected RandomAccessFile logFileAccess;

	public URLQueue() {
		urlSet = Collections.synchronizedSet(new HashSet<URL>()); // 同步的哈希表
		urlQueue = new LinkedBlockingQueue<URL>(); // URL队列
	}

	public URLQueue(String logFile) {
		this();

		// 创建日志文件
		String logPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "log";
		logFile = logPath + System.getProperty("file.separator") + logFile;
		try {
			File commonFile = new File(logPath);
			if (!commonFile.isDirectory()) // 生成日志文件夹,isDirectory()当且仅当此抽象路径名表示的文件存在且
											// 是一个目录时，返回 true；否则返回 false

				commonFile.mkdir(); // 创建此抽象路径名指定的目录。
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

	public void put(URL url) {
		// 检验记录中是否有此URL
		if (urlSet.add(url)) // note:使用Hash查询URL是否重复
		{
			try {
				urlQueue.put(url); // 在队列中加入此URL
				if (logFileAccess != null) {
					logFileAccess.writeBytes("" + urlSet.size() + "\t");
					GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
					logFileAccess.writeBytes("" + calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " "
							+ calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "."
							+ calendar.get(Calendar.MILLISECOND) + "\t");
					logFileAccess.writeBytes(url.toString().trim() + "\n");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public URL take() {
		try {
			return urlQueue.take();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public URL peek() {
		return urlQueue.peek();
	}

	public int size() {
		return urlQueue.size();
	}

	public boolean isEmpty() {
		return urlQueue.isEmpty();
	}
}
