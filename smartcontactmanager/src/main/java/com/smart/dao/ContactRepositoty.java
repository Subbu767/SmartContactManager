package com.smart.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;

public interface ContactRepositoty extends JpaRepository<Contact, Integer> {
	
	//pagination....
	
	//curent page
	//no of contacts per page-5 details contain pagelble ke pas
	
	@Query("from Contact as c where c.user.id= :uId")
	public Page<Contact> getAllContactsByUser(@Param("uId") int uid,Pageable pageable);
	
	

}
