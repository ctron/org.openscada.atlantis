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
 *   <li>{@link org.openscada.da.snmp.configuration.impl.ConnectionTypeImpl#getLimitToOid <em>Limit To Oid</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.impl.ConnectionTypeImpl#getRetries <em>Retries</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.impl.ConnectionTypeImpl#getTimeout <em>Timeout</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ConnectionTypeImpl extends MinimalEObjectImpl.Container implements
		ConnectionType {
	//$NON-NLS-1$

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

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
	 * The default value of the '{@link #getLimitToOid() <em>Limit To Oid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLimitToOid()
	 * @generated
	 * @ordered
	 */
	protected static final String LIMIT_TO_OID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLimitToOid() <em>Limit To Oid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLimitToOid()
	 * @generated
	 * @ordered
	 */
	protected String limitToOid = LIMIT_TO_OID_EDEFAULT;

	/**
	 * The default value of the '{@link #getRetries() <em>Retries</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRetries()
	 * @generated
	 * @ordered
	 */
	protected static final int RETRIES_EDEFAULT = 1;

	/**
	 * The cached value of the '{@link #getRetries() <em>Retries</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRetries()
	 * @generated
	 * @ordered
	 */
	protected int retries = RETRIES_EDEFAULT;

	/**
	 * The default value of the '{@link #getTimeout() <em>Timeout</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTimeout()
	 * @generated
	 * @ordered
	 */
	protected static final long TIMEOUT_EDEFAULT = 5000L;

	/**
	 * The cached value of the '{@link #getTimeout() <em>Timeout</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTimeout()
	 * @generated
	 * @ordered
	 */
	protected long timeout = TIMEOUT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ConnectionTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ConfigurationPackage.Literals.CONNECTION_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAddress(String newAddress) {
		String oldAddress = address;
		address = newAddress;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					ConfigurationPackage.CONNECTION_TYPE__ADDRESS, oldAddress,
					address));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCommunity() {
		return community;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCommunity(String newCommunity) {
		String oldCommunity = community;
		community = newCommunity;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					ConfigurationPackage.CONNECTION_TYPE__COMMUNITY,
					oldCommunity, community));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					ConfigurationPackage.CONNECTION_TYPE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnmpVersion getVersion() {
		return version;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVersion(SnmpVersion newVersion) {
		SnmpVersion oldVersion = version;
		version = newVersion == null ? VERSION_EDEFAULT : newVersion;
		boolean oldVersionESet = versionESet;
		versionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					ConfigurationPackage.CONNECTION_TYPE__VERSION, oldVersion,
					version, !oldVersionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetVersion() {
		SnmpVersion oldVersion = version;
		boolean oldVersionESet = versionESet;
		version = VERSION_EDEFAULT;
		versionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET,
					ConfigurationPackage.CONNECTION_TYPE__VERSION, oldVersion,
					VERSION_EDEFAULT, oldVersionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetVersion() {
		return versionESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLimitToOid() {
		return limitToOid;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLimitToOid(String newLimitToOid) {
		String oldLimitToOid = limitToOid;
		limitToOid = newLimitToOid;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					ConfigurationPackage.CONNECTION_TYPE__LIMIT_TO_OID,
					oldLimitToOid, limitToOid));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getRetries() {
		return retries;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRetries(int newRetries) {
		int oldRetries = retries;
		retries = newRetries;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					ConfigurationPackage.CONNECTION_TYPE__RETRIES, oldRetries,
					retries));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTimeout(long newTimeout) {
		long oldTimeout = timeout;
		timeout = newTimeout;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					ConfigurationPackage.CONNECTION_TYPE__TIMEOUT, oldTimeout,
					timeout));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ConfigurationPackage.CONNECTION_TYPE__ADDRESS:
			return getAddress();
		case ConfigurationPackage.CONNECTION_TYPE__COMMUNITY:
			return getCommunity();
		case ConfigurationPackage.CONNECTION_TYPE__NAME:
			return getName();
		case ConfigurationPackage.CONNECTION_TYPE__VERSION:
			return getVersion();
		case ConfigurationPackage.CONNECTION_TYPE__LIMIT_TO_OID:
			return getLimitToOid();
		case ConfigurationPackage.CONNECTION_TYPE__RETRIES:
			return getRetries();
		case ConfigurationPackage.CONNECTION_TYPE__TIMEOUT:
			return getTimeout();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case ConfigurationPackage.CONNECTION_TYPE__ADDRESS:
			setAddress((String) newValue);
			return;
		case ConfigurationPackage.CONNECTION_TYPE__COMMUNITY:
			setCommunity((String) newValue);
			return;
		case ConfigurationPackage.CONNECTION_TYPE__NAME:
			setName((String) newValue);
			return;
		case ConfigurationPackage.CONNECTION_TYPE__VERSION:
			setVersion((SnmpVersion) newValue);
			return;
		case ConfigurationPackage.CONNECTION_TYPE__LIMIT_TO_OID:
			setLimitToOid((String) newValue);
			return;
		case ConfigurationPackage.CONNECTION_TYPE__RETRIES:
			setRetries((Integer) newValue);
			return;
		case ConfigurationPackage.CONNECTION_TYPE__TIMEOUT:
			setTimeout((Long) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case ConfigurationPackage.CONNECTION_TYPE__ADDRESS:
			setAddress(ADDRESS_EDEFAULT);
			return;
		case ConfigurationPackage.CONNECTION_TYPE__COMMUNITY:
			setCommunity(COMMUNITY_EDEFAULT);
			return;
		case ConfigurationPackage.CONNECTION_TYPE__NAME:
			setName(NAME_EDEFAULT);
			return;
		case ConfigurationPackage.CONNECTION_TYPE__VERSION:
			unsetVersion();
			return;
		case ConfigurationPackage.CONNECTION_TYPE__LIMIT_TO_OID:
			setLimitToOid(LIMIT_TO_OID_EDEFAULT);
			return;
		case ConfigurationPackage.CONNECTION_TYPE__RETRIES:
			setRetries(RETRIES_EDEFAULT);
			return;
		case ConfigurationPackage.CONNECTION_TYPE__TIMEOUT:
			setTimeout(TIMEOUT_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case ConfigurationPackage.CONNECTION_TYPE__ADDRESS:
			return ADDRESS_EDEFAULT == null ? address != null
					: !ADDRESS_EDEFAULT.equals(address);
		case ConfigurationPackage.CONNECTION_TYPE__COMMUNITY:
			return COMMUNITY_EDEFAULT == null ? community != null
					: !COMMUNITY_EDEFAULT.equals(community);
		case ConfigurationPackage.CONNECTION_TYPE__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT
					.equals(name);
		case ConfigurationPackage.CONNECTION_TYPE__VERSION:
			return isSetVersion();
		case ConfigurationPackage.CONNECTION_TYPE__LIMIT_TO_OID:
			return LIMIT_TO_OID_EDEFAULT == null ? limitToOid != null
					: !LIMIT_TO_OID_EDEFAULT.equals(limitToOid);
		case ConfigurationPackage.CONNECTION_TYPE__RETRIES:
			return retries != RETRIES_EDEFAULT;
		case ConfigurationPackage.CONNECTION_TYPE__TIMEOUT:
			return timeout != TIMEOUT_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (address: "); //$NON-NLS-1$
		result.append(address);
		result.append(", community: "); //$NON-NLS-1$
		result.append(community);
		result.append(", name: "); //$NON-NLS-1$
		result.append(name);
		result.append(", version: "); //$NON-NLS-1$
		if (versionESet)
			result.append(version);
		else
			result.append("<unset>"); //$NON-NLS-1$
		result.append(", limitToOid: "); //$NON-NLS-1$
		result.append(limitToOid);
		result.append(", retries: "); //$NON-NLS-1$
		result.append(retries);
		result.append(", timeout: "); //$NON-NLS-1$
		result.append(timeout);
		result.append(')');
		return result.toString();
	}

} //ConnectionTypeImpl
