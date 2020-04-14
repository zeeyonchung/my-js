package modernjava.behaviorparameterization;

public class Apple {

    public enum Color {
        RED, GREEN;
    }

    private Color color;
    private int weight;

    public Apple(Color color, int weight) {
        this.color = color;
        this.weight = weight;
    }

    public Color getColor() {
        return color;
    }

    public int getWeight() {
        return weight;
    }
}
