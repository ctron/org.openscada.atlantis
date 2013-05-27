/**
 */
package org.openscada.da.opc.configuration;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Root Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.openscada.da.opc.configuration.RootType#getConnections <em>Connections</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.openscada.da.opc.configuration.ConfigurationPackage#getRootType()
 * @model extendedMetaData="name='RootType' kind='elementOnly'"
 * @generated
 */
public interface RootType extends EObject
{
    /**
     * Returns the value of the '<em><b>Connections</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Connections</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Connections</em>' containment reference.
     * @see #setConnections(ConnectionsType)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getRootType_Connections()
     * @model containment="true"
     *        extendedMetaData="kind='element' name='connections' namespace='##targetNamespace'"
     * @generated
     */
    ConnectionsType getConnections ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.RootType#getConnections <em>Connections</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Connections</em>' containment reference.
     * @see #getConnections()
     * @generated
     */
    void setConnections ( ConnectionsType value );

} // RootType
