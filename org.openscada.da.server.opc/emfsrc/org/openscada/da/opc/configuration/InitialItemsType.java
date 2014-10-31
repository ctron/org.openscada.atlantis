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

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Initial Items Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.openscada.da.opc.configuration.InitialItemsType#getItem <em>Item</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.openscada.da.opc.configuration.ConfigurationPackage#getInitialItemsType()
 * @model extendedMetaData="name='InitialItemsType' kind='elementOnly'"
 * @generated
 */
public interface InitialItemsType extends EObject
{
    /**
     * Returns the value of the '<em><b>Item</b></em>' containment reference list.
     * The list contents are of type {@link org.openscada.da.opc.configuration.InitialItemType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Item</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Item</em>' containment reference list.
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getInitialItemsType_Item()
     * @model containment="true"
     *        extendedMetaData="kind='element' name='item' namespace='##targetNamespace'"
     * @generated
     */
    EList<InitialItemType> getItem ();

} // InitialItemsType
