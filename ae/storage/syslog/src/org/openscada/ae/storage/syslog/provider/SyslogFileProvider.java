package org.openscada.ae.storage.syslog.provider;

import java.io.File;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openscada.ae.core.Event;
import org.openscada.ae.storage.syslog.DataStore;
import org.openscada.core.Variant;

public class SyslogFileProvider extends FileProviderBase
{
    private static Logger _log = Logger.getLogger ( SyslogFileProvider.class );

    private SyslogParser _parser = null;
    private DataStore _storage = null;

    private File _file;

    private String _severity;

    public SyslogFileProvider ( DataStore storage, File file, String severity )
    {
        super ( storage, file );
        _storage = storage;
        _file = file;
        _severity = severity;
    }

    @Override
    protected void handleLine ( String line )
    {
        if ( _parser != null )
            _parser.handleLine ( line );
        else
        {
            synchronized ( this )
            {
                if ( _parser == null )
                    _parser = new SyslogParser ( _storage, _file.getAbsolutePath (), _severity );
                _parser.handleLine ( line );
            }
        }
    }
    
   
}
