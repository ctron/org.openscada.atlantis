/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.modbus.configuration;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Item Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.openscada.da.modbus.configuration.ItemType#getName <em>Name</em>}</li>
 *   <li>{@link org.openscada.da.modbus.configuration.ItemType#getPriority <em>Priority</em>}</li>
 *   <li>{@link org.openscada.da.modbus.configuration.ItemType#getQuantity <em>Quantity</em>}</li>
 *   <li>{@link org.openscada.da.modbus.configuration.ItemType#getStartAddress <em>Start Address</em>}</li>
 *   <li>{@link org.openscada.da.modbus.configuration.ItemType#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getItemType()
 * @model extendedMetaData="name='ItemType' kind='empty'"
 * @generated
 */
public interface ItemType extends EObject
{
    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear, there really
     * should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getItemType_Name()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='name'"
     * @generated
     */
    String getName ();

    /**
     * Sets the value of the '{@link org.openscada.da.modbus.configuration.ItemType#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName ( String value );

    /**
     * Returns the value of the '<em><b>Priority</b></em>' attribute.
     * The default value is <code>"1"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Priority</em>' attribute isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Priority</em>' attribute.
     * @see #isSetPriority()
     * @see #unsetPriority()
     * @see #setPriority(int)
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getItemType_Priority()
     * @model default="1" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
     *        extendedMetaData="kind='attribute' name='priority'"
     * @generated
     */
    int getPriority ();

    /**
     * Sets the value of the '{@link org.openscada.da.modbus.configuration.ItemType#getPriority <em>Priority</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Priority</em>' attribute.
     * @see #isSetPriority()
     * @see #unsetPriority()
     * @see #getPriority()
     * @generated
     */
    void setPriority ( int value );

    /**
     * Unsets the value of the '{@link org.openscada.da.modbus.configuration.ItemType#getPriority <em>Priority</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetPriority()
     * @see #getPriority()
     * @see #setPriority(int)
     * @generated
     */
    void unsetPriority ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.modbus.configuration.ItemType#getPriority <em>Priority</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Priority</em>' attribute is set.
     * @see #unsetPriority()
     * @see #getPriority()
     * @see #setPriority(int)
     * @generated
     */
    boolean isSetPriority ();

    /**
     * Returns the value of the '<em><b>Quantity</b></em>' attribute.
     * The default value is <code>"1"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Quantity</em>' attribute isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Quantity</em>' attribute.
     * @see #isSetQuantity()
     * @see #unsetQuantity()
     * @see #setQuantity(int)
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getItemType_Quantity()
     * @model default="1" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
     *        extendedMetaData="kind='attribute' name='quantity'"
     * @generated
     */
    int getQuantity ();

    /**
     * Sets the value of the '{@link org.openscada.da.modbus.configuration.ItemType#getQuantity <em>Quantity</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Quantity</em>' attribute.
     * @see #isSetQuantity()
     * @see #unsetQuantity()
     * @see #getQuantity()
     * @generated
     */
    void setQuantity ( int value );

    /**
     * Unsets the value of the '{@link org.openscada.da.modbus.configuration.ItemType#getQuantity <em>Quantity</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetQuantity()
     * @see #getQuantity()
     * @see #setQuantity(int)
     * @generated
     */
    void unsetQuantity ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.modbus.configuration.ItemType#getQuantity <em>Quantity</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Quantity</em>' attribute is set.
     * @see #unsetQuantity()
     * @see #getQuantity()
     * @see #setQuantity(int)
     * @generated
     */
    boolean isSetQuantity ();

    /**
     * Returns the value of the '<em><b>Start Address</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Start Address</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Start Address</em>' attribute.
     * @see #setStartAddress(String)
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getItemType_StartAddress()
     * @model dataType="org.openscada.da.modbus.configuration.StartAddressType"
     *        extendedMetaData="kind='attribute' name='startAddress'"
     * @generated
     */
    String getStartAddress ();

    /**
     * Sets the value of the '{@link org.openscada.da.modbus.configuration.ItemType#getStartAddress <em>Start Address</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Start Address</em>' attribute.
     * @see #getStartAddress()
     * @generated
     */
    void setStartAddress ( String value );

    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute.
     * The default value is <code>"DEFAULT"</code>.
     * The literals are from the enumeration {@link org.openscada.da.modbus.configuration.TypeType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' attribute isn't clear, there really
     * should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' attribute.
     * @see org.openscada.da.modbus.configuration.TypeType
     * @see #isSetType()
     * @see #unsetType()
     * @see #setType(TypeType)
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getItemType_Type()
     * @model default="DEFAULT" unsettable="true"
     *        extendedMetaData="kind='attribute' name='type'"
     * @generated
     */
    TypeType getType ();

    /**
     * Sets the value of the '{@link org.openscada.da.modbus.configuration.ItemType#getType <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see org.openscada.da.modbus.configuration.TypeType
     * @see #isSetType()
     * @see #unsetType()
     * @see #getType()
     * @generated
     */
    void setType ( TypeType value );

    /**
     * Unsets the value of the '{@link org.openscada.da.modbus.configuration.ItemType#getType <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetType()
     * @see #getType()
     * @see #setType(TypeType)
     * @generated
     */
    void unsetType ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.modbus.configuration.ItemType#getType <em>Type</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Type</em>' attribute is set.
     * @see #unsetType()
     * @see #getType()
     * @see #setType(TypeType)
     * @generated
     */
    boolean isSetType ();

} // ItemType
