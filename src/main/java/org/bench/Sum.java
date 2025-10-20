package org.bench;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;

public class Sum {
    public static final VarHandle LONG_BB_VH = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);
    public static final VarHandle DOUBLE_BB_VH = MethodHandles.byteArrayViewVarHandle(double[].class, ByteOrder.LITTLE_ENDIAN);

    public static double byList(java.util.List<Object> list, int longsCount, int doublesCount) {
        double sum = 0.0;
        for (int i = 0; i < longsCount; i++) sum += (long) list.get(i);
        for (int i = 0; i < doublesCount; i++) sum += (double) list.get(i + longsCount);
        return sum;
    }

    public static double byByteArray(byte[] a, int longsCount, int doublesCount) {
        double sum = 0.0;
        var end = longsCount * Long.BYTES;
        for (int i = 0; i < end; i += Long.BYTES) {
            long v = (long) LONG_BB_VH.get(a, i);
            sum += v;
        }
        end = (longsCount + doublesCount) * Long.BYTES;
        for (int i = longsCount * Long.BYTES; i < end; i += Double.BYTES) {
            double v = (double) DOUBLE_BB_VH.get(a, i);
            sum += v;
        }
        return sum;
    }

}
