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
import com.google.common.collect.ImmutableMap;
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

    private final ImmutableMap<Integer, String> immutableMapCall = ImmutableMap.<Integer, String> builder()
        .put(1, "открытьзначение") //$NON-NLS-1$
        .put(2, "openvalue") //$NON-NLS-1$
        .put(3, "открытьформумодально") //$NON-NLS-1$
        .put(4, "openformmodal")//$NON-NLS-1$
        .put(5, "вопрос") //$NON-NLS-1$
        .put(6, "doquerybox") //$NON-NLS-1$
        .put(7, "предупреждение") //$NON-NLS-1$
        .put(8, "Domessagebox") //$NON-NLS-1$
        .put(9, "выбратьизсписка") //$NON-NLS-1$
        .put(10, "choosefromlist") //$NON-NLS-1$
        .put(11, "ввестистроку") //$NON-NLS-1$
        .put(12, "inputstring") //$NON-NLS-1$
        .put(13, "ввестичисло") //$NON-NLS-1$
        .put(14, "inputnumber") //$NON-NLS-1$
        .put(15, "ввестидату") //$NON-NLS-1$
        .put(16, "inputdate") //$NON-NLS-1$
        .put(17, "открытьмодально") //$NON-NLS-1$
        .put(18, "domodal") //$NON-NLS-1$
        .put(19, "поместитьфайл") //$NON-NLS-1$
        .put(20, "putfile") //$NON-NLS-1$
        .put(21, "отметитьэлементы") //$NON-NLS-1$
        .put(22, "checkitems") //$NON-NLS-1$
        .put(23, "выбратьэлемент") //$NON-NLS-1$
        .put(24, "chooseitem") //$NON-NLS-1$
        .put(25, "установитьрасширениеработысфайлами") //$NON-NLS-1$
        .put(26, "installfilesystemextension") //$NON-NLS-1$
        .put(27, "установитьвнешнююкомпоненту") //$NON-NLS-1$
        .put(28, "installaddin") //$NON-NLS-1$
        .build();

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
        if (configuration.getModalityUseMode() == ModalityUseMode.DONT_USE)
        {
            FeatureAccess featureAccess = invocation.getMethodAccess();
            String name = featureAccess.getName();

            if (immutableMapCall.containsValue(name.toLowerCase()))
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