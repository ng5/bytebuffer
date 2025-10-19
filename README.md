bytebuffer

This simple benchmark tests the performance of the ByteBuffer class compared to ArrayList<Object>.
In each collection, there are N long and N double numbers. It tests five scenarios:

1) Sum of elements in ArrayList
2) Sum of elements in ByteBuffer
3) Sum of elements in ByteBuffer using VarHandle
4) Sum of elements in ByteBuffer directly allocated
5) Sum of elements in ByteBuffer directly allocated and using VarHandle

HOW TO RUN

```
mvn clean package
java -jar target/bytebuffer-1.0-SNAPSHOT.jar -bm avgt -tu ns -wi 2 -i 3 -w 1s -r 1s -f 1 org.bench.SpatialLocalityBenchmark
```