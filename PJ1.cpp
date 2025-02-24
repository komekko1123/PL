#include <stdio.h>
#include <iostream>
#include <vector>
#include <string>


using namespace std;


string addBinary(string a, string b) {
      int al = a.length();
      int bl = b.length();
      int i = 0;
      if( al < bl ){   
        swap(a,b);
        swap(al,bl);
      } // if  
      char carry = '0';
      string ans = "";
      for( i = 0 ; i < bl ; i++){
        if( carry == '0' ){
          if( a[i] == '1' && b[i] == '1' ) {
            carry = '1';
            ans = ans + '0';	
          } // if
          else if( a[i] == '0' && b[i] == '0' ) 
            ans = ans + '0';
          else
            ans = ans + '1';
        } // if

        else if( carry == '1' ){
          if( a[i] == '1' && b[i] == '1' )
            ans = ans + '1';
          else if( a[i] == '0' && b[i] == '0' ){
            carry = '0';
            ans = ans + '1';  
          } // else if
          else
            ans = ans + '0';
        } // else if
        cout << ans << endl; 
      } // for

      for( i = bl ; i < al ; i++){
        if( carry == '0' ){
          if( a[i] == '1' )
            ans = ans + '1';
          else if( a[i] == '0' )
            ans = ans + '0';
        } // if

        else if( carry == '1' ){
          if( a[i] == '1')
            ans = ans + '0';
          else if( a[i] == '0'){
            ans = ans + '1';  
            carry = '0';
          } // else if
        } // else if
      } // for
      // 1010
      return ans;  
}


    int rangeBitwiseAnd(int left, int right) {
      if(left == 0 )
        return 0;
      else if (left == right)
        return left;  
      int ans = 1;
      for( ; right >= 0 ; right = right >> 1){ // ��X���ƪ�exp
    	cout << "left: " << left << endl;
    	cout << "right: " << right << endl;
        if(left == 0 ){
          if( right == 0)
            return ans;
          else 
            return 0;
        } // if
        left = left >> 1;
        ans *= 2;
      } // for
      return 0;
    }
    
int main(){
  cout << "123141242" << endl;	
  // printf("1231231231231231Error!n\n");
  return 0;
} // main


