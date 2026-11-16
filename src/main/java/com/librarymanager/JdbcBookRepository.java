package com.librarymanager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcBookRepository implements BookRepository {

    @Override
    public Book save(Book book) {

        if (book.id() == null) {

            String sql = "INSERT INTO books (title, author, year, available) VALUES (?, ?, ?, ?)";

            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, book.title());
                ps.setString(2, book.author());
                ps.setInt(3, book.year());
                ps.setBoolean(4, book.available());

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {

                    if (rs.next()) {
                        return new Book(
                                rs.getLong(1),
                                book.title(),
                                book.author(),
                                book.year(),
                                book.available()
                        );
                    }
                }

                throw new SQLException("Book was not saved.");

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {

            String sql = "UPDATE books SET title = ?, author = ?, year = ?, available = ? WHERE id = ?";

            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, book.title());
                ps.setString(2, book.author());
                ps.setInt(3, book.year());
                ps.setBoolean(4, book.available());
                ps.setLong(5, book.id());

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException("Book with id " + book.id() + " was not found.");
                }

                return book;

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<Book> findAll() {

        List<Book> books = new ArrayList<>();

        String sql = "SELECT * FROM books ORDER BY id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Book book = new Book(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("year"),
                        rs.getBoolean("available")
                );

                books.add(book);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return books;
    }

    @Override
    public Optional<Book> findById(Long id) {

        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Book book = new Book(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("year"),
                            rs.getBoolean("available")
                    );

                    return Optional.of(book);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {

        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}