/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// You must accept the terms of that agreement to use this software.
//
// Copyright (C) 2010-2014 Pentaho and others
// All Rights Reserved.
*/
package mondrian.rolap.agg;

import mondrian.util.Pair;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.SortedSet;

/**
 * Implementation of a segment body which stores the data inside
 * a dense primitive array of double precision numbers.
 *
 * @author LBoudreau
 */
class DenseBooleanSegmentBody extends AbstractSegmentBody {
    private static final long serialVersionUID = 5775717165497921144L;

    private final Boolean[] values;
    private final BitSet nullValues;

    /**
     * Creates a DenseBooleanSegmentBody.
     *
     * <p>Stores the given array of cell values and null indicators; caller must
     * not modify them afterwards.</p>
     *
     * @param nullValues A bit-set indicating whether values are null. Each
     *                   position in the bit-set corresponds to an offset in the
     *                   value array. If position is null, the corresponding
     *                   entry in the value array will also be 0.
     * @param values Cell values
     * @param axes Axes
     */
    DenseBooleanSegmentBody(
        BitSet nullValues,
        Boolean[] values,
        List<Pair<SortedSet<Comparable>, Boolean>> axes)
    {
        super(axes);
        this.values = values;
        this.nullValues = nullValues;
    }

    @Override
    public Object getValueArray() {
        return values;
    }

    @Override
    public BitSet getNullValueIndicators() {
        return nullValues;
    }

    @Override
    protected int getSize() {
        return values.length;
    }

    @Override
    protected Object getObject(int i) {
        Boolean value = values[i];
        if (value == null && nullValues.get(i)) {
            return null;
        }
        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DenseBooleanSegmentBody(size=");
        sb.append(values.length);
        sb.append(", data=");
        sb.append(Arrays.toString(values));
        sb.append(", nullValues=").append(nullValues);
        sb.append(", axisValueSets=");
        sb.append(Arrays.toString(getAxisValueSets()));
        sb.append(", nullAxisFlags=");
        sb.append(Arrays.toString(getNullAxisFlags()));
        if (getAxisValueSets().length > 0) {
            if (getAxisValueSets()[0].iterator().hasNext()) {
                sb.append(", aVS[0]=");
                sb.append(getAxisValueSets()[0].getClass());
                sb.append(", aVS[0][0]=");
                sb.append(getAxisValueSets()[0].iterator().next().getClass());
            }
        }
        sb.append(")");
        return sb.toString();
    }
}

// End DenseBooleanSegmentBody.java
