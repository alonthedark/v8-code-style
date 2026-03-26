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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.EXECUTE_STATEMENT;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.INVOCATION;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.BooleanLiteral;
import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.ExecuteStatement;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.IfPreprocessorStatement;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.PreprocessorItemStatements;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.util.Environment;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Restriction use execute and eval call on server
 *
 *  @author Ivan Sergeev
 */
public class RestrictionExecuteEvalServerCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "restriction-execute-eval-server"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.RestrictionExecuteEvalServerCheck_Title)
            .description(Messages.RestrictionExecuteEvalServerCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.SECURITY)
            .extension(new StandardCheckExtension(770, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(INVOCATION)
            .checkedObjectType(EXECUTE_STATEMENT);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        EObject eObject = (EObject)object;
        if (!(eObject instanceof ExecuteStatement) && !isEval(eObject))
        {
            return;
        }
        Environmental environmental = EcoreUtil2.getContainerOfType(eObject, Environmental.class);
        if (environmental == null)
        {
            return;
        }
        Environments environments = environmental.environments();

        if (environments.contains(Environment.SERVER))
        {
            Method method = EcoreUtil2.getContainerOfType(eObject, Method.class);
            if (method == null)
            {
                return;
            }
            List<Statement> statements = method.allStatements();
            boolean isSafeModeEnable = false;
            for (Statement statement : statements)
            {
                //TODO В будущем тут использовать граф потока управления
                if (hasSafeMode(statement))
                {
                    isSafeModeEnable = true;
                }
            }
            if (!isSafeModeEnable)
            {
                resultAceptor.addIssue(Messages.RestrictionExecuteEvalServerCheck_Issue);
            }
        }
    }

    private boolean isEval(EObject eObject)
    {
        if (eObject instanceof Invocation invocation)
        {
            String name = invocation.getMethodAccess().getName();
            String text = NodeModelUtils.findActualNodeFor(invocation.eContainer()).getText();
            if (text.toLowerCase().contains("выражениеxpath") || text.toLowerCase().contains("xpathexpression")) //$NON-NLS-1$ //$NON-NLS-2$
            {
                return false;
            }
            return "Вычислить".equalsIgnoreCase(name) || "Eval".equalsIgnoreCase(name); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return false;
    }

    private boolean hasSafeMode(Statement statement)
    {
        if (statement instanceof SimpleStatement simpleStatement)
        {
            Expression leftExpression = simpleStatement.getLeft();
            if (!(leftExpression instanceof Invocation))
            {
                return false;
            }
            Invocation invocation = (Invocation)leftExpression;
            String name = invocation.getMethodAccess().getName();

            if (("УстановитьБезопасныйРежим".equalsIgnoreCase(name) || "SetSafeMode".equalsIgnoreCase(name)) //$NON-NLS-1$//$NON-NLS-2$
                && invocation.getParams().size() == 1 && invocation.getParams().get(0) instanceof BooleanLiteral)
            {
                return ((BooleanLiteral)invocation.getParams().get(0)).isIsTrue();
            }
        }
        else if (statement instanceof IfStatement ifStatement)
        {
            List<Statement> inIfStatements = ifStatement.getIfPart().getStatements();
            List<Conditional> inElsIfStatements = ifStatement.getElsIfParts();
            boolean safeModeStatus = false;
            for (Statement statementFromIf : inIfStatements)
            {
                if (hasSafeMode(statementFromIf))
                {
                    safeModeStatus = true;
                }
            }
            for (Conditional conditional : inElsIfStatements)
            {
                List<Statement> statemenstCond = conditional.getStatements();
                for (Statement statementFromElsIf : statemenstCond)
                {
                    if (hasSafeMode(statementFromElsIf))
                    {
                        return true;
                    }
                }
            }
            return safeModeStatus;
        }
        else if (statement instanceof TryExceptStatement tryExceptStatement)
        {
            List<Statement> tryStatements = tryExceptStatement.getTryStatements();
            for (Statement tryStatement : tryStatements)
            {
                if (hasSafeMode(tryStatement))
                {
                    return true;
                }
            }
            return false;
        }
        else if (statement instanceof IfPreprocessorStatement ifPrepStat)
        {
            List<EObject> ifObjects = ifPrepStat.getIfPart().eContents();
            NodeModelUtils.findActualNodeFor(ifPrepStat).getText();
            for (EObject eObject : ifObjects)
            {
                if (eObject instanceof PreprocessorItemStatements preprocessorItemStatements)
                {
                    List<Statement> ifStatements = preprocessorItemStatements.getStatements();
                    for (Statement ifStatement : ifStatements)
                    {
                        if (hasSafeMode(ifStatement))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
