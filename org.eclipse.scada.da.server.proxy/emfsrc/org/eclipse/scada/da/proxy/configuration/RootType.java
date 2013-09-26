/**
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 * 
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 * 
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */
package org.eclipse.scada.da.proxy.configuration;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Root Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.scada.da.proxy.configuration.RootType#getProxy <em>Proxy</em>}</li>
 *   <li>{@link org.eclipse.scada.da.proxy.configuration.RootType#getSeparator <em>Separator</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.scada.da.proxy.configuration.ConfigurationPackage#getRootType()
 * @model extendedMetaData="name='RootType' kind='elementOnly'"
 * @generated
 */
public interface RootType extends EObject
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Proxy</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.scada.da.proxy.configuration.ProxyType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Proxy</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Proxy</em>' containment reference list.
     * @see org.eclipse.scada.da.proxy.configuration.ConfigurationPackage#getRootType_Proxy()
     * @model containment="true" required="true"
     *        extendedMetaData="kind='element' name='proxy' namespace='##targetNamespace'"
     * @generated
     */
    EList<ProxyType> getProxy ();

    /**
     * Returns the value of the '<em><b>Separator</b></em>' attribute.
     * The default value is <code>"."</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Separator</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Separator</em>' attribute.
     * @see #isSetSeparator()
     * @see #unsetSeparator()
     * @see #setSeparator(String)
     * @see org.eclipse.scada.da.proxy.configuration.ConfigurationPackage#getRootType_Separator()
     * @model default="." unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='separator'"
     * @generated
     */
    String getSeparator ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.proxy.configuration.RootType#getSeparator <em>Separator</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Separator</em>' attribute.
     * @see #isSetSeparator()
     * @see #unsetSeparator()
     * @see #getSeparator()
     * @generated
     */
    void setSeparator ( String value );

    /**
     * Unsets the value of the '{@link org.eclipse.scada.da.proxy.configuration.RootType#getSeparator <em>Separator</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetSeparator()
     * @see #getSeparator()
     * @see #setSeparator(String)
     * @generated
     */
    void unsetSeparator ();

    /**
     * Returns whether the value of the '{@link org.eclipse.scada.da.proxy.configuration.RootType#getSeparator <em>Separator</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Separator</em>' attribute is set.
     * @see #unsetSeparator()
     * @see #getSeparator()
     * @see #setSeparator(String)
     * @generated
     */
    boolean isSetSeparator ();

} // RootType
