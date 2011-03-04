/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.exporter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.openscada.da.core.server.Hive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

/**
 * Create a new hive by creating a new object.
 * @author Jens Reimann
 *
 */
public class NewInstanceHiveFactory implements HiveFactory
{

    private final static Logger logger = LoggerFactory.getLogger ( NewInstanceHiveFactory.class );

    @Override
    public Hive createHive ( final String reference, final HiveConfigurationType configuration ) throws ConfigurationException
    {
        Node subNode = null;
        if ( configuration != null )
        {
            for ( int i = 0; i < configuration.getDomNode ().getChildNodes ().getLength (); i++ )
            {
                final Node node = configuration.getDomNode ().getChildNodes ().item ( i );
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
        catch ( final Throwable e )
        {
            throw new ConfigurationException ( "Failed to initialze hive using new instance", e );
        }
    }

    protected static Hive createInstance ( final String hiveClassName, final Node node ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        final Class<?> hiveClass = Class.forName ( hiveClassName );

        Constructor<?> ctor = null;

        if ( node != null )
        {
            logger.debug ( "We have an xml configuration node. try XML-Node ctor" );
            // if we have an xml configuration node try to use the XML ctor
            try
            {
                ctor = hiveClass.getConstructor ( Node.class );
                if ( ctor != null )
                {
                    logger.debug ( "Using XML-Node constructor" );
                    return (Hive)ctor.newInstance ( new Object[] { node } );
                }
                // fall back to standard ctor
                logger.debug ( "No XML-Node ctor found .. fall back to default" );
            }
            catch ( final InvocationTargetException e )
            {
                logger.info ( "Failed to create new instance", e.getTargetException () );
                throw e;
            }
            catch ( final Throwable e )
            {
                logger.info ( String.format ( "No XML node constructor found (%s)", e.getMessage () ) );
            }
        }
        return (Hive)hiveClass.newInstance ();
    }

}
