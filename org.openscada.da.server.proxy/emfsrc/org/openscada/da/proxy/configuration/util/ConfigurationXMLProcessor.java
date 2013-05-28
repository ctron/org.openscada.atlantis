/**
 */
package org.openscada.da.proxy.configuration.util;

import java.util.Map;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.emf.ecore.xmi.util.XMLProcessor;

import org.openscada.da.proxy.configuration.ConfigurationPackage;

/**
 * This class contains helper methods to serialize and deserialize XML documents
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class ConfigurationXMLProcessor extends XMLProcessor
{

    /**
     * Public constructor to instantiate the helper.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ConfigurationXMLProcessor ()
    {
        super ( ( EPackage.Registry.INSTANCE ) );
        ConfigurationPackage.eINSTANCE.eClass ();
    }

    /**
     * Register for "*" and "xml" file extensions the ConfigurationResourceFactoryImpl factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected Map<String, Resource.Factory> getRegistrations ()
    {
        if ( registrations == null )
        {
            super.getRegistrations ();
            registrations.put ( XML_EXTENSION, new ConfigurationResourceFactoryImpl () );
            registrations.put ( STAR_EXTENSION, new ConfigurationResourceFactoryImpl () );
        }
        return registrations;
    }

} //ConfigurationXMLProcessor
