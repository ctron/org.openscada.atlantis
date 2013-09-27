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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Field Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.scada.da.exec.configuration.FieldType#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.scada.da.exec.configuration.FieldType#getVariantType <em>Variant Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.scada.da.exec.configuration.ConfigurationPackage#getFieldType()
 * @model extendedMetaData="name='FieldType' kind='empty'"
 * @generated
 */
public interface FieldType extends EObject
{

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see org.eclipse.scada.da.exec.configuration.ConfigurationPackage#getFieldType_Name()
     * @model dataType="org.eclipse.scada.da.exec.configuration.NameType"
     *        extendedMetaData="kind='attribute' name='name'"
     * @generated
     */
    String getName ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.exec.configuration.FieldType#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName ( String value );

    /**
     * Returns the value of the '<em><b>Variant Type</b></em>' attribute.
     * The default value is <code>"STRING"</code>.
     * The literals are from the enumeration {@link org.eclipse.scada.da.exec.configuration.VariantTypeType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Variant Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Variant Type</em>' attribute.
     * @see org.eclipse.scada.da.exec.configuration.VariantTypeType
     * @see #isSetVariantType()
     * @see #unsetVariantType()
     * @see #setVariantType(VariantTypeType)
     * @see org.eclipse.scada.da.exec.configuration.ConfigurationPackage#getFieldType_VariantType()
     * @model default="STRING" unsettable="true"
     *        extendedMetaData="kind='attribute' name='variantType'"
     * @generated
     */
    VariantTypeType getVariantType ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.exec.configuration.FieldType#getVariantType <em>Variant Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Variant Type</em>' attribute.
     * @see org.eclipse.scada.da.exec.configuration.VariantTypeType
     * @see #isSetVariantType()
     * @see #unsetVariantType()
     * @see #getVariantType()
     * @generated
     */
    void setVariantType ( VariantTypeType value );

    /**
     * Unsets the value of the '{@link org.eclipse.scada.da.exec.configuration.FieldType#getVariantType <em>Variant Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetVariantType()
     * @see #getVariantType()
     * @see #setVariantType(VariantTypeType)
     * @generated
     */
    void unsetVariantType ();

    /**
     * Returns whether the value of the '{@link org.eclipse.scada.da.exec.configuration.FieldType#getVariantType <em>Variant Type</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Variant Type</em>' attribute is set.
     * @see #unsetVariantType()
     * @see #getVariantType()
     * @see #setVariantType(VariantTypeType)
     * @generated
     */
    boolean isSetVariantType ();

} // FieldType
