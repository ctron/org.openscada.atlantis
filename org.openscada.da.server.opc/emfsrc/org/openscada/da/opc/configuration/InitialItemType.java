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
package org.openscada.da.opc.configuration;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Initial Item Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.openscada.da.opc.configuration.InitialItemType#getAccessPath <em>Access Path</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.InitialItemType#getDescription <em>Description</em>}</li>
 *   <li>{@link org.openscada.da.opc.configuration.InitialItemType#getId <em>Id</em>}</li>
 * </ul>
 *
 * @see org.openscada.da.opc.configuration.ConfigurationPackage#getInitialItemType()
 * @model extendedMetaData="name='InitialItemType' kind='empty'"
 * @generated
 */
public interface InitialItemType extends EObject
{
    /**
     * Returns the value of the '<em><b>Access Path</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Access Path</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Access Path</em>' attribute.
     * @see #setAccessPath(String)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getInitialItemType_AccessPath()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='accessPath'"
     * @generated
     */
    String getAccessPath ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.InitialItemType#getAccessPath <em>Access Path</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Access Path</em>' attribute.
     * @see #getAccessPath()
     * @generated
     */
    void setAccessPath ( String value );

    /**
     * Returns the value of the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Description</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Description</em>' attribute.
     * @see #setDescription(String)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getInitialItemType_Description()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='description'"
     * @generated
     */
    String getDescription ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.InitialItemType#getDescription <em>Description</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Description</em>' attribute.
     * @see #getDescription()
     * @generated
     */
    void setDescription ( String value );

    /**
     * Returns the value of the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Id</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Id</em>' attribute.
     * @see #setId(String)
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getInitialItemType_Id()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     *        extendedMetaData="kind='attribute' name='id'"
     * @generated
     */
    String getId ();

    /**
     * Sets the value of the '{@link org.openscada.da.opc.configuration.InitialItemType#getId <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Id</em>' attribute.
     * @see #getId()
     * @generated
     */
    void setId ( String value );

} // InitialItemType
