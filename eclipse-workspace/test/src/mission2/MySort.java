package mission2;

public class MySort {
	/**
	 * 用interface來確定這些是寫好要比較什麼的自訂array
	 * 如果不是用interface去實作的array的話代表著物件沒有去
	 * 實做compare，自然的也沒法比較，所以要做到比較所有物件的話
	 * 就要叫使用者的物件去implement後才能達到全部都能排序的效果
	 */
    public void sort(CompareForSort[] arr) { 
    // 使用題目給的來做bubble sort
      int n = arr.length;
      for ( int i = 0; i < n; i++ ) {
        for ( int j = 0; j < n - i - 1; j++ ) {	
           if ( arr[j].compare(arr[j+1]) > 0 ) { // 比較
        	   CompareForSort temp = arr[j]; // swap
               arr[j] = arr[j+1];
               arr[j+1] = temp; 
           } // if
        }  // for
      }  // for
    } // sort
}



