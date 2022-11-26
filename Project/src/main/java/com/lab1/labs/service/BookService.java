package com.lab1.labs.service;

import java.util.List;

import com.lab1.labs.Entities.Book;

public interface BookService {

	public List<Book> findAll();
	
	public Book findById(int theId);
	
	public void save(Book theBook);
	
	public void deleteById(int theId);

	public List<Book> getByKeyword(String keyword);
}
