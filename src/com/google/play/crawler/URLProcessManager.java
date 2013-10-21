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

		//创建日志文件
		String logPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "log";
		String logFile = logPath + System.getProperty("file.separator") + "dealingPageUrl.log";
		try {
			File commonFile = new File(logPath);
			if (!commonFile.isDirectory())	//生成日志文件夹
				commonFile.mkdir();
			commonFile = new File(logFile);
			if (commonFile.exists())	//删除原日志文件
				commonFile.delete();
			commonFile.createNewFile();	//创建新日志文件
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
			
			//判断是否有进程终止命令
			if (m_thrd_msg.getStopOrder())
			{
				if (m_log_file == null)
					break;
				
				//写入剩余的未处理网址，关闭日志文件
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
			
			//从网页URL队列中取出一个URL，新开一个进程，执行URL处理程序
			if (m_thrd_msg.getThreadNumber()<m_maxThreads && !m_page_que.isEmpty())
			{
				
				//开启新进程
				URL pageUrl = m_page_que.take();
				new URLProcessor(pageUrl,m_page_que,m_file_que,m_thrd_msg).start();
				
				//记录下载日志
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
			
			//进程休眠一段时间
			try {
				sleep(5000);	//note:在此设置网页处理进程等待时间
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
