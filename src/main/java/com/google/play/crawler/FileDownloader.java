package com.google.play.crawler;

import java.io.*;
import java.net.*;

public class FileDownloader extends Thread {

	protected RandomAccessFile m_file;
	protected URL m_url;
	protected ThreadMessage m_thrd_msg;

	public FileDownloader(URL fileUrl, String filename, ThreadMessage threadMsg) throws FileNotFoundException {
		// debug
		System.out.println("initializing FileDownloader with URL: " + fileUrl.toString());

		m_url = fileUrl;
		m_file = new RandomAccessFile(filename, "rw");// 读取和写入模式
		m_thrd_msg = threadMsg;
	}

	public void run() {
		m_thrd_msg.addThread();

		try {
			// debug
			System.out.println("FileDownloader now dealing with URL: " + m_url.toString());

			// 获得开始下载位置 //note:在此处添加断点续传功能，先读已下载部分大小，再从断点继续下载
			long startPos = m_file.length();

			// 打开连接
			HttpURLConnection httpConnection = (HttpURLConnection) m_url.openConnection();
			httpConnection.setRequestProperty("User-Agent", "Internet Explorer");

			// debug
			System.out.println("first connect working...");

			// 获得网页返回信息码
			int responseCode = httpConnection.getResponseCode(); // 下载模块问题出在这，访问拒绝：没办法，那就不下载

			// debug
			System.out.println("get response code: " + responseCode);

			if (responseCode >= 400) // 请求失败
			{
				m_thrd_msg.decThread();
				return;
			}

			// 获得文件长度
			long fileSize = 0;
			String strHeader;
			for (int i = 1; (strHeader = httpConnection.getHeaderFieldKey(i)) != null; i++) {
				if (strHeader.equals("Content-Length")) {
					fileSize = Integer.parseInt(httpConnection.getHeaderField(strHeader));
					break;
				}
			}

			// debug
			System.out.println("first connect ended. get content length: " + fileSize);

			// 判断是否需要下载
			if (startPos >= fileSize) {
				httpConnection.disconnect();
				m_thrd_msg.decThread();
				return;
			}

			// debug
			System.out.println("second connect working...");

			// 告诉服务器这个文件从startPos字节开始传
			httpConnection = (HttpURLConnection) m_url.openConnection();
			String property = "bytes=" + startPos + "-";
			httpConnection.setRequestProperty("User-Agent", "Internet Explorer");
			httpConnection.setRequestProperty("RANGE", property);

			// debug
			System.out.println("reading file now...");

			// 读取网络文件,写入指定的文件中
			InputStream istream = httpConnection.getInputStream();
			m_file.seek(startPos);
			byte[] buf = new byte[1024];
			int readNum = 0;
			while ((readNum = istream.read(buf)) > 0 && startPos < fileSize) {
				if (m_thrd_msg.getStopOrder()) // 进程中止命令
				{
					m_thrd_msg.decThread();
					return;
				}

				m_file.write(buf, 0, readNum);
				startPos += readNum;
				sleep(10); // 要有道德，别忘了sleep，否则会把人家网站下瘫了
			}

			// debug
			System.out.println("file reading completed.");

			// 断开网络连接
			httpConnection.disconnect();

			// debug
			System.out.println("second connect ended.");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("FileDownloader unable to download this file due to exception.");
		}

		System.out.println("FileDownloader work complete.");
		m_thrd_msg.decThread();
		System.out.println("FileDownloader end work with thread number: " + m_thrd_msg.getThreadNumber());
	}
}
