package dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import bean.ImageInfo;

@Component
public class ImageInfoDao {
	@Autowired 
	SessionFactory sessionFactory;
	
	private static int MAX_RESULT_COUNT=3;
	
	public List<ImageInfo> getImageInfo(String url)
	{
		Session session=sessionFactory.openSession();
		Transaction transaction=session.beginTransaction();
		
		String query_urlString="from ImageInfo where url=?";
		
		List<ImageInfo> result=(List<ImageInfo>) session.createQuery(query_urlString)
				.setString(0, url)
				.list();
		
		transaction.commit();
		
		session.close();
		return result;
	}
	@Cacheable(cacheNames="conditions",keyGenerator="keyGenerator")
	public List<ImageInfo> getPresentImageInfos(int type)
	{
		Session session=sessionFactory.openSession();
		Transaction transaction=session.beginTransaction();
		
		String query_urlString="from ImageInfo";
		
		List<ImageInfo> result=(List<ImageInfo>) session.createQuery(query_urlString)
				.setMaxResults(MAX_RESULT_COUNT)
				.list();
		
		transaction.commit();
		
		session.close();
		return result;
	}
	
	public void setImageInfo(ImageInfo info)
	{
		Session session=sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		session.update(info);
		transaction.commit();
		session.close();
	}
}
