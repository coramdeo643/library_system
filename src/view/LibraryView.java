package view;

import dto.Students;
import service.LibraryService;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * 사용자 인터페이스를 처리하는 뷰 클래스
 */
// public class LibraryView extends JFrame {
public class LibraryView {

    private final LibraryService service = new LibraryService();
    private final Scanner scanner = new Scanner(System.in);

    private Integer currentStudentId = null;
    private String currentStudentName = null;

    // principal = 접근 주체
//    private Students principalUser = null;
    public void start() {

        while (true) {
            System.out.println("- - - - 도서관리 시스템 - - - -");

            if (currentStudentId == null) {
                System.out.println("현재 로그아웃 상태입니다");
            } else {
                System.out.println("현재 로그인 유저 : " + currentStudentName);
            }

            System.out.println("| 1. 도서 추가 | 2. 도서 목록 | 3. 도서 검색 |");
            System.out.println("| 4. 학생 등록 | 5. 학생 목록 | 6. 도서 대출 |");
            System.out.println("| 7. 대출도서 목록 | 8. 도서 반납 | 9. Login | ");
            System.out.println("| 10. Logout | 11. 종료                      | ");
            System.out.print("선택 : ");
            int choice;

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // 버퍼 비우기
            } catch (Exception e) {
                System.out.println("정수값 입력하세요 1 ~ 11");
                scanner.nextLine();
                continue;
            }
            try {
                switch (choice) {
                    case 1:
                        System.out.println("도서 추가");
                        break;
                    case 2:
                        System.out.println("도서 목록");
                        break;
                    case 3:
                        System.out.println("도서 검색");
                        break;
                    case 4:
                        System.out.println("학생 추가");
                        break;
                    case 5:
                        System.out.println("학생 목록");
                        break;
                    case 6:
                        borrowBook();
                        break;
                    case 7:
                        System.out.println("대출 중 도서 목록");
                        break;
                    case 8:
                        System.out.println("도서 반납");
                        break;
                    case 9:
                        login();
                        break;
                    case 10:
                        logout();
                        break;
                    case 11:
                        System.out.println("시스템 종료");
                        scanner.close();
                        return;
                    default:
                        System.out.println("잘못된 입력입니다(1~11 중에서 입력하세요)");
                }
            } catch (SQLException e) {
                System.out.println("오류 : " + e.getMessage());
            } catch (Exception e) {
                System.out.println("전체 오류 : " + e.getMessage());
            }


        } // while
    } // end of start

    // Login func.
    private void login() throws SQLException {
        if (currentStudentId != null) {
            System.out.println("이미 로그인된 상태");
            return;
        } // defensive code
        System.out.print("학번 : ");
        String studentId = scanner.nextLine().trim();
        if (studentId.isEmpty()) {
            System.out.println("학번을 입력해주세요");
        }
        // 1. 학번 입력 받고나서 실제 존재하는 학번인지 목록 조회 하고 확인
        // 1.1. DB 접근 > 해당 학번(비번)맞는지 조회
        // 메서드 결과 2가지, 객체 존재 or null
        Students studentsDTO = service.authenticateStudent(studentId);
        // TODO try catch 해서 잘못된 000 입니다 예외처리 해주기
        // view > service > DAO > service > view

        if (studentsDTO == null) {
            System.out.println("존재하지 않는 학번입니다");
        } else {
            currentStudentId = studentsDTO.getId();
            currentStudentName = studentsDTO.getName();
            System.out.println("로그인 성공 : " + currentStudentName);
        }
    }

    // Logout func.
    private void logout() throws SQLException {
        if (currentStudentId == null) {
            System.out.println("이미 로그아웃했습니다");
        } else {
            currentStudentId = null;
            currentStudentName = null;
            System.out.println("로그아웃 완료");
        }

    }

    // borrow Book method.
    private void borrowBook() throws SQLException {
        // 기능 만드는 사고 연습!! 무슨 기능 / 정보가 있어야할까?
        // 로그인이 되어야 student id 값을 넘겨줄수있음
        if (currentStudentId == null) {
            System.out.println("Please login first");
            return;
        }
        System.out.print("도서 ID : ");
        int bookId;
        try {
            bookId = scanner.nextInt();
            if (bookId <= 0) {
                System.out.println("유효한 도서 ID를 입력해주세요");
                return;
            }
        } catch (Exception e) {
            System.out.println("유효한 도서 ID를 입력해주세요");
            return;
        }
        scanner.nextLine(); // buffer clean
        service.borrowBook(bookId, currentStudentId);
        System.out.println("대출 완료!");
    }


    // main
    public static void main(String[] args) {
        LibraryView libraryView = new LibraryView();
        libraryView.start();

    }
} // end of class
