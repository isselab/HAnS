package se.isselab.HAnS.assetsManagementTests.cloneAssetsTests;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightPlatformCodeInsightTestCase;
import com.intellij.testFramework.VfsTestUtil;
import com.intellij.testFramework.rules.TempDirectory;
import org.junit.Rule;
import org.junit.Test;
import se.isselab.HAnS.assetsManagement.CloneManagementSettingsState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;

public class CloneVfsTest extends LightPlatformCodeInsightTestCase {
    @Rule
    public TempDirectory tempDir = new TempDirectory();
    VirtualFile featureModel;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
        settingsState.prefKey = "All";
        featureModel = VfsTestUtil.createFile(getProject().getBaseDir(), ".feature-model");
    }

    @Test
    public void testCopyJavaVirtualFile() throws IOException {
        VirtualFile sourceDir = createDir();
        VirtualFile destDir = createDir();
        VirtualFile sourceFile = addChild(sourceDir, "source.java", false);
        String javaContent = "String test = \"test\"; // &line[Test]\n" +
                "\n" +
                "    // &begin[Test]\n" +
                "    public void test(){\n" +
                "        System.out.println(\"test\");\n" +
                "    }\n" +
                "    // &end[Test]";
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                sourceFile.setBinaryContent(javaContent.getBytes());
                sourceFile.copy(this, destDir, sourceFile.getName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        VirtualFile copiedFile = destDir.findChild(sourceFile.getName());
        assertNotNull("Copied file should not be null", copiedFile);
        assertEquals("Content of the copied file should match", javaContent, VfsUtilCore.loadText(copiedFile));
        assertEquals("File extension should be .java", "java", copiedFile.getExtension());
        verifyCloneResult();
        verifyTraceParsing();
    }

    private VirtualFile addChild(VirtualFile dir, String name, boolean directory) throws IOException {
        return WriteAction.computeAndWait(() -> {
            if (directory) {
                return dir.createChildDirectory(this, name);
            } else {
                return dir.createChildData(this, name);
            }
        });
    }

    private VirtualFile createDir() throws IOException {
        File dir = Files.createTempDirectory("vDir").toFile();
        VirtualFile vDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(dir);
        if (vDir != null) {
            vDir.getChildren();
            return vDir;
        } else {
            throw new IOException("Failed to create or find virtual directory.");
        }
    }
    private void verifyCloneResult() throws IOException {
        String file = getProject().getBasePath() + "/.trace-db.txt";
        VirtualFile traceFile = VfsTestUtil.findFileByCaseSensitivePath(file);
        String content = VfsUtilCore.loadText(traceFile);
        assertNotNull("Trace File was not created", traceFile);
        assertTrue("Trace was not stored", !content.isEmpty());
        assertTrue(content.contains("UNASSIGNED"));
    }
    private void verifyTraceParsing() throws IOException {
        String file = getProject().getBasePath() + "/.trace-db.txt";
        VirtualFile traceFile = VfsTestUtil.findFileByCaseSensitivePath(file);
        String content = VfsUtilCore.loadText(traceFile);
        String[] lines = content.split("\n");
        Pattern pattern = Pattern.compile("^[^;]+;[^;]+(?:;[^;]+)?$");
        for (String line : lines) {
            line = line.trim();
            if(line.equals(""))
                continue;
            boolean matches = pattern.matcher(line).matches();
            assertTrue("Line does not match the expected format: " + line, matches);
        }
    }
}
