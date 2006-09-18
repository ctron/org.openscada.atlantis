package org.openscada.da.core.common.configuration.xml;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.openscada.da.core.common.configuration.ConfigurableHive;
import org.openscada.da.core.common.configuration.Configurator;
import org.openscada.da.hive.BrowserType;
import org.openscada.da.hive.HiveDocument;
import org.openscada.da.hive.ItemTemplatesType;
import org.openscada.da.hive.ItemsType;

public class XMLConfigurator implements Configurator
{
    private ConfigurableHive _hive = null;
    private HiveDocument _document = null;
    
    public XMLConfigurator ( ConfigurableHive hive, File file ) throws XmlException, IOException
    {
        _hive = hive;
        
        _document = HiveDocument.Factory.parse ( file );
        _document.getHive ().getBrowser ().getFolder ().getEntryArray ( 0 ).getDataItemReferenceArray ( 0 ).getRef ();
        
        if ( !_document.validate () )
        {
            throw new XmlException ( "Document is not valid!" );
        }
    }
    
    /* (non-Javadoc)
     * @see org.openscada.da.core.common.configuration.Configurator#configure()
     */
    public void configure ()
    {
        configureTemplates ( _document.getHive ().getItemTemplates () );
        configureItems ( _document.getHive ().getItems () );
        configureBrowser ( _document.getHive ().getBrowser () );
    }

    private void configureBrowser ( BrowserType browser )
    {
    }

    private void configureItems ( ItemsType items )
    {
    }

    private void configureTemplates ( ItemTemplatesType itemTemplates )
    {
    }
}
