/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.client.viewer.configurator.xml;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.configurator.Configurator;
import org.openscada.da.client.viewer.model.AlreadyConnectedException;
import org.openscada.da.client.viewer.model.Connector;
import org.openscada.da.client.viewer.model.ConnectorFactory;
import org.openscada.da.client.viewer.model.Container;
import org.openscada.da.client.viewer.model.ContainerFactory;
import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.InputDefinition;
import org.openscada.da.client.viewer.model.ObjectFactory;
import org.openscada.da.client.viewer.model.OutputDefinition;
import org.openscada.da.client.viewer.model.Type;
import org.openscada.da.client.viewer.model.View;
import org.openscada.da.client.viewer.model.impl.ConstantObject;
import org.openscada.da.client.viewer.model.impl.ConstantOutput;
import org.openscada.da.client.viewer.model.impl.ContainerCreator;
import org.openscada.da.client.viewer.model.impl.DynamicObjectCreator;
import org.openscada.da.client.viewer.model.impl.Helper;
import org.openscada.da.client.viewer.model.types.Color;
import org.openscada.da.viewer.ConnectorFactoryType;
import org.openscada.da.viewer.ConnectorType;
import org.openscada.da.viewer.ConstantType;
import org.openscada.da.viewer.ContainerFactoryType;
import org.openscada.da.viewer.ContainerType;
import org.openscada.da.viewer.FactoriesType;
import org.openscada.da.viewer.InputExportType;
import org.openscada.da.viewer.InputType;
import org.openscada.da.viewer.ObjectFactoryType;
import org.openscada.da.viewer.ObjectType;
import org.openscada.da.viewer.OutputExportType;
import org.openscada.da.viewer.OutputType;
import org.openscada.da.viewer.PropertyType;
import org.openscada.da.viewer.RootDocument;
import org.openscada.da.viewer.TemplateObjectType;
import org.openscada.da.viewer.ViewType;
import org.openscada.da.viewer.ViewsType;
import org.w3c.dom.Node;

public class XMLConfigurator implements Configurator
{
    private static Logger _log = Logger.getLogger ( XMLConfigurator.class );

    private RootDocument _document = null;

    public XMLConfigurator ( final RootDocument document )
    {
        super ();
        this._document = document;
    }

    public XMLConfigurator ( final InputStream stream ) throws XmlException, IOException
    {
        this ( RootDocument.Factory.parse ( stream ) );
    }

    public View configure ( final String viewId ) throws ConfigurationError
    {
        final XMLConfigurationContext ctx = new XMLConfigurationContext ();
        ctx.setDocument ( this._document );

        configureFactories ( ctx, this._document.getRoot ().getFactories () );
        return configureView ( ctx, this._document.getRoot ().getViews (), viewId );
    }

    public static void configureFactories ( final XMLConfigurationContext ctx, final FactoriesType factories ) throws ConfigurationError
    {
        for ( final ContainerFactoryType factory : factories.getContainerFactoryList () )
        {
            createContainerFactory ( ctx, factory );
        }
        for ( final ConnectorFactoryType factory : factories.getConnectorFactoryList () )
        {
            createConnectorFactory ( ctx, factory );
        }
        for ( final ObjectFactoryType factory : factories.getObjectFactoryList () )
        {
            createObjectFactory ( ctx, factory );
        }
    }

    private static void createContainerFactory ( final XMLConfigurationContext ctx, final ContainerFactoryType factory ) throws ConfigurationError
    {
        try
        {
            final Class<?> factoryClass = Class.forName ( factory.getClass1 () );

            final ContainerFactory containerFactory = ContainerCreator.findFactory ( factoryClass );
            if ( containerFactory == null )
            {
                throw new ConfigurationError ( String.format ( "Unable to create new container factory." ) );
            }

            checkCallConfigurationHook ( ctx, containerFactory, factory.getDomNode () );

            ctx.getContainerFactories ().put ( factory.getId (), containerFactory );

        }
        catch ( final ClassNotFoundException e )
        {
            throw new ConfigurationError ( "Unable to load class", e );
        }
    }

    public static void createConnectorFactory ( final XMLConfigurationContext ctx, final ConnectorFactoryType factory ) throws ConfigurationError
    {
        try
        {
            final Class<?> factoryClass = Class.forName ( factory.getClass1 () );

            final Object factoryObject = factoryClass.newInstance ();

            if ( ! ( factoryObject instanceof ConnectorFactory ) )
            {
                throw new ConfigurationError ( "Class does not implement ConnectorFactory interface" );
            }

            final ConnectorFactory connectorFactory = (ConnectorFactory)factoryObject;
            checkCallConfigurationHook ( ctx, connectorFactory, factory.getDomNode () );

            ctx.getConnectorFactories ().put ( factory.getId (), connectorFactory );

        }
        catch ( final ClassNotFoundException e )
        {
            throw new ConfigurationError ( "Unable to load class", e );
        }
        catch ( final InstantiationException e )
        {
            throw new ConfigurationError ( "Unable to instatiate connector factory class", e );
        }
        catch ( final IllegalAccessException e )
        {
            throw new ConfigurationError ( "No access to instatiate connector factory class", e );
        }
    }

    public static void createObjectFactory ( final XMLConfigurationContext ctx, final ObjectFactoryType factory ) throws ConfigurationError
    {
        try
        {
            final Class<?> objectClass = Class.forName ( factory.getClass1 () );

            final ObjectFactory objectFactory = DynamicObjectCreator.findFactory ( objectClass );
            checkCallConfigurationHook ( ctx, objectFactory, factory.getDomNode () );

            ctx.getObjectFactories ().put ( factory.getId (), objectFactory );
            _log.debug ( String.format ( "Created object factory: %s", factory.getId () ) );

        }
        catch ( final ClassNotFoundException e )
        {
            throw new ConfigurationError ( "Unable to load class", e );
        }
    }

    public static View configureView ( final XMLConfigurationContext ctx, final ViewsType views, final String viewId ) throws ConfigurationError
    {
        for ( final ViewType view : views.getViewList () )
        {
            if ( view.getId ().equals ( viewId ) )
            {
                final Container container = createContainer ( new XMLContainerContext ( ctx ), view.getId (), view );
                if ( container instanceof View )
                {
                    return (View)container;
                }
                return null;
            }
        }
        return null;
    }

    public static Container createContainer ( final XMLContainerContext ctx, final String id, final ContainerType container ) throws ConfigurationError
    {
        Container containerObject = null;

        final ContainerFactory containerFactory = ctx.getConfigurationContext ().getContainerFactories ().get ( container.getType () );
        if ( containerFactory == null )
        {
            throw new ConfigurationError ( String.format ( "Unable to find container factory %s", container.getType () ) );
        }

        containerObject = containerFactory.create ( id );

        for ( final PropertyType property : container.getPropertyList () )
        {
            setObjectProperty ( containerObject, property.getName ().toString (), property.getStringValue () );
        }

        for ( final ConstantType constant : container.getObjects ().getConstantList () )
        {
            createConstant ( ctx, constant, containerObject );
        }

        for ( final ObjectType object : container.getObjects ().getObjectList () )
        {
            createObject ( ctx, object, containerObject );
        }

        for ( final TemplateObjectType template : container.getObjects ().getTemplateObjectList () )
        {
            createTemplateObject ( ctx, template, containerObject );
        }

        for ( final ContainerType containerType : container.getObjects ().getContainerList () )
        {
            createContainer ( ctx, containerType, containerObject );
        }

        if ( container.getConnectors () != null )
        {
            for ( final ConnectorType connector : container.getConnectors ().getConnectorList () )
            {
                createConnector ( ctx, connector, containerObject );
            }
        }

        if ( container.getInputs () != null )
        {
            for ( final InputExportType input : container.getInputs ().getInputExportList () )
            {
                containerObject.addInputExport ( new Container.Export ( input.getObject (), input.getName (), input.getExportName () ) );
            }
        }

        if ( container.getOutputs () != null )
        {
            for ( final OutputExportType output : container.getOutputs ().getOutputExportList () )
            {
                containerObject.addOutputExport ( new Container.Export ( output.getObject (), output.getName (), output.getExportName () ) );
            }
        }

        return containerObject;
    }

    private static void createContainer ( final XMLContainerContext ctx, final ContainerType template, final Container parentContainer ) throws ConfigurationError
    {
        final XMLContainerContext cctx = new XMLContainerContext ( ctx.getConfigurationContext () );
        final Container container = createContainer ( cctx, template.getId (), template );
        parentContainer.add ( container );
        ctx.getObjects ().put ( template.getId (), container );
    }

    public static void createTemplateObject ( final XMLContainerContext ctx, final TemplateObjectType template, final Container viewObject ) throws ConfigurationError
    {
        final ObjectFactory factory = ctx.getConfigurationContext ().getObjectFactories ().get ( template.getTemplate () );

        if ( factory == null )
        {
            throw new ConfigurationError ( String.format ( "Unable to find template object factory %s", template.getTemplate () ) );
        }

        final DynamicObject dynamicObject = factory.create ( template.getId () );

        if ( dynamicObject == null )
        {
            throw new ConfigurationError ( String.format ( "Unable to create template object %s", template.getTemplate () ) );
        }

        for ( final PropertyType property : template.getPropertyList () )
        {
            setObjectProperty ( dynamicObject, property.getName ().toString (), property.getStringValue () );
        }

        viewObject.add ( dynamicObject );
        ctx.getObjects ().put ( template.getId (), dynamicObject );
        _log.debug ( String.format ( "Created object (template) %s", template.getId () ) );
    }

    public static void createConstant ( final XMLContainerContext ctx, final ConstantType constant, final Container viewObject )
    {
        final ConstantOutput co = new ConstantOutput ( "value" );
        if ( constant.getNull () != null )
        {
            co.setValue ( Type.NULL, null );
        }
        else if ( constant.getBoolean () != null )
        {
            co.setValue ( Type.BOOLEAN, constant.getBoolean ().getBooleanValue () );
        }
        else if ( constant.getDouble () != null )
        {
            co.setValue ( Type.DOUBLE, constant.getDouble ().getDoubleValue () );
        }
        else if ( constant.getInteger () != null )
        {
            co.setValue ( Type.INTEGER, constant.getInteger ().getLongValue () );
        }
        else if ( constant.getString () != null )
        {
            co.setValue ( Type.STRING, constant.getString () );
        }
        else if ( constant.getVariant () != null )
        {
            co.setValue ( Type.VARIANT, Helper.fromXML ( constant.getVariant () ) );
        }
        else if ( constant.getColor () != null )
        {
            final Color color = new Color ( constant.getColor ().getRed (), constant.getColor ().getGreen (), constant.getColor ().getBlue () );
            co.setValue ( Type.COLOR, color );
        }
        else
        {
            _log.warn ( "Unknown constant type! Setting null!" );
            co.setValue ( Type.NULL, null );
        }
        final ConstantObject constantObject = new ConstantObject ( constant.getId (), co );
        viewObject.add ( constantObject );
        ctx.getObjects ().put ( constant.getId (), constantObject );
        _log.debug ( String.format ( "Created object (constant) %s", constant.getId () ) );
    }

    public static void createObject ( final XMLContainerContext ctx, final ObjectType object, final Container viewObject ) throws ConfigurationError
    {
        final ObjectFactory factory = ctx.getConfigurationContext ().getObjectFactories ().get ( object.getType () );
        if ( factory == null )
        {
            throw new ConfigurationError ( String.format ( "Failed to create object since object type %s is unknown", object.getType () ) );
        }

        final DynamicObject dynamicObject = factory.create ( object.getId () );

        for ( final PropertyType property : object.getPropertyList () )
        {
            setObjectProperty ( dynamicObject, property.getName ().toString (), property.getStringValue () );
        }

        viewObject.add ( dynamicObject );
        ctx.getObjects ().put ( object.getId (), dynamicObject );
        _log.debug ( String.format ( "Created object %s", object.getId () ) );
    }

    public static void setObjectProperty ( final Object object, final String name, final String stringValue ) throws ConfigurationError
    {
        try
        {
            final BeanInfo beanInfo = Introspector.getBeanInfo ( object.getClass () );
            for ( final PropertyDescriptor pd : beanInfo.getPropertyDescriptors () )
            {
                if ( pd.getName ().equals ( name ) )
                {
                    final PropertyEditor pe = PropertyEditorManager.findEditor ( pd.getPropertyType () );
                    if ( pe != null )
                    {
                        pe.setAsText ( stringValue );
                        pd.getWriteMethod ().invoke ( object, new Object[] { pe.getValue () } );
                        return;
                    }
                    if ( pd.getPropertyType ().isAssignableFrom ( stringValue.getClass () ) )
                    {
                        pd.getWriteMethod ().invoke ( object, new Object[] { stringValue } );
                        return;
                    }
                    throw new ConfigurationError ( String.format ( "No way to set value. Object: %s, Property: %s, String-Value: '%s'", object, name, stringValue ) );
                }
            }
        }
        catch ( final ConfigurationError e )
        {
            _log.debug ( "Failed to set property", e );
            throw e;
        }
        catch ( final Throwable e )
        {
            _log.debug ( "Failed to set property", e );
            throw new ConfigurationError ( String.format ( "Unable to set property for dynamic object. Object: %s, Property: %s, String-Value: '%s'", object, name, stringValue ), e );
        }
    }

    public static void createConnector ( final XMLContainerContext ctx, final ConnectorType connector, final Container viewObject ) throws ConfigurationError
    {
        final Connector connectorObject = ctx.getConfigurationContext ().getConnectorFactories ().get ( connector.getType () ).create ();

        final InputType input = connector.getInput ();
        try
        {
            final DynamicObject object = ctx.getObjects ().get ( input.getObject () );

            if ( object == null )
            {
                throw new ConfigurationError ( String.format ( "Unable to find object '%s' for binding", input.getObject () ) );
            }

            final InputDefinition inputDef = object.getInputByName ( input.getName () );
            if ( inputDef == null )
            {
                throw new ConfigurationError ( String.format ( "Unable to find input '%s' on '%s' for binding", input.getName (), input.getObject () ) );
            }

            connectorObject.setInput ( object.getInputByName ( input.getName () ) );
        }
        catch ( final AlreadyConnectedException e )
        {
            throw new ConfigurationError ( String.format ( "Unable to connect to object: %s/%s", input.getObject (), input.getName () ), e );
        }

        final OutputType output = connector.getOutput ();
        final DynamicObject outputObject = ctx.getObjects ().get ( output.getObject () );
        if ( outputObject == null )
        {
            throw new ConfigurationError ( String.format ( "Unable to connect to output: %s", output.getObject () ) );
        }

        final OutputDefinition outputDef = outputObject.getOutputByName ( output.getName () );
        if ( outputDef == null )
        {
            throw new ConfigurationError ( String.format ( "Unable to find output: %s/%s Input: %s/%s", output.getObject (), output.getName (), input.getObject (), input.getName () ) );
        }
        connectorObject.setOutput ( outputDef );

        viewObject.add ( connectorObject );
    }

    private static void checkCallConfigurationHook ( final XMLConfigurationContext ctx, final Object object, final Node node ) throws ConfigurationError
    {
        if ( object == null )
        {
            return;
        }
        if ( node == null )
        {
            return;
        }
        if ( ! ( object instanceof XMLConfigurable ) )
        {
            return;
        }

        for ( int i = 0; i < node.getChildNodes ().getLength (); i++ )
        {
            final Node childNode = node.getChildNodes ().item ( i );
            if ( childNode != null )
            {
                if ( childNode.getNodeType () == Node.ELEMENT_NODE )
                {
                    ( (XMLConfigurable)object ).configure ( ctx, childNode );
                    return;
                }
            }
        }
    }
}
