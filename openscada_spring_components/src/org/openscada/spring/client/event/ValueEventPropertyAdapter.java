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

package org.openscada.spring.client.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * A value event adapter which maps value events to method calls
 * @author Jens Reimann
 *
 */
public class ValueEventPropertyAdapter implements ValueEventListener, InitializingBean
{
    private static Logger log = Logger.getLogger ( ValueEventPropertyAdapter.class );

    /**
     * The target object
     */
    protected Object target;

    /**
     * the method to call
     */
    protected Method method;

    /**
     * the method name
     */
    protected String methodName;

    public void valueEvent ( final String topic, final Variant value )
    {
        try
        {
            this.method.invoke ( this.target, value );
        }
        catch ( final IllegalArgumentException e )
        {
            log.error ( "Failed to call target method", e );
        }
        catch ( final IllegalAccessException e )
        {
            log.error ( "Failed to call target method", e );
        }
        catch ( final InvocationTargetException e )
        {
            log.error ( "Failed to call target method", e );
        }
    }

    public void setTarget ( final Object target )
    {
        this.target = target;
        refreshMethod ();
    }

    /**
     * set the method name 
     * @param method
     */
    public void setMethodName ( final String method )
    {
        this.methodName = method;
        refreshMethod ();
    }

    protected void refreshMethod ()
    {
        this.method = null;
        if ( this.target != null && this.methodName != null )
        {
            try
            {
                this.method = this.target.getClass ().getMethod ( this.methodName, Variant.class );
            }
            catch ( final SecurityException e )
            {
                throw new RuntimeException ( "Failed to set method", e );
            }
            catch ( final NoSuchMethodException e )
            {
                throw new RuntimeException ( "No such method", e );
            }
        }
    }

    public void afterPropertiesSet () throws Exception
    {
        Assert.notNull ( this.target, "'target' must not be null" );
        Assert.notNull ( this.method, "'method' name must not be empty" );
    }

}
