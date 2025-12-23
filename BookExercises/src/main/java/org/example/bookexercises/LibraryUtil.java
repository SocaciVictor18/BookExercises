package org.example.bookexercises;

import org.example.bookexercises.model.BookLoan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


/*
 * Implement the methods below so that the requirements are met.
 */
public class LibraryUtil {

    // private constructor to prevent instantiation
    private LibraryUtil() {
    }

    // load resource file from resources folder
    static List<String> loadResourceFile(final String fileName) {
        // Write your code here and replace the return statement
        List<String> lines = new ArrayList<>();
        try(BufferedReader input = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = input.readLine()) != null) {
                lines.add(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return lines;
    }

    // retrieve loans from csv lines
    // return a map of "valid" and "malformed" lines as keys and list of BookLoan objects as values
    protected static Map<String, List<BookLoan>> parseCsvLines(final List<String> file) {
        // Write your code here and replace the return statement
        Map<String, List<BookLoan>> result = file.stream()
                .filter(line -> line != null && !line.isBlank())
                .map(line -> {
                    String[] parts = line.split(",");

                    if (parts.length != 7) {
                        return new AbstractMap.SimpleEntry<String, BookLoan>("malformed", null);
                    }
                    String loanId = parts[0].trim();
                    String memberId = parts[1].trim();
                    String loanDateStr = parts[2].trim();
                    String bookTitle = parts[3].trim();
                    String genre = parts[4].trim();
                    String author = parts[5].trim();
                    String daysLoanedStr = parts[6].trim();
                    try {
                        LocalDate loanDate = LocalDate.parse(loanDateStr);
                        int daysLoaned = Integer.parseInt(daysLoanedStr);

                        if (loanId.isEmpty() || memberId.isEmpty()
                                || bookTitle.isEmpty() || genre.isEmpty()
                                || author.isEmpty()) {
                            return new AbstractMap.SimpleEntry<String, BookLoan>("malformed", null);
                        }

                        BookLoan loan = new BookLoan(
                                loanId, memberId, loanDate, bookTitle, genre, author, daysLoaned
                        );
                        return new AbstractMap.SimpleEntry<String, BookLoan>("valid", loan);
                    } catch (Exception e) {
                        return new AbstractMap.SimpleEntry<String, BookLoan>("malformed", null);
                    }
                })
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(
                                Map.Entry::getValue,
                                Collectors.toList())));

        result.putIfAbsent("valid", new ArrayList<>());
        result.putIfAbsent("malformed", new ArrayList<>());

        return result;
    }

    // count loans per genre
    // sorted alphabetically by genre
    protected static Map<String, Long> loansByGenre(final List<BookLoan> loans) {
        // Write your code here and replace the return statement

        return Collections.emptyMap();
    }

    // get top "n" authors by number of loans
    protected static List<String> topAuthorsByLoans(final List<BookLoan> loans, final int n) {
        // Write your code here and replace the return statement
        return Collections.emptyList();
    }

    // get members who borrowed books from at least K genres
    protected static List<String> membersWithGenreDiversity(final List<BookLoan> loans, final int k) {
        // Write your code here and replace the return statement
        return Collections.emptyList();
    }

    // find the first book title containing a substring (case-insensitive)
    protected static Optional<BookLoan> findFirstBookContaining(final List<BookLoan> loans, final String book) {
        // Write your code here and replace the return statement
        return Optional.empty();
    }

    // checks if the book is present in the loans (case-insensitive)
    protected static Boolean isBookPresent(final List<BookLoan> loans, final String book) {
        // Write your code here and replace the return statement
        return null;
    }
}
