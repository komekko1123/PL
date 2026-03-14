package mission4;

public class Milk extends Ingredient {
  private Ingredient drink;
  public Milk(Ingredient drink) {
	        this.name = "鮮奶" ;
	        this.cost = 20;
	        this.drink = drink;
	        if( (drink.name.indexOf("紅") != -1 && drink.name.indexOf("珍珠") != -1) || drink.name.indexOf("珍珠紅茶拿鐵") != -1)
	        	this.name = "珍珠紅茶拿鐵";	
	        else if( (drink.name.indexOf("綠") != -1 && drink.name.indexOf("珍珠") != -1) || drink.name.indexOf("珍珠綠茶拿鐵") != -1)	
	        	this.name = "珍珠綠茶拿鐵";
	        else if( drink.name.indexOf("紅茶") != -1 || drink.name.indexOf("紅茶拿鐵") != -1)
	        	this.name = "紅茶拿鐵";
	        else if( drink.name.indexOf("綠茶") != -1  || drink.name.indexOf("奶綠") != -1 )
	        	this.name = "奶綠";
		    else 
		    	this.name = drink.name + " + 鮮奶";
	        
  } // Milk

  public Milk() {
      this.name = "鮮奶";
      this.cost = 20;
  } 

  
  public String getIngredient() {
	if( drink != null)
      return "鮮奶 " + drink.getIngredient();
	else 
	  return "鮮奶";	
  }

  public int getCost() {
	    if( drink != null)
		  return cost + drink.getCost();
		else 		  
	      return cost;
	  }
}
