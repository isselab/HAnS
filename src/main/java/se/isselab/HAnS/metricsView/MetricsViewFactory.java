/*
Copyright 2024 Luca Kramer & Johan Martinson

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

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import se.isselab.HAnS.AnnotationIcons;
import se.isselab.HAnS.featureExtension.FeatureServiceInterface;
import se.isselab.HAnS.metrics.FeatureMetrics;
import se.isselab.HAnS.metrics.FeatureScattering;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class MetricsViewFactory implements ToolWindowFactory {

    JPanel contentPanel;
    public MetricsViewFactory() {
        contentPanel = new JPanel();
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setIcon(AnnotationIcons.FeatureModelIcon);
        FeatureServiceInterface service = project.getService(FeatureServiceInterface.class);

        toolWindow.setTitleActions(List.of(new AnAction("Refresh Metrics", "Refresh metrics", AllIcons.Actions.Refresh) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                contentPanel.removeAll();
                service.getFeatureMetricsBackground(metrics -> {
                    addContent(toolWindow, contentPanel);

                    refreshTableContent(contentPanel, metrics, service);
                });
            }

        }));

        service.getFeatureMetricsBackground(metrics -> {
            addContent(toolWindow, contentPanel);

            refreshTableContent(contentPanel, metrics, service);
        });
    }


    private void addContent(ToolWindow toolWindow, JPanel contentPanel) {
        contentPanel.setLayout(new BorderLayout());

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(contentPanel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private void refreshTableContent(JPanel contentPanel, FeatureMetrics metrics, FeatureServiceInterface service) {
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Feature", "Scattering Degree", "Tangling Degree", "Line Count"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Set appropriate classes for sorting
                return switch (columnIndex) {
                    case 1, 2, 3 ->
                            Integer.class; // Use Integer class for numerical sorting
                    default -> Object.class; // Default class
                };
            }
        };
        JBTable table = new JBTable(tableModel);

        table.setRowSorter(createSorters(tableModel));

        populateTable(table, metrics, service);

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

    private void populateTable(JBTable table, FeatureMetrics metrics, FeatureServiceInterface service) {
        for (var featureFileMapping: metrics.getFeatureFileMappings().values()){
            var feature = featureFileMapping.getFeature();
            if (service.isRootFeature(feature)) continue;
            var featureName = feature.getLPQText();

            int featureScatteringDegree = FeatureScattering.getScatteringDegree(featureFileMapping);
            int featureTanglingDegree = metrics.getTanglingMap().containsKey(feature)? metrics.getTanglingMap().get(feature).size() : 0;
            int featureLineCount = featureFileMapping.getTotalFeatureLineCount();

            ((DefaultTableModel) table.getModel()).addRow(new Object[]{featureName, featureScatteringDegree, featureTanglingDegree, featureLineCount});
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
