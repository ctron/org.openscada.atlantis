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

package org.eclipse.scada.da.modbus.configuration.util;

import java.util.Map;

import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.emf.ecore.xml.type.util.XMLTypeUtil;
import org.eclipse.emf.ecore.xml.type.util.XMLTypeValidator;
import org.eclipse.scada.da.modbus.configuration.ConfigurationPackage;
import org.eclipse.scada.da.modbus.configuration.DeviceType;
import org.eclipse.scada.da.modbus.configuration.DevicesType;
import org.eclipse.scada.da.modbus.configuration.DocumentRoot;
import org.eclipse.scada.da.modbus.configuration.ItemType;
import org.eclipse.scada.da.modbus.configuration.ModbusSlave;
import org.eclipse.scada.da.modbus.configuration.ProtocolType;
import org.eclipse.scada.da.modbus.configuration.RootType;
import org.eclipse.scada.da.modbus.configuration.TypeType;

/**
 * <!-- begin-user-doc -->
 * The <b>Validator</b> for the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage
 * @generated
 */
public class ConfigurationValidator extends EObjectValidator
{
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final ConfigurationValidator INSTANCE = new ConfigurationValidator ();

    /**
     * A constant for the {@link org.eclipse.emf.common.util.Diagnostic#getSource() source} of diagnostic {@link org.eclipse.emf.common.util.Diagnostic#getCode() codes} from this package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.common.util.Diagnostic#getSource()
     * @see org.eclipse.emf.common.util.Diagnostic#getCode()
     * @generated
     */
    public static final String DIAGNOSTIC_SOURCE = "org.eclipse.scada.da.modbus.configuration"; //$NON-NLS-1$

    /**
     * A constant with a fixed name that can be used as the base value for additional hand written constants.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final int GENERATED_DIAGNOSTIC_CODE_COUNT = 0;

    /**
     * A constant with a fixed name that can be used as the base value for additional hand written constants in a derived class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static final int DIAGNOSTIC_CODE_COUNT = GENERATED_DIAGNOSTIC_CODE_COUNT;

    /**
     * The cached base package validator.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected XMLTypeValidator xmlTypeValidator;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ConfigurationValidator ()
    {
        super ();
        xmlTypeValidator = XMLTypeValidator.INSTANCE;
    }

    /**
     * Returns the package of this validator switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EPackage getEPackage ()
    {
        return ConfigurationPackage.eINSTANCE;
    }

    /**
     * Calls <code>validateXXX</code> for the corresponding classifier of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected boolean validate ( int classifierID, Object value, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        switch ( classifierID )
        {
            case ConfigurationPackage.DEVICES_TYPE:
                return validateDevicesType ( (DevicesType)value, diagnostics, context );
            case ConfigurationPackage.DEVICE_TYPE:
                return validateDeviceType ( (DeviceType)value, diagnostics, context );
            case ConfigurationPackage.DOCUMENT_ROOT:
                return validateDocumentRoot ( (DocumentRoot)value, diagnostics, context );
            case ConfigurationPackage.ITEM_TYPE:
                return validateItemType ( (ItemType)value, diagnostics, context );
            case ConfigurationPackage.MODBUS_SLAVE:
                return validateModbusSlave ( (ModbusSlave)value, diagnostics, context );
            case ConfigurationPackage.ROOT_TYPE:
                return validateRootType ( (RootType)value, diagnostics, context );
            case ConfigurationPackage.PROTOCOL_TYPE:
                return validateProtocolType ( (ProtocolType)value, diagnostics, context );
            case ConfigurationPackage.TYPE_TYPE:
                return validateTypeType ( (TypeType)value, diagnostics, context );
            case ConfigurationPackage.HOST_TYPE:
                return validateHostType ( (String)value, diagnostics, context );
            case ConfigurationPackage.ID_TYPE:
                return validateIdType ( (String)value, diagnostics, context );
            case ConfigurationPackage.ID_TYPE1:
                return validateIdType1 ( (Integer)value, diagnostics, context );
            case ConfigurationPackage.ID_TYPE_OBJECT:
                return validateIdTypeObject ( (Integer)value, diagnostics, context );
            case ConfigurationPackage.PROTOCOL_TYPE_OBJECT:
                return validateProtocolTypeObject ( (ProtocolType)value, diagnostics, context );
            case ConfigurationPackage.START_ADDRESS_TYPE:
                return validateStartAddressType ( (String)value, diagnostics, context );
            case ConfigurationPackage.TYPE_TYPE_OBJECT:
                return validateTypeTypeObject ( (TypeType)value, diagnostics, context );
            default:
                return true;
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateDevicesType ( DevicesType devicesType, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        return validate_EveryDefaultConstraint ( devicesType, diagnostics, context );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateDeviceType ( DeviceType deviceType, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        return validate_EveryDefaultConstraint ( deviceType, diagnostics, context );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateDocumentRoot ( DocumentRoot documentRoot, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        return validate_EveryDefaultConstraint ( documentRoot, diagnostics, context );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateItemType ( ItemType itemType, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        return validate_EveryDefaultConstraint ( itemType, diagnostics, context );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateModbusSlave ( ModbusSlave modbusSlave, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        return validate_EveryDefaultConstraint ( modbusSlave, diagnostics, context );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateRootType ( RootType rootType, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        return validate_EveryDefaultConstraint ( rootType, diagnostics, context );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateProtocolType ( ProtocolType protocolType, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        return true;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateTypeType ( TypeType typeType, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        return true;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateHostType ( String hostType, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        boolean result = validateHostType_Pattern ( hostType, diagnostics, context );
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @see #validateHostType_Pattern
     */
    public static final PatternMatcher[][] HOST_TYPE__PATTERN__VALUES = new PatternMatcher[][] { new PatternMatcher[] { XMLTypeUtil.createPatternMatcher ( "([0-9a-zA-Z]+)(\\.[0-9a-zA-Z]+)*" ) } };

    /**
     * Validates the Pattern constraint of '<em>Host Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateHostType_Pattern ( String hostType, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        return validatePattern ( ConfigurationPackage.Literals.HOST_TYPE, hostType, HOST_TYPE__PATTERN__VALUES, diagnostics, context );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateIdType ( String idType, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        boolean result = validateIdType_Pattern ( idType, diagnostics, context );
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @see #validateIdType_Pattern
     */
    public static final PatternMatcher[][] ID_TYPE__PATTERN__VALUES = new PatternMatcher[][] { new PatternMatcher[] { XMLTypeUtil.createPatternMatcher ( "[a-zA-Z0-9]+" ) } };

    /**
     * Validates the Pattern constraint of '<em>Id Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateIdType_Pattern ( String idType, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        return validatePattern ( ConfigurationPackage.Literals.ID_TYPE, idType, ID_TYPE__PATTERN__VALUES, diagnostics, context );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateIdType1 ( int idType1, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        boolean result = validateIdType1_Min ( idType1, diagnostics, context );
        if ( result || diagnostics != null )
            result &= validateIdType1_Max ( idType1, diagnostics, context );
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @see #validateIdType1_Min
     */
    public static final int ID_TYPE1__MIN__VALUE = 1;

    /**
     * Validates the Min constraint of '<em>Id Type1</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateIdType1_Min ( int idType1, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        boolean result = idType1 >= ID_TYPE1__MIN__VALUE;
        if ( !result && diagnostics != null )
            reportMinViolation ( ConfigurationPackage.Literals.ID_TYPE1, idType1, ID_TYPE1__MIN__VALUE, true, diagnostics, context );
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @see #validateIdType1_Max
     */
    public static final int ID_TYPE1__MAX__VALUE = 255;

    /**
     * Validates the Max constraint of '<em>Id Type1</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateIdType1_Max ( int idType1, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        boolean result = idType1 <= ID_TYPE1__MAX__VALUE;
        if ( !result && diagnostics != null )
            reportMaxViolation ( ConfigurationPackage.Literals.ID_TYPE1, idType1, ID_TYPE1__MAX__VALUE, true, diagnostics, context );
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateIdTypeObject ( Integer idTypeObject, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        boolean result = validateIdType1_Min ( idTypeObject, diagnostics, context );
        if ( result || diagnostics != null )
            result &= validateIdType1_Max ( idTypeObject, diagnostics, context );
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateProtocolTypeObject ( ProtocolType protocolTypeObject, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        return true;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateStartAddressType ( String startAddressType, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        boolean result = validateStartAddressType_Pattern ( startAddressType, diagnostics, context );
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @see #validateStartAddressType_Pattern
     */
    public static final PatternMatcher[][] START_ADDRESS_TYPE__PATTERN__VALUES = new PatternMatcher[][] { new PatternMatcher[] { XMLTypeUtil.createPatternMatcher ( "(0x[0-9a-fA-F]{4}|[0-9]{1,5})" ) } };

    /**
     * Validates the Pattern constraint of '<em>Start Address Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateStartAddressType_Pattern ( String startAddressType, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        return validatePattern ( ConfigurationPackage.Literals.START_ADDRESS_TYPE, startAddressType, START_ADDRESS_TYPE__PATTERN__VALUES, diagnostics, context );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateTypeTypeObject ( TypeType typeTypeObject, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        return true;
    }

    /**
     * Returns the resource locator that will be used to fetch messages for this validator's diagnostics.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator ()
    {
        // TODO
        // Specialize this to return a resource locator for messages specific to this validator.
        // Ensure that you remove @generated or mark it @generated NOT
        return super.getResourceLocator ();
    }

} //ConfigurationValidator
