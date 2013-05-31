/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.modbus.configuration.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.openscada.da.modbus.configuration.ConfigurationFactory;
import org.openscada.da.modbus.configuration.ConfigurationPackage;
import org.openscada.da.modbus.configuration.DeviceType;
import org.openscada.da.modbus.configuration.DevicesType;
import org.openscada.da.modbus.configuration.DocumentRoot;
import org.openscada.da.modbus.configuration.ItemType;
import org.openscada.da.modbus.configuration.ModbusSlave;
import org.openscada.da.modbus.configuration.ParityType;
import org.openscada.da.modbus.configuration.ProtocolType;
import org.openscada.da.modbus.configuration.RootType;
import org.openscada.da.modbus.configuration.StopBitsType;
import org.openscada.da.modbus.configuration.TypeType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class ConfigurationFactoryImpl extends EFactoryImpl implements ConfigurationFactory
{
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public static ConfigurationFactory init ()
    {
        try
        {
            final ConfigurationFactory theConfigurationFactory = (ConfigurationFactory)EPackage.Registry.INSTANCE.getEFactory ( ConfigurationPackage.eNS_URI );
            if ( theConfigurationFactory != null )
            {
                return theConfigurationFactory;
            }
        }
        catch ( final Exception exception )
        {
            EcorePlugin.INSTANCE.log ( exception );
        }
        return new ConfigurationFactoryImpl ();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public ConfigurationFactoryImpl ()
    {
        super ();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EObject create ( final EClass eClass )
    {
        switch ( eClass.getClassifierID () )
        {
            case ConfigurationPackage.DEVICES_TYPE:
                return createDevicesType ();
            case ConfigurationPackage.DEVICE_TYPE:
                return createDeviceType ();
            case ConfigurationPackage.DOCUMENT_ROOT:
                return createDocumentRoot ();
            case ConfigurationPackage.ITEM_TYPE:
                return createItemType ();
            case ConfigurationPackage.MODBUS_SLAVE:
                return createModbusSlave ();
            case ConfigurationPackage.ROOT_TYPE:
                return createRootType ();
            default:
                throw new IllegalArgumentException ( "The class '" + eClass.getName () + "' is not a valid classifier" ); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object createFromString ( final EDataType eDataType, final String initialValue )
    {
        switch ( eDataType.getClassifierID () )
        {
            case ConfigurationPackage.PARITY_TYPE:
                return createParityTypeFromString ( eDataType, initialValue );
            case ConfigurationPackage.PROTOCOL_TYPE:
                return createProtocolTypeFromString ( eDataType, initialValue );
            case ConfigurationPackage.STOP_BITS_TYPE:
                return createStopBitsTypeFromString ( eDataType, initialValue );
            case ConfigurationPackage.TYPE_TYPE:
                return createTypeTypeFromString ( eDataType, initialValue );
            case ConfigurationPackage.DATA_BITS_TYPE:
                return createDataBitsTypeFromString ( eDataType, initialValue );
            case ConfigurationPackage.DATA_BITS_TYPE_OBJECT:
                return createDataBitsTypeObjectFromString ( eDataType, initialValue );
            case ConfigurationPackage.HOST_TYPE:
                return createHostTypeFromString ( eDataType, initialValue );
            case ConfigurationPackage.ID_TYPE:
                return createIdTypeFromString ( eDataType, initialValue );
            case ConfigurationPackage.ID_TYPE1:
                return createIdType1FromString ( eDataType, initialValue );
            case ConfigurationPackage.ID_TYPE_OBJECT:
                return createIdTypeObjectFromString ( eDataType, initialValue );
            case ConfigurationPackage.PARITY_TYPE_OBJECT:
                return createParityTypeObjectFromString ( eDataType, initialValue );
            case ConfigurationPackage.PROTOCOL_TYPE_OBJECT:
                return createProtocolTypeObjectFromString ( eDataType, initialValue );
            case ConfigurationPackage.START_ADDRESS_TYPE:
                return createStartAddressTypeFromString ( eDataType, initialValue );
            case ConfigurationPackage.STOP_BITS_TYPE_OBJECT:
                return createStopBitsTypeObjectFromString ( eDataType, initialValue );
            case ConfigurationPackage.TYPE_TYPE_OBJECT:
                return createTypeTypeObjectFromString ( eDataType, initialValue );
            default:
                throw new IllegalArgumentException ( "The datatype '" + eDataType.getName () + "' is not a valid classifier" ); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String convertToString ( final EDataType eDataType, final Object instanceValue )
    {
        switch ( eDataType.getClassifierID () )
        {
            case ConfigurationPackage.PARITY_TYPE:
                return convertParityTypeToString ( eDataType, instanceValue );
            case ConfigurationPackage.PROTOCOL_TYPE:
                return convertProtocolTypeToString ( eDataType, instanceValue );
            case ConfigurationPackage.STOP_BITS_TYPE:
                return convertStopBitsTypeToString ( eDataType, instanceValue );
            case ConfigurationPackage.TYPE_TYPE:
                return convertTypeTypeToString ( eDataType, instanceValue );
            case ConfigurationPackage.DATA_BITS_TYPE:
                return convertDataBitsTypeToString ( eDataType, instanceValue );
            case ConfigurationPackage.DATA_BITS_TYPE_OBJECT:
                return convertDataBitsTypeObjectToString ( eDataType, instanceValue );
            case ConfigurationPackage.HOST_TYPE:
                return convertHostTypeToString ( eDataType, instanceValue );
            case ConfigurationPackage.ID_TYPE:
                return convertIdTypeToString ( eDataType, instanceValue );
            case ConfigurationPackage.ID_TYPE1:
                return convertIdType1ToString ( eDataType, instanceValue );
            case ConfigurationPackage.ID_TYPE_OBJECT:
                return convertIdTypeObjectToString ( eDataType, instanceValue );
            case ConfigurationPackage.PARITY_TYPE_OBJECT:
                return convertParityTypeObjectToString ( eDataType, instanceValue );
            case ConfigurationPackage.PROTOCOL_TYPE_OBJECT:
                return convertProtocolTypeObjectToString ( eDataType, instanceValue );
            case ConfigurationPackage.START_ADDRESS_TYPE:
                return convertStartAddressTypeToString ( eDataType, instanceValue );
            case ConfigurationPackage.STOP_BITS_TYPE_OBJECT:
                return convertStopBitsTypeObjectToString ( eDataType, instanceValue );
            case ConfigurationPackage.TYPE_TYPE_OBJECT:
                return convertTypeTypeObjectToString ( eDataType, instanceValue );
            default:
                throw new IllegalArgumentException ( "The datatype '" + eDataType.getName () + "' is not a valid classifier" ); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public DevicesType createDevicesType ()
    {
        final DevicesTypeImpl devicesType = new DevicesTypeImpl ();
        return devicesType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public DeviceType createDeviceType ()
    {
        final DeviceTypeImpl deviceType = new DeviceTypeImpl ();
        return deviceType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public DocumentRoot createDocumentRoot ()
    {
        final DocumentRootImpl documentRoot = new DocumentRootImpl ();
        return documentRoot;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ItemType createItemType ()
    {
        final ItemTypeImpl itemType = new ItemTypeImpl ();
        return itemType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ModbusSlave createModbusSlave ()
    {
        final ModbusSlaveImpl modbusSlave = new ModbusSlaveImpl ();
        return modbusSlave;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public RootType createRootType ()
    {
        final RootTypeImpl rootType = new RootTypeImpl ();
        return rootType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public ParityType createParityTypeFromString ( final EDataType eDataType, final String initialValue )
    {
        final ParityType result = ParityType.get ( initialValue );
        if ( result == null )
        {
            throw new IllegalArgumentException ( "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName () + "'" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertParityTypeToString ( final EDataType eDataType, final Object instanceValue )
    {
        return instanceValue == null ? null : instanceValue.toString ();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public ProtocolType createProtocolTypeFromString ( final EDataType eDataType, final String initialValue )
    {
        final ProtocolType result = ProtocolType.get ( initialValue );
        if ( result == null )
        {
            throw new IllegalArgumentException ( "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName () + "'" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertProtocolTypeToString ( final EDataType eDataType, final Object instanceValue )
    {
        return instanceValue == null ? null : instanceValue.toString ();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public StopBitsType createStopBitsTypeFromString ( final EDataType eDataType, final String initialValue )
    {
        final StopBitsType result = StopBitsType.get ( initialValue );
        if ( result == null )
        {
            throw new IllegalArgumentException ( "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName () + "'" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertStopBitsTypeToString ( final EDataType eDataType, final Object instanceValue )
    {
        return instanceValue == null ? null : instanceValue.toString ();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public TypeType createTypeTypeFromString ( final EDataType eDataType, final String initialValue )
    {
        final TypeType result = TypeType.get ( initialValue );
        if ( result == null )
        {
            throw new IllegalArgumentException ( "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName () + "'" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertTypeTypeToString ( final EDataType eDataType, final Object instanceValue )
    {
        return instanceValue == null ? null : instanceValue.toString ();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public Integer createDataBitsTypeFromString ( final EDataType eDataType, final String initialValue )
    {
        return (Integer)XMLTypeFactory.eINSTANCE.createFromString ( XMLTypePackage.Literals.INT, initialValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertDataBitsTypeToString ( final EDataType eDataType, final Object instanceValue )
    {
        return XMLTypeFactory.eINSTANCE.convertToString ( XMLTypePackage.Literals.INT, instanceValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public Integer createDataBitsTypeObjectFromString ( final EDataType eDataType, final String initialValue )
    {
        return createDataBitsTypeFromString ( ConfigurationPackage.Literals.DATA_BITS_TYPE, initialValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertDataBitsTypeObjectToString ( final EDataType eDataType, final Object instanceValue )
    {
        return convertDataBitsTypeToString ( ConfigurationPackage.Literals.DATA_BITS_TYPE, instanceValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public String createHostTypeFromString ( final EDataType eDataType, final String initialValue )
    {
        return (String)XMLTypeFactory.eINSTANCE.createFromString ( XMLTypePackage.Literals.STRING, initialValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertHostTypeToString ( final EDataType eDataType, final Object instanceValue )
    {
        return XMLTypeFactory.eINSTANCE.convertToString ( XMLTypePackage.Literals.STRING, instanceValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public String createIdTypeFromString ( final EDataType eDataType, final String initialValue )
    {
        return (String)XMLTypeFactory.eINSTANCE.createFromString ( XMLTypePackage.Literals.STRING, initialValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertIdTypeToString ( final EDataType eDataType, final Object instanceValue )
    {
        return XMLTypeFactory.eINSTANCE.convertToString ( XMLTypePackage.Literals.STRING, instanceValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public Integer createIdType1FromString ( final EDataType eDataType, final String initialValue )
    {
        return (Integer)XMLTypeFactory.eINSTANCE.createFromString ( XMLTypePackage.Literals.INT, initialValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertIdType1ToString ( final EDataType eDataType, final Object instanceValue )
    {
        return XMLTypeFactory.eINSTANCE.convertToString ( XMLTypePackage.Literals.INT, instanceValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public Integer createIdTypeObjectFromString ( final EDataType eDataType, final String initialValue )
    {
        return createIdType1FromString ( ConfigurationPackage.Literals.ID_TYPE1, initialValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertIdTypeObjectToString ( final EDataType eDataType, final Object instanceValue )
    {
        return convertIdType1ToString ( ConfigurationPackage.Literals.ID_TYPE1, instanceValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public ParityType createParityTypeObjectFromString ( final EDataType eDataType, final String initialValue )
    {
        return createParityTypeFromString ( ConfigurationPackage.Literals.PARITY_TYPE, initialValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertParityTypeObjectToString ( final EDataType eDataType, final Object instanceValue )
    {
        return convertParityTypeToString ( ConfigurationPackage.Literals.PARITY_TYPE, instanceValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public ProtocolType createProtocolTypeObjectFromString ( final EDataType eDataType, final String initialValue )
    {
        return createProtocolTypeFromString ( ConfigurationPackage.Literals.PROTOCOL_TYPE, initialValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertProtocolTypeObjectToString ( final EDataType eDataType, final Object instanceValue )
    {
        return convertProtocolTypeToString ( ConfigurationPackage.Literals.PROTOCOL_TYPE, instanceValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public String createStartAddressTypeFromString ( final EDataType eDataType, final String initialValue )
    {
        return (String)XMLTypeFactory.eINSTANCE.createFromString ( XMLTypePackage.Literals.STRING, initialValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertStartAddressTypeToString ( final EDataType eDataType, final Object instanceValue )
    {
        return XMLTypeFactory.eINSTANCE.convertToString ( XMLTypePackage.Literals.STRING, instanceValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public StopBitsType createStopBitsTypeObjectFromString ( final EDataType eDataType, final String initialValue )
    {
        return createStopBitsTypeFromString ( ConfigurationPackage.Literals.STOP_BITS_TYPE, initialValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertStopBitsTypeObjectToString ( final EDataType eDataType, final Object instanceValue )
    {
        return convertStopBitsTypeToString ( ConfigurationPackage.Literals.STOP_BITS_TYPE, instanceValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public TypeType createTypeTypeObjectFromString ( final EDataType eDataType, final String initialValue )
    {
        return createTypeTypeFromString ( ConfigurationPackage.Literals.TYPE_TYPE, initialValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertTypeTypeObjectToString ( final EDataType eDataType, final Object instanceValue )
    {
        return convertTypeTypeToString ( ConfigurationPackage.Literals.TYPE_TYPE, instanceValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ConfigurationPackage getConfigurationPackage ()
    {
        return (ConfigurationPackage)getEPackage ();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @deprecated
     * @generated
     */
    @Deprecated
    public static ConfigurationPackage getPackage ()
    {
        return ConfigurationPackage.eINSTANCE;
    }

} //ConfigurationFactoryImpl
