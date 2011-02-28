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

package org.openscada.ae.server.storage.jdbc.internal;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

/**
 * This derived {@link LocalSessionFactoryBean} is needed because hibernate treats
 * a schema which contains only of an empty space just like any other name. So this
 * implementation removes the property <code>hibernate.default_schema</code> if
 * it is empty
 * 
 * @author jrose
 */
public class CustomSessionFactoryBean extends LocalSessionFactoryBean
{

    public CustomSessionFactoryBean ()
    {
        super ();
    }

    @Override
    protected void postProcessConfiguration ( final Configuration config ) throws HibernateException
    {
        super.postProcessConfiguration ( config );
        if ( config.getProperty ( "hibernate.default_schema" ) != null && "".equals ( config.getProperty ( "hibernate.default_schema" ).trim () ) )
        {
            config.getProperties ().remove ( "hibernate.default_schema" );
        }
    }
}
