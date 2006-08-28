/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.core.data.test;

import org.junit.Assert;
import org.junit.Test;
import org.openscada.da.core.data.Variant;

public class VariantTest {

	private void compareVariant ( Variant v1, Variant v2, boolean equal )
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
	public void testVariantCompareNullSame () throws Exception {
		Variant nullValue = new Variant();
		
		compareVariant ( nullValue, nullValue, true );
	}
    
    @Test
	public void testVariantCompareNullDifferent () throws Exception {
		Variant nullValue1 = new Variant();
		Variant nullValue2 = new Variant();
		
		compareVariant ( nullValue1, nullValue2, true );
		
	}

    @Test
	public void testVariantCompareIntEqual () throws Exception {
		Variant v1 = new Variant((int)1);
		Variant v2 = new Variant((int)1);
		
		compareVariant ( v1, v2, true );
	}

    @Test
	public void testVariantCompareIntDifferent () throws Exception {
		Variant v1 = new Variant((int)1);
		Variant v2 = new Variant((int)2);
		
		compareVariant ( v1, v2, false );
	}

    @Test
	public void testVariantCompareLongEqual () throws Exception {
		Variant v1 = new Variant((long)1);
		Variant v2 = new Variant((long)1);
		
		compareVariant ( v1, v2, true );
	}

    @Test
	public void testVariantCompareLongDifferent () throws Exception {
		Variant v1 = new Variant((long)1);
		Variant v2 = new Variant((long)2);
		
		compareVariant ( v1, v2, false );
	}

    @Test
	public void testVariantCompareStringEqual () throws Exception {
		Variant v1 = new Variant("test");
		Variant v2 = new Variant("test");
		
		compareVariant ( v1, v2, true );
	}

    @Test
	public void testVariantCompareStringDifferent () throws Exception {
		Variant v1 = new Variant("test1");
		Variant v2 = new Variant("test2");
		
		compareVariant ( v1, v2, false );
	}

    @Test
	public void testVariantCompareIntVSLongEqual () throws Exception {
		Variant v1 = new Variant((int)1);
		Variant v2 = new Variant((long)1);
		
		compareVariant ( v1, v2, true );
	}

    @Test
	public void testVariantCompareIntVSLongDifferent () throws Exception {
		Variant v1 = new Variant((int)1);
		Variant v2 = new Variant((long)2);
		
		compareVariant ( v1, v2, false );
	}

    @Test
	public void testVariantCompareIntVSDoubleEqual () throws Exception {
		Variant v1 = new Variant((int)1);
		Variant v2 = new Variant((double)1.0);
		
		compareVariant ( v1, v2, true );
	}

    @Test
	public void testVariantCompareIntVSDoubleDifferent () throws Exception {
		Variant v1 = new Variant((int)1);
		Variant v2 = new Variant((double)1.5);
		
		compareVariant ( v1, v2, false );
	}
	
    @Test
	public void testVariantCompareStringVSDoubleEqual () throws Exception {
		Variant v1 = new Variant("1");
		Variant v2 = new Variant(1.0);
		
		compareVariant ( v1, v2, true );
	}

    @Test
	public void testVariantCompareStringVSDoubleDifferent () throws Exception {
		Variant v1 = new Variant("1");
		Variant v2 = new Variant(1.5);
		
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
        Variant v1 = new Variant ( false );
        Variant v2 = new Variant ( false );
        
        compareVariant ( v1, v2, true );
    }
    
    @Test
    public void testVariantCompareBooleanVSBooleanDifferent () throws Exception
    {
        Variant v1 = new Variant ( true );
        Variant v2 = new Variant ( false );
        
        compareVariant ( v1, v2, false );
    }
    
    @Test
    public void testVariantCompareBooleanVSLongEqual () throws Exception
    {
        Variant v1 = new Variant ( true );
        Variant v2 = new Variant ( (long)-2000 );
        
        compareVariant ( v1, v2, true );
    }
    
    @Test
    public void testVariantCompareBooleanVSLongDifferent () throws Exception
    {
        Variant v1 = new Variant ( true );
        Variant v2 = new Variant ( (long)0 );
        
        compareVariant ( v1, v2, false );
    }
    
    @Test
    public void testVariantCompareBooleanVSDoubleEqual () throws Exception
    {
        Variant v1 = new Variant ( false );
        Variant v2 = new Variant ( (double)0.0 );
        
        compareVariant ( v1, v2, true );
        
        v1 = new Variant ( true );
        v2 = new Variant ( (double)-1.0 );
        
        compareVariant ( v1, v2, true );
    }
    
    @Test
    public void testVariantCompareBooleanVSDoubleDifferent () throws Exception
    {
        Variant v1 = new Variant ( false );
        Variant v2 = new Variant ( (double)0.1 );
        
        compareVariant ( v1, v2, false );
        
        v1 = new Variant ( true );
        v2 = new Variant ( (double)0.0 );
        
        compareVariant ( v1, v2, false );
    }
	
}
