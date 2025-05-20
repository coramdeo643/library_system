package dao;

import dto.Borrows;
import util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {
    // 도서 대출 처리
    public void borrowBook(int bookId, int studentPk) throws SQLException {
//        select available from books where id = 1;
//        insert into borrows (student_id, book_id, borrow_date) values(3, 1, current_date);
//        update books set available = 0 where id = 1;
        String checkSql =
                "select available from books where id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
            checkPstmt.setInt(1, bookId);
            ResultSet rs1 = checkPstmt.executeQuery();
            if (rs1.next() && rs1.getBoolean("available")) {
                // insert + update
                String insertSql = "insert into borrows (student_id, book_id, borrow_date) " +
                        "values(?, ?, current_date) ";
                String updateSql = "update books set available = 0 where id = ? ";
                try (PreparedStatement borrowStmt = conn.prepareStatement(insertSql);
                     PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    borrowStmt.setInt(1, studentPk);
                    borrowStmt.setInt(2, bookId);
                    System.out.println("------------------------");
                    updateStmt.setInt(1, bookId);
                    borrowStmt.executeUpdate();
                    updateStmt.executeUpdate();
                }
            } else {
                throw new SQLException("도서가 대출 불가능 합니다");
            }
        }
    }

    // 현재 대출중인 도서 목록 조회
    public List<Borrows> getBorrowedBooks() throws SQLException {
        List<Borrows> borrowsList = new ArrayList<>();
        String sql = "select * from borrows where return_date is null ";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Borrows borrowsDTO = new Borrows();
                borrowsDTO.setId(rs.getInt("id"));
                borrowsDTO.setBookId(rs.getInt("book_id"));
                borrowsDTO.setStudentId(rs.getInt("student_id"));
                // JAVA DTO 에서 DT = LocalDate,
                // But JDBC API 에서 LocalDate 아직 지원하지않음,
                // JDBC API 에서 제공하는 날짜 DT 는 Date뿐
                borrowsDTO.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
                borrowsList.add(borrowsDTO);
            }
        }
        return borrowsList; // Don't forget! no [return null] for this case!
    }

    // 도서 반납 처리 기능 추가
    // 1. borrows 테이블에 책 정보 조회 select(복합조건)
    // 2. borrows 테이블에 return_date update
    // 3. books 테이블 available 0 > 1 update
    public void returnBook(int bookId, int studentPK) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            // transaction start
            conn.setAutoCommit(false);
            // 이 쿼리의 결과집합에서 필요한 것은 borrows의 pk(id)값
            int borrowId = 0;
            String checkSQL =
                    "select id from borrows where book_id = ? " +
                            "and student_id = ? and return_date is null ";
            try(PreparedStatement checkPstmt = conn.prepareStatement(checkSQL)) {
                checkPstmt.setInt(1, bookId);
                checkPstmt.setInt(2, studentPK);
                ResultSet rs = checkPstmt.executeQuery();
                if (!rs.next()) {
                    throw new SQLException("해당 대출 기록이 존재하지않거나 이미 반납되었습니다.");
                }
                borrowId = rs.getInt("id");
            }
            String updateBorrowSQL = "update borrows set return_date = current_date where id = ?";
            String updateBookSQL = "update books set available = true where id = ?";
            try (PreparedStatement borrowPstmt = conn.prepareStatement(updateBorrowSQL);
                 PreparedStatement bookPstmt = conn.prepareStatement(updateBookSQL)) {
                // borrows 설정
                borrowPstmt.setInt(1, borrowId);
                borrowPstmt.executeUpdate();

                bookPstmt.setInt(1, bookId);
                bookPstmt.executeUpdate();
            }
            conn.commit(); // transaction completed
            // 1. (book id) 반납하려는 특정 책을 찾아야함
            // 2. (student id) 책을 대출한 학생을 찾기 위함 / 다른 학생이 같은 책을 빌린 히스토리와 겹치지 않게
            // 3. (return date) 아직 반납되지 않은 대출 기록만 찾아야함 = 대출중이라는 뜻
            // 3.1. 반납되었던 예전 같은책 같은 학생의 대출기록은 조회할 필요없음.
        } catch (SQLException e) {
            if(conn != null) {
                conn.rollback(); // 오류 발생시 rollback
            }
            System.err.println("Rollback completed");
        } finally {
            if(conn != null) {
                conn.setAutoCommit(true); // 다시 auto commit setting
                conn.close(); // resource release
            }
        }
//        // studentPK --> borrows student_id
//        // student pk
//        //select * from borrows where student_id = 3 and book_id = 1 and return_date is null;
//        //update borrows set return_date = curdate() where student_id = 3 and book_id = 1;
//        //update books set available = 1 where available = 0 and id = 1;
//        String checkSql =
//                "select * from borrows where student_id = ? and book_id = ? and return_date is null ";
//        try (Connection conn = DatabaseUtil.getConnection();
//             PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
//            checkPstmt.setInt(1, studentPK);
//            checkPstmt.setInt(2, bookId);
//            ResultSet rs = checkPstmt.executeQuery();
//            if (rs.next()) {
//                String updateSql =
//                        "update borrows set return_date = curdate() where student_id = ? and book_id = ? ";
//                String updateSql2 =
//                        "update books set available = 1 where available = 0 and id = ?;";
//                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql);
//                     PreparedStatement updateStmt2 = conn.prepareStatement(updateSql2)) {
//                    updateStmt.setInt(1, studentPK);
//                    updateStmt.setInt(2, bookId);
//                    updateStmt2.setInt(1, bookId);
//                    updateStmt.executeUpdate();
//                    updateStmt2.executeUpdate();
//                }
//            } else {
//                throw new SQLException("도서 반납 불가");
//            }
//        }
//    }
//
    public static void main(String[] args) {
        // 대출 실행 테스트
        BorrowDAO borrowDAO = new BorrowDAO();
        try {
//            borrowDAO.returnBook(2,3);
            borrowDAO.getBorrowedBooks();
            for (int i = 0; i < borrowDAO.getBorrowedBooks().size(); i++) {
                System.out.println(borrowDAO.getBorrowedBooks().get(i));
            }
//            borrowDAO.borrowBook(2,3);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
