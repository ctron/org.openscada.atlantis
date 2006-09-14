package org.openscada.ae.submitter.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscada.ae.core.Event;
import org.openscada.core.Variant;

public class Application
{
    public static Event fillEventAndProperties ( String[] args, Properties properties )
    {
        String id = "openscada_ae_submitter." + System.currentTimeMillis ();
        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        
        Pattern p1 = Pattern.compile ( "-A([0-9a-zA-Z\\.-_:]+)=(.*)" );
        Pattern p2 = Pattern.compile ( "-P([0-9a-zA-Z\\.-_:]+)=(.*)" );
        Pattern p3 = Pattern.compile ( "--id=(.*)" );
        
        for ( String arg : args )
        {
            Matcher m;
            
            // check for attribute
            m = p1.matcher ( arg );
            if ( m.matches () )
            {
                String key = m.group ( 1 );
                String value = m.group ( 2 );
                attributes.put ( key, new Variant ( value ) );
            }
            
            // check for property
            m = p2.matcher ( arg );
            if ( m.matches () )
            {
                String key = m.group ( 1 );
                String value = m.group ( 2 );
                properties.put ( key, value );
            }
            
            m = p3.matcher ( arg );
            if ( m.matches () )
            {
                id = m.group ( 1 );
            }
        }
        
        Event event = new Event ( id );
        event.setAttributes ( attributes );
        return event;
    }
    
    public static void main ( String[] args ) throws Throwable
    {
        Properties properties = new Properties ();
        Event event = fillEventAndProperties ( args, properties );
        
        new Submitter ().submitEvent ( properties, event );
    }
}
