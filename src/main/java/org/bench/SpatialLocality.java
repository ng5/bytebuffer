package org.bench;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

public class SpatialLocality {
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    public void testSumList(ByteBufferState byteBufferState, Blackhole blackhole) {
        int longsCount = byteBufferState.getLongsCount();
        int doublesCount = byteBufferState.getDoublesCount();
        var sum = Sum.byList(byteBufferState.getList(), longsCount, doublesCount);
        blackhole.consume(sum);
    }

    @Benchmark
    public void testSumHeapBuffer(ByteBufferState byteBufferState, Blackhole blackhole) {
        int longsCount = byteBufferState.getLongsCount();
        int doublesCount = byteBufferState.getDoublesCount();
        byteBufferState.getHeapByteBuffer().position(0);
        var sum = Sum.byByteBuffer(byteBufferState.getHeapByteBuffer(), longsCount, doublesCount);
        blackhole.consume(sum);
    }

    @Benchmark
    public void testSumDirectBuffer(ByteBufferState byteBufferState, Blackhole blackhole) {
        int longsCount = byteBufferState.getLongsCount();
        int doublesCount = byteBufferState.getDoublesCount();
        byteBufferState.getDirectByteBuffer().position(0);
        var sum = Sum.byByteBuffer(byteBufferState.getDirectByteBuffer(), longsCount, doublesCount);
        blackhole.consume(sum);
    }

    @Benchmark
    public void testSumHeapBufferVarHandle(ByteBufferState byteBufferState, Blackhole blackhole) {
        int longsCount = byteBufferState.getLongsCount();
        int doublesCount = byteBufferState.getDoublesCount();
        byteBufferState.getHeapByteBuffer().position(0);
        var sum = Sum.byByteBufferVarHandle(byteBufferState.getHeapByteBuffer(), longsCount, doublesCount);
        blackhole.consume(sum);
    }

    @Benchmark
    public void testSumDirectBufferVarHandle(ByteBufferState byteBufferState, Blackhole blackhole) {
        int longsCount = byteBufferState.getLongsCount();
        int doublesCount = byteBufferState.getDoublesCount();
        byteBufferState.getDirectByteBuffer().position(0);
        var sum = Sum.byByteBufferVarHandle(byteBufferState.getDirectByteBuffer(), longsCount, doublesCount);
        blackhole.consume(sum);
    }
}
