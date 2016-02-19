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

import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.scada.utils.lang.Pair;
import org.openscada.opc.xmlda.OpcType;

public class ServerConfiguration
{
    private URL wsdlUrl;

    private URL serverUrl;

    private QName serviceName;

    private String localPart;

    private int connectTimeout = 10_000;

    private int requestTimeout = 10_000;

    private int waitTime = 5_000;

    private Integer samplingRate;

    private boolean pollByRead;

    private List<Pair<String, OpcType>> itemTypes = new LinkedList<> ();

    public ServerConfiguration ( final URL wsdlUrl, final URL serverUrl, final QName serviceName, final String localPart, final int connectTimeout, final int requestTimeout )
    {
        this.wsdlUrl = wsdlUrl;
        this.serverUrl = serverUrl;
        this.serviceName = serviceName;
        this.localPart = localPart;
        this.connectTimeout = connectTimeout;
        this.requestTimeout = requestTimeout;
    }

    public ServerConfiguration ( final URL wsdlUrl, final URL serverUrl, final QName serviceName, final String localPart, final int timeout )
    {
        this.wsdlUrl = wsdlUrl;
        this.serverUrl = serverUrl;
        this.serviceName = serviceName;
        this.localPart = localPart;
        this.connectTimeout = timeout;
        this.requestTimeout = timeout;
    }

    public URL getWsdlUrl ()
    {
        return this.wsdlUrl;
    }

    public void setWsdlUrl ( final URL wsdlUrl )
    {
        this.wsdlUrl = wsdlUrl;
    }

    public URL getServerUrl ()
    {
        return this.serverUrl;
    }

    public void setServerUrl ( final URL serverUrl )
    {
        this.serverUrl = serverUrl;
    }

    public QName getServiceName ()
    {
        return this.serviceName;
    }

    public void setServiceName ( final QName serviceName )
    {
        this.serviceName = serviceName;
    }

    public String getLocalPart ()
    {
        return this.localPart;
    }

    public void setLocalPart ( final String localPart )
    {
        this.localPart = localPart;
    }

    public int getConnectTimeout ()
    {
        return this.connectTimeout;
    }

    public void setConnectTimeout ( final int connectTimeout )
    {
        this.connectTimeout = connectTimeout;
    }

    public int getRequestTimeout ()
    {
        return this.requestTimeout;
    }

    public void setRequestTimeout ( final int requestTimeout )
    {
        this.requestTimeout = requestTimeout;
    }

    public void setWaitTime ( final int waitTime )
    {
        this.waitTime = waitTime;
    }

    public int getWaitTime ()
    {
        return this.waitTime;
    }

    public void setSamplingRate ( final Integer samplingRate )
    {
        this.samplingRate = samplingRate;
    }

    public Integer getSamplingRate ()
    {
        return this.samplingRate;
    }

    public void setPollByRead ( final boolean pollByRead )
    {
        this.pollByRead = pollByRead;
    }

    public boolean isPollByRead ()
    {
        return this.pollByRead;
    }

    public List<Pair<String, OpcType>> getItemTypes ()
    {
        return Collections.unmodifiableList ( itemTypes );
    }

    public void setItemTypes ( List<Pair<String, OpcType>> itemTypes )
    {
        this.itemTypes = new LinkedList<> ( itemTypes );
    }
}
