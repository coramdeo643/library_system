package dao;

import dto.Students;
import util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    // Add new student in the DB
    public void addStudent(Students student) throws SQLException {
        String sql =
                "insert into students (name, student_id) values (?,?) ";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getStudentId());
            pstmt.executeUpdate();
        }
    }

    // Get all student list
    public List<Students> getAllStudents() throws SQLException {
        List<Students> studentsList = new ArrayList<>();
        String sql =
                "select * from students ";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String studentId = rs.getString("student_id");
                studentsList.add(new Students(id, name, studentId));
            }

        }
        return studentsList;
    }

    // 학생 ID로 학생 인증(로그인) 기능 만들기
    public Students authenticateStudent (String studentId) throws SQLException {
        // 학생이 정확한 학번을 입력하면 Student 객체가 만들어져서 리턴됨
        // 학생이 잘못된 학번을 입력하면 null 값 반환
        // if code return new Student;
        String sql =
                "select * from students where student_id = ? ";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + studentId + "%");
            ResultSet rs = pstmt.executeQuery();
//            if () {
//                return new Students();
//            } else {
                return null;
//            }
        }
    }

    public static void main(String[] args) {
        StudentDAO studentDAO = new StudentDAO();
        try {
            studentDAO.addStudent(new Students(0, "Lee", "20250001"));
            studentDAO.getAllStudents();
            for (int i = 0; i < studentDAO.getAllStudents().size(); i++) {
                System.out.println(studentDAO.getAllStudents().get(i));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
