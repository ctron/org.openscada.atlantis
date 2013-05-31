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
 * A representation of the model object '<em><b>Reg Ex Extractor Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.openscada.da.exec.configuration.RegExExtractorType#getExpression <em>Expression</em>}</li>
 *   <li>{@link org.openscada.da.exec.configuration.RegExExtractorType#isRequireFullMatch <em>Require Full Match</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.openscada.da.exec.configuration.ConfigurationPackage#getRegExExtractorType()
 * @model extendedMetaData="name='RegExExtractorType' kind='elementOnly'"
 * @generated
 */
public interface RegExExtractorType extends FieldExtractorType
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Expression</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Expression</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Expression</em>' attribute.
     * @see #setExpression(String)
     * @see org.openscada.da.exec.configuration.ConfigurationPackage#getRegExExtractorType_Expression()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     *        extendedMetaData="kind='element' name='expression' namespace='##targetNamespace'"
     * @generated
     */
    String getExpression ();

    /**
     * Sets the value of the '{@link org.openscada.da.exec.configuration.RegExExtractorType#getExpression <em>Expression</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Expression</em>' attribute.
     * @see #getExpression()
     * @generated
     */
    void setExpression ( String value );

    /**
     * Returns the value of the '<em><b>Require Full Match</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Require Full Match</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Require Full Match</em>' attribute.
     * @see #isSetRequireFullMatch()
     * @see #unsetRequireFullMatch()
     * @see #setRequireFullMatch(boolean)
     * @see org.openscada.da.exec.configuration.ConfigurationPackage#getRegExExtractorType_RequireFullMatch()
     * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean" required="true"
     *        extendedMetaData="kind='attribute' name='requireFullMatch'"
     * @generated
     */
    boolean isRequireFullMatch ();

    /**
     * Sets the value of the '{@link org.openscada.da.exec.configuration.RegExExtractorType#isRequireFullMatch <em>Require Full Match</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Require Full Match</em>' attribute.
     * @see #isSetRequireFullMatch()
     * @see #unsetRequireFullMatch()
     * @see #isRequireFullMatch()
     * @generated
     */
    void setRequireFullMatch ( boolean value );

    /**
     * Unsets the value of the '{@link org.openscada.da.exec.configuration.RegExExtractorType#isRequireFullMatch <em>Require Full Match</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetRequireFullMatch()
     * @see #isRequireFullMatch()
     * @see #setRequireFullMatch(boolean)
     * @generated
     */
    void unsetRequireFullMatch ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.exec.configuration.RegExExtractorType#isRequireFullMatch <em>Require Full Match</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Require Full Match</em>' attribute is set.
     * @see #unsetRequireFullMatch()
     * @see #isRequireFullMatch()
     * @see #setRequireFullMatch(boolean)
     * @generated
     */
    boolean isSetRequireFullMatch ();

} // RegExExtractorType
