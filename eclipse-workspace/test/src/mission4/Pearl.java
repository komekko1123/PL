package mission4;

public class Pearl extends Ingredient {
	  private Ingredient drink;
	  public Pearl(Ingredient drink) {
		        this.name = "珍珠";
		        this.cost = 10;
		        this.drink = drink;
		        if( drink.name.indexOf("紅茶拿鐵") != -1 || drink.name.indexOf("珍珠紅茶拿鐵") != -1  )
		        	this.name = "珍珠紅茶拿鐵";	
			    else if( drink.name.indexOf("奶綠") != -1 || drink.name.indexOf("珍珠綠茶拿鐵") != -1 )	
			    	this.name = "珍珠綠茶拿鐵";
			   else if( drink.name.indexOf("紅茶") != -1  || drink.name.indexOf("珍珠紅") != -1 )
				   this.name = "珍珠紅";
			   else if( drink.name.indexOf("綠茶") != -1  || drink.name.indexOf("珍珠綠") != -1  )
				   this.name = "珍珠綠";
			    else 
			    	this.name = drink.name + " + 珍珠";  
		        
	  } // Pearl
	    public Pearl() {
	        this.name = "珍珠";
	        this.cost = 10;
	    }
	  
	  
	  public String getIngredient() {
			if( drink != null)
		      return "珍珠 " + drink.getIngredient();
			else 
			  return "珍珠";	
		  }

		  public int getCost() {
			    if( drink != null)
				  return cost + drink.getCost();
				else 		  
			      return cost;
		 }
}
