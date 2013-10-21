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
	
	protected URLQueue m_page_que;
	protected URLQueue m_file_que;	
	protected int m_maxThreads;
	protected ThreadMessage m_thrd_msg;
	protected RandomAccessFile m_log_file;

	public URLProcessManager(URLQueue pageUrls,URLQueue fileUrls,ThreadMessage threadMsg)
	{
		m_page_que = pageUrls;
		m_file_que = fileUrls;
		m_maxThreads = 0;
		m_thrd_msg = threadMsg;

		//������־�ļ�
		String logPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "log";
		String logFile = logPath + System.getProperty("file.separator") + "dealingPageUrl.log";
		try {
			File commonFile = new File(logPath);
			if (!commonFile.isDirectory())	//������־�ļ���
				commonFile.mkdir();
			commonFile = new File(logFile);
			if (commonFile.exists())	//ɾ��ԭ��־�ļ�
				commonFile.delete();
			commonFile.createNewFile();	//��������־�ļ�
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			m_log_file = new RandomAccessFile(logFile,"rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			m_log_file = null;
		}
	}
	
	public URLProcessManager(URLQueue pageUrls,URLQueue fileUrls,ThreadMessage threadMsg,int maxThreads)
	{
		this(pageUrls,fileUrls,threadMsg);
		setMaxThreads(maxThreads);
	}
	
	public void run()
	{
		while (true)
		{
			//debug
			System.out.println("hello URLProcessManager, with m_page_que.size() = "
					+ m_page_que.size() + " and thread number is " + m_thrd_msg.getThreadNumber() + " of " + m_maxThreads);
			
			//�ж��Ƿ��н�����ֹ����
			if (m_thrd_msg.getStopOrder())
			{
				if (m_log_file == null)
					break;
				
				//д��ʣ���δ������ַ���ر���־�ļ�
				try {
					m_log_file.write("\n#not measured URLs:\n".getBytes());
					while (!m_page_que.isEmpty())
						m_log_file.write((m_page_que.take().toString() + "\n").getBytes());
					m_log_file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			
			//����ҳURL������ȡ��һ��URL���¿�һ�����̣�ִ��URL�������
			if (m_thrd_msg.getThreadNumber()<m_maxThreads && !m_page_que.isEmpty())
			{
				
				//�����½���
				URL pageUrl = m_page_que.take();
				new URLProcessor(pageUrl,m_page_que,m_file_que,m_thrd_msg).start();
				
				//��¼������־
				try {
					if (m_log_file != null)
					{
						GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
						m_log_file.writeBytes("" +
								calendar.get(Calendar.YEAR) + "-" +
								(calendar.get(Calendar.MONTH)+1) + "-" +
								calendar.get(Calendar.DAY_OF_MONTH) + " " + 
								calendar.get(Calendar.HOUR_OF_DAY) + ":" +
								calendar.get(Calendar.MINUTE) + ":" + 
								calendar.get(Calendar.SECOND) + "." +
								calendar.get(Calendar.MILLISECOND) + "\t");
						m_log_file.write((pageUrl.toString() + "\n").getBytes());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			//��������һ��ʱ��
			try {
				sleep(5000);	//note:�ڴ�������ҳ������̵ȴ�ʱ��
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setMaxThreads(int maxThreads)
	{
		m_maxThreads = maxThreads;
	}
	
	public int getMaxThreads()
	{
		return m_maxThreads;
	}
}
