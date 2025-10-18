import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class RunSpeed {
    private static final VarHandle LONG_VH = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);
    private static final VarHandle DOUBLE_VH = MethodHandles.byteArrayViewVarHandle(double[].class, ByteOrder.BIG_ENDIAN);

    public static void main(String[] args) {
        final int longsCount = 10;
        final int doublesCount = 10;

        List<Object> list = new ArrayList<>();
        for (int i = 1; i <= longsCount; i++) list.add((long)i);
        for (int i = 1; i <= doublesCount; i++) list.add(i + 0.5);

        int capacity = longsCount * Long.BYTES + doublesCount * Double.BYTES;
        ByteBuffer heap = ByteBuffer.allocate(capacity);
        for (int i = 1; i <= longsCount; i++) heap.putLong(i);
        for (int i = 1; i <= doublesCount; i++) heap.putDouble(i + 0.5);
        heap.flip();

        double expected = expectedSum(longsCount, doublesCount);
        System.out.println("Expected per-iteration sum = " + expected);

        // read via ByteBuffer once
        heap.position(0);
        double viaBuf = sumByteBuffer(heap, longsCount, doublesCount);
        System.out.println("Read via ByteBuffer: " + viaBuf);

        // read via VarHandle once
        byte[] arr = heap.array();
        int heapArrayOffset = heap.arrayOffset();
        int baseLongByteOffset = heapArrayOffset; // bytes
        int baseDoubleByteOffset = heapArrayOffset + longsCount * Long.BYTES;
        double viaVH = sumHeapWithVarHandles(arr, baseLongByteOffset, baseDoubleByteOffset, longsCount, doublesCount);
        System.out.println("Read via VarHandle: " + viaVH);

        // print raw bytes
        System.out.println("Raw bytes:");
        for (int i = 0; i < arr.length; i++) {
            System.out.printf("%02x ", arr[i]);
            if ((i+1)%16==0) System.out.println();
        }
        System.out.println();

        // print longs and doubles read element-wise
        System.out.println("Element-wise via VarHandle (longs):");
        for (int i = 0; i < longsCount; i++) {
            long v = (long) LONG_VH.get(arr, baseLongByteOffset + i * Long.BYTES);
            System.out.println(i + ": " + v);
        }
        System.out.println("Element-wise via VarHandle (doubles):");
        for (int i = 0; i < doublesCount; i++) {
            double v = (double) DOUBLE_VH.get(arr, baseDoubleByteOffset + i * Double.BYTES);
            System.out.println(i + ": " + v);
        }
    }

    private static double expectedSum(int longsCount, int doublesCount) {
        long sumLongs = (longsCount * (longsCount + 1L)) / 2L;
        long sumInts = (doublesCount * (doublesCount + 1L)) / 2L;
        return sumLongs + sumInts + (doublesCount * 0.5);
    }

    private static double sumByteBuffer(ByteBuffer buf, int longsCount, int doublesCount) {
        double sum = 0.0;
        for (int i = 0; i < longsCount; i++) sum += buf.getLong();
        for (int i = 0; i < doublesCount; i++) sum += buf.getDouble();
        return sum;
    }

    private static double sumHeapWithVarHandles(byte[] arr, int baseLongByteOffset, int baseDoubleByteOffset, int longsCount, int doublesCount) {
        double sum = 0.0;
        for (int i = 0; i < longsCount; i++) sum += (long) LONG_VH.get(arr, baseLongByteOffset + i * Long.BYTES);
        for (int i = 0; i < doublesCount; i++) sum += (double) DOUBLE_VH.get(arr, baseDoubleByteOffset + i * Double.BYTES);
        return sum;
    }
}
