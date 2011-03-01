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

package org.openscada.da.server.spring;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.chain.AttributeBinder;
import org.openscada.da.server.common.chain.BaseChainItemCommon;
import org.openscada.da.server.common.chain.VariantBinder;

public class TestErrorChainItem extends BaseChainItemCommon
{

    private final AttributeBinder testErrorFlagBinder = new VariantBinder ( Variant.NULL );

    public TestErrorChainItem ()
    {
        super ( null );
        addBinder ( "test.error", this.testErrorFlagBinder );
    }

    @Override
    public boolean isPersistent ()
    {
        return false;
    }

    @Override
    public Variant process ( final Variant value, final Map<String, Variant> attributes )
    {
        // do nothing

        // add my attributes
        addAttributes ( attributes );

        return null;
    }
}
