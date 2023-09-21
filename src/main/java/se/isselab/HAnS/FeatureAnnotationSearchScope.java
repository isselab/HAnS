/*
Copyright 2021 Herman Jansson & Johan Martinson

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package se.isselab.HAnS;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.UnloadedModuleDescription;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.DelegatingGlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

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