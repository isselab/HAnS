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

package se.isselab.HAnS.metrics.view;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import se.isselab.HAnS.AnnotationIcons;
import se.isselab.HAnS.pluginExtensions.MetricsService;
import se.isselab.HAnS.metrics.ProjectMetrics;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MetricsViewFactory implements ToolWindowFactory {

    JPanel contentPanel;

    public MetricsViewFactory() {
        contentPanel = new JPanel();
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setIcon(AnnotationIcons.FeatureModelIcon);

        contentPanel.setLayout(new BorderLayout());

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(contentPanel, "", false);
        toolWindow.getContentManager().addContent(content);

        toolWindow.setTitleActions(List.of(new AnAction("Refresh Metrics", "Refresh metrics", AllIcons.Actions.Refresh) {
                                               @Override
                                               public void actionPerformed(@NotNull AnActionEvent e) {
                                                   TriggerService(project);
                                               }
                                           }
        ));

        TriggerService(project);
    }

    private void TriggerService(Project project) {
        MetricsService service = project.getService(MetricsService.class);

        if (!DumbService.isDumb(project)) {
            service.getProjectMetricsBackground(metrics -> refreshTableContent(contentPanel, metrics, service));
        } else {
            DumbService.getInstance(project).runWhenSmart(() -> service.getProjectMetricsBackground(metrics -> refreshTableContent(contentPanel, metrics, service)));
        }
    }


    private void refreshTableContent(JPanel contentPanel, ProjectMetrics metrics, MetricsService service) {
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[]{"Feature", "Scattering Degree", "Tangling Degree", "Lines of Feature-code",
                        "Avg Nesting Depth", "Max Nesting Depth", "Min Nesting Depth",
                "Annotated Files", "Folder Annotations", "File Annotations"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Set appropriate classes for sorting
                return switch (columnIndex) {
                    case 1, 2, 3, 5, 6, 7, 8, 9 -> Integer.class; // Use Integer class for numerical sorting
                    case 4 -> Double.class;
                    default -> Object.class; // Default class
                };
            }
        };
        contentPanel.removeAll();
        JBTable table = new JBTable(tableModel);
        JBScrollPane scrollPanel = new JBScrollPane(table);
        contentPanel.add(scrollPanel, BorderLayout.CENTER);

        table.setRowSorter(createSorters(tableModel));
        populateTable(table, metrics, service);

        table.setDefaultRenderer(Object.class, new IntegerTableCellRenderer());
        table.setAutoCreateRowSorter(true);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private TableRowSorter<DefaultTableModel> createSorters(DefaultTableModel tableModel) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        Comparator<Integer> integerComparator = Comparator.comparingInt(Integer::intValue);
        sorter.setComparator(1, integerComparator); // Scattering Degree column
        sorter.setComparator(2, integerComparator); // Tangling Degree column
        sorter.setComparator(3, integerComparator); // Line Count column
        sorter.setComparator(4, Comparator.comparingDouble(Double::doubleValue)); // AvgND
        sorter.setComparator(5, integerComparator); // MaxND
        sorter.setComparator(6, integerComparator); // MinND
        sorter.setComparator(7, integerComparator); // NumberOfAnnotatedFiles
        sorter.setComparator(8, integerComparator); // NumberOfFolderAnnotations
        sorter.setComparator(9, integerComparator); // NumberOfFileAnnotations

        return sorter;
    }

    private void populateTable(JBTable table, ProjectMetrics metrics, MetricsService service) {
        for (var featureFileMapping : metrics.getFeatureFileMappings().values()) {
            var feature = featureFileMapping.getFeature();
            if (service.isRootFeature(feature)) continue;

            ((DefaultTableModel) table.getModel()).addRow(new Object[]{feature.getLPQText(), feature.getScatteringDegree(),
                    feature.getTanglingDegree(), feature.getLineCount(), feature.getAvgNestingDepth(), feature.getMaxNestingDepth(),
                    feature.getMinNestingDepth(), feature.getNumberOfAnnotatedFiles(), feature.getNumberOfFolderAnnotations(), feature.getNumberOfFileAnnotations()});
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
