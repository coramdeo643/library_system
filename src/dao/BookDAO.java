package dao;

import dto.Book;
import util.DatabaseUtil;

import java.awt.desktop.PreferencesEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 도서 관련 데이터베이스 작업을 처리하는 DAO class
 * Create, Read, Update, Delete method 만든다
 */
public class BookDAO {
    // 새 도서를 DB에  추가 기능 insert
    public void addBook(Book book) throws SQLException {
        String sql
                = "insert into books (title, author, publisher, publication_year, isbn) "
                + "values (?,?,?,?,?) ";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // body
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getPublisher());
            pstmt.setInt(4, book.getPublicationYear());
            pstmt.setString(5, book.getIsbn());
            pstmt.executeUpdate();
        }
    }

    // 모든 도서 목록을 조회 기능 select
    public List<Book> getAllBooks() throws SQLException {
        List<Book> bookList = new ArrayList<>();
        // bookList.add();
        String sql
                = "select * from books";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            // vody
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String publisher = rs.getString("publisher");
                int publicationYear = rs.getInt("publication_year");
                String isbn = rs.getString("isbn");
                boolean available = rs.getBoolean("available");

                Book book = new Book(id, title, author, publisher, publicationYear, isbn, available);
                bookList.add(book);
                // bookList.add(new Book(id, title, author, publisher, publicationYear, isbn, available));

            }
        }

        return bookList;
    }

    // 책 제목으로 도서 검색 기능 select
    public void searchTitle() {
    }

    // 수정 / 삭제 기능 update / delete
    public void updateBook() {
    }

    public void deleteBook() {
    }

    //test code
    public static void main(String[] args) {
        // all select test
        BookDAO bookDAO = new BookDAO();
        try {
            bookDAO.getAllBooks();
            for (int i = 0; i < bookDAO.getAllBooks().size(); i++) {
                System.out.println(bookDAO.getAllBooks().get(i));
            }
            bookDAO.addBook(new Book(0, "10", "10", "10", 2025, "10",true));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
} // end of class
