package com.project.task.controller;

import java.util.ArrayList;
import java.util.List;
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
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NoResultException;

@Controller
@RequestMapping("/books")
public class BookController {

	private final BookService bookService;
	private final SeriesService seriesService;
	private final AuthorService authorService;
	private final CharacterService characterService;
	
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

		int index = -1;
		boolean isMainCharacterNew = true;
		String authorName = theBook.getAuthor().getName();

		Author tempAuthor;
		Series series = new Series("Not in any series");
		Series tempSeriesObject = null;

		List<Integer> characterIdList1 = new ArrayList<>();
		List<Character> characterList = new ArrayList<>();

		SessionFactory factory = new Configuration()
				.configure()
				.addAnnotatedClass(Author.class)
				.addAnnotatedClass(Series.class)
				.addAnnotatedClass(Book.class)
				.addAnnotatedClass(Character.class)
				.buildSessionFactory();
		Session session = factory.getCurrentSession();
		session.beginTransaction();

		try {
			Query<Integer> query1 = session.createQuery("select author.id from Author author where author.name=:name");
			query1.setParameter("name", authorName);
			index = query1.getSingleResult();
		} catch (NoResultException e) {

		}

		try {
			Query<Integer> query2 = session.createQuery("select character.id from Character character where character.role=:role");
			query2.setParameter("role", "main");
			characterIdList1 = query2.getResultList();

			query2.setParameter("role", "secondary");
			List<Integer> characterIdList2 = query2.getResultList();

			characterIdList1.addAll(characterIdList2);
		} catch (NoResultException e) {

		}

		for (int m : characterIdList1) {
			Character character = characterService.findById(m);
			if (character != null && character.getName() != null && !character.getName().isEmpty() && character.getRole() != null && !character.getRole().isEmpty()) {
				characterList.add(character);
			}
		}

		if (index == -1) {
			tempAuthor = new Author(authorName);
		} else {
			tempAuthor = authorService.findById(index);
		}

		for (Character cha : characterList) {
			for (int t = 0; t < theBook.getCharacters().size(); t++) {
				if (cha.getName().equals(theBook.getCharacters().get(t).getName())) {
					tempSeriesObject = cha.getBooks().get(0).getSeries();
					isMainCharacterNew = false;
					break;
				}
			}
		}

		if (isMainCharacterNew) {
			for (int t = 0; t < theBook.getCharacters().size(); t++) {
				if (theBook.getCharacters().get(t).getRole().equals("main") || theBook.getCharacters().get(t).getRole().equals("secondary")) {
					String name = theBook.getCharacters().get(t).getName();
					if (name != null && !name.trim().isEmpty()) {
						series = new Series(name + " adventures");
						tempAuthor.addSeries(series);
						authorService.save(tempAuthor);
						series.setAuthor(tempAuthor);
						series.addBook(theBook);
						seriesService.save(series);
						break;
					}
				}
			}
		} else {
			series = tempSeriesObject;
		}

		theBook.setCharacters(theBook.getCharacters().stream()
				.filter(character -> character.getName() != null && !character.getName().trim().isEmpty())
				.collect(Collectors.toList()));

		tempAuthor.addBook(theBook);
		theBook.setAuthor(tempAuthor);
		theBook.setSeries(series);

		bookService.save(theBook);

		return "redirect:/books/list";
	}

	@GetMapping("/delete")
	public  String delete(@RequestParam("bookId") int theId){

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

		try{
			for (Character character : characterList){
				characterService.deleteById(character.getId());
			}
		}catch (EmptyResultDataAccessException e){

		}

		return "redirect:/books/list";
	}

	@Autowired
	private SessionFactory sessionFactory;

	public void deleteWithBot(int theId) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		try {
			Book theBook = session.get(Book.class, theId);

			Series theSeries = theBook.getSeries();
			Hibernate.initialize(theSeries.getBooks());

			Author theAuthor = theBook.getAuthor();

			List<Character> characterList = theBook.getCharacters();

			session.delete(theBook);

			if (theSeries.getBooks().isEmpty()) {
				session.delete(theSeries);
			}

			if (theAuthor.getBooks().isEmpty()) {
				session.delete(theAuthor);
			}

			for (Character character : characterList) {
				try {
					session.delete(character);
				} catch (EmptyResultDataAccessException e) {

				}
			}

			// Commit the transaction
			session.getTransaction().commit();
		} catch (Exception e) {
			// If an error occurs, roll back the transaction
			if (session.getTransaction() != null) {
				session.getTransaction().rollback();
			}
			throw e;  // Rethrow the exception after rolling back the transaction
		} finally {
			// Make sure to close the session regardless of success or failure
			session.close();
		}
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
}









