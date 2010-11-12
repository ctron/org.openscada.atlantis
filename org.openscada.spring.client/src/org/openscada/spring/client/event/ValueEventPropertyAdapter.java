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
