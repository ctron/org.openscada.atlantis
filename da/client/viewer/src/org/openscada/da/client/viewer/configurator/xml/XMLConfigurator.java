package org.openscada.da.client.viewer.configurator.xml;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

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
    
    public XMLConfigurator ( RootDocument document )
    {
        super ();
        _document = document;
    }
    
    public XMLConfigurator ( InputStream stream ) throws XmlException, IOException
    {
        this ( RootDocument.Factory.parse ( stream ) );
    }
    
    public List<View> configure () throws ConfigurationError
    {
        XMLConfigurationContext ctx = new XMLConfigurationContext ();
        ctx.setDocument ( _document );
        
        List<View> views = new LinkedList<View> ();
        
        configureFactories ( ctx, _document.getRoot ().getFactories () );
        configureViews ( ctx, _document.getRoot ().getViews (), views );
        
        return views;
    }

    public static void configureFactories ( XMLConfigurationContext ctx, FactoriesType factories ) throws ConfigurationError
    {
        for ( ContainerFactoryType factory : factories.getContainerFactoryList () )
        {
            createContainerFactory ( ctx, factory );
        }
        for ( ConnectorFactoryType factory : factories.getConnectorFactoryList () )
        {
            createConnectorFactory ( ctx, factory );
        }
        for ( ObjectFactoryType factory : factories.getObjectFactoryList () )
        {
            createObjectFactory ( ctx, factory );
        }
    }

    private static void createContainerFactory ( XMLConfigurationContext ctx, ContainerFactoryType factory ) throws ConfigurationError
    {
        try
        {
            Class factoryClass = Class.forName ( factory.getClass1 () );
            
            
            ContainerFactory containerFactory = ContainerCreator.findFactory ( factoryClass );
            if ( containerFactory == null )
            {
                throw new ConfigurationError ( String.format ( "Unable to create new container factory." ) );
            }
            
            checkCallConfigurationHook ( ctx, containerFactory, factory.getDomNode () );
            
            ctx.getContainerFactories ().put ( factory.getId (), containerFactory );
                
        }
        catch ( ClassNotFoundException e )
        {
            throw new ConfigurationError ( "Unable to load class", e );
        }
    }

    public static void createConnectorFactory ( XMLConfigurationContext ctx, ConnectorFactoryType factory ) throws ConfigurationError
    {
        try
        {
            Class factoryClass = Class.forName ( factory.getClass1 () );
            
            Object factoryObject = factoryClass.newInstance ();
            
            if ( ! ( factoryObject instanceof ConnectorFactory ) )
                throw new ConfigurationError ( "Class does not implement ConnectorFactory interface" );
            
            ConnectorFactory connectorFactory = (ConnectorFactory)factoryObject;
            checkCallConfigurationHook ( ctx, connectorFactory, factory.getDomNode () );
            
            ctx.getConnectorFactories ().put ( factory.getId (), connectorFactory );
                
        }
        catch ( ClassNotFoundException e )
        {
            throw new ConfigurationError ( "Unable to load class", e );
        }
        catch ( InstantiationException e )
        {
            throw new ConfigurationError ( "Unable to instatiate connector factory class", e );
        }
        catch ( IllegalAccessException e )
        {
            throw new ConfigurationError ( "No access to instatiate connector factory class", e );        }
    }
    
    public static void createObjectFactory ( XMLConfigurationContext ctx, ObjectFactoryType factory ) throws ConfigurationError
    {
        try
        {
            Class objectClass = Class.forName ( factory.getClass1 () );
            
            ObjectFactory objectFactory = DynamicObjectCreator.findFactory ( objectClass );
            checkCallConfigurationHook ( ctx, objectFactory, factory.getDomNode () );
            
            ctx.getObjectFactories ().put ( factory.getId (), objectFactory );
            _log.debug ( String.format ( "Created object factory: %s", factory.getId () ) );
                
        }
        catch ( ClassNotFoundException e )
        {
            throw new ConfigurationError ( "Unable to load class", e );
        }
    }

    public static void configureViews ( XMLConfigurationContext ctx, ViewsType views, List<View> viewList ) throws ConfigurationError
    {
        for ( ViewType view : views.getViewList () )
        {
            Container container = createContainer ( new XMLContainerContext ( ctx ), view.getId (), view );
            if ( container instanceof View )
            {
                viewList.add ( (View)container );
            }
        }
    }

    public static Container createContainer ( XMLContainerContext ctx, String id, ContainerType container ) throws ConfigurationError
    {
        Container containerObject = null;

        ContainerFactory containerFactory = ctx.getConfigurationContext ().getContainerFactories ().get ( container.getType () );
        if ( containerFactory == null )
            throw new ConfigurationError ( String.format ( "Unable to find container factory %s", container.getType () ) );
        
        containerObject = containerFactory.create ( id );
        
        for ( PropertyType property : container.getPropertyList () )
        {
            setObjectProperty ( containerObject, property.getName ().toString (), property.getStringValue () );
        }
        
        for ( ConstantType constant : container.getObjects ().getConstantList () )
        {
            createConstant ( ctx, constant, containerObject );
        }
        
        for ( ObjectType object : container.getObjects ().getObjectList () )
        {
            createObject ( ctx, object, containerObject );
        }
        
        for ( TemplateObjectType template : container.getObjects ().getTemplateObjectList () )
        {
            createTemplateObject ( ctx, template, containerObject );
        }
        
        for ( ContainerType containerType : container.getObjects ().getContainerList () )
        {
            createContainer ( ctx, containerType, containerObject );
        }
        
        for ( ConnectorType connector : container.getConnectors ().getConnectorList () )
        {
            createConnector ( ctx, connector, containerObject );
        }
       
        if ( container.getInputs () != null )
        {
            for ( InputExportType input : container.getInputs ().getInputExportList () )
            {
                containerObject.addInputExport ( new Container.Export ( input.getObject (), input.getName (), input.getExportName () ) );
            }
        }
        
        if ( container.getOutputs () != null )
        {
            for ( OutputExportType output : container.getOutputs ().getOutputExportList () )
            {
                containerObject.addOutputExport ( new Container.Export ( output.getObject (), output.getName (), output.getExportName () ) );
            }
        }
        
        return containerObject;
    }

    private static void createContainer ( XMLContainerContext ctx, ContainerType template, Container parentContainer ) throws ConfigurationError
    {
        XMLContainerContext cctx = new XMLContainerContext ( ctx.getConfigurationContext () );
        Container container = createContainer ( cctx, template.getId (), template );
        parentContainer.add ( container );
        ctx.getObjects ().put ( template.getId (), container );
    }

    public static void createTemplateObject ( XMLContainerContext ctx, TemplateObjectType template, Container viewObject ) throws ConfigurationError
    {
        ObjectFactory factory = ctx.getConfigurationContext ().getObjectFactories ().get ( template.getTemplate () );
        
        if ( factory == null )
            throw new ConfigurationError ( String.format ( "Unable to find template object factory %s", template.getTemplate () ) );
        
        DynamicObject dynamicObject = factory.create ( template.getId () );
        
        if ( dynamicObject == null )
        {
            throw new ConfigurationError ( String.format ( "Unable to create template object %s", template.getTemplate () ) );
        }
        
        for ( PropertyType property : template.getPropertyList () )
        {
            setObjectProperty ( dynamicObject, property.getName ().toString (), property.getStringValue () );
        }
        
        viewObject.add ( dynamicObject );
        ctx.getObjects ().put ( template.getId (), dynamicObject );
        _log.debug ( String.format ( "Created object (template) %s", template.getId () ) );
    }

    public static void createConstant ( XMLContainerContext ctx, ConstantType constant, Container viewObject )
    {
        ConstantOutput co = new ConstantOutput ( "value" );
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
            Color color = new Color ( constant.getColor ().getRed (), constant.getColor ().getGreen (), constant.getColor ().getBlue () );
            co.setValue ( Type.COLOR, color );
        }
        else 
        {
            _log.warn ( "Unknown constant type! Setting null!" );
            co.setValue ( Type.NULL, null );
        }
        ConstantObject constantObject = new ConstantObject ( constant.getId (), co );
        viewObject.add ( constantObject );
        ctx.getObjects ().put ( constant.getId (), constantObject );
        _log.debug ( String.format ( "Created object (constant) %s", constant.getId () ) );
    }

    public static void createObject ( XMLContainerContext ctx, ObjectType object, Container viewObject ) throws ConfigurationError
    {
        ObjectFactory factory = ctx.getConfigurationContext ().getObjectFactories ().get ( object.getType () );
        if ( factory == null )
            throw new ConfigurationError ( String.format ( "Failed to create object since object type %s is unknown", object.getType () ) );
        
        DynamicObject dynamicObject = factory.create ( object.getId () );
        
        for ( PropertyType property : object.getPropertyList () )
        {
            setObjectProperty ( dynamicObject, property.getName ().toString (), property.getStringValue () );
        }
        
        viewObject.add ( dynamicObject );
        ctx.getObjects ().put ( object.getId (), dynamicObject );
        _log.debug ( String.format ( "Created object %s", object.getId () ) );
    }

    public static void setObjectProperty ( Object object, String name, String stringValue ) throws ConfigurationError
    {
        try
        {
            BeanInfo beanInfo = Introspector.getBeanInfo ( object.getClass () );
            for ( PropertyDescriptor pd  : beanInfo.getPropertyDescriptors () )
            {
                if ( pd.getName ().equals ( name ) )
                {
                    PropertyEditor pe = PropertyEditorManager.findEditor ( pd.getPropertyType () );
                    pe.setAsText ( stringValue );
                    pd.getWriteMethod ().invoke ( object, new Object[] { pe.getValue () } );
                }
            }
        }
        catch ( Exception e )
        {
            throw new ConfigurationError ( "Unable to set property for dynamic object", e );
        }
    }

    public static void createConnector ( XMLContainerContext ctx, ConnectorType connector, Container viewObject ) throws ConfigurationError
    {
        Connector connectorObject = ctx.getConfigurationContext ().getConnectorFactories ().get ( connector.getType () ).create ();
        
        InputType input = connector.getInput ();
        try
        {
            DynamicObject object = ctx.getObjects ().get ( input.getObject () );
            
            if ( object == null )
                throw new ConfigurationError ( String.format ( "Unable to find object '%s' for binding", input.getObject () ) );
            
            InputDefinition inputDef = object.getInputByName ( input.getName () );
            if ( inputDef == null )
                throw new ConfigurationError ( String.format ( "Unable to find input '%s' on '%s' for binding", input.getName (), input.getObject () ) );
            
            connectorObject.setInput ( object.getInputByName ( input.getName () ) );
        }
        catch ( AlreadyConnectedException e )
        {
            throw new ConfigurationError ( String.format ( "Unable to connect to object: %s/%s", input.getObject (), input.getName () ), e );
        }
        
        OutputType output = connector.getOutput ();
        DynamicObject outputObject = ctx.getObjects ().get ( output.getObject () );
        if ( outputObject == null )
            throw new ConfigurationError ( String.format ( "Unable to connect to output: %s", output.getObject () ) );
        
        OutputDefinition outputDef = outputObject.getOutputByName ( output.getName () );
        if ( outputDef == null )
            throw new ConfigurationError ( String.format ( "Unable to find output: %s/%s Input: %s/%s", output.getObject (), output.getName (), input.getObject (), input.getName () ) );
        connectorObject.setOutput ( outputDef );
        
        viewObject.add ( connectorObject );
    }

    private static void checkCallConfigurationHook ( XMLConfigurationContext ctx, Object object, Node node ) throws ConfigurationError
    {
        if ( object == null )
            return;
        if ( node == null )
            return;
        if ( !(object instanceof XMLConfigurable) )
            return;
        
        for ( int i = 0; i < node.getChildNodes ().getLength (); i++ )
        {
            Node childNode = node.getChildNodes ().item ( i );
            if ( childNode != null )
            {
                if ( childNode.getNodeType () == Node.ELEMENT_NODE )
                {
                    ((XMLConfigurable)object).configure ( ctx, childNode );
                    return;
                }
            }
        }
    }
}
