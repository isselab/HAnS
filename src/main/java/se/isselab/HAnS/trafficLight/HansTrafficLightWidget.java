package se.isselab.HAnS.trafficLight;

import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.ActionButtonLook;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.JBColor;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import se.isselab.HAnS.AnnotationIcons;
import com.intellij.openapi.actionSystem.ActionButtonComponent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Objects;

public class HansTrafficLightWidget extends JPanel {
    private AnAction action;
    private Presentation presentation;
    private MouseListener mouseListener;
    private boolean mousePressed = false;
    private boolean mouseHover = false;
    private JLabel hansIcon;
    private Editor editor;
    private String place;

    HansTrafficLightWidget(AnAction action, Presentation presentation,
                           String place, Editor editor){
        setOpaque(false);

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

        hansIcon.setVisible(false);

        add(hansIcon);



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
                openMappingsFile();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                mouseHover = true;
                repaint();
                showPopupWithMappingName();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseHover = false;
                repaint();
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

    private void showPopupWithMappingName() {
    }

    private void openMappingsFile() {

    }

    @Override
    protected void paintComponent(Graphics graphics) {
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
}
