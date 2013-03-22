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
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.1
 */
public class X509CA
{

    private final static Logger logger = LoggerFactory.getLogger ( X509CA.class );

    private volatile X509Certificate[] certificates;

    private volatile X509CRL[] crls;

    private final String certificateUrl;

    private final String crlUrl;

    private final CertificateFactory certificateFactory;

    public X509CA ( final CertificateFactory cf, final String certificateUrl, final String crlUrl )
    {
        this.certificateFactory = cf;
        this.certificateUrl = certificateUrl;
        this.crlUrl = crlUrl;

        this.certificates = new X509Certificate[0];
        this.crls = new X509CRL[0];
    }

    public void load () throws Exception
    {
        final Collection<X509Certificate> certificates = loadCert ( this.certificateUrl );
        final Collection<X509CRL> crls = loadCrl ( this.crlUrl );

        this.certificates = certificates.toArray ( new X509Certificate[certificates.size ()] );
        this.crls = crls.toArray ( new X509CRL[crls.size ()] );

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
            return (Collection<X509CRL>)this.certificateFactory.generateCRLs ( stream );
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
            return (Collection<X509Certificate>)this.certificateFactory.generateCertificates ( stream );
        }
        finally
        {
            stream.close ();
        }
    }

    public X509Certificate[] getCertificates ()
    {
        return this.certificates;
    }

    public X509CRL[] getCrls ()
    {
        return this.crls;
    }

    public boolean isRevoked ( final X509Certificate cert )
    {
        for ( final X509CRL crl : this.crls )
        {
            if ( crl.isRevoked ( cert ) )
            {
                return true;
            }
        }
        return false;
    }

    public boolean isValid ()
    {
        for ( final X509Certificate cert : this.certificates )
        {
            try
            {
                cert.checkValidity ();
                return true;
            }
            catch ( final Exception e )
            {
            }

        }
        return false;
    }
}
