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

package org.eclipse.scada.da.server.common.impl;

import java.util.Map;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.core.subscription.SubscriptionListener;
import org.eclipse.scada.da.core.server.ItemChangeListener;
import org.eclipse.scada.da.server.common.DataItem;

/**
 * A subscription listener for data items
 * <p>
 * Interface is analog to {@link ItemChangeListener}
 * @author Jens Reimann
 *
 */
public interface DataItemSubscriptionListener extends SubscriptionListener
{
    public void dataChanged ( DataItem item, Variant value, Map<String, Variant> attributes, boolean cache );
}
