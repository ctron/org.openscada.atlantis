package org.openscada.da.client.viewer.configurator.xml;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.InputDefinition;
import org.openscada.da.client.viewer.model.ObjectFactory;
import org.openscada.da.client.viewer.model.OutputDefinition;
import org.openscada.da.client.viewer.model.impl.View;
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

    public DynamicObject create () throws ConfigurationError
    {
        XMLViewContext ctx = new XMLViewContext ( _context );
        View view = XMLConfigurator.createView ( ctx, _template );
        return createTemplate ( ctx, view, _template ); 
    }

    private DynamicObject createTemplate ( XMLViewContext ctx, View view, TemplateType template ) throws ConfigurationError
    {
        XMLDynamicObject xo = null;
        if ( template.getGui () )
        {
            xo = new XMLDynamicUIObject ( view );
        }
        else
        {
            xo =  new XMLDynamicObject ( view );
        }
        
        configureXO ( ctx, xo, template );
        
        return xo;
    }

    private void configureXO ( XMLViewContext ctx, XMLDynamicObject xo, TemplateType template ) throws ConfigurationError
    {
        List<XMLInputExport> inputExports = new LinkedList<XMLInputExport> ();
        
        for ( InputExportType input : template.getInputs ().getInputExportList () )
        {
            DynamicObject object = ctx.getObjects ().get ( input.getObject () );
            if ( object == null )
                throw new ConfigurationError ( String.format ( "Unable to export input since object %s is unknown", input.getObject () ) );
             
            InputDefinition inputDef = object.getInputByName ( input.getName () );
            if ( inputDef == null )
                throw new ConfigurationError ( String.format ( "Unable to export input since input %s of object %s is unknown", input.getName (), input.getObject () ) );
            
            inputExports.add ( new XMLInputExport ( inputDef, input.getExportName () ) );
        }
        xo.setInputExports ( inputExports );
        
        // now the same for outputs
        
        List<XMLOutputExport> outputExports = new LinkedList<XMLOutputExport> ();
        
        for ( OutputExportType output : template.getOutputs ().getOutputExportList () )
        {
            DynamicObject object = ctx.getObjects ().get ( output.getObject () );
            if ( object == null )
                throw new ConfigurationError ( String.format ( "Unable to export output since object %s is unknown", output.getObject () ) );
             
            OutputDefinition outputDef = object.getOutputByName ( output.getName () );
            if ( outputDef == null )
                throw new ConfigurationError ( String.format ( "Unable to export input since output %s of object %s is unknown", output.getName (), output.getObject () ) );
            
            outputExports.add ( new XMLOutputExport ( outputDef, output.getExportName () ) );
        }
        xo.setOutputExports ( outputExports );
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
