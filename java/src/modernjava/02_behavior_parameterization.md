## 동작 파라미터화 코드 전달하기
참고: [AppleFilterApp](./behaviorparameterization)  

- 동작 파라미터화란 아직은 어떻게 실행할 것인지 결정하지 않은 코드 블럭. 이 코드 블럭은 나중에 프로그램에서 호출한다.  
변화하는 동작을 캡슐화한 다음 메서드로 전달해서 메서드의 동작을 파라미터화 한다.

- Predicate: 참 또는 거짓을 반환하는 함수. 선택 조건을 결정하는 인터페이스.  
프레디케이트를 사용해 다양한 동작을 한 개의 파라미터에 매핑할 수 있꼬, 컬렉션 탐색 로직과 각 항목에 적용할 동작을 분리할 수 있다 (strategy pattern).    

- 새로운 동작을 전달하려면 매번 Predicate 인터페이스를 구현하는 여러 클래스를 정의한 다음에 인스턴스화 해야 한다. 이를 개선하기 위해 익명 클래스를 사용할 수 있다.  
- 익명 클래스 사용은 코드가 장황해져 한 눈에 들어오지 않는다. 이를 개선하기 위해 람다 표현식을 사용할 수 있다.

---
### 예시
- Comparator 객체를 이용하여 List의 sort 메서드의 동작을 파라미터화
```
// java.util.Comparator
public interface Comparator<T> {
    int compare(T o1, T o2);
}
```
```
inventory.sort(new Comparator<Apple>() {
    public int compare(Apple a1, Apple a2) {
        return a1.getWeight.compareTo(a2.getWeight());
    }
});
```

- Runnable 객체를 이용하여 Thread의 동작을 파라미터화
```
// java.lang.Runnable
public interface Runnable {
    void run();
}
```
```
Thread t = new Thread(new Runnable() {
    public void run() {
        System.out.println("Hello World");
    }
});
```
람다표현식을 이용하면 다음처럼 스레드 코드를 구현할 수 있다.
```
Thread t = new Thread(() -> System.out.println("Hello World"));
```

- Callable 객체를 이용하여 ExecutorService의 동작을 파라미터화
```
// java.util.concurrent.Callable
public interface Callabke<V> {
    V call();
}
```
```
ExecutorService executorService = Executor.newCachedThreadPool();
Future<String> threadName = executorService.submit(new Callable<String>() {
    @Override
    public String call() throws Exception() {
        return Thread.currentThread().getName();
    }
});
```
람다표현식을 이용하면 다음처럼 태스크 코드를 구현할 수 있다.
```
Future<String> threadName = executorService.submit(() -> Thread.currentThread().getName());
```