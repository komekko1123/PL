package mission4;

public abstract class Ingredient {
    protected String name;
    protected int cost;

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public abstract String getIngredient();
}

