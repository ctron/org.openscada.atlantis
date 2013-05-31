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
package org.openscada.da.exec.configuration;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Field Extractor Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.openscada.da.exec.configuration.FieldExtractorType#getField <em>Field</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.openscada.da.exec.configuration.ConfigurationPackage#getFieldExtractorType()
 * @model abstract="true"
 *        extendedMetaData="name='FieldExtractorType' kind='elementOnly'"
 * @generated
 */
public interface FieldExtractorType extends ExtractorType
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Field</b></em>' containment reference list.
     * The list contents are of type {@link org.openscada.da.exec.configuration.FieldType}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * 
     *     							Each regular expression group must be
     *     							named. The name will be the data item
     *     							name. If a regular expression group is
     *     							not named it will not be extracted.
     *     						
     * <!-- end-model-doc -->
     * @return the value of the '<em>Field</em>' containment reference list.
     * @see org.openscada.da.exec.configuration.ConfigurationPackage#getFieldExtractorType_Field()
     * @model containment="true"
     *        extendedMetaData="kind='element' name='field' namespace='##targetNamespace'"
     * @generated
     */
    EList<FieldType> getField ();

} // FieldExtractorType
