package se.ch.HAnS.featureModel.toolWindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.treeStructure.Tree;
import jnr.ffi.annotations.In;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;
import se.ch.HAnS.featureModel.psi.impl.FeatureModelProjectNameImpl;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FeatureView extends JPanel {

    private Project project;
    private DefaultMutableTreeNode root;

    public FeatureView(Project project) {
        super(new BorderLayout());
        this.project = project;

        getFeatureNames();

        DefaultTreeModel tree = new DefaultTreeModel(root);

        Tree left = new Tree(tree);

        add(new JScrollPane(left));
    }

    private void getFeatureNames() {
        PsiFile[] allFilenames = FilenameIndex.getFilesByName(project, ".feature-model", GlobalSearchScope.projectScope(project));
        PsiFile f;
        if (allFilenames.length > 0) {
            f = allFilenames[0];
        }
        else {
            Collection<VirtualFile> c = FilenameIndex.getAllFilesByExt(project, "feature-model");
            f = PsiManager.getInstance(project).findFile(c.iterator().next());
        }

        if (f != null) {
            f.accept(new PsiRecursiveElementWalkingVisitor() {
                DefaultMutableTreeNode current;
                int indent = 1;

                IndentedNode no;

                @Override
                public void visitElement(@NotNull PsiElement element) {
                    if (element instanceof FeatureModelProjectNameImpl){
                        root = new DefaultMutableTreeNode(element.getText());
                        current = root;

                        no = new IndentedNode(null, 0);
                    }
                    else if (element instanceof FeatureModelFeatureImpl) {
                        if (element.getPrevSibling().getText().length() > indent) {
                            indent = element.getPrevSibling().getText().length();
                            DefaultMutableTreeNode n = new DefaultMutableTreeNode(element.getText());
                            current.add(n);
                            current = n;

                            no = new IndentedNode(no, element.getPrevSibling().getText().length());
                        }
                        else if (element.getPrevSibling().getText().length() == indent) {
                            current.add(new DefaultMutableTreeNode(element.getText()));

                            no = new IndentedNode(no.getParent(), element.getPrevSibling().getText().length());
                        }
                        else {
                            int i = no.getParent().getIndent();
                            if (element.getPrevSibling().getText().length() == i) {

                            }
                            indent = element.getPrevSibling().getText().length();
                        }
                    }
                    super.visitElement(element);
                }
            });
        }
    }

    private class IndentedNode {
        private IndentedNode parent;
        private int indent;

        public IndentedNode(IndentedNode p, int i) {
            this.parent = p;
            this.indent = i;
        }

        private IndentedNode getParent() {
            return this.parent;
        }

        private int getIndent() {
            return this.indent;
        }
    }

}