package org.jabref.logic.citationformat;

import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;

/** Simple IEEE-like formatter for preview purposes. */
public class IEEEFormatter implements CitationFormatter {
    @Override
    public String format(BibEntry e) {
        // IEEE style: up to 6 authors, then et al.
        String authors = e.getField(StandardField.AUTHOR)
                          .map(org.jabref.model.entry.AuthorList::parse)
                          .map(al -> {
                              var authorList = al.getAuthors();
                              if (authorList.size() <= 6) {
                                  return authorList.stream()
                                          .map(a -> a.getGivenNameAbbreviated().orElseGet(() -> a.getGivenName().orElse(""))
                                                  + " " + a.getNamePrefixAndFamilyName())
                                          .reduce((a, b) -> a + ", " + b)
                                          .orElse("");
                              } else {
                                  return authorList.subList(0, 6).stream()
                                          .map(a -> a.getGivenNameAbbreviated().orElseGet(() -> a.getGivenName().orElse(""))
                                                  + " " + a.getNamePrefixAndFamilyName())
                                          .reduce((a, b) -> a + ", " + b)
                                          .orElse("")
                                          + ", et al.";
                              }
                          })
                          .orElse("");

        String title = CitationFormatterUtils.title(e);
        String journal = e.getField(StandardField.JOURNAL).orElse("");
        String conference = CitationFormatterUtils.venueForConference(e);
        String volume = e.getField(StandardField.VOLUME).orElse("");
        String issue = e.getField(StandardField.NUMBER).orElse("");
        String pages = e.getField(StandardField.PAGES).orElse("");
        String month = e.getField(StandardField.MONTH).orElse("");
        String year = CitationFormatterUtils.year(e);
        String doi = e.getField(StandardField.DOI).orElse("");

        StringBuilder sb = new StringBuilder();

        // Authors
        if (!authors.isBlank()) {
            sb.append(authors).append(',').append(' ');
        }

        // Title in quotes
        if (!title.isBlank()) {
            sb.append('"').append(title).append('"').append(',').append(' ');
        }

        // Journal/Conference name in italics
        if (!journal.isBlank()) {
            sb.append("_").append(journal).append("_");
        } else if (!conference.isBlank()) {
            sb.append("in _").append(conference).append("_");
        }

        // Volume, issue, pages
        if (!volume.isBlank()) {
            sb.append(", vol. ").append(volume);
            if (!issue.isBlank()) {
                sb.append(", no. ").append(issue);
            }
            if (!pages.isBlank()) {
                sb.append(", pp. ").append(pages);
            }
        } else if (!pages.isBlank()) {
            sb.append(", pp. ").append(pages);
        }

        // Date
        if (!month.isBlank() || !year.isBlank()) {
            sb.append(", ");
            if (!month.isBlank()) {
                sb.append(month).append(' ');
            }
            if (!year.isBlank()) {
                sb.append(year);
            }
        }

        // DOI
        if (!doi.isBlank()) {
            sb.append(", doi: ").append(doi);
        }

        sb.append('.');
        return sb.toString().trim();
    }
}
