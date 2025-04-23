/*
Copyright 2025 Johan Martinson & Manhal Jaseem

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
package se.isselab.HAnS.trafficLight;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.pluginExtensions.MetricsService;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.featureFileMappingTasks.FeatureFileMappingCallback;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class HansTrafficLightAction extends AnAction implements DumbAware, CustomComponentAction {

    private static final Key<HansTrafficLightDashboardModel> DASHBOARD_MODEL = new Key<>("DASHBOARD_MODEL");

    private Editor editor;
    private HansTrafficLightWidget widget;
    private HansTrafficLightPopup popup;

    HansTrafficLightAction() {
        super();
    }

    HansTrafficLightAction(Editor editor) {
        this.editor = editor;
        popup = new HansTrafficLightPopup(editor);
    }


    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        widget = new HansTrafficLightWidget(this, presentation, place, editor, popup);
        return widget;
    }

    @Override
    public void updateCustomComponent(@NotNull JComponent component, Presentation presentation) {
        HansTrafficLightDashboardModel model = presentation.getClientProperty(DASHBOARD_MODEL);
        if (model != null && component instanceof HansTrafficLightWidget trafficLightWidget) {
            trafficLightWidget.refresh(model, popup);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        if (editor.getProject() != null) {
            update(anActionEvent);
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public final void update(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null || !project.isInitialized() || project.isDisposed()) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }

        e.getPresentation().setVisible(true);
        e.getPresentation().setEnabled(true);

        // Update the presentation with model
        updatePresentation(e, project);
    }

    public void updatePresentation(AnActionEvent e, Project project) {
        if (project.isDisposed()) {
            return;
        }

        Presentation presentation = e.getPresentation();
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        if (file != null) {
            var service = project.getService(MetricsService.class);
            if (service != null) {
                service.getAllFeatureFileMappingsBackground(new FeatureFileMappingCallback() {
                    @Override
                    public void onComplete(Map<String, FeatureFileMapping> featureFileMappings) {
                        var currentFilePath = editor.getVirtualFile().getPath();
                        Set<Pair<String, Pair<String,String>>> filePathFeatureMappings = new HashSet<>();

                        featureFileMappings.forEach((key, mapping) -> {
                            var featuresMappedToFiles = mapping.getFilePathFeatureMappings(currentFilePath);

                            if (!featuresMappedToFiles.isEmpty()) {
                                filePathFeatureMappings.addAll(featuresMappedToFiles);
                            }
                        });

                        Map<String, Map<String, Set<String>>> result = filePathFeatureMappings.stream()
                                .collect(Collectors.groupingBy(
                                        pair -> pair.getSecond().getFirst(), // Grouping by String3
                                        Collectors.groupingBy(
                                                pair -> pair.getSecond().getSecond(), // Grouping by String2
                                                Collectors.mapping(
                                                        pair -> pair.getFirst(), // Collect String1
                                                        Collectors.toSet()
                                                )
                                        )
                                ));

                        var model = new HansTrafficLightDashboardModel(true, result);
                        presentation.putClientProperty(DASHBOARD_MODEL, model);

                        ApplicationManager.getApplication().invokeLater(() -> {
                            if (widget != null) {
                                widget.refresh(model, popup);
                                widget.repaint();
                            }
                        });
                    }
                });
            }
        } else {
            // Set a default model if no file is available
            presentation.putClientProperty(DASHBOARD_MODEL, new HansTrafficLightDashboardModel(false));
        }
    }
}
