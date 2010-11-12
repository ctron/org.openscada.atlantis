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

package org.openscada.hd.server.storage.internal;

import org.openscada.hsdb.datatypes.BaseValue;
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;

/**
 * This class provides static methods that are used by the query implementation.
 * @author Ludwig Straub
 */
public class QueryHelper
{
    /**
     * This method removes elements from the end of the second array until there is no more time overlapping between the first and the second array.
     * The method returns the shortened second array.
     * @param firstArray first array
     * @param secondArray second array
     * @return shortened second array
     */
    public static BaseValue[] removeTimeOverlay ( final BaseValue[] firstArray, final BaseValue[] secondArray )
    {
        if ( firstArray.length == 0 || secondArray.length == 0 )
        {
            return secondArray;
        }
        final long timeBorderValue = firstArray[0].getTime ();
        for ( int i = secondArray.length - 1; i >= 0; i-- )
        {
            if ( secondArray[i].getTime () < timeBorderValue )
            {
                final BaseValue[] resultArray = secondArray instanceof LongValue[] ? new LongValue[i + 1] : new DoubleValue[i + 1];
                for ( int j = 0; j <= i; j++ )
                {
                    resultArray[j] = secondArray[j];
                }
                return resultArray;
            }
        }
        return secondArray;
    }

    /**
     * This method merges the two passed arrays. The second array will be joined before the first array.
     * The result array has the same type as the second array.
     * If the types do not match, an automatic conversion is performed.
     * @param firstArray first array that has to be merged. this array can be null
     * @param secondArray second array that has to be merged. this array must not be null
     * @return new array containing the elements of both arrays
     */
    public static BaseValue[] joinValueArrays ( final BaseValue[] firstArray, final BaseValue[] secondArray )
    {
        if ( firstArray == null || firstArray.length == 0 )
        {
            return secondArray;
        }
        if ( secondArray instanceof LongValue[] )
        {
            final LongValue[] secondArray1 = (LongValue[])secondArray;
            final LongValue[] result = new LongValue[firstArray.length + secondArray.length];
            for ( int i = 0; i < secondArray.length; i++ )
            {
                result[i] = secondArray1[i];
            }
            if ( firstArray instanceof LongValue[] )
            {
                final LongValue[] firstArray1 = (LongValue[])firstArray;
                for ( int i = 0, j = secondArray.length; i < firstArray.length; i++, j++ )
                {
                    result[j] = firstArray1[i];
                }
            }
            else
            {
                final DoubleValue[] firstArray1 = (DoubleValue[])firstArray;
                for ( int i = 0, j = secondArray.length; i < firstArray.length; i++, j++ )
                {
                    final DoubleValue srcValue = firstArray1[i];
                    result[j] = new LongValue ( srcValue.getTime (), srcValue.getQualityIndicator (), srcValue.getManualIndicator (), srcValue.getBaseValueCount (), Math.round ( srcValue.getValue () ) );
                }
            }
            return result;
        }
        else
        {
            final DoubleValue[] secondArray1 = (DoubleValue[])secondArray;
            final DoubleValue[] result = new DoubleValue[firstArray.length + secondArray.length];
            for ( int i = 0; i < secondArray.length; i++ )
            {
                result[i] = secondArray1[i];
            }
            if ( firstArray instanceof DoubleValue[] )
            {
                final DoubleValue[] firstArray1 = (DoubleValue[])firstArray;
                for ( int i = 0, j = secondArray.length; i < firstArray.length; i++, j++ )
                {
                    result[j] = firstArray1[i];
                }
            }
            else
            {
                final LongValue[] firstArray1 = (LongValue[])firstArray;
                for ( int i = 0, j = secondArray.length; i < firstArray.length; i++, j++ )
                {
                    final LongValue srcValue = firstArray1[i];
                    result[j] = new DoubleValue ( srcValue.getTime (), srcValue.getQualityIndicator (), srcValue.getManualIndicator (), srcValue.getBaseValueCount (), srcValue.getValue () );
                }
            }
            return result;
        }
    }
}
