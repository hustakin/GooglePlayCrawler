package src.sitesolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Vector;
import src.URLQueue;


/**
输入一个搜狐网的网页URL，获得其中包含的下载文件URL
*/
public class SohuSolver extends SiteSolver {
	
	public SohuSolver(URL url,URLQueue pageQueue,URLQueue fileQueue) {
		super(url, pageQueue, fileQueue);
	}
	
	public void analyze()
	{
		//检验是否为搜狐网URL
		if (!m_url.toString().toLowerCase().contains("sohu"))
			return;
		
		//debug
		System.out.println("SohuSolver start working...");
		
		
		//检验如果URL为文件URL，则不再分析其网页中的页面URL
		if (analyzeFileUrl(m_url))
		{
			//debug
			System.out.println("SohuSolver end work, with input URL a file URL.");
			System.out.println("two storages' sizes are " + m_page_que.size() + " and " + m_file_que.size());
			
			return;
		}
		
		//否则，在网页中查找视频网页链接，加入page队列
		Vector<URL> pageUrls = super.getMatchedUrls(m_url,
				"","http://v.blog.sohu.com/u/vw/\\w+","");
		for (int i=0; i<pageUrls.size(); ++i)
			m_page_que.put(pageUrls.get(i));
		
		//debug
		System.out.println("SohuSolver end work, with input URL a page URL.");
		System.out.println("two storages' sizes are " + m_page_que.size() + " and " + m_file_que.size());
	}
	
	/**
	输入链接到文件的URL，解析网页，将解析到的文件URL加入到m_file_que中去，返回真
	如果URL不能指向文件，返回假
	*/
	public boolean analyzeFileUrl(URL fileUrl)
	{
		//检验是否为文件URL
		if (!checkUrl(m_url,"http://v.blog.sohu.com/u/vw/\\w+"))
			return false;	//不是文件URL，不做操作，返回错误
		
		//读取网页内容
		StringBuffer contentBuffer = super.getContent(fileUrl);
		
		//读取网页内满足给定正则表达式的URL
		Vector<URL> urls = SiteSolver.getMatchedUrls(contentBuffer,
				"http://v.blog.sohu.com/videinfo.jhtml?m=view&id=",
				"(?<=var _videoId = )\\w*(?=;)","&outType=3&from=1&block=");
		
		//分析网页内其他视频链接
		Vector<URL> otherFileUrls = super.getMatchedUrls(contentBuffer,
				"","http://v.blog.sohu.com/u/vw/\\w+","");
		
		//解析匹配到的网页
		for (int i=0; i<urls.size(); ++i)
		{
			//取一个URL
			URL tUrl = urls.get(i);
			
			//分析其指向的网页，得到视频被分割的段数
			int n=0;	//视频被分割的段数
			try {
				URL url0 = new URL(tUrl.toString().trim()+"0");
				BufferedReader contentReader = 
					new BufferedReader(new InputStreamReader(url0.openStream()));
				String line;
				Pattern filePattern = Pattern.compile("(?<=flvUrl\":\")\\S*?(?=\")",Pattern.CASE_INSENSITIVE);	//flv地址模式
				Pattern lengthPattern = Pattern.compile("(?<=videoLength\":)\\S*?(?=,)",Pattern.CASE_INSENSITIVE);	//flv长度模式
				//Pattern lengthPattern = Pattern.compile("(?<=videoSize\":)\\S*?(?=,)",Pattern.CASE_INSENSITIVE);	//flv长度模式
				while ((line = contentReader.readLine())!=null)
				{
					Matcher fileMatcher = filePattern.matcher(line);
					Matcher lengthMatcher = lengthPattern.matcher(line);
					if (fileMatcher.find() && lengthMatcher.find())
					{
						int length = Integer.parseInt(lengthMatcher.group());	//获得视频长度
						n = length/420;		//视频被分割成7分钟一段//tbc:会不会少算一段
						break;	//跳出循环
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//对每段视频所在网页进行分析，得到视频地址
			for (int j=0; j<=n; ++j)
			{
				try {
					Vector<URL> fileUrls = SiteSolver.getMatchedUrls(
							new URL(tUrl.toString().trim() + j),"","(?<=flvUrl\":\")\\S*?(?=\")","");
					for (int k=0; k<fileUrls.size(); ++k)	//将解析到的网址写入下载队列
						m_file_que.put(fileUrls.get(i));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}

		//将其他视频连接加入到page队列
		for (int i=0; i<otherFileUrls.size(); ++i)
			m_page_que.put(otherFileUrls.get(i));
		
		//返回真
		return true;
	}
}
