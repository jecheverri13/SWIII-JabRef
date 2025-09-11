package org.jabref.logic.citationstyle;

import java.util.List;

import org.jabref.model.database.BibDatabase;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;
import org.jabref.model.entry.BibEntryTypesManager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CitationStyleGeneratorExtendedTest {

    private static List<CitationStyle> allStyles;
    private BibEntry testEntry;
    private BibDatabaseContext databaseContext;
    private static final BibEntryTypesManager ENTRY_TYPES_MANAGER = new BibEntryTypesManager();

    @BeforeAll
    static void setUpClass() {
        CSLStyleLoader.loadInternalStyles();
        allStyles = CSLStyleLoader.getInternalStyles();
    }

    @BeforeEach
    void setUp() {
        testEntry = new BibEntry(StandardEntryType.Article)
            .withField(StandardField.AUTHOR, "Smith, John and Johnson, Mark")
            .withField(StandardField.TITLE, "Test Article")
            .withField(StandardField.JOURNAL, "Journal of Testing")
            .withField(StandardField.YEAR, "2023")
            .withField(StandardField.VOLUME, "1")
            .withField(StandardField.NUMBER, "2")
            .withField(StandardField.PAGES, "100-120")
            .withCitationKey("Smith2023");

        databaseContext = new BibDatabaseContext(new BibDatabase(List.of(testEntry)));
    }

    @Test
    void testAPAStyleGeneration() {
        CitationStyle apaStyle = allStyles.stream()
                                        .filter(s -> "American Psychological Association 7th edition".equals(s.getTitle()))
                                        .findFirst()
                                        .orElseThrow();

        String citation = CitationStyleGenerator.generateCitation(List.of(testEntry),
                                                               apaStyle.getSource(),
                                                               CitationStyleOutputFormat.HTML,
                                                               databaseContext,
                                                               ENTRY_TYPES_MANAGER);

        assertTrue(citation.contains("Smith"));
        assertTrue(citation.contains("Johnson"));
        assertTrue(citation.contains("2023"));
    }

    @Test
    void testIEEEStyleGeneration() {
        CitationStyle ieeeStyle = allStyles.stream()
                                         .filter(s -> "IEEE".equals(s.getTitle()))
                                         .findFirst()
                                         .orElseThrow();

        String citation = CitationStyleGenerator.generateCitation(List.of(testEntry),
                                                               ieeeStyle.getSource(),
                                                               CitationStyleOutputFormat.HTML,
                                                               databaseContext,
                                                               ENTRY_TYPES_MANAGER);

        assertTrue(citation.contains("J. Smith"));
        assertTrue(citation.contains("M. Johnson"));
    }

    @Test
    void testMLAStyleGeneration() {
        CitationStyle mlaStyle = allStyles.stream()
                                        .filter(s -> s.getTitle().contains("Modern Language Association"))
                                        .findFirst()
                                        .orElseThrow();

        String citation = CitationStyleGenerator.generateCitation(List.of(testEntry),
                                                               mlaStyle.getSource(),
                                                               CitationStyleOutputFormat.HTML,
                                                               databaseContext,
                                                               ENTRY_TYPES_MANAGER);

        assertTrue(citation.contains("Smith"));
        assertTrue(citation.contains("Johnson"));
    }

    @Test
    void testVancouverStyleGeneration() {
        CitationStyle vancouverStyle = allStyles.stream()
                                              .filter(s -> "Vancouver".equals(s.getTitle()))
                                              .findFirst()
                                              .orElseThrow();

        String citation = CitationStyleGenerator.generateCitation(List.of(testEntry),
                                                               vancouverStyle.getSource(),
                                                               CitationStyleOutputFormat.HTML,
                                                               databaseContext,
                                                               ENTRY_TYPES_MANAGER);

        assertTrue(citation.matches(".*\\d+.*")); // Should contain a number
    }

    @Test
    void testBibliographyGeneration() {
        CitationStyle style = allStyles.get(0); // Use any style that supports bibliography

        List<String> bibliography = CitationStyleGenerator.generateBibliography(List.of(testEntry),
                                                                             style.getSource(),
                                                                             CitationStyleOutputFormat.HTML,
                                                                             databaseContext,
                                                                             ENTRY_TYPES_MANAGER);

        assertNotNull(bibliography);
        assertTrue(bibliography.size() > 0);
        assertTrue(bibliography.get(0).contains(testEntry.getField(StandardField.TITLE).orElse("")));
    }

    @ParameterizedTest
    @EnumSource(CitationStyleOutputFormat.class)
    void testOutputFormats(CitationStyleOutputFormat format) {
        CitationStyle style = allStyles.get(0);

        String citation = CitationStyleGenerator.generateCitation(List.of(testEntry),
                                                               style.getSource(),
                                                               format,
                                                               databaseContext,
                                                               ENTRY_TYPES_MANAGER);

        assertNotNull(citation);
        assertTrue(citation.length() > 0);

        // HTML format should contain HTML tags
        if (format == CitationStyleOutputFormat.HTML) {
            assertTrue(citation.contains("<") && citation.contains(">"));
        }

        // TEXT format should not contain HTML tags
        if (format == CitationStyleOutputFormat.TEXT) {
            assertTrue(!citation.contains("<") && !citation.contains(">"));
        }
    }

    @Test
    void testMultipleEntriesCitation() {
        BibEntry secondEntry = new BibEntry(StandardEntryType.Article)
            .withField(StandardField.AUTHOR, "Williams, Jane")
            .withField(StandardField.TITLE, "Another Test")
            .withField(StandardField.YEAR, "2023");

        BibDatabaseContext contextWithTwoEntries =
            new BibDatabaseContext(new BibDatabase(List.of(testEntry, secondEntry)));

        CitationStyle style = allStyles.get(0);

        String citation = CitationStyleGenerator.generateCitation(List.of(testEntry, secondEntry),
                                                               style.getSource(),
                                                               CitationStyleOutputFormat.HTML,
                                                               contextWithTwoEntries,
                                                               ENTRY_TYPES_MANAGER);

        assertNotNull(citation);
        assertTrue(citation.contains("Smith") && citation.contains("Williams"));
    }

    @Test
    void testBibliographyOrder() {
        BibEntry entryA = new BibEntry(StandardEntryType.Article)
            .withField(StandardField.AUTHOR, "Anderson, Alan")
            .withField(StandardField.YEAR, "2023");

        BibEntry entryB = new BibEntry(StandardEntryType.Article)
            .withField(StandardField.AUTHOR, "Brown, Bob")
            .withField(StandardField.YEAR, "2023");

        BibDatabaseContext orderedContext =
            new BibDatabaseContext(new BibDatabase(List.of(entryB, entryA))); // Add out of order

        CitationStyle style = allStyles.get(0);

        List<String> bibliography = CitationStyleGenerator.generateBibliography(List.of(entryB, entryA),
                                                                             style.getSource(),
                                                                             CitationStyleOutputFormat.HTML,
                                                                             orderedContext,
                                                                             ENTRY_TYPES_MANAGER);

        // Should maintain the order they were added in
        assertTrue(bibliography.get(0).contains("Brown"));
        assertTrue(bibliography.get(1).contains("Anderson"));
    }
}
