/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.da.server.common;

import java.util.Map;

import org.eclipse.scada.core.InvalidOperationException;
import org.eclipse.scada.core.NotConvertableException;
import org.eclipse.scada.core.NullValueException;
import org.eclipse.scada.core.OperationException;
import org.eclipse.scada.core.Variant;
import org.eclipse.scada.utils.concurrent.NotifyFuture;
import org.openscada.core.server.OperationParameters;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;

public interface DataItem
{
    public DataItemInformation getInformation ();

    /**
     * The read operation of a data item.
     * 
     * @return The value read from the subsystem.
     * @throws InvalidOperationException
     *             Raised if "read" is not a valid operation for this item
     */
    public NotifyFuture<Variant> readValue () throws InvalidOperationException;

    public Map<String, Variant> getAttributes ();

    /**
     * Sets the listener for this item.
     * Set by the controller to which this item is registered. The item has to
     * use the listener
     * provided.
     * 
     * @param listener
     *            The listener to use or null to disable notification
     */
    public void setListener ( ItemListener listener );

    /**
     * The write operation of a data item.
     * 
     * @param session
     *            The user session
     * @param value
     *            The value to write to the subsystem
     * @param operationParameters
     * @return the future to the operation
     * @throws InvalidOperationException
     *             Raised if "write" is not a valid operation for this item
     * @throws NullValueException
     *             Raised if a null value was passed but the subsystem does not
     *             allow null values to be written
     * @throws NotConvertableException
     *             Raised if a value was passed that cannot be converted in a
     *             variant type suitable for the subsystem
     * @throws OperationException
     *             Raised if the value could not be written due to some
     *             subsystem error
     */
    public NotifyFuture<WriteResult> startWriteValue ( Variant value, OperationParameters operationParameters );

    /**
     * Start the write attributes operation
     * 
     * @param attributes
     *            attributes to set
     * @param session
     *            The user session
     * @param operationParameters
     * @return the future to the operation
     */
    public NotifyFuture<WriteAttributeResults> startSetAttributes ( Map<String, Variant> attributes, OperationParameters operationParameters );
}
