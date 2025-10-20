bytebuffer

This benchmark tests the performance of the `byte[]` compared to `ArrayList<Object>`
In each collection, there are N long and N double numbers. It tests 2 scenarios:

1) Sum of elements in ArrayList
2) Sum of elements in a heap allocated byte[]

In theory, it is expected that the byte[] is faster due to better spatial locality achieved with compact storage.
But Java's ArrayList is known to be heavily optimized by JVM. Byte array is accessed with VarHandle to achieve
well-defined stride and type safe access.

HOW TO RUN

```
mvn clean package
java -jar target/bytebuffer-1.0-SNAPSHOT.jar -bm avgt -tu ns -wi 2 -i 3 -w 1s -r 1s -f 1 org.bench.SpatialLocalityBenchmark
```