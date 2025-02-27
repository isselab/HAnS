package se.isselab.HAnS.featureChildren;

import com.intellij.usages.Usage;
import com.intellij.usages.UsageGroup;
import com.intellij.usages.rules.UsageGroupingRule;
import com.intellij.usages.rules.UsageGroupingRuleProvider;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeatureUsageGroupingRuleProvider implements UsageGroupingRuleProvider {
    private final Map<Usage, UsageGroup> usageToGroupMap;

    public FeatureUsageGroupingRuleProvider(Map<Usage, UsageGroup> usageToGroupMap) {
        this.usageToGroupMap = usageToGroupMap;
    }

    @Override
    public @NotNull UsageGroupingRule[] getActiveRules(@NotNull Project project) {
        // Return an array of UsageGroupingRule
        return new UsageGroupingRule[]{
                new FeatureUsageGroupingRule(usageToGroupMap)
        };
    }
}

