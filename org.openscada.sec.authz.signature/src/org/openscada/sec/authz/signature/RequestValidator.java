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

import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * @since 1.1
 */
public class RequestValidator
{

    private final XMLSignatureFactory factory;

    private final KeySelector keySelector;

    public RequestValidator ( final KeySelector keySelector )
    {
        this.factory = XMLSignatureFactory.getInstance ( "DOM" ); //$NON-NLS-1$
        this.keySelector = keySelector;
    }

    public static class Result
    {
        public static Result INVALID = new Result ( false );

        private final boolean valid;

        private final KeySelectorResult keySelectorResult;

        public Result ( final boolean valid )
        {
            this.valid = valid;
            this.keySelectorResult = null;
        }

        public Result ( final boolean valid, final KeySelectorResult keySelectorResult )
        {
            this.valid = valid;
            this.keySelectorResult = keySelectorResult;
        }

        public boolean isValid ()
        {
            return this.valid;
        }

        public KeySelectorResult getKeySelectorResult ()
        {
            return this.keySelectorResult;
        }
    }

    public Result validate ( final Document doc ) throws Exception
    {
        final NodeList nl = doc.getElementsByTagNameNS ( XMLSignature.XMLNS, "Signature" ); //$NON-NLS-1$

        if ( nl.getLength () == 0 )
        {
            return Result.INVALID;
        }

        final DOMValidateContext dvc = new DOMValidateContext ( this.keySelector, nl.item ( 0 ) );

        final XMLSignature signature = this.factory.unmarshalXMLSignature ( dvc );

        try
        {
            final boolean result = signature.validate ( dvc );

            return new Result ( result, signature.getKeySelectorResult () );
        }
        catch ( final XMLSignatureException e )
        {
            return Result.INVALID;
        }
    }

}
