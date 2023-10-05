/**
 Copyright 2023 Johan Martinson & Herman Jansson

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 **/

package se.isselab.HAnS.unassignedFeature;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureModel.FeatureModelFileType;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.FeatureModelFile;

import java.util.Collection;

// An Intention Action
public class UnassignedFeatureQuickFix extends BaseIntentionAction {
    private final String key;

    public UnassignedFeatureQuickFix(String key) {
        this.key = key;
    }

    @NotNull
    @Override
    public String getText() {
        return "Add New Feature '" + key + "' in Feature Model";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return "Add New Feature in FeatureModel";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull final Project project, final Editor editor, PsiFile file) throws
            IncorrectOperationException {
        ApplicationManager.getApplication().invokeLaterOnWriteThread(() -> {
            Collection<VirtualFile> virtualFiles =
                    FileTypeIndex.getFiles(FeatureModelFileType.INSTANCE, GlobalSearchScope.allScope(project));
            if (virtualFiles.size() == 1) {
                createFeatureInFeatureModel(project, virtualFiles.iterator().next());
            } else {
                final FileChooserDescriptor descriptor =
                        FileChooserDescriptorFactory.createSingleFileDescriptor(FeatureModelFileType.INSTANCE);
                descriptor.setRoots(ProjectUtil.guessProjectDir(project));
                final VirtualFile file1 = FileChooser.chooseFile(descriptor, project, null);
                if (file1 != null) {
                    createFeatureInFeatureModel(project, file1);
                }
            }
        });
    }

    // with top-level feature "UNASSIGNED"
    private void createFeatureInFeatureModel(final Project project, final VirtualFile file) {
        WriteCommandAction.writeCommandAction(project).run(() -> {
            FeatureModelFile featureModelFileFile = (FeatureModelFile) PsiManager.getInstance(project).findFile(file);
            ASTNode[] children = featureModelFileFile.getNode().getChildren(null);
            ASTNode unassignedNode = null;
            boolean exist = false;
            for (ASTNode child : children) {
                if (child.getText().contains("UNASSIGNED")) {
                    unassignedNode = child;
                    exist = true;
                }
            }
            if (!exist) {
                ASTNode lastChildNode = featureModelFileFile.getNode().getLastChildNode();
                if (lastChildNode != null) { // && !lastChildNode.getElementType().equals(SimpleTypes.CRLF)
                    featureModelFileFile.getNode().addChild(UnassignedFeatureFactory.createCRLF(project).getNode());
                }
                FeatureModelFeature unassigned = UnassignedFeatureFactory.createFeature(project, "UNASSIGNED".replaceAll(" ",
                        "\\\\ "));
                unassignedNode = unassigned.getNode();
                featureModelFileFile.getNode().addChild(unassignedNode);
                unassignedNode.addChild(UnassignedFeatureFactory.createCRLF(project).getNode());
            }
            // IMPORTANT: change spaces to escaped spaces or the new node will only have the first word for the key
            FeatureModelFeature property = UnassignedFeatureFactory.createFeature(project, key.replaceAll(" ",
                    "\\\\ "));
            unassignedNode.addChild(property.getNode());
            property.getNode().addChild(UnassignedFeatureFactory.createCRLF(project).getNode());
            unassignedNode.addChild(UnassignedFeatureFactory.createPlace(project).getNode(), property.getNode());
            ((Navigatable) property.getLastChild().getNavigationElement()).navigate(true);
            FileEditorManager.getInstance(project).getSelectedTextEditor().getCaretModel().moveCaretRelatively(2,
                    0, false, false, false);
        });
    }
}