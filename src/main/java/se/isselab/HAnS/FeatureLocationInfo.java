package se.isselab.HAnS;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Holds information on a given feature and its occurrences in a given file
 */
public class FeatureLocationInfo {
    private PsiFile file;
    private String featureName;
    private Integer startOffset;
    private Integer endOffset;
    /**
     * @param file        File in which the location is referring to
     * @param featureName Name of the feature which is located
     */
    public FeatureLocationInfo(PsiFile file, String featureName, int startOffset, int endOffset) {
        this.file = file;
        this.featureName = featureName;
        this.startOffset = startOffset;
        this.endOffset = endOffset;

    }
    public FeatureLocationInfo() {
    }

    public PsiFile getFile() {
        return file;
    }

    public String getFeatureName() {
        return featureName;
    }

    public Integer getStartOffset(){return startOffset;}
    public Integer getEndOffset(){return endOffset;}

    public void printMembers() {
        System.out.println(this.file.getName() + ": " + this.startOffset + "/" + this.endOffset);
    }
    /**
     *
     * @param project project instance
     * @return Map which contains FeatureLocationInfo objects for each Feature
     */

    public static HashMap<String, ArrayList<FeatureLocationInfo>> getAllFeatureLocations(Project project){
        HashMap<String, ArrayList<FeatureLocationInfo>> map = new HashMap<>();

        String beginSuffix = "&begin";
        String endSuffix = "&end";
        String lineSuffix = "&line";

        //TODO check if project is valid
        //TODO use LPQ names - currently this algorithm only checks for the featureName instead of the LPQ

        // get all the Java files in the project
        Collection<VirtualFile> javaFiles = FileTypeIndex.getFiles(
                FileTypeManager.getInstance().getFileTypeByExtension("java")
                , GlobalSearchScope.projectScope(Objects.requireNonNull(project)));
        Iterator<VirtualFile> it = javaFiles.iterator();
        PsiManager psiManager = PsiManager.getInstance(project);

        //Iterate over .java files
        while(it.hasNext()){
            PsiFile openedFile = psiManager.findFile(it.next());
            if(openedFile != null){
                //helper classes to retrieve line numbers
                PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
                Document document = psiDocumentManager.getDocument(openedFile);

                //iterate over each psiElement and check for PsiComment-Feature-Annotations
                openedFile.accept(new PsiRecursiveElementVisitor() {
                    @Override
                    public void visitComment(@NotNull PsiComment comment) {

                        /*
                        //checking for the start of a block feature
                        if(comment.getText().contains(beginSuffix + "[")) {
                            //get feature name
                            String[] names = extractFeatureNames(comment.getText(), beginSuffix);
                            for(String featureName : names){
                                if (featureName != null) {
                                    FeatureLocationInfo featureLocationInfo = new FeatureLocationInfo();
                                    featureLocationInfo.startOffset = document != null ? document.getLineNumber(comment.getTextRange().getStartOffset()) + 1 : null;
                                    featureLocationInfo.file = openedFile;
                                    featureLocationInfo.featureName = featureName;
                                    ArrayList<FeatureLocationInfo> arrayList = map.get(featureName);
                                    if (arrayList == null)
                                        arrayList = new ArrayList<>();

                                    arrayList.add(featureLocationInfo);
                                    map.put(featureName, arrayList);
                                }
                            }
                        }
                        //checking for the end of a block feature - which can be in the same comment as the start
                        if(comment.getText().contains(endSuffix + "[")){

                            //get feature name
                            String[] names = extractFeatureNames(comment.getText(), endSuffix);
                            for(String featureName : names) {
                                if (featureName != null) {
                                    ArrayList<FeatureLocationInfo> arrayList = map.get(featureName);
                                    //check if a corresponding begin was found
                                    if (arrayList.size() > 0) {
                                        FeatureLocationInfo featureLocationInfo = arrayList.get(arrayList.size() - 1);
                                        //check if the begin was in the same file
                                        if (featureLocationInfo.file == openedFile) {
                                            featureLocationInfo.endOffset = document != null ? document.getLineNumber(comment.getTextRange().getEndOffset()) +1 : -1;
                                        }
                                    }
                                }
                            }
                        }
                        else if(comment.getText().contains(lineSuffix + "[")){
                            String[] names = extractFeatureNames(comment.getText(), lineSuffix);
                            for(String featureName : names) {
                                if (featureName != null) {
                                    int lineNumber = document != null ? document.getLineNumber(comment.getTextRange().getStartOffset()) + 1 : -1;
                                    FeatureLocationInfo featureLocationInfo = new FeatureLocationInfo(openedFile, featureName, lineNumber, lineNumber);
                                    ArrayList<FeatureLocationInfo> arrayList = map.get(featureName);
                                    if (arrayList == null) {
                                        arrayList = new ArrayList<>();
                                    }
                                    arrayList.add(featureLocationInfo);
                                    map.put(featureName, arrayList);
                                }
                            }
                        }
                        */

                        super.visitComment(comment);
                    }
                });
            }
        }

        return map;
    }

    /**
     *
     * @param input String where the feature should be extracted from
     * @return feature name array if found or null
     */
    public static String[] extractFeatureNames(String input, String suffix){
        String regex = suffix + "\\[(.*?)\\]";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if(matcher.find()){
            return matcher.group(1).split(",");
        }else{
            return null;
        }
    }




}