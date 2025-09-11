package org.jabref.logic.citationformat;

import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.strings.LatexToUnicodeAdapter;

/** Simple APA-like formatter for preview purposes. */
public class APAFormatter implements CitationFormatter {
    @Override
    public String format(BibEntry e) {
        // APA 7th edition: Up to 20 authors listed, then et al.
        String authors = e.getField(StandardField.AUTHOR)
                          .map(org.jabref.model.entry.AuthorList::parse)
                          .map(al -> {
                              var authorList = al.getAuthors();
                              if (authorList.size() <= 20) {
                                  return authorList.stream()
                                          .map(a -> a.getFamilyName().orElse("") + ", "
                                                  + a.getGivenNameAbbreviated().orElseGet(() -> a.getGivenName().orElse("")))
                                          .reduce((a, b) -> authorList.size() == 2 ? a + " & " + b :
                                                         a + ", " + (b.equals(authorList.get(authorList.size()-1).getFamilyName().orElse("") + ", "
                                                         + authorList.get(authorList.size()-1).getGivenNameAbbreviated().orElseGet(() ->
                                                         authorList.get(authorList.size()-1).getGivenName().orElse(""))) ? "& " + b : b))
                                          .orElse("");
                              } else {
                                  return authorList.subList(0, 19).stream()
                                          .map(a -> a.getFamilyName().orElse("") + ", "
                                                  + a.getGivenNameAbbreviated().orElseGet(() -> a.getGivenName().orElse("")))
                                          .reduce((a, b) -> a + ", " + b)
                                          .orElse("")
                                          + ", ... " + authorList.get(19).getFamilyName().orElse("") + ", "
                                          + authorList.get(19).getGivenNameAbbreviated().orElseGet(() -> authorList.get(19).getGivenName().orElse(""));
                              }
                          })
                          .orElse("");

        String year = CitationFormatterUtils.year(e);
        String title = LatexToUnicodeAdapter.format(CitationFormatterUtils.title(e));
        String publisher = e.getField(StandardField.PUBLISHER).orElse("");
        String volume = e.getField(StandardField.VOLUME).orElse("");
        String issue = e.getField(StandardField.NUMBER).orElse("");
        String pages = e.getField(StandardField.PAGES).orElse("");
        String doi = e.getField(StandardField.DOI).orElse("");
        String journal = e.getField(StandardField.JOURNAL).orElse("");

        StringBuilder sb = new StringBuilder();
        if (!authors.isBlank()) {
            sb.append(authors);
            if (!authors.endsWith(".")) {
                sb.append('.');
            }
            sb.append(' ');
        }
        if (!year.isBlank()) sb.append('(').append(year).append(')').append('.').append(' ');
        if (!title.isBlank()) sb.append(title);
        if (!title.endsWith(".")) sb.append('.');
        sb.append(' ');

        // Add journal or publisher info with italics (using *)
        if (!journal.isBlank()) {
            sb.append("*").append(journal);
            if (!volume.isBlank()) {
                sb.append(", ").append(volume);
                if (!issue.isBlank()) {
                    sb.append("*(").append(issue).append(")");
                } else {
                    sb.append("*");
                }
            } else {
                sb.append("*");
            }
            if (!pages.isBlank()) {
                sb.append(", ").append(pages);
            }
            sb.append(".");
        } else if (!publisher.isBlank()) {
            sb.append("*").append(publisher).append("*.");
        }

        // Add DOI if available
        if (!doi.isBlank()) {
            sb.append(" https://doi.org/").append(doi);
        }

        return sb.toString().trim();
    }
}
