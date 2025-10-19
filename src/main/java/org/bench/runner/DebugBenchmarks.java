package org.bench.runner;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

public final class DebugBenchmarks {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                // Include all benchmarks under org.bench.*
                .include("org\\.bench\\..*")
                // Debug-friendly settings
                .warmupIterations(0)
                .measurementIterations(1)
                .forks(0) // run in the same JVM for easier debugging
                .threads(1)
                .shouldDoGC(true)
                .shouldFailOnError(true)
                .verbosity(VerboseMode.NORMAL)
                .param("pair", "4x4")
                .build();

        new Runner(opt).run();
    }

}
