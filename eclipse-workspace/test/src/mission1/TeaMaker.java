package mission1;

public class TeaMaker extends BeverageMaker {

  public void brew() { // (overide)實做抽象類別
    System.out.println("浸泡茶葉3分鐘");
  } // brew()
  public void addCondiments() { // (overide)實做抽象類別
    System.out.println("添加檸檬等額外添加物");	  
  } // addCondiments
  
}