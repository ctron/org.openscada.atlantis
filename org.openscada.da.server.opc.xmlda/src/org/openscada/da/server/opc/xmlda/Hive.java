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
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.eclipse.scada.ca.ConfigurationAdministrator;
import org.eclipse.scada.ca.ConfigurationDataHelper;
import org.eclipse.scada.da.server.browser.common.FolderCommon;
import org.eclipse.scada.da.server.common.DataItem;
import org.eclipse.scada.da.server.common.ValidationStrategy;
import org.openscada.da.server.common.AbstractWriteHandlerHive;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

public class Hive extends AbstractWriteHandlerHive
{
    private static final String DATA_DELIM_PATTERN = Pattern.quote ( ".data" + ServerConnection.DATA_DELIM );

    private final FolderCommon rootFolder;

    private final ConfigurationFactory configurator;

    private final BundleContext context;

    private ServiceRegistration<org.eclipse.scada.ca.ConfigurationFactory> handle;

    private final Map<String, ServerConnection> servers = new HashMap<> ();

    public Hive ()
    {
        this ( FrameworkUtil.getBundle ( Hive.class ).getBundleContext () );
    }

    public Hive ( final BundleContext context )
    {
        super ( context );

        this.context = context;

        setValidatonStrategy ( ValidationStrategy.GRANT_ALL );

        this.rootFolder = new FolderCommon ();
        setRootFolder ( this.rootFolder );

        this.configurator = new ConfigurationFactory ( this );
    }

    @Override
    public String getHiveId ()
    {
        return Hive.class.getPackage ().getName ();
    }

    @Override
    protected void performStart () throws Exception
    {
        super.performStart ();

        final Dictionary<String, Object> properties = new Hashtable<> ( 2 );
        properties.put ( ConfigurationAdministrator.FACTORY_ID, "org.openscada.da.server.opc.xmlda.server" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "An OPC XML DA server connection" );
        this.handle = this.context.registerService ( org.eclipse.scada.ca.ConfigurationFactory.class, this.configurator, properties );
    }

    @Override
    protected void performStop () throws Exception
    {
        this.handle.unregister ();
        super.performStop ();
    }

    public ServerConnection addServer ( final String configurationId, final Map<String, String> parameters ) throws Exception
    {
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        final URL url = new URL ( cfg.getStringNonEmptyChecked ( "url", "'url' must be set to a valid server URL" ) );
        final String wsdl = cfg.getStringNonEmpty ( "wsdlUrl" );
        final URL wsdlUrl = wsdl != null ? new URL ( wsdl ) : null;

        final String namespace = cfg.getString ( "namespace", "http://opcfoundation.org/webservices/XMLDA/1.0/" );

        final String serviceName = cfg.getStringNonEmptyChecked ( "serviceName", "'serviceName' must be set to the name of the SOAP service" );
        final String portName = cfg.getString ( "portName", serviceName + "Soap" );

        final int timeout = cfg.getInteger ( "timeout", 10_000 );
        final int connectTimeout = cfg.getInteger ( "connectTimeout", timeout );
        final int requestTimeout = cfg.getInteger ( "requestTimeout", timeout );

        final ServerConnection service = new ServerConnection ( configurationId, wsdlUrl, url, new QName ( namespace, serviceName ), portName, connectTimeout, requestTimeout, this, this.rootFolder );

        synchronized ( this )
        {
            // perform add
            final ServerConnection oldService = this.servers.put ( configurationId, service );
            if ( oldService != null )
            {
                oldService.dispose ();
            }
        }

        service.start ();

        return service;
    }

    @Override
    public DataItem lookupItem ( final String id )
    {
        final String[] toks = splitDataItem ( id );
        if ( toks == null )
        {
            return super.lookupItem ( id );
        }

        return getDataItem ( toks[0], toks[1] );
    }

    protected String[] splitDataItem ( final String id )
    {
        final String[] toks = id.split ( DATA_DELIM_PATTERN, 2 );
        if ( toks.length == 2 )
        {
            return toks;
        }
        return null;
    }

    protected DataItem getDataItem ( final String connectionId, final String remoteId )
    {
        final ServerConnection server;

        synchronized ( this )
        {
            server = this.servers.get ( connectionId );
            if ( server == null )
            {
                return null;
            }
        }

        return server.getRemoteDataItem ( remoteId );
    }

    public void removeServer ( final String configurationId )
    {
        final ServerConnection server;

        synchronized ( this )
        {
            server = this.servers.remove ( configurationId );
        }
        if ( server != null )
        {
            server.dispose ();
        }
    }

}
