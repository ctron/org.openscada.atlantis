/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.server.exporter.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.openscada.da.server.exporter.AnnouncerType;
import org.openscada.da.server.exporter.ConfigurationType;
import org.openscada.da.server.exporter.DocumentRoot;
import org.openscada.da.server.exporter.ExportType;
import org.openscada.da.server.exporter.ExporterFactory;
import org.openscada.da.server.exporter.ExporterPackage;
import org.openscada.da.server.exporter.HiveConfigurationType;
import org.openscada.da.server.exporter.HiveType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ExporterFactoryImpl extends EFactoryImpl implements ExporterFactory
{
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ExporterFactory init ()
    {
        try
        {
            ExporterFactory theExporterFactory = (ExporterFactory)EPackage.Registry.INSTANCE.getEFactory ( ExporterPackage.eNS_URI );
            if ( theExporterFactory != null )
            {
                return theExporterFactory;
            }
        }
        catch ( Exception exception )
        {
            EcorePlugin.INSTANCE.log ( exception );
        }
        return new ExporterFactoryImpl ();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ExporterFactoryImpl ()
    {
        super ();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create ( EClass eClass )
    {
        switch ( eClass.getClassifierID () )
        {
            case ExporterPackage.ANNOUNCER_TYPE:
                return createAnnouncerType ();
            case ExporterPackage.CONFIGURATION_TYPE:
                return createConfigurationType ();
            case ExporterPackage.DOCUMENT_ROOT:
                return createDocumentRoot ();
            case ExporterPackage.EXPORT_TYPE:
                return createExportType ();
            case ExporterPackage.HIVE_CONFIGURATION_TYPE:
                return createHiveConfigurationType ();
            case ExporterPackage.HIVE_TYPE:
                return createHiveType ();
            default:
                throw new IllegalArgumentException ( "The class '" + eClass.getName () + "' is not a valid classifier" ); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public AnnouncerType createAnnouncerType ()
    {
        AnnouncerTypeImpl announcerType = new AnnouncerTypeImpl ();
        return announcerType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ConfigurationType createConfigurationType ()
    {
        ConfigurationTypeImpl configurationType = new ConfigurationTypeImpl ();
        return configurationType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public DocumentRoot createDocumentRoot ()
    {
        DocumentRootImpl documentRoot = new DocumentRootImpl ();
        return documentRoot;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ExportType createExportType ()
    {
        ExportTypeImpl exportType = new ExportTypeImpl ();
        return exportType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public HiveConfigurationType createHiveConfigurationType ()
    {
        HiveConfigurationTypeImpl hiveConfigurationType = new HiveConfigurationTypeImpl ();
        return hiveConfigurationType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public HiveType createHiveType ()
    {
        HiveTypeImpl hiveType = new HiveTypeImpl ();
        return hiveType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ExporterPackage getExporterPackage ()
    {
        return (ExporterPackage)getEPackage ();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static ExporterPackage getPackage ()
    {
        return ExporterPackage.eINSTANCE;
    }

} //ExporterFactoryImpl
