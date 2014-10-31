/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.snmp.mib;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.server.browser.common.FolderCommon;
import org.eclipse.scada.utils.collection.MapBuilder;
import org.openscada.da.snmp.configuration.MibsType;

public interface MibManager
{

    public void fillAttributes ( String oid, MapBuilder<String, Variant> attributes );

    public void buildMIBFolders ( FolderCommon mibFolder );

    public void configure ( MibsType mibs );

}