package se.isselab.HAnS.trafficLight;

import com.intellij.codeInsight.hint.HintManagerImpl;
import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.ShowLogAction;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.GridBag;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HansTrafficLightPanel {
    private static final String NO_FINDINGS_TEXT = "File is not mapped.";
    private static final String METRICS_ERROR_MSG = "Error while fetching mapping.";
    private static final String FEATURE_FILE_MAPPINGS = "Mapped features through *.feature-file:";
    private static final String FEATURE_FOLDER_MAPPINGS = "Mapped features through *.feature-folder:";
    private static final int DEFAULT_MAX_WIDTH = 270;


    JPanel panel = new JPanel(new GridBagLayout());

    private final JBLabel findingsSummaryLabel = new JBLabel(NO_FINDINGS_TEXT);
    private final JBLabel hansCrashed = new JBLabel(METRICS_ERROR_MSG);

    private final JPanel fileMappingsPanel = new JPanel(new GridBagLayout());
    private final JPanel folderMappingsPanel = new JPanel(new GridBagLayout());
    private final JPanel fileMappingsFeaturePanel = new JPanel(new GridBagLayout());
    private final JPanel folderMappingsFeaturePanel = new JPanel(new GridBagLayout());

    private final Editor editor;

    public HansTrafficLightPanel(Editor editor) {
        this.editor = editor;

        TrafficLightActionButton menuButton =
                new TrafficLightActionButton(
                        new MenuAction(),
                        null,
                        ActionPlaces.EDITOR_POPUP,
                        ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE);

        GridBag gc = new GridBag();
        gc.nextLine().next().anchor(GridBagConstraints.LINE_START).weightx(1.0).fillCellHorizontally().insets(10, 10, 10, 10);

        hansCrashed.setVisible(false);
        panel.add(hansCrashed, gc);

        panel.add(findingsSummaryLabel, gc);

        panel.add(menuButton, gc.next().anchor(GridBagConstraints.LINE_END).weightx(0.0).insets(10, 6, 10, 6));

        JBLabel fileMappingsHeaderLabel = new JBLabel(FEATURE_FILE_MAPPINGS);
        fileMappingsHeaderLabel.setFont(JBUI.Fonts.label().asBold());
        fileMappingsPanel.add(fileMappingsHeaderLabel, gc.nextLine().next().anchor(GridBagConstraints.LINE_START).weightx(1.0).fillCellHorizontally()
                .insets(0, 10, 6, 10)
        );
        fileMappingsFeaturePanel.setVisible(false);
        fileMappingsPanel.add(fileMappingsFeaturePanel, gc.nextLine().next().anchor(GridBagConstraints.LINE_START)
                .weightx(1.0).fillCellHorizontally().insets(0, 10, 6, 10));

        JBLabel folderMappingsHeaderLabel = new JBLabel(FEATURE_FOLDER_MAPPINGS);
        folderMappingsHeaderLabel.setFont(JBUI.Fonts.label().asBold());
        folderMappingsPanel.add(folderMappingsHeaderLabel, gc.nextLine().next().anchor(GridBagConstraints.LINE_START).weightx(1.0).fillCellHorizontally()
                .insets(0, 10, 6, 10)
        );
        folderMappingsFeaturePanel.setVisible(false);
        folderMappingsPanel.add(folderMappingsFeaturePanel, gc.nextLine().next().anchor(GridBagConstraints.LINE_START)
                .weightx(1.0).fillCellHorizontally().insets(0, 10, 6, 10));

        panel.add(fileMappingsPanel, gc.nextLine().next().anchor(GridBagConstraints.LINE_START).weightx(1.0).fillCellHorizontally().insets(0, 5, 10, 0)
        );
        panel.add(folderMappingsPanel, gc.nextLine().next().anchor(GridBagConstraints.LINE_START).weightx(1.0).fillCellHorizontally().insets(0, 5, 10, 0)
        );
    }

    private void toggleAliveStatusVisibility(Boolean isAlive) {
        findingsSummaryLabel.setVisible(isAlive);
        hansCrashed.setVisible(!isAlive);
    }

    protected void refresh(HansTrafficLightDashboardModel model) {
        toggleAliveStatusVisibility(model.isAlive());
        if (!model.hasFindings()) {
            findingsSummaryLabel.setText(NO_FINDINGS_TEXT);
            fileMappingsPanel.setVisible(false);
            folderMappingsPanel.setVisible(false);
        } else {
            findingsSummaryLabel.setText("Number of features connected to file: " + model.findingsCount());
            populateFeatureMappings(fileMappingsFeaturePanel, model.getFilePathsFeatureFileMapping());
            populateFeatureMappings(folderMappingsFeaturePanel, model.getFilePathsFeatureFolderMapping());
        }
        repaint();
    }

    private void repaint() {
        fileMappingsPanel.revalidate();
        fileMappingsPanel.repaint();
        folderMappingsPanel.revalidate();
        folderMappingsPanel.repaint();

        panel.revalidate();
        panel.repaint();
    }

    private void populateFeatureMappings(JPanel mappingsPanel, Map<String, Set<String>> mappings) {
        if (mappings == null || mappings.isEmpty()) {
            toggleParentPanelVisibility(mappingsPanel, false);
            return;
        }

        var gc = new GridBag();
        int currentWidth = 0;
        int maxWidth = calculateMaxWidth();
        var rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));

        for (var entry : mappings.entrySet()) {
            var filePath = entry.getKey();
            var features = entry.getValue();

            for (Iterator<String> iterator = features.iterator(); iterator.hasNext(); ) {
                String feature = iterator.next();
                HyperlinkLabel featureLink = new HyperlinkLabel(feature);
                featureLink.addHyperlinkListener(e -> openLink(filePath));

                int featureWidth = featureLink.getPreferredSize().width;

                if (shouldWrapRow(currentWidth, featureWidth, maxWidth)) {
                    mappingsPanel.add(rowPanel, gc.nextLine().next().anchor(GridBagConstraints.LINE_START)
                            .weightx(1.0).fillCellHorizontally());
                    rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
                    currentWidth = 0;
                }

                rowPanel.add(featureLink);
                if (iterator.hasNext()) rowPanel.add(new JBLabel(", "));

                currentWidth += featureWidth + 10;
            }

            if (rowPanel.getComponentCount() > 0) {
                mappingsPanel.add(rowPanel, gc.nextLine().next().anchor(GridBagConstraints.LINE_START).weightx(1.0).fillCellHorizontally());
            }
            toggleParentPanelVisibility(mappingsPanel, true);
        }
    }

    private void toggleParentPanelVisibility(JPanel mappingsPanel, boolean visible) {
        var parent = mappingsPanel.getParent();
        parent.setVisible(visible);
        for (Component component : parent.getComponents()) {
            component.setVisible(visible);
        }
    }

    private int calculateMaxWidth() {
        int width = findingsSummaryLabel.getWidth();
        return (width > 0) ? width : DEFAULT_MAX_WIDTH;
    }

    private boolean shouldWrapRow(int currentWidth, int featureWidth, int maxWidth) {
        return currentWidth + featureWidth + 10 > maxWidth;
    }

    private void openLink(String filePath) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(filePath));
        if (virtualFile != null && this.editor.getProject() != null) {
            FileEditorManager.getInstance(this.editor.getProject()).openFile(virtualFile, true);
        } else {
            JOptionPane.showMessageDialog(null, "File not found: " + filePath);
        }
    }

    private static class MenuAction extends DefaultActionGroup implements HintManagerImpl.ActionToIgnore {
        public MenuAction() {
            add(new ShowLogAction());
            getTemplatePresentation().setPopupGroup(true);
            getTemplatePresentation().setIcon(AllIcons.Actions.More);
        }
    }

    private static class TrafficLightActionButton extends ActionButton {

        public TrafficLightActionButton(@NotNull AnAction action, @Nullable Presentation presentation,
                                        @NotNull String place, @NotNull Dimension minimumSize) {
            super(action, presentation, place, minimumSize);
        }

        @Override
        protected boolean shallPaintDownArrow() {
            return false;
        }
    }
}
