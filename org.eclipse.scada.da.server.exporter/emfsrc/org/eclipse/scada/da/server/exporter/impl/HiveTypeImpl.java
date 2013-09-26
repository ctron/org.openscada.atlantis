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
package org.eclipse.scada.da.server.exporter.impl;

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
import org.eclipse.scada.da.server.exporter.ExportType;
import org.eclipse.scada.da.server.exporter.ExporterPackage;
import org.eclipse.scada.da.server.exporter.HiveConfigurationType;
import org.eclipse.scada.da.server.exporter.HiveType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Hive Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.scada.da.server.exporter.impl.HiveTypeImpl#getConfiguration <em>Configuration</em>}</li>
 *   <li>{@link org.eclipse.scada.da.server.exporter.impl.HiveTypeImpl#getExport <em>Export</em>}</li>
 *   <li>{@link org.eclipse.scada.da.server.exporter.impl.HiveTypeImpl#getRef <em>Ref</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class HiveTypeImpl extends MinimalEObjectImpl.Container implements HiveType
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getConfiguration() <em>Configuration</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getConfiguration()
     * @generated
     * @ordered
     */
    protected HiveConfigurationType configuration;

    /**
     * The cached value of the '{@link #getExport() <em>Export</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getExport()
     * @generated
     * @ordered
     */
    protected EList<ExportType> export;

    /**
     * The default value of the '{@link #getRef() <em>Ref</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRef()
     * @generated
     * @ordered
     */
    protected static final String REF_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getRef() <em>Ref</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRef()
     * @generated
     * @ordered
     */
    protected String ref = REF_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected HiveTypeImpl ()
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
        return ExporterPackage.Literals.HIVE_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public HiveConfigurationType getConfiguration ()
    {
        return configuration;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetConfiguration ( HiveConfigurationType newConfiguration, NotificationChain msgs )
    {
        HiveConfigurationType oldConfiguration = configuration;
        configuration = newConfiguration;
        if ( eNotificationRequired () )
        {
            ENotificationImpl notification = new ENotificationImpl ( this, Notification.SET, ExporterPackage.HIVE_TYPE__CONFIGURATION, oldConfiguration, newConfiguration );
            if ( msgs == null )
                msgs = notification;
            else
                msgs.add ( notification );
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setConfiguration ( HiveConfigurationType newConfiguration )
    {
        if ( newConfiguration != configuration )
        {
            NotificationChain msgs = null;
            if ( configuration != null )
                msgs = ( (InternalEObject)configuration ).eInverseRemove ( this, EOPPOSITE_FEATURE_BASE - ExporterPackage.HIVE_TYPE__CONFIGURATION, null, msgs );
            if ( newConfiguration != null )
                msgs = ( (InternalEObject)newConfiguration ).eInverseAdd ( this, EOPPOSITE_FEATURE_BASE - ExporterPackage.HIVE_TYPE__CONFIGURATION, null, msgs );
            msgs = basicSetConfiguration ( newConfiguration, msgs );
            if ( msgs != null )
                msgs.dispatch ();
        }
        else if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ExporterPackage.HIVE_TYPE__CONFIGURATION, newConfiguration, newConfiguration ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<ExportType> getExport ()
    {
        if ( export == null )
        {
            export = new EObjectContainmentEList<ExportType> ( ExportType.class, this, ExporterPackage.HIVE_TYPE__EXPORT );
        }
        return export;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getRef ()
    {
        return ref;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRef ( String newRef )
    {
        String oldRef = ref;
        ref = newRef;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ExporterPackage.HIVE_TYPE__REF, oldRef, ref ) );
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
            case ExporterPackage.HIVE_TYPE__CONFIGURATION:
                return basicSetConfiguration ( null, msgs );
            case ExporterPackage.HIVE_TYPE__EXPORT:
                return ( (InternalEList<?>)getExport () ).basicRemove ( otherEnd, msgs );
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
            case ExporterPackage.HIVE_TYPE__CONFIGURATION:
                return getConfiguration ();
            case ExporterPackage.HIVE_TYPE__EXPORT:
                return getExport ();
            case ExporterPackage.HIVE_TYPE__REF:
                return getRef ();
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
            case ExporterPackage.HIVE_TYPE__CONFIGURATION:
                setConfiguration ( (HiveConfigurationType)newValue );
                return;
            case ExporterPackage.HIVE_TYPE__EXPORT:
                getExport ().clear ();
                getExport ().addAll ( (Collection<? extends ExportType>)newValue );
                return;
            case ExporterPackage.HIVE_TYPE__REF:
                setRef ( (String)newValue );
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
            case ExporterPackage.HIVE_TYPE__CONFIGURATION:
                setConfiguration ( (HiveConfigurationType)null );
                return;
            case ExporterPackage.HIVE_TYPE__EXPORT:
                getExport ().clear ();
                return;
            case ExporterPackage.HIVE_TYPE__REF:
                setRef ( REF_EDEFAULT );
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
            case ExporterPackage.HIVE_TYPE__CONFIGURATION:
                return configuration != null;
            case ExporterPackage.HIVE_TYPE__EXPORT:
                return export != null && !export.isEmpty ();
            case ExporterPackage.HIVE_TYPE__REF:
                return REF_EDEFAULT == null ? ref != null : !REF_EDEFAULT.equals ( ref );
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
        result.append ( " (ref: " ); //$NON-NLS-1$
        result.append ( ref );
        result.append ( ')' );
        return result.toString ();
    }

} //HiveTypeImpl
