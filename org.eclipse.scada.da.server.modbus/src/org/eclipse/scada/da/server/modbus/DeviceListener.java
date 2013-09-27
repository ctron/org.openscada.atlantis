/*******************************************************************************
 * Copyright (c) 2013 TH4 SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     TH4 SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.scada.da.server.modbus;

import org.eclipse.scada.da.server.modbus.io.message.RequestWrapper;
import org.eclipse.scada.da.server.modbus.io.message.ResponseWrapper;

public interface DeviceListener
{
    public void onMessageSent ( final RequestWrapper requestWrapper );

    public void onMessageReceived ( final ResponseWrapper responseWrapper );

    public void onError ( final Throwable cause );

    public void messageQueueEmpty ();
}
