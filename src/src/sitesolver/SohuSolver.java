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
����һ���Ѻ�������ҳURL��������а����������ļ�URL
*/
public class SohuSolver extends SiteSolver {
	
	public SohuSolver(URL url,URLQueue pageQueue,URLQueue fileQueue) {
		super(url, pageQueue, fileQueue);
	}
	
	public void analyze()
	{
		//�����Ƿ�Ϊ�Ѻ���URL
		if (!m_url.toString().toLowerCase().contains("sohu"))
			return;
		
		//debug
		System.out.println("SohuSolver start working...");
		
		
		//�������URLΪ�ļ�URL�����ٷ�������ҳ�е�ҳ��URL
		if (analyzeFileUrl(m_url))
		{
			//debug
			System.out.println("SohuSolver end work, with input URL a file URL.");
			System.out.println("two storages' sizes are " + m_page_que.size() + " and " + m_file_que.size());
			
			return;
		}
		
		//��������ҳ�в�����Ƶ��ҳ���ӣ�����page����
		Vector<URL> pageUrls = super.getMatchedUrls(m_url,
				"","http://v.blog.sohu.com/u/vw/\\w+","");
		for (int i=0; i<pageUrls.size(); ++i)
			m_page_que.put(pageUrls.get(i));
		
		//debug
		System.out.println("SohuSolver end work, with input URL a page URL.");
		System.out.println("two storages' sizes are " + m_page_que.size() + " and " + m_file_que.size());
	}
	
	/**
	�������ӵ��ļ���URL��������ҳ�������������ļ�URL���뵽m_file_que��ȥ��������
	���URL����ָ���ļ������ؼ�
	*/
	public boolean analyzeFileUrl(URL fileUrl)
	{
		//�����Ƿ�Ϊ�ļ�URL
		if (!checkUrl(m_url,"http://v.blog.sohu.com/u/vw/\\w+"))
			return false;	//�����ļ�URL���������������ش���
		
		//��ȡ��ҳ����
		StringBuffer contentBuffer = super.getContent(fileUrl);
		
		//��ȡ��ҳ���������������ʽ��URL
		Vector<URL> urls = SiteSolver.getMatchedUrls(contentBuffer,
				"http://v.blog.sohu.com/videinfo.jhtml?m=view&id=",
				"(?<=var _videoId = )\\w*(?=;)","&outType=3&from=1&block=");
		
		//������ҳ��������Ƶ����
		Vector<URL> otherFileUrls = super.getMatchedUrls(contentBuffer,
				"","http://v.blog.sohu.com/u/vw/\\w+","");
		
		//����ƥ�䵽����ҳ
		for (int i=0; i<urls.size(); ++i)
		{
			//ȡһ��URL
			URL tUrl = urls.get(i);
			
			//������ָ�����ҳ���õ���Ƶ���ָ�Ķ���
			int n=0;	//��Ƶ���ָ�Ķ���
			try {
				URL url0 = new URL(tUrl.toString().trim()+"0");
				BufferedReader contentReader = 
					new BufferedReader(new InputStreamReader(url0.openStream()));
				String line;
				Pattern filePattern = Pattern.compile("(?<=flvUrl\":\")\\S*?(?=\")",Pattern.CASE_INSENSITIVE);	//flv��ַģʽ
				Pattern lengthPattern = Pattern.compile("(?<=videoLength\":)\\S*?(?=,)",Pattern.CASE_INSENSITIVE);	//flv����ģʽ
				//Pattern lengthPattern = Pattern.compile("(?<=videoSize\":)\\S*?(?=,)",Pattern.CASE_INSENSITIVE);	//flv����ģʽ
				while ((line = contentReader.readLine())!=null)
				{
					Matcher fileMatcher = filePattern.matcher(line);
					Matcher lengthMatcher = lengthPattern.matcher(line);
					if (fileMatcher.find() && lengthMatcher.find())
					{
						int length = Integer.parseInt(lengthMatcher.group());	//�����Ƶ����
						n = length/420;		//��Ƶ���ָ��7����һ��//tbc:�᲻������һ��
						break;	//����ѭ��
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//��ÿ����Ƶ������ҳ���з������õ���Ƶ��ַ
			for (int j=0; j<=n; ++j)
			{
				try {
					Vector<URL> fileUrls = SiteSolver.getMatchedUrls(
							new URL(tUrl.toString().trim() + j),"","(?<=flvUrl\":\")\\S*?(?=\")","");
					for (int k=0; k<fileUrls.size(); ++k)	//������������ַд�����ض���
						m_file_que.put(fileUrls.get(i));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}

		//��������Ƶ���Ӽ��뵽page����
		for (int i=0; i<otherFileUrls.size(); ++i)
			m_page_que.put(otherFileUrls.get(i));
		
		//������
		return true;
	}
}
