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
package se.isselab.HAnS.featureModel;

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
import se.isselab.HAnS.featureModel.parser.FeatureModelParser;
import se.isselab.HAnS.featureModel.psi.FeatureModelFile;
import se.isselab.HAnS.featureModel.psi.FeatureModelTypes;
import org.jetbrains.annotations.NotNull;

public class FeatureModelParserDefinition implements ParserDefinition {

    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final IFileElementType FILE = new IFileElementType(FeatureModelLanguage.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new FeatureModelLexerAdapter();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new FeatureModelParser();
    }

    // &begin[FeatureModel::File]
    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }
    // &end[FeatureModel::File]

    // &begin[FeatureModel::Language]
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
        return FeatureModelTypes.Factory.createElement(node);
    }
    // &end[FeatureModel::Language]

    // &begin[FeatureModel::File]
    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new FeatureModelFile(viewProvider);
    }
    // &end[FeatureModel::File]

    @Override
    public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY; // &line[FeatureModel::Language]
    }
}
