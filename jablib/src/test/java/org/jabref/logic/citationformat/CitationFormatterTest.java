package org.jabref.logic.citationformat;

import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CitationFormatterTest {

    private BibEntry book(String title, String authors, String publisher, String year) {
        return new BibEntry(StandardEntryType.Book)
                .withField(StandardField.TITLE, title)
                .withField(StandardField.AUTHOR, authors)
                .withField(StandardField.PUBLISHER, publisher)
                .withField(StandardField.YEAR, year);
    }

    private BibEntry article(String title, String authors, String journal, String year) {
        return new BibEntry(StandardEntryType.Article)
                .withField(StandardField.TITLE, title)
                .withField(StandardField.AUTHOR, authors)
                .withField(StandardField.JOURNAL, journal)
                .withField(StandardField.YEAR, year);
    }

    private BibEntry inProceedings(String title, String authors, String booktitle, String year) {
        return new BibEntry(StandardEntryType.InProceedings)
                .withField(StandardField.TITLE, title)
                .withField(StandardField.AUTHOR, authors)
                .withField(StandardField.BOOKTITLE, booktitle)
                .withField(StandardField.YEAR, year);
    }

    @Test
    void apaFormatter_book_example() {
        BibEntry e = book("Clean Code", "Robert C. Martin", "Pearson", "2008");
        String out = new APAFormatter().format(e);
        assertEquals("Martin, R. C. (2008). Clean Code. Pearson.", out);
    }

    @Test
    void ieeeFormatter_book_example() {
        BibEntry e = book("Clean Code", "Robert C. Martin", "Pearson", "2008");
        String out = new IEEEFormatter().format(e);
        assertEquals("R. C. Martin, \"Clean Code\", Pearson, 2008.", out);
    }

    @Test
    void mlaFormatter_book_example() {
        BibEntry e = book("Clean Code", "Robert C. Martin", "Pearson", "2008");
        String out = new MLAFormatter().format(e);
        assertEquals("Martin, Robert C.. Clean Code. Pearson, 2008.", out);
    }

    @Test
    void vancouverFormatter_book_example() {
        BibEntry e = book("Clean code", "Robert C. Martin", "Pearson", "2008");
        String out = new VancouverFormatter().format(e);
        assertEquals("Martin RC. Clean code. Pearson; 2008.", out);
    }

    @Test
    void ieee_article_example() {
        BibEntry e = article("A Paper", "Alice B. Doe and Bob C. Roe", "J. Testing", "2020");
        String out = new IEEEFormatter().format(e);
        assertEquals("A. B. Doe, B. C. Roe, \"A Paper\", J. Testing, 2020.", out);
    }

    @Test
    void apa_inproceedings_example_handles_missing_publisher() {
        BibEntry e = inProceedings("Proc Paper", "Jane Q. Public", "ICSE", "2019");
        String out = new APAFormatter().format(e);
        // No publisher expected
        assertEquals("Public, J. Q. (2019). Proc Paper.", out);
    }

    @Test
    void testCitationFormatterFactory() {
        BibEntry entry = new BibEntry(StandardEntryType.Book)
                .withField(StandardField.TITLE, "Clean Code")
                .withField(StandardField.AUTHOR, "Robert C. Martin")
                .withField(StandardField.PUBLISHER, "Pearson")
                .withField(StandardField.YEAR, "2008");

        System.out.println(CitationFormatterFactory.of(CitationStyleKind.APA).format(entry));
        System.out.println(CitationFormatterFactory.of(CitationStyleKind.IEEE).format(entry));
        System.out.println(CitationFormatterFactory.of(CitationStyleKind.MLA).format(entry));
        System.out.println(CitationFormatterFactory.of(CitationStyleKind.VANCOUVER).format(entry));
    }
}
