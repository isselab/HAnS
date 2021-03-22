package se.ch.HAnS.featureModel.toolWindow;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;
import se.ch.HAnS.featureModel.psi.impl.FeatureModelProjectNameImpl;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import static se.ch.HAnS.AnnotationIcons.FileType;

public class FeatureView extends JPanel implements ActionListener {

    private Project project;
    private DefaultTreeModel tree;
    private DefaultMutableTreeNode root;
    private JBTextField newFeature;

    private static String ADD_COMMAND = "add";
    private static String REMOVE_COMMAND = "remove";
    private static String CLEAR_COMMAND = "clear";

    private static Logger LOGGER = Logger.getLogger("FeatureView");

    public FeatureView(Project project) {
        super(new BorderLayout());
        this.project = project;

        getFeatureNames();
        Dimension btnDimension = new Dimension(20, 20);
        Insets btnMargin = JBUI.insets(0,5,0,5);

        JButton addButton = new JButton(AllIcons.General.Add);
        addButton.setBorderPainted(false);
        addButton.setContentAreaFilled(false);
        addButton.setRolloverEnabled(true);
        addButton.setMargin(btnMargin);
        addButton.setPreferredSize(btnDimension);
        addButton.setActionCommand(ADD_COMMAND);
        addButton.addActionListener(this);

        JButton removeButton = new JButton(AllIcons.General.Remove);
        removeButton.setBorderPainted(false);
        removeButton.setContentAreaFilled(false);
        removeButton.setRolloverEnabled(true);
        removeButton.setMargin(btnMargin);
        removeButton.setPreferredSize(btnDimension);
        removeButton.setActionCommand(REMOVE_COMMAND);
        removeButton.addActionListener(this);

        JButton clearButton = new JButton(AllIcons.General.Reset);
        clearButton.setBorderPainted(false);
        clearButton.setContentAreaFilled(false);
        clearButton.setRolloverEnabled(true);
        clearButton.setMargin(btnMargin);
        clearButton.setPreferredSize(btnDimension);
        clearButton.setActionCommand(CLEAR_COMMAND);
        clearButton.addActionListener(this);

        newFeature = new JBTextField();
        newFeature.setEditable(true);
        newFeature.setVisible(true);

        tree = new DefaultTreeModel(root);
        Tree left = new Tree(tree);
        add(new JScrollPane(left), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());

        JPanel buttons = new JPanel(new GridLayout(0,3));
        buttons.add(addButton);
        buttons.add(removeButton);
        buttons.add(clearButton);

        JButton FMIcon = new JButton(FileType);
        FMIcon.setMargin(btnMargin);
        FMIcon.setPreferredSize(btnDimension);
        FMIcon.setBorderPainted(false);
        FMIcon.setContentAreaFilled(false);

        south.add(FMIcon,BorderLayout.WEST);
        south.add(newFeature, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.EAST);

        add(south, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (ADD_COMMAND.equals(command)) {
            // Add button clicked
            LOGGER.log(Level.INFO, newFeature.getText());


            addObject(null);
        } else if (REMOVE_COMMAND.equals(command)) {
            // Remove button clicked
            removeCurrentNode();
        } else if (CLEAR_COMMAND.equals(command)) {
            // Clear button clicked.
            clear();
        }
    }

    public DefaultMutableTreeNode addObject(Object child) {
        return null;
    }

    public void removeCurrentNode() {
        return;
    }

    public void clear() {
        getFeatureNames();
        tree.reload();
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
                int indent = 0;

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
                            DefaultMutableTreeNode p = (DefaultMutableTreeNode)current.getParent();
                            DefaultMutableTreeNode n = new DefaultMutableTreeNode(element.getText());
                            p.add(n);
                            current = n;

                            no = new IndentedNode(no.getParent(), element.getPrevSibling().getText().length());
                        }
                        else {
                            indent = element.getPrevSibling().getText().length();

                            while (no.getIndent() > indent) {
                                current = (DefaultMutableTreeNode)current.getParent();
                                no = no.getParent();

                                if (no.getIndent() == indent) {
                                    DefaultMutableTreeNode p = (DefaultMutableTreeNode)current.getParent();
                                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(element.getText());
                                    p.add(n);
                                    current = n;

                                    no = new IndentedNode(no.getParent(), element.getPrevSibling().getText().length());
                                }
                                else if (no.getIndent() < indent) {
                                    DefaultMutableTreeNode n = new DefaultMutableTreeNode(element.getText());
                                    current.add(n);
                                    current = n;

                                    no = new IndentedNode(no, element.getPrevSibling().getText().length());
                                }
                            }
                        }
                    }
                    super.visitElement(element);
                }
            });
        }
    }

    private static class IndentedNode {
        private final IndentedNode parent;
        private final int indent;

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