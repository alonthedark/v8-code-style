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
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;

/**
 * Correct format link part
 *
 *  @author Ivan Sergeev
 */
@QuickFix(checkId = "link-part-comment-space", supplierId = "com.e1c.v8codestyle.bsl")
public class LinkPartSpaceFix
    extends SingleVariantXtextBslModuleFix
{

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description(Messages.LinkPartSpaceFix_Description)
            .details(Messages.LinkPartSpaceFix_Details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        int issueOffset = model.getIssue().getOffset();
        Method method = (Method)model.getElement();
        EObject eObject = getModule(method);
        while (!(eObject instanceof Module))
        {
            eObject = getModule(eObject);
        }
        if (eObject instanceof Module)
        {
            INode node = NodeModelUtils.findActualNodeFor(eObject);
            if (node == null)
            {
                return null;
            }
            String editText = node.getText();
            char checkChar = editText.charAt(issueOffset + 1);

            if (!Character.isLetter(checkChar))
            {
                String nextChar = String.valueOf(editText.charAt(issueOffset + 2));
                if (nextChar.equals(" ")) //$NON-NLS-1$
                {
                    return new DeleteEdit(issueOffset + 1, 1);
                }
                else
                {
                    return new ReplaceEdit(issueOffset + 1, 1, " "); //$NON-NLS-1$
                }
            }
            else
            {
                return new InsertEdit(issueOffset + 1, " "); //$NON-NLS-1$
            }
        }
        return null;
    }

    private EObject getModule(EObject eObject)
    {
        EObject nextContainer = eObject.eContainer();
        return nextContainer;
    }
}
