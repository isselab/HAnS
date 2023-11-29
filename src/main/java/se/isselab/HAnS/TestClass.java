package se.isselab.HAnS;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.Map;

public class TestClass extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Map<String, ArrayList<FeatureLocationInfo>> map = FeatureLocationInfo.getAllFeatureLocations(e.getProject());

        for(String value : map.keySet()){
            System.out.println("/////////");
            System.out.println(value);
            for(FeatureLocationInfo info : map.get(value)){
                System.out.print("   ");
                info.printMembers();
            }
            System.out.println("/////////\n");
        }


    }
}
