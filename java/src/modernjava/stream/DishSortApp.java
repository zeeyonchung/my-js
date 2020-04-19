package modernjava.stream;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 칼로리를 기준으로 요리를 정렬한다.
 */
public class DishSortApp {

    public static void main(String[] args) {

    }

    /**
     * stream 사용
     * 400칼로리 미만의 요리 선택 후 칼로리로 요리 정렬
     * @param menu
     * @return
     */
    private List<String> getNames(List<Dish> menu) {
        return menu.stream()
                .filter(d -> d.getCalories() < 400)
                .sorted(Comparator.comparing(Dish::getCalories))
                .map(Dish::getName)
                .collect(Collectors.toList());
    }

    /**
     * parallelStream 사용
     * 멀티코어 아키텍처에서 병렬로 실행
     * 400칼로리 미만의 요리 선택 후 칼로리로 요리 정렬
     * @param menu
     * @return
     */
    private List<String> getNames2(List<Dish> menu) {
        return menu.parallelStream()
                .filter(d -> d.getCalories() < 400)
                .sorted(Comparator.comparing(Dish::getCalories))
                .map(Dish::getName)
                .collect(Collectors.toList());
    }

    public List<Dish> getDished() {
        return Arrays.asList(
                new Dish("pork", false, 800, Dish.Type.MEAT),
                new Dish("beef", false, 700, Dish.Type.MEAT),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("pizza", true, 550, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("salmon", false, 450, Dish.Type.FISH)
        );
    }
}
