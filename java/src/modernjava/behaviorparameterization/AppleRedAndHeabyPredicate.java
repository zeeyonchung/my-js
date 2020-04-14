package modernjava.behaviorparameterization;

import static modernjava.behaviorparameterization.Apple.Color.RED;

public class AppleRedAndHeabyPredicate implements ApplePredicate {
    @Override
    public boolean test(Apple apple) {
        return RED.equals(apple.getColor()) && apple.getWeight() > 150;
    }
}
