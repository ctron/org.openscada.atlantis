package org.openscada.da.server.opc;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.openscada.da.opc.configuration.ConfigurationType;
import org.openscada.da.opc.configuration.RootDocument;
import org.openscada.opc.lib.common.ConnectionInformation;

public class XMLConfigurator
{
    
    private RootDocument _rootDocument = null;

    public XMLConfigurator ( RootDocument rootDocument )
    {
        _rootDocument  = rootDocument;
    }
    
    public XMLConfigurator ( String filename ) throws XmlException, IOException
    {
        this ( RootDocument.Factory.parse ( new File ( filename ) ) );
    }
    
    public void configure ( Hive hive )
    {
        for ( ConfigurationType configuration : _rootDocument.getRoot ().getConnections ().getConfigurationList () )
        {
            ConnectionInformation ci = new ConnectionInformation ();
            ci.setUser ( configuration.getUser () );
            ci.setPassword ( configuration.getPassword () );
            ci.setDomain ( configuration.getDomain () );
            ci.setHost ( configuration.getHost () );
            ci.setClsid ( configuration.getClsid () );
            ci.setProgId ( configuration.getProgid () );
            hive.addConnection ( ci );
        }
    }
}
