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
package se.isselab.HAnS.syntaxHighlighting.codeAnnotations;

import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.states.ToggleStateService;

// &begin [SyntaxHighlighting]
public class CodeAnnotationsSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
    @Override
    public @NotNull SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile) {
        ToggleStateService toggleStateService = ToggleStateService.getInstance(project);
        if (toggleStateService.isEnabled()) {
            //System.out.println("CodeAnnotationsSyntaxHighlighterFactory: ToggleStateService is disabled. Supposed to use CodeAnnotationsSyntaxHighlighter.");
            return new CodeAnnotationsSyntaxHighlighter(); //Return regular syntaxHighlighter
        }
        //System.out.println("CodeAnnotationsSyntaxHighlighterFactory: ToggleStateService is enabled or unavailable. Supposed to use NoOpSyntaxHighlighter.");
        return new NoOpSyntaxHighlighter(); //Return syntaxHighlighter that does nothing
    }
    // &end [SyntaxHighlighting]

}