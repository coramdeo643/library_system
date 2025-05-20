package service;

import dao.BookDAO;
import dao.BorrowDAO;
import dao.StudentDAO;
import dto.Book;
import dto.Borrows;
import dto.Students;

import java.sql.SQLException;
import java.util.List;

/**
 * 비즈니스 로직을 처리하는 서비스 클래스
 */
public class LibraryService {
    private final BookDAO bookDAO = new BookDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final BorrowDAO borrowDAO = new BorrowDAO();

    // 책을 추가하는 서비스
    public void addBook(Book book) throws SQLException{
        // 입력값 유효성 검사
        if (book.getTitle() == null || book.getAuthor() == null
                || book.getTitle().trim().isEmpty() || book.getAuthor().trim().isEmpty()) {
            throw new SQLException("도서 제목과 저자는 필수 입력 항목입니다");
        } // 유효성 검사 통과 후 BookDAO 에게 작업 협력 요청
        bookDAO.addBook(book);
    }

    // 책을 전체조회 하는 서비스
    public List<Book> getAllBooks() throws SQLException{
        return bookDAO.getAllBooks();
    }

    // 책 이름으로 조회하는 서비스
    public List<Book> searchBooksByTitle(String title) throws SQLException {
        // 입력값 유효성 검사
        if (title == null || title.trim().isEmpty()) {
            throw new SQLException("검색 제목을 입력해주세요");
        }
        return bookDAO.searchBooksTitle(title);
    }

    //학생을 추가하는 서비스
    public void addStudent(Students students) throws SQLException{
        // 유효성 검사
        if (students.getName() == null || students.getName().trim().isEmpty()
                || students.getStudentId() == null || students.getStudentId().trim().isEmpty()) {
            throw new SQLException("학생 이름과 학생 ID는 필수 입력 항목입니다");
        }
        studentDAO.addStudent(students);
    }

    // 전체 학생 목록 조회
    public List<Students> getAllStudents() throws SQLException {
        return studentDAO.getAllStudents();
    }

    // 도서 대출 서비스
    public void borrowBook(int bookId, int studentId) throws SQLException {
        // validation
        if(bookId <=0 || studentId <=0) {
            throw new SQLException("유효한 도서 ID와 학생 ID를 입력해주세요");
        } // borrowDAO 협력요청 borrows 테이블에 insert 책임은 borrowDAO 객체에게 있음
        borrowDAO.borrowBook(bookId,studentId);
    }

    // 현재 대출중인 도서목록 조회 서비스
    public List<Borrows> getBorrowedBooks() throws SQLException {
        return borrowDAO.getBorrowedBooks();
    }

    // 도서 반납처리 서비스
    public void returnBook(int bookId, int studentId) throws SQLException {
        // validation
        if (bookId <=0 || studentId<=0) {
            throw new SQLException("유효한 도서 ID와 학생 ID를 입력해주세요");
        }
        borrowDAO.returnBook(bookId, studentId);
    }

    // 인증 Authentication = student ID 로 인증 (+pwd 있다면 둘다 있어야 인증되게...)
    public Students authenticateStudent(String studentId) throws SQLException {
        // validation
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new SQLException("유효한 학생 ID를 입력해주세요");
        }
        return studentDAO.authenticateStudent(studentId);
    }
}
