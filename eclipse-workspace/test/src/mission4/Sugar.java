package mission4;

public class Sugar extends Ingredient {
	  private Ingredient drink;
	  public Sugar(Ingredient drink) {
	        this.name = "糖";
	        this.cost = 5;
	        this.drink = drink;
	  } // Sugar

	    public Sugar() {
	        this.name = "糖";
	        this.cost = 5;
	    } 
	  
	  public String getIngredient() {
			if( drink != null)
		      return "糖 " + drink.getIngredient();
			else 
			  return "糖";	
		  }

		  public int getCost() {
			    if( drink != null)
				  return cost + drink.getCost();
				else 		  
			      return cost;
			  }
}
