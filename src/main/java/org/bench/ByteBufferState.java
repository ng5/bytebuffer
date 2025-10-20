package org.bench;

import lombok.Data;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
@Data
public class ByteBufferState {
    List<Object> list;
    byte[] data;

    @Param({"4x4", "8x8", "16x16", "32x32", "64x64", "512x512", "1024000x1024000", "10240000x10240000"})
    private String pair;
    private int longsCount;
    private int doublesCount;

    @Setup
    public void setup() {
        if (pair != null && !pair.isEmpty()) {
            String[] parts = pair.split("x");
            longsCount = Integer.parseInt(parts[0]);
            doublesCount = Integer.parseInt(parts[1]);
        }

        var lArray = new long[longsCount];
        var dArray = new double[doublesCount];
        for (int i = 0; i < longsCount; i++) lArray[i] = (long) (Math.random() * Integer.MAX_VALUE);
        for (int i = 0; i < doublesCount; i++) dArray[i] = Math.random();

        list = new ArrayList<>(longsCount + doublesCount);
        for (int i = 0; i < longsCount; i++) {
            list.add(lArray[i]);
        }
        for (int i = 0; i < doublesCount; i++) {
            list.add(dArray[i]);
        }
        int capacity = longsCount * Long.BYTES + doublesCount * Double.BYTES;
        data = new byte[capacity];
        for (int i = 0; i < longsCount * Long.BYTES; i += Long.BYTES) {
            Sum.LONG_BB_VH.set(data, i, lArray[i / Long.BYTES]);
        }
        for (int i = longsCount * Long.BYTES; i < (longsCount * Long.BYTES + doublesCount * Double.BYTES); i += Double.BYTES) {
            Sum.DOUBLE_BB_VH.set(data, i, dArray[(i - longsCount * Long.BYTES) / Double.BYTES]);
        }
    }

}
