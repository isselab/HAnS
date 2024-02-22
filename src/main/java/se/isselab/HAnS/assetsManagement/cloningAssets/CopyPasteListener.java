package se.isselab.HAnS.assetsManagement.cloningAssets;

import com.intellij.openapi.ide.CopyPasteManager;
import org.jetbrains.annotations.Nullable;

import java.awt.datatransfer.Transferable;

public class CopyPasteListener implements CopyPasteManager.ContentChangedListener {
    @Override
    public void contentChanged(@Nullable Transferable transferable, Transferable transferable1) {
        System.out.println(transferable);
    }
}
