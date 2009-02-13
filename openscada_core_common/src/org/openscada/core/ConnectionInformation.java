/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

    private String _interface = null;

    private String _driver = null;

    private String _target = null;

    private Integer _secondaryTarget = null;

    private List<String> _subtargets = new LinkedList<String> ();

    private Map<String, String> _properties = new HashMap<String, String> ();

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
                {
                    path = path.substring ( 1 );
                }

                if ( path.length () > 0 )
                {
                    ci._subtargets = new LinkedList<String> ( Arrays.asList ( path.split ( "\\/" ) ) );
                }
            }

            // parse user info
            if ( subUri.getUserInfo () != null )
            {
                final String[] userInfo = subUri.getRawUserInfo ().split ( "\\:" );
                if ( userInfo.length > 0 )
                {
                    ci._properties.put ( "user", URLDecoder.decode ( userInfo[0], "utf-8" ) );
                }
                if ( userInfo.length > 1 )
                {
                    ci._properties.put ( "password", URLDecoder.decode ( userInfo[1], "utf-8" ) );
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
                            final String key = URLDecoder.decode ( m.group ( 1 ), "utf-8" );
                            final String value = URLDecoder.decode ( m.group ( 2 ), "utf-8" );
                            ci._properties.put ( key, value );
                        }
                        else
                        {
                            ci._properties.put ( URLDecoder.decode ( pair, "utf-8" ), "" );
                        }
                    }
                }
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
            String userInfo = null;
            final String user = this._properties.get ( "user" );
            final String password = this._properties.get ( "password" );
            String subtargets = null;
            String query = null;

            // prepare subtargets
            if ( this._subtargets.size () > 0 )
            {
                subtargets = "";
                for ( final String subtarget : this._subtargets )
                {
                    subtargets += "/";
                    subtargets += URLEncoder.encode ( subtarget, "utf-8" );
                }
            }

            // perpare properties
            this._properties.remove ( "user" );
            this._properties.remove ( "password" );
            for ( final Map.Entry<String, String> entry : this._properties.entrySet () )
            {
                if ( query == null )
                {
                    query = "?";
                }
                else
                {
                    query += "&";
                }
                query += URLEncoder.encode ( entry.getKey (), "utf-8" );
                query += "=";
                query += URLEncoder.encode ( entry.getValue (), "utf-8" );
            }

            // prepare user info
            if ( user != null && password != null )
            {
                userInfo = URLEncoder.encode ( user, "utf-8" ) + ":" + URLEncoder.encode ( password, "utf-8" );
            }
            else if ( user != null )
            {
                userInfo = URLEncoder.encode ( user, "utf-8" );
            }

            String uri = "";

            uri += URLEncoder.encode ( this._interface, "utf-8" ) + ":" + URLEncoder.encode ( this._driver, "utf-8" ) + "://";
            if ( userInfo != null )
            {
                uri += userInfo + "@";
            }

            uri += URLEncoder.encode ( this._target, "utf-8" );
            if ( this._secondaryTarget != null )
            {
                uri += ":" + this._secondaryTarget;
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
        return this._driver;
    }

    public void setDriver ( final String driver )
    {
        this._driver = driver;
    }

    public String getInterface ()
    {
        return this._interface;
    }

    public void setInterface ( final String interface1 )
    {
        this._interface = interface1;
    }

    public Integer getSecondaryTarget ()
    {
        return this._secondaryTarget;
    }

    public void setSecondaryTarget ( final Integer secondaryTarget )
    {
        this._secondaryTarget = secondaryTarget;
    }

    public List<String> getSubtargets ()
    {
        return this._subtargets;
    }

    public void setSubtargets ( final List<String> subtargets )
    {
        this._subtargets = subtargets;
    }

    public String getTarget ()
    {
        return this._target;
    }

    public void setTarget ( final String target )
    {
        this._target = target;
    }

    public Map<String, String> getProperties ()
    {
        return this._properties;
    }

    public void setProperties ( final Map<String, String> properties )
    {
        this._properties = properties;
    }

    public boolean isValid ()
    {
        return this._driver != null && this._interface != null && this._properties != null && this._subtargets != null && this._target != null;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this._driver == null ? 0 : this._driver.hashCode () );
        result = prime * result + ( this._interface == null ? 0 : this._interface.hashCode () );
        result = prime * result + ( this._properties == null ? 0 : this._properties.hashCode () );
        result = prime * result + ( this._secondaryTarget == null ? 0 : this._secondaryTarget.hashCode () );
        result = prime * result + ( this._subtargets == null ? 0 : this._subtargets.hashCode () );
        result = prime * result + ( this._target == null ? 0 : this._target.hashCode () );
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
        if ( this._driver == null )
        {
            if ( other._driver != null )
            {
                return false;
            }
        }
        else if ( !this._driver.equals ( other._driver ) )
        {
            return false;
        }
        if ( this._interface == null )
        {
            if ( other._interface != null )
            {
                return false;
            }
        }
        else if ( !this._interface.equals ( other._interface ) )
        {
            return false;
        }
        if ( this._properties == null )
        {
            if ( other._properties != null )
            {
                return false;
            }
        }
        else if ( !this._properties.equals ( other._properties ) )
        {
            return false;
        }
        if ( this._secondaryTarget == null )
        {
            if ( other._secondaryTarget != null )
            {
                return false;
            }
        }
        else if ( !this._secondaryTarget.equals ( other._secondaryTarget ) )
        {
            return false;
        }
        if ( this._subtargets == null )
        {
            if ( other._subtargets != null )
            {
                return false;
            }
        }
        else if ( !this._subtargets.equals ( other._subtargets ) )
        {
            return false;
        }
        if ( this._target == null )
        {
            if ( other._target != null )
            {
                return false;
            }
        }
        else if ( !this._target.equals ( other._target ) )
        {
            return false;
        }
        return true;
    }

}
