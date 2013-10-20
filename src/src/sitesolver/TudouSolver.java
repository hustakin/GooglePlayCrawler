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
输入一个土豆网的网页URL，获得其中包含的其他土豆网网页和下载文件URL
*/
public class TudouSolver extends SiteSolver {
	
	public TudouSolver(URL url,URLQueue pageQueue,URLQueue fileQueue) {
		super(url, pageQueue, fileQueue);
	}
	
	public void analyze()
	{
		//检验是否为土豆网URL
		if (!m_url.toString().toLowerCase().contains("tudou"))
			return;
		
		//debug
		System.out.println("TudouSolver start working...");
		
		//如果网页为视频网页，分析之，退出程序
		if (analyzeFileUrl(m_url))
		{
			//debug
			System.out.println("TudouSolver end work, with input URL a file URL.");
			System.out.println("two storages' sizes are " + m_page_que.size() + " and " + m_file_que.size());
			
			return;
		}
		
		//在网页中查找视频网页链接，加入page队列
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
		//判断是否为文件URL
		if (!super.checkUrl(fileUrl,"/programs/view/\\S*"))
			return false;
		
		//读取网页内容
		StringBuffer contentBuffer = super.getContent(fileUrl);
		
		//分析网页内满足正则表达式的子串
		Vector<URL> sub1Urls = super.getMatchedUrls(contentBuffer,
				"http://www.tudou.com/player/v.php?id=","(?<=var iid = )\\w+","");
		
		//分析网页内其他视频链接
		Vector<URL> otherFileUrls = super.getMatchedUrls(contentBuffer,
				"http://www.tudou.com","/programs/view/\\S*","");

		//解析匹配到的网页
		for (int i=0; i<sub1Urls.size(); ++i)
		{
			Vector<URL> sub2Urls = super.getMatchedUrls(sub1Urls.get(i),
					"","(?<=<f w='10'>)\\S*?(?=</f>)","");
			for (int j=0; j<sub2Urls.size(); ++j)	//将解析到的网址放入m_file_que中
				m_file_que.put(sub2Urls.get(j));
		}
		
		//将其他视频连接加入到page队列
		for (int i=0; i<otherFileUrls.size(); ++i)
			m_page_que.put(otherFileUrls.get(i));
		
		//返回真
		return true;
	}
}
