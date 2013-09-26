/**
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 * 
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 * 
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */
package org.eclipse.scada.da.exec.configuration;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Process Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.scada.da.exec.configuration.ProcessType#getArgument <em>Argument</em>}</li>
 *   <li>{@link org.eclipse.scada.da.exec.configuration.ProcessType#getEnv <em>Env</em>}</li>
 *   <li>{@link org.eclipse.scada.da.exec.configuration.ProcessType#getExec <em>Exec</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.scada.da.exec.configuration.ConfigurationPackage#getProcessType()
 * @model extendedMetaData="name='ProcessType' kind='elementOnly'"
 * @generated
 */
public interface ProcessType extends EObject
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Argument</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Argument</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Argument</em>' attribute list.
     * @see org.eclipse.scada.da.exec.configuration.ConfigurationPackage#getProcessType_Argument()
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='element' name='argument' namespace='##targetNamespace'"
     * @generated
     */
    EList<String> getArgument ();

    /**
     * Returns the value of the '<em><b>Env</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.scada.da.exec.configuration.EnvEntryType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Env</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Env</em>' containment reference list.
     * @see org.eclipse.scada.da.exec.configuration.ConfigurationPackage#getProcessType_Env()
     * @model containment="true"
     *        extendedMetaData="kind='element' name='env' namespace='##targetNamespace'"
     * @generated
     */
    EList<EnvEntryType> getEnv ();

    /**
     * Returns the value of the '<em><b>Exec</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Exec</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Exec</em>' attribute.
     * @see #setExec(String)
     * @see org.eclipse.scada.da.exec.configuration.ConfigurationPackage#getProcessType_Exec()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='exec'"
     * @generated
     */
    String getExec ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.exec.configuration.ProcessType#getExec <em>Exec</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Exec</em>' attribute.
     * @see #getExec()
     * @generated
     */
    void setExec ( String value );

} // ProcessType
