package mission1;

public class CoffeeMaker extends BeverageMaker {
	  public void brew() { // (overide)實做抽象類別
	    System.out.println("把熱水倒進磨好的咖啡粉");
	  } // brew()
	  public void addCondiments() { // (overide)實做抽象類別
	    System.out.println("添加糖或牛奶等額外添加物");	  
	  } // addCondiments
	  
	  public void addIce() { //要加冰所以加冰塊
	    System.out.println("加大冰塊");	 
	  } // 
}