import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Find no usages logic class.
 * Created by kaelaela on 2015/03/26.
 */
public class FindNoUsagesLogic {
    private static List<PsiElement> mJavaClassPsiElements;
    private static List<PsiElement> mNoUsageTargets;
    private static Project mProject;

    public FindNoUsagesLogic(Project project) {
        mJavaClassPsiElements = new ArrayList<PsiElement>();
        mNoUsageTargets = new ArrayList<PsiElement>();
        mProject = project;
    }

    public List<PsiElement> getJavaClassPsiElements() {
        PsiManager psiManager = PsiManager.getInstance(mProject);
        VirtualFile[] virtualFiles = ProjectRootManager.getInstance(mProject).getContentSourceRoots();
        PsiDirectory directory = psiManager.findDirectory(virtualFiles[0]);
        PsiElement[] elements = directory.getChildren();
        Collections.addAll(mJavaClassPsiElements, elements);

        for (final PsiElement psiElement : elements) {
            PsiTreeUtil.collectElements(psiElement, new PsiElementFilter() {
                @Override
                public boolean isAccepted(PsiElement psiElement) {
                    if (psiElement instanceof PsiDirectory) {
                        mJavaClassPsiElements.remove(psiElement);
                        getChildFromDirectory(psiElement);
                    }
                    return psiElement instanceof PsiJavaFile;
                }
            });
        }
        return mJavaClassPsiElements;
    }

    private boolean isTerminationPoint(PsiElement element) {
        return element.getChildren().length == 0;
    }

    private void getChildFromDirectory(PsiElement directory) {
        PsiElement[] children = directory.getChildren();

        for (PsiElement child : children) {
            if (isTerminationPoint(child) && child instanceof PsiIdentifier) {
                mJavaClassPsiElements.add(child);
            } else {
                getChildFromDirectory(child);
            }
        }
    }

    public List<PsiElement> getNoUsageTargets(List<PsiElement> elements) {
        for (PsiElement element : elements) {
            Query<PsiReference> query = ReferencesSearch.search(element);
            if (query.findAll().size() == 0) {
                mNoUsageTargets.add(element);
            }
        }
        return mNoUsageTargets;
    }
}
