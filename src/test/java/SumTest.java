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
        var sumByteBufferVarHandle = Sum.byByteArray(byteBufferState.getData(), byteBufferState.getLongsCount(), byteBufferState.getDoublesCount());
        System.out.println("Sum by List: " + sumList);
        assertEquals(sumList, sumByteBufferVarHandle);
    }
}
