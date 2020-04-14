package modernjava.behaviorparameterization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 사과를 주어진 조건에 맞게 필터링한다.
 */
public class AppleFilterApp {

    public static void main(String[] args) {

        List<Apple> apples = Arrays.asList(
                new Apple(Apple.Color.GREEN, 180),
                new Apple(Apple.Color.GREEN, 110),
                new Apple(Apple.Color.RED, 160));

        List<Apple> redAndHeavyApples = filterApples(apples, new AppleRedAndHeabyPredicate());

        /**
         * 익명 클래스 사용
         * 매번 Predicate 클래스를 정의하고 인스턴스화 하지 않고 익명 클래스를 사용할 수 있다.
         */
        List<Apple> greenApples = filterApples(apples, new ApplePredicate() {
            @Override
            public boolean test(Apple apple) {
                return Apple.Color.GREEN.equals(apple.getColor());
            }
        });

        /**
         * 람다 표현식 사용
         * 위 익명 클래스 사용 방식은 코드가 장황하므로 이해하기 쉽지 않다.
         */
        List<Apple> redApples = filterApples(apples, apple -> Apple.Color.RED.equals(apple.getColor()));
    }

    /**
     * 동작 파라미터화 - Predicate 사용
     * 요구사항에 유연하게 대응하도록 필터링 동작을 파라미터화 한다.
     * 필요한대로 다양한 ApplePredicate를 만들어 filterApples 메서드로 전달하면 된다.
     */
    public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
        List<Apple> result = new ArrayList<>();

        for (Apple apple : inventory) {
            if (p.test(apple)) {
                result.add(apple);
            }
        }

        return result;
    }
}
