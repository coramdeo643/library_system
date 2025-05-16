package dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Borrows {
    private int id;
    private int studentId;
    private int bookId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
}
