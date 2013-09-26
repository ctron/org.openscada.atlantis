/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.da.component.script;

import java.util.Map;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.core.server.OperationParameters;
import org.eclipse.scada.da.server.common.AttributeMode;
import org.eclipse.scada.da.server.common.chain.WriteHandler;

public interface ScriptContext
{
    public static interface Item
    {
        public String getItemId ();

        public void updateData ( Variant value, Map<String, Variant> attributes, AttributeMode attributeMode );

        public void dispose ();
    };

    public Item registerItem ( String itemId, Map<String, Variant> attributes, WriteHandler writeHandler );

    public void unregisterItem ( String itemId );

    public void unregisterItem ( Item item );

    public void writeDataItem ( String connectionId, String itemId, Variant value, OperationParameters operationParameters ) throws Exception;

    public Map<String, String> getParameters ();

    public void dispose ();
}
