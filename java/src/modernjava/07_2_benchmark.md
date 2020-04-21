## 코드 수행시간 측정하기

### JMH (Java Microbenchmark Harness)  
- 자바 마이크로 벤치마크 라이브러리
- 자바 프로그램이나 JVM을 대상으로 하는 다른 언어용 벤치마크 구현 가능
- 어노테이션 기반 방식을 지원
- [참고 예제](https://github.com/melix/jmh-gradle-example/tree/master/src/jmh/java/org/openjdk/jmh/samples)

```
//[build.gradle]
plugins {
    // 벤치마크 편리하게 실행하기
    // https://github.com/melix/jmh-gradle-plugin
    id 'me.champeau.gradle.jmh'
}

dependencies {
    // 핵심 JMH 구현 포함
    compile group: 'org.openjdk.jmh', name: 'jmh-core', version: '1.23'
    // JAR 파일을 만드는 데 도움을 주는 어노테이션 프로세서 포함
    testCompile group: 'org.openjdk.jmh', name: 'jmh-generator-annprocess', version: '1.23'
}

jmh {
    // 설정
}
```

- 실행: `# gradle jmh`

- 결과 확인: `build/reports` 폴더
