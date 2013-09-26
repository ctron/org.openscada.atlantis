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
package org.eclipse.scada.da.exec.configuration.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.scada.da.exec.configuration.ConfigurationPackage;
import org.eclipse.scada.da.exec.configuration.SplitContinuousCommandType;
import org.eclipse.scada.da.exec.configuration.SplitterType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Split Continuous Command Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.scada.da.exec.configuration.impl.SplitContinuousCommandTypeImpl#getSplitter <em>Splitter</em>}</li>
 *   <li>{@link org.eclipse.scada.da.exec.configuration.impl.SplitContinuousCommandTypeImpl#getIgnoreStartLines <em>Ignore Start Lines</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SplitContinuousCommandTypeImpl extends ContinuousCommandTypeImpl implements SplitContinuousCommandType
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getSplitter() <em>Splitter</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSplitter()
     * @generated
     * @ordered
     */
    protected SplitterType splitter;

    /**
     * The default value of the '{@link #getIgnoreStartLines() <em>Ignore Start Lines</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIgnoreStartLines()
     * @generated
     * @ordered
     */
    protected static final int IGNORE_START_LINES_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getIgnoreStartLines() <em>Ignore Start Lines</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIgnoreStartLines()
     * @generated
     * @ordered
     */
    protected int ignoreStartLines = IGNORE_START_LINES_EDEFAULT;

    /**
     * This is true if the Ignore Start Lines attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean ignoreStartLinesESet;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SplitContinuousCommandTypeImpl ()
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
        return ConfigurationPackage.Literals.SPLIT_CONTINUOUS_COMMAND_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SplitterType getSplitter ()
    {
        return splitter;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetSplitter ( SplitterType newSplitter, NotificationChain msgs )
    {
        SplitterType oldSplitter = splitter;
        splitter = newSplitter;
        if ( eNotificationRequired () )
        {
            ENotificationImpl notification = new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.SPLIT_CONTINUOUS_COMMAND_TYPE__SPLITTER, oldSplitter, newSplitter );
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
    public void setSplitter ( SplitterType newSplitter )
    {
        if ( newSplitter != splitter )
        {
            NotificationChain msgs = null;
            if ( splitter != null )
                msgs = ( (InternalEObject)splitter ).eInverseRemove ( this, EOPPOSITE_FEATURE_BASE - ConfigurationPackage.SPLIT_CONTINUOUS_COMMAND_TYPE__SPLITTER, null, msgs );
            if ( newSplitter != null )
                msgs = ( (InternalEObject)newSplitter ).eInverseAdd ( this, EOPPOSITE_FEATURE_BASE - ConfigurationPackage.SPLIT_CONTINUOUS_COMMAND_TYPE__SPLITTER, null, msgs );
            msgs = basicSetSplitter ( newSplitter, msgs );
            if ( msgs != null )
                msgs.dispatch ();
        }
        else if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.SPLIT_CONTINUOUS_COMMAND_TYPE__SPLITTER, newSplitter, newSplitter ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getIgnoreStartLines ()
    {
        return ignoreStartLines;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setIgnoreStartLines ( int newIgnoreStartLines )
    {
        int oldIgnoreStartLines = ignoreStartLines;
        ignoreStartLines = newIgnoreStartLines;
        boolean oldIgnoreStartLinesESet = ignoreStartLinesESet;
        ignoreStartLinesESet = true;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.SPLIT_CONTINUOUS_COMMAND_TYPE__IGNORE_START_LINES, oldIgnoreStartLines, ignoreStartLines, !oldIgnoreStartLinesESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetIgnoreStartLines ()
    {
        int oldIgnoreStartLines = ignoreStartLines;
        boolean oldIgnoreStartLinesESet = ignoreStartLinesESet;
        ignoreStartLines = IGNORE_START_LINES_EDEFAULT;
        ignoreStartLinesESet = false;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.SPLIT_CONTINUOUS_COMMAND_TYPE__IGNORE_START_LINES, oldIgnoreStartLines, IGNORE_START_LINES_EDEFAULT, oldIgnoreStartLinesESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetIgnoreStartLines ()
    {
        return ignoreStartLinesESet;
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
            case ConfigurationPackage.SPLIT_CONTINUOUS_COMMAND_TYPE__SPLITTER:
                return basicSetSplitter ( null, msgs );
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
            case ConfigurationPackage.SPLIT_CONTINUOUS_COMMAND_TYPE__SPLITTER:
                return getSplitter ();
            case ConfigurationPackage.SPLIT_CONTINUOUS_COMMAND_TYPE__IGNORE_START_LINES:
                return getIgnoreStartLines ();
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
            case ConfigurationPackage.SPLIT_CONTINUOUS_COMMAND_TYPE__SPLITTER:
                setSplitter ( (SplitterType)newValue );
                return;
            case ConfigurationPackage.SPLIT_CONTINUOUS_COMMAND_TYPE__IGNORE_START_LINES:
                setIgnoreStartLines ( (Integer)newValue );
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
            case ConfigurationPackage.SPLIT_CONTINUOUS_COMMAND_TYPE__SPLITTER:
                setSplitter ( (SplitterType)null );
                return;
            case ConfigurationPackage.SPLIT_CONTINUOUS_COMMAND_TYPE__IGNORE_START_LINES:
                unsetIgnoreStartLines ();
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
            case ConfigurationPackage.SPLIT_CONTINUOUS_COMMAND_TYPE__SPLITTER:
                return splitter != null;
            case ConfigurationPackage.SPLIT_CONTINUOUS_COMMAND_TYPE__IGNORE_START_LINES:
                return isSetIgnoreStartLines ();
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
        result.append ( " (ignoreStartLines: " ); //$NON-NLS-1$
        if ( ignoreStartLinesESet )
            result.append ( ignoreStartLines );
        else
            result.append ( "<unset>" ); //$NON-NLS-1$
        result.append ( ')' );
        return result.toString ();
    }

} //SplitContinuousCommandTypeImpl
