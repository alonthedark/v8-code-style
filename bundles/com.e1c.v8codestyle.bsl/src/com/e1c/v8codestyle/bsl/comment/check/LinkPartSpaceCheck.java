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
package com.e1c.v8codestyle.bsl.comment.check;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.scoping.IScopeProvider;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Description;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.LinkPart;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.dt.core.api.platform.BmOperationContext;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Validates {@link LinkPart} of documentation comment has a space before the link
 *
 * @author Ivan Sergeev
 */
public class LinkPartSpaceCheck
    extends AbstractDocCommentTypeCheck
{
    private static final String CHECK_ID = "link-part-comment-space"; //$NON-NLS-1$

    /**
     * Instantiates a new reference link part check.
     *
     * @param resourceLookup service for look up workspace resources, see {@link IResourceLookup}, cannot be <code>null</code>
     * @param namingService service for getting names of EDT object and resources, cannot be <code>null</code>
     * @param bmModelManager service for getting instance of Bm Model by {@link EObject}, cannot be <code>null</code>
     * @param v8ProjectManager {@link IV8ProjectManager} for getting {@link IV8Project} by {@link EObject}, cannot be <code>null</code>
     * @param scopeProvider the scope provider service, cannot be {@code null}.
     */
    @Inject
    public LinkPartSpaceCheck(IResourceLookup resourceLookup, INamingService namingService,
        IBmModelManager bmModelManager, IV8ProjectManager v8ProjectManager, IScopeProvider scopeProvider)
    {
        super(resourceLookup, namingService, bmModelManager, v8ProjectManager);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.LinkPartSpaceCheck_title)
            .description(Messages.LinkPartSpaceCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .delegate(LinkPart.class);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters,
        BmOperationContext typeComputationContext, IProgressMonitor monitor)
    {
        LinkPart linkPart = (LinkPart)object;

        if (object.getParent() instanceof Description)
        {
            String checkString = linkPart.getInitialContent();
            String stringLink = linkPart.getLinkText();
            Method method = root.getMethod();
            INode node = NodeModelUtils.findActualNodeFor(method);
            if (node == null)
            {
                return;
            }
            if (checkString.toLowerCase().indexOf("См.".toLowerCase()) != -1 //$NON-NLS-1$
                || checkString.toLowerCase().indexOf("See".toLowerCase()) != -1) //$NON-NLS-1$
            {
                String textMethod = node.getText();
                int indexCheckChar = textMethod.indexOf(checkString);
                int indexLinkText = checkString.indexOf(stringLink);
                char checkChar = checkString.charAt(indexLinkText - 1);
                char checkPrevChar = textMethod.charAt(indexCheckChar - 1);
                if (!String.valueOf(checkChar).equals(" ") //$NON-NLS-1$
                    && !Character.isLetter(checkPrevChar))
                {
                    if (stringLink.equals(":") || stringLink.equals("_")) //$NON-NLS-1$ //$NON-NLS-2$
                    {
                        resultAceptor.addIssue(Messages.LinkPartSpaceCheck_issueColon, linkPart.getLineNumber(),
                            linkPart.getLinkTextOffset() - 1, 2);
                    }
                    else
                    {
                        resultAceptor.addIssue(Messages.LinkPartSpaceCheck_issue, linkPart.getLineNumber(),
                            linkPart.getLinkTextOffset() - 1, 2);
                    }
                }
            }
        }
    }
}