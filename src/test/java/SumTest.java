import org.bench.ByteBufferState;
import org.bench.Sum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SumTest {
    @Test
    public void testSum() {
        var byteBufferState = new ByteBufferState();
        byteBufferState.setLongsCount(100);
        byteBufferState.setDoublesCount(100);
        byteBufferState.setup();

        var sumList = Sum.byList(byteBufferState.getList(), byteBufferState.getLongsCount(), byteBufferState.getDoublesCount());
        var sumByteBuffer = Sum.byByteBuffer(byteBufferState.getHeapByteBuffer(), byteBufferState.getLongsCount(), byteBufferState.getDoublesCount());
        var sumByteBufferVarHandle = Sum.byByteBufferVarHandle(byteBufferState.getHeapByteBuffer(), byteBufferState.getLongsCount(), byteBufferState.getDoublesCount());
        var sumDirectByteBuffer = Sum.byByteBuffer(byteBufferState.getDirectByteBuffer(), byteBufferState.getLongsCount(), byteBufferState.getDoublesCount());
        var sumDirectByteBufferVarHandle = Sum.byByteBufferVarHandle(byteBufferState.getDirectByteBuffer(), byteBufferState.getLongsCount(), byteBufferState.getDoublesCount());

        System.out.println("Sum by List: " + sumList);
        System.out.println("Sum by ByteBuffer: " + sumByteBuffer);
        System.out.println("Sum by ByteBuffer VarHandle: " + sumByteBufferVarHandle);
        System.out.println("Sum by Direct ByteBuffer: " + sumDirectByteBuffer);
        System.out.println("Sum by Direct ByteBuffer VarHandle: " + sumDirectByteBufferVarHandle);

        assertEquals(sumList, sumByteBuffer);
        assertEquals(sumList, sumByteBufferVarHandle);
        assertEquals(sumList, sumDirectByteBuffer);
        assertEquals(sumList, sumDirectByteBufferVarHandle);
    }
}
