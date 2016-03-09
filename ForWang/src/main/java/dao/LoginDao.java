package dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginDao {
	
	@Autowired
	SessionFactory sessionFactory;
	
	
	public int login(String username,String password)
	{
		Session session=sessionFactory.openSession();
		Transaction transaction=session.beginTransaction();
		@SuppressWarnings("unchecked")
		List<Integer> result=session.createSQLQuery("select u.group from users_table u where username=? and password=?").
								setString(0,username).
								setString(1,password).list();
		
		if(result.size()>0)
		{
			return result.get(0);
		}
		transaction.commit();
		session.close();
		return -1;
	}
}
