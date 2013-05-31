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
package org.openscada.da.opc.configuration.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.openscada.da.opc.configuration.ConfigurationPackage;
import org.openscada.da.opc.configuration.InitialItemType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Initial Item Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.openscada.da.opc.configuration.impl.InitialItemTypeImpl#getAccessPath <em>Access Path</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.InitialItemTypeImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.InitialItemTypeImpl#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InitialItemTypeImpl extends MinimalEObjectImpl.Container implements InitialItemType
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getAccessPath() <em>Access Path</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAccessPath()
     * @generated
     * @ordered
     */
    protected static final String ACCESS_PATH_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getAccessPath() <em>Access Path</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAccessPath()
     * @generated
     * @ordered
     */
    protected String accessPath = ACCESS_PATH_EDEFAULT;

    /**
     * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDescription()
     * @generated
     * @ordered
     */
    protected static final String DESCRIPTION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDescription()
     * @generated
     * @ordered
     */
    protected String description = DESCRIPTION_EDEFAULT;

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
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected InitialItemTypeImpl ()
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
        return ConfigurationPackage.Literals.INITIAL_ITEM_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getAccessPath ()
    {
        return accessPath;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAccessPath ( String newAccessPath )
    {
        String oldAccessPath = accessPath;
        accessPath = newAccessPath;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.INITIAL_ITEM_TYPE__ACCESS_PATH, oldAccessPath, accessPath ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDescription ()
    {
        return description;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDescription ( String newDescription )
    {
        String oldDescription = description;
        description = newDescription;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.INITIAL_ITEM_TYPE__DESCRIPTION, oldDescription, description ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getId ()
    {
        return id;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setId ( String newId )
    {
        String oldId = id;
        id = newId;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.INITIAL_ITEM_TYPE__ID, oldId, id ) );
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
            case ConfigurationPackage.INITIAL_ITEM_TYPE__ACCESS_PATH:
                return getAccessPath ();
            case ConfigurationPackage.INITIAL_ITEM_TYPE__DESCRIPTION:
                return getDescription ();
            case ConfigurationPackage.INITIAL_ITEM_TYPE__ID:
                return getId ();
        }
        return super.eGet ( featureID, resolve, coreType );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet ( int featureID, Object newValue )
    {
        switch ( featureID )
        {
            case ConfigurationPackage.INITIAL_ITEM_TYPE__ACCESS_PATH:
                setAccessPath ( (String)newValue );
                return;
            case ConfigurationPackage.INITIAL_ITEM_TYPE__DESCRIPTION:
                setDescription ( (String)newValue );
                return;
            case ConfigurationPackage.INITIAL_ITEM_TYPE__ID:
                setId ( (String)newValue );
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
            case ConfigurationPackage.INITIAL_ITEM_TYPE__ACCESS_PATH:
                setAccessPath ( ACCESS_PATH_EDEFAULT );
                return;
            case ConfigurationPackage.INITIAL_ITEM_TYPE__DESCRIPTION:
                setDescription ( DESCRIPTION_EDEFAULT );
                return;
            case ConfigurationPackage.INITIAL_ITEM_TYPE__ID:
                setId ( ID_EDEFAULT );
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
            case ConfigurationPackage.INITIAL_ITEM_TYPE__ACCESS_PATH:
                return ACCESS_PATH_EDEFAULT == null ? accessPath != null : !ACCESS_PATH_EDEFAULT.equals ( accessPath );
            case ConfigurationPackage.INITIAL_ITEM_TYPE__DESCRIPTION:
                return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals ( description );
            case ConfigurationPackage.INITIAL_ITEM_TYPE__ID:
                return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals ( id );
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
        result.append ( " (accessPath: " ); //$NON-NLS-1$
        result.append ( accessPath );
        result.append ( ", description: " ); //$NON-NLS-1$
        result.append ( description );
        result.append ( ", id: " ); //$NON-NLS-1$
        result.append ( id );
        result.append ( ')' );
        return result.toString ();
    }

} //InitialItemTypeImpl
