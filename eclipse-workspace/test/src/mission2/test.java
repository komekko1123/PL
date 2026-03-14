package mission2;


public class test {	
	public static void main(String[] args) {
		MySort ms = new MySort(); 
		Dog[] arr = new Dog[6]; //假設比較狗這個物件
		arr[0] = new Dog("雞塊",49);
		arr[1] = new Dog("叔叔",86);
		arr[2] = new Dog("漢堡",30);
		arr[3] = new Dog("奶昔",40);
		arr[4] = new Dog("薯條",15);
		arr[5] = new Dog("麥當勞",68); 
		System.out.println( "排序前");	  
		for( Dog var: arr) {
		  var.printAll(); // show出排序前的所有資訊	
		} // for
		ms.sort(arr); //做排序
		System.out.println( "\n排序後");	  
		for( Dog var: arr) {
		  var.printAll(); // show出排序後的所有資訊	
		} // for
	}
}
