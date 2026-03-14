package mission1;

public abstract class BeverageMaker {
	public final void prepare() {
      boilWater();
      brew();
      pourInCup();
      addIce();
      addCondiments();
    } 
  
  public void boilWater() {
    System.out.println("煮水");
  }
  
  public abstract void brew();
  
  public void pourInCup() {
    System.out.println("倒進杯子");
  }
  
  public void addIce(){ // 就是foo()的什麼都不做
  } // addIce
  
  public abstract void addCondiments();
}
