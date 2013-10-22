package com.google.play.crawler.sitesolver;

import java.net.URL;
import java.util.Vector;

import com.google.play.crawler.URLQueue;

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
����һ��ku6������ҳURL��������а�����������������ҳ�������ļ�URL
*/
public class YoukuSolver extends SiteSolver {
	
	public YoukuSolver(URL url,URLQueue pageQueue,URLQueue fileQueue) {
		super(url, pageQueue, fileQueue);
	}
	
	public void analyze()
	{
		//�����Ƿ�Ϊ��6��URL
		if (!url.toString().toLowerCase().contains("youku"))
			return;
		
		//debug
		System.out.println("YoukuSolver start working...");
		
		//�����ҳΪ��Ƶ��ҳ������֮���˳�����
		if (analyzeFileUrl(url))
		{
			//debug
			System.out.println("YoukuSolve end work, with input URL a file URL.");
			System.out.println("two storages' sizes are " + pageUrlQueue.size() + " and " + fileQueue.size());
			
			return;
		}
		
		//��������ҳ�в�����Ƶ��ҳ���ӣ�����page����
		Vector<URL> pageUrls = super.getMatchedUrls(url,
				"","http://v.youku.com/v_show/id_\\S+=.html","");
		for (int i=0; i<pageUrls.size(); ++i)
			pageUrlQueue.put(pageUrls.get(i));
		
		//debug
		System.out.println("ku6Solver end work, with input URL a page URL.");
		System.out.println("two storages' sizes are " + pageUrlQueue.size() + " and " + fileQueue.size());
	}
	
	public boolean analyzeFileUrl(URL fileUrl)
	{
		//�ж��Ƿ�Ϊ�ļ�URL
		if (!super.checkUrl(fileUrl,"http://v.youku.com/v_show/id_\\S+=.html"))
			return false;
		
		//��ȡ��ҳ����
		StringBuffer contentBuffer = super.getContent(fileUrl);
		
		//������ҳ������������ʽ���Ӵ�
		Vector<URL> sub1Urls = super.getMatchedUrls(contentBuffer,
				"http://www.flvcd.com/parse.php?kw=","http://v.youku.com/v_show/id_\\S+=.html","");
		
		//������ҳ��������Ƶ����
		Vector<URL> otherFileUrls = super.getMatchedUrls(contentBuffer,
				"","http://v.youku.com/v_show/id_\\S+=.html","");

		//����ƥ�䵽����ҳ
		for (int i=0; i<sub1Urls.size(); ++i)
		{
			Vector<URL> sub2Urls = super.getMatchedUrls(sub1Urls.get(i),
					"","(?<=a href=\")\\S*/flv\\S*?(?=\")","");
			for (int j=0; j<sub2Urls.size(); ++j)	//������������ַ����m_file_que��
				fileQueue.put(sub2Urls.get(j));
		}
		
		//��������Ƶ���Ӽ��뵽page����
		for (int i=0; i<otherFileUrls.size(); ++i)
			pageUrlQueue.put(otherFileUrls.get(i));
		
		//������
		return true;
	}
}