package com.librarymanager;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public Book getById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new BookNotFoundException("Book with id " + id + " not found."));
    }

    public Book update(Long id, Book book) {

        getById(id);

        Book updatedBook = new Book(
                id,
                book.title(),
                book.author(),
                book.year(),
                book.available()
        );

        return repository.save(updatedBook);
    }

    public void delete(Long id) {

        getById(id);
        repository.deleteById(id);
    }

    public List<Book> searchByAuthor(String author) {

        return repository.findAll().stream()
                .filter(book -> book.author().toLowerCase()
                        .contains(author.toLowerCase()))
                .toList();
    }

    public List<Book> getAvailableSortedByYear() {

        return repository.findAll().stream()
                .filter(Book::available)
                .sorted((b1, b2) ->
                        Integer.compare(b2.year(), b1.year()))
                .toList();
    }

    public Map<String, Long> countBooksByAuthor() {

        return repository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Book::author,
                        Collectors.counting()
                ));
    }
}