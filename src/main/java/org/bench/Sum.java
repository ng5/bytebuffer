package org.bench;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Sum {
    private static final VarHandle LONG_BB_VH = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle DOUBLE_BB_VH = MethodHandles.byteBufferViewVarHandle(double[].class, ByteOrder.LITTLE_ENDIAN);

    public static double byList(java.util.List<Object> list, int longsCount, int doublesCount) {
        double sum = 0.0;
        for (int i = 0; i < longsCount; i++) sum += (long) list.get(i);
        for (int i = 0; i < doublesCount; i++) sum += (double) list.get(i + longsCount);
        return sum;
    }

    public static double byByteBuffer(ByteBuffer byteBuffer, int longsCount, int doublesCount) {
        double sum = 0.0;
        for (int i = 0; i < longsCount; i++) sum += byteBuffer.getLong();
        for (int i = 0; i < doublesCount; i++) sum += byteBuffer.getDouble();
        return sum;
    }

    public static double byByteBufferVarHandle(ByteBuffer byteBuffer, int longsCount, int doublesCount) {
        double sum = 0.0;
        int longBaseOffset = 0;
        int doubleBaseOffset = longsCount * Long.BYTES;
        for (int i = 0; i < longsCount; i++) {
            int offset = longBaseOffset + i * Long.BYTES;
            long v = (long) LONG_BB_VH.get(byteBuffer, offset);
            sum += v;
        }
        for (int i = 0; i < doublesCount; i++) {
            int offset = doubleBaseOffset + i * Double.BYTES;
            double v = (double) DOUBLE_BB_VH.get(byteBuffer, offset);
            sum += v;
        }
        return sum;
    }

}
