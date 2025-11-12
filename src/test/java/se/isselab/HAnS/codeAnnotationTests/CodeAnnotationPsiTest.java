package se.isselab.HAnS.codeAnnotationTests;

import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.featureAnnotation.codeAnnotation.psi.CodeAnnotationLpq;
import se.isselab.HAnS.featureAnnotation.codeAnnotation.psi.CodeAnnotationElementFactory;

import java.util.Collection;

public class CodeAnnotationPsiTest extends BasePlatformTestCase {

    public void testCreateLPQElement() {
        CodeAnnotationLpq lpq = CodeAnnotationElementFactory.createLPQ(getProject(), "MyFeature");
        assertNotNull(lpq);
        assertTrue(lpq.getText().contains("MyFeature"));

        // create file and collect LPQ elements
        var file = CodeAnnotationElementFactory.createFile(getProject(), "A::B");
        Collection<CodeAnnotationLpq> list = PsiTreeUtil.collectElementsOfType(file, CodeAnnotationLpq.class);
        assertNotNull(list);
        assertTrue(list.size() >= 1);
    }
}
