package se.isselab.HAnS.trafficLight;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.ActionButtonLook;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import se.isselab.HAnS.AnnotationIcons;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

public class HansTrafficLightWidget extends JPanel {
    private HansTrafficLightPopup dashboardPopup;
    private MouseListener mouseListener;
    private JLabel iconAndFeatureCountLabel = new JBLabel();
    private boolean mousePressed = false;
    private boolean mouseHover = false;

    private Editor editor;
    private String filePath;

    HansTrafficLightWidget(AnAction action, Presentation presentation,
                           String place, Editor editor) {
        this.editor = editor;
        dashboardPopup = new HansTrafficLightPopup(editor);

        setOpaque(false);
        // &begin[WidgetStyle]

        if (!SystemInfo.isWindows) {
            iconAndFeatureCountLabel.setFont(new FontUIResource(getFont().deriveFont(getFont().getStyle(),
                    (getFont().getSize() - JBUIScale.scale(2)))));
        }

        iconAndFeatureCountLabel.setForeground(new JBColor(
                Objects.requireNonNull(editor.getColorsScheme().getColor(ColorKey.createColorKey("ActionButton.iconTextForeground",
                        UIUtil.getContextHelpForeground()))),
                ColorKey.createColorKey("ActionButton.iconTextForeground", UIUtil.getContextHelpForeground()).getDefaultColor()
        ));
        // &end[WidgetStyle]
        add(iconAndFeatureCountLabel);

        // &begin[ClickAndHover]
        mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                var context = ActionToolbar.getDataContextFor(HansTrafficLightWidget.this);
                var event = AnActionEvent.createFromInputEvent(e, place, presentation, context, false, true);
                ActionUtil.performActionDumbAwareWithCallbacks(action, event);
                mousePressed = false;
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                mouseHover = true;
                repaint();
                dashboardPopup.scheduleShow(HansTrafficLightWidget.this);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseHover = false;
                repaint();
                dashboardPopup.scheduleHide();
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

    @Override
    public void removeNotify() {
        removeMouseListener(mouseListener);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        addMouseListener(mouseListener);
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

    public void refresh(HansTrafficLightDashboardModel model) {
        if (!model.isAlive()) {
            iconAndFeatureCountLabel.setIcon(AnnotationIcons.PluginIcon);
            iconAndFeatureCountLabel.setText("DEAD");
        } else if (model.hasFindings()) {
            iconAndFeatureCountLabel.setIcon(AnnotationIcons.PluginIcon);
            iconAndFeatureCountLabel.setText(String.valueOf(model.findingsCount()));
        }
        else {
            iconAndFeatureCountLabel.setIcon(AnnotationIcons.PluginIcon);
            iconAndFeatureCountLabel.setText(null);
        }
        dashboardPopup.refresh(model);
    }
}
