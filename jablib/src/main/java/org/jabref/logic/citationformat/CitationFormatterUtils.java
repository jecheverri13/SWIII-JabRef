package org.jabref.logic.citationformat;

import java.util.Optional;

import org.jabref.model.entry.AuthorList;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;
import org.jabref.model.strings.LatexToUnicodeAdapter;

final class CitationFormatterUtils {
    private CitationFormatterUtils() {}

    static String authorsAcronymed(BibEntry e) {
        return e.getField(StandardField.AUTHOR)
                .map(AuthorList::parse)
                .map(al -> al.getAuthors().stream()
                             .map(a -> a.getGivenNameAbbreviated().orElseGet(() -> a.getGivenName().orElse(""))
                                         + " " + a.getNamePrefixAndFamilyName())
                             .reduce((a, b) -> a + ", " + b)
                             .orElse(""))
                .orElse("");
    }

    static String authorsLastFirstAbbr(BibEntry e, boolean oxfordComma) {
        return e.getField(StandardField.AUTHOR)
                .map(AuthorList::parse)
                .map(al -> al.getAsLastFirstNames(true, oxfordComma))
                .orElse("");
    }

    static String authorsFirstLast(BibEntry e) {
        return e.getField(StandardField.AUTHOR)
                .map(AuthorList::parse)
                .map(AuthorList::getAsFirstLastNamesWithAnd)
                .orElse("");
    }

    static String authorsVancouver(BibEntry e) {
        // Vancouver: Last FM, Last FM (initials concatenated, no spaces)
        return e.getField(StandardField.AUTHOR)
                .map(AuthorList::parse)
                .map(al -> al.getAuthors().stream()
                        .map(a -> {
                            String initials = a.getGivenNameAbbreviated().orElse("").replaceAll("[.\s]", "");
                            if (initials.isBlank()) {
                                initials = a.getGivenName().orElse("").replaceAll("[.\s]", "");
                            }
                            return a.getNamePrefixAndFamilyName() + " " + initials;
                        })
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("")
                )
                .orElse("");
    }

    static String title(BibEntry e) {
        return e.getField(StandardField.TITLE)
                .map(LatexToUnicodeAdapter::format)
                .map(String::trim)
                .orElse("");
    }

    static String year(BibEntry e) {
        // Try YEAR field first, then try to extract year from DATE field
        return e.getField(StandardField.YEAR)
                .or(() -> e.getField(StandardField.DATE)
                        .map(date -> {
                            // Try to extract year from date string (assumes YYYY or YYYY-MM-DD format)
                            if (date.length() >= 4) {
                                return date.substring(0, 4);
                            }
                            return "";
                        }))
                .orElse("");
    }

    static String publisherOrJournal(BibEntry e) {
        if (e.getType() == StandardEntryType.Article) {
            return e.getField(StandardField.JOURNAL).orElse("");
        }
        return e.getField(StandardField.PUBLISHER).orElse("");
    }

    static String venueForConference(BibEntry e) {
        // Try booktitle (InProceedings)
        return e.getField(StandardField.BOOKTITLE)
                .orElseGet(() -> e.getField(StandardField.EVENTTITLE).orElse(""));
    }

    static String optionalPrefix(String value, String prefix) {
        return (value == null || value.isBlank()) ? "" : prefix + value;
    }

    static String joinNonBlank(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if ((p != null) && !p.isBlank()) {
                if (sb.length() != 0) sb.append(' ');
                sb.append(p.trim());
            }
        }
        return sb.toString();
    }
}
