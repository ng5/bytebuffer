package org.bench;

import lombok.Data;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
@Data
public class ByteBufferState {
    List<Object> list;
    ByteBuffer heapByteBuffer;
    ByteBuffer directByteBuffer;
    
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

        heapByteBuffer = ByteBuffer.allocate(capacity).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < longsCount; i++) {
            heapByteBuffer.putLong(lArray[i]);
        }
        for (int i = 0; i < doublesCount; i++) {
            heapByteBuffer.putDouble(dArray[i]);
        }
        heapByteBuffer.flip();

        directByteBuffer = ByteBuffer.allocateDirect(capacity).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < longsCount; i++) {
            directByteBuffer.putLong(lArray[i]);
        }
        for (int i = 0; i < doublesCount; i++) {
            directByteBuffer.putDouble(dArray[i]);
        }
        directByteBuffer.flip();
    }

}
