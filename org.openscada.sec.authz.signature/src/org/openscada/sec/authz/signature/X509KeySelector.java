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

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.1
 */
public class X509KeySelector extends KeySelector
{

    private final static Logger logger = LoggerFactory.getLogger ( X509KeySelector.class );

    private final X509CA[] cas;

    public X509KeySelector ( final X509CA ca )
    {
        this ( Collections.singleton ( ca ) );
    }

    public X509KeySelector ( final Collection<X509CA> cas )
    {
        this.cas = cas.toArray ( new X509CA[cas.size ()] );
    }

    @Override
    public KeySelectorResult select ( final KeyInfo keyInfo, final KeySelector.Purpose purpose, final AlgorithmMethod method, final XMLCryptoContext context ) throws KeySelectorException
    {
        if ( keyInfo == null )
        {
            throw new KeySelectorException ( "Null KeyInfo object!" );
        }

        final SignatureMethod sm = (SignatureMethod)method;
        final List<?> list = keyInfo.getContent ();

        for ( final Object l : list )
        {
            final XMLStructure xmlStructure = (XMLStructure)l;
            if ( xmlStructure instanceof X509Data )
            {
                for ( final Object o : ( (X509Data)xmlStructure ).getContent () )
                {
                    KeySelectorResult result = null;
                    if ( o instanceof X509Certificate )
                    {
                        result = findPublicKey ( (X509Certificate)o, sm );
                    }

                    if ( result != null )
                    {
                        return result;
                    }
                }
            }
        }
        throw new KeySelectorException ( "No KeyValue element found!" );
    }

    private KeySelectorResult findPublicKey ( final X509Certificate cert, final SignatureMethod sm )
    {
        try
        {
            final PublicKey pk = cert.getPublicKey ();

            if ( pk == null || !algEquals ( sm.getAlgorithm (), pk.getAlgorithm () ) )
            {
                return null;
            }

            cert.checkValidity ();

            for ( final X509CA ca : this.cas )
            {
                if ( ca.isRevoked ( cert ) )
                {
                    logger.trace ( "Cert is revoked by CA" );
                    continue;
                }

                for ( final X509Certificate caCert : ca.getCertificates () )
                {
                    try
                    {
                        caCert.checkValidity ();

                        // FIXME: validate CA chain
                        cert.verify ( caCert.getPublicKey () );
                        return new X509KeySelectorResult ( cert );
                    }
                    catch ( final Exception e )
                    {
                        // try next
                    }
                }

            }

        }
        catch ( final Exception e )
        {
            logger.trace ( "Failed to select key", e );
        }

        return null;
    }

    static boolean algEquals ( final String algURI, final String algName )
    {
        if ( algName.equalsIgnoreCase ( "DSA" ) && algURI.equalsIgnoreCase ( SignatureMethod.DSA_SHA1 ) )
        {
            return true;
        }
        else if ( algName.equalsIgnoreCase ( "RSA" ) && algURI.equalsIgnoreCase ( SignatureMethod.RSA_SHA1 ) )
        {
            return true;
        }
        else
        {
            logger.trace ( "Failed to check key - algUri: {}, algName: {}", algURI, algName );
            return false;
        }
    }
}