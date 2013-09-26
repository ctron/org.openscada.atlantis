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

package org.eclipse.scada.core.subscription;

import java.util.Collection;

/**
 * A event source which can be used with the subscription manager.
 * @author Jens Reimann
 *
 */
public interface SubscriptionSource
{
    /**
     * Validate if the provided subcription information can bind to this subscription source
     * @param information The information to check
     * @return <code>true</code> if the listener can bind to this event source. In this case the {@link #addListener(Collection)}
     * method may not reject the listener.
     */
    public abstract boolean supportsListener ( SubscriptionInformation information );

    public abstract void addListener ( Collection<SubscriptionInformation> listeners );

    public abstract void removeListener ( Collection<SubscriptionInformation> listeners );
}
