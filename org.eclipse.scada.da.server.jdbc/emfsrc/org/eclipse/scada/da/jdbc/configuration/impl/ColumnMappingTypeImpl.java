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
package org.eclipse.scada.da.jdbc.configuration.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.scada.da.jdbc.configuration.ColumnMappingType;
import org.eclipse.scada.da.jdbc.configuration.ConfigurationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Column Mapping Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.scada.da.jdbc.configuration.impl.ColumnMappingTypeImpl#getAliasName <em>Alias Name</em>}</li>
 *   <li>{@link org.eclipse.scada.da.jdbc.configuration.impl.ColumnMappingTypeImpl#getColumnNumber <em>Column Number</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ColumnMappingTypeImpl extends MinimalEObjectImpl.Container implements ColumnMappingType
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getAliasName() <em>Alias Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAliasName()
     * @generated
     * @ordered
     */
    protected static final String ALIAS_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getAliasName() <em>Alias Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAliasName()
     * @generated
     * @ordered
     */
    protected String aliasName = ALIAS_NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getColumnNumber() <em>Column Number</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getColumnNumber()
     * @generated
     * @ordered
     */
    protected static final int COLUMN_NUMBER_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getColumnNumber() <em>Column Number</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getColumnNumber()
     * @generated
     * @ordered
     */
    protected int columnNumber = COLUMN_NUMBER_EDEFAULT;

    /**
     * This is true if the Column Number attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean columnNumberESet;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ColumnMappingTypeImpl ()
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
        return ConfigurationPackage.Literals.COLUMN_MAPPING_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getAliasName ()
    {
        return aliasName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAliasName ( String newAliasName )
    {
        String oldAliasName = aliasName;
        aliasName = newAliasName;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.COLUMN_MAPPING_TYPE__ALIAS_NAME, oldAliasName, aliasName ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getColumnNumber ()
    {
        return columnNumber;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setColumnNumber ( int newColumnNumber )
    {
        int oldColumnNumber = columnNumber;
        columnNumber = newColumnNumber;
        boolean oldColumnNumberESet = columnNumberESet;
        columnNumberESet = true;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.COLUMN_MAPPING_TYPE__COLUMN_NUMBER, oldColumnNumber, columnNumber, !oldColumnNumberESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetColumnNumber ()
    {
        int oldColumnNumber = columnNumber;
        boolean oldColumnNumberESet = columnNumberESet;
        columnNumber = COLUMN_NUMBER_EDEFAULT;
        columnNumberESet = false;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.COLUMN_MAPPING_TYPE__COLUMN_NUMBER, oldColumnNumber, COLUMN_NUMBER_EDEFAULT, oldColumnNumberESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetColumnNumber ()
    {
        return columnNumberESet;
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
            case ConfigurationPackage.COLUMN_MAPPING_TYPE__ALIAS_NAME:
                return getAliasName ();
            case ConfigurationPackage.COLUMN_MAPPING_TYPE__COLUMN_NUMBER:
                return getColumnNumber ();
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
            case ConfigurationPackage.COLUMN_MAPPING_TYPE__ALIAS_NAME:
                setAliasName ( (String)newValue );
                return;
            case ConfigurationPackage.COLUMN_MAPPING_TYPE__COLUMN_NUMBER:
                setColumnNumber ( (Integer)newValue );
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
            case ConfigurationPackage.COLUMN_MAPPING_TYPE__ALIAS_NAME:
                setAliasName ( ALIAS_NAME_EDEFAULT );
                return;
            case ConfigurationPackage.COLUMN_MAPPING_TYPE__COLUMN_NUMBER:
                unsetColumnNumber ();
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
            case ConfigurationPackage.COLUMN_MAPPING_TYPE__ALIAS_NAME:
                return ALIAS_NAME_EDEFAULT == null ? aliasName != null : !ALIAS_NAME_EDEFAULT.equals ( aliasName );
            case ConfigurationPackage.COLUMN_MAPPING_TYPE__COLUMN_NUMBER:
                return isSetColumnNumber ();
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
        result.append ( " (aliasName: " ); //$NON-NLS-1$
        result.append ( aliasName );
        result.append ( ", columnNumber: " ); //$NON-NLS-1$
        if ( columnNumberESet )
            result.append ( columnNumber );
        else
            result.append ( "<unset>" ); //$NON-NLS-1$
        result.append ( ')' );
        return result.toString ();
    }

} //ColumnMappingTypeImpl
