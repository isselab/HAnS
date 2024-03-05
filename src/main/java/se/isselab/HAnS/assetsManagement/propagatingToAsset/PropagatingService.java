package se.isselab.HAnS.assetsManagement.propagatingToAsset;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.Collection;
import java.util.Iterator;

public class PropagatingService {
    Project myProject = ProjectManager.getInstance().getDefaultProject();

    public PropagatingService(Project project) {
        this.myProject = project;
    }
    public void ServiceMethod(VirtualFile openedVirtualFile) {
    }
}
