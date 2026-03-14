package mission1;


public class test {
	/**	依任務這個類別設抽象BeverageMaker就是要讓後續的飲料
	 *  像是Tea和coffee這些去繼承，這樣他們既可以使用規定流程的prepare
	 *  一樣的流程就不用去重複，而個別不同的部分由於用了抽象所以必須個別
	 *  實作個別的部分
	 */
	public static void main(String[] args) {
	  System.out.println( "首先先煮茶(不加冰)\n");	  	
	  BeverageMaker tea = new TeaMaker();
	  tea.prepare();
	  System.out.println( "-------------------------------");	
	  System.out.println( "然後做冰咖啡\n");	  
	  BeverageMaker coffee  = new CoffeeMaker();
	  coffee.prepare();
	} // 
}


