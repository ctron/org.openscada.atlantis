/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common.chain.storage;

import java.util.Map;
import java.util.Set;

import org.openscada.core.Variant;
import org.openscada.da.server.common.HiveService;

public interface ChainStorageService extends HiveService
{
    public static final String SERVICE_ID = "chainStorageService";

    public abstract void storeValues ( String itemId, Map<String, Variant> values );

    public abstract Map<String, Variant> loadValues ( String itemId, Set<String> valueNames );
}
