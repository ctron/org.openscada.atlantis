/**
 */
package org.openscada.da.snmp.configuration.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.openscada.da.snmp.configuration.ConfigurationPackage;
import org.openscada.da.snmp.configuration.ConnectionType;
import org.openscada.da.snmp.configuration.SnmpVersion;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Connection Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.openscada.da.snmp.configuration.impl.ConnectionTypeImpl#getAddress <em>Address</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.impl.ConnectionTypeImpl#getCommunity <em>Community</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.impl.ConnectionTypeImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.impl.ConnectionTypeImpl#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ConnectionTypeImpl extends MinimalEObjectImpl.Container implements ConnectionType
{
    /**
     * The default value of the '{@link #getAddress() <em>Address</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAddress()
     * @generated
     * @ordered
     */
    protected static final String ADDRESS_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getAddress() <em>Address</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAddress()
     * @generated
     * @ordered
     */
    protected String address = ADDRESS_EDEFAULT;

    /**
     * The default value of the '{@link #getCommunity() <em>Community</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCommunity()
     * @generated
     * @ordered
     */
    protected static final String COMMUNITY_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getCommunity() <em>Community</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCommunity()
     * @generated
     * @ordered
     */
    protected String community = COMMUNITY_EDEFAULT;

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getVersion()
     * @generated
     * @ordered
     */
    protected static final SnmpVersion VERSION_EDEFAULT = SnmpVersion._2;

    /**
     * The cached value of the '{@link #getVersion() <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getVersion()
     * @generated
     * @ordered
     */
    protected SnmpVersion version = VERSION_EDEFAULT;

    /**
     * This is true if the Version attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean versionESet;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ConnectionTypeImpl ()
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
        return ConfigurationPackage.Literals.CONNECTION_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getAddress ()
    {
        return address;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAddress ( String newAddress )
    {
        String oldAddress = address;
        address = newAddress;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONNECTION_TYPE__ADDRESS, oldAddress, address ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getCommunity ()
    {
        return community;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCommunity ( String newCommunity )
    {
        String oldCommunity = community;
        community = newCommunity;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONNECTION_TYPE__COMMUNITY, oldCommunity, community ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName ()
    {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setName ( String newName )
    {
        String oldName = name;
        name = newName;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONNECTION_TYPE__NAME, oldName, name ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SnmpVersion getVersion ()
    {
        return version;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setVersion ( SnmpVersion newVersion )
    {
        SnmpVersion oldVersion = version;
        version = newVersion == null ? VERSION_EDEFAULT : newVersion;
        boolean oldVersionESet = versionESet;
        versionESet = true;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONNECTION_TYPE__VERSION, oldVersion, version, !oldVersionESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetVersion ()
    {
        SnmpVersion oldVersion = version;
        boolean oldVersionESet = versionESet;
        version = VERSION_EDEFAULT;
        versionESet = false;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.CONNECTION_TYPE__VERSION, oldVersion, VERSION_EDEFAULT, oldVersionESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetVersion ()
    {
        return versionESet;
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
            case ConfigurationPackage.CONNECTION_TYPE__ADDRESS:
                return getAddress ();
            case ConfigurationPackage.CONNECTION_TYPE__COMMUNITY:
                return getCommunity ();
            case ConfigurationPackage.CONNECTION_TYPE__NAME:
                return getName ();
            case ConfigurationPackage.CONNECTION_TYPE__VERSION:
                return getVersion ();
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
            case ConfigurationPackage.CONNECTION_TYPE__ADDRESS:
                setAddress ( (String)newValue );
                return;
            case ConfigurationPackage.CONNECTION_TYPE__COMMUNITY:
                setCommunity ( (String)newValue );
                return;
            case ConfigurationPackage.CONNECTION_TYPE__NAME:
                setName ( (String)newValue );
                return;
            case ConfigurationPackage.CONNECTION_TYPE__VERSION:
                setVersion ( (SnmpVersion)newValue );
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
            case ConfigurationPackage.CONNECTION_TYPE__ADDRESS:
                setAddress ( ADDRESS_EDEFAULT );
                return;
            case ConfigurationPackage.CONNECTION_TYPE__COMMUNITY:
                setCommunity ( COMMUNITY_EDEFAULT );
                return;
            case ConfigurationPackage.CONNECTION_TYPE__NAME:
                setName ( NAME_EDEFAULT );
                return;
            case ConfigurationPackage.CONNECTION_TYPE__VERSION:
                unsetVersion ();
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
            case ConfigurationPackage.CONNECTION_TYPE__ADDRESS:
                return ADDRESS_EDEFAULT == null ? address != null : !ADDRESS_EDEFAULT.equals ( address );
            case ConfigurationPackage.CONNECTION_TYPE__COMMUNITY:
                return COMMUNITY_EDEFAULT == null ? community != null : !COMMUNITY_EDEFAULT.equals ( community );
            case ConfigurationPackage.CONNECTION_TYPE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals ( name );
            case ConfigurationPackage.CONNECTION_TYPE__VERSION:
                return isSetVersion ();
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
        result.append ( " (address: " ); //$NON-NLS-1$
        result.append ( address );
        result.append ( ", community: " ); //$NON-NLS-1$
        result.append ( community );
        result.append ( ", name: " ); //$NON-NLS-1$
        result.append ( name );
        result.append ( ", version: " ); //$NON-NLS-1$
        if ( versionESet )
            result.append ( version );
        else
            result.append ( "<unset>" ); //$NON-NLS-1$
        result.append ( ')' );
        return result.toString ();
    }

} //ConnectionTypeImpl
