/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.snmp;

import org.eclipse.scada.da.server.browser.common.FolderCommon;
import org.eclipse.scada.da.server.common.impl.HiveCommon;
import org.eclipse.scada.da.server.common.item.factory.DefaultChainItemFactory;

/**
 * our default item factory for folders
 * 
 * @author jens Reimann
 */
public class DefaultFolderItemFactory extends DefaultChainItemFactory
{

    public DefaultFolderItemFactory ( final HiveCommon hive, final FolderCommon parentFolder, final String baseId, final String folderName )
    {
        super ( hive, parentFolder, baseId, folderName );
    }

}
