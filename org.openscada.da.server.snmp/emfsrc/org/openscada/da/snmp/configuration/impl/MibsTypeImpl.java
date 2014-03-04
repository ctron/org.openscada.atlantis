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
package org.openscada.da.snmp.configuration.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.openscada.da.snmp.configuration.ConfigurationPackage;
import org.openscada.da.snmp.configuration.MibsType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mibs Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.openscada.da.snmp.configuration.impl.MibsTypeImpl#getGroup <em>Group</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.impl.MibsTypeImpl#getStaticMibName <em>Static Mib Name</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.impl.MibsTypeImpl#getMibDir <em>Mib Dir</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.impl.MibsTypeImpl#getRecursiveMibDir <em>Recursive Mib Dir</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MibsTypeImpl extends MinimalEObjectImpl.Container implements MibsType
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

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
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected MibsTypeImpl ()
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
        return ConfigurationPackage.Literals.MIBS_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public FeatureMap getGroup ()
    {
        if ( group == null )
        {
            group = new BasicFeatureMap ( this, ConfigurationPackage.MIBS_TYPE__GROUP );
        }
        return group;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<String> getStaticMibName ()
    {
        return getGroup ().list ( ConfigurationPackage.Literals.MIBS_TYPE__STATIC_MIB_NAME );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<String> getMibDir ()
    {
        return getGroup ().list ( ConfigurationPackage.Literals.MIBS_TYPE__MIB_DIR );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<String> getRecursiveMibDir ()
    {
        return getGroup ().list ( ConfigurationPackage.Literals.MIBS_TYPE__RECURSIVE_MIB_DIR );
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
            case ConfigurationPackage.MIBS_TYPE__GROUP:
                return ( (InternalEList<?>)getGroup () ).basicRemove ( otherEnd, msgs );
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
            case ConfigurationPackage.MIBS_TYPE__GROUP:
                if ( coreType )
                    return getGroup ();
                return ( (FeatureMap.Internal)getGroup () ).getWrapper ();
            case ConfigurationPackage.MIBS_TYPE__STATIC_MIB_NAME:
                return getStaticMibName ();
            case ConfigurationPackage.MIBS_TYPE__MIB_DIR:
                return getMibDir ();
            case ConfigurationPackage.MIBS_TYPE__RECURSIVE_MIB_DIR:
                return getRecursiveMibDir ();
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
            case ConfigurationPackage.MIBS_TYPE__GROUP:
                ( (FeatureMap.Internal)getGroup () ).set ( newValue );
                return;
            case ConfigurationPackage.MIBS_TYPE__STATIC_MIB_NAME:
                getStaticMibName ().clear ();
                getStaticMibName ().addAll ( (Collection<? extends String>)newValue );
                return;
            case ConfigurationPackage.MIBS_TYPE__MIB_DIR:
                getMibDir ().clear ();
                getMibDir ().addAll ( (Collection<? extends String>)newValue );
                return;
            case ConfigurationPackage.MIBS_TYPE__RECURSIVE_MIB_DIR:
                getRecursiveMibDir ().clear ();
                getRecursiveMibDir ().addAll ( (Collection<? extends String>)newValue );
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
            case ConfigurationPackage.MIBS_TYPE__GROUP:
                getGroup ().clear ();
                return;
            case ConfigurationPackage.MIBS_TYPE__STATIC_MIB_NAME:
                getStaticMibName ().clear ();
                return;
            case ConfigurationPackage.MIBS_TYPE__MIB_DIR:
                getMibDir ().clear ();
                return;
            case ConfigurationPackage.MIBS_TYPE__RECURSIVE_MIB_DIR:
                getRecursiveMibDir ().clear ();
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
            case ConfigurationPackage.MIBS_TYPE__GROUP:
                return group != null && !group.isEmpty ();
            case ConfigurationPackage.MIBS_TYPE__STATIC_MIB_NAME:
                return !getStaticMibName ().isEmpty ();
            case ConfigurationPackage.MIBS_TYPE__MIB_DIR:
                return !getMibDir ().isEmpty ();
            case ConfigurationPackage.MIBS_TYPE__RECURSIVE_MIB_DIR:
                return !getRecursiveMibDir ().isEmpty ();
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
        result.append ( ')' );
        return result.toString ();
    }

} //MibsTypeImpl
