package se.isselab.HAnS.assetsManagement.cloningAssets;

import com.intellij.codeInsight.editorActions.TextBlockTransferableData;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CopyPastePostProcessor extends com.intellij.codeInsight.editorActions.CopyPastePostProcessor {
    @Override
    public @NotNull List<TextBlockTransferableData> collectTransferableData(@NotNull PsiFile psiFile, @NotNull Editor editor, int @NotNull [] ints, int @NotNull [] ints1) {
        System.out.println(psiFile.getName());
        List<TextBlockTransferableData> data = new List<TextBlockTransferableData>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<TextBlockTransferableData> iterator() {
                return null;
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return null;
            }

            @Override
            public boolean add(TextBlockTransferableData textBlockTransferableData) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends TextBlockTransferableData> c) {
                return false;
            }

            @Override
            public boolean addAll(int index, Collection<? extends TextBlockTransferableData> c) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public TextBlockTransferableData get(int index) {
                return null;
            }

            @Override
            public TextBlockTransferableData set(int index, TextBlockTransferableData element) {
                return null;
            }

            @Override
            public void add(int index, TextBlockTransferableData element) {

            }

            @Override
            public TextBlockTransferableData remove(int index) {
                return null;
            }

            @Override
            public int indexOf(Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(Object o) {
                return 0;
            }

            @Override
            public ListIterator<TextBlockTransferableData> listIterator() {
                return null;
            }

            @Override
            public ListIterator<TextBlockTransferableData> listIterator(int index) {
                return null;
            }

            @Override
            public List<TextBlockTransferableData> subList(int fromIndex, int toIndex) {
                return null;
            }
        };
        return data;
    }
}
