package se.isselab.HAnS.trafficLight;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.Alarm;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HansTrafficLightPopup {

    private AncestorListenerAdapter onAncestorChangedListener = new AncestorListenerAdapter() {
        @Override
        public void ancestorMoved(AncestorEvent event) {
            hidePopup();
        }
    };

    private Alarm popupAlarm = new Alarm();
    private JBPopup myPopup;
    private boolean insidePopup = false;
    private HansTrafficLightPanel dashboard;

    private Editor editor;

    public HansTrafficLightPopup(Editor editor) {
        this.editor = editor;
        dashboard = new HansTrafficLightPanel(editor);

        dashboard.panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                insidePopup = true;
            }

            @Override
            public void mouseExited(MouseEvent event) {
                Point point = event.getPoint();
                if (!dashboard.panel.getBounds().contains(point) || point.x == 0 || point.y == 0) {
                    insidePopup = false;
                    if (canClose()) {
                        hidePopup();
                    }
                }
            }
        });

    }
    public void scheduleShow(Component target) {
        popupAlarm.cancelAllRequests();
        popupAlarm.addRequest(() -> showPopup(target), Registry.intValue("ide.tooltip.initialReshowDelay"));
    }

    private void showPopup(Component target) {
        hidePopup();
        JPanel myContent = dashboard.panel;

        JBPopupFactory popupFactory = JBPopupFactory.getInstance();
        var myPopupBuilder = popupFactory.createComponentPopupBuilder(myContent, null)
                .setCancelOnClickOutside(true);

        JBPopupListener myPopupListener = new JBPopupListener() {
            @Override
            public void onClosed(LightweightWindowEvent event) {
                editor.getComponent().removeAncestorListener(onAncestorChangedListener);
            }
        };

        myPopup = myPopupBuilder.createPopup();
        myPopup.addListener(myPopupListener);
        editor.getComponent().addAncestorListener(onAncestorChangedListener);

        Dimension size = myContent.getPreferredSize();
        size.width = Math.max(size.width, JBUIScale.scale(296));
        int targetBottom = target.getY() + target.getHeight();
        Point point = new Point(editor.getComponent().getWidth() - 10 - size.width, targetBottom + 5);
        RelativePoint relativePoint = new RelativePoint(editor.getComponent(), point);

        myPopup.setSize(size);
        myPopup.show(relativePoint);
    }

    public void scheduleHide() {
        popupAlarm.cancelAllRequests();
        popupAlarm.addRequest(() -> {
            if (canClose()) {
                hidePopup();
            }
        }, Registry.intValue("ide.tooltip.initialDelay.highlighter"));
    }


    private boolean canClose() {
        return !insidePopup;
    }

    private void hidePopup() {
        if (myPopup != null && !myPopup.isDisposed()) {
            myPopup.cancel();
        }
        myPopup = null;
    }

    public void refresh(HansTrafficLightDashboardModel model) {
        dashboard.refresh(model);
    }

}
