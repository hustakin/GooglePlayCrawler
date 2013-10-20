package src;

import java.net.URL;
import src.sitesolver.*;

/**
����һ��URL��һ���洢���У�
����URLSolver�࣬��������URL������������URL����洢����
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
		
		//ע�����
		m_thrd_msg.addThread();
		
		//����URL���õ���ӦURL����
		SiteSolver siteSolver;
		
		//�ȶ��Ѿ��ܹ���������վ��ѡ����Ӧ������
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


		//note:�µ���ҳ�������ŵ��˴�
		else
			siteSolver = new SiteSolver(m_url,m_page_que,m_file_que);
		
		//������ҳ
		siteSolver.analyze();
		
		//ע������
		m_thrd_msg.decThread();
	}
}
