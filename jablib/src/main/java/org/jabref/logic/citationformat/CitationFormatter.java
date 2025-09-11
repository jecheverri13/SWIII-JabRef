package org.jabref.logic.citationformat;

import org.jabref.model.entry.BibEntry;

/**
 * Contract for formatting a single {@link BibEntry} into a citation string.
 * Implementations must be stateless and thread-safe.
 */
public interface CitationFormatter {
    /**
     * Formats the given entry into a style-specific citation string.
     * Implementations should be resilient to missing fields and omit
     * unavailable parts gracefully without throwing.
     *
     * @param entry BibEntry to format (non-null)
     * @return human-readable citation
     */
    String format(BibEntry entry);
}
