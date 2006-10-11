package org.openscada.da.client.viewer.configurator.xml;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.model.Container;
import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.ObjectFactory;
import org.openscada.da.viewer.template.InputExportType;
import org.openscada.da.viewer.template.OutputExportType;
import org.openscada.da.viewer.template.TemplateDocument;
import org.openscada.da.viewer.template.TemplateType;
import org.w3c.dom.Node;

public class XMLTemplateFactory implements ObjectFactory, XMLConfigurable
{
    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger ( XMLTemplateFactory.class );
    
    private TemplateType _template = null;
    private XMLConfigurationContext _context = null;

    public DynamicObject create ( String id ) throws ConfigurationError
    {
        XMLViewContext ctx = new XMLViewContext ( _context );
        return createTemplate ( id, ctx );
    }

    private DynamicObject createTemplate ( String id, XMLViewContext ctx ) throws ConfigurationError
    {
        Container container = XMLConfigurator.createContainer ( ctx, id, _template );
        
        for ( InputExportType input : _template.getInputs ().getInputExportList () )
        {
            container.addInputExport ( new Container.Export ( input.getObject (), input.getName (), input.getExportName () ) );
        }
        
        // now the same for outputs
        for ( OutputExportType output : _template.getOutputs ().getOutputExportList () )
        {
            container.addOutputExport ( new Container.Export ( output.getObject (), output.getName (), output.getExportName () ) );
        }
        
        return container;
    }

    public void configure ( XMLConfigurationContext ctx, Node node ) throws ConfigurationError
    {
        _context = ctx;
        try
        {
            TemplateDocument document = TemplateDocument.Factory.parse ( node );
            _template = document.getTemplate ();
        }
        catch ( XmlException e )
        {
           throw new ConfigurationError ( "failed to parse template factory xml", e );
        }
    }

}
