/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.core;

import org.junit.Assert;
import org.junit.Test;

public class VariantTest
{

    /**
     * Assert two variants in both directions
     * 
     * @param v1
     *            value one
     * @param v2
     *            value two
     * @param equal
     *            expected to be equal
     */
    private void compareVariant ( final Variant v1, final Variant v2, final boolean equal )
    {
        if ( equal )
        {
            Assert.assertTrue ( v1.equals ( v2 ) );
            Assert.assertTrue ( v2.equals ( v1 ) );
        }
        else
        {
            Assert.assertFalse ( v1.equals ( v2 ) );
            Assert.assertFalse ( v2.equals ( v1 ) );
        }
    }

    @Test
    public void testVariantCompareNullSame () throws Exception
    {
        final Variant nullValue = new Variant ();

        compareVariant ( nullValue, nullValue, true );
    }

    @Test
    public void testVariantCompareNullDifferent () throws Exception
    {
        final Variant nullValue1 = new Variant ();
        final Variant nullValue2 = new Variant ();

        compareVariant ( nullValue1, nullValue2, true );

    }

    @Test
    public void testVariantCompareIntEqual () throws Exception
    {
        final Variant v1 = new Variant ( 1 );
        final Variant v2 = new Variant ( 1 );

        compareVariant ( v1, v2, true );
    }

    @Test
    public void testVariantCompareIntDifferent () throws Exception
    {
        final Variant v1 = new Variant ( 1 );
        final Variant v2 = new Variant ( 2 );

        compareVariant ( v1, v2, false );
    }

    @Test
    public void testVariantCompareLongEqual () throws Exception
    {
        final Variant v1 = new Variant ( (long)1 );
        final Variant v2 = new Variant ( (long)1 );

        compareVariant ( v1, v2, true );
    }

    @Test
    public void testVariantCompareLongDifferent () throws Exception
    {
        final Variant v1 = new Variant ( (long)1 );
        final Variant v2 = new Variant ( (long)2 );

        compareVariant ( v1, v2, false );
    }

    @Test
    public void testVariantCompareStringEqual () throws Exception
    {
        final Variant v1 = new Variant ( "test" );
        final Variant v2 = new Variant ( "test" );

        compareVariant ( v1, v2, true );
    }

    @Test
    public void testVariantCompareStringDifferent () throws Exception
    {
        final Variant v1 = new Variant ( "test1" );
        final Variant v2 = new Variant ( "test2" );

        compareVariant ( v1, v2, false );
    }

    @Test
    public void testVariantCompareIntVSLongEqual () throws Exception
    {
        final Variant v1 = new Variant ( 1 );
        final Variant v2 = new Variant ( (long)1 );

        compareVariant ( v1, v2, true );
    }

    @Test
    public void testVariantCompareIntVSLongDifferent () throws Exception
    {
        final Variant v1 = new Variant ( 1 );
        final Variant v2 = new Variant ( (long)2 );

        compareVariant ( v1, v2, false );
    }

    @Test
    public void testVariantCompareIntVSDoubleEqual () throws Exception
    {
        final Variant v1 = new Variant ( 1 );
        final Variant v2 = new Variant ( 1.0 );

        compareVariant ( v1, v2, true );
    }

    @Test
    public void testVariantCompareIntVSDoubleDifferent () throws Exception
    {
        final Variant v1 = new Variant ( 1 );
        final Variant v2 = new Variant ( 1.5 );

        compareVariant ( v1, v2, false );
    }

    @Test
    public void testVariantCompareStringVSDoubleEqual () throws Exception
    {
        final Variant v1 = new Variant ( "1" );
        final Variant v2 = new Variant ( 1.0 );

        compareVariant ( v1, v2, true );
    }

    @Test
    public void testVariantCompareStringVSDoubleDifferent () throws Exception
    {
        final Variant v1 = new Variant ( "1" );
        final Variant v2 = new Variant ( 1.5 );

        compareVariant ( v1, v2, false );
    }

    @Test
    public void testVariantCompareStringVSBooleanEqual () throws Exception
    {
        Variant v1 = new Variant ( "1" );
        Variant v2 = new Variant ( true );

        compareVariant ( v1, v2, true );

        v1 = new Variant ( "0" );
        v2 = new Variant ( false );

        compareVariant ( v1, v2, true );

        v1 = new Variant ( "" );
        v2 = new Variant ( false );

        compareVariant ( v1, v2, true );

        v1 = new Variant ( "true" );
        v2 = new Variant ( true );

        compareVariant ( v1, v2, true );

        v1 = new Variant ( "false" );
        v2 = new Variant ( false );

        compareVariant ( v1, v2, true );
    }

    @Test
    public void testVariantCompareStringVSBooleanDifferent () throws Exception
    {
        Variant v1 = new Variant ( "1" );
        Variant v2 = new Variant ( false );

        compareVariant ( v1, v2, false );

        v1 = new Variant ( "0" );
        v2 = new Variant ( true );

        compareVariant ( v1, v2, false );

        v1 = new Variant ( "true" );
        v2 = new Variant ( false );

        compareVariant ( v1, v2, false );

        v1 = new Variant ( "false" );
        v2 = new Variant ( true );

        compareVariant ( v1, v2, false );
    }

    @Test
    public void testVariantCompareBooleanVSBooleanEqual () throws Exception
    {
        final Variant v1 = new Variant ( false );
        final Variant v2 = new Variant ( false );

        compareVariant ( v1, v2, true );
    }

    @Test
    public void testVariantCompareBooleanVSBooleanDifferent () throws Exception
    {
        final Variant v1 = new Variant ( true );
        final Variant v2 = new Variant ( false );

        compareVariant ( v1, v2, false );
    }

    @Test
    public void testVariantCompareBooleanVSNullEqual1 () throws Exception
    {
        final Variant v1 = new Variant ( false );
        final Variant v2 = new Variant ();

        compareVariant ( v1, v2, false );
    }

    @Test
    public void testVariantCompareBooleanVSNullEqual2 () throws Exception
    {
        final Variant v1 = new Variant ( true );
        final Variant v2 = new Variant ();

        compareVariant ( v1, v2, false );
    }

    @Test
    public void testVariantCompareNullVSNullEqual () throws Exception
    {
        final Variant v1 = new Variant ();
        final Variant v2 = new Variant ();

        compareVariant ( v1, v2, true );
    }

    @Test
    public void testVariantCompareBooleanVSLongEqual () throws Exception
    {
        final Variant v1 = new Variant ( true );
        final Variant v2 = new Variant ( (long)-2000 );

        compareVariant ( v1, v2, true );
    }

    @Test
    public void testVariantCompareBooleanVSLongDifferent () throws Exception
    {
        final Variant v1 = new Variant ( true );
        final Variant v2 = new Variant ( (long)0 );

        compareVariant ( v1, v2, false );
    }

    @Test
    public void testVariantCompareBooleanVSDoubleEqual () throws Exception
    {
        Variant v1 = new Variant ( false );
        Variant v2 = new Variant ( 0.0 );

        compareVariant ( v1, v2, true );

        v1 = new Variant ( true );
        v2 = new Variant ( -1.0 );

        compareVariant ( v1, v2, true );
    }

    @Test
    public void testVariantCompareBooleanVSDoubleDifferent () throws Exception
    {
        Variant v1 = new Variant ( false );
        Variant v2 = new Variant ( 0.1 );

        compareVariant ( v1, v2, false );

        v1 = new Variant ( true );
        v2 = new Variant ( 0.0 );

        compareVariant ( v1, v2, false );
    }

    @Test
    public void testVariantCompareIntegerVSDoubleEqual () throws Exception
    {
        final Variant v1 = new Variant ( 1 );
        final Variant v2 = new Variant ( 1.0 );

        compareVariant ( v1, v2, true );
    }

    @Test
    public void testVariantCompareIntegerVSDoubleDifferent () throws Exception
    {
        final Variant v1 = new Variant ( 1 );
        final Variant v2 = new Variant ( 1.1 );

        compareVariant ( v1, v2, false );
    }

    @Test
    public void testNumberIssue ()
    {
        final Variant v1 = new Variant ( 1.1 );
        final Variant v2 = new Variant ( new Double ( 1.1 ) );

        compareVariant ( v1, v2, true );
    }

}
