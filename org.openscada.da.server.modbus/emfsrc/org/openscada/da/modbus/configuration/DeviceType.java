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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Device Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.openscada.da.modbus.configuration.DeviceType#getGroup <em>
 * Group</em>}</li>
 * <li>{@link org.openscada.da.modbus.configuration.DeviceType#getSlave <em>
 * Slave</em>}</li>
 * <li>{@link org.openscada.da.modbus.configuration.DeviceType#getBaudRate <em>
 * Baud Rate</em>}</li>
 * <li>{@link org.openscada.da.modbus.configuration.DeviceType#getDataBits <em>
 * Data Bits</em>}</li>
 * <li>{@link org.openscada.da.modbus.configuration.DeviceType#getHost <em>Host
 * </em>}</li>
 * <li>{@link org.openscada.da.modbus.configuration.DeviceType#getId <em>Id
 * </em>}</li>
 * <li>
 * {@link org.openscada.da.modbus.configuration.DeviceType#getInterCharacterTimeout
 * <em>Inter Character Timeout</em>}</li>
 * <li>
 * {@link org.openscada.da.modbus.configuration.DeviceType#getInterFrameDelay
 * <em>Inter Frame Delay</em>}</li>
 * <li>{@link org.openscada.da.modbus.configuration.DeviceType#getParity <em>
 * Parity</em>}</li>
 * <li>{@link org.openscada.da.modbus.configuration.DeviceType#getPort <em>Port
 * </em>}</li>
 * <li>{@link org.openscada.da.modbus.configuration.DeviceType#getProtocol <em>
 * Protocol</em>}</li>
 * <li>{@link org.openscada.da.modbus.configuration.DeviceType#getStopBits <em>
 * Stop Bits</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getDeviceType()
 * @model extendedMetaData="name='DeviceType' kind='elementOnly'"
 * @generated
 */
public interface DeviceType extends EObject
{
    /**
     * Returns the value of the '<em><b>Group</b></em>' attribute list.
     * The list contents are of type
     * {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Group</em>' attribute list isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Group</em>' attribute list.
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getDeviceType_Group()
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry"
     *        many="true"
     *        extendedMetaData="kind='group' name='group:0'"
     * @generated
     */
    FeatureMap getGroup ();

    /**
     * Returns the value of the '<em><b>Slave</b></em>' containment reference
     * list.
     * The list contents are of type
     * {@link org.openscada.da.modbus.configuration.ModbusSlave}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Slave</em>' containment reference list isn't
     * clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Slave</em>' containment reference list.
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getDeviceType_Slave()
     * @model containment="true" required="true" transient="true"
     *        volatile="true" derived="true"
     *        extendedMetaData=
     *        "kind='element' name='slave' namespace='##targetNamespace' group='#group:0'"
     * @generated
     */
    EList<ModbusSlave> getSlave ();

    /**
     * Returns the value of the '<em><b>Baud Rate</b></em>' attribute.
     * The default value is <code>"19200"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Baud Rate</em>' attribute isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Baud Rate</em>' attribute.
     * @see #isSetBaudRate()
     * @see #unsetBaudRate()
     * @see #setBaudRate(int)
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getDeviceType_BaudRate()
     * @model default="19200" unsettable="true"
     *        dataType="org.eclipse.emf.ecore.xml.type.Int"
     *        extendedMetaData="kind='attribute' name='baudRate'"
     * @generated
     */
    int getBaudRate ();

    /**
     * Sets the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getBaudRate
     * <em>Baud Rate</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @param value
     *            the new value of the '<em>Baud Rate</em>' attribute.
     * @see #isSetBaudRate()
     * @see #unsetBaudRate()
     * @see #getBaudRate()
     * @generated
     */
    void setBaudRate ( int value );

    /**
     * Unsets the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getBaudRate
     * <em>Baud Rate</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #isSetBaudRate()
     * @see #getBaudRate()
     * @see #setBaudRate(int)
     * @generated
     */
    void unsetBaudRate ();

    /**
     * Returns whether the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getBaudRate
     * <em>Baud Rate</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @return whether the value of the '<em>Baud Rate</em>' attribute is set.
     * @see #unsetBaudRate()
     * @see #getBaudRate()
     * @see #setBaudRate(int)
     * @generated
     */
    boolean isSetBaudRate ();

    /**
     * Returns the value of the '<em><b>Data Bits</b></em>' attribute.
     * The default value is <code>"8"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Data Bits</em>' attribute isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Data Bits</em>' attribute.
     * @see #isSetDataBits()
     * @see #unsetDataBits()
     * @see #setDataBits(int)
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getDeviceType_DataBits()
     * @model default="8" unsettable="true"
     *        dataType="org.openscada.da.modbus.configuration.DataBitsType"
     *        extendedMetaData="kind='attribute' name='dataBits'"
     * @generated
     */
    int getDataBits ();

    /**
     * Sets the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getDataBits
     * <em>Data Bits</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @param value
     *            the new value of the '<em>Data Bits</em>' attribute.
     * @see #isSetDataBits()
     * @see #unsetDataBits()
     * @see #getDataBits()
     * @generated
     */
    void setDataBits ( int value );

    /**
     * Unsets the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getDataBits
     * <em>Data Bits</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #isSetDataBits()
     * @see #getDataBits()
     * @see #setDataBits(int)
     * @generated
     */
    void unsetDataBits ();

    /**
     * Returns whether the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getDataBits
     * <em>Data Bits</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @return whether the value of the '<em>Data Bits</em>' attribute is set.
     * @see #unsetDataBits()
     * @see #getDataBits()
     * @see #setDataBits(int)
     * @generated
     */
    boolean isSetDataBits ();

    /**
     * Returns the value of the '<em><b>Host</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The hostname or IP address of the device
     * <!-- end-model-doc -->
     * 
     * @return the value of the '<em>Host</em>' attribute.
     * @see #setHost(String)
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getDeviceType_Host()
     * @model dataType="org.openscada.da.modbus.configuration.HostType"
     *        required="true"
     *        extendedMetaData="kind='attribute' name='host'"
     * @generated
     */
    String getHost ();

    /**
     * Sets the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getHost
     * <em>Host</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @param value
     *            the new value of the '<em>Host</em>' attribute.
     * @see #getHost()
     * @generated
     */
    void setHost ( String value );

    /**
     * Returns the value of the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The ID of the device
     * <!-- end-model-doc -->
     * 
     * @return the value of the '<em>Id</em>' attribute.
     * @see #setId(String)
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getDeviceType_Id()
     * @model dataType="org.openscada.da.modbus.configuration.IdType"
     *        required="true"
     *        extendedMetaData="kind='attribute' name='id'"
     * @generated
     */
    String getId ();

    /**
     * Sets the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getId
     * <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @param value
     *            the new value of the '<em>Id</em>' attribute.
     * @see #getId()
     * @generated
     */
    void setId ( String value );

    /**
     * Returns the value of the '<em><b>Inter Character Timeout</b></em>'
     * attribute.
     * The default value is <code>"1.5"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Inter Character Timeout</em>' attribute isn't
     * clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Inter Character Timeout</em>' attribute.
     * @see #isSetInterCharacterTimeout()
     * @see #unsetInterCharacterTimeout()
     * @see #setInterCharacterTimeout(float)
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getDeviceType_InterCharacterTimeout()
     * @model default="1.5" unsettable="true"
     *        dataType="org.eclipse.emf.ecore.xml.type.Float"
     *        extendedMetaData="kind='attribute' name='interCharacterTimeout'"
     * @generated
     */
    float getInterCharacterTimeout ();

    /**
     * Sets the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getInterCharacterTimeout
     * <em>Inter Character Timeout</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @param value
     *            the new value of the '<em>Inter Character Timeout</em>'
     *            attribute.
     * @see #isSetInterCharacterTimeout()
     * @see #unsetInterCharacterTimeout()
     * @see #getInterCharacterTimeout()
     * @generated
     */
    void setInterCharacterTimeout ( float value );

    /**
     * Unsets the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getInterCharacterTimeout
     * <em>Inter Character Timeout</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #isSetInterCharacterTimeout()
     * @see #getInterCharacterTimeout()
     * @see #setInterCharacterTimeout(float)
     * @generated
     */
    void unsetInterCharacterTimeout ();

    /**
     * Returns whether the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getInterCharacterTimeout
     * <em>Inter Character Timeout</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @return whether the value of the '<em>Inter Character Timeout</em>'
     *         attribute is set.
     * @see #unsetInterCharacterTimeout()
     * @see #getInterCharacterTimeout()
     * @see #setInterCharacterTimeout(float)
     * @generated
     */
    boolean isSetInterCharacterTimeout ();

    /**
     * Returns the value of the '<em><b>Inter Frame Delay</b></em>' attribute.
     * The default value is <code>"3.5"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Inter Frame Delay</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Inter Frame Delay</em>' attribute.
     * @see #isSetInterFrameDelay()
     * @see #unsetInterFrameDelay()
     * @see #setInterFrameDelay(float)
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getDeviceType_InterFrameDelay()
     * @model default="3.5" unsettable="true"
     *        dataType="org.eclipse.emf.ecore.xml.type.Float"
     *        extendedMetaData="kind='attribute' name='interFrameDelay'"
     * @generated
     */
    float getInterFrameDelay ();

    /**
     * Sets the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getInterFrameDelay
     * <em>Inter Frame Delay</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @param value
     *            the new value of the '<em>Inter Frame Delay</em>' attribute.
     * @see #isSetInterFrameDelay()
     * @see #unsetInterFrameDelay()
     * @see #getInterFrameDelay()
     * @generated
     */
    void setInterFrameDelay ( float value );

    /**
     * Unsets the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getInterFrameDelay
     * <em>Inter Frame Delay</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #isSetInterFrameDelay()
     * @see #getInterFrameDelay()
     * @see #setInterFrameDelay(float)
     * @generated
     */
    void unsetInterFrameDelay ();

    /**
     * Returns whether the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getInterFrameDelay
     * <em>Inter Frame Delay</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @return whether the value of the '<em>Inter Frame Delay</em>' attribute
     *         is set.
     * @see #unsetInterFrameDelay()
     * @see #getInterFrameDelay()
     * @see #setInterFrameDelay(float)
     * @generated
     */
    boolean isSetInterFrameDelay ();

    /**
     * Returns the value of the '<em><b>Parity</b></em>' attribute.
     * The default value is <code>"NONE"</code>.
     * The literals are from the enumeration
     * {@link org.openscada.da.modbus.configuration.ParityType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parity</em>' attribute isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Parity</em>' attribute.
     * @see org.openscada.da.modbus.configuration.ParityType
     * @see #isSetParity()
     * @see #unsetParity()
     * @see #setParity(ParityType)
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getDeviceType_Parity()
     * @model default="NONE" unsettable="true"
     *        extendedMetaData="kind='attribute' name='parity'"
     * @generated
     */
    ParityType getParity ();

    /**
     * Sets the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getParity
     * <em>Parity</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @param value
     *            the new value of the '<em>Parity</em>' attribute.
     * @see org.openscada.da.modbus.configuration.ParityType
     * @see #isSetParity()
     * @see #unsetParity()
     * @see #getParity()
     * @generated
     */
    void setParity ( ParityType value );

    /**
     * Unsets the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getParity
     * <em>Parity</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #isSetParity()
     * @see #getParity()
     * @see #setParity(ParityType)
     * @generated
     */
    void unsetParity ();

    /**
     * Returns whether the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getParity
     * <em>Parity</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @return whether the value of the '<em>Parity</em>' attribute is set.
     * @see #unsetParity()
     * @see #getParity()
     * @see #setParity(ParityType)
     * @generated
     */
    boolean isSetParity ();

    /**
     * Returns the value of the '<em><b>Port</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The port number of the device
     * <!-- end-model-doc -->
     * 
     * @return the value of the '<em>Port</em>' attribute.
     * @see #isSetPort()
     * @see #unsetPort()
     * @see #setPort(short)
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getDeviceType_Port()
     * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Short"
     *        required="true"
     *        extendedMetaData="kind='attribute' name='port'"
     * @generated
     */
    short getPort ();

    /**
     * Sets the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getPort
     * <em>Port</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @param value
     *            the new value of the '<em>Port</em>' attribute.
     * @see #isSetPort()
     * @see #unsetPort()
     * @see #getPort()
     * @generated
     */
    void setPort ( short value );

    /**
     * Unsets the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getPort
     * <em>Port</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #isSetPort()
     * @see #getPort()
     * @see #setPort(short)
     * @generated
     */
    void unsetPort ();

    /**
     * Returns whether the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getPort
     * <em>Port</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @return whether the value of the '<em>Port</em>' attribute is set.
     * @see #unsetPort()
     * @see #getPort()
     * @see #setPort(short)
     * @generated
     */
    boolean isSetPort ();

    /**
     * Returns the value of the '<em><b>Protocol</b></em>' attribute.
     * The default value is <code>"TCP"</code>.
     * The literals are from the enumeration
     * {@link org.openscada.da.modbus.configuration.ProtocolType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Protocol</em>' attribute isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Protocol</em>' attribute.
     * @see org.openscada.da.modbus.configuration.ProtocolType
     * @see #isSetProtocol()
     * @see #unsetProtocol()
     * @see #setProtocol(ProtocolType)
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getDeviceType_Protocol()
     * @model default="TCP" unsettable="true"
     *        extendedMetaData="kind='attribute' name='protocol'"
     * @generated
     */
    ProtocolType getProtocol ();

    /**
     * Sets the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getProtocol
     * <em>Protocol</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @param value
     *            the new value of the '<em>Protocol</em>' attribute.
     * @see org.openscada.da.modbus.configuration.ProtocolType
     * @see #isSetProtocol()
     * @see #unsetProtocol()
     * @see #getProtocol()
     * @generated
     */
    void setProtocol ( ProtocolType value );

    /**
     * Unsets the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getProtocol
     * <em>Protocol</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #isSetProtocol()
     * @see #getProtocol()
     * @see #setProtocol(ProtocolType)
     * @generated
     */
    void unsetProtocol ();

    /**
     * Returns whether the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getProtocol
     * <em>Protocol</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @return whether the value of the '<em>Protocol</em>' attribute is set.
     * @see #unsetProtocol()
     * @see #getProtocol()
     * @see #setProtocol(ProtocolType)
     * @generated
     */
    boolean isSetProtocol ();

    /**
     * Returns the value of the '<em><b>Stop Bits</b></em>' attribute.
     * The default value is <code>"1"</code>.
     * The literals are from the enumeration
     * {@link org.openscada.da.modbus.configuration.StopBitsType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Stop Bits</em>' attribute isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Stop Bits</em>' attribute.
     * @see org.openscada.da.modbus.configuration.StopBitsType
     * @see #isSetStopBits()
     * @see #unsetStopBits()
     * @see #setStopBits(StopBitsType)
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getDeviceType_StopBits()
     * @model default="1" unsettable="true"
     *        extendedMetaData="kind='attribute' name='stopBits'"
     * @generated
     */
    StopBitsType getStopBits ();

    /**
     * Sets the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getStopBits
     * <em>Stop Bits</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @param value
     *            the new value of the '<em>Stop Bits</em>' attribute.
     * @see org.openscada.da.modbus.configuration.StopBitsType
     * @see #isSetStopBits()
     * @see #unsetStopBits()
     * @see #getStopBits()
     * @generated
     */
    void setStopBits ( StopBitsType value );

    /**
     * Unsets the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getStopBits
     * <em>Stop Bits</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #isSetStopBits()
     * @see #getStopBits()
     * @see #setStopBits(StopBitsType)
     * @generated
     */
    void unsetStopBits ();

    /**
     * Returns whether the value of the '
     * {@link org.openscada.da.modbus.configuration.DeviceType#getStopBits
     * <em>Stop Bits</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @return whether the value of the '<em>Stop Bits</em>' attribute is set.
     * @see #unsetStopBits()
     * @see #getStopBits()
     * @see #setStopBits(StopBitsType)
     * @generated
     */
    boolean isSetStopBits ();

} // DeviceType
