package com.project.tests;


import com.project.task.Entities.Author;
import com.project.task.Entities.Book;
import com.project.task.Entities.Series;
import com.project.task.dao.AuthorRepository;
import com.project.task.dao.BookRepository;
import com.project.task.dao.SeriesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private SeriesRepository seriesRepository;

    private Book book;
    private Author author;
    private Series series;

    @BeforeEach
    public void setUp() {
        author = new Author("J.K. Rowling");
        series = new Series("Harry Potter Series");
        authorRepository.save(author);
        seriesRepository.save(series);

        book = new Book();
        book.setName("Harry Potter and the Sorcerer's Stone");
        book.setRelease_year("1997");
        book.setPage_count("223");
        book.setDescription("A book about a young wizard.");
        book.setRating(5);
        book.setAuthor(author);
        book.setSeries(series);
    }

    @Test
    public void testSaveBook() {
        // Save book and verify it's saved
        bookRepository.save(book);
        assertNotNull(book.getId(), "Book should have an ID after saving.");
    }

    @Test
    public void testFindBookById() {
        // Save book
        bookRepository.save(book);

        // Retrieve book by ID
        Book retrievedBook = bookRepository.findById(book.getId()).orElse(null);
        assertNotNull(retrievedBook, "Book should be retrieved from the database.");
        assertEquals(book.getName(), retrievedBook.getName(), "Book name should match.");
    }

    @Test
    public void testDeleteBook() {
        // Save book
        bookRepository.save(book);

        // Delete book
        bookRepository.delete(book);

        // Verify the book is deleted
        Book deletedBook = bookRepository.findById(book.getId()).orElse(null);
        assertNull(deletedBook, "Book should be deleted.");
    }
}
