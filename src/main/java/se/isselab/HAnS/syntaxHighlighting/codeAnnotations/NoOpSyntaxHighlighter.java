package se.isselab.HAnS.syntaxHighlighting.codeAnnotations;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.LexerPosition;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * A No-Operation (NoOp) Syntax Highlighter that does not apply any highlighting.
 */
public class NoOpSyntaxHighlighter extends SyntaxHighlighterBase {

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        // Return an empty lexer that processes the text but produces no tokens.
        return new EmptyLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        // Return an empty array for all token types, ensuring no highlighting.
        return EMPTY_KEYS;
    }

    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    /**
     * An empty lexer implementation that produces no tokens.
     */
    private static class EmptyLexer extends Lexer {
        private CharSequence buffer = "";
        private int startOffset;
        private int endOffset;
        private int currentOffset;

        @Override
        public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
            this.buffer = buffer;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.currentOffset = startOffset;
        }

        @Override
        public int getState() {
            return 0; // No state is maintained.
        }

        @Override
        public IElementType getTokenType() {
            return null; // No tokens are produced.
        }

        @Override
        public int getTokenStart() {
            return currentOffset;
        }

        @Override
        public int getTokenEnd() {
            return currentOffset;
        }

        @Override
        public void advance() {
            currentOffset = endOffset; // Move to the end, finishing token iteration.
        }

        @Override
        public @NotNull CharSequence getBufferSequence() {
            return buffer;
        }

        @Override
        public int getBufferEnd() {
            return endOffset;
        }

        @Override
        public @NotNull LexerPosition getCurrentPosition() {
            return new LexerPosition() {
                @Override
                public int getOffset() {
                    return 0;
                }

                @Override
                public int getState() {
                    return 0;
                }
            };
        }
        @Override
        public void restore(@NotNull LexerPosition position) {
            // No state to restore.
        }
    }
}
