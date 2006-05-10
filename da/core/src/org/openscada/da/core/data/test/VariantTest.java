package org.openscada.da.core.data.test;

import org.openscada.da.core.data.Variant;

import junit.framework.TestCase;

public class VariantTest extends TestCase {
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	private void compareVariant ( Variant v1, Variant v2, boolean equal )
	{
		if ( equal )
		{
			assertTrue ( v1.equals(v2) );
			assertTrue ( v2.equals(v1) );
		}
		else
		{
			assertFalse ( v1.equals(v2) );
			assertFalse ( v2.equals(v1) );
		}
	}
	
	public void testVariantCompareNullSame() throws Exception {
		Variant nullValue = new Variant();
		
		compareVariant ( nullValue, nullValue, true );
	}
	
	public void testVariantCompareNullDifferent() throws Exception {
		Variant nullValue1 = new Variant();
		Variant nullValue2 = new Variant();
		
		compareVariant ( nullValue1, nullValue2, true );
		
	}
	
	public void testVariantCompareIntEqual() throws Exception {
		Variant v1 = new Variant((int)1);
		Variant v2 = new Variant((int)1);
		
		compareVariant ( v1, v2, true );
	}
	
	public void testVariantCompareIntDifferent() throws Exception {
		Variant v1 = new Variant((int)1);
		Variant v2 = new Variant((int)2);
		
		compareVariant ( v1, v2, false );
	}
	
	public void testVariantCompareLongEqual() throws Exception {
		Variant v1 = new Variant((long)1);
		Variant v2 = new Variant((long)1);
		
		compareVariant ( v1, v2, true );
	}
	
	public void testVariantCompareLongDifferent() throws Exception {
		Variant v1 = new Variant((long)1);
		Variant v2 = new Variant((long)2);
		
		compareVariant ( v1, v2, false );
	}
	
	public void testVariantCompareStringEqual() throws Exception {
		Variant v1 = new Variant("test");
		Variant v2 = new Variant("test");
		
		compareVariant ( v1, v2, true );
	}
	
	public void testVariantCompareStringDifferent() throws Exception {
		Variant v1 = new Variant("test1");
		Variant v2 = new Variant("test2");
		
		compareVariant ( v1, v2, false );
	}
	
	public void testVariantCompareIntVSLongEqual() throws Exception {
		Variant v1 = new Variant((int)1);
		Variant v2 = new Variant((long)1);
		
		compareVariant ( v1, v2, true );
	}
	
	public void testVariantCompareIntVSLongDifferent() throws Exception {
		Variant v1 = new Variant((int)1);
		Variant v2 = new Variant((long)2);
		
		compareVariant ( v1, v2, false );
	}
	
	

	public void testVariantCompareIntVSDoubleEqual() throws Exception {
		Variant v1 = new Variant((int)1);
		Variant v2 = new Variant((double)1.0);
		
		compareVariant ( v1, v2, true );
	}
	
	public void testVariantCompareIntVSDoubleDifferent() throws Exception {
		Variant v1 = new Variant((int)1);
		Variant v2 = new Variant((double)1.5);
		
		compareVariant ( v1, v2, false );
	}
	
	
	public void testVariantCompareStringVSDoubleEqual() throws Exception {
		Variant v1 = new Variant("1");
		Variant v2 = new Variant(1.0);
		
		compareVariant ( v1, v2, true );
	}
	
	public void testVariantCompareStringVSDoubleDifferent() throws Exception {
		Variant v1 = new Variant("1");
		Variant v2 = new Variant(1.5);
		
		compareVariant ( v1, v2, false );
	}
	
	
}
