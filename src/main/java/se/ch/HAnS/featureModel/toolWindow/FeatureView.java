package se.ch.HAnS.featureModel.toolWindow;

import com.intellij.icons.AllIcons;
import com.intellij.ide.ui.customization.CustomizationUtil;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;
import se.ch.HAnS.featureModel.psi.impl.FeatureModelProjectNameImpl;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static se.ch.HAnS.AnnotationIcons.FileType;

public class FeatureView extends JPanel implements ActionListener{

    private static FeatureView view;

    private Project project;
    private DefaultTreeModel tree;
    private Tree left;
    private DefaultMutableTreeNode root = null;
    private DefaultMutableTreeNode selectedFeature;

    private JButton addButton, removeButton;

    private static final String ADD_COMMAND = "add";
    private static final String REMOVE_COMMAND = "remove";
    private static final String CLEAR_COMMAND = "clear";

    private static Logger LOGGER = Logger.getLogger("FeatureView");

    public FeatureView(Project project) {
        super(new BorderLayout());
        view = this;
        this.project = project;

        getFeatureNames(getFeatureModel());
        tree = new DefaultTreeModel(root);
        left = new Tree(tree);
        left.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        left.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                    left.getLastSelectedPathComponent();

            if (node == null) {
                //Nothing is selected.
                return;
            }

            selectedFeature = node;
            addButton.setEnabled(true);
            removeButton.setEnabled(true);
            LOGGER.log(Level.INFO, "Selected Feature: " + selectedFeature.toString());
        });
        JBScrollPane scrollPane = new JBScrollPane(left);

        add(scrollPane, BorderLayout.CENTER);

        addBottomPanel();

        CustomizationUtil.installPopupHandler(left, "FeatureView", ActionPlaces.getActionGroupPopupPlace("FeatureView"));

        project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                for (VFileEvent e : events) {
                    if (Objects.equals(Objects.requireNonNull(e.getFile()).getExtension(), "feature-model")) {
                        ApplicationManager.getApplication().invokeLater(FeatureView.getView()::clear);
                    }
                }
            }
        });
    }

    public static FeatureView getView() {
        return view;
    }

    private void addBottomPanel() {
        Dimension btnDimension = new Dimension(20, 20);
        Insets btnMargin = JBUI.insets(0,5,0,5);

        addButton = new JButton(AllIcons.General.Add);
        addButton.setBorderPainted(false);
        addButton.setContentAreaFilled(false);
        addButton.setRolloverEnabled(true);
        addButton.setMargin(btnMargin);
        addButton.setPreferredSize(btnDimension);
        addButton.setActionCommand(ADD_COMMAND);
        addButton.addActionListener(this);
        addButton.setEnabled(false);

        removeButton = new JButton(AllIcons.General.Remove);
        removeButton.setBorderPainted(false);
        removeButton.setContentAreaFilled(false);
        removeButton.setRolloverEnabled(true);
        removeButton.setMargin(btnMargin);
        removeButton.setPreferredSize(btnDimension);
        removeButton.setActionCommand(REMOVE_COMMAND);
        removeButton.addActionListener(this);
        removeButton.setEnabled(false);

        JButton clearButton = new JButton(AllIcons.General.Reset);
        clearButton.setBorderPainted(false);
        clearButton.setContentAreaFilled(false);
        clearButton.setRolloverEnabled(true);
        clearButton.setMargin(btnMargin);
        clearButton.setPreferredSize(btnDimension);
        clearButton.setActionCommand(CLEAR_COMMAND);
        clearButton.addActionListener(this);

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
        south.add(buttons, BorderLayout.EAST);

        this.add(south, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (ADD_COMMAND.equals(command)) {
            // Add button clicked
            addFeature();
        } else if (REMOVE_COMMAND.equals(command)) {
            // Remove button clicked
            deleteFeature();
        } else if (CLEAR_COMMAND.equals(command)) {
            // Clear button clicked.
            clear();
        }
    }

    public void renameFeature() {
        PsiElement selected = getSelectedItemAsPsiElement();
        String s = null;
        if (selected instanceof FeatureModelProjectNameImpl) {
            s = ((FeatureModelProjectNameImpl) selected).renameFeature();
        }
        else if (selected instanceof FeatureModelFeatureImpl) {
            s = ((FeatureModelFeatureImpl) selected).renameFeature();
        }
        if (s != null) {
            selectedFeature.setUserObject(s);
            tree.nodeChanged(selectedFeature);
        }
    }

    public void addFeature() {
        PsiElement selected = getSelectedItemAsPsiElement();
        String s = null;
        if (selected instanceof FeatureModelProjectNameImpl) {
            s = ((FeatureModelProjectNameImpl) selected).addFeature();
        }
        else if (selected instanceof FeatureModelFeatureImpl) {
            s = ((FeatureModelFeatureImpl) selected).addFeature();
        }
        if (s != null) {
            tree.insertNodeInto(new DefaultMutableTreeNode(s), selectedFeature, 0);
        }
    }

    public void deleteFeature() {
        PsiElement selected = getSelectedItemAsPsiElement();
        Integer s = null;
        if (selected instanceof FeatureModelProjectNameImpl) {
            s = ((FeatureModelProjectNameImpl) selected).deleteFeature();
        }
        else if (selected instanceof FeatureModelFeatureImpl) {
            s = ((FeatureModelFeatureImpl) selected).deleteFeature();
        }
        if (s == 1) {
            tree.removeNodeFromParent(selectedFeature);
        }
    }

    public void clear() {
        /*
        PsiFile f = getFeatureModel();
        if (f == null) {
            System.out.println("No feature model");
        }
        else {
            getFeatureNames(f);
        }
         */
        getFeatureNames(getFeatureModel());
        tree.reload();
    }

    public PsiFile getFeatureModel() {
        PsiFile[] allFilenames = FilenameIndex.getFilesByName(project, ".feature-model", GlobalSearchScope.projectScope(project));
        PsiFile f = null;
        if (allFilenames.length > 0) {
            f = allFilenames[0];
        }
        else {
            Collection<VirtualFile> c = FilenameIndex.getAllFilesByExt(project, "feature-model");
            if (!c.isEmpty()) {
                f = PsiManager.getInstance(project).findFile(c.iterator().next());
            }
        }
        return f;
    }

    public static List<String> getElementPath(DefaultMutableTreeNode node) {
        List<String> path = new ArrayList<>();
        for (TreeNode n:node.getPath()) {
            path.add(n.toString());
        }
        return path;
    }

    public PsiElement getSelectedItemAsPsiElement(){
        DefaultMutableTreeNode node = selectedFeature;
        List<String> path = getElementPath(node);

        final PsiElement[] result = new PsiElement[1];
        PsiFile f = getFeatureModel();
        if (f != null) {
            f.accept(new PsiRecursiveElementWalkingVisitor() {
                Integer indent = null;

                @Override
                public void visitElement(@NotNull PsiElement element) {
                    if (element instanceof FeatureModelProjectNameImpl && result[0] == null){
                        if (element.getText().equals(path.get(0))){
                            path.remove(0);
                        }
                    }
                    else if (element instanceof FeatureModelFeatureImpl && result[0] == null){
                        if (indent == null) {
                            indent = element.getPrevSibling().getTextLength();
                            if (element.getText().equals(path.get(0))){
                                path.remove(0);
                                indent = null;
                            }
                        }
                        else if (indent >= element.getPrevSibling().getTextLength()) {
                            indent = element.getPrevSibling().getTextLength();
                            if (element.getText().equals(path.get(0))){
                                path.remove(0);
                                indent = null;
                            }
                        }
                    }
                    if (path.isEmpty() && result[0] == null) {
                        result[0] = element;
                    }
                    super.visitElement(element);
                }
            });
        }
        return result[0];
    }

    private void getFeatureNames(PsiFile f) {
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