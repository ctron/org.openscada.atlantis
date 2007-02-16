package org.openscada.da.server.opc;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.openscada.da.opc.configuration.ConfigurationType;
import org.openscada.da.opc.configuration.RootDocument;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.w3c.dom.Node;

public class XMLConfigurator
{
    
    private RootDocument _rootDocument = null;

    public XMLConfigurator ( RootDocument rootDocument )
    {
        _rootDocument  = rootDocument;
    }
    
    public XMLConfigurator ( Node node ) throws XmlException
    {
        this ( RootDocument.Factory.parse ( node ) );
    }
    
    public XMLConfigurator ( String filename ) throws XmlException, IOException
    {
        this ( RootDocument.Factory.parse ( new File ( filename ) ) );
    }
    
    public void configure ( Hive hive ) throws ConfigurationError
    {
        // first configure the base hive
        new org.openscada.da.server.common.configuration.xml.XMLConfigurator ( null, _rootDocument.getRoot ().getItemTemplates (), null, null ).configure ( hive );
        
        // now configure the opc hive
        for ( ConfigurationType configuration : _rootDocument.getRoot ().getConnections ().getConfigurationList () )
        {
            if ( !configuration.getEnabled () )
            {
                continue;
            }
            
            ConnectionInformation ci = new ConnectionInformation ();
            ci.setUser ( configuration.getUser () );
            ci.setPassword ( configuration.getPassword () );
            ci.setDomain ( configuration.getDomain () );
            ci.setHost ( configuration.getHost () );
            ci.setClsid ( configuration.getClsid () );
            ci.setProgId ( configuration.getProgid () );
            
            ConnectionSetup setup = new ConnectionSetup ( ci );
            
            String access = configuration.getAccess ();
            if ( access.equalsIgnoreCase ( "sync" ) )
            {
                setup.setAccessMethod ( AccessMethod.SYNC );
            }
            else if ( access.equalsIgnoreCase ( "async" ) )
            {
                setup.setAccessMethod ( AccessMethod.ASYNC20 );
            }
            else if ( access.equalsIgnoreCase ( "async20" ) )
            {
                setup.setAccessMethod ( AccessMethod.ASYNC20 );
            }
            
            setup.setRefreshTimeout ( configuration.getRefresh () );
            setup.setInitialConnect ( configuration.getInitialRefresh () );
            
            hive.addConnection ( setup, configuration.getConnected () );
        }
    }
}
