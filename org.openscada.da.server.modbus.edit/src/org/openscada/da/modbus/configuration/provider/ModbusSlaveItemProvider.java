/**
 */
package org.openscada.da.modbus.configuration.provider;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

import org.openscada.da.modbus.configuration.ConfigurationFactory;
import org.openscada.da.modbus.configuration.ConfigurationPackage;
import org.openscada.da.modbus.configuration.ModbusSlave;

/**
 * This is the item provider adapter for a {@link org.openscada.da.modbus.configuration.ModbusSlave} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class ModbusSlaveItemProvider extends ItemProviderAdapter implements IEditingDomainItemProvider, IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource
{
    /**
     * This constructs an instance from a factory and a notifier.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModbusSlaveItemProvider ( AdapterFactory adapterFactory )
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

            addCoilOffsetPropertyDescriptor ( object );
            addDiscreteInputOffsetPropertyDescriptor ( object );
            addHoldingRegisterOffsetPropertyDescriptor ( object );
            addIdPropertyDescriptor ( object );
            addInputRegisterOffsetPropertyDescriptor ( object );
            addNamePropertyDescriptor ( object );
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Coil Offset feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addCoilOffsetPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ModbusSlave_coilOffset_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ModbusSlave_coilOffset_feature", "_UI_ModbusSlave_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.MODBUS_SLAVE__COIL_OFFSET, true, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Discrete Input Offset feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addDiscreteInputOffsetPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ModbusSlave_discreteInputOffset_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ModbusSlave_discreteInputOffset_feature", "_UI_ModbusSlave_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.MODBUS_SLAVE__DISCRETE_INPUT_OFFSET, true, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Holding Register Offset feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addHoldingRegisterOffsetPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ModbusSlave_holdingRegisterOffset_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ModbusSlave_holdingRegisterOffset_feature", "_UI_ModbusSlave_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.MODBUS_SLAVE__HOLDING_REGISTER_OFFSET, true, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Id feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addIdPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ModbusSlave_id_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ModbusSlave_id_feature", "_UI_ModbusSlave_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.MODBUS_SLAVE__ID, true, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Input Register Offset feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addInputRegisterOffsetPropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ModbusSlave_inputRegisterOffset_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ModbusSlave_inputRegisterOffset_feature", "_UI_ModbusSlave_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.MODBUS_SLAVE__INPUT_REGISTER_OFFSET, true, false, false, ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null ) );
    }

    /**
     * This adds a property descriptor for the Name feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addNamePropertyDescriptor ( Object object )
    {
        itemPropertyDescriptors.add ( createItemPropertyDescriptor ( ( (ComposeableAdapterFactory)adapterFactory ).getRootAdapterFactory (), getResourceLocator (), getString ( "_UI_ModbusSlave_name_feature" ), //$NON-NLS-1$
                getString ( "_UI_PropertyDescriptor_description", "_UI_ModbusSlave_name_feature", "_UI_ModbusSlave_type" ), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                ConfigurationPackage.Literals.MODBUS_SLAVE__NAME, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null ) );
    }

    /**
     * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
     * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
     * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Collection<? extends EStructuralFeature> getChildrenFeatures ( Object object )
    {
        if ( childrenFeatures == null )
        {
            super.getChildrenFeatures ( object );
            childrenFeatures.add ( ConfigurationPackage.Literals.MODBUS_SLAVE__GROUP );
            childrenFeatures.add ( ConfigurationPackage.Literals.MODBUS_SLAVE__GROUP1 );
            childrenFeatures.add ( ConfigurationPackage.Literals.MODBUS_SLAVE__GROUP2 );
            childrenFeatures.add ( ConfigurationPackage.Literals.MODBUS_SLAVE__GROUP3 );
        }
        return childrenFeatures;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EStructuralFeature getChildFeature ( Object object, Object child )
    {
        // Check the type of the specified child object and return the proper feature to use for
        // adding (see {@link AddCommand}) it as a child.

        return super.getChildFeature ( object, child );
    }

    /**
     * This returns ModbusSlave.gif.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object getImage ( Object object )
    {
        return overlayImage ( object, getResourceLocator ().getImage ( "full/obj16/ModbusSlave" ) ); //$NON-NLS-1$
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
        String label = ( (ModbusSlave)object ).getName ();
        return label == null || label.length () == 0 ? getString ( "_UI_ModbusSlave_type" ) : //$NON-NLS-1$
        getString ( "_UI_ModbusSlave_type" ) + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        switch ( notification.getFeatureID ( ModbusSlave.class ) )
        {
            case ConfigurationPackage.MODBUS_SLAVE__COIL_OFFSET:
            case ConfigurationPackage.MODBUS_SLAVE__DISCRETE_INPUT_OFFSET:
            case ConfigurationPackage.MODBUS_SLAVE__HOLDING_REGISTER_OFFSET:
            case ConfigurationPackage.MODBUS_SLAVE__ID:
            case ConfigurationPackage.MODBUS_SLAVE__INPUT_REGISTER_OFFSET:
            case ConfigurationPackage.MODBUS_SLAVE__NAME:
                fireNotifyChanged ( new ViewerNotification ( notification, notification.getNotifier (), false, true ) );
                return;
            case ConfigurationPackage.MODBUS_SLAVE__GROUP:
            case ConfigurationPackage.MODBUS_SLAVE__GROUP1:
            case ConfigurationPackage.MODBUS_SLAVE__GROUP2:
            case ConfigurationPackage.MODBUS_SLAVE__GROUP3:
                fireNotifyChanged ( new ViewerNotification ( notification, notification.getNotifier (), true, false ) );
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

        newChildDescriptors.add ( createChildParameter ( ConfigurationPackage.Literals.MODBUS_SLAVE__GROUP, FeatureMapUtil.createEntry ( ConfigurationPackage.Literals.MODBUS_SLAVE__DISCRETE_INPUT, ConfigurationFactory.eINSTANCE.createItemType () ) ) );

        newChildDescriptors.add ( createChildParameter ( ConfigurationPackage.Literals.MODBUS_SLAVE__GROUP1, FeatureMapUtil.createEntry ( ConfigurationPackage.Literals.MODBUS_SLAVE__COIL, ConfigurationFactory.eINSTANCE.createItemType () ) ) );

        newChildDescriptors.add ( createChildParameter ( ConfigurationPackage.Literals.MODBUS_SLAVE__GROUP2, FeatureMapUtil.createEntry ( ConfigurationPackage.Literals.MODBUS_SLAVE__INPUT_REGISTER, ConfigurationFactory.eINSTANCE.createItemType () ) ) );

        newChildDescriptors.add ( createChildParameter ( ConfigurationPackage.Literals.MODBUS_SLAVE__GROUP3, FeatureMapUtil.createEntry ( ConfigurationPackage.Literals.MODBUS_SLAVE__HOLDING_REGISTER, ConfigurationFactory.eINSTANCE.createItemType () ) ) );
    }

    /**
     * This returns the label text for {@link org.eclipse.emf.edit.command.CreateChildCommand}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getCreateChildText ( Object owner, Object feature, Object child, Collection<?> selection )
    {
        Object childFeature = feature;
        Object childObject = child;

        if ( childFeature instanceof EStructuralFeature && FeatureMapUtil.isFeatureMap ( (EStructuralFeature)childFeature ) )
        {
            FeatureMap.Entry entry = (FeatureMap.Entry)childObject;
            childFeature = entry.getEStructuralFeature ();
            childObject = entry.getValue ();
        }

        boolean qualify = childFeature == ConfigurationPackage.Literals.MODBUS_SLAVE__DISCRETE_INPUT || childFeature == ConfigurationPackage.Literals.MODBUS_SLAVE__COIL || childFeature == ConfigurationPackage.Literals.MODBUS_SLAVE__INPUT_REGISTER || childFeature == ConfigurationPackage.Literals.MODBUS_SLAVE__HOLDING_REGISTER;

        if ( qualify )
        {
            return getString ( "_UI_CreateChild_text2", //$NON-NLS-1$
                    new Object[] { getTypeText ( childObject ), getFeatureText ( childFeature ), getTypeText ( owner ) } );
        }
        return super.getCreateChildText ( owner, feature, child, selection );
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
        return ModbusEditPlugin.INSTANCE;
    }

}
