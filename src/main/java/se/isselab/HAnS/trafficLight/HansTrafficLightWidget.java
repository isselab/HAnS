package se.isselab.HAnS.trafficLight;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ActionButtonLook;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import se.isselab.HAnS.AnnotationIcons;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.featureFileMappingTasks.FeatureFileMappingCallback;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.featureFileMappingTasks.GetFeatureFileMappings;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class HansTrafficLightWidget extends JPanel {
    private AnAction action;
    private Presentation presentation;
    private MouseListener mouseListener;
    private boolean mousePressed = false;
    private boolean mouseHover = false;
    private JLabel hansIcon;
    private Editor editor;
    private String place;
    private String filePath;
    private Set<String> featuresInFile = new HashSet<>();

    HansTrafficLightWidget(AnAction action, Presentation presentation,
                           String place, Editor editor) {
        this.action = action;
        this.presentation = presentation;
        this.place = place;
        this.editor = editor;
        setOpaque(false);
        // &begin[WidgetStyle]
        hansIcon = new JLabel();

        if (!SystemInfo.isWindows) {
            hansIcon.setFont(new FontUIResource(getFont().deriveFont(getFont().getStyle(),
                    (float) (getFont().getSize() - JBUIScale.scale(2)))));
        }

        hansIcon.setForeground(new JBColor(
                Objects.requireNonNull(editor.getColorsScheme().getColor(ColorKey.createColorKey("ActionButton.iconTextForeground",
                        UIUtil.getContextHelpForeground()))),
                ColorKey.createColorKey("ActionButton.iconTextForeground", UIUtil.getContextHelpForeground()).getDefaultColor()
        ));

        hansIcon.setIcon(AnnotationIcons.PluginIcon);
        // &end[WidgetStyle]
        hansIcon.setVisible(false);

        searchFeatures();

        add(hansIcon);
        // &begin[ClickAndHover]
        mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                if (hansIcon.isVisible()) {
                    repaint();
                }

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                var context = ActionToolbar.getDataContextFor(HansTrafficLightWidget.this);
                var event = AnActionEvent.createFromInputEvent(e, place, presentation, context, false, true);
                ActionUtil.performActionDumbAwareWithCallbacks(action, event);
                mousePressed = false;
                if (hansIcon.isVisible()) {
                    repaint();
                    openMappingsFile();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                mouseHover = true;
                if (hansIcon.isVisible()) {
                    repaint();
                    showPopupWithMappingName();
                }

            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseHover = false;
                if (hansIcon.isVisible()) {
                    repaint();
                }
            }
        };

        setBorder(new Border() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                // Empty border
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return JBUI.insets(0, 2);
            }

            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        });

        if (editor.getProject() != null) {
            var disposable = Disposer.newDisposable();
            EditorUtil.disposeWithEditor(editor, disposable);
        }
        // &end[ClickAndHover]
    }

    // &begin[SearchFeatures]
    private void searchFeatures() {
        new GetFeatureFileMappings(this.editor.getProject(), "Find Feature File Mappings", new FeatureFileMappingCallback() {
            @Override
            public void onComplete(Map<String, FeatureFileMapping> featureFileMappings) {
                var currentFilePath = editor.getVirtualFile().getPath();
                Set<String> filePathsOfMappingFiles = new HashSet<>();
                featuresInFile = new HashSet<>();

                featureFileMappings.forEach((key, mapping) -> {
                    var featuresMappedToFiles = mapping.getFileMappingPairsForFile(currentFilePath);
                    if (!featuresMappedToFiles.isEmpty()) {
                        featuresInFile.addAll(mapping.getFeaturesMappedInFile(currentFilePath));
                        filePathsOfMappingFiles.addAll(featuresMappedToFiles.stream().map(x -> x.second)
                                .collect(Collectors.toSet()));
                    }
                });

                if (!featuresInFile.isEmpty() && !filePathsOfMappingFiles.isEmpty()) {
                    hansIcon.setText(String.valueOf(featuresInFile.size()));
                    hansIcon.setVisible(true);
                    if (filePathsOfMappingFiles.size() == 1) filePath = filePathsOfMappingFiles.iterator().next();
                    else {
                        // TODO handle several filePaths
                        System.out.println(String.join(" --- ", filePathsOfMappingFiles.stream().toList()));
                    }
                }
            }
        }).queue();
    }
    // &end[SearchFeatures]

    @Override
    public void removeNotify() {
        removeMouseListener(mouseListener);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        addMouseListener(mouseListener);
    }

    // &begin[HoverPopupStyle]
    private void showPopupWithMappingName() {
        Notification notification = new Notification(
                "notification",
                "Mapped features",
                String.join(", ", featuresInFile),
                NotificationType.INFORMATION
        );
        Notifications.Bus.notify(notification);
    }

    // &end[HoverPopupStyle]
    private void openMappingsFile() {
        VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(filePath));
        if (virtualFile != null && this.editor.getProject() != null) {
            FileEditorManager.getInstance(this.editor.getProject()).openFile(virtualFile, true);
        } else {
            JOptionPane.showMessageDialog(null, "File not found: " + filePath);
        }
    }

    // &begin[WidgetStyle]
    @Override
    protected void paintComponent(Graphics graphics) {
        if (filePath == null) return;
        int state = mousePressed ? ActionButtonComponent.PUSHED
                : mouseHover ? ActionButtonComponent.POPPED
                : ActionButtonComponent.NORMAL;
        if (state == ActionButtonComponent.NORMAL) return;

        var rect = new Rectangle(getSize());
        JBInsets.removeFrom(rect, JBUI.insets(2));

        Color color = (state == ActionButtonComponent.PUSHED)
                ? JBUI.CurrentTheme.ActionButton.pressedBackground()
                : JBUI.CurrentTheme.ActionButton.hoverBackground();

        ActionButtonLook.SYSTEM_LOOK.paintLookBackground(graphics, rect, color);
    }
    // &end[WidgetStyle]
}
