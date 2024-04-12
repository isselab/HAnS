package se.isselab.HAnS.cloneAssetsTests;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffManager;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.util.DiffUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.Before;
import org.junit.Test;

public class DiffViewerTests extends BasePlatformTestCase {
    private Project mockProject;
    private VirtualFile mockSourceFile;
    private VirtualFile mockClonedFile;
    private DiffContentFactory mockContentFactory;
    private DiffContent mockSourceFileContent;
    private DiffContent mockClonedFileContent;
    private DiffManager mockDiffManager;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testOpenMergeWindow() {

    }
}
