package src.sitesolver;

import java.net.URL;
import java.util.Vector;
import src.URLQueue;

/*
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Vector;
import src.URLQueue;
*/

/**
����һ������������ҳURL��������а�����������������ҳ�������ļ�URL
*/
public class TudouSolver extends SiteSolver {
	
	public TudouSolver(URL url,URLQueue pageQueue,URLQueue fileQueue) {
		super(url, pageQueue, fileQueue);
	}
	
	public void analyze()
	{
		//�����Ƿ�Ϊ������URL
		if (!m_url.toString().toLowerCase().contains("tudou"))
			return;
		
		//debug
		System.out.println("TudouSolver start working...");
		
		//�����ҳΪ��Ƶ��ҳ������֮���˳�����
		if (analyzeFileUrl(m_url))
		{
			//debug
			System.out.println("TudouSolver end work, with input URL a file URL.");
			System.out.println("two storages' sizes are " + m_page_que.size() + " and " + m_file_que.size());
			
			return;
		}
		
		//����ҳ�в�����Ƶ��ҳ���ӣ�����page����
		Vector<URL> pageUrls = super.getMatchedUrls(m_url,
				"http://www.tudou.com","/programs/view/\\S*","");
		for (int i=0; i<pageUrls.size(); ++i)
			m_page_que.put(pageUrls.get(i));
		
		//debug
		System.out.println("TudouSolver end work, with input URL a page URL.");
		System.out.println("two storages' sizes are " + m_page_que.size() + " and " + m_file_que.size());
	}
	
	public boolean analyzeFileUrl(URL fileUrl)
	{
		//�ж��Ƿ�Ϊ�ļ�URL
		if (!super.checkUrl(fileUrl,"/programs/view/\\S*"))
			return false;
		
		//��ȡ��ҳ����
		StringBuffer contentBuffer = super.getContent(fileUrl);
		
		//������ҳ������������ʽ���Ӵ�
		Vector<URL> sub1Urls = super.getMatchedUrls(contentBuffer,
				"http://www.tudou.com/player/v.php?id=","(?<=var iid = )\\w+","");
		
		//������ҳ��������Ƶ����
		Vector<URL> otherFileUrls = super.getMatchedUrls(contentBuffer,
				"http://www.tudou.com","/programs/view/\\S*","");

		//����ƥ�䵽����ҳ
		for (int i=0; i<sub1Urls.size(); ++i)
		{
			Vector<URL> sub2Urls = super.getMatchedUrls(sub1Urls.get(i),
					"","(?<=<f w='10'>)\\S*?(?=</f>)","");
			for (int j=0; j<sub2Urls.size(); ++j)	//������������ַ����m_file_que��
				m_file_que.put(sub2Urls.get(j));
		}
		
		//��������Ƶ���Ӽ��뵽page����
		for (int i=0; i<otherFileUrls.size(); ++i)
			m_page_que.put(otherFileUrls.get(i));
		
		//������
		return true;
	}
}
