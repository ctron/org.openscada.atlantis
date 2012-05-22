/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionInformation implements Cloneable
{
    private final static Logger logger = LoggerFactory.getLogger ( ConnectionInformation.class );

    /*
     * Format:
     *  da:net://target:secondarytarget/subtarget/subsubtarget?property1=value1&property2=value2
     */

    public static final String PROP_PASSWORD = "password";

    public static final String PROP_USER = "user";

    private static final String URI_ENCODING = "utf-8";

    private String interfaceName;

    private String driver;

    private String target;

    private Integer secondaryTarget;

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
                    ci.properties.put ( PROP_USER, URLDecoder.decode ( userInfo[0], URI_ENCODING ) );
                }
                if ( userInfo.length > 1 )
                {
                    ci.properties.put ( PROP_PASSWORD, URLDecoder.decode ( userInfo[1], URI_ENCODING ) );
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
                throw new IllegalArgumentException ( "URI has no interface" );
            }
            if ( ci.getDriver () == null )
            {
                throw new IllegalArgumentException ( "URI has no driver" );
            }

            return ci;
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to decode URI", e );
            return null;
        }
    }

    /**
     * Set the user name
     * 
     * @param user
     *            the user name to set, can be <code>null</code> in order to reset the password
     */
    public void setUser ( final String user )
    {
        if ( user != null )
        {
            this.properties.put ( PROP_USER, user );
        }
        else
        {
            this.properties.remove ( PROP_USER );
        }
    }

    /**
     * Set the password
     * 
     * @param password
     *            the password to set, can be <code>null</code> in order to reset the password
     */
    public void setPassword ( final String password )
    {
        if ( password != null )
        {
            this.properties.put ( PROP_PASSWORD, password );
        }
        else
        {
            this.properties.remove ( PROP_PASSWORD );
        }
    }

    /**
     * Get the password from the properties
     * 
     * @return the password or <code>null</code> if none was set
     */
    public String getPassword ()
    {
        return this.properties.get ( PROP_PASSWORD );
    }

    @Override
    public String toString ()
    {
        try
        {
            final HashMap<String, String> properties = new HashMap<String, String> ( this.properties );

            String userInfo = null;
            final String user = properties.remove ( PROP_USER );
            final String password = properties.remove ( PROP_PASSWORD );
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

            if ( this.target != null )
            {
                uri += URLEncoder.encode ( this.target, URI_ENCODING );
            }
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
            logger.warn ( "Failed to encode URI", e );
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

    @Override
    public ConnectionInformation clone ()
    {
        final ConnectionInformation connectionInformation = new ConnectionInformation ();

        connectionInformation.driver = this.driver;
        connectionInformation.interfaceName = this.interfaceName;
        connectionInformation.properties = new HashMap<String, String> ( this.properties );
        connectionInformation.secondaryTarget = this.secondaryTarget;
        connectionInformation.subtargets = new LinkedList<String> ( this.subtargets );
        connectionInformation.target = this.target;

        return connectionInformation;
    }

    /**
     * Returns a string with the password masked out if one is set.
     * <p>
     * The method actually replaces the password with the mask if it is set and calls {@link #toString()} on the result. The current instance is not altered in the process.
     * </p>
     * 
     * @param mask
     *            The mask to use instead of the password or <code>null</code> if the password should simply be removed
     * @return the masked string
     * @see #toString()
     */
    public String toMaskedString ( final String mask )
    {
        final ConnectionInformation connectionInformation = clone ();
        final String password = connectionInformation.getPassword ();
        if ( password != null )
        {
            connectionInformation.setPassword ( mask );
        }
        return connectionInformation.toString ();
    }

    /**
     * Return a masked string with the default mask
     * 
     * @return a masked string
     * @see #toMaskedString(String)
     */
    public String toMaskedString ()
    {
        return toMaskedString ( "***" );
    }
}
