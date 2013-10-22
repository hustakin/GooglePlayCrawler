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
	protected String m_file_path; // �����ļ�����·��
	protected RandomAccessFile m_log_file;

	public DownloadManager(URLQueue fileUrls, ThreadMessage threadMsg) {
		// ��Ա������ʼ��
		m_file_que = fileUrls;
		m_maxThreads = 0;
		m_thrd_msg = threadMsg;

		// ���������ļ���
		m_file_path = System.getProperty("user.dir") + System.getProperty("file.separator") + "download";
		File path = new File(m_file_path);
		if (!path.isDirectory()) // ����ļ��в����ڣ��򴴽����ļ���
			path.mkdir();

		// ������־�ļ�
		String logPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "log";
		String logFile = logPath + System.getProperty("file.separator") + "dealingFileUrl.log";
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

			// �ж��Ƿ��н�����ֹ����
			if (m_thrd_msg.getStopOrder()) {
				if (m_log_file == null)
					break;

				// д��ʣ���δ������ַ���ر���־�ļ�
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

			// ���ļ�URL�洢����ȡ��һ��URL���¿�һ�����̣����������ļ�����¼������־
			if (m_thrd_msg.getThreadNumber() < m_maxThreads && !m_file_que.isEmpty()) {
				// �����½���
				URL fileUrl = m_file_que.take();
				try {
					new FileDownloader(fileUrl, getFilename(fileUrl), m_thrd_msg).start();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				// ��¼������־
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

			// ����һ��ʱ��
			try {
				sleep(5000); // note:�ڴ��������ؽ��̵ȴ�ʱ��
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected String getFilename(URL fileUrl) {
		// ����·�����ļ���
		String urlStr = fileUrl.toString();
		String name;
		if (urlStr.toLowerCase().contains("youku"))
			name = (urlStr.substring(urlStr.lastIndexOf('=') + 1)) + ".flv";
		else
			name = urlStr.substring(urlStr.lastIndexOf('/') + 1);
		String path = m_file_path + System.getProperty("file.separator");

		// ���������ļ�
		return path + name;

		/*
		 * //����ļ��������ڣ����ظ��ļ��� if (!new File(path + name).exists()) return
		 * path+name;
		 * 
		 * //����ļ������ڣ�ģ��Ѹ���������ظ��ļ� int counter = 0; String testName; do{ testName
		 * = path + name.substring(0,name.lastIndexOf('.')) + "(" + ++counter +
		 * ")" + name.substring(name.lastIndexOf('.')); //Pay attention!������++
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
