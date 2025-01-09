package com.project.tests;

import com.project.task.Entities.Author;
import com.project.task.Entities.Book;
import com.project.task.Entities.Character;
import com.project.task.Entities.Series;
import com.project.task.Application;
import com.project.task.dao.AuthorRepository;
import com.project.task.dao.BookRepository;
import com.project.task.dao.SeriesRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class)
public class BookRepositoryTests {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private SeriesRepository seriesRepository;

    SessionFactory factory = new Configuration()
            .configure()
            .addAnnotatedClass(Author.class)
            .addAnnotatedClass(Series.class)
            .addAnnotatedClass(Book.class)
            .addAnnotatedClass(Character.class)
            .buildSessionFactory();

    @Test
    public void testSaveAndDeleteBook() {
        Author author = new Author();
        author.setName("Test Author");
        author = authorRepository.save(author);

        Series series = new Series();
        series.setName("Test Series");
        series.setAuthor(author);
        series = seriesRepository.save(series);

        Book book = new Book();
        book.setName("Test Book");
        book.setRelease_year("2023");
        book.setPage_count("300");
        book.setDescription("A test book description");
        book.setRating(5);
        book.setAuthor(author);
        book.setSeries(series);

        Book savedBook = bookRepository.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getName()).isEqualTo("Test Book");
        assertThat(savedBook.getAuthor().getName()).isEqualTo("Test Author");
        assertThat(savedBook.getSeries().getName()).isEqualTo("Test Series");

        bookRepository.deleteById(book.getId());
        authorRepository.deleteById(book.getAuthor().getId());
        boolean bookExists = bookRepository.findById(book.getId()).isPresent();
        boolean authorExists = authorRepository.findById(book.getAuthor().getId()).isPresent();

        assertThat(bookExists).isFalse();
        assertThat(authorExists).isFalse();

        resetAutoIncrement();
    }

    @Test
    public void testFindBookById() {
        Author author = new Author();
        author.setName("Test Author");
        author = authorRepository.save(author);

        Series series = new Series();
        series.setName("Test Series");
        series.setAuthor(author);
        series = seriesRepository.save(series);

        Book book = new Book();
        book.setName("Test Book");
        book.setRelease_year("2023");
        book.setPage_count("300");
        book.setDescription("A test book description");
        book.setRating(5);
        book.setAuthor(author);
        book.setSeries(series);

        bookRepository.save(book);

        Book foundBook = bookRepository.findById(book.getId()).orElse(null);

        assertThat(foundBook).isNotNull();
        assertThat(foundBook.getName()).isEqualTo("Test Book");

        bookRepository.deleteById(book.getId());
        authorRepository.deleteById(book.getAuthor().getId());
        boolean bookExists = bookRepository.findById(book.getId()).isPresent();
        boolean authorExists = authorRepository.findById(book.getAuthor().getId()).isPresent();

        assertThat(bookExists).isFalse();
        assertThat(authorExists).isFalse();

        resetAutoIncrement();
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
