/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.eclipse.scada.ae.monitor.common;

import org.eclipse.scada.ae.Event;
import org.eclipse.scada.ae.Event.EventBuilder;
import org.eclipse.scada.ae.utils.AbstractBaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractConfiguration extends AbstractBaseConfiguration
{
    final static Logger logger = LoggerFactory.getLogger ( AbstractConfiguration.class );

    final AbstractStateMonitor monitor;

    public AbstractConfiguration ( final AbstractConfiguration currentConfiguration, final AbstractStateMonitor monitor )
    {
        super ( currentConfiguration );
        this.monitor = monitor;
    }

    @Override
    protected void sendEvent ( final Event event )
    {
        // configuration events should always we recorded, force them
        this.monitor.sendEvent ( event, true );
    }

    @Override
    protected void injectEventAttributes ( final EventBuilder builder )
    {
        this.monitor.injectEventAttributes ( builder );
    }

}