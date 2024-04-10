package se.isselab.HAnS.cloneAssetsTests;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.testFramework.fixtures.BareTestFixtureTestCase;
import com.intellij.testFramework.rules.TempDirectory;
import org.jetbrains.annotations.NotNull;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CloneVfsTests extends BareTestFixtureTestCase {
    @Test
    public void testCopyPasteEvent() throws IOException {
        VirtualFile sourceDir = createDir();
        VirtualFile destDir = createDir();
        VirtualFile sourceFile = addChild(sourceDir, "source.txt", false);

        AtomicBoolean copyEventFired = new AtomicBoolean(false);
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                for (VFileEvent event : events) {
                    if (event instanceof VFileCopyEvent && ((VFileCopyEvent) event).getFile().equals(sourceFile)) {
                        copyEventFired.set(true);
                    }
                }
            }
        });

        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                sourceFile.copy(this, destDir, sourceFile.getName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

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
}
