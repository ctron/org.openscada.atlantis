package org.openscada.ae.storage.syslog;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.openscada.ae.core.Event;
import org.openscada.ae.core.QueryDescription;
import org.openscada.ae.storage.common.StorageCommon;
import org.openscada.ae.storage.common.memory.MemoryQuery;
import org.openscada.ae.storage.syslog.provider.SyslogDaemonProvider;
import org.openscada.ae.storage.syslog.provider.SyslogFileProvider;
import org.openscada.ae.syslog.ConfigurationDocument;
import org.openscada.ae.syslog.FileProviderType;
import org.openscada.ae.syslog.SyslogUdpProviderType;
import org.openscada.core.Variant;
import org.openscada.utils.collection.MapBuilder;

public class Storage extends StorageCommon implements DataStore
{
    private static Logger _log = Logger.getLogger ( Storage.class );
    
    private MemoryQuery _allQuery = new MemoryQuery ();
    
    private List<Object> _providers = new LinkedList<Object> ();
    
    public Storage () throws XmlException, IOException
    {
        super ();
        
        addQuery ( new QueryDescription ( "all", new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "A query containing all items" ) )
                .getMap () ), _allQuery );
    
        configure ();
        
        _providers.add ( new SyslogFileProvider ( this, new File ( "/var/log/syslog" ), "INFO" ) );
        _providers.add ( new SyslogFileProvider ( this, new File ( "/var/log/auth.log" ), "INFO" ) );
        _providers.add ( new SyslogFileProvider ( this, new File ( "/var/log/daemon.log" ), "INFO" ) );
        _providers.add ( new SyslogFileProvider ( this, new File ( "/var/log/user.log" ), "INFO" ) );
        _providers.add ( new SyslogFileProvider ( this, new File ( "/var/log/debug" ), "DEBUG" ) );
        
        _providers.add ( new SyslogDaemonProvider ( this, 1402 ) );
    }
    
    private void configure () throws XmlException, IOException
    {
        String fileName = System.getProperty ( "org.openscada.ae.syslog.configuration", "configuration.xml" );
        
        File file = new File ( fileName );
        if ( file.canRead () )
        {
            ConfigurationDocument doc = ConfigurationDocument.Factory.parse ( file );
            for ( FileProviderType fileProvider : doc.getConfiguration ().getProviders ().getFileProviderList () )
            {
                _providers.add ( new SyslogFileProvider ( this, new File ( fileProvider.getFile () ), fileProvider.getDefaultSeverity ().toString () ) );
            }
            for ( SyslogUdpProviderType udpProvider : doc.getConfiguration ().getProviders ().getSyslogUdpProviderList () )
            {
                _providers.add ( new SyslogDaemonProvider ( this, udpProvider.getPort () ) );
            }

        }
    }

    @Override
    public synchronized void submitEvent ( Properties properties, Event event ) throws Exception
    {
        throw new Exception ( "not supported" );
    }
    
    public void submitEvent ( Event event )
    {
        try
        {
            super.submitEvent ( new Properties (), event );
        }
        catch ( Exception e )
        {
         _log.warn ( "failed to add event", e );   
        }
    }
}
