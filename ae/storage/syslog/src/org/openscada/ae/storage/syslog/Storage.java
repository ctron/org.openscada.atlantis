package org.openscada.ae.storage.syslog;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openscada.ae.core.Event;
import org.openscada.ae.core.QueryDescription;
import org.openscada.ae.storage.common.StorageCommon;
import org.openscada.ae.storage.common.memory.MemoryQuery;
import org.openscada.ae.storage.syslog.provider.SyslogProvider;
import org.openscada.core.Variant;
import org.openscada.utils.collection.MapBuilder;

public class Storage extends StorageCommon implements DataStore
{
    private static Logger _log = Logger.getLogger ( Storage.class );
    
    private MemoryQuery _allQuery = new MemoryQuery ();
    
    private List<Object> _providers = new LinkedList<Object> ();
    
    public Storage ()
    {
        super ();
        
        addQuery ( new QueryDescription ( "all", new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "A query containing all items" ) )
                .getMap () ), _allQuery );
    
        _providers.add ( new SyslogProvider ( this, new File ( "/var/log/syslog" ), "syslog", "INFO" ) );
        _providers.add ( new SyslogProvider ( this, new File ( "/var/log/auth.log" ), "auth", "INFO" ) );
        _providers.add ( new SyslogProvider ( this, new File ( "/var/log/daemon.log" ), "daemon", "INFO" ) );
        _providers.add ( new SyslogProvider ( this, new File ( "/var/log/user.log" ), "user", "INFO" ) );
        _providers.add ( new SyslogProvider ( this, new File ( "/var/log/debug" ), "debug", "DEBUG" ) );
    }

    public void submitEvent ( Event event )
    {
        try
        {
            submitEvent ( new Properties (), event );
        }
        catch ( Exception e )
        {
         _log.warn ( "failed to add event", e );   
        }
    }
}
