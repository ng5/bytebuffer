import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpeedTest {
    private static final VarHandle LONG_VH = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle DOUBLE_VH = MethodHandles.byteArrayViewVarHandle(double[].class, ByteOrder.LITTLE_ENDIAN);
    // VarHandles for ByteBuffer-backed views (for direct buffers)
    private static final VarHandle LONG_BB_VH = MethodHandles.byteBufferViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);
    private static final VarHandle DOUBLE_BB_VH = MethodHandles.byteBufferViewVarHandle(double[].class, ByteOrder.LITTLE_ENDIAN);

    public void reportBenchmark(int longsCount, int doublesCount,int iterations) {
        // 1) ArrayList<Object> with 10 longs and 10 doubles
        List<Object> list = new ArrayList<>(longsCount + doublesCount);
        for (int i = 1; i <= longsCount; i++) {
            list.add((long) i);
        }
        for (int i = 1; i <= doublesCount; i++) {
            list.add(i + 0.5);
        }

        // 2) ByteBuffers (heap and direct), each with 10 longs and 10 doubles
        int capacity = longsCount * Long.BYTES + doublesCount * Double.BYTES;

        ByteBuffer heap = ByteBuffer.allocate(capacity).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 1; i <= longsCount; i++) {
            heap.putLong(i);
        }
        for (int i = 1; i <= doublesCount; i++) {
            heap.putDouble(i + 0.5);
        }
        heap.flip();

        ByteBuffer direct = ByteBuffer.allocateDirect(capacity).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 1; i <= longsCount; i++) {
            direct.putLong(i);
        }
        for (int i = 1; i <= doublesCount; i++) {
            direct.putDouble(i + 0.5);
        }
        direct.flip();

        double expectedPerIteration = expectedSum(longsCount, doublesCount);

        double listTotal = 0.0;
        double heapTotal = 0.0;
        double directTotal = 0.0;
        double heapVarHandleTotal = 0.0;
        double directVarHandleTotal = 0.0;

        // Measure ArrayList
        long t0 = System.nanoTime();
        for (int it = 0; it < iterations; it++) {
            listTotal += sumList(list,longsCount, doublesCount);
        }
        long t1 = System.nanoTime();
        long listNs = t1 - t0;
        double listPerIterNs = (double) listNs / iterations;

        // Measure heap ByteBuffer (getLong/getDouble)
        t0 = System.nanoTime();
        for (int it = 0; it < iterations; it++) {
            heap.position(0);
            heapTotal += sumByteBuffer(heap, longsCount, doublesCount);
        }
        t1 = System.nanoTime();
        long heapNs = t1 - t0;
        double heapPerIterNs = (double) heapNs / iterations;

        // Measure direct ByteBuffer
        t0 = System.nanoTime();
        for (int it = 0; it < iterations; it++) {
            direct.position(0);
            directTotal += sumByteBuffer(direct, longsCount, doublesCount);
        }
        t1 = System.nanoTime();
        long directNs = t1 - t0;
        double directPerIterNs = (double) directNs / iterations;

        // Measure heap ByteBuffer accessed via VarHandle over backing byte[]
        byte[] heapArray = heap.array();
        // base byte offsets for long and double views (arrayOffset() is the start of buffer within the array)
        int baseLongByteOffset = heap.arrayOffset(); // bytes
        int baseDoubleByteOffset = baseLongByteOffset + longsCount * Long.BYTES;

        t0 = System.nanoTime();
        for (int it = 0; it < iterations; it++) {
            heapVarHandleTotal += sumHeapWithVarHandles(heapArray, baseLongByteOffset, baseDoubleByteOffset, longsCount, doublesCount);
        }
        t1 = System.nanoTime();
        long heapVarNs = t1 - t0;
        double heapVarPerIterNs = (double) heapVarNs / iterations;

        // Measure direct ByteBuffer accessed via VarHandle
        // For ByteBuffer VarHandle, the index is a byte offset into the buffer
        int baseDirectLongByteOffset = 0; // read from buffer position 0
        int baseDirectDoubleByteOffset = longsCount * Long.BYTES;
        t0 = System.nanoTime();
        for (int it = 0; it < iterations; it++) {
            // use absolute VarHandle reads from the ByteBuffer
            direct.position(0);
            directVarHandleTotal += sumDirectWithVarHandles(direct, baseDirectLongByteOffset, baseDirectDoubleByteOffset, longsCount, doublesCount);
        }
        t1 = System.nanoTime();
        long directVarNs = t1 - t0;
        double directVarPerIterNs = (double) directVarNs / iterations;

        double expectedTotal = expectedPerIteration * iterations;

        assertEquals(expectedTotal, listTotal, 1e-6, "ArrayList sum mismatch");
        assertEquals(expectedTotal, heapTotal, 1e-6, "Heap ByteBuffer sum mismatch");
        assertEquals(expectedTotal, directTotal, 1e-6, "Direct ByteBuffer sum mismatch");
        assertEquals(expectedTotal, heapVarHandleTotal, 1e-6, "Heap VarHandle sum mismatch");
        assertEquals(expectedTotal, directVarHandleTotal, 1e-6, "Direct VarHandle sum mismatch");

        System.out.printf("%-18s %-16.2f %-12.2f %-12s\n", "ArrayList", listTotal, listPerIterNs, longsCount+"/"+doublesCount);
        System.out.printf("%-18s %-16.2f %-12.2f %-12s\n", "Heap", heapTotal, heapPerIterNs , longsCount+"/"+doublesCount);
        System.out.printf("%-18s %-16.2f %-12.2f %-12s\n", "Direct", directTotal, directPerIterNs , longsCount+"/"+doublesCount);
        System.out.printf("%-18s %-16.2f %-12.2f %-12s\n", "Heap VarHandle", heapVarHandleTotal, heapVarPerIterNs, longsCount+"/"+doublesCount);
        System.out.printf("%-18s %-16.2f %-12.2f %-12s\n", "Direct VarHandle", directVarHandleTotal, directVarPerIterNs, longsCount+"/"+doublesCount);
        System.out.println("---------------------------------------------------------------------");
    }

    @Test
    public void testSpeed() {
        System.out.printf("%-18s %-16s %-12s %-12s%n", "Method", "Total", "Per-iter", "NLong/NDouble");
        System.out.println("---------------------------------------------------------------------");
        int iterations = Integer.parseInt(System.getProperty("speed.test.iters", "100000000"));
        reportBenchmark(2, 2, iterations);
        reportBenchmark(4, 4, iterations);
        reportBenchmark(8, 8, iterations);
        reportBenchmark(16, 16, iterations);
        reportBenchmark(32, 32, iterations);
    }

    private static double expectedSum(int longsCount, int doublesCount) {
        // longs: 1..longsCount
        long sumLongs = (longsCount * (longsCount + 1L)) / 2L;
        // doubles: (i + 0.5) for i=1..doublesCount -> sum(i) + 0.5 * count
        long sumInts = (doublesCount * (doublesCount + 1L)) / 2L;
        return sumLongs + sumInts + (doublesCount * 0.5);
    }

    private static double sumList(List<Object> list,int longsCount, int doublesCount) {
        double sum = 0.0;
        for(int i=0; i<longsCount; i++) sum += (long)list.get(i);
        for(int i=0; i<doublesCount; i++) sum += (double)list.get(i+longsCount);
        return sum;
    }

    private static double sumByteBuffer(ByteBuffer buf, int longsCount, int doublesCount) {
        double sum = 0.0;
        for (int i = 0; i < longsCount; i++) {
            sum += buf.getLong();
        }
        for (int i = 0; i < doublesCount; i++) {
            sum += buf.getDouble();
        }
        return sum;
    }

    private static double sumDirectWithVarHandles(ByteBuffer buf, int baseLongByteOffset, int baseDoubleByteOffset, int longsCount, int doublesCount) {
        double sum = 0.0;
        for (int i = 0; i < longsCount; i++) {
            int byteOffset = baseLongByteOffset + i * Long.BYTES;
            long v = (long) LONG_BB_VH.get(buf, byteOffset);
            sum += v;
        }
        for (int i = 0; i < doublesCount; i++) {
            int byteOffset = baseDoubleByteOffset + i * Double.BYTES;
            double v = (double) DOUBLE_BB_VH.get(buf, byteOffset);
            sum += v;
        }
        return sum;
    }

    private static double sumHeapWithVarHandles(byte[] arr, int baseLongByteOffset, int baseDoubleByteOffset, int longsCount, int doublesCount) {
        double sum = 0.0;
        for (int i = 0; i < longsCount; i++) {
            int byteOffset = baseLongByteOffset + i * Long.BYTES;
            long v = (long) LONG_VH.get(arr, byteOffset);
            sum += v;
        }
        for (int i = 0; i < doublesCount; i++) {
            int byteOffset = baseDoubleByteOffset + i * Double.BYTES;
            double v = (double) DOUBLE_VH.get(arr, byteOffset);
            sum += v;
        }
        return sum;
    }
}
