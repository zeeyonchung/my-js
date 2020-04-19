## 스트림
참고: [DishSortApp](./stream)

- 스트림이란, 데이터 처리 연산을 지원하도록 소스에서 추출된 연속된 요소
    - 연속된 요소: 특정 요소 형식으로 이루어진 연속된 값 집합의 인터페이스
    - 소스: 스트림은 컬렉션, 배열, I/O 자원등의 데이터 제공 소스로부터 데이터를 소비한다. 
    - 데이터 처리 연산: filter, map, reduce, find, match, sort 등으로 데이터를 조작할 수 있다.
    
- 스트림의 중요 특징
    - 파이프라이닝 Pipelining: 대부분의 스트림 연산은 스트림 연산끼리 연결해서 커다란 파이프라인을 구성할 수 있도록 스트림 자신을 반환한다.
    - 내부 반복: 반복자를 이용해서 명시적으로 반복하는 컬렉션과 달리 스트림은 내부 반복을 지원한다.
    
```
List<String> threeHighCaloricDishNames = menu.stream()
            .filter(dish -> dish.getCalories() > 300)
            .map(Dish::getName)
            .limit(3)
            .collect(Collectors.toList());

System.out.println(threeHighCaloricDishNames);

/**
* 데이터 소스 menu에서 스트림을 얻어 일련의 데이터 처리 연산을 적용한다.
* 마지막에 collect를 호출하기 전까지는 menu에서 무엇도 선택되지 않으며 출력 결과도 없다.
* 즉, collect가 호출되기 전까지는 메서드 호출이 저장되는 효과가 있다.
*/
```

- 스트림과 컬렉션
    - 컬렉션은 혀재 자료구조가 포함하는 모든 값을 메모리에 저장하는 자료구조다. 즉, 컬렉션의 모든 요소는 컬렉션에 추가하기 전에 계산되어야 한다. 스트림은 요청할 때만 요소를 계산하는 고정된 자료구조다.
    - 컬렉션 반복자와 마찬가지로 스트림도 한 번만 탐색할 수 있다. 한 번 탐색한 요소를 다시 탐색하려면 초기 데이터 소스에서 새로운 스트림을 만들어야 한다.
    ```
        List<String> alphabets = Arrays.asList("a", "b", "c");
        Stream<String> s = alphabets.stream();
        s.forEach(System.out::println);
        s.forEach(System.out::println); //java.lang.IllegalStateException 스트림이 이미 소비되었거나 닫힘
    ```
    - 컬렉션 인터페이스를 사용하려면 사용자가 for-each 등을 사용해서 직접 요소를 반복해야 한다. 스트림 라이브러리는 반복을 알아서 처리하고 결과 스트림값을 어딘가에 저장해주는 내부 반복을 사용한다.

- 스트림의 장점
    - 선언형: 루프와 if 조건문 등을 사용할 필요없이 'OO를 선택하라' 같은 동작의 수행을 지정할 수 있다.
    - 조립할 수 있음: filter, sorted, map, collect 같은 여러 빌딩 블록 연산을 연결해서 복잡한 데이터 처리 파이프라인을 만들 수 있다. 여러 연산을 연결해도 가독성과 명확성이 유지된다.
    - 병렬화: 스트림 연산은 단일 스레드 모델에 사용할 수 있지만 멀티코어 아키텍쳐를 활용할 수 있게 구현되어 있어, 우리가 데이터 처리 과정을 병렬화하면서 스레드와 락을 걱정할 필요가 없다.
    
- 스트림 연산
    - 중간연산
        - filter, sorted, map, limit 등
        - 다른 스트림을 반환한다. 따라서 여러 중간연산을 연결하여 질의를 만들 수 있다.
        - 단말연산을 스트림 파이프라인에 실행하기 전까지는 아무 연산도 수행하지 않는다.
    - 최종연산
        - collect, forEach, count 등
        - 스트림 파이프라인에서 결과를 도출한다.