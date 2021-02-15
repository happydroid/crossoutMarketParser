package main;

public enum RarityEnum {
    Common("Common"),
    Rare("Rare"),
    Special("Special"),
    Epic("Epic"),
    Legendary("Legendary"),
    Relic("Relic");

    private final String text;

    RarityEnum(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
