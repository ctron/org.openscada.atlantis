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
package org.openscada.da.jdbc.configuration.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.openscada.da.jdbc.configuration.ConfigurationPackage;
import org.openscada.da.jdbc.configuration.TabularQueryType;
import org.openscada.da.jdbc.configuration.UpdateColumnsType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Tabular Query Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.openscada.da.jdbc.configuration.impl.TabularQueryTypeImpl#getUpdateColumns <em>Update Columns</em>}</li>
 *   <li>{@link org.openscada.da.jdbc.configuration.impl.TabularQueryTypeImpl#getDefaultUpdateSql <em>Default Update Sql</em>}</li>
 *   <li>{@link org.openscada.da.jdbc.configuration.impl.TabularQueryTypeImpl#getDefaultUpdateSql1 <em>Default Update Sql1</em>}</li>
 *   <li>{@link org.openscada.da.jdbc.configuration.impl.TabularQueryTypeImpl#getIdColumn <em>Id Column</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TabularQueryTypeImpl extends AbstractQueryTypeImpl implements TabularQueryType
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getUpdateColumns() <em>Update Columns</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUpdateColumns()
     * @generated
     * @ordered
     */
    protected EList<UpdateColumnsType> updateColumns;

    /**
     * The default value of the '{@link #getDefaultUpdateSql() <em>Default Update Sql</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDefaultUpdateSql()
     * @generated
     * @ordered
     */
    protected static final String DEFAULT_UPDATE_SQL_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDefaultUpdateSql() <em>Default Update Sql</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDefaultUpdateSql()
     * @generated
     * @ordered
     */
    protected String defaultUpdateSql = DEFAULT_UPDATE_SQL_EDEFAULT;

    /**
     * The default value of the '{@link #getDefaultUpdateSql1() <em>Default Update Sql1</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDefaultUpdateSql1()
     * @generated
     * @ordered
     */
    protected static final String DEFAULT_UPDATE_SQL1_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDefaultUpdateSql1() <em>Default Update Sql1</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDefaultUpdateSql1()
     * @generated
     * @ordered
     */
    protected String defaultUpdateSql1 = DEFAULT_UPDATE_SQL1_EDEFAULT;

    /**
     * The default value of the '{@link #getIdColumn() <em>Id Column</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIdColumn()
     * @generated
     * @ordered
     */
    protected static final int ID_COLUMN_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getIdColumn() <em>Id Column</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIdColumn()
     * @generated
     * @ordered
     */
    protected int idColumn = ID_COLUMN_EDEFAULT;

    /**
     * This is true if the Id Column attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean idColumnESet;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TabularQueryTypeImpl ()
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
        return ConfigurationPackage.Literals.TABULAR_QUERY_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<UpdateColumnsType> getUpdateColumns ()
    {
        if ( updateColumns == null )
        {
            updateColumns = new EObjectContainmentEList<UpdateColumnsType> ( UpdateColumnsType.class, this, ConfigurationPackage.TABULAR_QUERY_TYPE__UPDATE_COLUMNS );
        }
        return updateColumns;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDefaultUpdateSql ()
    {
        return defaultUpdateSql;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDefaultUpdateSql ( String newDefaultUpdateSql )
    {
        String oldDefaultUpdateSql = defaultUpdateSql;
        defaultUpdateSql = newDefaultUpdateSql;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.TABULAR_QUERY_TYPE__DEFAULT_UPDATE_SQL, oldDefaultUpdateSql, defaultUpdateSql ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDefaultUpdateSql1 ()
    {
        return defaultUpdateSql1;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDefaultUpdateSql1 ( String newDefaultUpdateSql1 )
    {
        String oldDefaultUpdateSql1 = defaultUpdateSql1;
        defaultUpdateSql1 = newDefaultUpdateSql1;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.TABULAR_QUERY_TYPE__DEFAULT_UPDATE_SQL1, oldDefaultUpdateSql1, defaultUpdateSql1 ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getIdColumn ()
    {
        return idColumn;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setIdColumn ( int newIdColumn )
    {
        int oldIdColumn = idColumn;
        idColumn = newIdColumn;
        boolean oldIdColumnESet = idColumnESet;
        idColumnESet = true;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.TABULAR_QUERY_TYPE__ID_COLUMN, oldIdColumn, idColumn, !oldIdColumnESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetIdColumn ()
    {
        int oldIdColumn = idColumn;
        boolean oldIdColumnESet = idColumnESet;
        idColumn = ID_COLUMN_EDEFAULT;
        idColumnESet = false;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.TABULAR_QUERY_TYPE__ID_COLUMN, oldIdColumn, ID_COLUMN_EDEFAULT, oldIdColumnESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetIdColumn ()
    {
        return idColumnESet;
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
            case ConfigurationPackage.TABULAR_QUERY_TYPE__UPDATE_COLUMNS:
                return ( (InternalEList<?>)getUpdateColumns () ).basicRemove ( otherEnd, msgs );
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
            case ConfigurationPackage.TABULAR_QUERY_TYPE__UPDATE_COLUMNS:
                return getUpdateColumns ();
            case ConfigurationPackage.TABULAR_QUERY_TYPE__DEFAULT_UPDATE_SQL:
                return getDefaultUpdateSql ();
            case ConfigurationPackage.TABULAR_QUERY_TYPE__DEFAULT_UPDATE_SQL1:
                return getDefaultUpdateSql1 ();
            case ConfigurationPackage.TABULAR_QUERY_TYPE__ID_COLUMN:
                return getIdColumn ();
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
            case ConfigurationPackage.TABULAR_QUERY_TYPE__UPDATE_COLUMNS:
                getUpdateColumns ().clear ();
                getUpdateColumns ().addAll ( (Collection<? extends UpdateColumnsType>)newValue );
                return;
            case ConfigurationPackage.TABULAR_QUERY_TYPE__DEFAULT_UPDATE_SQL:
                setDefaultUpdateSql ( (String)newValue );
                return;
            case ConfigurationPackage.TABULAR_QUERY_TYPE__DEFAULT_UPDATE_SQL1:
                setDefaultUpdateSql1 ( (String)newValue );
                return;
            case ConfigurationPackage.TABULAR_QUERY_TYPE__ID_COLUMN:
                setIdColumn ( (Integer)newValue );
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
            case ConfigurationPackage.TABULAR_QUERY_TYPE__UPDATE_COLUMNS:
                getUpdateColumns ().clear ();
                return;
            case ConfigurationPackage.TABULAR_QUERY_TYPE__DEFAULT_UPDATE_SQL:
                setDefaultUpdateSql ( DEFAULT_UPDATE_SQL_EDEFAULT );
                return;
            case ConfigurationPackage.TABULAR_QUERY_TYPE__DEFAULT_UPDATE_SQL1:
                setDefaultUpdateSql1 ( DEFAULT_UPDATE_SQL1_EDEFAULT );
                return;
            case ConfigurationPackage.TABULAR_QUERY_TYPE__ID_COLUMN:
                unsetIdColumn ();
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
            case ConfigurationPackage.TABULAR_QUERY_TYPE__UPDATE_COLUMNS:
                return updateColumns != null && !updateColumns.isEmpty ();
            case ConfigurationPackage.TABULAR_QUERY_TYPE__DEFAULT_UPDATE_SQL:
                return DEFAULT_UPDATE_SQL_EDEFAULT == null ? defaultUpdateSql != null : !DEFAULT_UPDATE_SQL_EDEFAULT.equals ( defaultUpdateSql );
            case ConfigurationPackage.TABULAR_QUERY_TYPE__DEFAULT_UPDATE_SQL1:
                return DEFAULT_UPDATE_SQL1_EDEFAULT == null ? defaultUpdateSql1 != null : !DEFAULT_UPDATE_SQL1_EDEFAULT.equals ( defaultUpdateSql1 );
            case ConfigurationPackage.TABULAR_QUERY_TYPE__ID_COLUMN:
                return isSetIdColumn ();
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
        result.append ( " (defaultUpdateSql: " ); //$NON-NLS-1$
        result.append ( defaultUpdateSql );
        result.append ( ", defaultUpdateSql1: " ); //$NON-NLS-1$
        result.append ( defaultUpdateSql1 );
        result.append ( ", idColumn: " ); //$NON-NLS-1$
        if ( idColumnESet )
            result.append ( idColumn );
        else
            result.append ( "<unset>" ); //$NON-NLS-1$
        result.append ( ')' );
        return result.toString ();
    }

} //TabularQueryTypeImpl
