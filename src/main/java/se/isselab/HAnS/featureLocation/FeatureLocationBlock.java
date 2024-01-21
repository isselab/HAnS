package se.isselab.HAnS.featureLocation;

/**
 * Structure which represents a block of lines from start to end
 */
public class FeatureLocationBlock {
    private final int start;
    private final int end;

    FeatureLocationBlock(int s, int e) {
        start = s;
        end = e;
    }

    public int getStartLine() {
        return start;
    }

    public int getEndLine() {
        return end;
    }
    // &begin[LineCount]

    /**
     * Method to get the line-count of the given Block
     *
     * @return Line-count of the given block
     */
    public int getLineCount() {
        int numberOfLines = end - start;

        return Math.max(numberOfLines + 1, 0);
    }
    // &end[LineCount]

    /**
     * Checks whether the block shares the same lines with another block
     *
     * @param block the block which should be checked against
     * @return true if the blocks intersect - otherwise false
     */
    public boolean hasSharedLines(FeatureLocationBlock block) {
        return !(block.getStartLine() > end || block.getEndLine() < start);
    }

    /**
     * Checks whether the block shares the same lines with at least one other block
     *
     * @param blocks block[] which should be checked against
     * @return true if at least one of the blocks intersect - otherwise false
     */
    public boolean hasSharedLines(FeatureLocationBlock[] blocks) {
        for (var block : blocks) {
            if (this.hasSharedLines(block))
                return true;
        }
        return false;
    }

    /**
     * Method to get the start and end line formatted as a string
     *
     * @return String which shows information of start and end-line
     */
    @Override
    public String toString() {
        return "Start: [" + getStartLine() + "]  End: [" + getEndLine() + "]";
    }
}
