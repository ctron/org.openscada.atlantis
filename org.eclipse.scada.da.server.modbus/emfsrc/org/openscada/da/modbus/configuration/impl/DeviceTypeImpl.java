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

package org.openscada.da.modbus.configuration.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.openscada.da.modbus.configuration.ConfigurationPackage;
import org.openscada.da.modbus.configuration.DeviceType;
import org.openscada.da.modbus.configuration.ModbusSlave;
import org.openscada.da.modbus.configuration.ProtocolType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Device Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.openscada.da.modbus.configuration.impl.DeviceTypeImpl#getGroup <em>Group</em>}</li>
 *   <li>{@link org.openscada.da.modbus.configuration.impl.DeviceTypeImpl#getSlave <em>Slave</em>}</li>
 *   <li>{@link org.openscada.da.modbus.configuration.impl.DeviceTypeImpl#getHost <em>Host</em>}</li>
 *   <li>{@link org.openscada.da.modbus.configuration.impl.DeviceTypeImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.openscada.da.modbus.configuration.impl.DeviceTypeImpl#getInterFrameDelay <em>Inter Frame Delay</em>}</li>
 *   <li>{@link org.openscada.da.modbus.configuration.impl.DeviceTypeImpl#getPort <em>Port</em>}</li>
 *   <li>{@link org.openscada.da.modbus.configuration.impl.DeviceTypeImpl#getProtocol <em>Protocol</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DeviceTypeImpl extends MinimalEObjectImpl.Container implements DeviceType
{
    /**
     * The cached value of the '{@link #getGroup() <em>Group</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getGroup()
     * @generated
     * @ordered
     */
    protected FeatureMap group;

    /**
     * The default value of the '{@link #getHost() <em>Host</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getHost()
     * @generated
     * @ordered
     */
    protected static final String HOST_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getHost() <em>Host</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getHost()
     * @generated
     * @ordered
     */
    protected String host = HOST_EDEFAULT;

    /**
     * The default value of the '{@link #getId() <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getId()
     * @generated
     * @ordered
     */
    protected static final String ID_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getId()
     * @generated
     * @ordered
     */
    protected String id = ID_EDEFAULT;

    /**
     * The default value of the '{@link #getInterFrameDelay() <em>Inter Frame Delay</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInterFrameDelay()
     * @generated
     * @ordered
     */
    protected static final float INTER_FRAME_DELAY_EDEFAULT = 3.5F;

    /**
     * The cached value of the '{@link #getInterFrameDelay() <em>Inter Frame Delay</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInterFrameDelay()
     * @generated
     * @ordered
     */
    protected float interFrameDelay = INTER_FRAME_DELAY_EDEFAULT;

    /**
     * This is true if the Inter Frame Delay attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean interFrameDelayESet;

    /**
     * The default value of the '{@link #getPort() <em>Port</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPort()
     * @generated
     * @ordered
     */
    protected static final short PORT_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getPort() <em>Port</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPort()
     * @generated
     * @ordered
     */
    protected short port = PORT_EDEFAULT;

    /**
     * This is true if the Port attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean portESet;

    /**
     * The default value of the '{@link #getProtocol() <em>Protocol</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProtocol()
     * @generated
     * @ordered
     */
    protected static final ProtocolType PROTOCOL_EDEFAULT = ProtocolType.TCP;

    /**
     * The cached value of the '{@link #getProtocol() <em>Protocol</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProtocol()
     * @generated
     * @ordered
     */
    protected ProtocolType protocol = PROTOCOL_EDEFAULT;

    /**
     * This is true if the Protocol attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean protocolESet;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DeviceTypeImpl ()
    {
        super ();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass ()
    {
        return ConfigurationPackage.Literals.DEVICE_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public FeatureMap getGroup ()
    {
        if ( group == null )
        {
            group = new BasicFeatureMap ( this, ConfigurationPackage.DEVICE_TYPE__GROUP );
        }
        return group;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EList<ModbusSlave> getSlave ()
    {
        return getGroup ().list ( ConfigurationPackage.Literals.DEVICE_TYPE__SLAVE );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getHost ()
    {
        return host;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setHost ( String newHost )
    {
        String oldHost = host;
        host = newHost;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.DEVICE_TYPE__HOST, oldHost, host ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getId ()
    {
        return id;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setId ( String newId )
    {
        String oldId = id;
        id = newId;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.DEVICE_TYPE__ID, oldId, id ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public float getInterFrameDelay ()
    {
        return interFrameDelay;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setInterFrameDelay ( float newInterFrameDelay )
    {
        float oldInterFrameDelay = interFrameDelay;
        interFrameDelay = newInterFrameDelay;
        boolean oldInterFrameDelayESet = interFrameDelayESet;
        interFrameDelayESet = true;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.DEVICE_TYPE__INTER_FRAME_DELAY, oldInterFrameDelay, interFrameDelay, !oldInterFrameDelayESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void unsetInterFrameDelay ()
    {
        float oldInterFrameDelay = interFrameDelay;
        boolean oldInterFrameDelayESet = interFrameDelayESet;
        interFrameDelay = INTER_FRAME_DELAY_EDEFAULT;
        interFrameDelayESet = false;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.DEVICE_TYPE__INTER_FRAME_DELAY, oldInterFrameDelay, INTER_FRAME_DELAY_EDEFAULT, oldInterFrameDelayESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean isSetInterFrameDelay ()
    {
        return interFrameDelayESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public short getPort ()
    {
        return port;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setPort ( short newPort )
    {
        short oldPort = port;
        port = newPort;
        boolean oldPortESet = portESet;
        portESet = true;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.DEVICE_TYPE__PORT, oldPort, port, !oldPortESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void unsetPort ()
    {
        short oldPort = port;
        boolean oldPortESet = portESet;
        port = PORT_EDEFAULT;
        portESet = false;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.DEVICE_TYPE__PORT, oldPort, PORT_EDEFAULT, oldPortESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean isSetPort ()
    {
        return portESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ProtocolType getProtocol ()
    {
        return protocol;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setProtocol ( ProtocolType newProtocol )
    {
        ProtocolType oldProtocol = protocol;
        protocol = newProtocol == null ? PROTOCOL_EDEFAULT : newProtocol;
        boolean oldProtocolESet = protocolESet;
        protocolESet = true;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.DEVICE_TYPE__PROTOCOL, oldProtocol, protocol, !oldProtocolESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void unsetProtocol ()
    {
        ProtocolType oldProtocol = protocol;
        boolean oldProtocolESet = protocolESet;
        protocol = PROTOCOL_EDEFAULT;
        protocolESet = false;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.DEVICE_TYPE__PROTOCOL, oldProtocol, PROTOCOL_EDEFAULT, oldProtocolESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean isSetProtocol ()
    {
        return protocolESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove ( InternalEObject otherEnd, int featureID, NotificationChain msgs )
    {
        switch ( featureID )
        {
            case ConfigurationPackage.DEVICE_TYPE__GROUP:
                return ( (InternalEList<?>)getGroup () ).basicRemove ( otherEnd, msgs );
            case ConfigurationPackage.DEVICE_TYPE__SLAVE:
                return ( (InternalEList<?>)getSlave () ).basicRemove ( otherEnd, msgs );
        }
        return super.eInverseRemove ( otherEnd, featureID, msgs );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet ( int featureID, boolean resolve, boolean coreType )
    {
        switch ( featureID )
        {
            case ConfigurationPackage.DEVICE_TYPE__GROUP:
                if ( coreType )
                    return getGroup ();
                return ( (FeatureMap.Internal)getGroup () ).getWrapper ();
            case ConfigurationPackage.DEVICE_TYPE__SLAVE:
                return getSlave ();
            case ConfigurationPackage.DEVICE_TYPE__HOST:
                return getHost ();
            case ConfigurationPackage.DEVICE_TYPE__ID:
                return getId ();
            case ConfigurationPackage.DEVICE_TYPE__INTER_FRAME_DELAY:
                return getInterFrameDelay ();
            case ConfigurationPackage.DEVICE_TYPE__PORT:
                return getPort ();
            case ConfigurationPackage.DEVICE_TYPE__PROTOCOL:
                return getProtocol ();
        }
        return super.eGet ( featureID, resolve, coreType );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings ( "unchecked" )
    @Override
    public void eSet ( int featureID, Object newValue )
    {
        switch ( featureID )
        {
            case ConfigurationPackage.DEVICE_TYPE__GROUP:
                ( (FeatureMap.Internal)getGroup () ).set ( newValue );
                return;
            case ConfigurationPackage.DEVICE_TYPE__SLAVE:
                getSlave ().clear ();
                getSlave ().addAll ( (Collection<? extends ModbusSlave>)newValue );
                return;
            case ConfigurationPackage.DEVICE_TYPE__HOST:
                setHost ( (String)newValue );
                return;
            case ConfigurationPackage.DEVICE_TYPE__ID:
                setId ( (String)newValue );
                return;
            case ConfigurationPackage.DEVICE_TYPE__INTER_FRAME_DELAY:
                setInterFrameDelay ( (Float)newValue );
                return;
            case ConfigurationPackage.DEVICE_TYPE__PORT:
                setPort ( (Short)newValue );
                return;
            case ConfigurationPackage.DEVICE_TYPE__PROTOCOL:
                setProtocol ( (ProtocolType)newValue );
                return;
        }
        super.eSet ( featureID, newValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset ( int featureID )
    {
        switch ( featureID )
        {
            case ConfigurationPackage.DEVICE_TYPE__GROUP:
                getGroup ().clear ();
                return;
            case ConfigurationPackage.DEVICE_TYPE__SLAVE:
                getSlave ().clear ();
                return;
            case ConfigurationPackage.DEVICE_TYPE__HOST:
                setHost ( HOST_EDEFAULT );
                return;
            case ConfigurationPackage.DEVICE_TYPE__ID:
                setId ( ID_EDEFAULT );
                return;
            case ConfigurationPackage.DEVICE_TYPE__INTER_FRAME_DELAY:
                unsetInterFrameDelay ();
                return;
            case ConfigurationPackage.DEVICE_TYPE__PORT:
                unsetPort ();
                return;
            case ConfigurationPackage.DEVICE_TYPE__PROTOCOL:
                unsetProtocol ();
                return;
        }
        super.eUnset ( featureID );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet ( int featureID )
    {
        switch ( featureID )
        {
            case ConfigurationPackage.DEVICE_TYPE__GROUP:
                return group != null && !group.isEmpty ();
            case ConfigurationPackage.DEVICE_TYPE__SLAVE:
                return !getSlave ().isEmpty ();
            case ConfigurationPackage.DEVICE_TYPE__HOST:
                return HOST_EDEFAULT == null ? host != null : !HOST_EDEFAULT.equals ( host );
            case ConfigurationPackage.DEVICE_TYPE__ID:
                return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals ( id );
            case ConfigurationPackage.DEVICE_TYPE__INTER_FRAME_DELAY:
                return isSetInterFrameDelay ();
            case ConfigurationPackage.DEVICE_TYPE__PORT:
                return isSetPort ();
            case ConfigurationPackage.DEVICE_TYPE__PROTOCOL:
                return isSetProtocol ();
        }
        return super.eIsSet ( featureID );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString ()
    {
        if ( eIsProxy () )
            return super.toString ();

        StringBuffer result = new StringBuffer ( super.toString () );
        result.append ( " (group: " ); //$NON-NLS-1$
        result.append ( group );
        result.append ( ", host: " ); //$NON-NLS-1$
        result.append ( host );
        result.append ( ", id: " ); //$NON-NLS-1$
        result.append ( id );
        result.append ( ", interFrameDelay: " ); //$NON-NLS-1$
        if ( interFrameDelayESet )
            result.append ( interFrameDelay );
        else
            result.append ( "<unset>" ); //$NON-NLS-1$
        result.append ( ", port: " ); //$NON-NLS-1$
        if ( portESet )
            result.append ( port );
        else
            result.append ( "<unset>" ); //$NON-NLS-1$
        result.append ( ", protocol: " ); //$NON-NLS-1$
        if ( protocolESet )
            result.append ( protocol );
        else
            result.append ( "<unset>" ); //$NON-NLS-1$
        result.append ( ')' );
        return result.toString ();
    }

} //DeviceTypeImpl
