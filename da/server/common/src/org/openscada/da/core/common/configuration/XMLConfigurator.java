package org.openscada.da.core.common.configuration;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.openscada.da.hive.HiveDocument;

public class XMLConfigurator
{
    private HiveDocument _document = null;
    
    public XMLConfigurator ( File file ) throws XmlException, IOException
    {
        _document = HiveDocument.Factory.parse ( file );
        _document.getHive ().getBrowser ().getFolderArray ( 0 ).getEntryArray ( 0 ).getDataItemReferenceArray ( 0 ).getRef ();
    }
}
