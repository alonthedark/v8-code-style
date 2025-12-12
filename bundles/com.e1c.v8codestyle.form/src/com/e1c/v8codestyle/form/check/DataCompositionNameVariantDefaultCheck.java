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

package com.e1c.v8codestyle.form.check;

import static com._1c.g5.v8.dt.dcs.model.schema.DcsPackage.Literals.DATA_COMPOSITION_SCHEMA;
import static com._1c.g5.v8.dt.dcs.model.schema.DcsPackage.Literals.DATA_COMPOSITION_SCHEMA__SETTINGS_VARIANTS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.BasicEMap.Entry;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.dcs.model.schema.DataCompositionSchema;
import com._1c.g5.v8.dt.dcs.model.settings.SettingsVariant;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.form.CorePlugin;
import com.google.inject.Inject;

/**
 * The check report data composition schema name variant.
 * @author Ivan Sergeev
 */
public class DataCompositionNameVariantDefaultCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "data-composition-variant-name-default"; //$NON-NLS-1$

    @Inject
    public DataCompositionNameVariantDefaultCheck()
    {
        super();
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.DataCompositionNameVariantDefault_title)
            .description(Messages.DataCompositionNameVariantDefault_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new StandardCheckExtension(674, getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(DATA_COMPOSITION_SCHEMA)
            .features(DATA_COMPOSITION_SCHEMA__SETTINGS_VARIANTS);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        DataCompositionSchema dcs = (DataCompositionSchema)object;
        EList<SettingsVariant> variants = dcs.getSettingsVariants();
        if (variants == null)
        {
            return;
        }
        for (SettingsVariant settingsVariant : variants)
        {
            String name = settingsVariant.getName();
            EList<EObject> presentations = settingsVariant.getPresentation().eContents().get(0).eContents();
            if (name.equalsIgnoreCase("Default") || name.equalsIgnoreCase("Основной")) //$NON-NLS-1$ //$NON-NLS-2$
            {
                if (presentations.isEmpty())
                {
                    resultAcceptor.addIssue(Messages.DataCompositionNameVariantDefault_issue);
                    continue;
                }
                else
                {
                    checkPresentation(presentations, resultAcceptor);
                }
            }
            else
            {
                if (presentations.isEmpty())
                {
                    continue;
                }
                else
                {
                    checkPresentation(presentations, resultAcceptor);
                }
            }
        }
    }

    private void checkPresentation(EList<EObject> presentations, ResultAcceptor resultAcceptor)
    {
        EObject eObject = presentations.get(0);
        if (!(eObject instanceof com._1c.g5.v8.bm.core.BmObject))
        {
            return;
        }
        @SuppressWarnings("unchecked")
        BasicEMap.Entry<String, String> map = (Entry<String, String>)eObject;
        String presentationName = map.getValue();
        if (presentationName.equalsIgnoreCase("Default") || presentationName.equalsIgnoreCase("Основной")) //$NON-NLS-1$//$NON-NLS-2$
        {
            resultAcceptor.addIssue(Messages.DataCompositionNameVariantDefault_issue);
        }
    }
}
