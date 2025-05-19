package dao;

import util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public static void main(String[] args) {
        // 대출 실행 테스트
        BorrowDAO borrowDAO = new BorrowDAO();
        try {
            borrowDAO.borrowBook(2,3);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
