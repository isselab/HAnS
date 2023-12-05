package se.isselab.HAnS.featureLocation;

public class FeatureLocationBlock {
    private int start;
    private int end;

    FeatureLocationBlock(int s, int e){
        start = s;
        end = e;
    }

    public int getStartLine(){
        return start;
    }

    public int getEndLine(){
        return end;
    }


    public int getLineCount(){
        int numberOfLines = end - start;

        return Math.max(numberOfLines + 1, 0);
    }

}
