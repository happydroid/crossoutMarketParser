package calc;

public enum CategoryEnum {
    Weapons("Weapons"),
    Cabins("Cabins"),
    Hardware("Hardware"),
    Movement("Movement"),

    Decor("Decor"),
    Resources("Resources"),
    Dyes("Dyes");

    private final String text;

    CategoryEnum(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
