package src.sitesolver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Vector;
import src.URLQueue;


/**
输入一个新浪网的网页URL，获得其中包含的其他新浪网网页和下载文件URL
*/
public class SinaSolver extends SiteSolver {
	
	public SinaSolver(URL url,URLQueue pageQueue,URLQueue fileQueue) {
		super(url, pageQueue, fileQueue);
	}
	
	public void analyze()
	{
		//检验是否为新浪网URL
		if (!m_url.toString().toLowerCase().contains("sina"))
			return;
		
		//debug
		System.out.println("SinaSolver.analyze() working...");
		
		//检查是否为文件URL，若是，分析后退出
		boolean isFileUrl = false;
		if (analyzeType1Url(m_url))
			isFileUrl = true;
		else if (analyzeType2Url(m_url))
			isFileUrl = true;
		else if (analyzeType3Url(m_url))
			isFileUrl = true;
		else
			isFileUrl = false;
		if (isFileUrl)
		{
			//debug
			System.out.println("SinaSolver.analyze() end work, with URL a file URL.");
			System.out.println("two storages' sizes are " + m_page_que.size() + " and " + m_file_que.size());
			
			return;
		}
		
		//debug
		System.out.println("SinaSolver.analyze() no url type matchs...");
		System.out.println("SinaSolver.analyze() reading page content...");
		
		
		//读取网页内容
		m_content = super.getContent(m_url);
		
		//debug
		System.out.println("SinaSolver.analyze() end read content.");
		
		//将URL一律放入page队列
		//分析网页内容，获取文件URL，加入page队列
		Vector<URL> urls = super.getMatchedUrls(m_content,"",
				"(http://video.sina.com.cn/\\S*.shtml)|" +
				"(http://you.video.sina.com.cn/pg/topicdetail/topicPlay.php\\S*#\\S*(?=\"))|" +
				"(http://you.video.sina.com.cn/b/\\S*.html)","");
		for (int i=0; i<urls.size(); ++i)
		{
			//debug
			System.out.println("SinaSolver.analyze(): adding URL to m_file_que: " + urls.get(i));
			
			m_page_que.put(urls.get(i));
		}
		
	/*	
		//分析网页内容，获取文件URL，按不同类型URL执行不同操作
		Vector<URL> urls = super.getMatchedUrls(m_content,"",
				"(http://video.sina.com.cn/\\S*.shtml)|" +
				"(http://you.video.sina.com.cn/pg/topicdetail/topicPlay.php\\S*#\\S*(?=\"))|" +
				"(http://you.video.sina.com.cn/b/\\S*.html)","");
		for (int i=0; i<urls.size(); ++i)
		{
			URL tUrl = urls.get(i);
			if(super.checkUrl(tUrl,"http://video.sina.com.cn/\\S*.shtml"))
				m_page_que.put(tUrl);	//加入page队列
			else if (super.checkUrl(tUrl,"http://you.video.sina.com.cn/pg/topicdetail/topicPlay.php\\S*#\\S*(?=\")"))
				analyzeType2Url(tUrl);	//直接分析URL
			else if (super.checkUrl(tUrl,"http://you.video.sina.com.cn/b/\\S*.html"))
				analyzeType3Url(tUrl);	//直接分析URL
		}
	*/	
		//debug
		System.out.println("SinaSolver.analyze() end work, with URL a page URL.");
		System.out.println("two storages' sizes are " + m_page_que.size() + " and " + m_file_que.size());
	}
	
	/**
	分析形如"http://video.sina.com.cn/\\S*.shtml"的URL
	*/
	public boolean analyzeType1Url(URL type1Url)
	{
		//检验是否为第一类文件URL
		if (!super.checkUrl(type1Url,"http://video.sina.com.cn/\\S*.shtml"))
			return false;	//不是第一类文件URL，不做操作，返回错误
		
		//debug
		System.out.println("SinaSolver.analyzeType1Url() working...");
		
		//读取网页内容
		StringBuffer contentBuffer = super.getContent(type1Url);
		
		//查找匹配子串，得到vid和uid
		Vector<String> vids = new Vector<String>();
		Vector<String> uids = new Vector<String>();
		Matcher vidMatcher = Pattern.compile("(?<=&vid=)\\w+",Pattern.CASE_INSENSITIVE).matcher(contentBuffer);
		Matcher uidMatcher = Pattern.compile("(?<=&uid=)\\w+",Pattern.CASE_INSENSITIVE).matcher(contentBuffer);
		while (vidMatcher.find() && uidMatcher.find())
		{
			vids.add(vidMatcher.group());
			uids.add(uidMatcher.group());
		}
		
		//分析查找文件URL，加入队列
		for (int i=0; i<vids.size() && i<uids.size(); ++i)
		{
			Vector<URL> urls = getUrlWithIds(vids.get(i),uids.get(i));
			for (int j=0; j<urls.size(); ++j)
			{
				//debug
				System.out.println("SinaSover.analyzeType1Url(): adding url to m_file_que: " + urls.get(j).toString());
				
				m_file_que.put(urls.get(j));	//Pay attention! 是j不是i
			}
		}
		
		//debug
		System.out.println("SinaSolver.analyzeType1Url() end work.");
		
		return true;
	}
	
	/**
	分析形如"http://you.video.sina.com.cn/pg/topicdetail/topicPlay.php\\S*#\\S*(?=\")"的URL
	*/
	public boolean analyzeType2Url(URL type2Url)
	{
		//检验是否为第二类文件URL	
		if (!super.checkUrl(type2Url,"http://you.video.sina.com.cn/pg/topicdetail/topicPlay.php\\S*#\\S*"))	//Pay attention!已经得到的URL中不会再有"了
			return false;	//不是第二类文件URL，不做操作，返回错误
			
		//debug
		System.out.println("SinaSolver.analyzeType2Url() working...");
		
		//分析URL本身，得到vid和uid
		Matcher vidMatcher = Pattern.compile("(?<=#)\\w+",Pattern.CASE_INSENSITIVE).matcher(type2Url.toString());
		Matcher uidMatcher = Pattern.compile("(?<=&uid=)\\w+",Pattern.CASE_INSENSITIVE).matcher(type2Url.toString());
		if ( !(vidMatcher.find()&&uidMatcher.find()) )
		{
			
			//debug
			System.out.println("SinaSolver.analyzeType2Url() end work.");
			
			return true;
		}
		String vid = vidMatcher.group();
		String uid = uidMatcher.group();
		
		//分析查找文件URL，加入队列
		Vector<URL> urls = getUrlWithIds(vid,uid);
		for (int j=0; j<urls.size(); ++j)
		{

			//debug
			System.out.println("SinaSolver.analyzeType2Url() adding url to m_file_que: " + urls.get(j));
			
			m_file_que.put(urls.get(j));
		}
		
		//debug
		System.out.println("SinaSolver.analyzeType2Url() end work.");
		
		return true;
	}
	
	/**
	分析形如"http://you.video.sina.com.cn/b/\\S*.html"的URL
	*/
	public boolean analyzeType3Url(URL type3Url)
	{
		//检验是否为第三类文件URL
		if (!super.checkUrl(type3Url,"http://you.video.sina.com.cn/b/\\S*.html"))
			return false;	//不是第三类文件URL，不做操作，返回错误
			
		//debug
		System.out.println("SinaSolver.analyzeType3Url() working...");
		
		//读取网页内容
		StringBuffer contentBuffer = super.getContent(type3Url);
		
		//查找匹配子串，得到vid和uid
		Matcher vidMatcher = Pattern.compile("(?<=\"\\Svid\":)\\w*(?=,)",Pattern.CASE_INSENSITIVE).matcher(contentBuffer);
		Matcher uidMatcher = Pattern.compile("(?<=\"\\Suid\":)\\w*(?=,)",Pattern.CASE_INSENSITIVE).matcher(contentBuffer);
		if ( !(vidMatcher.find()&&uidMatcher.find()) )
		{
			
			//debug
			System.out.println("SinaSolver.analyzeType3Url() end work.");
			
			return true;
		}
		String vid = vidMatcher.group();
		String uid = uidMatcher.group();
		
		//分析查找文件URL，加入队列
		Vector<URL> urls = getUrlWithIds(vid,uid);
		for (int j=0; j<urls.size(); ++j)
		{
			
			//debug
			System.out.println("SinaSolver.analyzeType3Url(): adding url to m_file_que: " + urls.get(j));
			
			m_file_que.put(urls.get(j));
		}
		
		//debug
		System.out.println("SinaSolver.analyzeType3Url() end work.");
		
		return true;
	}
	
	/**
	按照输入的vid和uid访问网页，得到文件URL
	*/
	public Vector<URL> getUrlWithIds(String vid,String uid)
	{
		//debug
		System.out.println("SinaSolver.getUrlWithIds() working...");
		

		//读取网页内容
		StringBuffer contentBuffer = new StringBuffer();
		try {
			//tbc:怀疑是否要加%
			contentBuffer = super.getContent(
					new URL("http://v.iask.com/v_ask.php?auto=1&vid=%" + vid + "&uid=%" + uid));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//debug
		System.out.println("URL is: http://v.iask.com/v_ask.php?auto=1&vid=%" + vid + "&uid=%" + uid);
		
		//匹配子串，修改部分标志，得到文件URL
		Pattern pattern = Pattern.compile("(?<=urldown=)\\S*",Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(contentBuffer);
		Vector<URL> urls = new Vector<URL>();
		while (matcher.find())
		{
			String strUrl = matcher.group().trim();
			strUrl = strUrl.replaceAll("%3A",":");
			strUrl = strUrl.replaceAll("%2F","/");
			try {
				urls.add(new URL(strUrl));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			//debug
			System.out.println("I want to see URL: " + strUrl);
		}
		
		//debug
		System.out.println("SinaSolver.getUrlWithIds() end work.");
		
		//返回结果
		return urls;
	}
}
