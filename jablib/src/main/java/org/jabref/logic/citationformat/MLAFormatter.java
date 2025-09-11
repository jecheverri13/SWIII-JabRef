package org.jabref.logic.citationformat;

import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.strings.LatexToUnicodeAdapter;

/** Simple MLA-like formatter for preview purposes. */
public class MLAFormatter implements CitationFormatter {
    @Override
    public String format(BibEntry e) {
        // MLA 9th edition formatting
        String authors = e.getField(StandardField.AUTHOR)
                          .map(org.jabref.model.entry.AuthorList::parse)
                          .map(al -> {
                              var authorList = al.getAuthors();
                              if (authorList.size() <= 2) {
                                  return authorList.stream()
                                          .map(a -> a.getFamilyName().orElse("") + ", "
                                                  + a.getGivenName().orElse(""))
                                          .reduce((a, b) -> a + ", and " + b)
                                          .orElse("");
                              } else {
                                  return authorList.get(0).getFamilyName().orElse("") + ", "
                                         + authorList.get(0).getGivenName().orElse("")
                                         + ", et al.";
                              }
                          })
                          .orElse("");

        String title = LatexToUnicodeAdapter.format(CitationFormatterUtils.title(e));
        String container = e.getField(StandardField.JOURNAL)
                           .or(() -> e.getField(StandardField.BOOKTITLE))
                           .orElse("");
        String publisher = e.getField(StandardField.PUBLISHER).orElse("");
        String volume = e.getField(StandardField.VOLUME).orElse("");
        String issue = e.getField(StandardField.NUMBER).orElse("");
        String pages = e.getField(StandardField.PAGES).orElse("");
        String year = CitationFormatterUtils.year(e);
        String doi = e.getField(StandardField.DOI).orElse("");
        String url = e.getField(StandardField.URL).orElse("");

        StringBuilder sb = new StringBuilder();

        // Authors
        if (!authors.isBlank()) {
            sb.append(authors).append('.').append(' ');
        }

        // Title (in quotes for articles, italicized for books)
        if (!title.isBlank()) {
            if (!container.isBlank()) {
                sb.append('"').append(title).append('"');
            } else {
                sb.append("_").append(title).append("_");
            }
            sb.append('.').append(' ');
        }

        // Container (journal/book title in italics)
        if (!container.isBlank()) {
            sb.append("_").append(container).append("_");

            // Volume and issue
            if (!volume.isBlank()) {
                sb.append(", vol. ").append(volume);
                if (!issue.isBlank()) {
                    sb.append(", no. ").append(issue);
                }
            }

            if (!pages.isBlank()) {
                sb.append(", pp. ").append(pages);
            }
            sb.append(", ");
        }

        // Publisher info
        if (!publisher.isBlank()) {
            sb.append(publisher);
            if (!year.isBlank()) {
                sb.append(", ");
            }
        }

        // Year
        if (!year.isBlank()) {
            sb.append(year);
        }

        // DOI or URL
        if (!doi.isBlank()) {
            sb.append(", https://doi.org/").append(doi);
        } else if (!url.isBlank()) {
            sb.append(", ").append(url);
        }

        sb.append('.');
        return sb.toString().trim();
    }
}
