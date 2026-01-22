/*******************************************************************************
 * Copyright (C) 2025, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     1C-Soft LLC - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.INVOCATION;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.core.platform.IConfigurationProvider;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.ModalityUseMode;
import com.e1c.g5.v8.dt.check.BslDirectLocationIssue;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.DirectLocation;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.Issue;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Checks dont use modality call in dont use modality mode.
 *
 *  @author Ivan Sergeev
 */
public class DontUseModalityModeCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "dont-use-modality-mode"; //$NON-NLS-1$

    private final IConfigurationProvider configurationProvider;

    @Inject
    public DontUseModalityModeCheck(IConfigurationProvider configurationProvider)
    {
        super();
        this.configurationProvider = configurationProvider;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.DontUseModalityModeCheck_Title)
            .description(Messages.DontUseModalityModeCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(INVOCATION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Invocation invocation = (Invocation)object;
        Configuration configuration = configurationProvider.getConfiguration(invocation);
        if (configuration.getModalityUseMode() == ModalityUseMode.get(2))
        {
            String[] modalityCall = new String[] { "ОткрытьЗначение", "OpenValue", //$NON-NLS-1$ //$NON-NLS-2$
                "ОткрытьФормуМодально", "OpenFormModal", //$NON-NLS-1$ //$NON-NLS-2$
                "Вопрос", "DoQueryBox", //$NON-NLS-1$ //$NON-NLS-2$
                "Предупреждение", "DoMessageBox", //$NON-NLS-1$ //$NON-NLS-2$
                "ВыбратьИзСписка", "ChooseFromList", //$NON-NLS-1$ //$NON-NLS-2$
                "ВвестиСтроку", "InputString", //$NON-NLS-1$ //$NON-NLS-2$
                "ОткрытьМодально", "DoModal", //$NON-NLS-1$ //$NON-NLS-2$
                "ВвестиЧисло", "InputNumber", //$NON-NLS-1$ //$NON-NLS-2$
                "ВвестиДату", "InputDate", //$NON-NLS-1$ //$NON-NLS-2$
                "ПоместитьФайл", "PutFile", //$NON-NLS-1$ //$NON-NLS-2$
                "ОтметитьЭлементы", "CheckItems", //$NON-NLS-1$ //$NON-NLS-2$
                "ВыбратьЭлемент", "ChooseItem", //$NON-NLS-1$ //$NON-NLS-2$
                "УстановитьРасширениеРаботыСФайлами", "InstallFileSystemExtension", //$NON-NLS-1$ //$NON-NLS-2$
                "УстановитьВнешнююКомпоненту", "InstallAddIn" }; //$NON-NLS-1$ //$NON-NLS-2$
            List<String> callList = Arrays.asList(modalityCall);

            FeatureAccess featureAccess = invocation.getMethodAccess();
            String name = featureAccess.getName();

            if (callList.stream().anyMatch(str -> str.equalsIgnoreCase(name)))
            {
                ICompositeNode node = NodeModelUtils.findActualNodeFor(featureAccess);
                DirectLocation directLocation =
                    new DirectLocation(node.getOffset(), node.getLength(), node.getStartLine(), invocation);

                Issue issue = new BslDirectLocationIssue(Messages.DontUseModalityModeCheck_Issue, directLocation);
                resultAceptor.addIssue(issue);
            }
        }
    }
}
