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
package org.openscada.da.exec.configuration;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Continuous Command Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.openscada.da.exec.configuration.ContinuousCommandType#getMaxInputBuffer <em>Max Input Buffer</em>}</li>
 *   <li>{@link org.openscada.da.exec.configuration.ContinuousCommandType#getRestartDelay <em>Restart Delay</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.openscada.da.exec.configuration.ConfigurationPackage#getContinuousCommandType()
 * @model extendedMetaData="name='ContinuousCommandType' kind='elementOnly'"
 * @generated
 */
public interface ContinuousCommandType extends CommandType
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Max Input Buffer</b></em>' attribute.
     * The default value is <code>"4000"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Max Input Buffer</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Max Input Buffer</em>' attribute.
     * @see #isSetMaxInputBuffer()
     * @see #unsetMaxInputBuffer()
     * @see #setMaxInputBuffer(int)
     * @see org.openscada.da.exec.configuration.ConfigurationPackage#getContinuousCommandType_MaxInputBuffer()
     * @model default="4000" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
     *        extendedMetaData="kind='attribute' name='maxInputBuffer'"
     * @generated
     */
    int getMaxInputBuffer ();

    /**
     * Sets the value of the '{@link org.openscada.da.exec.configuration.ContinuousCommandType#getMaxInputBuffer <em>Max Input Buffer</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Max Input Buffer</em>' attribute.
     * @see #isSetMaxInputBuffer()
     * @see #unsetMaxInputBuffer()
     * @see #getMaxInputBuffer()
     * @generated
     */
    void setMaxInputBuffer ( int value );

    /**
     * Unsets the value of the '{@link org.openscada.da.exec.configuration.ContinuousCommandType#getMaxInputBuffer <em>Max Input Buffer</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetMaxInputBuffer()
     * @see #getMaxInputBuffer()
     * @see #setMaxInputBuffer(int)
     * @generated
     */
    void unsetMaxInputBuffer ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.exec.configuration.ContinuousCommandType#getMaxInputBuffer <em>Max Input Buffer</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Max Input Buffer</em>' attribute is set.
     * @see #unsetMaxInputBuffer()
     * @see #getMaxInputBuffer()
     * @see #setMaxInputBuffer(int)
     * @generated
     */
    boolean isSetMaxInputBuffer ();

    /**
     * Returns the value of the '<em><b>Restart Delay</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Restart Delay</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Restart Delay</em>' attribute.
     * @see #isSetRestartDelay()
     * @see #unsetRestartDelay()
     * @see #setRestartDelay(int)
     * @see org.openscada.da.exec.configuration.ConfigurationPackage#getContinuousCommandType_RestartDelay()
     * @model unsettable="true" dataType="org.openscada.da.exec.configuration.RestartDelayType" required="true"
     *        extendedMetaData="kind='attribute' name='restartDelay'"
     * @generated
     */
    int getRestartDelay ();

    /**
     * Sets the value of the '{@link org.openscada.da.exec.configuration.ContinuousCommandType#getRestartDelay <em>Restart Delay</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Restart Delay</em>' attribute.
     * @see #isSetRestartDelay()
     * @see #unsetRestartDelay()
     * @see #getRestartDelay()
     * @generated
     */
    void setRestartDelay ( int value );

    /**
     * Unsets the value of the '{@link org.openscada.da.exec.configuration.ContinuousCommandType#getRestartDelay <em>Restart Delay</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetRestartDelay()
     * @see #getRestartDelay()
     * @see #setRestartDelay(int)
     * @generated
     */
    void unsetRestartDelay ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.exec.configuration.ContinuousCommandType#getRestartDelay <em>Restart Delay</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Restart Delay</em>' attribute is set.
     * @see #unsetRestartDelay()
     * @see #getRestartDelay()
     * @see #setRestartDelay(int)
     * @generated
     */
    boolean isSetRestartDelay ();

} // ContinuousCommandType
