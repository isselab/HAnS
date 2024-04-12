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
package se.isselab.HAnS.folderAnnotation;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.folderAnnotation.parser.FolderAnnotationParser;
import se.isselab.HAnS.folderAnnotation.psi.FolderAnnotationFile;
import se.isselab.HAnS.folderAnnotation.psi.FolderAnnotationTypes;

public class FolderAnnotationParserDefinition implements ParserDefinition {

    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final IFileElementType FILE = new IFileElementType(FolderAnnotationLanguage.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new FolderAnnotationLexerAdapter();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new FolderAnnotationParser();
    }

    // &begin[FolderAnnotation::File]
    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }
    // &end[FolderAnnotation::File]

    // &begin[FolderAnnotation::Language]
    @Override
    public @NotNull TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return TokenSet.EMPTY;
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        return FolderAnnotationTypes.Factory.createElement(node);
    }
    // &end[FolderAnnotation::Language]

    // &begin[FolderAnnotation::File]
    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new FolderAnnotationFile(viewProvider);
    }
    // &end[FolderAnnotation::File]

    @Override
    public SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY; // &line[FolderAnnotation::Language]
    }
}
