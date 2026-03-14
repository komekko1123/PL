package mission5;

public class test {
	  public static int[][] CreateTriArr(int n) {
	        int[][] triArr = new int[n][];
	        
	        int count = 1;
	        for (int i = 0; i < n; i++) {
	            triArr[i] = new int[i + 1];
	            for (int j = 0; j <= i; j++) {
	                triArr[i][j] = count++;
	            }
	        }
	        
	        return triArr;
	    }
	    
	    public static void main(String[] args) {
	        int n = 4;
	        int[][] triangleArray = CreateTriArr(n);
	        bar b = new bar();
	        classA F = new classB();
	        F.foo();
	        // 印出三角矩陣
	        for (int i = 0; i < n; i++) {
	            for (int j = 0; j <= i; j++) {
	                System.out.print(triangleArray[i][j] + " ");
	            }
	            System.out.println();
	        }
	    }
	    
	 
}

class bar {
  public static final int BASL;
  static {
    System.out.print(false);
    BASL = 25;
  }
}
class classA {
	public static void foo() {
	  System.out.print("1");	
	}
}
class classB extends classA {
	public static void foo() {
		  System.out.print("2");	
		}
	}
