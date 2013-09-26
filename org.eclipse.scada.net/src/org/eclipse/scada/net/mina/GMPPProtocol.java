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

package org.eclipse.scada.net.mina;

public interface GMPPProtocol
{

    public final static int VT_STRING = 0x000000001;

    public final static int VT_LONG = 0x000000002;

    public final static int VT_DOUBLE = 0x000000003;

    public final static int VT_VOID = 0x000000004;

    public final static int VT_INTEGER = 0x000000005;

    public final static int VT_LIST = 0x000000006;

    public final static int VT_MAP = 0x000000007;

    public final static int VT_BOOLEAN = 0x000000008;

    public final int HEADER_SIZE = 4 + 8 + 8 + 8 + 4;

}
