package se.isselab.HAnS.cloneAssetsTests;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.VfsTestUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.Before;
import org.junit.Test;
import se.isselab.HAnS.assetsManagement.cloneManagement.NotificationProvider;

public class DiffViewerTests extends BasePlatformTestCase {
    private VirtualFile mockSourceFile;
    private VirtualFile mockClonedFile;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testOpenMergeWindow() {
        mockSourceFile = VfsTestUtil.createFile(myFixture.getProject().getBaseDir(), "SourceFile.java");
        mockClonedFile = VfsTestUtil.createFile(myFixture.getProject().getBaseDir(), "ClonedFile.java");
        myFixture.openFileInEditor(mockSourceFile);
        myFixture.openFileInEditor(mockClonedFile);
        NotificationProvider notif = new NotificationProvider();
        notif.openMergeWindow(myFixture.getProject(), mockSourceFile, mockClonedFile);
        assertTrue(true);
    }
}
