/*******************************************************************************
 * Copyright (c) 2013 IBH SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBH SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.openscada.da.server.common.memory;

public interface MemoryDevice
{
    public void writeBit ( int globalAddress, int subIndex, boolean value );

    public void writeFloat ( int globalAddress, float value );

    public void writeDoubleInteger ( int globalAddress, int value );

    public void writeWord ( int globalAddress, short value );

    public void writeByte ( int globalAddress, byte value );
}
