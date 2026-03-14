package mission4;

public class BlackTea extends Ingredient {
	  private Ingredient drink;
	  	
	  public BlackTea(Ingredient drink) {
		        this.name = "紅茶";
		        this.cost = 20;
		        this.drink = drink;
		        if( (drink.name.indexOf("珍珠") != -1 && drink.name.indexOf("鮮奶") != -1) || drink.name.indexOf("珍珠紅茶拿鐵") != -1  )
		        	this.name = "珍珠紅茶拿鐵";	
			    else if( drink.name.indexOf("珍珠") != -1 || drink.name.indexOf("珍珠綠茶拿鐵") != -1 )	
			    	this.name = "珍珠紅";
			    else if( drink.name.indexOf("鮮奶") != -1 || drink.name.indexOf("珍珠綠茶拿鐵") != -1)	
			    	this.name = "紅茶拿鐵"; 
			    else 
			    	this.name = drink.name + " + 紅茶";
	  } // BlackTea

	  public BlackTea() {
	        this.name = "紅茶";
	        this.cost = 20;
	  } // BlackTea
	  
	  public String getIngredient() {
		if( drink != null)
	      return "紅茶 " + drink.getIngredient();
		else 
		  return "紅茶";	
	  }

	  public int getCost() {
	    if( drink != null)
		  return cost + drink.getCost();
		else 		  
	      return cost;
	  }
}
