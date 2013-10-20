package src;

import java.net.URL;
import src.sitesolver.*;

/**
输入一个URL和一个存储队列，
调用URLSolver类，将由输入URL解析出的其他URL存入存储队列
*/
public class URLProcessor extends Thread {
	
	protected URL m_url;
	protected URLQueue m_page_que;
	protected URLQueue m_file_que;
	protected ThreadMessage m_thrd_msg;
	
	public URLProcessor(URL url,URLQueue pageUrls,URLQueue fileUrls,ThreadMessage threadMsg)
	{
		//debug
		System.out.println("initializing URLProcesser with URL: " + url.toString());
		
		m_url = url;
		m_page_que = pageUrls;
		m_file_que = fileUrls;
		m_thrd_msg = threadMsg;
	}
	
	public void run()
	{
		//debug
		System.out.println("URLProcesser now dealing with URL: " + m_url.toString());
		
		//注册进程
		m_thrd_msg.addThread();
		
		//解析URL，得到相应URL集合
		SiteSolver siteSolver;
		
		//比对已经能够解析的网站，选择相应工具类
		String str = m_url.toString().trim().toLowerCase();
		if (str.contains("tudou"))
			siteSolver = new TudouSolver(m_url,m_page_que,m_file_que);
		else if (str.contains("sohu"))
			siteSolver = new SohuSolver(m_url,m_page_que,m_file_que);
		else if (str.contains("sina"))
			siteSolver = new SinaSolver(m_url,m_page_que,m_file_que);
		else if (str.contains("ku6"))
			siteSolver = new Ku6Solver(m_url,m_page_que,m_file_que);
		else if (str.contains("youku"))
			siteSolver = new YoukuSolver(m_url,m_page_que,m_file_que);


		//note:新的网页分析器放到此处
		else
			siteSolver = new SiteSolver(m_url,m_page_que,m_file_que);
		
		//分析网页
		siteSolver.analyze();
		
		//注销进程
		m_thrd_msg.decThread();
	}
}
