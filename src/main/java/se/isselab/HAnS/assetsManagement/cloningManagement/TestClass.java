package se.isselab.HAnS.assetsManagement.cloningManagement;

import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class TestClass extends BasePlatformTestCase {

   @Override
    protected LightProjectDescriptor getProjectDescriptor(){
        return null;
    }

    public void testCloningFile() throws Exception {
       /*
        final PsiFile sourceFile = myFixture.configureByText("SourceFile.java", "public class SourceFile {}");
        assertNotNull(sourceFile);
        final PsiDirectory sourceDirectory = sourceFile.getContainingDirectory();
        assertNotNull(sourceDirectory);
        DataContext dataContext = SimpleDataContext.builder()
                .add(CommonDataKeys.PSI_ELEMENT, sourceFile)
                .add(CommonDataKeys.PROJECT, getProject())
                .build();
        CloneAsset cloneAsset = new CloneAsset();
        AnActionEvent e = AnActionEvent.createFromDataContext("ProjectViewPopup", null, dataContext);
        cloneAsset.actionPerformed(e);

        //pasting process
        VirtualFile directoryVirtualFile = myFixture.getTempDirFixture().findOrCreateDir("src/targetDirectory");
        PsiDirectory targetDirectory = myFixture.getPsiManager().findDirectory(directoryVirtualFile);
        assertNotNull(targetDirectory);

        DataContext pastingContext = SimpleDataContext.builder()
                .add(CommonDataKeys.PSI_ELEMENT, targetDirectory)
                .add(CommonDataKeys.PROJECT, getProject())
                .build();
        PasteClonedAsset pasteClonedAsset = new PasteClonedAsset();
        AnActionEvent pasteEvent = AnActionEvent.createFromDataContext("ProjectViewPopup", null, pastingContext);
        pasteClonedAsset.actionPerformed(pasteEvent);
        PsiFile pastedFile = targetDirectory.findFile("SourceFile.java");
        assertNotNull("The file was not pasted as expected", pastedFile);
        assertEquals("The contents of the pasted file do not match", sourceFile.getText(), pastedFile.getText());


        PsiFile clonedFile = findClonedFile(sourceDirectory, "SourceFile.java"); // Implement this method based on your plugin's logic
        assertNotNull("The file was not cloned as expected", clonedFile);
        assertEquals("The contents of the cloned file do not match", sourceFile.getText(), clonedFile.getText());

        CloneAsset cloneAsset = (CloneAsset) actionManager.getAction("clone");
        final PsiFile sourceFile = myFixture.addFileToProject("src/graphics/Window.java", "public class Window extends JFrame {}");
        final PsiDirectory sourceDirectory = sourceFile.getContainingDirectory();
        assertNotNull(sourceFile);
        assertNotNull(sourceDirectory);
        assertNotNull(targetDirectory);
        cloneAsset.testClone(sourceDirectory, targetDirectory);
        PsiFile clonedFile = targetDirectory.findFile(sourceFile.getName());

        assertNotNull("The file was not cloned as expected", clonedFile);
        assertEquals("The contents of the cloned file do not match the source",
                sourceFile.getText(), clonedFile.getText());
         */
    }
}
