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
package org.openscada.da.exec.configuration.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.openscada.da.exec.configuration.ConfigurationPackage;
import org.openscada.da.exec.configuration.EnvEntryType;
import org.openscada.da.exec.configuration.ProcessType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Process Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.openscada.da.exec.configuration.impl.ProcessTypeImpl#getArgument <em>Argument</em>}</li>
 *   <li>{@link org.openscada.da.exec.configuration.impl.ProcessTypeImpl#getEnv <em>Env</em>}</li>
 *   <li>{@link org.openscada.da.exec.configuration.impl.ProcessTypeImpl#getExec <em>Exec</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ProcessTypeImpl extends MinimalEObjectImpl.Container implements ProcessType
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getArgument() <em>Argument</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getArgument()
     * @generated
     * @ordered
     */
    protected EList<String> argument;

    /**
     * The cached value of the '{@link #getEnv() <em>Env</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEnv()
     * @generated
     * @ordered
     */
    protected EList<EnvEntryType> env;

    /**
     * The default value of the '{@link #getExec() <em>Exec</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getExec()
     * @generated
     * @ordered
     */
    protected static final String EXEC_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getExec() <em>Exec</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getExec()
     * @generated
     * @ordered
     */
    protected String exec = EXEC_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ProcessTypeImpl ()
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
        return ConfigurationPackage.Literals.PROCESS_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<String> getArgument ()
    {
        if ( argument == null )
        {
            argument = new EDataTypeEList<String> ( String.class, this, ConfigurationPackage.PROCESS_TYPE__ARGUMENT );
        }
        return argument;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<EnvEntryType> getEnv ()
    {
        if ( env == null )
        {
            env = new EObjectContainmentEList<EnvEntryType> ( EnvEntryType.class, this, ConfigurationPackage.PROCESS_TYPE__ENV );
        }
        return env;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getExec ()
    {
        return exec;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setExec ( String newExec )
    {
        String oldExec = exec;
        exec = newExec;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.PROCESS_TYPE__EXEC, oldExec, exec ) );
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
            case ConfigurationPackage.PROCESS_TYPE__ENV:
                return ( (InternalEList<?>)getEnv () ).basicRemove ( otherEnd, msgs );
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
            case ConfigurationPackage.PROCESS_TYPE__ARGUMENT:
                return getArgument ();
            case ConfigurationPackage.PROCESS_TYPE__ENV:
                return getEnv ();
            case ConfigurationPackage.PROCESS_TYPE__EXEC:
                return getExec ();
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
            case ConfigurationPackage.PROCESS_TYPE__ARGUMENT:
                getArgument ().clear ();
                getArgument ().addAll ( (Collection<? extends String>)newValue );
                return;
            case ConfigurationPackage.PROCESS_TYPE__ENV:
                getEnv ().clear ();
                getEnv ().addAll ( (Collection<? extends EnvEntryType>)newValue );
                return;
            case ConfigurationPackage.PROCESS_TYPE__EXEC:
                setExec ( (String)newValue );
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
            case ConfigurationPackage.PROCESS_TYPE__ARGUMENT:
                getArgument ().clear ();
                return;
            case ConfigurationPackage.PROCESS_TYPE__ENV:
                getEnv ().clear ();
                return;
            case ConfigurationPackage.PROCESS_TYPE__EXEC:
                setExec ( EXEC_EDEFAULT );
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
            case ConfigurationPackage.PROCESS_TYPE__ARGUMENT:
                return argument != null && !argument.isEmpty ();
            case ConfigurationPackage.PROCESS_TYPE__ENV:
                return env != null && !env.isEmpty ();
            case ConfigurationPackage.PROCESS_TYPE__EXEC:
                return EXEC_EDEFAULT == null ? exec != null : !EXEC_EDEFAULT.equals ( exec );
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
        result.append ( " (argument: " ); //$NON-NLS-1$
        result.append ( argument );
        result.append ( ", exec: " ); //$NON-NLS-1$
        result.append ( exec );
        result.append ( ')' );
        return result.toString ();
    }

} //ProcessTypeImpl
