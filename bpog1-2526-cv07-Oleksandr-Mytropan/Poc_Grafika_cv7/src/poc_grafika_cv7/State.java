package poc_grafika_cv7;

public class State {
    final private String name;
    final private String continent;

    public State(String name, String continent) {
        this.name = name;
        this.continent = continent;
    }

    public String getName() {
        return name;
    }

    public String getContinent() {
        return continent;
    }
}