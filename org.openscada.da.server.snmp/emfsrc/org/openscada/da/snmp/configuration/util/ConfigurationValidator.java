/**
 */
package org.openscada.da.snmp.configuration.util;

import java.util.Map;

import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.EObjectValidator;

import org.eclipse.emf.ecore.xml.type.util.XMLTypeUtil;
import org.eclipse.emf.ecore.xml.type.util.XMLTypeValidator;

import org.openscada.da.snmp.configuration.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Validator</b> for the model.
 * <!-- end-user-doc -->
 * @see org.openscada.da.snmp.configuration.ConfigurationPackage
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
    public static final String DIAGNOSTIC_SOURCE = "org.openscada.da.snmp.configuration"; //$NON-NLS-1$

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
            case ConfigurationPackage.CONFIGURATION_TYPE:
                return validateConfigurationType ( (ConfigurationType)value, diagnostics, context );
            case ConfigurationPackage.CONNECTION_TYPE:
                return validateConnectionType ( (ConnectionType)value, diagnostics, context );
            case ConfigurationPackage.DOCUMENT_ROOT:
                return validateDocumentRoot ( (DocumentRoot)value, diagnostics, context );
            case ConfigurationPackage.MIBS_TYPE:
                return validateMibsType ( (MibsType)value, diagnostics, context );
            case ConfigurationPackage.SNMP_VERSION:
                return validateSnmpVersion ( (SnmpVersion)value, diagnostics, context );
            case ConfigurationPackage.ADDRESS:
                return validateAddress ( (String)value, diagnostics, context );
            case ConfigurationPackage.SNMP_VERSION_OBJECT:
                return validateSnmpVersionObject ( (SnmpVersion)value, diagnostics, context );
            default:
                return true;
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateConfigurationType ( ConfigurationType configurationType, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        return validate_EveryDefaultConstraint ( configurationType, diagnostics, context );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateConnectionType ( ConnectionType connectionType, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        return validate_EveryDefaultConstraint ( connectionType, diagnostics, context );
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
    public boolean validateMibsType ( MibsType mibsType, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        return validate_EveryDefaultConstraint ( mibsType, diagnostics, context );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateSnmpVersion ( SnmpVersion snmpVersion, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        return true;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateAddress ( String address, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        boolean result = validateAddress_Pattern ( address, diagnostics, context );
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @see #validateAddress_Pattern
     */
    public static final PatternMatcher[][] ADDRESS__PATTERN__VALUES = new PatternMatcher[][] { new PatternMatcher[] { XMLTypeUtil.createPatternMatcher ( "(udp|tcp):([a-zA-Z0-9]+\\.?)+/[0-9]{1,5}" ) } };

    /**
     * Validates the Pattern constraint of '<em>Address</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateAddress_Pattern ( String address, DiagnosticChain diagnostics, Map<Object, Object> context )
    {
        return validatePattern ( ConfigurationPackage.Literals.ADDRESS, address, ADDRESS__PATTERN__VALUES, diagnostics, context );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean validateSnmpVersionObject ( SnmpVersion snmpVersionObject, DiagnosticChain diagnostics, Map<Object, Object> context )
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
