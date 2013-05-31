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
package org.openscada.da.opc.configuration;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#getProgid <em>Progid</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#getClsid <em>Clsid</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#getInitialItem <em>Initial Item</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#getInitialItemResource <em>Initial Item Resource</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#getAccess <em>Access</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#getAlias <em>Alias</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#isConnected <em>Connected</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#getDomain <em>Domain</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#isEnabled <em>Enabled</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#isFlatBrowser <em>Flat Browser</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#getHost <em>Host</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#isIgnoreTimestampOnlyChange <em>Ignore Timestamp Only Change</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#isInitialRefresh <em>Initial Refresh</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#getItemIdPrefix <em>Item Id Prefix</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#getPassword <em>Password</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#getQualityErrorIfLessThen <em>Quality Error If Less Then</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#getReconnectDelay <em>Reconnect Delay</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#getRefresh <em>Refresh</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#isTreeBrowser <em>Tree Browser</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.ConfigurationType#getUser <em>User</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType()
 * @model extendedMetaData="name='ConfigurationType' kind='elementOnly'"
 * @generated
 */
public interface ConfigurationType extends EObject
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Progid</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Progid</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Progid</em>' attribute.
     * @see #setProgid(String)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_Progid()
     * @model dataType="org.openscada.da.opc.configuration.ProgIdType"
     *        extendedMetaData="kind='element' name='progid' namespace='##targetNamespace'"
     * @generated
     */
    String getProgid ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getProgid <em>Progid</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Progid</em>' attribute.
     * @see #getProgid()
     * @generated
     */
    void setProgid ( String value );

    /**
     * Returns the value of the '<em><b>Clsid</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Clsid</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Clsid</em>' attribute.
     * @see #setClsid(String)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_Clsid()
     * @model dataType="org.openscada.da.opc.configuration.UUIDType"
     *        extendedMetaData="kind='element' name='clsid' namespace='##targetNamespace'"
     * @generated
     */
    String getClsid ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getClsid <em>Clsid</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Clsid</em>' attribute.
     * @see #getClsid()
     * @generated
     */
    void setClsid ( String value );

    /**
     * Returns the value of the '<em><b>Initial Item</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Initial Item</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Initial Item</em>' attribute list.
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_InitialItem()
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='element' name='initialItem' namespace='##targetNamespace'"
     * @generated
     */
    EList<String> getInitialItem ();

    /**
     * Returns the value of the '<em><b>Initial Item Resource</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Initial Item Resource</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Initial Item Resource</em>' attribute.
     * @see #setInitialItemResource(String)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_InitialItemResource()
     * @model dataType="org.eclipse.emf.ecore.xml.type.AnyURI"
     *        extendedMetaData="kind='element' name='initialItemResource' namespace='##targetNamespace'"
     * @generated
     */
    String getInitialItemResource ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getInitialItemResource <em>Initial Item Resource</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Initial Item Resource</em>' attribute.
     * @see #getInitialItemResource()
     * @generated
     */
    void setInitialItemResource ( String value );

    /**
     * Returns the value of the '<em><b>Access</b></em>' attribute.
     * The default value is <code>"sync"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Access</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Access</em>' attribute.
     * @see #isSetAccess()
     * @see #unsetAccess()
     * @see #setAccess(String)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_Access()
     * @model default="sync" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='access'"
     * @generated
     */
    String getAccess ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getAccess <em>Access</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Access</em>' attribute.
     * @see #isSetAccess()
     * @see #unsetAccess()
     * @see #getAccess()
     * @generated
     */
    void setAccess ( String value );

    /**
     * Unsets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getAccess <em>Access</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetAccess()
     * @see #getAccess()
     * @see #setAccess(String)
     * @generated
     */
    void unsetAccess ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getAccess <em>Access</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Access</em>' attribute is set.
     * @see #unsetAccess()
     * @see #getAccess()
     * @see #setAccess(String)
     * @generated
     */
    boolean isSetAccess ();

    /**
     * Returns the value of the '<em><b>Alias</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Alias</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Alias</em>' attribute.
     * @see #setAlias(String)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_Alias()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='alias'"
     * @generated
     */
    String getAlias ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getAlias <em>Alias</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Alias</em>' attribute.
     * @see #getAlias()
     * @generated
     */
    void setAlias ( String value );

    /**
     * Returns the value of the '<em><b>Connected</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Connected</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Connected</em>' attribute.
     * @see #isSetConnected()
     * @see #unsetConnected()
     * @see #setConnected(boolean)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_Connected()
     * @model default="true" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
     *        extendedMetaData="kind='attribute' name='connected'"
     * @generated
     */
    boolean isConnected ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#isConnected <em>Connected</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Connected</em>' attribute.
     * @see #isSetConnected()
     * @see #unsetConnected()
     * @see #isConnected()
     * @generated
     */
    void setConnected ( boolean value );

    /**
     * Unsets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#isConnected <em>Connected</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetConnected()
     * @see #isConnected()
     * @see #setConnected(boolean)
     * @generated
     */
    void unsetConnected ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#isConnected <em>Connected</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Connected</em>' attribute is set.
     * @see #unsetConnected()
     * @see #isConnected()
     * @see #setConnected(boolean)
     * @generated
     */
    boolean isSetConnected ();

    /**
     * Returns the value of the '<em><b>Domain</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Domain</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Domain</em>' attribute.
     * @see #setDomain(String)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_Domain()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     *        extendedMetaData="kind='attribute' name='domain'"
     * @generated
     */
    String getDomain ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getDomain <em>Domain</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Domain</em>' attribute.
     * @see #getDomain()
     * @generated
     */
    void setDomain ( String value );

    /**
     * Returns the value of the '<em><b>Enabled</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Enabled</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Enabled</em>' attribute.
     * @see #isSetEnabled()
     * @see #unsetEnabled()
     * @see #setEnabled(boolean)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_Enabled()
     * @model default="true" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
     *        extendedMetaData="kind='attribute' name='enabled'"
     * @generated
     */
    boolean isEnabled ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#isEnabled <em>Enabled</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Enabled</em>' attribute.
     * @see #isSetEnabled()
     * @see #unsetEnabled()
     * @see #isEnabled()
     * @generated
     */
    void setEnabled ( boolean value );

    /**
     * Unsets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#isEnabled <em>Enabled</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetEnabled()
     * @see #isEnabled()
     * @see #setEnabled(boolean)
     * @generated
     */
    void unsetEnabled ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#isEnabled <em>Enabled</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Enabled</em>' attribute is set.
     * @see #unsetEnabled()
     * @see #isEnabled()
     * @see #setEnabled(boolean)
     * @generated
     */
    boolean isSetEnabled ();

    /**
     * Returns the value of the '<em><b>Flat Browser</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Flat Browser</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Flat Browser</em>' attribute.
     * @see #isSetFlatBrowser()
     * @see #unsetFlatBrowser()
     * @see #setFlatBrowser(boolean)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_FlatBrowser()
     * @model default="true" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
     *        extendedMetaData="kind='attribute' name='flat-browser'"
     * @generated
     */
    boolean isFlatBrowser ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#isFlatBrowser <em>Flat Browser</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Flat Browser</em>' attribute.
     * @see #isSetFlatBrowser()
     * @see #unsetFlatBrowser()
     * @see #isFlatBrowser()
     * @generated
     */
    void setFlatBrowser ( boolean value );

    /**
     * Unsets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#isFlatBrowser <em>Flat Browser</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetFlatBrowser()
     * @see #isFlatBrowser()
     * @see #setFlatBrowser(boolean)
     * @generated
     */
    void unsetFlatBrowser ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#isFlatBrowser <em>Flat Browser</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Flat Browser</em>' attribute is set.
     * @see #unsetFlatBrowser()
     * @see #isFlatBrowser()
     * @see #setFlatBrowser(boolean)
     * @generated
     */
    boolean isSetFlatBrowser ();

    /**
     * Returns the value of the '<em><b>Host</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Host</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Host</em>' attribute.
     * @see #setHost(String)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_Host()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     *        extendedMetaData="kind='attribute' name='host'"
     * @generated
     */
    String getHost ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getHost <em>Host</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Host</em>' attribute.
     * @see #getHost()
     * @generated
     */
    void setHost ( String value );

    /**
     * Returns the value of the '<em><b>Ignore Timestamp Only Change</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Ignore Timestamp Only Change</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Ignore Timestamp Only Change</em>' attribute.
     * @see #isSetIgnoreTimestampOnlyChange()
     * @see #unsetIgnoreTimestampOnlyChange()
     * @see #setIgnoreTimestampOnlyChange(boolean)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_IgnoreTimestampOnlyChange()
     * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
     *        extendedMetaData="kind='attribute' name='ignoreTimestampOnlyChange'"
     * @generated
     */
    boolean isIgnoreTimestampOnlyChange ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#isIgnoreTimestampOnlyChange <em>Ignore Timestamp Only Change</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Ignore Timestamp Only Change</em>' attribute.
     * @see #isSetIgnoreTimestampOnlyChange()
     * @see #unsetIgnoreTimestampOnlyChange()
     * @see #isIgnoreTimestampOnlyChange()
     * @generated
     */
    void setIgnoreTimestampOnlyChange ( boolean value );

    /**
     * Unsets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#isIgnoreTimestampOnlyChange <em>Ignore Timestamp Only Change</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetIgnoreTimestampOnlyChange()
     * @see #isIgnoreTimestampOnlyChange()
     * @see #setIgnoreTimestampOnlyChange(boolean)
     * @generated
     */
    void unsetIgnoreTimestampOnlyChange ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#isIgnoreTimestampOnlyChange <em>Ignore Timestamp Only Change</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Ignore Timestamp Only Change</em>' attribute is set.
     * @see #unsetIgnoreTimestampOnlyChange()
     * @see #isIgnoreTimestampOnlyChange()
     * @see #setIgnoreTimestampOnlyChange(boolean)
     * @generated
     */
    boolean isSetIgnoreTimestampOnlyChange ();

    /**
     * Returns the value of the '<em><b>Initial Refresh</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Initial Refresh</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Initial Refresh</em>' attribute.
     * @see #isSetInitialRefresh()
     * @see #unsetInitialRefresh()
     * @see #setInitialRefresh(boolean)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_InitialRefresh()
     * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
     *        extendedMetaData="kind='attribute' name='initial-refresh'"
     * @generated
     */
    boolean isInitialRefresh ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#isInitialRefresh <em>Initial Refresh</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Initial Refresh</em>' attribute.
     * @see #isSetInitialRefresh()
     * @see #unsetInitialRefresh()
     * @see #isInitialRefresh()
     * @generated
     */
    void setInitialRefresh ( boolean value );

    /**
     * Unsets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#isInitialRefresh <em>Initial Refresh</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetInitialRefresh()
     * @see #isInitialRefresh()
     * @see #setInitialRefresh(boolean)
     * @generated
     */
    void unsetInitialRefresh ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#isInitialRefresh <em>Initial Refresh</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Initial Refresh</em>' attribute is set.
     * @see #unsetInitialRefresh()
     * @see #isInitialRefresh()
     * @see #setInitialRefresh(boolean)
     * @generated
     */
    boolean isSetInitialRefresh ();

    /**
     * Returns the value of the '<em><b>Item Id Prefix</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * 
     * 					The prefix for item IDs of the OpenSCADA items
     * 				
     * <!-- end-model-doc -->
     * @return the value of the '<em>Item Id Prefix</em>' attribute.
     * @see #setItemIdPrefix(String)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_ItemIdPrefix()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='itemIdPrefix'"
     * @generated
     */
    String getItemIdPrefix ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getItemIdPrefix <em>Item Id Prefix</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Item Id Prefix</em>' attribute.
     * @see #getItemIdPrefix()
     * @generated
     */
    void setItemIdPrefix ( String value );

    /**
     * Returns the value of the '<em><b>Password</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Password</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Password</em>' attribute.
     * @see #setPassword(String)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_Password()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     *        extendedMetaData="kind='attribute' name='password'"
     * @generated
     */
    String getPassword ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getPassword <em>Password</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Password</em>' attribute.
     * @see #getPassword()
     * @generated
     */
    void setPassword ( String value );

    /**
     * Returns the value of the '<em><b>Quality Error If Less Then</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Quality Error If Less Then</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Quality Error If Less Then</em>' attribute.
     * @see #isSetQualityErrorIfLessThen()
     * @see #unsetQualityErrorIfLessThen()
     * @see #setQualityErrorIfLessThen(int)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_QualityErrorIfLessThen()
     * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
     *        extendedMetaData="kind='attribute' name='qualityErrorIfLessThen'"
     * @generated
     */
    int getQualityErrorIfLessThen ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getQualityErrorIfLessThen <em>Quality Error If Less Then</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Quality Error If Less Then</em>' attribute.
     * @see #isSetQualityErrorIfLessThen()
     * @see #unsetQualityErrorIfLessThen()
     * @see #getQualityErrorIfLessThen()
     * @generated
     */
    void setQualityErrorIfLessThen ( int value );

    /**
     * Unsets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getQualityErrorIfLessThen <em>Quality Error If Less Then</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetQualityErrorIfLessThen()
     * @see #getQualityErrorIfLessThen()
     * @see #setQualityErrorIfLessThen(int)
     * @generated
     */
    void unsetQualityErrorIfLessThen ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getQualityErrorIfLessThen <em>Quality Error If Less Then</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Quality Error If Less Then</em>' attribute is set.
     * @see #unsetQualityErrorIfLessThen()
     * @see #getQualityErrorIfLessThen()
     * @see #setQualityErrorIfLessThen(int)
     * @generated
     */
    boolean isSetQualityErrorIfLessThen ();

    /**
     * Returns the value of the '<em><b>Reconnect Delay</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Reconnect Delay</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Reconnect Delay</em>' attribute.
     * @see #isSetReconnectDelay()
     * @see #unsetReconnectDelay()
     * @see #setReconnectDelay(int)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_ReconnectDelay()
     * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
     *        extendedMetaData="kind='attribute' name='reconnectDelay'"
     * @generated
     */
    int getReconnectDelay ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getReconnectDelay <em>Reconnect Delay</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Reconnect Delay</em>' attribute.
     * @see #isSetReconnectDelay()
     * @see #unsetReconnectDelay()
     * @see #getReconnectDelay()
     * @generated
     */
    void setReconnectDelay ( int value );

    /**
     * Unsets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getReconnectDelay <em>Reconnect Delay</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetReconnectDelay()
     * @see #getReconnectDelay()
     * @see #setReconnectDelay(int)
     * @generated
     */
    void unsetReconnectDelay ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getReconnectDelay <em>Reconnect Delay</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Reconnect Delay</em>' attribute is set.
     * @see #unsetReconnectDelay()
     * @see #getReconnectDelay()
     * @see #setReconnectDelay(int)
     * @generated
     */
    boolean isSetReconnectDelay ();

    /**
     * Returns the value of the '<em><b>Refresh</b></em>' attribute.
     * The default value is <code>"500"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Refresh</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Refresh</em>' attribute.
     * @see #isSetRefresh()
     * @see #unsetRefresh()
     * @see #setRefresh(int)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_Refresh()
     * @model default="500" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
     *        extendedMetaData="kind='attribute' name='refresh'"
     * @generated
     */
    int getRefresh ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getRefresh <em>Refresh</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Refresh</em>' attribute.
     * @see #isSetRefresh()
     * @see #unsetRefresh()
     * @see #getRefresh()
     * @generated
     */
    void setRefresh ( int value );

    /**
     * Unsets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getRefresh <em>Refresh</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetRefresh()
     * @see #getRefresh()
     * @see #setRefresh(int)
     * @generated
     */
    void unsetRefresh ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getRefresh <em>Refresh</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Refresh</em>' attribute is set.
     * @see #unsetRefresh()
     * @see #getRefresh()
     * @see #setRefresh(int)
     * @generated
     */
    boolean isSetRefresh ();

    /**
     * Returns the value of the '<em><b>Tree Browser</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Tree Browser</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Tree Browser</em>' attribute.
     * @see #isSetTreeBrowser()
     * @see #unsetTreeBrowser()
     * @see #setTreeBrowser(boolean)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_TreeBrowser()
     * @model default="true" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
     *        extendedMetaData="kind='attribute' name='tree-browser'"
     * @generated
     */
    boolean isTreeBrowser ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#isTreeBrowser <em>Tree Browser</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Tree Browser</em>' attribute.
     * @see #isSetTreeBrowser()
     * @see #unsetTreeBrowser()
     * @see #isTreeBrowser()
     * @generated
     */
    void setTreeBrowser ( boolean value );

    /**
     * Unsets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#isTreeBrowser <em>Tree Browser</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetTreeBrowser()
     * @see #isTreeBrowser()
     * @see #setTreeBrowser(boolean)
     * @generated
     */
    void unsetTreeBrowser ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#isTreeBrowser <em>Tree Browser</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Tree Browser</em>' attribute is set.
     * @see #unsetTreeBrowser()
     * @see #isTreeBrowser()
     * @see #setTreeBrowser(boolean)
     * @generated
     */
    boolean isSetTreeBrowser ();

    /**
     * Returns the value of the '<em><b>User</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>User</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>User</em>' attribute.
     * @see #setUser(String)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConfigurationType_User()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     *        extendedMetaData="kind='attribute' name='user'"
     * @generated
     */
    String getUser ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.ConfigurationType#getUser <em>User</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>User</em>' attribute.
     * @see #getUser()
     * @generated
     */
    void setUser ( String value );

} // ConfigurationType
