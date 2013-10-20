package src.sitesolver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Vector;
import src.URLQueue;


/**
����һ������������ҳURL��������а�����������������ҳ�������ļ�URL
*/
public class SinaSolver extends SiteSolver {
	
	public SinaSolver(URL url,URLQueue pageQueue,URLQueue fileQueue) {
		super(url, pageQueue, fileQueue);
	}
	
	public void analyze()
	{
		//�����Ƿ�Ϊ������URL
		if (!m_url.toString().toLowerCase().contains("sina"))
			return;
		
		//debug
		System.out.println("SinaSolver.analyze() working...");
		
		//����Ƿ�Ϊ�ļ�URL�����ǣ��������˳�
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
		
		
		//��ȡ��ҳ����
		m_content = super.getContent(m_url);
		
		//debug
		System.out.println("SinaSolver.analyze() end read content.");
		
		//��URLһ�ɷ���page����
		//������ҳ���ݣ���ȡ�ļ�URL������page����
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
		//������ҳ���ݣ���ȡ�ļ�URL������ͬ����URLִ�в�ͬ����
		Vector<URL> urls = super.getMatchedUrls(m_content,"",
				"(http://video.sina.com.cn/\\S*.shtml)|" +
				"(http://you.video.sina.com.cn/pg/topicdetail/topicPlay.php\\S*#\\S*(?=\"))|" +
				"(http://you.video.sina.com.cn/b/\\S*.html)","");
		for (int i=0; i<urls.size(); ++i)
		{
			URL tUrl = urls.get(i);
			if(super.checkUrl(tUrl,"http://video.sina.com.cn/\\S*.shtml"))
				m_page_que.put(tUrl);	//����page����
			else if (super.checkUrl(tUrl,"http://you.video.sina.com.cn/pg/topicdetail/topicPlay.php\\S*#\\S*(?=\")"))
				analyzeType2Url(tUrl);	//ֱ�ӷ���URL
			else if (super.checkUrl(tUrl,"http://you.video.sina.com.cn/b/\\S*.html"))
				analyzeType3Url(tUrl);	//ֱ�ӷ���URL
		}
	*/	
		//debug
		System.out.println("SinaSolver.analyze() end work, with URL a page URL.");
		System.out.println("two storages' sizes are " + m_page_que.size() + " and " + m_file_que.size());
	}
	
	/**
	��������"http://video.sina.com.cn/\\S*.shtml"��URL
	*/
	public boolean analyzeType1Url(URL type1Url)
	{
		//�����Ƿ�Ϊ��һ���ļ�URL
		if (!super.checkUrl(type1Url,"http://video.sina.com.cn/\\S*.shtml"))
			return false;	//���ǵ�һ���ļ�URL���������������ش���
		
		//debug
		System.out.println("SinaSolver.analyzeType1Url() working...");
		
		//��ȡ��ҳ����
		StringBuffer contentBuffer = super.getContent(type1Url);
		
		//����ƥ���Ӵ����õ�vid��uid
		Vector<String> vids = new Vector<String>();
		Vector<String> uids = new Vector<String>();
		Matcher vidMatcher = Pattern.compile("(?<=&vid=)\\w+",Pattern.CASE_INSENSITIVE).matcher(contentBuffer);
		Matcher uidMatcher = Pattern.compile("(?<=&uid=)\\w+",Pattern.CASE_INSENSITIVE).matcher(contentBuffer);
		while (vidMatcher.find() && uidMatcher.find())
		{
			vids.add(vidMatcher.group());
			uids.add(uidMatcher.group());
		}
		
		//���������ļ�URL���������
		for (int i=0; i<vids.size() && i<uids.size(); ++i)
		{
			Vector<URL> urls = getUrlWithIds(vids.get(i),uids.get(i));
			for (int j=0; j<urls.size(); ++j)
			{
				//debug
				System.out.println("SinaSover.analyzeType1Url(): adding url to m_file_que: " + urls.get(j).toString());
				
				m_file_que.put(urls.get(j));	//Pay attention! ��j����i
			}
		}
		
		//debug
		System.out.println("SinaSolver.analyzeType1Url() end work.");
		
		return true;
	}
	
	/**
	��������"http://you.video.sina.com.cn/pg/topicdetail/topicPlay.php\\S*#\\S*(?=\")"��URL
	*/
	public boolean analyzeType2Url(URL type2Url)
	{
		//�����Ƿ�Ϊ�ڶ����ļ�URL	
		if (!super.checkUrl(type2Url,"http://you.video.sina.com.cn/pg/topicdetail/topicPlay.php\\S*#\\S*"))	//Pay attention!�Ѿ��õ���URL�в�������"��
			return false;	//���ǵڶ����ļ�URL���������������ش���
			
		//debug
		System.out.println("SinaSolver.analyzeType2Url() working...");
		
		//����URL�����õ�vid��uid
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
		
		//���������ļ�URL���������
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
	��������"http://you.video.sina.com.cn/b/\\S*.html"��URL
	*/
	public boolean analyzeType3Url(URL type3Url)
	{
		//�����Ƿ�Ϊ�������ļ�URL
		if (!super.checkUrl(type3Url,"http://you.video.sina.com.cn/b/\\S*.html"))
			return false;	//���ǵ������ļ�URL���������������ش���
			
		//debug
		System.out.println("SinaSolver.analyzeType3Url() working...");
		
		//��ȡ��ҳ����
		StringBuffer contentBuffer = super.getContent(type3Url);
		
		//����ƥ���Ӵ����õ�vid��uid
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
		
		//���������ļ�URL���������
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
	���������vid��uid������ҳ���õ��ļ�URL
	*/
	public Vector<URL> getUrlWithIds(String vid,String uid)
	{
		//debug
		System.out.println("SinaSolver.getUrlWithIds() working...");
		

		//��ȡ��ҳ����
		StringBuffer contentBuffer = new StringBuffer();
		try {
			//tbc:�����Ƿ�Ҫ��%
			contentBuffer = super.getContent(
					new URL("http://v.iask.com/v_ask.php?auto=1&vid=%" + vid + "&uid=%" + uid));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//debug
		System.out.println("URL is: http://v.iask.com/v_ask.php?auto=1&vid=%" + vid + "&uid=%" + uid);
		
		//ƥ���Ӵ����޸Ĳ��ֱ�־���õ��ļ�URL
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
		
		//���ؽ��
		return urls;
	}
}
