package mission4;

public class GreenTea extends Ingredient {
	private Ingredient drink;
    public GreenTea(Ingredient drink) {
        this.name = "綠茶";
        this.cost = 20;
        this.drink = drink;
        if( (drink.name.indexOf("珍珠") != -1 && drink.name.indexOf("鮮奶") != -1) || drink.name.indexOf("珍珠綠茶拿鐵") != -1   )
        	this.name = "珍珠綠茶拿鐵";	
	    else if( drink.name.indexOf("珍珠") != -1 || drink.name.indexOf("珍珠綠") != -1 )	
	    	this.name = "珍珠綠";
	    else if( drink.name.indexOf("鮮奶") != -1 || drink.name.indexOf("珍珠綠茶拿鐵") != -1 )	
	    	this.name = "奶綠"; 
	    else 
	    	this.name = drink.name + " + 綠茶";
    }
    
    public GreenTea() {
        this.name = "綠茶";
        this.cost = 20;
    }

	  public String getIngredient() {
		if( drink != null)
	      return "綠茶 " + drink.getIngredient();
		else 
		  return "綠茶";	
	  }

	  public int getCost() {
		    if( drink != null)
			  return cost + drink.getCost();
			else 		  
		      return cost;
		  }
}
