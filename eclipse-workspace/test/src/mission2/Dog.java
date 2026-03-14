package mission2;

public class Dog implements CompareForSort{ // 假設我今天要排的是狗這個物件
	private String name;
	private int age;
	public Dog() {}; // 未給值的constructor
	public Dog(String name, int age) {  // 設定好名字跟年齡
	    this.name = name;
	    this.age = age;
	}
	public int getAge(){
	  return age;
	}     
	public String getName(){
	  return name;
	} 
    public void setAge(int age) {
	  if( age >= 0 )
	    this.age = age;	  
	}  
	public void setName(String name) {
	  this.name = name; 
	}  
	  
	public void printAll() {
	  System.out.println( "年齡: " + this.age + " 名字: "+ this.name  );	  
	}  
	  
	public int compare(Object o1) { // 主要讓物件知道(要比較什麼)
	  if( o1 instanceof Dog )	{ // 確定可以去做比較  
	    Dog d = (Dog)o1; 
	    if( this.getAge() > d.getAge() ) //假設使用者要必較年齡(比較的實物)
	      return 1;	
	    else if ( this.getAge() == d.getAge() )
	      return 0;
	    else 
	      return -1;
	  } // if
	  else { // 不同物件故跳例外情況
	    System.out.println("發現比較的物件為不同實體，故無法比較並跳入例外情況");	
	    throw new IllegalArgumentException("Objects are not of the same type.");
	  } // else  
	} // compare
} // Dog