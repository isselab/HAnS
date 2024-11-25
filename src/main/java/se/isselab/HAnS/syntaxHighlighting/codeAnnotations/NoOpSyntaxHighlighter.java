package se.isselab.HAnS.syntaxHighlighting.codeAnnotations;

import com.intellij.lang.Language;
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


    private static final IElementType EMPTY_TOKEN = new IElementType("EMPTY_TOKEN", Language.ANY);

    //Added logging to the below to methods to see if this Highlighter is being used
    @Override
    public @NotNull Lexer getHighlightingLexer() {
        // Return an empty lexer that processes the text but produces no tokens.
        System.out.println("NoOpSyntaxHighlighter: Using EmptyLexer.");
        return new EmptyLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        // Return an empty array for all token types, ensuring no highlighting.
        System.out.println("NoOpSyntaxHighlighter: No highlights for token - " + tokenType);
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
            System.out.println("EmptyLexer: Starting with buffer of length " + buffer.length() + " from " + startOffset + " to " + endOffset);
            this.buffer = buffer;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.currentOffset = startOffset;

            if (buffer.isEmpty() || startOffset >= endOffset) {
                this.currentOffset = endOffset;
            }
        }

        @Override
        public int getState() {
            return 0; // No state is maintained.
        }

        @Override
        public IElementType getTokenType() {
            System.out.println("EmptyLexer: No tokens produced.");
            if (currentOffset < endOffset) {
                return EMPTY_TOKEN; // Return the empty token if not at the end
            } else {
                return null; // Signal end of lexing
            }
        }

        @Override
        public int getTokenStart() {
            return currentOffset;
        }

        @Override
        public int getTokenEnd() {
            return currentOffset + 1;
        }

        @Override
        public void advance() {
            System.out.println("EmptyLexer: Advancing...");
            if (currentOffset < endOffset) {
                currentOffset++;
            }
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
                    return currentOffset;
                }

                @Override
                public int getState() {
                    return 0;
                }
            };
        }
        @Override
        public void restore(@NotNull LexerPosition position) {
            this.currentOffset = position.getOffset();
        }
    }
}
