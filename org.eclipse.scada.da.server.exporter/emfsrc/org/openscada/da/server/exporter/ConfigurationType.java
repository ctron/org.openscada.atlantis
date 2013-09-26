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
package org.openscada.da.server.exporter;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Configuration Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.openscada.da.server.exporter.ConfigurationType#getHive <em>Hive</em>}</li>
 *   <li>{@link org.openscada.da.server.exporter.ConfigurationType#getAnnouncer <em>Announcer</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.openscada.da.server.exporter.ExporterPackage#getConfigurationType()
 * @model extendedMetaData="name='ConfigurationType' kind='elementOnly'"
 * @generated
 */
public interface ConfigurationType extends EObject
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Hive</b></em>' containment reference list.
     * The list contents are of type {@link org.openscada.da.server.exporter.HiveType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Hive</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Hive</em>' containment reference list.
     * @see org.openscada.da.server.exporter.ExporterPackage#getConfigurationType_Hive()
     * @model containment="true"
     *        extendedMetaData="kind='element' name='hive' namespace='##targetNamespace'"
     * @generated
     */
    EList<HiveType> getHive ();

    /**
     * Returns the value of the '<em><b>Announcer</b></em>' containment reference list.
     * The list contents are of type {@link org.openscada.da.server.exporter.AnnouncerType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Announcer</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Announcer</em>' containment reference list.
     * @see org.openscada.da.server.exporter.ExporterPackage#getConfigurationType_Announcer()
     * @model containment="true"
     *        extendedMetaData="kind='element' name='announcer' namespace='##targetNamespace'"
     * @generated
     */
    EList<AnnouncerType> getAnnouncer ();

} // ConfigurationType
