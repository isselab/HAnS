package se.isselab.HAnS;

import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import javax.swing.text.Document;

public class FeatureAnnotationToDelete {

    private String tangledFeatureLPQ;
    private String mainFeatureLPQ;
    private FeatureModelFeature mainFeature;
    private String filePath;
    private Document document;
    private int startLine;
    private int endLine;
    private FeatureFileMapping.AnnotationType annotationType;

    public FeatureAnnotationToDelete(String mainFeature,
                                     String tangledFeature,
                                     Document document,
                                     int startLine,
                                     int endLine,
                                     FeatureFileMapping.AnnotationType annotationType) {
        this.tangledFeatureLPQ = tangledFeature;
        this.mainFeatureLPQ = mainFeature;
        this.document = document;
        this.startLine = startLine;
        this.endLine = endLine;
        this.annotationType = annotationType;
    }

    public FeatureAnnotationToDelete(String mainFeature,
                                     String filePath,
                                     int startLine,
                                     int endLine,
                                     FeatureFileMapping.AnnotationType annotationType) {
        this.mainFeatureLPQ = mainFeature;
        this.filePath = filePath;
        this.startLine = startLine;
        this.endLine = endLine;
        this.annotationType = annotationType;
    }

    public FeatureAnnotationToDelete(FeatureModelFeature mainFeature,
                                     String filePath,
                                     int startLine,
                                     int endLine) {
        this.mainFeature = mainFeature;
        this.filePath = filePath;
        this.startLine = startLine;
        this.endLine = endLine;
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
