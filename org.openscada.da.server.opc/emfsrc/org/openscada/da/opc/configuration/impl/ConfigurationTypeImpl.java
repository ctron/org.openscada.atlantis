/**
 */
package org.openscada.da.opc.configuration.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;

import org.openscada.da.opc.configuration.ConfigurationPackage;
import org.openscada.da.opc.configuration.ConfigurationType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#getProgid <em>Progid</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#getClsid <em>Clsid</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#getInitialItem <em>Initial Item</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#getInitialItemResource <em>Initial Item Resource</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#getAccess <em>Access</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#getAlias <em>Alias</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#isConnected <em>Connected</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#getDomain <em>Domain</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#isEnabled <em>Enabled</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#isFlatBrowser <em>Flat Browser</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#getHost <em>Host</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#isIgnoreTimestampOnlyChange <em>Ignore Timestamp Only Change</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#isInitialRefresh <em>Initial Refresh</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#getItemIdPrefix <em>Item Id Prefix</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#getPassword <em>Password</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#getQualityErrorIfLessThen <em>Quality Error If Less Then</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#getReconnectDelay <em>Reconnect Delay</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#getRefresh <em>Refresh</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#isTreeBrowser <em>Tree Browser</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl#getUser <em>User</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ConfigurationTypeImpl extends MinimalEObjectImpl.Container implements ConfigurationType
{
    /**
     * The default value of the '{@link #getProgid() <em>Progid</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProgid()
     * @generated
     * @ordered
     */
    protected static final String PROGID_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getProgid() <em>Progid</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProgid()
     * @generated
     * @ordered
     */
    protected String progid = PROGID_EDEFAULT;

    /**
     * The default value of the '{@link #getClsid() <em>Clsid</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getClsid()
     * @generated
     * @ordered
     */
    protected static final String CLSID_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getClsid() <em>Clsid</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getClsid()
     * @generated
     * @ordered
     */
    protected String clsid = CLSID_EDEFAULT;

    /**
     * The cached value of the '{@link #getInitialItem() <em>Initial Item</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInitialItem()
     * @generated
     * @ordered
     */
    protected EList<String> initialItem;

    /**
     * The default value of the '{@link #getInitialItemResource() <em>Initial Item Resource</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInitialItemResource()
     * @generated
     * @ordered
     */
    protected static final String INITIAL_ITEM_RESOURCE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getInitialItemResource() <em>Initial Item Resource</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInitialItemResource()
     * @generated
     * @ordered
     */
    protected String initialItemResource = INITIAL_ITEM_RESOURCE_EDEFAULT;

    /**
     * The default value of the '{@link #getAccess() <em>Access</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAccess()
     * @generated
     * @ordered
     */
    protected static final String ACCESS_EDEFAULT = "sync"; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getAccess() <em>Access</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAccess()
     * @generated
     * @ordered
     */
    protected String access = ACCESS_EDEFAULT;

    /**
     * This is true if the Access attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean accessESet;

    /**
     * The default value of the '{@link #getAlias() <em>Alias</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAlias()
     * @generated
     * @ordered
     */
    protected static final String ALIAS_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getAlias() <em>Alias</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAlias()
     * @generated
     * @ordered
     */
    protected String alias = ALIAS_EDEFAULT;

    /**
     * The default value of the '{@link #isConnected() <em>Connected</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isConnected()
     * @generated
     * @ordered
     */
    protected static final boolean CONNECTED_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isConnected() <em>Connected</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isConnected()
     * @generated
     * @ordered
     */
    protected boolean connected = CONNECTED_EDEFAULT;

    /**
     * This is true if the Connected attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean connectedESet;

    /**
     * The default value of the '{@link #getDomain() <em>Domain</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDomain()
     * @generated
     * @ordered
     */
    protected static final String DOMAIN_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDomain() <em>Domain</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDomain()
     * @generated
     * @ordered
     */
    protected String domain = DOMAIN_EDEFAULT;

    /**
     * The default value of the '{@link #isEnabled() <em>Enabled</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isEnabled()
     * @generated
     * @ordered
     */
    protected static final boolean ENABLED_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isEnabled() <em>Enabled</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isEnabled()
     * @generated
     * @ordered
     */
    protected boolean enabled = ENABLED_EDEFAULT;

    /**
     * This is true if the Enabled attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean enabledESet;

    /**
     * The default value of the '{@link #isFlatBrowser() <em>Flat Browser</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isFlatBrowser()
     * @generated
     * @ordered
     */
    protected static final boolean FLAT_BROWSER_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isFlatBrowser() <em>Flat Browser</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isFlatBrowser()
     * @generated
     * @ordered
     */
    protected boolean flatBrowser = FLAT_BROWSER_EDEFAULT;

    /**
     * This is true if the Flat Browser attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean flatBrowserESet;

    /**
     * The default value of the '{@link #getHost() <em>Host</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getHost()
     * @generated
     * @ordered
     */
    protected static final String HOST_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getHost() <em>Host</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getHost()
     * @generated
     * @ordered
     */
    protected String host = HOST_EDEFAULT;

    /**
     * The default value of the '{@link #isIgnoreTimestampOnlyChange() <em>Ignore Timestamp Only Change</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIgnoreTimestampOnlyChange()
     * @generated
     * @ordered
     */
    protected static final boolean IGNORE_TIMESTAMP_ONLY_CHANGE_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isIgnoreTimestampOnlyChange() <em>Ignore Timestamp Only Change</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isIgnoreTimestampOnlyChange()
     * @generated
     * @ordered
     */
    protected boolean ignoreTimestampOnlyChange = IGNORE_TIMESTAMP_ONLY_CHANGE_EDEFAULT;

    /**
     * This is true if the Ignore Timestamp Only Change attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean ignoreTimestampOnlyChangeESet;

    /**
     * The default value of the '{@link #isInitialRefresh() <em>Initial Refresh</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isInitialRefresh()
     * @generated
     * @ordered
     */
    protected static final boolean INITIAL_REFRESH_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isInitialRefresh() <em>Initial Refresh</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isInitialRefresh()
     * @generated
     * @ordered
     */
    protected boolean initialRefresh = INITIAL_REFRESH_EDEFAULT;

    /**
     * This is true if the Initial Refresh attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean initialRefreshESet;

    /**
     * The default value of the '{@link #getItemIdPrefix() <em>Item Id Prefix</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getItemIdPrefix()
     * @generated
     * @ordered
     */
    protected static final String ITEM_ID_PREFIX_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getItemIdPrefix() <em>Item Id Prefix</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getItemIdPrefix()
     * @generated
     * @ordered
     */
    protected String itemIdPrefix = ITEM_ID_PREFIX_EDEFAULT;

    /**
     * The default value of the '{@link #getPassword() <em>Password</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPassword()
     * @generated
     * @ordered
     */
    protected static final String PASSWORD_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPassword() <em>Password</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPassword()
     * @generated
     * @ordered
     */
    protected String password = PASSWORD_EDEFAULT;

    /**
     * The default value of the '{@link #getQualityErrorIfLessThen() <em>Quality Error If Less Then</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getQualityErrorIfLessThen()
     * @generated
     * @ordered
     */
    protected static final int QUALITY_ERROR_IF_LESS_THEN_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getQualityErrorIfLessThen() <em>Quality Error If Less Then</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getQualityErrorIfLessThen()
     * @generated
     * @ordered
     */
    protected int qualityErrorIfLessThen = QUALITY_ERROR_IF_LESS_THEN_EDEFAULT;

    /**
     * This is true if the Quality Error If Less Then attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean qualityErrorIfLessThenESet;

    /**
     * The default value of the '{@link #getReconnectDelay() <em>Reconnect Delay</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReconnectDelay()
     * @generated
     * @ordered
     */
    protected static final int RECONNECT_DELAY_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getReconnectDelay() <em>Reconnect Delay</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReconnectDelay()
     * @generated
     * @ordered
     */
    protected int reconnectDelay = RECONNECT_DELAY_EDEFAULT;

    /**
     * This is true if the Reconnect Delay attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean reconnectDelayESet;

    /**
     * The default value of the '{@link #getRefresh() <em>Refresh</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRefresh()
     * @generated
     * @ordered
     */
    protected static final int REFRESH_EDEFAULT = 500;

    /**
     * The cached value of the '{@link #getRefresh() <em>Refresh</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRefresh()
     * @generated
     * @ordered
     */
    protected int refresh = REFRESH_EDEFAULT;

    /**
     * This is true if the Refresh attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean refreshESet;

    /**
     * The default value of the '{@link #isTreeBrowser() <em>Tree Browser</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isTreeBrowser()
     * @generated
     * @ordered
     */
    protected static final boolean TREE_BROWSER_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isTreeBrowser() <em>Tree Browser</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isTreeBrowser()
     * @generated
     * @ordered
     */
    protected boolean treeBrowser = TREE_BROWSER_EDEFAULT;

    /**
     * This is true if the Tree Browser attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean treeBrowserESet;

    /**
     * The default value of the '{@link #getUser() <em>User</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUser()
     * @generated
     * @ordered
     */
    protected static final String USER_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUser() <em>User</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUser()
     * @generated
     * @ordered
     */
    protected String user = USER_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ConfigurationTypeImpl ()
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
        return ConfigurationPackage.Literals.CONFIGURATION_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getProgid ()
    {
        return progid;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setProgid ( String newProgid )
    {
        String oldProgid = progid;
        progid = newProgid;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__PROGID, oldProgid, progid ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getClsid ()
    {
        return clsid;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setClsid ( String newClsid )
    {
        String oldClsid = clsid;
        clsid = newClsid;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__CLSID, oldClsid, clsid ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<String> getInitialItem ()
    {
        if ( initialItem == null )
        {
            initialItem = new EDataTypeEList<String> ( String.class, this, ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_ITEM );
        }
        return initialItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getInitialItemResource ()
    {
        return initialItemResource;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setInitialItemResource ( String newInitialItemResource )
    {
        String oldInitialItemResource = initialItemResource;
        initialItemResource = newInitialItemResource;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_ITEM_RESOURCE, oldInitialItemResource, initialItemResource ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getAccess ()
    {
        return access;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAccess ( String newAccess )
    {
        String oldAccess = access;
        access = newAccess;
        boolean oldAccessESet = accessESet;
        accessESet = true;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__ACCESS, oldAccess, access, !oldAccessESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetAccess ()
    {
        String oldAccess = access;
        boolean oldAccessESet = accessESet;
        access = ACCESS_EDEFAULT;
        accessESet = false;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.CONFIGURATION_TYPE__ACCESS, oldAccess, ACCESS_EDEFAULT, oldAccessESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetAccess ()
    {
        return accessESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getAlias ()
    {
        return alias;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAlias ( String newAlias )
    {
        String oldAlias = alias;
        alias = newAlias;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__ALIAS, oldAlias, alias ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isConnected ()
    {
        return connected;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setConnected ( boolean newConnected )
    {
        boolean oldConnected = connected;
        connected = newConnected;
        boolean oldConnectedESet = connectedESet;
        connectedESet = true;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__CONNECTED, oldConnected, connected, !oldConnectedESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetConnected ()
    {
        boolean oldConnected = connected;
        boolean oldConnectedESet = connectedESet;
        connected = CONNECTED_EDEFAULT;
        connectedESet = false;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.CONFIGURATION_TYPE__CONNECTED, oldConnected, CONNECTED_EDEFAULT, oldConnectedESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetConnected ()
    {
        return connectedESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDomain ()
    {
        return domain;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDomain ( String newDomain )
    {
        String oldDomain = domain;
        domain = newDomain;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__DOMAIN, oldDomain, domain ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isEnabled ()
    {
        return enabled;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEnabled ( boolean newEnabled )
    {
        boolean oldEnabled = enabled;
        enabled = newEnabled;
        boolean oldEnabledESet = enabledESet;
        enabledESet = true;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__ENABLED, oldEnabled, enabled, !oldEnabledESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetEnabled ()
    {
        boolean oldEnabled = enabled;
        boolean oldEnabledESet = enabledESet;
        enabled = ENABLED_EDEFAULT;
        enabledESet = false;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.CONFIGURATION_TYPE__ENABLED, oldEnabled, ENABLED_EDEFAULT, oldEnabledESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetEnabled ()
    {
        return enabledESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isFlatBrowser ()
    {
        return flatBrowser;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFlatBrowser ( boolean newFlatBrowser )
    {
        boolean oldFlatBrowser = flatBrowser;
        flatBrowser = newFlatBrowser;
        boolean oldFlatBrowserESet = flatBrowserESet;
        flatBrowserESet = true;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__FLAT_BROWSER, oldFlatBrowser, flatBrowser, !oldFlatBrowserESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetFlatBrowser ()
    {
        boolean oldFlatBrowser = flatBrowser;
        boolean oldFlatBrowserESet = flatBrowserESet;
        flatBrowser = FLAT_BROWSER_EDEFAULT;
        flatBrowserESet = false;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.CONFIGURATION_TYPE__FLAT_BROWSER, oldFlatBrowser, FLAT_BROWSER_EDEFAULT, oldFlatBrowserESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetFlatBrowser ()
    {
        return flatBrowserESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getHost ()
    {
        return host;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setHost ( String newHost )
    {
        String oldHost = host;
        host = newHost;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__HOST, oldHost, host ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isIgnoreTimestampOnlyChange ()
    {
        return ignoreTimestampOnlyChange;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setIgnoreTimestampOnlyChange ( boolean newIgnoreTimestampOnlyChange )
    {
        boolean oldIgnoreTimestampOnlyChange = ignoreTimestampOnlyChange;
        ignoreTimestampOnlyChange = newIgnoreTimestampOnlyChange;
        boolean oldIgnoreTimestampOnlyChangeESet = ignoreTimestampOnlyChangeESet;
        ignoreTimestampOnlyChangeESet = true;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__IGNORE_TIMESTAMP_ONLY_CHANGE, oldIgnoreTimestampOnlyChange, ignoreTimestampOnlyChange, !oldIgnoreTimestampOnlyChangeESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetIgnoreTimestampOnlyChange ()
    {
        boolean oldIgnoreTimestampOnlyChange = ignoreTimestampOnlyChange;
        boolean oldIgnoreTimestampOnlyChangeESet = ignoreTimestampOnlyChangeESet;
        ignoreTimestampOnlyChange = IGNORE_TIMESTAMP_ONLY_CHANGE_EDEFAULT;
        ignoreTimestampOnlyChangeESet = false;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.CONFIGURATION_TYPE__IGNORE_TIMESTAMP_ONLY_CHANGE, oldIgnoreTimestampOnlyChange, IGNORE_TIMESTAMP_ONLY_CHANGE_EDEFAULT, oldIgnoreTimestampOnlyChangeESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetIgnoreTimestampOnlyChange ()
    {
        return ignoreTimestampOnlyChangeESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isInitialRefresh ()
    {
        return initialRefresh;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setInitialRefresh ( boolean newInitialRefresh )
    {
        boolean oldInitialRefresh = initialRefresh;
        initialRefresh = newInitialRefresh;
        boolean oldInitialRefreshESet = initialRefreshESet;
        initialRefreshESet = true;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_REFRESH, oldInitialRefresh, initialRefresh, !oldInitialRefreshESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetInitialRefresh ()
    {
        boolean oldInitialRefresh = initialRefresh;
        boolean oldInitialRefreshESet = initialRefreshESet;
        initialRefresh = INITIAL_REFRESH_EDEFAULT;
        initialRefreshESet = false;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_REFRESH, oldInitialRefresh, INITIAL_REFRESH_EDEFAULT, oldInitialRefreshESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetInitialRefresh ()
    {
        return initialRefreshESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getItemIdPrefix ()
    {
        return itemIdPrefix;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setItemIdPrefix ( String newItemIdPrefix )
    {
        String oldItemIdPrefix = itemIdPrefix;
        itemIdPrefix = newItemIdPrefix;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__ITEM_ID_PREFIX, oldItemIdPrefix, itemIdPrefix ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getPassword ()
    {
        return password;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPassword ( String newPassword )
    {
        String oldPassword = password;
        password = newPassword;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__PASSWORD, oldPassword, password ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getQualityErrorIfLessThen ()
    {
        return qualityErrorIfLessThen;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setQualityErrorIfLessThen ( int newQualityErrorIfLessThen )
    {
        int oldQualityErrorIfLessThen = qualityErrorIfLessThen;
        qualityErrorIfLessThen = newQualityErrorIfLessThen;
        boolean oldQualityErrorIfLessThenESet = qualityErrorIfLessThenESet;
        qualityErrorIfLessThenESet = true;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__QUALITY_ERROR_IF_LESS_THEN, oldQualityErrorIfLessThen, qualityErrorIfLessThen, !oldQualityErrorIfLessThenESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetQualityErrorIfLessThen ()
    {
        int oldQualityErrorIfLessThen = qualityErrorIfLessThen;
        boolean oldQualityErrorIfLessThenESet = qualityErrorIfLessThenESet;
        qualityErrorIfLessThen = QUALITY_ERROR_IF_LESS_THEN_EDEFAULT;
        qualityErrorIfLessThenESet = false;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.CONFIGURATION_TYPE__QUALITY_ERROR_IF_LESS_THEN, oldQualityErrorIfLessThen, QUALITY_ERROR_IF_LESS_THEN_EDEFAULT, oldQualityErrorIfLessThenESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetQualityErrorIfLessThen ()
    {
        return qualityErrorIfLessThenESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getReconnectDelay ()
    {
        return reconnectDelay;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setReconnectDelay ( int newReconnectDelay )
    {
        int oldReconnectDelay = reconnectDelay;
        reconnectDelay = newReconnectDelay;
        boolean oldReconnectDelayESet = reconnectDelayESet;
        reconnectDelayESet = true;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__RECONNECT_DELAY, oldReconnectDelay, reconnectDelay, !oldReconnectDelayESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetReconnectDelay ()
    {
        int oldReconnectDelay = reconnectDelay;
        boolean oldReconnectDelayESet = reconnectDelayESet;
        reconnectDelay = RECONNECT_DELAY_EDEFAULT;
        reconnectDelayESet = false;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.CONFIGURATION_TYPE__RECONNECT_DELAY, oldReconnectDelay, RECONNECT_DELAY_EDEFAULT, oldReconnectDelayESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetReconnectDelay ()
    {
        return reconnectDelayESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getRefresh ()
    {
        return refresh;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRefresh ( int newRefresh )
    {
        int oldRefresh = refresh;
        refresh = newRefresh;
        boolean oldRefreshESet = refreshESet;
        refreshESet = true;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__REFRESH, oldRefresh, refresh, !oldRefreshESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetRefresh ()
    {
        int oldRefresh = refresh;
        boolean oldRefreshESet = refreshESet;
        refresh = REFRESH_EDEFAULT;
        refreshESet = false;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.CONFIGURATION_TYPE__REFRESH, oldRefresh, REFRESH_EDEFAULT, oldRefreshESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetRefresh ()
    {
        return refreshESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isTreeBrowser ()
    {
        return treeBrowser;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTreeBrowser ( boolean newTreeBrowser )
    {
        boolean oldTreeBrowser = treeBrowser;
        treeBrowser = newTreeBrowser;
        boolean oldTreeBrowserESet = treeBrowserESet;
        treeBrowserESet = true;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__TREE_BROWSER, oldTreeBrowser, treeBrowser, !oldTreeBrowserESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetTreeBrowser ()
    {
        boolean oldTreeBrowser = treeBrowser;
        boolean oldTreeBrowserESet = treeBrowserESet;
        treeBrowser = TREE_BROWSER_EDEFAULT;
        treeBrowserESet = false;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.CONFIGURATION_TYPE__TREE_BROWSER, oldTreeBrowser, TREE_BROWSER_EDEFAULT, oldTreeBrowserESet ) );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetTreeBrowser ()
    {
        return treeBrowserESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getUser ()
    {
        return user;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUser ( String newUser )
    {
        String oldUser = user;
        user = newUser;
        if ( eNotificationRequired () )
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.CONFIGURATION_TYPE__USER, oldUser, user ) );
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
            case ConfigurationPackage.CONFIGURATION_TYPE__PROGID:
                return getProgid ();
            case ConfigurationPackage.CONFIGURATION_TYPE__CLSID:
                return getClsid ();
            case ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_ITEM:
                return getInitialItem ();
            case ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_ITEM_RESOURCE:
                return getInitialItemResource ();
            case ConfigurationPackage.CONFIGURATION_TYPE__ACCESS:
                return getAccess ();
            case ConfigurationPackage.CONFIGURATION_TYPE__ALIAS:
                return getAlias ();
            case ConfigurationPackage.CONFIGURATION_TYPE__CONNECTED:
                return isConnected ();
            case ConfigurationPackage.CONFIGURATION_TYPE__DOMAIN:
                return getDomain ();
            case ConfigurationPackage.CONFIGURATION_TYPE__ENABLED:
                return isEnabled ();
            case ConfigurationPackage.CONFIGURATION_TYPE__FLAT_BROWSER:
                return isFlatBrowser ();
            case ConfigurationPackage.CONFIGURATION_TYPE__HOST:
                return getHost ();
            case ConfigurationPackage.CONFIGURATION_TYPE__IGNORE_TIMESTAMP_ONLY_CHANGE:
                return isIgnoreTimestampOnlyChange ();
            case ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_REFRESH:
                return isInitialRefresh ();
            case ConfigurationPackage.CONFIGURATION_TYPE__ITEM_ID_PREFIX:
                return getItemIdPrefix ();
            case ConfigurationPackage.CONFIGURATION_TYPE__PASSWORD:
                return getPassword ();
            case ConfigurationPackage.CONFIGURATION_TYPE__QUALITY_ERROR_IF_LESS_THEN:
                return getQualityErrorIfLessThen ();
            case ConfigurationPackage.CONFIGURATION_TYPE__RECONNECT_DELAY:
                return getReconnectDelay ();
            case ConfigurationPackage.CONFIGURATION_TYPE__REFRESH:
                return getRefresh ();
            case ConfigurationPackage.CONFIGURATION_TYPE__TREE_BROWSER:
                return isTreeBrowser ();
            case ConfigurationPackage.CONFIGURATION_TYPE__USER:
                return getUser ();
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
            case ConfigurationPackage.CONFIGURATION_TYPE__PROGID:
                setProgid ( (String)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__CLSID:
                setClsid ( (String)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_ITEM:
                getInitialItem ().clear ();
                getInitialItem ().addAll ( (Collection<? extends String>)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_ITEM_RESOURCE:
                setInitialItemResource ( (String)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__ACCESS:
                setAccess ( (String)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__ALIAS:
                setAlias ( (String)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__CONNECTED:
                setConnected ( (Boolean)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__DOMAIN:
                setDomain ( (String)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__ENABLED:
                setEnabled ( (Boolean)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__FLAT_BROWSER:
                setFlatBrowser ( (Boolean)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__HOST:
                setHost ( (String)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__IGNORE_TIMESTAMP_ONLY_CHANGE:
                setIgnoreTimestampOnlyChange ( (Boolean)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_REFRESH:
                setInitialRefresh ( (Boolean)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__ITEM_ID_PREFIX:
                setItemIdPrefix ( (String)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__PASSWORD:
                setPassword ( (String)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__QUALITY_ERROR_IF_LESS_THEN:
                setQualityErrorIfLessThen ( (Integer)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__RECONNECT_DELAY:
                setReconnectDelay ( (Integer)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__REFRESH:
                setRefresh ( (Integer)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__TREE_BROWSER:
                setTreeBrowser ( (Boolean)newValue );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__USER:
                setUser ( (String)newValue );
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
            case ConfigurationPackage.CONFIGURATION_TYPE__PROGID:
                setProgid ( PROGID_EDEFAULT );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__CLSID:
                setClsid ( CLSID_EDEFAULT );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_ITEM:
                getInitialItem ().clear ();
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_ITEM_RESOURCE:
                setInitialItemResource ( INITIAL_ITEM_RESOURCE_EDEFAULT );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__ACCESS:
                unsetAccess ();
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__ALIAS:
                setAlias ( ALIAS_EDEFAULT );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__CONNECTED:
                unsetConnected ();
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__DOMAIN:
                setDomain ( DOMAIN_EDEFAULT );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__ENABLED:
                unsetEnabled ();
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__FLAT_BROWSER:
                unsetFlatBrowser ();
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__HOST:
                setHost ( HOST_EDEFAULT );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__IGNORE_TIMESTAMP_ONLY_CHANGE:
                unsetIgnoreTimestampOnlyChange ();
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_REFRESH:
                unsetInitialRefresh ();
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__ITEM_ID_PREFIX:
                setItemIdPrefix ( ITEM_ID_PREFIX_EDEFAULT );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__PASSWORD:
                setPassword ( PASSWORD_EDEFAULT );
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__QUALITY_ERROR_IF_LESS_THEN:
                unsetQualityErrorIfLessThen ();
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__RECONNECT_DELAY:
                unsetReconnectDelay ();
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__REFRESH:
                unsetRefresh ();
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__TREE_BROWSER:
                unsetTreeBrowser ();
                return;
            case ConfigurationPackage.CONFIGURATION_TYPE__USER:
                setUser ( USER_EDEFAULT );
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
            case ConfigurationPackage.CONFIGURATION_TYPE__PROGID:
                return PROGID_EDEFAULT == null ? progid != null : !PROGID_EDEFAULT.equals ( progid );
            case ConfigurationPackage.CONFIGURATION_TYPE__CLSID:
                return CLSID_EDEFAULT == null ? clsid != null : !CLSID_EDEFAULT.equals ( clsid );
            case ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_ITEM:
                return initialItem != null && !initialItem.isEmpty ();
            case ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_ITEM_RESOURCE:
                return INITIAL_ITEM_RESOURCE_EDEFAULT == null ? initialItemResource != null : !INITIAL_ITEM_RESOURCE_EDEFAULT.equals ( initialItemResource );
            case ConfigurationPackage.CONFIGURATION_TYPE__ACCESS:
                return isSetAccess ();
            case ConfigurationPackage.CONFIGURATION_TYPE__ALIAS:
                return ALIAS_EDEFAULT == null ? alias != null : !ALIAS_EDEFAULT.equals ( alias );
            case ConfigurationPackage.CONFIGURATION_TYPE__CONNECTED:
                return isSetConnected ();
            case ConfigurationPackage.CONFIGURATION_TYPE__DOMAIN:
                return DOMAIN_EDEFAULT == null ? domain != null : !DOMAIN_EDEFAULT.equals ( domain );
            case ConfigurationPackage.CONFIGURATION_TYPE__ENABLED:
                return isSetEnabled ();
            case ConfigurationPackage.CONFIGURATION_TYPE__FLAT_BROWSER:
                return isSetFlatBrowser ();
            case ConfigurationPackage.CONFIGURATION_TYPE__HOST:
                return HOST_EDEFAULT == null ? host != null : !HOST_EDEFAULT.equals ( host );
            case ConfigurationPackage.CONFIGURATION_TYPE__IGNORE_TIMESTAMP_ONLY_CHANGE:
                return isSetIgnoreTimestampOnlyChange ();
            case ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_REFRESH:
                return isSetInitialRefresh ();
            case ConfigurationPackage.CONFIGURATION_TYPE__ITEM_ID_PREFIX:
                return ITEM_ID_PREFIX_EDEFAULT == null ? itemIdPrefix != null : !ITEM_ID_PREFIX_EDEFAULT.equals ( itemIdPrefix );
            case ConfigurationPackage.CONFIGURATION_TYPE__PASSWORD:
                return PASSWORD_EDEFAULT == null ? password != null : !PASSWORD_EDEFAULT.equals ( password );
            case ConfigurationPackage.CONFIGURATION_TYPE__QUALITY_ERROR_IF_LESS_THEN:
                return isSetQualityErrorIfLessThen ();
            case ConfigurationPackage.CONFIGURATION_TYPE__RECONNECT_DELAY:
                return isSetReconnectDelay ();
            case ConfigurationPackage.CONFIGURATION_TYPE__REFRESH:
                return isSetRefresh ();
            case ConfigurationPackage.CONFIGURATION_TYPE__TREE_BROWSER:
                return isSetTreeBrowser ();
            case ConfigurationPackage.CONFIGURATION_TYPE__USER:
                return USER_EDEFAULT == null ? user != null : !USER_EDEFAULT.equals ( user );
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
        result.append ( " (progid: " ); //$NON-NLS-1$
        result.append ( progid );
        result.append ( ", clsid: " ); //$NON-NLS-1$
        result.append ( clsid );
        result.append ( ", initialItem: " ); //$NON-NLS-1$
        result.append ( initialItem );
        result.append ( ", initialItemResource: " ); //$NON-NLS-1$
        result.append ( initialItemResource );
        result.append ( ", access: " ); //$NON-NLS-1$
        if ( accessESet )
            result.append ( access );
        else
            result.append ( "<unset>" ); //$NON-NLS-1$
        result.append ( ", alias: " ); //$NON-NLS-1$
        result.append ( alias );
        result.append ( ", connected: " ); //$NON-NLS-1$
        if ( connectedESet )
            result.append ( connected );
        else
            result.append ( "<unset>" ); //$NON-NLS-1$
        result.append ( ", domain: " ); //$NON-NLS-1$
        result.append ( domain );
        result.append ( ", enabled: " ); //$NON-NLS-1$
        if ( enabledESet )
            result.append ( enabled );
        else
            result.append ( "<unset>" ); //$NON-NLS-1$
        result.append ( ", flatBrowser: " ); //$NON-NLS-1$
        if ( flatBrowserESet )
            result.append ( flatBrowser );
        else
            result.append ( "<unset>" ); //$NON-NLS-1$
        result.append ( ", host: " ); //$NON-NLS-1$
        result.append ( host );
        result.append ( ", ignoreTimestampOnlyChange: " ); //$NON-NLS-1$
        if ( ignoreTimestampOnlyChangeESet )
            result.append ( ignoreTimestampOnlyChange );
        else
            result.append ( "<unset>" ); //$NON-NLS-1$
        result.append ( ", initialRefresh: " ); //$NON-NLS-1$
        if ( initialRefreshESet )
            result.append ( initialRefresh );
        else
            result.append ( "<unset>" ); //$NON-NLS-1$
        result.append ( ", itemIdPrefix: " ); //$NON-NLS-1$
        result.append ( itemIdPrefix );
        result.append ( ", password: " ); //$NON-NLS-1$
        result.append ( password );
        result.append ( ", qualityErrorIfLessThen: " ); //$NON-NLS-1$
        if ( qualityErrorIfLessThenESet )
            result.append ( qualityErrorIfLessThen );
        else
            result.append ( "<unset>" ); //$NON-NLS-1$
        result.append ( ", reconnectDelay: " ); //$NON-NLS-1$
        if ( reconnectDelayESet )
            result.append ( reconnectDelay );
        else
            result.append ( "<unset>" ); //$NON-NLS-1$
        result.append ( ", refresh: " ); //$NON-NLS-1$
        if ( refreshESet )
            result.append ( refresh );
        else
            result.append ( "<unset>" ); //$NON-NLS-1$
        result.append ( ", treeBrowser: " ); //$NON-NLS-1$
        if ( treeBrowserESet )
            result.append ( treeBrowser );
        else
            result.append ( "<unset>" ); //$NON-NLS-1$
        result.append ( ", user: " ); //$NON-NLS-1$
        result.append ( user );
        result.append ( ')' );
        return result.toString ();
    }

} //ConfigurationTypeImpl
