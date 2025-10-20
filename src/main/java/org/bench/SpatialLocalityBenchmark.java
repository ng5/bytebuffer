package org.bench;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;


@Fork(1)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Timeout(time = 10, timeUnit = TimeUnit.SECONDS)
public class SpatialLocalityBenchmark {
    @Benchmark
    public void testSumList(ByteBufferState byteBufferState, Blackhole blackhole) {
        int longsCount = byteBufferState.getLongsCount();
        int doublesCount = byteBufferState.getDoublesCount();
        var sum = Sum.byList(byteBufferState.getList(), longsCount, doublesCount);
        blackhole.consume(sum);
    }

    @Benchmark
    public void testSumByteArrayVarHandle(ByteBufferState byteBufferState, Blackhole blackhole) {
        int longsCount = byteBufferState.getLongsCount();
        int doublesCount = byteBufferState.getDoublesCount();
        var sum = Sum.byByteArray(byteBufferState.getData(), longsCount, doublesCount);
        blackhole.consume(sum);
    }

}
