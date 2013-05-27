/**
 */
package org.openscada.da.snmp.configuration;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Connection Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.openscada.da.snmp.configuration.ConnectionType#getAddress <em>Address</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.ConnectionType#getCommunity <em>Community</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.ConnectionType#getName <em>Name</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.ConnectionType#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getConnectionType()
 * @model extendedMetaData="name='connection_._type' kind='empty'"
 * @generated
 */
public interface ConnectionType extends EObject
{
    /**
     * Returns the value of the '<em><b>Address</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Address</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Address</em>' attribute.
     * @see #setAddress(String)
     * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getConnectionType_Address()
     * @model dataType="org.openscada.da.snmp.configuration.Address" required="true"
     *        extendedMetaData="kind='attribute' name='address'"
     * @generated
     */
    String getAddress ();

    /**
     * Sets the value of the '{@link org.openscada.da.snmp.configuration.ConnectionType#getAddress <em>Address</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Address</em>' attribute.
     * @see #getAddress()
     * @generated
     */
    void setAddress ( String value );

    /**
     * Returns the value of the '<em><b>Community</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Community</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Community</em>' attribute.
     * @see #setCommunity(String)
     * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getConnectionType_Community()
     * @model dataType="org.eclipse.emf.ecore.xml.type.NMTOKEN" required="true"
     *        extendedMetaData="kind='attribute' name='community'"
     * @generated
     */
    String getCommunity ();

    /**
     * Sets the value of the '{@link org.openscada.da.snmp.configuration.ConnectionType#getCommunity <em>Community</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Community</em>' attribute.
     * @see #getCommunity()
     * @generated
     */
    void setCommunity ( String value );

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getConnectionType_Name()
     * @model dataType="org.eclipse.emf.ecore.xml.type.NMTOKEN" required="true"
     *        extendedMetaData="kind='attribute' name='name'"
     * @generated
     */
    String getName ();

    /**
     * Sets the value of the '{@link org.openscada.da.snmp.configuration.ConnectionType#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName ( String value );

    /**
     * Returns the value of the '<em><b>Version</b></em>' attribute.
     * The default value is <code>"2"</code>.
     * The literals are from the enumeration {@link org.openscada.da.snmp.configuration.SnmpVersion}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Version</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Version</em>' attribute.
     * @see org.openscada.da.snmp.configuration.SnmpVersion
     * @see #isSetVersion()
     * @see #unsetVersion()
     * @see #setVersion(SnmpVersion)
     * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getConnectionType_Version()
     * @model default="2" unsettable="true"
     *        extendedMetaData="kind='attribute' name='version'"
     * @generated
     */
    SnmpVersion getVersion ();

    /**
     * Sets the value of the '{@link org.openscada.da.snmp.configuration.ConnectionType#getVersion <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Version</em>' attribute.
     * @see org.openscada.da.snmp.configuration.SnmpVersion
     * @see #isSetVersion()
     * @see #unsetVersion()
     * @see #getVersion()
     * @generated
     */
    void setVersion ( SnmpVersion value );

    /**
     * Unsets the value of the '{@link org.openscada.da.snmp.configuration.ConnectionType#getVersion <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetVersion()
     * @see #getVersion()
     * @see #setVersion(SnmpVersion)
     * @generated
     */
    void unsetVersion ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.snmp.configuration.ConnectionType#getVersion <em>Version</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Version</em>' attribute is set.
     * @see #unsetVersion()
     * @see #getVersion()
     * @see #setVersion(SnmpVersion)
     * @generated
     */
    boolean isSetVersion ();

} // ConnectionType
