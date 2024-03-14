/*
Copyright 2024 Luca Kramer

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package se.isselab.HAnS.metricsView;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import se.isselab.HAnS.AnnotationIcons;
import se.isselab.HAnS.featureExtension.FeatureService;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.metrics.FeatureScattering;
import se.isselab.HAnS.metrics.FeatureTangling;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.List;

public class MetricsViewFactory implements ToolWindowFactory {

    private JButton triggerButton;
    private JPanel contentPanel;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setIcon(AnnotationIcons.PluginIcon);
        createButton(project, toolWindow);
        addContent(toolWindow);
    }

    private void createButton(Project project, ToolWindow toolWindow) {
        triggerButton = new JButton("Refresh Metrics");
        triggerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Run the task in the background
                ProgressManager.getInstance().run(new Task.Backgroundable(project, "Refreshing metrics") {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        refreshTableContent(project, toolWindow);
                    }
                });
            }
        });
    }

    private void addContent(ToolWindow toolWindow) {
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(triggerButton);
        contentPanel.add(buttonPanel, BorderLayout.NORTH);

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(contentPanel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private void refreshTableContent(Project project, ToolWindow toolWindow) {
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Feature", "Scattering Degree", "Tangling Degree", "Line Count"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Set appropriate classes for sorting
                switch (columnIndex) {
                    case 1: // Scattering Degree column
                    case 2: // Tangling Degree column
                    case 3: // Line Count column
                        return Integer.class; // Use Integer class for numerical sorting
                    default:
                        return Object.class; // Default class
                }
            }
        };
        JBTable table = new JBTable(tableModel);

        table.setRowSorter(createSorters(tableModel));

        FeatureService featureService = project.getService(FeatureService.class);
        List<FeatureModelFeature> features = featureService.getFeatures();
        List<FeatureModelFeature> root = featureService.getRootFeatures();

        populateTable(project, table, features, root, featureService);

        table.setDefaultRenderer(Object.class, new IntegerTableCellRenderer());
        table.setAutoCreateRowSorter(true);
        JBScrollPane scrollPanel = new JBScrollPane(table);

        contentPanel.add(scrollPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private TableRowSorter<DefaultTableModel> createSorters(DefaultTableModel tableModel) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        Comparator<Integer> integerComparator = Comparator.comparingInt(Integer::intValue);
        sorter.setComparator(1, integerComparator); // Scattering Degree column
        sorter.setComparator(2, integerComparator); // Tangling Degree column
        sorter.setComparator(3, integerComparator); // Line Count column

        return sorter;
    }

    private void populateTable(Project project, JBTable table, List<FeatureModelFeature> features, List<FeatureModelFeature> root, FeatureService featureService) {
        for (FeatureModelFeature feature : features) {
            String featureName = feature.getFeatureName();
            FeatureFileMapping featureFileMapping = featureService.getFeatureFileMapping(feature);

            if(!root.contains(feature)){
                int featureScatteringDegree = FeatureScattering.getScatteringDegree(project, feature);
                int featureTanglingDegree = FeatureTangling.getFeatureTanglingDegree(project, feature);
                int featureLineCount = featureService.getTotalFeatureLineCount(featureFileMapping);

                ((DefaultTableModel) table.getModel()).addRow(new Object[]{featureName, featureScatteringDegree, featureTanglingDegree, featureLineCount});
            }
        }
    }

    private static class IntegerTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Integer) {
                value = String.valueOf(value);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
