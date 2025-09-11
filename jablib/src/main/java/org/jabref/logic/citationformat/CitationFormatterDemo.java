package org.jabref.logic.citationformat;

import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.types.StandardEntryType;
import org.jabref.model.entry.field.StandardField;

public class CitationFormatterDemo {
    public static void main(String[] args) {
        // Crear un ejemplo de BibEntry
        BibEntry entry = new BibEntry();
        entry.setType(StandardEntryType.Article);
        entry.setField(StandardField.AUTHOR, "García, Juan y Pérez, Ana");
        entry.setField(StandardField.TITLE, "Un estudio sobre citas");
        entry.setField(StandardField.JOURNAL, "Revista de Ejemplo");
        entry.setField(StandardField.YEAR, "2023");
        entry.setField(StandardField.VOLUME, "15");
        entry.setField(StandardField.NUMBER, "2");
        entry.setField(StandardField.PAGES, "123-130");
        entry.setField(StandardField.DOI, "10.1234/ejemplo.2023.15.2.123");

        // Mostrar la cita en todos los formatos
        for (CitationStyleKind style : CitationStyleKind.values()) {
            CitationFormatter formatter = CitationFormatterFactory.of(style);
            String citation = formatter.format(entry);
            System.out.println("\nFormato " + style + ":\n" + citation);
        }
    }
}
