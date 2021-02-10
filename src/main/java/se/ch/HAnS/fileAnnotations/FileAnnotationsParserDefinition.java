package se.ch.HAnS.fileAnnotations;

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
import se.ch.HAnS.fileAnnotations.FileAnnotationsLanguage;
import se.ch.HAnS.fileAnnotations.FileAnnotationsLexerAdapter;
import se.ch.HAnS.fileAnnotations.parser.FileAnnotationsParser;
import se.ch.HAnS.fileAnnotations.psi.FileAnnotationsFile;
import se.ch.HAnS.fileAnnotations.psi.FileAnnotationsTypes;

public class FileAnnotationsParserDefinition implements ParserDefinition {

    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final IFileElementType FILE = new IFileElementType(FileAnnotationsLanguage.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new FileAnnotationsLexerAdapter();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new FileAnnotationsParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

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
        return FileAnnotationsTypes.Factory.createElement(node);
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new FileAnnotationsFile(viewProvider);
    }

    @Override
    public SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }
}
