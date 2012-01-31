/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.common.exporter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.item.factory.ItemFactory;

public class StaticObjectExporter<T> extends AbstractObjectExporter
{
    private T target;

    private HashMap<String, Variant> additionalAttributes;

    public StaticObjectExporter ( final ItemFactory itemFactory, final Class<T> modelClazz, final boolean readOnly, final boolean nullIsError )
    {
        super ( itemFactory, readOnly, nullIsError );

        createDataItems ( modelClazz );
    }

    public synchronized void setTarget ( final T target, final Map<String, Variant> attributes )
    {
        this.target = target;
        applyAttributes ( attributes );
        updateItemsFromTarget ();
    }

    public synchronized void setTarget ( final T target )
    {
        this.target = target;
        updateItemsFromTarget ();
    }

    public synchronized void setAttributes ( final Map<String, Variant> attributes )
    {
        applyAttributes ( attributes );

        // refresh
        updateItemsFromTarget ();
    }

    private void applyAttributes ( final Map<String, Variant> attributes )
    {
        if ( attributes == null )
        {
            this.additionalAttributes = null;
        }
        else
        {
            this.additionalAttributes = new HashMap<String, Variant> ( attributes );
        }
    }

    @Override
    protected Map<String, Variant> getAdditionalAttributes ()
    {
        if ( this.additionalAttributes == null )
        {
            return null;
        }
        else
        {
            return Collections.unmodifiableMap ( this.additionalAttributes );
        }
    }

    @Override
    protected Object getTarget ()
    {
        return this.target;
    }

}
