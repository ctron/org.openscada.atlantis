package org.openscada.da.client.viewer.configurator.xml;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.model.Container;
import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.ObjectFactory;
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
        XMLContainerContext ctx = new XMLContainerContext ( _context );
        return createTemplate ( id, ctx );
    }

    private DynamicObject createTemplate ( String id, XMLContainerContext ctx ) throws ConfigurationError
    {
        return XMLConfigurator.createContainer ( ctx, id, _template );
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
