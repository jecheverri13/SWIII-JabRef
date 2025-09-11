package org.jabref.gui.preview;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.jabref.logic.citationformat.CitationFormatter;
import org.jabref.logic.citationformat.CitationFormatterFactory;
import org.jabref.logic.citationformat.CitationStyleKind;
import org.jabref.model.entry.BibEntry;

public class CitationPreviewPanel extends BorderPane {
    private final ComboBox<CitationStyleKind> styleCombo;
    private final TextArea previewArea;
    private BibEntry currentEntry;

    public CitationPreviewPanel() {
        styleCombo = new ComboBox<>();
        styleCombo.getItems().addAll(CitationStyleKind.values());
        styleCombo.setValue(CitationStyleKind.APA);
        styleCombo.setOnAction(e -> updatePreview());

        previewArea = new TextArea();
        previewArea.setEditable(false);
        previewArea.setWrapText(true);

        VBox topBox = new VBox(8, new Label("Estilo de cita:"), styleCombo);
        topBox.setPadding(new Insets(8));
        setTop(topBox);
        setCenter(previewArea);
        setPadding(new Insets(8));
    }

    public void setEntry(BibEntry entry) {
        this.currentEntry = entry;
        updatePreview();
    }

    private void updatePreview() {
        if (currentEntry != null) {
            CitationFormatter formatter = CitationFormatterFactory.of(styleCombo.getValue());
            String citation = formatter.format(currentEntry);
            previewArea.setText(citation);
        } else {
            previewArea.setText("No hay entrada seleccionada.");
        }
    }
}
