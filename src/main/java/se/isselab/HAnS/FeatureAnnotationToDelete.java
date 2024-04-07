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
    private FeatureFileMapping.AnnotationType annotationType;

    public FeatureAnnotationToDelete() {
        this.tangledFeatureLPQ = null;
        this.mainFeatureLPQ = null;
        this.mainFeature = null;
        this.filePath = null;
        this.document = null;
        this.startLine = -1;
        this.endLine = -1;
        this.annotationType = null;
    }


    @Override
    public int hashCode() {
        System.out.println("HASHCODE reached");
        String mainFeature2 = null;
        if (this.mainFeature != null) {
            mainFeature2 = this.mainFeature.getLPQText();
        }

        return Objects.hash(tangledFeatureLPQ, mainFeatureLPQ, mainFeature2, filePath, document, startLine, endLine, annotationType);
    }

    @Override
    public boolean equals(Object o) {
        System.out.println("EQUALS reached");
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

        boolean equals = Objects.equals(tangledFeatureLPQ, featureAnnotationToDelete.tangledFeatureLPQ);
        System.out.println("tangledFeatureLPQ comparison: " + equals);

        equals = Objects.equals(mainFeatureLPQ, featureAnnotationToDelete.mainFeatureLPQ);
        System.out.println("mainFeatureLPQ comparison: " + equals);

        equals = Objects.equals(mainFeatureLpq, otherFeatureLpq);
        System.out.println("mainFeature LPQText comparison: " + equals);

        equals = Objects.equals(filePath, featureAnnotationToDelete.filePath);
        System.out.println("filePath comparison: " + equals);

        equals = document == featureAnnotationToDelete.document;
        System.out.println("document comparison: " + equals);

        equals = startLine == featureAnnotationToDelete.startLine;
        System.out.println("startLine comparison: " + equals);

        equals = endLine == featureAnnotationToDelete.endLine;
        System.out.println("endLine comparison: " + equals);

        equals = annotationType == featureAnnotationToDelete.annotationType;
        System.out.println("annotationType comparison: " + equals);


        return Objects.equals(tangledFeatureLPQ, featureAnnotationToDelete.tangledFeatureLPQ) &&
                Objects.equals(mainFeatureLPQ, featureAnnotationToDelete.mainFeatureLPQ) &&
                Objects.equals(mainFeatureLpq, otherFeatureLpq) &&
                Objects.equals(filePath, featureAnnotationToDelete.filePath) &&
                document == featureAnnotationToDelete.document &&
                startLine == featureAnnotationToDelete.startLine &&
                endLine == featureAnnotationToDelete.endLine &&
                annotationType == featureAnnotationToDelete.annotationType;
    }

    //    public FeatureAnnotationToDelete(String mainFeature,
//                                     String tangledFeature,
//                                     Document document,
//                                     int startLine,
//                                     int endLine,
//                                     FeatureFileMapping.AnnotationType annotationType) {
//        this.tangledFeatureLPQ = tangledFeature;
//        this.mainFeatureLPQ = mainFeature;
//        this.document = document;
//        this.startLine = startLine;
//        this.endLine = endLine;
//        this.annotationType = annotationType;
//    }
//
//    public FeatureAnnotationToDelete(String mainFeature,
//                                     String filePath,
//                                     int startLine,
//                                     int endLine,
//                                     FeatureFileMapping.AnnotationType annotationType) {
//        this.mainFeatureLPQ = mainFeature;
//        this.filePath = filePath;
//        this.startLine = startLine;
//        this.endLine = endLine;
//        this.annotationType = annotationType;
//    }
//
//    public FeatureAnnotationToDelete(FeatureModelFeature mainFeature,
//                                     String filePath,
//                                     int startLine,
//                                     int endLine) {
//        this.mainFeature = mainFeature;
//        this.filePath = filePath;
//        this.startLine = startLine;
//        this.endLine = endLine;
//    }

    public void setAnnotationType(FeatureFileMapping.AnnotationType annotationType) {
        this.annotationType = annotationType;
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

    public FeatureFileMapping.AnnotationType getAnnotationType() {
        return annotationType;
    }

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
