package fr.adrienbrault.idea.symfony2plugin.dic;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import fr.adrienbrault.idea.symfony2plugin.Symfony2InterfacesUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author Adrien Brault <adrien.brault@gmail.com>
 */
public class ServiceReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar psiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(
            PlatformPatterns.psiElement(StringLiteralExpression.class).withLanguage(PhpLanguage.INSTANCE),
            new PsiReferenceProvider() {
                @NotNull
                @Override
                public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
                    if (!(psiElement.getContext() instanceof ParameterList)) {
                        return new PsiReference[0];
                    }
                    ParameterList parameterList = (ParameterList) psiElement.getContext();

                    if (!(parameterList.getContext() instanceof MethodReference)) {
                        return new PsiReference[0];
                    }
                    MethodReference method = (MethodReference) parameterList.getContext();

                    Symfony2InterfacesUtil interfacesUtil = new Symfony2InterfacesUtil();
                    if (!interfacesUtil.isContainerGetCall(method)) {
                        return new PsiReference[0];
                    }

                    return new PsiReference[]{ new ServiceReference((StringLiteralExpression) psiElement) };
                }
            }
        );
    }
}
