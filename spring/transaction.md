# Transaction


### 주의사항 
1. Spring Transaction은 default로 AOP Proxy 기반이다.
    [docs 읽기](https://docs.spring.io/spring/docs/current/spring-framework-reference/data-access.html#spring-data-tier)  
    - 어플리케이션이 실행되면 Service의 Proxy객체가 생성되고 Proxy를 거쳐 Transaction에 대한 처리가 이루어진다. 
    @Transactional이 적용된 로직 메서드를 호출하면 그 로직 메서드가 바로 호출되는 게 아니라, 
    Proxy를 거쳐 TransactionAdvisor에서 Transaction을 시작한 후 로직 메서드가 실행되고, 다시 TransactionAdvisor에서 실행 후처리(커밋 or 롤백)를 한다.
    - private 메서드에는 @Transactional을 적용할 수 없다.
	- Transaction이 적용되지 않은 메서드에서 같은 클래스 내의 Transaction 메서드를 호출하는 경우 Proxy에 의해 wrapping되지 않고 
	그 인스턴스의 메서드가 바로 호출되기 때문에 Transaction도 동작하지 않게 된다. 그래서 아래와 같이 쓰기도 한다.  
 	  
    ```
    public class UserService {
        @Autowired
        private UserService self; //두둥!
    
        public method1(List<User> users) {
            for (User user : users) {
                self.method2();
            }
        }
    
        @Transactional
        public method2() {
            //...
        }
    }
    ```

2. @Transactional의 default propagation은 REQUIRED로, 사용 중인 Transaction을 유지한다. 
    - readOnly Transaction 메서드에서 not-readOnly Transaction 메서드를 호출하는 경우 모든 메서드는 readOnly Transaction으로 동작한다. (반대도 마찬가지)  
	- Transaction 설정을 바꾸고 싶으면 하위 메서드의 Transaction propagation을 REQUIRED_NEW로 설정해서 새로운 Transaction을 사용하게 해야 한다.