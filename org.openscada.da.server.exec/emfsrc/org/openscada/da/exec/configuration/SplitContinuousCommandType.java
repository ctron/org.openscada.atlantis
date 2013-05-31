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
 * A representation of the model object '<em><b>Split Continuous Command Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.openscada.da.exec.configuration.SplitContinuousCommandType#getSplitter <em>Splitter</em>}</li>
 *   <li>{@link org.openscada.da.exec.configuration.SplitContinuousCommandType#getIgnoreStartLines <em>Ignore Start Lines</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.openscada.da.exec.configuration.ConfigurationPackage#getSplitContinuousCommandType()
 * @model extendedMetaData="name='SplitContinuousCommandType' kind='elementOnly'"
 * @generated
 */
public interface SplitContinuousCommandType extends ContinuousCommandType
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Splitter</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Splitter</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Splitter</em>' containment reference.
     * @see #setSplitter(SplitterType)
     * @see org.openscada.da.exec.configuration.ConfigurationPackage#getSplitContinuousCommandType_Splitter()
     * @model containment="true" required="true"
     *        extendedMetaData="kind='element' name='splitter' namespace='##targetNamespace'"
     * @generated
     */
    SplitterType getSplitter ();

    /**
     * Sets the value of the '{@link org.openscada.da.exec.configuration.SplitContinuousCommandType#getSplitter <em>Splitter</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Splitter</em>' containment reference.
     * @see #getSplitter()
     * @generated
     */
    void setSplitter ( SplitterType value );

    /**
     * Returns the value of the '<em><b>Ignore Start Lines</b></em>' attribute.
     * The default value is <code>"0"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Ignore Start Lines</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Ignore Start Lines</em>' attribute.
     * @see #isSetIgnoreStartLines()
     * @see #unsetIgnoreStartLines()
     * @see #setIgnoreStartLines(int)
     * @see org.openscada.da.exec.configuration.ConfigurationPackage#getSplitContinuousCommandType_IgnoreStartLines()
     * @model default="0" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
     *        extendedMetaData="kind='attribute' name='ignoreStartLines'"
     * @generated
     */
    int getIgnoreStartLines ();

    /**
     * Sets the value of the '{@link org.openscada.da.exec.configuration.SplitContinuousCommandType#getIgnoreStartLines <em>Ignore Start Lines</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Ignore Start Lines</em>' attribute.
     * @see #isSetIgnoreStartLines()
     * @see #unsetIgnoreStartLines()
     * @see #getIgnoreStartLines()
     * @generated
     */
    void setIgnoreStartLines ( int value );

    /**
     * Unsets the value of the '{@link org.openscada.da.exec.configuration.SplitContinuousCommandType#getIgnoreStartLines <em>Ignore Start Lines</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetIgnoreStartLines()
     * @see #getIgnoreStartLines()
     * @see #setIgnoreStartLines(int)
     * @generated
     */
    void unsetIgnoreStartLines ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.exec.configuration.SplitContinuousCommandType#getIgnoreStartLines <em>Ignore Start Lines</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Ignore Start Lines</em>' attribute is set.
     * @see #unsetIgnoreStartLines()
     * @see #getIgnoreStartLines()
     * @see #setIgnoreStartLines(int)
     * @generated
     */
    boolean isSetIgnoreStartLines ();

} // SplitContinuousCommandType
