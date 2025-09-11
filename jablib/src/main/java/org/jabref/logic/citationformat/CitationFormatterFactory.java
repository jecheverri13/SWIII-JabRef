package org.jabref.logic.citationformat;

/** Factory for built-in citation formatters. */
public final class CitationFormatterFactory {
    private CitationFormatterFactory() {}

    public static CitationFormatter of(CitationStyleKind kind) {
        return switch (kind) {
            case APA -> new APAFormatter();
            case IEEE -> new IEEEFormatter();
            case MLA -> new MLAFormatter();
            case VANCOUVER -> new VancouverFormatter();
        };
    }
}
