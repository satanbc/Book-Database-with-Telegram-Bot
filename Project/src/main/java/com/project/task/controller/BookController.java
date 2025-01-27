package com.project.task.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.project.task.Entities.Author;
import com.project.task.Entities.Book;
import com.project.task.Entities.Character;
import com.project.task.Entities.Series;
import com.project.task.service.AuthorService;
import com.project.task.service.BookService;
import com.project.task.service.CharacterService;
import com.project.task.service.SeriesService;
import com.project.task.sorting.Sorting;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
public class BookController {

	private final BookService bookService;
	private final SeriesService seriesService;
	private final AuthorService authorService;
	private final CharacterService characterService;

	SessionFactory factory = new Configuration()
			.configure()
			.addAnnotatedClass(Author.class)
			.addAnnotatedClass(Series.class)
			.addAnnotatedClass(Book.class)
			.addAnnotatedClass(Character.class)
			.buildSessionFactory();

	public BookController(BookService bookService, SeriesService seriesService, AuthorService authorService, CharacterService characterService) {
		this.bookService = bookService;
		this.seriesService = seriesService;
		this.authorService = authorService;
		this.characterService = characterService;
	}

	@GetMapping("/list")
	public String listBooks(Model theModel){

		List<Book> theBooks = bookService.findAll();

		theModel.addAttribute("books", theBooks);

		return "books/list-books";
	}

	@GetMapping("/showFormForAdd")
	public String showFormForAdd(Model theModel){

		Book theBook = new Book();

		for(int i = 1; i <= 3; i++){
			theBook.addCharacter(new Character());
		}

		theModel.addAttribute("book", theBook);

		return "books/book-form";
	}

	@GetMapping("/showFormForSort")
	public String showFormForSort(Model theModel){

		List<Book> bookList = bookService.findAll();

		theModel.addAttribute("books", bookList);

		return "books/book-sort";
	}

	@RequestMapping(path = {"/books/list","/search"})
	public String search(Model theModel, String keyword) {
		if(keyword!=null) {
			List<Book> books = bookService.getByKeyword(keyword);
			theModel.addAttribute("books", books);
		}else {
			List<Book> books = bookService.findAll();
			theModel.addAttribute("books", books);}
		return "books/search-books";
	}

	@Transactional
	@PostMapping("/save")
	public String saveBook(@ModelAttribute("book") Book theBook) {
		if (theBook == null || theBook.getAuthor() == null || theBook.getAuthor().getName() == null || theBook.getAuthor().getName().trim().isEmpty()) {
			throw new IllegalArgumentException("Invalid book or author details provided.");
		}

		String authorName = theBook.getAuthor().getName().trim();
		Author author;
		Series series;

		try (Session session = factory.openSession()) {
			session.beginTransaction();

			Query<Author> authorQuery = session.createQuery("from Author where name = :name", Author.class);
			authorQuery.setParameter("name", authorName);
			author = authorQuery.uniqueResult();

			if (author == null) {
				author = new Author(authorName);
				session.save(author);
			}

			List<Character> validCharacters = theBook.getCharacters().stream()
					.filter(character -> character.getName() != null && !character.getName().trim().isEmpty() && character.getRole() != null && !character.getRole().trim().isEmpty())
					.collect(Collectors.toList());

			theBook.setCharacters(validCharacters);

			Optional<Character> mainCharacter = validCharacters.stream()
					.filter(character -> character.getRole().equalsIgnoreCase("main") || character.getRole().equalsIgnoreCase("secondary"))
					.findFirst();

			if (mainCharacter.isPresent()) {
				String seriesName = mainCharacter.get().getName() + " adventures";
				Query<Series> seriesQuery = session.createQuery("from Series where name = :name", Series.class);
				seriesQuery.setParameter("name", seriesName);
				series = seriesQuery.uniqueResult();

				if (series == null) {
					series = new Series(seriesName);
					series.setAuthor(author);
					session.save(series);
				}
			} else {
				series = new Series("Not in any series");
				session.save(series);
			}

			theBook.setAuthor(author);
			theBook.setSeries(series);
			author.addBook(theBook);
			series.addBook(theBook);

			session.saveOrUpdate(theBook);

			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to save the book.", e);
		}

		return "redirect:/books/list";
	}

	@GetMapping("/delete")
	public String delete(@RequestParam("bookId") int theId){

		Book theBook = bookService.findById(theId);
		Series theSeries = seriesService.findById(theBook.getSeries().getId());
		Author theAuthor = authorService.findById(theBook.getAuthor().getId());
		List<Character> characterList = theBook.getCharacters();

		int seriesId = theBook.getSeries().getId();
		int authorId = theBook.getAuthor().getId();

		bookService.deleteById(theId);

		if (theSeries.getBooks().isEmpty())
			seriesService.deleteById(seriesId);
		if (theAuthor.getBooks().isEmpty())
			authorService.deleteById(authorId);

		try {
			for (Character character : characterList){
				characterService.deleteById(character.getId());
			}
		} catch (EmptyResultDataAccessException e){

		}

		resetAutoIncrement();

		return "redirect:/books/list";
	}

	public void deleteByBot(int theId) {
		Session session = factory.openSession();
		session.beginTransaction();
		try {
			Book theBook = session.get(Book.class, theId);
			Series theSeries = theBook.getSeries();
			Author theAuthor = theBook.getAuthor();
			List<Character> characterList = theBook.getCharacters();

			session.delete(theBook);

			if (theSeries.getBooks().size() <= 1) {
				session.delete(theSeries);
			}

			if (theAuthor.getBooks().size() <= 1) {
				session.delete(theAuthor);
			}

			for (Character character : characterList) {
				try {
					session.delete(character);
				} catch (EmptyResultDataAccessException e) {

				}
			}

			session.getTransaction().commit();
		} catch (Exception e) {
			if (session.getTransaction() != null) {
				session.getTransaction().rollback();
			}
			throw e;
		} finally {
			session.close();
		}

		resetAutoIncrement();
	}


	@GetMapping("/insertionSort")
	public String insertionSort(Model theModel){
		Book[] booksArray = bookService.findAll().toArray(new Book[0]);
		Sorting sort = new Sorting(booksArray);

		sort.insertionSort(booksArray);

		theModel.addAttribute("booksArray", booksArray);

		return "books/list-books-sorted";
	}
	@GetMapping("/quickSort")
	public String quickSort(Model theModel){
		Book[] booksArray = bookService.findAll().toArray(new Book[0]);
		Sorting sort = new Sorting(booksArray);

		try {
			sort.quickSort(booksArray, 0, booksArray.length-1);
		}catch (ArrayIndexOutOfBoundsException e){

		}

		theModel.addAttribute("booksArray", booksArray);

		return "books/list-books-sorted";
	}

	@GetMapping("/mergeSort")
	public String mergeSort(Model theModel){
		Book[] booksArray = bookService.findAll().toArray(new Book[0]);
		Sorting sort = new Sorting(booksArray);

		sort.mergeSort(booksArray, booksArray.length);

		theModel.addAttribute("booksArray", booksArray);

		return "books/list-books-sorted";
	}
	@GetMapping("/selectionSort")
	public String selectionSort(Model theModel){
		Book[] booksArray = bookService.findAll().toArray(new Book[0]);
		Sorting sort = new Sorting(booksArray);

		sort.selectionSort(booksArray);

		theModel.addAttribute("booksArray", booksArray);

		return "books/list-books-sorted";
	}
	@GetMapping("/shuttleSort")
	public String shuttleSort(Model theModel){
		Book[] booksArray = bookService.findAll().toArray(new Book[0]);
		Sorting sort = new Sorting(booksArray);

		sort.shuttleSort(booksArray);

		theModel.addAttribute("booksArray", booksArray);

		return "books/list-books-sorted";
	}
	@GetMapping("/shellSort")
	public String shellSort(Model theModel){
		Book[] booksArray = bookService.findAll().toArray(new Book[0]);
		Sorting sort = new Sorting(booksArray);

		sort.shellSort(booksArray);

		theModel.addAttribute("booksArray", booksArray);

		return "books/list-books-sorted";
	}

	private void resetAutoIncrement() {
		Session session = factory.openSession();
		session.beginTransaction();
		try {
			Integer lastBookId = (Integer) session.createNativeQuery("SELECT MAX(id) FROM book").getSingleResult();
			Integer lastSeriesId = (Integer) session.createNativeQuery("SELECT MAX(id) FROM series").getSingleResult();
			Integer lastCharacterId = (Integer) session.createNativeQuery("SELECT MAX(id) FROM `character`").getSingleResult();
			Integer lastAuthorId = (Integer) session.createNativeQuery("SELECT MAX(id) FROM author").getSingleResult();

			if (lastBookId != null) {
				session.createNativeQuery("ALTER TABLE book AUTO_INCREMENT = " + (lastBookId + 1)).executeUpdate();
			}
			if (lastSeriesId != null) {
				session.createNativeQuery("ALTER TABLE series AUTO_INCREMENT = " + (lastSeriesId + 1)).executeUpdate();
			}
			if (lastCharacterId != null) {
				session.createNativeQuery("ALTER TABLE `character` AUTO_INCREMENT = " + (lastCharacterId + 1)).executeUpdate();
			}
			if (lastAuthorId != null) {
				session.createNativeQuery("ALTER TABLE author AUTO_INCREMENT = " + (lastAuthorId + 1)).executeUpdate();
			}

			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			session.close();
		}
	}
}
