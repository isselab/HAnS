package se.isselab.HAnS.referencing;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiElementBase;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CustomPsiElement implements PsiElement { // Ersetzen Sie MyPsiElementBase durch Ihre tatsächliche Basis-Klasse
    private final String customName;
    private final PsiElement element;

    public CustomPsiElement(PsiElement element, String customName) {
        this.customName = customName;
        this.element = element;
    }

    @Override
    public @NotNull Project getProject() throws PsiInvalidElementAccessException {
        return element.getProject();
    }

    @Override
    public @NotNull Language getLanguage() {
        return element.getLanguage();
    }

    @Override
    public PsiManager getManager() {
        return element.getManager();
    }

    @Override
    public @NotNull PsiElement @NotNull [] getChildren() {
        return element.getChildren();
    }

    @Override
    public PsiElement getParent() {
        return element.getParent();
    }

    @Override
    public PsiElement getFirstChild() {
        return element.getFirstChild();
    }

    @Override
    public PsiElement getLastChild() {
        return element.getLastChild();
    }

    @Override
    public PsiElement getNextSibling() {
        return element.getNextSibling();
    }

    @Override
    public PsiElement getPrevSibling() {
        return element.getPrevSibling();
    }

    @Override
    public PsiFile getContainingFile() throws PsiInvalidElementAccessException {
        return element.getContainingFile();
    }

    @Override
    public TextRange getTextRange() {
        return element.getTextRange();
    }

    @Override
    public int getStartOffsetInParent() {
        return element.getStartOffsetInParent();
    }

    @Override
    public int getTextLength() {
        return element.getTextLength();
    }

    @Override
    public @Nullable PsiElement findElementAt(int i) {
        return element.findElementAt(i);
    }

    @Override
    public @Nullable PsiReference findReferenceAt(int i) {
        return element.findReferenceAt(i);
    }

    @Override
    public int getTextOffset() {
        return element.getTextOffset();
    }

    @Override
    public @NlsSafe String getText() {
        return element.getText();
    }

    @Override
    public char @NotNull [] textToCharArray() {
        return element.textToCharArray();
    }

    @Override
    public PsiElement getNavigationElement() {
        return element.getNavigationElement();
    }

    @Override
    public PsiElement getOriginalElement() {
        return element.getOriginalElement();
    }

    @Override
    public boolean textMatches(@NotNull @NonNls CharSequence charSequence) {
        return element.textMatches(charSequence);
    }

    @Override
    public boolean textMatches(@NotNull PsiElement psiElement) {
        return element.textMatches(psiElement);
    }

    @Override
    public boolean textContains(char c) {
        return element.textContains(c);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor psiElementVisitor) {
        element.accept(psiElementVisitor);
    }

    @Override
    public void acceptChildren(@NotNull PsiElementVisitor psiElementVisitor) {
        element.acceptChildren(psiElementVisitor);
    }

    @Override
    public PsiElement copy() {
        return  element.copy();
    }

    @Override
    public PsiElement add(@NotNull PsiElement psiElement) throws IncorrectOperationException {
        return element.add(psiElement);
    }

    @Override
    public PsiElement addBefore(@NotNull PsiElement psiElement, @Nullable PsiElement psiElement1) throws IncorrectOperationException {
        return element.addBefore(psiElement, psiElement1);
    }

    @Override
    public PsiElement addAfter(@NotNull PsiElement psiElement, @Nullable PsiElement psiElement1) throws IncorrectOperationException {
        return null;
    }

    @Override
    public void checkAdd(@NotNull PsiElement psiElement) throws IncorrectOperationException {

    }

    @Override
    public PsiElement addRange(PsiElement psiElement, PsiElement psiElement1) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiElement addRangeBefore(@NotNull PsiElement psiElement, @NotNull PsiElement psiElement1, PsiElement psiElement2) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiElement addRangeAfter(PsiElement psiElement, PsiElement psiElement1, PsiElement psiElement2) throws IncorrectOperationException {
        return null;
    }

    @Override
    public void delete() throws IncorrectOperationException {

    }

    @Override
    public void checkDelete() throws IncorrectOperationException {

    }

    @Override
    public void deleteChildRange(PsiElement psiElement, PsiElement psiElement1) throws IncorrectOperationException {

    }

    @Override
    public PsiElement replace(@NotNull PsiElement psiElement) throws IncorrectOperationException {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public @Nullable PsiReference getReference() {
        return null;
    }

    @Override
    public @NotNull PsiReference[] getReferences() {
        return new PsiReference[]{
                new CustomPsiReference(this, customName)
        };
    }

    @Override
    public <T> @Nullable T getCopyableUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putCopyableUserData(@NotNull Key<T> key, @Nullable T t) {

    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor psiScopeProcessor, @NotNull ResolveState resolveState, @Nullable PsiElement psiElement, @NotNull PsiElement psiElement1) {
        return false;
    }

    @Override
    public @Nullable PsiElement getContext() {
        return null;
    }

    @Override
    public boolean isPhysical() {
        return false;
    }

    @Override
    public @NotNull GlobalSearchScope getResolveScope() {
        return null;
    }

    @Override
    public @NotNull SearchScope getUseScope() {
        return null;
    }

    @Override
    public ASTNode getNode() {
        return null;
    }

    @Override
    public boolean isEquivalentTo(PsiElement psiElement) {
        return false;
    }

    @Override
    public Icon getIcon(int i) {
        return null;
    }

    @Override
    public <T> @Nullable T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T t) {

    }
}
