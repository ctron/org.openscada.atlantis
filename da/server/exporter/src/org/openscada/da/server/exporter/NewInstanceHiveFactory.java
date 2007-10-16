package org.openscada.da.server.exporter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.openscada.da.core.server.Hive;
import org.w3c.dom.Node;

/**
 * Create a new hive by creating a new object.
 * @author Jens Reimann
 *
 */
public class NewInstanceHiveFactory implements HiveFactory
{
    private static Logger _log = Logger.getLogger ( NewInstanceHiveFactory.class );
    
    public Hive createHive ( String reference, HiveConfigurationType configuration ) throws ConfigurationException
    {
        Node subNode = null;
        if ( configuration != null )
        {
            for ( int i = 0; i < configuration.getDomNode ().getChildNodes ().getLength (); i++ )
            {
                Node node = configuration.getDomNode ().getChildNodes ().item ( i ); 
                if ( node.getNodeType () == Node.ELEMENT_NODE )
                {
                    subNode = node;
                }
            }
        }
        
        try
        {
            return createInstance ( reference, subNode );
        }
        catch ( Throwable e )
        {
            throw new ConfigurationException ( "Failed to initialze hive using new instance", e );
        }
    }
    
    protected static Hive createInstance ( String hiveClassName, Node node ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Class<?> hiveClass = Class.forName ( hiveClassName );
       
        Constructor<?> ctor = null;
        
        if ( node != null )
        {
            // if we have an xml configuration node try to use the XML ctor
            try
            {
                ctor = hiveClass.getConstructor ( Node.class );
                if ( ctor != null)
                {
                    _log.debug ( "Using XML-Node constructor" );
                    return (Hive)ctor.newInstance ( new Object [] { node } );
                }
                // fall back to standard ctor
            }
            catch ( Exception e )
            {
                _log.info ( "No XML node constructor found" );
            }
        }
        return (Hive)hiveClass.newInstance ();
    }

}
