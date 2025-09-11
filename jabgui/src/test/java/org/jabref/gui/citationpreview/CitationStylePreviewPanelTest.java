package org.jabref.gui.citationpreview;

import java.util.List;

import org.jabref.gui.DialogService;
import org.jabref.gui.StateManager;
import org.jabref.gui.util.TaskExecutor;
import org.jabref.logic.citationstyle.CSLStyleLoader;
import org.jabref.logic.citationstyle.CitationStyle;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;
import org.jabref.preferences.PreferencesService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CitationStylePreviewPanelTest {

    private CitationStylePreviewPanel panel;
    private BibEntry testEntry;
    private BibDatabaseContext databaseContext;

    @Mock private DialogService dialogService;
    @Mock private TaskExecutor taskExecutor;
    @Mock private StateManager stateManager;
    @Mock private PreferencesService preferencesService;

    @BeforeEach
    void setUp() {
        CSLStyleLoader.loadInternalStyles();

        panel = new CitationStylePreviewPanel(dialogService, taskExecutor, stateManager, preferencesService);

        testEntry = new BibEntry(StandardEntryType.Article)
            .withField(StandardField.AUTHOR, "Smith, John")
            .withField(StandardField.TITLE, "Test Title")
            .withField(StandardField.JOURNAL, "Test Journal")
            .withField(StandardField.YEAR, "2023");

        databaseContext = new BibDatabaseContext();
    }

    @Test
    void testPanelInitialization() {
        assertNotNull(panel);
    }

    @Test
    void testAvailableCitationStyles() {
        List<CitationStyle> styles = CSLStyleLoader.getInternalStyles();
        assertNotNull(styles);
        // We should have at least APA, IEEE, MLA, Vancouver styles
        assert(styles.size() >= 4);
    }

    @Test
    void testAPAStylePreview() {
        CitationStyle apaStyle = CSLStyleLoader.getInternalStyles().stream()
                                             .filter(s -> "American Psychological Association 7th edition".equals(s.getTitle()))
                                             .findFirst()
                                             .orElseThrow();

        panel.setEntry(testEntry);
        panel.setDatabase(databaseContext);
        panel.setStyle(apaStyle);

        // Generate preview
        String preview = panel.generatePreview(testEntry);

        // APA preview should contain author and year
        assert(preview.contains("Smith"));
        assert(preview.contains("2023"));
    }

    @Test
    void testIEEEStylePreview() {
        CitationStyle ieeeStyle = CSLStyleLoader.getInternalStyles().stream()
                                              .filter(s -> "IEEE".equals(s.getTitle()))
                                              .findFirst()
                                              .orElseThrow();

        panel.setEntry(testEntry);
        panel.setDatabase(databaseContext);
        panel.setStyle(ieeeStyle);

        // Generate preview
        String preview = panel.generatePreview(testEntry);

        // IEEE preview should contain numbered reference style
        assert(preview.contains("[1]") || preview.contains("J. Smith"));
    }

    @Test
    void testVancouverStylePreview() {
        CitationStyle vancouverStyle = CSLStyleLoader.getInternalStyles().stream()
                                                   .filter(s -> "Vancouver".equals(s.getTitle()))
                                                   .findFirst()
                                                   .orElseThrow();

        panel.setEntry(testEntry);
        panel.setDatabase(databaseContext);
        panel.setStyle(vancouverStyle);

        // Generate preview
        String preview = panel.generatePreview(testEntry);

        // Vancouver style uses numbered references
        assert(preview.contains("1."));
    }

    @Test
    void testMLAStylePreview() {
        CitationStyle mlaStyle = CSLStyleLoader.getInternalStyles().stream()
                                             .filter(s -> s.getTitle().contains("Modern Language Association"))
                                             .findFirst()
                                             .orElseThrow();

        panel.setEntry(testEntry);
        panel.setDatabase(databaseContext);
        panel.setStyle(mlaStyle);

        // Generate preview
        String preview = panel.generatePreview(testEntry);

        // MLA preview should contain author name
        assert(preview.contains("Smith"));
    }

    @Test
    void testStyleSwitching() {
        List<CitationStyle> styles = CSLStyleLoader.getInternalStyles();
        panel.setEntry(testEntry);
        panel.setDatabase(databaseContext);

        String previousPreview = null;

        // Test that switching between styles produces different previews
        for (CitationStyle style : styles.subList(0, 2)) {
            panel.setStyle(style);
            String currentPreview = panel.generatePreview(testEntry);

            if (previousPreview != null) {
                assert(!currentPreview.equals(previousPreview));
            }

            previousPreview = currentPreview;
        }
    }
}
