/**
 */
package org.openscada.da.snmp.configuration;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mibs Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.openscada.da.snmp.configuration.MibsType#getGroup <em>Group</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.MibsType#getStaticMibName <em>Static Mib Name</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.MibsType#getMibDir <em>Mib Dir</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.MibsType#getRecursiveMibDir <em>Recursive Mib Dir</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getMibsType()
 * @model extendedMetaData="name='mibsType' kind='elementOnly'"
 * @generated
 */
public interface MibsType extends EObject
{
    /**
     * Returns the value of the '<em><b>Group</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Group</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Group</em>' attribute list.
     * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getMibsType_Group()
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     *        extendedMetaData="kind='group' name='group:0'"
     * @generated
     */
    FeatureMap getGroup ();

    /**
     * Returns the value of the '<em><b>Static Mib Name</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Static Mib Name</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Static Mib Name</em>' attribute list.
     * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getMibsType_StaticMibName()
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='staticMibName' namespace='##targetNamespace' group='#group:0'"
     * @generated
     */
    EList<String> getStaticMibName ();

    /**
     * Returns the value of the '<em><b>Mib Dir</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Mib Dir</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Mib Dir</em>' attribute list.
     * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getMibsType_MibDir()
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='mibDir' namespace='##targetNamespace' group='#group:0'"
     * @generated
     */
    EList<String> getMibDir ();

    /**
     * Returns the value of the '<em><b>Recursive Mib Dir</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Recursive Mib Dir</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Recursive Mib Dir</em>' attribute list.
     * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getMibsType_RecursiveMibDir()
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='recursiveMibDir' namespace='##targetNamespace' group='#group:0'"
     * @generated
     */
    EList<String> getRecursiveMibDir ();

} // MibsType
