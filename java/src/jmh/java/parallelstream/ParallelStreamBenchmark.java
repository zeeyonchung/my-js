package parallelstream;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime) //벤치마크 대상 메서드를 실행하는 데 걸린 평균 시간 측정
@OutputTimeUnit(TimeUnit.MILLISECONDS) //벤치마크 결과를 밀리초 단위로 출력
@Fork(value = 2, jvmArgs = {"-Xms4G", "-Xmx4G"}) //4gb의 힙 공간을 제공한 환경에서 두 번 벤치마크를 수행해 결과의 신뢰성 확보
@Measurement(iterations = 2)
@Warmup(iterations = 3)
public class ParallelStreamBenchmark {
    private static final long N = 10_000_000L;

    @Benchmark //벤치마크 대상 메서드
    public long iterativeSum() {
        long result = 0;
        for (long i = 1L; i <= N; i++) {
            result += i;
        }
        return result;
    }

    @Benchmark
    public long sequentialSum() {
        return Stream.iterate(1L, i -> i + 1)
                .limit(N)
                .reduce(0L, Long::sum);
    }

    @Benchmark
    public long parallelSum() {
        return Stream.iterate(1L, i -> i + 1)
                .limit(N)
                .parallel()
                .reduce(0L, Long::sum);
    }

    @Benchmark
    public long sequentialRangedSum() {
        return LongStream.rangeClosed(1, N)
                .reduce(0L, Long::sum);
    }

    @Benchmark
    public long parallelRangedSum() {
        return LongStream.rangeClosed(1, N)
                .parallel()
                .reduce(0L, Long::sum);
    }


    @TearDown(Level.Invocation) //매 벤치마크를 실행한 다음에는 가비지 컬렉터 동작 시도
    public void tearDown() {
        System.gc();
    }
}

/*
쿼드코어에서 실행 결과

Benchmark                              Mode  Cnt   Score    Error  Units
ParallelStreamBenchmark.iterativeSum   avgt    4   2.772 ±  0.204  ms/op
ParallelStreamBenchmark.sequentialSum  avgt    4  59.052 ±  0.675  ms/op
ParallelStreamBenchmark.parallelSum    avgt    4  83.663 ± 60.060  ms/op

Why? 반복 작업은 병렬로 수행할 수 있는 독립 단위로 나누기가 어렵다.
리듀싱 과정을 시작하는 시점에 전체 숫자 리스트가 준비되지 않았으므로 스트림을 병렬로 처리할 수 있도록 청크로 분할할 수 없다.
스트림이 병렬로 처리되도록 지시했고 각각의 합계가 다른 스레드에서 수행되었지만 결국 순차처리 방식과 크게 다른 점이 없으므로 스레드를 할당하는 오버헤드만 증가하였다.


Benchmark                                        Mode  Cnt   Score    Error  Units
ParallelStreamBenchmark.sequentialRangedSum      avgt    4   4.856 ±  6.850  ms/op
ParallelStreamBenchmark.parallelRangedSum        avgt    4   3.897 ± 21.505  ms/op

올바른 자료구조를 선택해야 병렬 실행도 최적의 성능을 발휘할 수 있다.
 */
