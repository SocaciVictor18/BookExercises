package org.example.bookexercises;

import org.example.bookexercises.model.BookLoan;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.example.bookexercises.UtilityClassTest.createLoan;
import static org.junit.jupiter.api.Assertions.*;

public class LibraryUtilTest {

    private static final String RESOURCE_FILE = "libraryLoans.csv";

    @Test
    public void testLoadResourceFile_givenExistingCsvFile_whenLoadResourceFile_thenAllLinesAreLoaded() {
        Path resourcePath = Paths.get("src", "main", "resources", RESOURCE_FILE);
        String filePath = resourcePath.toString();

        List<String> lines = LibraryUtil.loadResourceFile(filePath);

        assertNotNull(lines);
        assertFalse(lines.isEmpty());
        assertEquals(35, lines.size());
        assertTrue(lines.getFirst().contains("L-1001"));
    }

    @Test
    public void testParseCsvLines() {
        List<String> lines = List.of(
                "L-2000, M-100, 2024-01-01, Test Book, Fantasy, Test Author, 10,",
                "BAD_LINE_SHOULD_BE_SKIPPED,",
                "L-2001, M-101, not-a-date, Other Book, Horror, Other Author, 5,",
                "   "
        );

        Map<String, List<BookLoan>> result = LibraryUtil.parseCsvLines(lines);

        assertNotNull(result);
        assertTrue(result.containsKey("valid"));
        assertTrue(result.containsKey("malformed"));

        List<BookLoan> validLoans = result.get("valid");
        List<BookLoan> malformedLoans = result.get("malformed");

        assertEquals(1, validLoans.size());
        assertEquals(2, malformedLoans.size());

        BookLoan loan = validLoans.getFirst();
        assertEquals("L-2000", loan.getLoanId());
        assertEquals("M-100", loan.getMemberId());
        assertEquals(LocalDate.of(2024, 1, 1), loan.getLoanDate());
        assertEquals("Test Book", loan.getBookTitle());
        assertEquals("Fantasy", loan.getGenre());
        assertEquals("Test Author", loan.getAuthor());
        assertEquals(10, loan.getDaysLoaned());
    }

    @Test
    public void testLoansByGenre_withMultipleGenresAndEmptyList() {
        List<BookLoan> loans = List.of(
                createLoan("L1", "M1", "2024-01-01", "B1", "Fantasy", "A", 10),
                createLoan("L2", "M1", "2024-01-02", "B2", "Fantasy", "B", 5),
                createLoan("L3", "M2", "2024-01-03", "B3", "Horror", "C", 7),
                createLoan("L4", "M3", "2024-01-04", "B4", "Science Fiction", "D", 8),
                createLoan("L5", "M3", "2024-01-05", "B5", "Science Fiction", "E", 9)
        );

        Map<String, Long> byGenre = LibraryUtil.loansByGenre(loans);

        assertEquals(3, byGenre.size());
        assertEquals(2L, byGenre.get("Fantasy"));
        assertEquals(1L, byGenre.get("Horror"));
        assertEquals(2L, byGenre.get("Science Fiction"));
        assertEquals(List.of("Fantasy", "Horror", "Science Fiction"), byGenre.keySet().stream().toList());

        Map<String, Long> emptyResult = LibraryUtil.loansByGenre(List.of());
        assertTrue(emptyResult.isEmpty());
    }

    @Test
    public void testTopAuthorsByLoans_withLimitAndGreaterThanAuthors() {
        List<BookLoan> loans = List.of(
                createLoan("L1", "M1", "2024-01-01", "B1", "Fantasy", "AuthorA", 10),
                createLoan("L2", "M1", "2024-01-02", "B2", "Fantasy", "AuthorA", 7),
                createLoan("L3", "M2", "2024-01-03", "B3", "Horror", "AuthorB", 5),
                createLoan("L4", "M2", "2024-01-04", "B4", "Horror", "AuthorB", 6),
                createLoan("L5", "M3", "2024-01-05", "B5", "Sci-Fi", "AuthorC", 3)
        );

        List<String> top2 = LibraryUtil.topAuthorsByLoans(loans, 2);
        assertEquals(List.of("AuthorB", "AuthorA"), top2);

        List<String> top10 = LibraryUtil.topAuthorsByLoans(loans, 10);
        assertEquals(3, top10.size());
        assertTrue(top10.containsAll(List.of("AuthorA", "AuthorB", "AuthorC")));

        List<String> emptyTop = LibraryUtil.topAuthorsByLoans(List.of(), 3);
        assertTrue(emptyTop.isEmpty());
    }

    @Test
    public void testMembersWithGenreDiversity_variousKValuesAndDuplicates() {
        List<BookLoan> loans = List.of(
                createLoan("L1", "M1", "2024-01-01", "B1", "Fantasy", "A", 10),
                createLoan("L2", "M1", "2024-01-02", "B2", "Horror", "A", 10),
                createLoan("L3", "M1", "2024-01-03", "B3", "Science Fiction", "B", 10),
                createLoan("L4", "M2", "2024-01-04", "B4", "Fantasy", "B", 10),
                createLoan("L5", "M2", "2024-01-05", "B5", "Fantasy", "C", 10),
                createLoan("L6", "M3", "2024-01-06", "B6", "Horror", "C", 10),
                createLoan("L7", "M3", "2024-01-07", "B7", "Dystopian", "D", 10)
        );

        List<String> k2 = LibraryUtil.membersWithGenreDiversity(loans, 2);
        assertTrue(k2.contains("M1"));
        assertTrue(k2.contains("M3"));
        assertEquals(2, k2.size());

        List<String> k3 = LibraryUtil.membersWithGenreDiversity(loans, 3);
        assertEquals(List.of("M1"), k3);

        List<String> k4 = LibraryUtil.membersWithGenreDiversity(loans, 4);
        assertTrue(k4.isEmpty());
    }

    @Test
    public void testFindFirstBookContaining_caseInsensitiveAndNotFound() {
        List<BookLoan> loans = List.of(
            createLoan("L1", "M1", "2024-01-01", "The Hobbit", "Fantasy", "A", 10),
            createLoan("L2", "M2", "2024-01-02", "Dune", "Science Fiction", "B", 7),
            createLoan("L3", "M3", "2024-01-03", "Dune Messiah", "Science Fiction", "C", 8)
        );

        Optional<BookLoan> firstDune = LibraryUtil.findFirstBookContaining(loans, "dune");
        assertTrue(firstDune.isPresent());
        assertEquals("L2", firstDune.get().getLoanId());

        Optional<BookLoan> notFound = LibraryUtil.findFirstBookContaining(loans, "Harry Potter");
        assertTrue(notFound.isEmpty());
    }

    @Test
    public void testIsBookPresent_caseInsensitiveAndAbsent() {
        List<BookLoan> loans = List.of(
                createLoan("L1", "M1", "2024-01-01", "The Hobbit", "Fantasy", "A", 10),
                createLoan("L2", "M2", "2024-01-02", "Dune", "Science Fiction", "B", 7)
        );

        Boolean present = LibraryUtil.isBookPresent(loans, "dune");
        Boolean notPresent = LibraryUtil.isBookPresent(loans, "Harry Potter");

        assertTrue(present);
        assertFalse(notPresent);
    }


}
