/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassid.de)
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

package org.openscada.sec.authz.signature;

import java.io.InputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.crypto.KeySelector;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.sec.AuthorizationService;
import org.openscada.sec.audit.AuditLogService;
import org.openscada.sec.authz.AuthorizationRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.1
 */
public class SignatureAuthorizationService implements AuthorizationService
{

    private final static Logger logger = LoggerFactory.getLogger ( SignatureAuthorizationService.class );

    private AuditLogService auditLogService;

    private final CertificateFactory cf;

    public void setAuditLogService ( final AuditLogService auditLogService )
    {
        this.auditLogService = auditLogService;
    }

    public SignatureAuthorizationService () throws CertificateException
    {
        this.cf = CertificateFactory.getInstance ( "X.509" );
    }

    @Override
    public synchronized AuthorizationRule createRule ( final Map<String, String> properties ) throws Exception
    {
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( properties );

        final boolean indent = cfg.getBoolean ( "indent", false );

        final RequestSignatureRuleImpl rule = new RequestSignatureRuleImpl ( new SignatureRequestBuilder (), buildRequestValidator ( properties ), this.auditLogService, indent );

        rule.setPreFilter ( properties );

        return rule;
    }

    private RequestValidator buildRequestValidator ( final Map<String, String> properties ) throws Exception
    {
        final KeySelector keySelector = makeKeySelector ( properties );

        return new RequestValidator ( keySelector );
    }

    private KeySelector makeKeySelector ( final Map<String, String> properties ) throws Exception
    {
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( properties );

        final Collection<X509CA> cas = new LinkedList<X509CA> ();

        final Map<String, String> caProperties = cfg.getPrefixed ( "ca." );
        for ( final Map.Entry<String, String> entry : caProperties.entrySet () )
        {
            final String key = entry.getKey ();
            if ( key.equals ( "cert" ) )
            {
                final String caCertFile = entry.getValue ();
                final Collection<X509Certificate> certificates = loadCert ( caCertFile );
                final String crl = caProperties.get ( "crl" );
                final X509CA ca = new X509CA ( certificates, loadCrl ( crl ) );
                cas.add ( ca );
            }
            else if ( key.endsWith ( ".cert" ) )
            {
                final String caCertFile = entry.getValue ();
                final Collection<X509Certificate> certificates = loadCert ( caCertFile );
                final String crl = caProperties.get ( key.substring ( 0, key.length () - ".cert".length () ) ) + ".crl";
                final X509CA ca = new X509CA ( certificates, loadCrl ( crl ) );
                cas.add ( ca );
            }
        }

        if ( cas.isEmpty () )
        {
            throw new IllegalStateException ( "No key selector configuration found" );
        }
        else
        {
            return new X509KeySelector ( cas );
        }
    }

    @SuppressWarnings ( "unchecked" )
    private Collection<X509CRL> loadCrl ( final String crl ) throws Exception
    {
        if ( crl == null )
        {
            return Collections.emptyList ();
        }

        logger.info ( "Loading CA CRL from : {}", crl );

        final InputStream stream = new URL ( crl ).openStream ();
        try
        {
            return (Collection<X509CRL>)this.cf.generateCRLs ( stream );
        }
        finally
        {
            stream.close ();
        }
    }

    @SuppressWarnings ( "unchecked" )
    private Collection<X509Certificate> loadCert ( final String value ) throws Exception
    {
        logger.info ( "Loading CA cert from : {}", value );

        final InputStream stream = new URL ( value ).openStream ();
        try
        {
            return (Collection<X509Certificate>)this.cf.generateCertificates ( stream );
        }
        finally
        {
            stream.close ();
        }
    }
}
