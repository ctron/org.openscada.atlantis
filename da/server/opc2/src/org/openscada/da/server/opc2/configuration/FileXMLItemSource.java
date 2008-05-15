package org.openscada.da.server.opc2.configuration;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.openscada.da.opc.configuration.InitialItemsType;
import org.openscada.da.opc.configuration.ItemsDocument;
import org.openscada.da.server.common.item.factory.FolderItemFactory;

public class FileXMLItemSource extends AbstractXMLItemSource
{
    private File file;

    public FileXMLItemSource ( String file, FolderItemFactory itemFactory, String baseId )
    {
        super ( itemFactory, baseId );
        this.file = new File ( file );
    }

    @Override
    protected InitialItemsType parse () throws XmlException, IOException
    {
        InitialItemsType items = ItemsDocument.Factory.parse ( this.file ).getItems ();
        return items;
    }

}
