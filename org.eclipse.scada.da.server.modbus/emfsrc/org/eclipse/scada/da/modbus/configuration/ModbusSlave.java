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

package org.eclipse.scada.da.modbus.configuration;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Modbus Slave</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getGroup <em>Group</em>}</li>
 *   <li>{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getDiscreteInput <em>Discrete Input</em>}</li>
 *   <li>{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getGroup1 <em>Group1</em>}</li>
 *   <li>{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getCoil <em>Coil</em>}</li>
 *   <li>{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getGroup2 <em>Group2</em>}</li>
 *   <li>{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getInputRegister <em>Input Register</em>}</li>
 *   <li>{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getGroup3 <em>Group3</em>}</li>
 *   <li>{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getHoldingRegister <em>Holding Register</em>}</li>
 *   <li>{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getCoilOffset <em>Coil Offset</em>}</li>
 *   <li>{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getDiscreteInputOffset <em>Discrete Input Offset</em>}</li>
 *   <li>{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getHoldingRegisterOffset <em>Holding Register Offset</em>}</li>
 *   <li>{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getInputRegisterOffset <em>Input Register Offset</em>}</li>
 *   <li>{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getModbusSlave()
 * @model extendedMetaData="name='ModbusSlave' kind='elementOnly'"
 * @generated
 */
public interface ModbusSlave extends EObject
{
    /**
     * Returns the value of the '<em><b>Group</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Group</em>' attribute list isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Group</em>' attribute list.
     * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getModbusSlave_Group()
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     *        extendedMetaData="kind='group' name='group:0'"
     * @generated
     */
    FeatureMap getGroup ();

    /**
     * Returns the value of the '<em><b>Discrete Input</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.scada.da.modbus.configuration.ItemType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Discrete Input</em>' containment reference
     * list isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Discrete Input</em>' containment reference list.
     * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getModbusSlave_DiscreteInput()
     * @model containment="true" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='discreteInput' namespace='##targetNamespace' group='#group:0'"
     * @generated
     */
    EList<ItemType> getDiscreteInput ();

    /**
     * Returns the value of the '<em><b>Group1</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Group1</em>' attribute list isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Group1</em>' attribute list.
     * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getModbusSlave_Group1()
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     *        extendedMetaData="kind='group' name='group:2'"
     * @generated
     */
    FeatureMap getGroup1 ();

    /**
     * Returns the value of the '<em><b>Coil</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.scada.da.modbus.configuration.ItemType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Coil</em>' containment reference list isn't
     * clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Coil</em>' containment reference list.
     * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getModbusSlave_Coil()
     * @model containment="true" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='coil' namespace='##targetNamespace' group='#group:2'"
     * @generated
     */
    EList<ItemType> getCoil ();

    /**
     * Returns the value of the '<em><b>Group2</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Group2</em>' attribute list isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Group2</em>' attribute list.
     * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getModbusSlave_Group2()
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     *        extendedMetaData="kind='group' name='group:4'"
     * @generated
     */
    FeatureMap getGroup2 ();

    /**
     * Returns the value of the '<em><b>Input Register</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.scada.da.modbus.configuration.ItemType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Input Register</em>' containment reference
     * list isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Input Register</em>' containment reference list.
     * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getModbusSlave_InputRegister()
     * @model containment="true" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='inputRegister' namespace='##targetNamespace' group='#group:4'"
     * @generated
     */
    EList<ItemType> getInputRegister ();

    /**
     * Returns the value of the '<em><b>Group3</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Group3</em>' attribute list isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Group3</em>' attribute list.
     * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getModbusSlave_Group3()
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     *        extendedMetaData="kind='group' name='group:6'"
     * @generated
     */
    FeatureMap getGroup3 ();

    /**
     * Returns the value of the '<em><b>Holding Register</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.scada.da.modbus.configuration.ItemType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Holding Register</em>' containment reference
     * list isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Holding Register</em>' containment reference list.
     * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getModbusSlave_HoldingRegister()
     * @model containment="true" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='holdingRegister' namespace='##targetNamespace' group='#group:6'"
     * @generated
     */
    EList<ItemType> getHoldingRegister ();

    /**
     * Returns the value of the '<em><b>Coil Offset</b></em>' attribute.
     * The default value is <code>"0"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Coil Offset</em>' attribute isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Coil Offset</em>' attribute.
     * @see #isSetCoilOffset()
     * @see #unsetCoilOffset()
     * @see #setCoilOffset(int)
     * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getModbusSlave_CoilOffset()
     * @model default="0" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
     *        extendedMetaData="kind='attribute' name='coilOffset'"
     * @generated
     */
    int getCoilOffset ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getCoilOffset <em>Coil Offset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Coil Offset</em>' attribute.
     * @see #isSetCoilOffset()
     * @see #unsetCoilOffset()
     * @see #getCoilOffset()
     * @generated
     */
    void setCoilOffset ( int value );

    /**
     * Unsets the value of the '{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getCoilOffset <em>Coil Offset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetCoilOffset()
     * @see #getCoilOffset()
     * @see #setCoilOffset(int)
     * @generated
     */
    void unsetCoilOffset ();

    /**
     * Returns whether the value of the '{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getCoilOffset <em>Coil Offset</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Coil Offset</em>' attribute is set.
     * @see #unsetCoilOffset()
     * @see #getCoilOffset()
     * @see #setCoilOffset(int)
     * @generated
     */
    boolean isSetCoilOffset ();

    /**
     * Returns the value of the '<em><b>Discrete Input Offset</b></em>' attribute.
     * The default value is <code>"0"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Discrete Input Offset</em>' attribute isn't
     * clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Discrete Input Offset</em>' attribute.
     * @see #isSetDiscreteInputOffset()
     * @see #unsetDiscreteInputOffset()
     * @see #setDiscreteInputOffset(int)
     * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getModbusSlave_DiscreteInputOffset()
     * @model default="0" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
     *        extendedMetaData="kind='attribute' name='discreteInputOffset'"
     * @generated
     */
    int getDiscreteInputOffset ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getDiscreteInputOffset <em>Discrete Input Offset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Discrete Input Offset</em>' attribute.
     * @see #isSetDiscreteInputOffset()
     * @see #unsetDiscreteInputOffset()
     * @see #getDiscreteInputOffset()
     * @generated
     */
    void setDiscreteInputOffset ( int value );

    /**
     * Unsets the value of the '{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getDiscreteInputOffset <em>Discrete Input Offset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetDiscreteInputOffset()
     * @see #getDiscreteInputOffset()
     * @see #setDiscreteInputOffset(int)
     * @generated
     */
    void unsetDiscreteInputOffset ();

    /**
     * Returns whether the value of the '{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getDiscreteInputOffset <em>Discrete Input Offset</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Discrete Input Offset</em>' attribute is set.
     * @see #unsetDiscreteInputOffset()
     * @see #getDiscreteInputOffset()
     * @see #setDiscreteInputOffset(int)
     * @generated
     */
    boolean isSetDiscreteInputOffset ();

    /**
     * Returns the value of the '<em><b>Holding Register Offset</b></em>' attribute.
     * The default value is <code>"0"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Holding Register Offset</em>' attribute isn't
     * clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Holding Register Offset</em>' attribute.
     * @see #isSetHoldingRegisterOffset()
     * @see #unsetHoldingRegisterOffset()
     * @see #setHoldingRegisterOffset(int)
     * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getModbusSlave_HoldingRegisterOffset()
     * @model default="0" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
     *        extendedMetaData="kind='attribute' name='holdingRegisterOffset'"
     * @generated
     */
    int getHoldingRegisterOffset ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getHoldingRegisterOffset <em>Holding Register Offset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Holding Register Offset</em>' attribute.
     * @see #isSetHoldingRegisterOffset()
     * @see #unsetHoldingRegisterOffset()
     * @see #getHoldingRegisterOffset()
     * @generated
     */
    void setHoldingRegisterOffset ( int value );

    /**
     * Unsets the value of the '{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getHoldingRegisterOffset <em>Holding Register Offset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetHoldingRegisterOffset()
     * @see #getHoldingRegisterOffset()
     * @see #setHoldingRegisterOffset(int)
     * @generated
     */
    void unsetHoldingRegisterOffset ();

    /**
     * Returns whether the value of the '{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getHoldingRegisterOffset <em>Holding Register Offset</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Holding Register Offset</em>' attribute is set.
     * @see #unsetHoldingRegisterOffset()
     * @see #getHoldingRegisterOffset()
     * @see #setHoldingRegisterOffset(int)
     * @generated
     */
    boolean isSetHoldingRegisterOffset ();

    /**
     * Returns the value of the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Id</em>' attribute isn't clear, there really
     * should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Id</em>' attribute.
     * @see #isSetId()
     * @see #unsetId()
     * @see #setId(int)
     * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getModbusSlave_Id()
     * @model unsettable="true" dataType="org.eclipse.scada.da.modbus.configuration.IdType1" required="true"
     *        extendedMetaData="kind='attribute' name='id'"
     * @generated
     */
    int getId ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getId <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Id</em>' attribute.
     * @see #isSetId()
     * @see #unsetId()
     * @see #getId()
     * @generated
     */
    void setId ( int value );

    /**
     * Unsets the value of the '{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getId <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetId()
     * @see #getId()
     * @see #setId(int)
     * @generated
     */
    void unsetId ();

    /**
     * Returns whether the value of the '{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getId <em>Id</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Id</em>' attribute is set.
     * @see #unsetId()
     * @see #getId()
     * @see #setId(int)
     * @generated
     */
    boolean isSetId ();

    /**
     * Returns the value of the '<em><b>Input Register Offset</b></em>' attribute.
     * The default value is <code>"0"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Input Register Offset</em>' attribute isn't
     * clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Input Register Offset</em>' attribute.
     * @see #isSetInputRegisterOffset()
     * @see #unsetInputRegisterOffset()
     * @see #setInputRegisterOffset(int)
     * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getModbusSlave_InputRegisterOffset()
     * @model default="0" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
     *        extendedMetaData="kind='attribute' name='inputRegisterOffset'"
     * @generated
     */
    int getInputRegisterOffset ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getInputRegisterOffset <em>Input Register Offset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Input Register Offset</em>' attribute.
     * @see #isSetInputRegisterOffset()
     * @see #unsetInputRegisterOffset()
     * @see #getInputRegisterOffset()
     * @generated
     */
    void setInputRegisterOffset ( int value );

    /**
     * Unsets the value of the '{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getInputRegisterOffset <em>Input Register Offset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetInputRegisterOffset()
     * @see #getInputRegisterOffset()
     * @see #setInputRegisterOffset(int)
     * @generated
     */
    void unsetInputRegisterOffset ();

    /**
     * Returns whether the value of the '{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getInputRegisterOffset <em>Input Register Offset</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Input Register Offset</em>' attribute is set.
     * @see #unsetInputRegisterOffset()
     * @see #getInputRegisterOffset()
     * @see #setInputRegisterOffset(int)
     * @generated
     */
    boolean isSetInputRegisterOffset ();

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
     * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getModbusSlave_Name()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='name'"
     * @generated
     */
    String getName ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.modbus.configuration.ModbusSlave#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName ( String value );

} // ModbusSlave
