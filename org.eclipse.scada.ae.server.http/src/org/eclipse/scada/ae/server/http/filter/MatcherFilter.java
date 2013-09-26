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

package org.eclipse.scada.ae.server.http.filter;

import org.eclipse.scada.ae.Event;
import org.eclipse.scada.ae.filter.EventMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatcherFilter extends FilterEntry
{
    private final static Logger logger = LoggerFactory.getLogger ( MatcherFilter.class );

    private final EventMatcher eventMatcher;

    public MatcherFilter ( final String id, final long priority, final EventMatcher eventMatcher )
    {
        super ( id, priority );
        this.eventMatcher = eventMatcher;
    }

    @Override
    public boolean matches ( final Event event )
    {
        final boolean result = this.eventMatcher.matches ( event );

        logger.trace ( "Matching filter - event: {}, result: {}", event, result );

        return result;
    }

}
