package com.lab1.labs.service;

import com.lab1.labs.Entities.Author;
import com.lab1.labs.dao.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

	private AuthorRepository authorRepository;

	@Autowired
	public AuthorServiceImpl(AuthorRepository theAuthorRepository) {
		authorRepository = theAuthorRepository;
	}
	
	@Override
	public List<Author> findAll() {
		return authorRepository.findAll();
	}

	@Override
	public Author findById(int theId) {
		Optional<Author> result = authorRepository.findById(theId);

		Author theAuthor = null;

		if (result.isPresent()) {
			theAuthor = result.get();
		}
		else {
			throw new RuntimeException("Did not find author id - " + theId);
		}

		return theAuthor;
	}

	@Override
	public void save(Author theAuthor) {
		authorRepository.save(theAuthor);
	}

	@Override
	public void deleteById(int theId) {
		authorRepository.deleteById(theId);
	}

}






