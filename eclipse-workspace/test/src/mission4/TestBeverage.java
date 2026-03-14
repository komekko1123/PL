package mission4;

public class TestBeverage {

	public static void main(String[] args) {
		// 紅茶
		Ingredient blackTea = new BlackTea();
        System.out.println("Beverage: " + blackTea.getName());
        System.out.println("Ingredient: " + blackTea.getIngredient());
        System.out.println("Cost: " + blackTea.getCost());
        System.out.println();
        
		// 大紅茶 (兩份紅茶)
		Ingredient BigBlackTea = new BlackTea(new BlackTea());
        System.out.println("Beverage: " + BigBlackTea.getName());
        System.out.println("Ingredient: " + BigBlackTea.getIngredient());
        System.out.println("Cost: " + BigBlackTea.getCost());
        System.out.println();
        
        // 奶綠 (綠茶拿鐵)
        Ingredient greenTeaLatte = new Milk(new GreenTea());
        System.out.println("Beverage: " + greenTeaLatte.getName());
        System.out.println("Ingredient: " + greenTeaLatte.getIngredient());
        System.out.println("Cost: " + greenTeaLatte.getCost());
        System.out.println();

        //珍珠
		Ingredient pearl = new Pearl();
        System.out.println("Beverage: " + pearl.getName());
        System.out.println("Ingredient: " + pearl.getIngredient());
        System.out.println("Cost: " + pearl.getCost());
        System.out.println();

        //糖
		Ingredient sugar = new Sugar();
        System.out.println("Beverage: " + sugar.getName());
        System.out.println("Ingredient: " + sugar.getIngredient());
        System.out.println("Cost: " + sugar.getCost());
        System.out.println();
        

        // 珍珠紅茶拿鐵
        Ingredient pearlBlackTeaLatte = new Milk(new Pearl(new BlackTea()));
        System.out.println("Beverage: " + pearlBlackTeaLatte.getName());
        System.out.println("Ingredient: " + pearlBlackTeaLatte.getIngredient());
        System.out.println("Cost: " + pearlBlackTeaLatte.getCost());
        System.out.println();
        
        
        pearlBlackTeaLatte = new Pearl(pearlBlackTeaLatte);
        System.out.println("Beverage: " + pearlBlackTeaLatte.getName());
        System.out.println("Ingredient: " + pearlBlackTeaLatte.getIngredient());
        System.out.println("Cost: " + pearlBlackTeaLatte.getCost());
        System.out.println();
        
        
        System.out.println("\n---結束---");
	}

}
