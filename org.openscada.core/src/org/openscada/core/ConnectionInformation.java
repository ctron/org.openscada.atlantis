/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.core;

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

    private static final String URI_ENCODING = "utf-8";

    private String interfaceName = null;

    private String driver = null;

    private String target = null;

    private Integer secondaryTarget = null;

    private List<String> subtargets = new LinkedList<String> ();

    private Map<String, String> properties = new HashMap<String, String> ();

    public static ConnectionInformation fromURI ( final String uri )
    {
        return fromURI ( URI.create ( uri ) );
    }

    public static ConnectionInformation fromURI ( final URI uri )
    {
        final URI subUri = URI.create ( uri.getRawSchemeSpecificPart () );

        try
        {
            final ConnectionInformation ci = new ConnectionInformation ();
            ci.interfaceName = uri.getScheme ();
            ci.driver = subUri.getScheme ();
            ci.target = subUri.getHost ();

            if ( subUri.getPort () >= 0 )
            {
                ci.secondaryTarget = subUri.getPort ();
            }

            if ( subUri.getPath () != null )
            {
                // remove leading slash since it would create an empty first subtarget
                String path = subUri.getPath ();
                if ( path.startsWith ( "/" ) )
                {
                    path = path.substring ( 1 );
                }

                if ( path.length () > 0 )
                {
                    ci.subtargets = new LinkedList<String> ( Arrays.asList ( path.split ( "\\/" ) ) );
                }
            }

            // parse user info
            if ( subUri.getUserInfo () != null )
            {
                final String[] userInfo = subUri.getRawUserInfo ().split ( "\\:" );
                if ( userInfo.length > 0 )
                {
                    ci.properties.put ( "user", URLDecoder.decode ( userInfo[0], URI_ENCODING ) );
                }
                if ( userInfo.length > 1 )
                {
                    ci.properties.put ( "password", URLDecoder.decode ( userInfo[1], URI_ENCODING ) );
                }
            }

            // parse query
            if ( subUri.getRawQuery () != null )
            {
                if ( subUri.getRawQuery ().length () > 0 )
                {
                    final Pattern p = Pattern.compile ( "(.*?)=(.*)" );
                    for ( final String pair : subUri.getRawQuery ().split ( "\\&" ) )
                    {
                        final Matcher m = p.matcher ( pair );
                        if ( m.matches () )
                        {
                            final String key = URLDecoder.decode ( m.group ( 1 ), URI_ENCODING );
                            final String value = URLDecoder.decode ( m.group ( 2 ), URI_ENCODING );
                            ci.properties.put ( key, value );
                        }
                        else
                        {
                            ci.properties.put ( URLDecoder.decode ( pair, URI_ENCODING ), "" );
                        }
                    }
                }
            }

            if ( ci.getInterface () == null )
            {
                return null;
            }
            if ( ci.getDriver () == null )
            {
                return null;
            }

            return ci;
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    @Override
    public String toString ()
    {
        try
        {
            final HashMap<String, String> properties = new HashMap<String, String> ( this.properties );

            String userInfo = null;
            final String user = properties.remove ( "user" );
            final String password = properties.remove ( "password" );
            String subtargets = null;
            String query = null;

            // prepare subtargets
            if ( this.subtargets.size () > 0 )
            {
                subtargets = "";
                for ( final String subtarget : this.subtargets )
                {
                    subtargets += "/";
                    subtargets += URLEncoder.encode ( subtarget, URI_ENCODING );
                }
            }

            // perpare properties

            for ( final Map.Entry<String, String> entry : properties.entrySet () )
            {
                if ( query == null )
                {
                    query = "?";
                }
                else
                {
                    query += "&";
                }
                query += URLEncoder.encode ( entry.getKey (), URI_ENCODING );
                query += "=";
                query += URLEncoder.encode ( entry.getValue (), URI_ENCODING );
            }

            // prepare user info
            if ( user != null && password != null )
            {
                userInfo = URLEncoder.encode ( user, URI_ENCODING ) + ":" + URLEncoder.encode ( password, URI_ENCODING );
            }
            else if ( user != null )
            {
                userInfo = URLEncoder.encode ( user, URI_ENCODING );
            }

            String uri = "";

            uri += URLEncoder.encode ( this.interfaceName, URI_ENCODING ) + ":" + URLEncoder.encode ( this.driver, URI_ENCODING ) + "://";
            if ( userInfo != null )
            {
                uri += userInfo + "@";
            }

            uri += URLEncoder.encode ( this.target, URI_ENCODING );
            if ( this.secondaryTarget != null )
            {
                uri += ":" + this.secondaryTarget;
            }
            if ( subtargets != null )
            {
                uri += subtargets;
            }
            if ( query != null )
            {
                uri += query;
            }

            return uri;
        }
        catch ( final UnsupportedEncodingException e )
        {
            return null;
        }
    }

    public String getDriver ()
    {
        return this.driver;
    }

    public void setDriver ( final String driver )
    {
        this.driver = driver;
    }

    public String getInterface ()
    {
        return this.interfaceName;
    }

    public void setInterface ( final String interface1 )
    {
        this.interfaceName = interface1;
    }

    public Integer getSecondaryTarget ()
    {
        return this.secondaryTarget;
    }

    public void setSecondaryTarget ( final Integer secondaryTarget )
    {
        this.secondaryTarget = secondaryTarget;
    }

    public List<String> getSubtargets ()
    {
        return this.subtargets;
    }

    public void setSubtargets ( final List<String> subtargets )
    {
        this.subtargets = subtargets;
    }

    public String getTarget ()
    {
        return this.target;
    }

    public void setTarget ( final String target )
    {
        this.target = target;
    }

    public Map<String, String> getProperties ()
    {
        return this.properties;
    }

    public void setProperties ( final Map<String, String> properties )
    {
        this.properties = properties;
    }

    public boolean isValid ()
    {
        return this.driver != null && this.interfaceName != null && this.properties != null && this.subtargets != null && this.target != null;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.driver == null ? 0 : this.driver.hashCode () );
        result = prime * result + ( this.interfaceName == null ? 0 : this.interfaceName.hashCode () );
        result = prime * result + ( this.properties == null ? 0 : this.properties.hashCode () );
        result = prime * result + ( this.secondaryTarget == null ? 0 : this.secondaryTarget.hashCode () );
        result = prime * result + ( this.subtargets == null ? 0 : this.subtargets.hashCode () );
        result = prime * result + ( this.target == null ? 0 : this.target.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( ! ( obj instanceof ConnectionInformation ) )
        {
            return false;
        }
        final ConnectionInformation other = (ConnectionInformation)obj;
        if ( this.driver == null )
        {
            if ( other.driver != null )
            {
                return false;
            }
        }
        else if ( !this.driver.equals ( other.driver ) )
        {
            return false;
        }
        if ( this.interfaceName == null )
        {
            if ( other.interfaceName != null )
            {
                return false;
            }
        }
        else if ( !this.interfaceName.equals ( other.interfaceName ) )
        {
            return false;
        }
        if ( this.properties == null )
        {
            if ( other.properties != null )
            {
                return false;
            }
        }
        else if ( !this.properties.equals ( other.properties ) )
        {
            return false;
        }
        if ( this.secondaryTarget == null )
        {
            if ( other.secondaryTarget != null )
            {
                return false;
            }
        }
        else if ( !this.secondaryTarget.equals ( other.secondaryTarget ) )
        {
            return false;
        }
        if ( this.subtargets == null )
        {
            if ( other.subtargets != null )
            {
                return false;
            }
        }
        else if ( !this.subtargets.equals ( other.subtargets ) )
        {
            return false;
        }
        if ( this.target == null )
        {
            if ( other.target != null )
            {
                return false;
            }
        }
        else if ( !this.target.equals ( other.target ) )
        {
            return false;
        }
        return true;
    }

}
