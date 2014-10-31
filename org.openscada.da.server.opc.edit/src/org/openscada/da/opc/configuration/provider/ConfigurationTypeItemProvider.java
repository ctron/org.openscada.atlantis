/**
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openscada.da.opc.configuration.provider;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IChildCreationExtender;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

import org.openscada.da.opc.configuration.ConfigurationPackage;
import org.openscada.da.opc.configuration.ConfigurationType;

/**
 * This is the item provider adapter for a {@link org.openscada.da.opc.configuration.ConfigurationType} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class ConfigurationTypeItemProvider extends ItemProviderAdapter implements IEditingDomainItemProvider, IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource
{
    /**
     * This constructs an instance from a factory and a notifier.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ConfigurationTypeItemProvider ( AdapterFactory adapterFactory )
    {
        super ( adapterFactory );
    }

    /**
     * This returns the property descriptors for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public List<IItemPropertyDescriptor> getPropertyDescriptors ( Object object )
    {
        if ( itemPropertyDescriptors == null )
        {
            super.getPropertyDescriptors ( object );

            addProgidPropertyDescriptor ( object );
            addClsidPropertyDescriptor ( object );
            addInitialItemPropertyDescriptor ( object );
            addInitialItemResourcePropertyDescriptor ( object );
            addAccessPropertyDescriptor ( object );
            addAliasPropertyDescriptor ( object );
            addConnectedPropertyDescriptor ( object );
            addDomainPropertyDescriptor ( object );
            addEnabledPropertyDescriptor ( object );
            addFlatBrowserPropertyDescriptor ( object );
            addHostPropertyDescriptor ( object );
            addIgnoreTimestampOnlyChangePropertyDescriptor ( object );
            addInitialRefreshPropertyDescriptor ( object );
            addItemIdPrefixPropertyDescriptor ( object );
            addPasswordPropertyDescriptor ( object );
            addQualityErrorIfLessThenPropertyDescriptor ( object );
            addReconnectDelayPropertyDescriptor ( object );
            addRefreshPropertyDescriptor ( object );
            addTreeBrowserPropertyDescriptor ( object );
            addUserPropertyDescriptor ( object );
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Progid feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addProgidPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_progid_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_progid_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__PROGID, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Clsid feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addClsidPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_clsid_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_clsid_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__CLSID, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Initial Item feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addInitialItemPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_initialItem_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_initialItem_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__INITIAL_ITEM, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Initial Item Resource feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addInitialItemResourcePropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_initialItemResource_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_initialItemResource_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__INITIAL_ITEM_RESOURCE, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Access feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addAccessPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_access_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_access_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__ACCESS, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Alias feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addAliasPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_alias_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_alias_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__ALIAS, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Connected feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addConnectedPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_connected_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_connected_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__CONNECTED, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Domain feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addDomainPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_domain_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_domain_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__DOMAIN, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Enabled feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addEnabledPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_enabled_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_enabled_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__ENABLED, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Flat Browser feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addFlatBrowserPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_flatBrowser_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_flatBrowser_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__FLAT_BROWSER, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Host feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addHostPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_host_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_host_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__HOST, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Ignore Timestamp Only Change feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addIgnoreTimestampOnlyChangePropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_ignoreTimestampOnlyChange_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_ignoreTimestampOnlyChange_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__IGNORE_TIMESTAMP_ONLY_CHANGE, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Initial Refresh feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addInitialRefreshPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_initialRefresh_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_initialRefresh_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__INITIAL_REFRESH, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Item Id Prefix feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addItemIdPrefixPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_itemIdPrefix_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_itemIdPrefix_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__ITEM_ID_PREFIX, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Password feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addPasswordPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_password_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_password_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__PASSWORD, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Quality Error If Less Then feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addQualityErrorIfLessThenPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_qualityErrorIfLessThen_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_qualityErrorIfLessThen_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__QUALITY_ERROR_IF_LESS_THEN, true, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Reconnect Delay feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addReconnectDelayPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_reconnectDelay_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_reconnectDelay_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__RECONNECT_DELAY, true, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Refresh feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addRefreshPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_refresh_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_refresh_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__REFRESH, true, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Tree Browser feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addTreeBrowserPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_treeBrowser_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_treeBrowser_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__TREE_BROWSER, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the User feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addUserPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ConfigurationType_user_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ConfigurationType_user_feature", "_UI_ConfigurationType_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.CONFIGURATION_TYPE__USER, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null ) );
    }

    /**
     * This returns ConfigurationType.gif.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object getImage ( Object object )
    {
        return overlayImage ( object, getResourceLocator ().getImage ( "full/obj16/ConfigurationType" ) ); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getText ( Object object )
    {
        String label = ( (ConfigurationType)object ).getProgid ();
        return label == null || label.length () == 0 ? getString ( "_UI_ConfigurationType_type" ) : //$NON-NLS-1$
        getString ( "_UI_ConfigurationType_type" ) + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * This handles model notifications by calling {@link #updateChildren} to update any cached
     * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void notifyChanged ( Notification notification )
    {
        updateChildren ( notification );

        switch ( notification.getFeatureID ( ConfigurationType.class ) )
        {
            case ConfigurationPackage.CONFIGURATION_TYPE__PROGID:
            case ConfigurationPackage.CONFIGURATION_TYPE__CLSID:
            case ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_ITEM:
            case ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_ITEM_RESOURCE:
            case ConfigurationPackage.CONFIGURATION_TYPE__ACCESS:
            case ConfigurationPackage.CONFIGURATION_TYPE__ALIAS:
            case ConfigurationPackage.CONFIGURATION_TYPE__CONNECTED:
            case ConfigurationPackage.CONFIGURATION_TYPE__DOMAIN:
            case ConfigurationPackage.CONFIGURATION_TYPE__ENABLED:
            case ConfigurationPackage.CONFIGURATION_TYPE__FLAT_BROWSER:
            case ConfigurationPackage.CONFIGURATION_TYPE__HOST:
            case ConfigurationPackage.CONFIGURATION_TYPE__IGNORE_TIMESTAMP_ONLY_CHANGE:
            case ConfigurationPackage.CONFIGURATION_TYPE__INITIAL_REFRESH:
            case ConfigurationPackage.CONFIGURATION_TYPE__ITEM_ID_PREFIX:
            case ConfigurationPackage.CONFIGURATION_TYPE__PASSWORD:
            case ConfigurationPackage.CONFIGURATION_TYPE__QUALITY_ERROR_IF_LESS_THEN:
            case ConfigurationPackage.CONFIGURATION_TYPE__RECONNECT_DELAY:
            case ConfigurationPackage.CONFIGURATION_TYPE__REFRESH:
            case ConfigurationPackage.CONFIGURATION_TYPE__TREE_BROWSER:
            case ConfigurationPackage.CONFIGURATION_TYPE__USER:
                fireNotifyChanged ( new ViewerNotification ( notification, notification.getNotifier (), false, true ) );
                return;
        }
        super.notifyChanged ( notification );
    }

    /**
     * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
     * that can be created under this object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected void collectNewChildDescriptors ( Collection<Object> newChildDescriptors, Object object )
    {
        super.collectNewChildDescriptors ( newChildDescriptors, object );
    }

    /**
     * Return the resource locator for this item provider's resources.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator ()
    {
        return ( (IChildCreationExtender)adapterFactory ).getResourceLocator ();
    }

}
