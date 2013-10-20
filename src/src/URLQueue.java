package src;

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.HashSet;
import java.util.TimeZone;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.URL;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
�洢���У��洢URL���ڶ����������ڼ����ظ���
*/
public class URLQueue {
	protected Set<URL> m_url_rec;
	protected LinkedBlockingQueue<URL> m_url_que;
	protected RandomAccessFile m_log_file;
	
	public URLQueue()
	{
		m_url_rec = Collections.synchronizedSet(new HashSet<URL>());	//ͬ���Ĺ�ϣ��
		m_url_que = new LinkedBlockingQueue<URL>();	//URL����
	}
	
	public URLQueue(String logFile)
	{
		this();

		//������־�ļ�
		String logPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "log";
		logFile = logPath + System.getProperty("file.separator") + logFile;
		try {
			File commonFile = new File(logPath);
			if (!commonFile.isDirectory())	//������־�ļ���,isDirectory()���ҽ����˳���·������ʾ���ļ������� ��һ��Ŀ¼ʱ������ true�����򷵻� false 

				commonFile.mkdir();          //�����˳���·����ָ����Ŀ¼��
			commonFile = new File(logFile);
			if (commonFile.exists())	//ɾ��ԭ��־�ļ�
				commonFile.delete();
			commonFile.createNewFile();	//��������־�ļ�
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			m_log_file = new RandomAccessFile(logFile,"rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			m_log_file = null;
		}
	}
	
	public void put(URL url)
	{
		//�����¼���Ƿ��д�URL
		if (m_url_rec.add(url))		//note:ʹ��Hash��ѯURL�Ƿ��ظ�
		{
			try {
				m_url_que.put(url);	//�ڶ����м����URL
				if (m_log_file!=null)
				{
					m_log_file.writeBytes("" + m_url_rec.size() + "\t");
					GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
					m_log_file.writeBytes("" +
							calendar.get(Calendar.YEAR) + "-" +
							(calendar.get(Calendar.MONTH)+1) + "-" +
							calendar.get(Calendar.DAY_OF_MONTH) + " " + 
							calendar.get(Calendar.HOUR_OF_DAY) + ":" +
							calendar.get(Calendar.MINUTE) + ":" + 
							calendar.get(Calendar.SECOND) + "." +
							calendar.get(Calendar.MILLISECOND) + "\t");
					m_log_file.writeBytes(url.toString().trim()+"\n");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
	
	public URL take()
	{
		try
		{
			return m_url_que.take();
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	public URL peek()
	{
		return m_url_que.peek();
	}
	
	public int size()
	{
		return m_url_que.size();
	}
	
	public boolean isEmpty()
	{
		return m_url_que.isEmpty();
	}
}
