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
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.google.inject.Inject;

/**
 * Replacing a modal call with a non-modal one
 *
 *  @author Ivan Sergeev
 */
@QuickFix(checkId = "dont-use-modality-mode", supplierId = "com.e1c.v8codestyle.bsl")
public class DontUseModalityModeFix
    extends SingleVariantXtextBslModuleFix
{

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public DontUseModalityModeFix(IV8ProjectManager v8ProjectManager)
    {
        super();
        this.v8ProjectManager = v8ProjectManager;
    }

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.interactive(true)
            .description(Messages.DontUseModalityModeFix_Description)
            .details(Messages.DontUseModalityModeFix_Details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        EObject eobject = model.getElement();
        int issueOffset = model.getIssue().getOffset();
        Invocation invocation = (Invocation)eobject;
        FeatureAccess featureAccess = invocation.getMethodAccess();
        IV8Project project = v8ProjectManager.getProject(eobject);
        ScriptVariant languageCode = project.getScriptVariant();
        ICompositeNode node = NodeModelUtils.findActualNodeFor(eobject);
        if (node == null)
        {
            return null;
        }
        String text = node.getText();
        String name = featureAccess.getName();

        if (name.equalsIgnoreCase("ОткрытьЗначение") || name.equalsIgnoreCase("OpenValue")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
            {
                String newMessage = newCallString("ПоказатьЗначение", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall + 1, text.length(), newMessage);
            }
            else
            {
                String newMessage = newCallString("ShowValue", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall + 1, text.length(), newMessage);
            }
        }
        else if (name.equalsIgnoreCase("ОткрытьФормуМодально") || name.equalsIgnoreCase("OpenFormModal")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
            {
                String newMessage = newCallString("ОткрытьФорму ", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall + 1, text.length(), newMessage);
            }
            else
            {
                String newMessage = newCallString("OpenForm", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall + 1, text.length(), newMessage);
            }
        }
        else if (name.equalsIgnoreCase("Вопрос") || name.equalsIgnoreCase("DoQueryBox")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
            {
                String newMessage = newCallString("ПоказатьВопрос", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall + 1, text.length(), newMessage);
            }
            else
            {
                String newMessage = newCallString("ShowQueryBox", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall + 1, text.length(), newMessage);
            }
        }
        else if (name.equalsIgnoreCase("Предупреждение") || name.equalsIgnoreCase("DoMessageBox")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
            {
                String newMessage = newCallString("ПоказатьПредупреждение", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall, text.length(), newMessage);
            }
            else
            {
                String newMessage = newCallString("ShowMessageBox", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall + 1, text.length(), newMessage);
            }
        }
        else if (name.equalsIgnoreCase("ВыбратьИзСписка") || name.equalsIgnoreCase("ChooseFromList")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
            {
                String newMessage = newCallString("ПоказатьВыборИзСписка", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset + indexCall - 1, text.length() - indexCall, newMessage);
            }
            else
            {
                String newMessage = newCallString("ShowChooseFromList", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset + indexCall - 1, text.length() - indexCall, newMessage);
            }
        }
        else if (name.equalsIgnoreCase("ВвестиСтроку") || name.equalsIgnoreCase("InputString")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
            {
                String newMessage = newCallString("ПоказатьВводСтроки", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall + 1, text.length(), newMessage);
            }
            else
            {
                String newMessage = newCallString("ShowInputString", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall + 1, text.length(), newMessage);
            }
        }
        else if (name.equalsIgnoreCase("ВвестиЧисло") || name.equalsIgnoreCase("InputNumber")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
            {
                String newMessage = newCallString("ПоказатьВводЧисла", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall + 1, text.length(), newMessage);
            }
            else
            {
                String newMessage = newCallString("InputNumber", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall + 1, text.length(), newMessage);
            }
        }
        else if (name.equalsIgnoreCase("ВвестиДату") || name.equalsIgnoreCase("InputDate")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
            {
                String newMessage = newCallString("ПоказатьВводДаты", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall, text.length(), newMessage);
            }
            else
            {
                String newMessage = newCallString("ShowInputDate", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall, text.length(), newMessage);
            }
        }
        else if (name.equalsIgnoreCase("ОткрытьМодально") || name.equalsIgnoreCase("DoModal")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
            {
                String newMessage = newCallString("Показать", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall, text.length(), newMessage);
            }
            else
            {
                String newMessage = newCallString("Show", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall, text.length(), newMessage);
            }
        }
        else if (name.equalsIgnoreCase("ПоместитьФайл") || name.equalsIgnoreCase("PutFile")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
            {
                String newMessage = newCallString("НачатьПомещениеФайла", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall + 1, text.length(), newMessage);
            }
            else
            {
                String newMessage = newCallString("BeginPutFile", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall + 1, text.length(), newMessage);
            }
        }
        else if (name.equalsIgnoreCase("ОтметитьЭлементы") || name.equalsIgnoreCase("CheckItems")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
            {
                String newMessage = newCallString("ПоказатьОтметкуЭлементов", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset + indexCall - 1, text.length() - indexCall, newMessage);
            }
            else
            {
                String newMessage = newCallString("ShowCheckItems", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset + indexCall - 1, text.length() - indexCall, newMessage);
            }
        }
        else if (name.equalsIgnoreCase("ВыбратьЭлемент") || name.equalsIgnoreCase("ChooseItem")) //$NON-NLS-1$ //$NON-NLS-2$
        {
            if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
            {
                String newMessage = newCallString("ПоказатьВыборЭлемента", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset + indexCall - 1, text.length() - indexCall, newMessage);
            }
            else
            {
                String newMessage = newCallString("ShowChooseItem", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset + indexCall - 1, text.length() - indexCall, newMessage);
            }
        }
        else if (name.equalsIgnoreCase("УстановитьРасширениеРаботыСФайлами") //$NON-NLS-1$
            || name.equalsIgnoreCase("InstallFileSystemExtension")) //$NON-NLS-1$
        {
            if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
            {
                String newMessage = newCallString("НачатьУстановкуРасширенияРаботыСФайлами", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall + 1, text.length(), newMessage);
            }
            else
            {
                String newMessage = newCallString("BeginInstallFileSystemExtension", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall + 1, text.length(), newMessage);
            }
        }
        else if (name.equalsIgnoreCase("УстановитьВнешнююКомпоненту") //$NON-NLS-1$
            || name.equalsIgnoreCase("InstallAddIn")) //$NON-NLS-1$
        {
            if (languageCode == com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant.RUSSIAN)
            {
                String newMessage = newCallString("НачатьУстановкуВнешнейКомпоненты ", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall + 1, text.length(), newMessage);
            }
            else
            {
                String newMessage = newCallString("BeginInstallAddIn", text, name); //$NON-NLS-1$
                if (newMessage == null)
                {
                    return null;
                }
                int indexCall = text.indexOf(name);
                return new ReplaceEdit(issueOffset - indexCall + 1, text.length(), newMessage);
            }
        }
        return null;
    }

    private String newCallString(String newName, String textOldCall, String oldName)
    {
        int index = textOldCall.indexOf("("); //$NON-NLS-1$
        if (index != -1)
        {
            String newMessage = null;
            String contentString = textOldCall.substring(index + 1, textOldCall.length() - 1);
            if (oldName.equalsIgnoreCase("Предупреждение") || oldName.equalsIgnoreCase("DoMessageBox")) //$NON-NLS-1$ //$NON-NLS-2$
            {
                newMessage = "\n\t" + newName + "(," + contentString + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            else if (oldName.equalsIgnoreCase("ОткрытьФормуМодально") || oldName.equalsIgnoreCase("OpenFormModal") //$NON-NLS-1$ //$NON-NLS-2$
                || oldName.equalsIgnoreCase("УстановитьРасширениеРаботыСФайлами") //$NON-NLS-1$
                || oldName.equalsIgnoreCase("InstallFileSystemExtension") || oldName.equalsIgnoreCase("ОткрытьМодально") //$NON-NLS-1$ //$NON-NLS-2$
                || oldName.equalsIgnoreCase("DoModal")) //$NON-NLS-1$
            {
                newMessage = newName + "(" + contentString + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                newMessage = newName + "(," + contentString + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            }
            return newMessage;
        }
        else
        {
            return null;
        }
    }
}
