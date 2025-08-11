package se.isselab.HAnS.featureChildren;

import com.intellij.usages.Usage;
import com.intellij.usages.rules.UsageGroupingRule;
import com.intellij.usages.UsageGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FeatureUsageGroupingRule implements UsageGroupingRule {
    private final Map<Usage, UsageGroup> usageToGroupMap;

    public FeatureUsageGroupingRule(Map<Usage, UsageGroup> usageToGroupMap) {
        this.usageToGroupMap = usageToGroupMap;
    }

    @Override
    public @Nullable UsageGroup groupUsage(@NotNull Usage usage) {
        // Return the corresponding group for the usage or null if no group is defined
        return usageToGroupMap.get(usage);
    }
}
