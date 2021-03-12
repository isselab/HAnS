package se.ch.HAnS.codeCompletion;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.patterns.PatternCondition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

class CommentPattern extends PatternCondition<PsiElement> {
    CommentPattern() {
        super("commentPattern()");
    }

    @Override
    public boolean accepts(@NotNull PsiElement psi, ProcessingContext context) {
        Language language = PsiUtilCore.findLanguageFromElement(psi);
        ParserDefinition definition = LanguageParserDefinitions.INSTANCE.forLanguage(language);
        if (definition == null) {
            return false;
        }

        TokenSet tokens = TokenSet.orSet(
                definition.getCommentTokens());

        ASTNode node = psi.getNode();
        if (node == null) {
            return false;
        }

        if (tokens.contains(node.getElementType())) {
            return true;
        }

        return false;
    }
}