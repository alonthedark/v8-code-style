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
package com.e1c.v8codestyle.bsl.ui.qfix;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.google.inject.Inject;

/**
 * Replacement no SSL call to SSL call
 *
 *  @author Ivan Sergeev
 */
@QuickFix(checkId = "restriction-execute-external-code", supplierId = "com.e1c.v8codestyle.bsl")
public class RestrictionExecuteExternalCodeFix
    extends SingleVariantXtextBslModuleFix
{

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public RestrictionExecuteExternalCodeFix(IV8ProjectManager v8ProjectManager)
    {
        super();
        this.v8ProjectManager = v8ProjectManager;
    }

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description(Messages.RestrictionExecuteExternalCodeFix_Description)
            .details(Messages.RestrictionExecuteExternalCodeFix_Details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        EObject eobject = model.getElement();
        if (!(eobject instanceof OperatorStyleCreator))
        {
            return null;
        }
        int issueOffset = model.getIssue().getOffset();
        Module module = EcoreUtil2.getContainerOfType(model.getElement(), Module.class);
        INode moduleNode = NodeModelUtils.findActualNodeFor(module);
        if (moduleNode == null)
        {
            return null;
        }

        IV8Project baseProject = v8ProjectManager.getProject(eobject);
        ScriptVariant languageCode = baseProject.getScriptVariant();

        INode node = NodeModelUtils.findActualNodeFor(eobject);
        if (node == null)
        {
            return null;
        }
        if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
        {
            String oldText = "новый защищенноесоединениеopenssl"; //$NON-NLS-1$
            String text = node.getText().toLowerCase();
            int index = text.indexOf(oldText);
            if (index != -1)
            {
                return new ReplaceEdit(issueOffset + index - 1, oldText.length(),
                    "ОбщегоНазначенияКлиентСервер.НовоеЗащищенноеСоединение()"); //$NON-NLS-1$
            }
        }
        else if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.ENGLISH)
        {
            String oldText = "new opensslsecureconnection"; //$NON-NLS-1$
            String text = node.getText().toLowerCase();
            int index = text.indexOf(oldText);
            if (index != -1)
            {
                return new ReplaceEdit(issueOffset + index - 1, oldText.length(),
                    "CommonClientServer.NewSecureConnection()"); //$NON-NLS-1$
            }
        }
        return null;
    }
}
