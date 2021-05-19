package se.ch.HAnS.featureModel.toolWindow;

import com.intellij.find.FindManager;
import com.intellij.icons.AllIcons;
import com.intellij.ide.ui.customization.CustomizationUtil;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
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

    private JButton addButton, removeButton, clearButton;

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
        addButton(btnDimension, btnMargin, addButton, ADD_COMMAND, false);

        removeButton = new JButton(AllIcons.General.Remove);
        addButton(btnDimension, btnMargin, removeButton, REMOVE_COMMAND, false);

        clearButton = new JButton(AllIcons.General.Reset);
        addButton(btnDimension, btnMargin, clearButton, CLEAR_COMMAND, true);

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

    private void addButton(Dimension btnDimension, Insets btnMargin, JButton removeButton, String removeCommand, Boolean enabled) {
        removeButton.setBorderPainted(false);
        removeButton.setContentAreaFilled(false);
        removeButton.setRolloverEnabled(true);
        removeButton.setMargin(btnMargin);
        removeButton.setPreferredSize(btnDimension);
        removeButton.setActionCommand(removeCommand);
        removeButton.addActionListener(this);
        removeButton.setEnabled(enabled);
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

    public void findFeature() {
        PsiElement selected = getSelectedItemAsPsiElement();
        FindManager.getInstance(selected.getProject()).findUsages(selected);
    }

    public void renameFeature() {
        ((FeatureModelFeature) selectedFeature.getUserObject()).renameFeature();
        //RefactoringBundle.message("rename.0.and.its.usages.to", "'" + "Heyyyoooo" + "'");
        /*
        PsiElement selected = getSelectedItemAsPsiElement();
        String s = ((FeatureModelFeatureImpl) selected).renameFeature();
        if (s != null) {
            selectedFeature.setUserObject(s);
            tree.nodeChanged(selectedFeature);
        }*/
    }

    public void addFeature() {
        PsiElement selected = getSelectedItemAsPsiElement();
        String s = null;
        s = ((FeatureModelFeature) selected).addFeature();
        if (s != null) {
            tree.insertNodeInto(new DefaultMutableTreeNode(s), selectedFeature, 0);
        }
    }

    public void deleteFeature() {
        PsiElement selected = getSelectedItemAsPsiElement();
        Integer s = null;
        s = ((FeatureModelFeature) selected).deleteFeature();
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
        return null;
    }

    private void getFeatureNames(PsiFile f) {
        PsiElement r = f.getFirstChild();
        root = new DefaultMutableTreeNode(r.getFirstChild().getText());

        getChildren(r);
    }

    private void getChildren(PsiElement p) {
        for (PsiElement e:p.getChildren()) {

        }
    }
}