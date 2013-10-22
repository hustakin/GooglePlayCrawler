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
输入一个ku6网的网页URL，获得其中包含的其他土豆网网页和下载文件URL
*/
public class YoukuSolver extends SiteSolver {
	
	public YoukuSolver(URL url,URLQueue pageQueue,URLQueue fileQueue) {
		super(url, pageQueue, fileQueue);
	}
	
	public void analyze()
	{
		//检验是否为酷6网URL
		if (!url.toString().toLowerCase().contains("youku"))
			return;
		
		//debug
		System.out.println("YoukuSolver start working...");
		
		//如果网页为视频网页，分析之，退出程序
		if (analyzeFileUrl(url))
		{
			//debug
			System.out.println("YoukuSolve end work, with input URL a file URL.");
			System.out.println("two storages' sizes are " + pageUrlQueue.size() + " and " + fileQueue.size());
			
			return;
		}
		
		//否则，在网页中查找视频网页链接，加入page队列
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
		//判断是否为文件URL
		if (!super.checkUrl(fileUrl,"http://v.youku.com/v_show/id_\\S+=.html"))
			return false;
		
		//读取网页内容
		StringBuffer contentBuffer = super.getContent(fileUrl);
		
		//分析网页内满足正则表达式的子串
		Vector<URL> sub1Urls = super.getMatchedUrls(contentBuffer,
				"http://www.flvcd.com/parse.php?kw=","http://v.youku.com/v_show/id_\\S+=.html","");
		
		//分析网页内其他视频链接
		Vector<URL> otherFileUrls = super.getMatchedUrls(contentBuffer,
				"","http://v.youku.com/v_show/id_\\S+=.html","");

		//解析匹配到的网页
		for (int i=0; i<sub1Urls.size(); ++i)
		{
			Vector<URL> sub2Urls = super.getMatchedUrls(sub1Urls.get(i),
					"","(?<=a href=\")\\S*/flv\\S*?(?=\")","");
			for (int j=0; j<sub2Urls.size(); ++j)	//将解析到的网址放入m_file_que中
				fileQueue.put(sub2Urls.get(j));
		}
		
		//将其他视频连接加入到page队列
		for (int i=0; i<otherFileUrls.size(); ++i)
			pageUrlQueue.put(otherFileUrls.get(i));
		
		//返回真
		return true;
	}
}