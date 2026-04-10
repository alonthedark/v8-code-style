/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.SIMPLE_STATEMENT;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.core.platform.IConfigurationProvider;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.Subsystem;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Restriction execute external code check.
 *
 *  @author Ivan Sergeev
 */
public class RestrictionExecuteExternalCodeCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "restriction-execute-external-code"; //$NON-NLS-1$

    private static final Set<String> IMMUTABLE_MAP_COMPONENT = Set.of("подключитьвнешнююкомпоненту", "attachaddin", //$NON-NLS-1$//$NON-NLS-2$
        "начатьустановкувнешнейкомпоненты", "begininstalladdin", "установитьвнешнююкомпоненту", "installaddin", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        "начатьподключениевнешнейкомпоненты", "beginattachingaddin", "загрузитьвнешнююкомпоненту", "loadaddin"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    private final IConfigurationProvider configurationProvider;

    @Inject
    public RestrictionExecuteExternalCodeCheck(IV8ProjectManager v8ProjectManager,
        IConfigurationProvider configurationProvider)
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
        builder.title(Messages.RestrictionExecuteExternalCodeCheck_Title)
            .description(Messages.RestrictionExecuteExternalCodeCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.SECURITY)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(SIMPLE_STATEMENT)
            .checkedObjectType(INVOCATION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        EObject eObject = (EObject)object;
        Configuration configuration = configurationProvider.getConfiguration(eObject);
        EList<Subsystem> subSystems = configuration.getSubsystems();
        if (findSSL(subSystems))
        {
            if (object instanceof SimpleStatement statement)
            {
                if (statement.getRight() instanceof OperatorStyleCreator right)
                {
                    String nameObj = NodeModelUtils.findActualNodeFor(right).getText();
                    if (nameObj.toLowerCase().contains("защищенноесоединениеopenssl") //$NON-NLS-1$
                        || nameObj.toLowerCase().contains("opensslsecureconnection")) //$NON-NLS-1$
                    {
                        resultAceptor.addIssue(Messages.RestrictionExecuteExternalCodeCheck_Issue, right);
                    }
                }
            }
            if (object instanceof Invocation invocation)
            {
                FeatureAccess featureAccess = invocation.getMethodAccess();
                String name = featureAccess.getName();
                if (IMMUTABLE_MAP_COMPONENT.contains(name.toLowerCase()))
                {
                    resultAceptor.addIssue(Messages.RestrictionExecuteExternalCodeCheck_Issue, invocation);
                }
            }
        }
    }

    private boolean findSSL(EList<Subsystem> subSystems)
    {
        for (Subsystem subsystem : subSystems)
        {
            String name = subsystem.getName();
            if (name.equalsIgnoreCase("СтандартныеПодсистемы") || name.equalsIgnoreCase("StandardSubsystems")) //$NON-NLS-1$ //$NON-NLS-2$
            {
                return true;
            }
        }
        return false;
    }
}
