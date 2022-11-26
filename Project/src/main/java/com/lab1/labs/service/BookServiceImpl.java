package com.lab1.labs.service;

import java.util.List;
import java.util.Optional;

import com.lab1.labs.Entities.Book;
import com.lab1.labs.dao.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

	private BookRepository bookRepository;
	
	@Autowired
	public BookServiceImpl(BookRepository theBookRepository) {
		bookRepository = theBookRepository;
	}
	
	@Override
	public List<Book> findAll() {
		return bookRepository.findAll();
	}

	@Override
	public Book findById(int theId) {
		Optional<Book> result = bookRepository.findById(theId);

		Book theBook = null;

		if (result.isPresent()) {
			theBook = result.get();
		}
		else {
			throw new RuntimeException("Did not find book id - " + theId);
		}

		return theBook;
	}

	@Override
	public void save(Book theBook) {
		bookRepository.save(theBook);
	}

	@Override
	public void deleteById(int theId) {
		bookRepository.deleteById(theId);
	}

	@Override
	public List<Book> getByKeyword(String keyword){
		return bookRepository.findByKeyword(keyword);
	}
}





