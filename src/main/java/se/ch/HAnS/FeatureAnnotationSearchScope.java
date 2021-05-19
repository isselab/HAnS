package se.ch.HAnS;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.UnloadedModuleDescription;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.DelegatingGlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.fileAnnotation.FileAnnotationFileType;

import java.util.Collection;

public class FeatureAnnotationSearchScope extends GlobalSearchScope {

    private final FileIndexFacade myFileIndexFacade;
    private final Project p;

    public FeatureAnnotationSearchScope(Project project) {
        super(project);
        this.p = project;
        myFileIndexFacade = FileIndexFacade.getInstance(project);
    }

    @Override
    public boolean isSearchInModuleContent(@NotNull Module aModule) {
        return true;
    }

    public GlobalSearchScope restrictedByFileType() {
        return new DelegatingGlobalSearchScope(GlobalSearchScope.allScope(p));
    }

    @Override
    public boolean contains(@NotNull VirtualFile file) {
        // even if isSearchInLibraries returns false, the check for isInLibraryXXX is still needed
        return !myFileIndexFacade.isExcludedFile(file) &&
                !myFileIndexFacade.isInLibraryClasses(file) &&
                !myFileIndexFacade.isInLibrarySource(file);
    }
    @Override
    public boolean isSearchInLibraries() {
        return false;
    }

    @NotNull
    @Override
    public Collection<UnloadedModuleDescription> getUnloadedModulesBelongingToScope() {
        return myFileIndexFacade.getUnloadedModuleDescriptions();
    }
}