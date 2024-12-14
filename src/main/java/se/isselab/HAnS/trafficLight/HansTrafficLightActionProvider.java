package se.isselab.HAnS.trafficLight;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.InspectionWidgetActionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HansTrafficLightActionProvider implements InspectionWidgetActionProvider {

    @Override
    public @Nullable AnAction createAction(@NotNull Editor editor) {
        if (editor.getEditorKind() == EditorKind.MAIN_EDITOR) {
            return new DefaultActionGroup(new HansTrafficLightAction(editor), Separator.create());
        }
        return null;
    }
}
