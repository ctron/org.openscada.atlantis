/*******************************************************************************
 * Copyright (c) 2015 IBH SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBH SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.openscada.da.server.opc.xmlda;

import org.openscada.opc.xmlda.requests.ServerState;

public class ServerStateInformation
{
    private ServerState state;

    private String productVersion;

    private String vendorInformation;

    private String startTime;

    private String information;

    public String getProductVersion ()
    {
        return this.productVersion;
    }

    public void setProductVersion ( final String productVersion )
    {
        this.productVersion = productVersion;
    }

    public String getVendorInformation ()
    {
        return this.vendorInformation;
    }

    public void setVendorInformation ( final String vendorInformation )
    {
        this.vendorInformation = vendorInformation;
    }

    public String getStartTime ()
    {
        return this.startTime;
    }

    public void setStartTime ( final String startTime )
    {
        this.startTime = startTime;
    }

    public String getInformation ()
    {
        return this.information;
    }

    public void setInformation ( final String information )
    {
        this.information = information;
    }

    public void setState ( final ServerState state )
    {
        this.state = state;
    }

    public ServerState getState ()
    {
        return this.state;
    }
}
