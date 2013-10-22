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
		m_file = new RandomAccessFile(filename, "rw");// ��ȡ��д��ģʽ
		m_thrd_msg = threadMsg;
	}

	public void run() {
		m_thrd_msg.addThread();

		try {
			// debug
			System.out.println("FileDownloader now dealing with URL: " + m_url.toString());

			// ��ÿ�ʼ����λ�� //note:�ڴ˴���Ӷϵ��������ܣ��ȶ������ز��ִ�С���ٴӶϵ��������
			long startPos = m_file.length();

			// ������
			HttpURLConnection httpConnection = (HttpURLConnection) m_url.openConnection();
			httpConnection.setRequestProperty("User-Agent", "Internet Explorer");

			// debug
			System.out.println("first connect working...");

			// �����ҳ������Ϣ��
			int responseCode = httpConnection.getResponseCode(); // ����ģ����������⣬���ʾܾ���û�취���ǾͲ�����

			// debug
			System.out.println("get response code: " + responseCode);

			if (responseCode >= 400) // ����ʧ��
			{
				m_thrd_msg.decThread();
				return;
			}

			// ����ļ�����
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

			// �ж��Ƿ���Ҫ����
			if (startPos >= fileSize) {
				httpConnection.disconnect();
				m_thrd_msg.decThread();
				return;
			}

			// debug
			System.out.println("second connect working...");

			// ���߷���������ļ���startPos�ֽڿ�ʼ��
			httpConnection = (HttpURLConnection) m_url.openConnection();
			String property = "bytes=" + startPos + "-";
			httpConnection.setRequestProperty("User-Agent", "Internet Explorer");
			httpConnection.setRequestProperty("RANGE", property);

			// debug
			System.out.println("reading file now...");

			// ��ȡ�����ļ�,д��ָ�����ļ���
			InputStream istream = httpConnection.getInputStream();
			m_file.seek(startPos);
			byte[] buf = new byte[1024];
			int readNum = 0;
			while ((readNum = istream.read(buf)) > 0 && startPos < fileSize) {
				if (m_thrd_msg.getStopOrder()) // ������ֹ����
				{
					m_thrd_msg.decThread();
					return;
				}

				m_file.write(buf, 0, readNum);
				startPos += readNum;
				sleep(10); // Ҫ�е��£�������sleep���������˼���վ��̱��
			}

			// debug
			System.out.println("file reading completed.");

			// �Ͽ���������
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
