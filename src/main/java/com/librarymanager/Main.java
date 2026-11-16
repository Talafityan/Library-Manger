package com.librarymanager;

public class Main {

    public static void main(String[] args) {

        BookRepository repository = new JdbcBookRepository();
        BookService service = new BookService(repository);

        System.out.println("All Books:");
        repository.findAll().forEach(System.out::println);

        System.out.println("\nAdding a new book:");

        Book newBook = new Book(
                null,
                "Java Basics",
                "John Smith",
                2022,
                true
        );

        Book savedBook = repository.save(newBook);
        System.out.println(savedBook);

        System.out.println("\nGet Book By ID:");
        System.out.println(service.getById(savedBook.id()));

        System.out.println("\nUpdate Book:");

        Book updatedBook = new Book(
                savedBook.id(),
                "Java Basics Updated",
                "John Smith",
                2023,
                true
        );

        System.out.println(service.update(savedBook.id(), updatedBook));

        System.out.println("\nSearch By Author:");

        service.searchByAuthor("Robert Martin")
                .forEach(System.out::println);

        System.out.println("\nAvailable Books Sorted By Year:");

        var availableBooks = service.getAvailableSortedByYear();
        availableBooks.forEach(System.out::println);

        System.out.println("\nCount Books By Author:");
        System.out.println(service.countBooksByAuthor());

        System.out.println("\nDelete Book:");
        service.delete(savedBook.id());

        System.out.println("\nTesting Book Not Found:");

        try {
            service.getById(999L);
        } catch (BookNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}