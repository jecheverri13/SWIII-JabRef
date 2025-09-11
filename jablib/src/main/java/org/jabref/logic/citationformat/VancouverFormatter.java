package org.jabref.logic.citationformat;

import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.strings.LatexToUnicodeAdapter;

/** Simple Vancouver-like formatter for preview purposes. */
public class VancouverFormatter implements CitationFormatter {
    @Override
    public String format(BibEntry e) {
        // Vancouver style: List first 6 authors, then et al.
        String authors = e.getField(StandardField.AUTHOR)
                          .map(org.jabref.model.entry.AuthorList::parse)
                          .map(al -> {
                              var authorList = al.getAuthors();
                              int maxAuthors = 6;
                              int displayCount = Math.min(authorList.size(), maxAuthors);
                              return authorList.subList(0, displayCount).stream()
                                      .map(a -> {
                                          String surname = a.getFamilyName().orElse("");
                                          String initials = a.getGivenNameAbbreviated().orElseGet(() ->
                                              a.getGivenName().orElse("")).replaceAll("\\.", "").replaceAll("\\s+", "");
                                          return surname + " " + initials;
                                      })
                                      .reduce((a, b) -> a + ", " + b)
                                      .orElse("")
                                      + (authorList.size() > maxAuthors ? ", et al" : "");
                          })
                          .orElse("");

        String title = LatexToUnicodeAdapter.format(CitationFormatterUtils.title(e));
        String journal = e.getField(StandardField.JOURNAL)
                         .map(j -> j.replaceAll("\\.", "")) // Vancouver removes periods in journal names
                         .orElse("");
        String year = CitationFormatterUtils.year(e);
        String volume = e.getField(StandardField.VOLUME).orElse("");
        String issue = e.getField(StandardField.NUMBER).orElse("");
        String pages = e.getField(StandardField.PAGES).orElse("");
        String doi = e.getField(StandardField.DOI).orElse("");

        StringBuilder sb = new StringBuilder();

        // Authors
        if (!authors.isBlank()) {
            sb.append(authors).append(". ");
        }

        // Title
        if (!title.isBlank()) {
            sb.append(title);
            if (!title.endsWith(".")) {
                sb.append(".");
            }
            sb.append(" ");
        }

        // Journal info with exact Vancouver format
        if (!journal.isBlank()) {
            sb.append(journal).append(". ");

            // Year;volume(issue):pages
            if (!year.isBlank()) {
                sb.append(year);
            }
            if (!volume.isBlank()) {
                sb.append(";").append(volume);
                if (!issue.isBlank()) {
                    sb.append("(").append(issue).append(")");
                }
            }
            if (!pages.isBlank()) {
                sb.append(":").append(pages.replace("--", "-")); // Ensure page ranges use single hyphen
            }
            sb.append(".");
        }

        // DOI without label, just colon
        if (!doi.isBlank()) {
            sb.append(" doi:").append(doi);
        }        return sb.toString().trim();
    }
}
