// 啟航日3/5 pj1
// 完成日
#include <stdio.h>
#include <iostream>
#include <vector>
#include <string>
#include <sstream>
#include <map> 
#include <stdexcept> //錯誤處理
#include <iomanip> // for std::setprecision

using namespace std;

enum tokenType{
  LP, // ()
  RP, // )
  INT, // '123', +123 , -123
  STRING, // 雙引號包起來的字
  DOT, // .
  FLOAT, // 123.567 '123.' '.567', '+123.4', '-.123'
  NIL, // nil or #f 但是NIL,nIL不能
  T,  // 't' or '#t' 但是 'T', '#T'不能
  QUOTE, // '
  SYMBOL, 
  EOF, // EOF類別
  ERROR, // Error
};


class Token {
public:
  tokenType mtype;  
  string mvalue;
  int m_num;
  float mf_num;
  bool m_bool;
  int mline;
  int mcolumn;
  Token(){

  } // Token()

  
  Token(tokenType type,string mvalue, int mline, int mcolumn){
    mtype = type;
    mvalue = value;
    mline = line;
    mcolumn = column;
    if( type == INT )
      m_num = atoi( value.c_str() );
    else if( type == FLOAT )
      mf_num = atof( value.c_str() );
  } // Token()
}; // Token

class Scanner {
  bool m_havePeek;
  Token mpeekToken;
  int mline;
  int mcolumn;
  Scanner(){
    m_havePeek = false;
    mline = 1;
    mcolmun = 1;
  } // Scanner()

  char getTheChar(){ // 要這個function是因為getchar時要更新line跟column，很多地方到要用到。
    char c = cin.get();
    if( c == '\n'){
      line = line + 1;
      column = 1;
    } // if

    else
      column = column + 1;
    return c
    // getChar()怕跟getChar 撞到取getTheChar()
  } // getTheChar()


  void skipWhiteSpaceAndComment(){ // 跳過空白跟註解
    while( cin.good() ){ // cin,good的是可以讀的輸入，型別也同
      char c = cin.peek();
      if ( c == ' ' || c == '\t' || c == '\n'  )
        getTheChar(); // 等同getchar()
      else if ( c == ';' ){ // 這是註解要getchar到換行
        while( cin.good() && c != '\n' ){ // cin,
          c = getTheChar();
        } // while
      } // else if

      else
        break;
    } // while
  } // skipWhiteSpace()


  Token getToken(){
    Token t;
    if( m_havePeek == true ){
      m_havePeek = false;
      t = mpeekToken;
    } // if

    else
      t = scanToken();
    return t;
  } // getToken()
  
  Token peekToken(){
    if ( m_havePeek == false ){
      mpeekToken = scanToken();
      m_havePeek = true;
    } // if
  } // peekToken()

  Token scanToken(){
    skipWhiteSpace(); // 先跳過空白的Token
    int sline = mline; // start的line和column
    int scolumn = mcolumn; 
    if( cin.eof() == true )
      return Token(EOF, "EOF1" , sline , scolumn );
    char c = getTheChar();
    string str;
    if( c == '(' )
      return Token(LP, "(", sline, scolumn);
    else if( c == ')' )
      return Token(RP, ")", sline, scolumn);
    else if( c == '\'')
      return Token(QUOTE, "'", sline, scolumn);
    else if( c == '\"'){ // 雙括是字串
      str = str + c;
      bool closed = false;
      while( cin.good() ){
        char c = getTheChar();
        if ( c == '\"' ){
          closed = true;
          break;
        } // if

        if( c == '\\' ){
          char nextchar = cin.peek(); // 要看下一個是不是4個char，不是就是/
          if ( nextchar == '\\' ){
            getTheChar();
            str = str + "\\\\";
          } // if

          else if( nextchar == 'n' ){
            getTheChar();
            str = str + "\n";
          } // else if

          else if( nextchar == 't' ){
            getTheChar();
            str = str + "\t";  
          } // else if

          else if( nextchar == '\"' ){
            getTheChar();
            str = str + "\\\"";
          } // else if

          else
            str = str + c;
        } // if

        else
          str = str + c;
      } // while

      if ( closed == false ) // eof因為沒有"
        return Token(EOF, "EOF2" , mline , mcolumn );
      else
        return Token(STRING, str, sline, scolumn );
    } // else if

    else if( isNumber(c)  || c == '+' || c == '-' || c == '.' ){ // 數字相關，要peek下一個token
      str = str + c;
      char nextchar = cin.peek();
      bool haveDot = false;
      bool haveNumber = false;
      if ( c == '.')
        haveDot = true;
      if ( isNumber(c) )
        haveNumber = true;
      while( cin.good() ){
        if ( isNumber( nextchar ) ){
          haveNumber = true;
          c = getTheChar();
        } // if
        else if ( nextchar == '.' && haveDot == false  ) { //這是要有float的狀況
          haveDot = true;
          c = getTheChar();
        } // else if

        else
          break;
        str = str + c;
        char nextchar = cin.peek();
      } // while

      if ( (str == "+" || s == "-") &&  haveNumber == false ) //+. -. ,+ -都是 
        return Token(SYMBOL, str, sline)
      else if ( haveDot == true && haveNumber == true )
        return Token(FLOAT, str, sline, scolumn);
      else if( haveDot == true && haveNumber == false )
        return Token(DOT, ".", sline, scolumn);
      else if ( haveDot == false && haveNumber == true )
        return Token(INT, str, sline, scolumn);
      else if ( ) //加減開頭且下一個沒有數字跟dot
    } // if
  } // scanToken()

}; // Scanner
  
bool isNumber(char c){
  if( c >= '0' && c <= '9' )
    return true;
  else 
    return false;
} // isNumber()

int main(){
  cout << "Welcome to OurScheme!" << endl << "> ";	
  return 0;
} // main


