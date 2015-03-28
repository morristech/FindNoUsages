import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;

import java.util.List;

/**
 * Find no usages action class.
 * <p/>
 * Created by kaelaela  on 2015/03/26.
 */
public class FindNoUsagesAction extends AnAction {


    public FindNoUsagesAction() {
        setInjectedContext(true);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) {
            return;
        }

        FindNoUsagesLogic logic = new FindNoUsagesLogic(project);
        List<PsiElement> javaClassPsiElements;
        javaClassPsiElements = logic.getJavaClassPsiElements();
        List<PsiElement> noUsageTargets;
        noUsageTargets = logic.getNoUsageTargets(javaClassPsiElements);

        StringBuilder builder = new StringBuilder();
        builder.append("No usage files (").append(noUsageTargets.size()).append("):\n");
        for (PsiElement target : noUsageTargets) {
            builder.append(target.toString()).append("\n");
        }
        Messages.showInfoMessage(builder.toString(), "No Usages");
    }


}
