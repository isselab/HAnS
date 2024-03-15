package se.isselab.HAnS.assetsManagement.cloningManagement;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
@Service(Service.Level.PROJECT)
public final class NotificationService {
    private final Project myProject;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    public NotificationService(Project project){
        myProject = project;
    }

    public void startChecking() {
        Runnable checkTask = this::checkEvent;
        scheduler.scheduleAtFixedRate(checkTask, 0, 3, TimeUnit.SECONDS);
    }

    private void checkEvent() {
        System.out.println("Checking for event...");
    }

    public void dispose() {
        scheduler.shutdown();
    }
}
