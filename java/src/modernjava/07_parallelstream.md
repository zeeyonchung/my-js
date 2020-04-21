## 병렬 데이터 처리와 성능

- 자바7 이전에 데이터 컬렉션을 병렬로 처리하려면? 
데이터를 분할하여 각각의 스레드로 할당, 할당한 다음 의도치 않은 경쟁 상태가 발생하지 않도록 적절한 동기화를 추가, 마지막엔 부분 결과들을 합쳐야 한다.

- 병렬 스트림: 각각의 스레드에서 처리할 수 있도록 스트림 요소를 여러 청크로 분할한 스트림  
컬렉션의 parallelStream을 호출하면 병렬 스트림이 생성된다.
```
// 숫자 n을 받아서 1부터 n까지의 합을 구한다.
// 전통적인 자바에서는 반복문을 사용한다.
public long iterativeSum(long n) {
    long result = 0;
    for (long i = 1L; i <= n; i++) {
        result += i;
    }
    return result;
}

// 스트림을 써서 구현할 수 있다.
public long sequentialSum(long n) {
    return Stream.iterate(1L, i -> i + 1)  //무한 자연수 스트림 생성
                 .limit(n)
                 .reduce(0L, Long::sum);   //모든 숫자를 더하는 스트림 reduce 연산
}

// n이 커지면 위 연산을 병렬로 처리하는 것이 좋을 것이다.
public long parallelSum(long n) {
    return Stream.iterate(1L, i -> i + 1)
                 .limit(n)
                 .parallel()               //스트림을 병렬 스트림으로 변환
                 .reduce(0L, Long::sum);
}
```

- `sequential`로 병렬 스트림을 순차 스트림으로 바꿀 수 있다. `parallel`과 `sequential` 중 마지막으로 호출된 메서드가 전체 파이프라인에 영향을 미친다.
```
stream.parallel()
      .filter(...)
      .sequential(...)
      .map(...)
      .parallel(...)
      .reduce();

// 위 파이프라인은 병렬로 실행된다.
```

- ForkJoinPool  
병렬 스트림은 내부적으로 ForkJoinPool을 사용한다. ForkJoinPool은 기본적으로 프로세서 수에 상응하는 스레드를 갖는다. 
커스터마이징하려면 `System.setProperty("java.util.concurrent.ForkJoinPool.common.paralletism", "12);` 
그러나 전역 설정 코드이므로 이후의 모든 병렬 스트림 연산에 영향을 준다.

- 스트림의 성능  
    - 스트림이 전통적인 반복문보다 무조건 빠를까? 병렬 스트림이 스트림보다 무조건 빠를까? 
    [ParallelStreamBenchmark 확인](../jmh/java/parallelstream/ParallelStreamBenchmark.java)
    - 1~N까지의 합계 연산은 `LongStream.rangeClosed()`가 더 빠르다. 박싱/언박싱 오버헤드가 없고, `rangeClosed()`가 쉽게 청크로 분할할 수 있는 숫자 범위를 생산한다.  
    [ParallelStreamBenchmark 확인](../jmh/java/parallelstream/ParallelStreamBenchmark.java)