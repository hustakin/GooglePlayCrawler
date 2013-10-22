package com.google.play.crawler;

import java.io.*;
import java.net.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DownloadManager extends Thread {

	protected URLQueue m_file_que;
	protected int m_maxThreads;
	protected ThreadMessage m_thrd_msg;
	protected String m_file_path; // 下载文件所在路径
	protected RandomAccessFile m_log_file;

	public DownloadManager(URLQueue fileUrls, ThreadMessage threadMsg) {
		// 成员变量初始化
		m_file_que = fileUrls;
		m_maxThreads = 0;
		m_thrd_msg = threadMsg;

		// 创建下载文件夹
		m_file_path = System.getProperty("user.dir") + System.getProperty("file.separator") + "download";
		File path = new File(m_file_path);
		if (!path.isDirectory()) // 如果文件夹不存在，则创建新文件夹
			path.mkdir();

		// 创建日志文件
		String logPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "log";
		String logFile = logPath + System.getProperty("file.separator") + "dealingFileUrl.log";
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
			m_log_file = new RandomAccessFile(logFile, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			m_log_file = null;
		}
	}

	public DownloadManager(URLQueue fileUrls, ThreadMessage threadMsg, int maxThreads) {
		this(fileUrls, threadMsg);
		setMaxThreads(maxThreads);
	}

	public void run() {
		while (true) {
			// debug
			System.out.println("hello DownloadManager, with m_file_que.size() = " + m_file_que.size() + " and thread number is " + m_thrd_msg.getThreadNumber() + " of "
					+ m_maxThreads);

			// 判断是否有进程终止命令
			if (m_thrd_msg.getStopOrder()) {
				if (m_log_file == null)
					break;

				// 写入剩余的未处理网址，关闭日志文件
				try {
					m_log_file.write("\n#not measured URLs:\n".getBytes());
					while (!m_file_que.isEmpty())
						m_log_file.write((m_file_que.take().toString() + "\n").getBytes());
					m_log_file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}

			// 从文件URL存储队列取出一个URL，新开一个进程，尝试下载文件，记录下载日志
			if (m_thrd_msg.getThreadNumber() < m_maxThreads && !m_file_que.isEmpty()) {
				// 开启新进程
				URL fileUrl = m_file_que.take();
				try {
					new FileDownloader(fileUrl, getFilename(fileUrl), m_thrd_msg).start();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				// 记录下载日志
				try {
					if (m_log_file != null) {
						GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
						m_log_file.writeBytes("" + calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " "
								+ calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "."
								+ calendar.get(Calendar.MILLISECOND) + "\t");
						m_log_file.write((fileUrl.toString() + "\n").getBytes());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// 休眠一定时间
			try {
				sleep(5000); // note:在此设置下载进程等待时间
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected String getFilename(URL fileUrl) {
		// 解析路径和文件名
		String urlStr = fileUrl.toString();
		String name;
		if (urlStr.toLowerCase().contains("youku"))
			name = (urlStr.substring(urlStr.lastIndexOf('=') + 1)) + ".flv";
		else
			name = urlStr.substring(urlStr.lastIndexOf('/') + 1);
		String path = m_file_path + System.getProperty("file.separator");

		// 不重命名文件
		return path + name;

		/*
		 * //如果文件名不存在，返回该文件名 if (!new File(path + name).exists()) return
		 * path+name;
		 * 
		 * //如果文件名存在，模仿迅雷重命名重复文件 int counter = 0; String testName; do{ testName
		 * = path + name.substring(0,name.lastIndexOf('.')) + "(" + ++counter +
		 * ")" + name.substring(name.lastIndexOf('.')); //Pay attention!别忘了++
		 * }while(new File(testName).exists()); return testName;
		 */
	}

	public void setMaxThreads(int maxThreads) {
		m_maxThreads = maxThreads;
	}

	public int getMaxThreads() {
		return m_maxThreads;
	}
}
