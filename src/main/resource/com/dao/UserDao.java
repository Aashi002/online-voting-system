package com.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import com.example.model.User;

public class UserDao {
		
		public int insert(User u) {
			 int i=0;
			try {
				Session s=Dao.getSessionFactory().openSession();
				Transaction txt=s.beginTransaction();
				s.persist(u);
				txt.commit();
				s.close();
				i=1;
				
			   }catch (Exception e) {
				System.out.println("Error occurred during user registration:" + e.getMessage());
				i =-1;
			}
			return i;	
		}	
	}
	
