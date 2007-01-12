package org.openscada.core.client;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionInformation
{

    /*
     * Format:
     *  da:net://target:secondarytarget/subtarget/subsubtarget?property1=value1&property2=value2
     */

    private String _interface = null;

    private String _driver = null;

    private String _target = null;

    private Integer _secondaryTarget = null;

    private List<String> _subtargets = new LinkedList<String> ();

    private Map<String, String> _properties = new HashMap<String, String> ();

    public static ConnectionInformation fromURI ( String uri )
    {
        return fromURI ( URI.create ( uri ) );
    }

    public static ConnectionInformation fromURI ( URI uri )
    {
        URI subUri = URI.create ( uri.getRawSchemeSpecificPart () );

        try
        {
            ConnectionInformation ci = new ConnectionInformation ();
            ci._interface = uri.getScheme ();
            ci._driver = subUri.getScheme ();
            ci._target = subUri.getHost ();

            if ( subUri.getPort () >= 0 )
            {
                ci._secondaryTarget = subUri.getPort ();
            }

            if ( subUri.getPath () != null )
            {
                // remove leading slash since it would create an empty first subtarget
                String path = subUri.getPath ();
                if ( path.startsWith ( "/" ) )
                    path = path.substring ( 1 );

                if ( path.length () > 0 )
                {
                    ci._subtargets = new LinkedList<String> ( Arrays.asList ( path.split ( "\\/" ) ) );
                }
            }

            // parse user info
            if ( subUri.getUserInfo () != null )
            {
                String[] userInfo = subUri.getRawUserInfo ().split ( "\\:" );
                if ( userInfo.length > 0 )
                    ci._properties.put ( "user", URLDecoder.decode ( userInfo[0], "utf-8" ) );
                if ( userInfo.length > 1 )
                    ci._properties.put ( "password", URLDecoder.decode ( userInfo[1], "utf-8" ) );
            }

            // parse query
            if ( subUri.getRawQuery () != null )
            {
                Pattern p = Pattern.compile ( "(.*?)=(.*)" );
                for ( String pair : subUri.getRawQuery ().split ( "\\&" ) )
                {
                    Matcher m = p.matcher ( pair );
                    if ( m.matches () )
                    {
                        String key = URLDecoder.decode ( m.group ( 1 ), "utf-8" );
                        String value = URLDecoder.decode ( m.group ( 2 ), "utf-8" );
                        ci._properties.put ( key, value );
                    }
                    else
                    {
                        ci._properties.put ( URLDecoder.decode ( pair, "utf-8" ), "" );
                    }
                }
            }

            return ci;
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    @Override
    public String toString ()
    {
        try
        {
            String userInfo = null;
            String user = _properties.get ( "user" );
            String password = _properties.get ( "password" );
            String subtargets = null;
            String query = null;

            // prepare subtargets
            if ( _subtargets.size () > 0 )
            {
                subtargets = "";
                for ( String subtarget : _subtargets )
                {
                    subtargets += "/";
                    subtargets += URLEncoder.encode ( subtarget, "utf-8" );
                }
            }

            // perpare properties
            _properties.remove ( "user" );
            _properties.remove ( "password" );
            for ( Map.Entry<String, String> entry : _properties.entrySet () )
            {
                if ( query == null )
                    query = "?";
                else
                    query += "&";
                query += URLEncoder.encode ( entry.getKey (), "utf-8" );
                query += "=";
                query += URLEncoder.encode ( entry.getValue (), "utf-8" );
            }

            // prepare user info
            if ( user != null && password != null )
                userInfo = URLEncoder.encode ( user, "utf-8" ) + ":" + URLEncoder.encode ( password, "utf-8" );
            else if ( user != null )
                userInfo = URLEncoder.encode ( user, "utf-8" );

            String uri = "";
            
            uri += URLEncoder.encode ( _interface, "utf-8" ) + ":" + URLEncoder.encode ( _driver, "utf-8" ) + "://";
            if ( userInfo != null )
                uri += userInfo + "@";
            
            uri += URLEncoder.encode ( _target, "utf-8" );
            if ( _secondaryTarget != null )
                uri += ":" + _secondaryTarget;
            uri += subtargets + query;
            
            return uri;
        }
        catch ( UnsupportedEncodingException e )
        {
            return null;
        }
    }

    public String getDriver ()
    {
        return _driver;
    }

    public void setDriver ( String driver )
    {
        _driver = driver;
    }

    public String getInterface ()
    {
        return _interface;
    }

    public void setInterface ( String interface1 )
    {
        _interface = interface1;
    }

    public Integer getSecondaryTarget ()
    {
        return _secondaryTarget;
    }

    public void setSecondaryTarget ( Integer secondaryTarget )
    {
        _secondaryTarget = secondaryTarget;
    }

    public List<String> getSubtargets ()
    {
        return _subtargets;
    }

    public void setSubtargets ( List<String> subtargets )
    {
        _subtargets = subtargets;
    }

    public String getTarget ()
    {
        return _target;
    }

    public void setTarget ( String target )
    {
        _target = target;
    }

    public Map<String, String> getProperties ()
    {
        return _properties;
    }

    public void setProperties ( Map<String, String> properties )
    {
        _properties = properties;
    }

    public boolean isValid ()
    {
        return _driver != null && _interface != null && _properties != null && _subtargets != null && _target != null;
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( _driver == null ) ? 0 : _driver.hashCode () );
        result = PRIME * result + ( ( _interface == null ) ? 0 : _interface.hashCode () );
        result = PRIME * result + ( ( _properties == null ) ? 0 : _properties.hashCode () );
        result = PRIME * result + ( ( _secondaryTarget == null ) ? 0 : _secondaryTarget.hashCode () );
        result = PRIME * result + ( ( _subtargets == null ) ? 0 : _subtargets.hashCode () );
        result = PRIME * result + ( ( _target == null ) ? 0 : _target.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass () != obj.getClass () )
            return false;
        final ConnectionInformation other = (ConnectionInformation)obj;
        if ( _driver == null )
        {
            if ( other._driver != null )
                return false;
        }
        else if ( !_driver.equals ( other._driver ) )
            return false;
        if ( _interface == null )
        {
            if ( other._interface != null )
                return false;
        }
        else if ( !_interface.equals ( other._interface ) )
            return false;
        if ( _properties == null )
        {
            if ( other._properties != null )
                return false;
        }
        else if ( !_properties.equals ( other._properties ) )
            return false;
        if ( _secondaryTarget == null )
        {
            if ( other._secondaryTarget != null )
                return false;
        }
        else if ( !_secondaryTarget.equals ( other._secondaryTarget ) )
            return false;
        if ( _subtargets == null )
        {
            if ( other._subtargets != null )
                return false;
        }
        else if ( !_subtargets.equals ( other._subtargets ) )
            return false;
        if ( _target == null )
        {
            if ( other._target != null )
                return false;
        }
        else if ( !_target.equals ( other._target ) )
            return false;
        return true;
    }
}
