package org.bench.runner;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;

public final class RealBenchmarks {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                // Include all benchmarks under org.bench.*
                .include("org\\.bench\\..*")
                // Realistic settings
                .warmupIterations(5)
                .warmupTime(TimeValue.seconds(1))
                .measurementIterations(10)
                .measurementTime(TimeValue.seconds(1))
                .forks(2)
                .threads(Math.max(1, Runtime.getRuntime().availableProcessors()))
                .shouldDoGC(true)
                .shouldFailOnError(true)
                .verbosity(VerboseMode.NORMAL)
                .param("pair", "2x2", "4x4", "8x8", "16x16", "32x32", "64x64")
                .build();

        new Runner(opt).run();
    }
}
