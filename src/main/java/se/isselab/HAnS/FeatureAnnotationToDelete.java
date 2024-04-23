package se.isselab.HAnS;

import com.intellij.openapi.editor.Document;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.Objects;

public class FeatureAnnotationToDelete {

    private String tangledFeatureLPQ;
    private String mainFeatureLPQ;
    private FeatureModelFeature mainFeature;
    private String filePath;
    private Document document;
    private int startLine;
    private int endLine;
    private FeatureFileMapping.AnnotationType tangledAnnotationType;
    private FeatureFileMapping.AnnotationType mainAnnotationType;

    public FeatureAnnotationToDelete() {
        this.tangledFeatureLPQ = null;
        this.mainFeatureLPQ = null;
        this.mainFeature = null;
        this.filePath = null;
        this.document = null;
        this.startLine = -1;
        this.endLine = -1;
        this.tangledAnnotationType = null;
        this.mainAnnotationType = null;
    }

    @Override
    public int hashCode() {
        String mainFeature2 = null;
        if (this.mainFeature != null) {
            mainFeature2 = this.mainFeature.getLPQText();
        }

        return Objects.hash(tangledFeatureLPQ, mainFeatureLPQ, mainFeature2, filePath, document, startLine, endLine, tangledAnnotationType, mainAnnotationType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        String mainFeatureLpq = null;
        String otherFeatureLpq = null;
        if (mainFeature != null) {
            mainFeatureLpq = mainFeature.getLPQText();
        }
        FeatureAnnotationToDelete featureAnnotationToDelete = (FeatureAnnotationToDelete) o;
        if (featureAnnotationToDelete.mainFeature != null) {
            otherFeatureLpq = featureAnnotationToDelete.mainFeature.getLPQText();
        }

        return Objects.equals(tangledFeatureLPQ, featureAnnotationToDelete.tangledFeatureLPQ) &&
                Objects.equals(mainFeatureLPQ, featureAnnotationToDelete.mainFeatureLPQ) &&
                Objects.equals(mainFeatureLpq, otherFeatureLpq) &&
                Objects.equals(filePath, featureAnnotationToDelete.filePath) &&
                document == featureAnnotationToDelete.document &&
                startLine == featureAnnotationToDelete.startLine &&
                endLine == featureAnnotationToDelete.endLine &&
                tangledAnnotationType == featureAnnotationToDelete.tangledAnnotationType &&
                mainAnnotationType == featureAnnotationToDelete.mainAnnotationType;
    }
    public void setTangledAnnotationType(FeatureFileMapping.AnnotationType tangledAnnotationType) {
        this.tangledAnnotationType = tangledAnnotationType;
    }

    public void setMainAnnotationType(FeatureFileMapping.AnnotationType mainAnnotationType) {
        this.mainAnnotationType = mainAnnotationType;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setMainFeature(FeatureModelFeature mainFeature) {
        this.mainFeature = mainFeature;
    }

    public void setMainFeatureLPQ(String mainFeatureLPQ) {
        this.mainFeatureLPQ = mainFeatureLPQ;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public void setTangledFeatureLPQ(String tangledFeatureLPQ) {
        this.tangledFeatureLPQ = tangledFeatureLPQ;
    }

    public FeatureFileMapping.AnnotationType getTangledAnnotationType() {
        return tangledAnnotationType;
    }

    public FeatureFileMapping.AnnotationType getMainAnnotationType() { return mainAnnotationType; }

    public Document getDocument() {
        return document;
    }

    public FeatureModelFeature getMainFeature() {
        return mainFeature;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getStartLine() {
        return startLine;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getMainFeatureLPQ() {
        return mainFeatureLPQ;
    }

    public String getTangledFeatureLPQ() {
        return tangledFeatureLPQ;
    }

}
