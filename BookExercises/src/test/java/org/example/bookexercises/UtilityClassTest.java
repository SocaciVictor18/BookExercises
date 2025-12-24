package org.example.bookexercises;

import org.example.bookexercises.model.BookLoan;

import java.time.LocalDate;

public class UtilityClassTest {

    static BookLoan createLoan(String loanId, String memberId, String loanDate,
                               String bookTitle, String genre, String author,
                               int daysLoaned) {
        return new BookLoan(loanId, memberId, LocalDate.parse(loanDate),
                bookTitle, genre, author, daysLoaned);
    }
}
