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
package org.eclipse.scada.common;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.scada.common.CommonPackage
 * @generated
 */
public interface CommonFactory extends EFactory
{
    
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    CommonFactory eINSTANCE = org.eclipse.scada.common.impl.CommonFactoryImpl.init ();

    /**
     * Returns a new object of class '<em>Attributes Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Attributes Type</em>'.
     * @generated
     */
    AttributesType createAttributesType ();

    /**
     * Returns a new object of class '<em>Attribute Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Attribute Type</em>'.
     * @generated
     */
    AttributeType createAttributeType ();

    /**
     * Returns a new object of class '<em>Variant Boolean Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Variant Boolean Type</em>'.
     * @generated
     */
    VariantBooleanType createVariantBooleanType ();

    /**
     * Returns a new object of class '<em>Variant Double Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Variant Double Type</em>'.
     * @generated
     */
    VariantDoubleType createVariantDoubleType ();

    /**
     * Returns a new object of class '<em>Variant Int32 Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Variant Int32 Type</em>'.
     * @generated
     */
    VariantInt32Type createVariantInt32Type ();

    /**
     * Returns a new object of class '<em>Variant Int64 Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Variant Int64 Type</em>'.
     * @generated
     */
    VariantInt64Type createVariantInt64Type ();

    /**
     * Returns a new object of class '<em>Variant Null Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Variant Null Type</em>'.
     * @generated
     */
    VariantNullType createVariantNullType ();

    /**
     * Returns a new object of class '<em>Variant Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Variant Type</em>'.
     * @generated
     */
    VariantType createVariantType ();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    CommonPackage getCommonPackage ();

} //CommonFactory
