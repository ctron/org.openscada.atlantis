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
package org.openscada.da.proxy.configuration.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.openscada.da.proxy.configuration.ConfigurationPackage;
import org.openscada.da.proxy.configuration.ConnectionType;
import org.openscada.da.proxy.configuration.ProxyType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Proxy Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.openscada.da.proxy.configuration.impl.ProxyTypeImpl#getConnection <em>Connection</em>}</li>
 *   <li>{@link org.openscada.da.proxy.configuration.impl.ProxyTypeImpl#getPrefix <em>Prefix</em>}</li>
 *   <li>{@link org.openscada.da.proxy.configuration.impl.ProxyTypeImpl#getWait <em>Wait</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ProxyTypeImpl extends MinimalEObjectImpl.Container implements ProxyType
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getConnection() <em>Connection</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getConnection()
     * @generated
     * @ordered
     */
    protected EList<ConnectionType> connection;

    /**
     * The default value of the '{@link #getPrefix() <em>Prefix</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPrefix()
     * @generated
     * @ordered
     */
    protected static final String PREFIX_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPrefix() <em>Prefix</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPrefix()
     * @generated
     * @ordered
     */
    protected String prefix = PREFIX_EDEFAULT;

    /**
     * The default value of the '{@link #getWait() <em>Wait</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWait()
     * @generated
     * @ordered
     */
    protected static final int WAIT_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getWait() <em>Wait</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWait()
     * @generated
     * @ordered
     */
    protected int wait = WAIT_EDEFAULT;

    /**
     * This is true if the Wait attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean waitESet;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ProxyTypeImpl ()
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
        return ConfigurationPackage.Literals.PROXY_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<ConnectionType> getConnection ()
    {
        if ( connection == null )
        {
            connection = new EObjectContainmentEList<ConnectionType> ( ConnectionType.class, this, ConfigurationPackage.PROXY_TYPE__CONNECTION );
        }
        return connection;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getPrefix ()
    {
        return prefix;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPrefix ( String newPrefix )
    {
        String oldPrefix = prefix;
        prefix = newPrefix;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.PROXY_TYPE__PREFIX, oldPrefix, prefix ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getWait ()
    {
        return wait;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setWait ( int newWait )
    {
        int oldWait = wait;
        wait = newWait;
        boolean oldWaitESet = waitESet;
        waitESet = true;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.PROXY_TYPE__WAIT, oldWait, wait, !oldWaitESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetWait ()
    {
        int oldWait = wait;
        boolean oldWaitESet = waitESet;
        wait = WAIT_EDEFAULT;
        waitESet = false;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.PROXY_TYPE__WAIT, oldWait, WAIT_EDEFAULT, oldWaitESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetWait ()
    {
        return waitESet;
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
            case ConfigurationPackage.PROXY_TYPE__CONNECTION:
                return ( (InternalEList<?>)getConnection () ).basicRemove ( otherEnd, msgs );
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
            case ConfigurationPackage.PROXY_TYPE__CONNECTION:
                return getConnection ();
            case ConfigurationPackage.PROXY_TYPE__PREFIX:
                return getPrefix ();
            case ConfigurationPackage.PROXY_TYPE__WAIT:
                return getWait ();
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
            case ConfigurationPackage.PROXY_TYPE__CONNECTION:
                getConnection ().clear ();
                getConnection ().addAll ( (Collection<? extends ConnectionType>)newValue );
                return;
            case ConfigurationPackage.PROXY_TYPE__PREFIX:
                setPrefix ( (String)newValue );
                return;
            case ConfigurationPackage.PROXY_TYPE__WAIT:
                setWait ( (Integer)newValue );
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
            case ConfigurationPackage.PROXY_TYPE__CONNECTION:
                getConnection ().clear ();
                return;
            case ConfigurationPackage.PROXY_TYPE__PREFIX:
                setPrefix ( PREFIX_EDEFAULT );
                return;
            case ConfigurationPackage.PROXY_TYPE__WAIT:
                unsetWait ();
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
            case ConfigurationPackage.PROXY_TYPE__CONNECTION:
                return connection != null && !connection.isEmpty ();
            case ConfigurationPackage.PROXY_TYPE__PREFIX:
                return PREFIX_EDEFAULT == null ? prefix != null : !PREFIX_EDEFAULT.equals ( prefix );
            case ConfigurationPackage.PROXY_TYPE__WAIT:
                return isSetWait ();
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
        result.append ( " (prefix: " ); //$NON-NLS-1$
        result.append ( prefix );
        result.append ( ", wait: " ); //$NON-NLS-1$
        if ( waitESet )
            result.append ( wait );
        else
            result.append ( "<unset>" ); //$NON-NLS-1$
        result.append ( ')' );
        return result.toString ();
    }

} //ProxyTypeImpl
