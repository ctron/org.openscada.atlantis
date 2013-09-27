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

import java.util.Queue;

import org.eclipse.scada.da.server.io.common.BaseDevice;
import org.eclipse.scada.da.server.modbus.io.message.RequestWrapper;

public interface Device extends BaseDevice
{
    void addDeviceListener ( DeviceListener deviceListener );

    void removeDeviceListener ( DeviceListener deviceListener );

    void sendMessages ( Queue<RequestWrapper> requestQueue );
}
