/**
 */
package org.openscada.da.snmp.configuration;

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
 *   <li>{@link org.openscada.da.snmp.configuration.ConfigurationType#getMibs <em>Mibs</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.ConfigurationType#getConnection <em>Connection</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getConfigurationType()
 * @model extendedMetaData="name='configuration_._type' kind='elementOnly'"
 * @generated
 */
public interface ConfigurationType extends EObject
{
    /**
     * Returns the value of the '<em><b>Mibs</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Mibs</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Mibs</em>' containment reference.
     * @see #setMibs(MibsType)
     * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getConfigurationType_Mibs()
     * @model containment="true" required="true"
     *        extendedMetaData="kind='element' name='mibs' namespace='##targetNamespace'"
     * @generated
     */
    MibsType getMibs ();

    /**
     * Sets the value of the '{@link org.openscada.da.snmp.configuration.ConfigurationType#getMibs <em>Mibs</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Mibs</em>' containment reference.
     * @see #getMibs()
     * @generated
     */
    void setMibs ( MibsType value );

    /**
     * Returns the value of the '<em><b>Connection</b></em>' containment reference list.
     * The list contents are of type {@link org.openscada.da.snmp.configuration.ConnectionType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Connection</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Connection</em>' containment reference list.
     * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getConfigurationType_Connection()
     * @model containment="true"
     *        extendedMetaData="kind='element' name='connection' namespace='##targetNamespace'"
     * @generated
     */
    EList<ConnectionType> getConnection ();

} // ConfigurationType
