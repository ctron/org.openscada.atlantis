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

import java.security.Key;
import java.security.cert.X509Certificate;

import javax.xml.crypto.KeySelectorResult;

/**
 * @since 1.1
 */
public class X509KeySelectorResult implements KeySelectorResult
{

    private final X509Certificate certificate;

    public X509KeySelectorResult ( final X509Certificate certificate )
    {
        this.certificate = certificate;
    }

    @Override
    public Key getKey ()
    {
        return this.certificate.getPublicKey ();
    }

    public X509Certificate getCertificate ()
    {
        return this.certificate;
    }

}
