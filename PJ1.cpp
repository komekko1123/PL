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
#include <ctype.h> // 用來看symbol是否可Print
using namespace std;


bool isNumber(char c){
  if( c >= '0' && c <= '9' )
    return true;
  else 
    return false;
} // isNumber()

bool isSymbolCharacter(char c){
  if ( c == ' ' || c == '\t' || c == '\n' || c == '\'' || c == '\"' || c == '(' || c == ')' || c == ';' || isNumber(c) ) // whitespace and illegal character
    return false;
  if( isprint(c) ) // c的函數庫，用來看可不可print
    return true;
  else
    return false;
} // isSymbol



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
  SYM, 
  ERR, // Error
};

enum SexpType{
  Number,
  SYMBOL,
  ERROR,
  FUNCTION,
  LIST,
};
/*
// Token
  LEFT-PAREN       // '('
  RIGHT-PAREN      // ')'
  INT              // e.g., '123', '+123', '-123'
  STRING           // "string's (example)." (strings do not extend across lines)
  DOT              // '.'
  FLOAT            // '123.567', '123.', '.567', '+123.4', '-.123'
  NIL              // 'nil' or '#f', but not 'NIL' nor 'nIL'
  T                // 't' or '#t', but not 'T' nor '#T'
  QUOTE            // '
  SYMBOL
<S-exp> ::= <ATOM> 
            | LEFT-PAREN <S-exp> { <S-exp> } [ DOT <S-exp> ] RIGHT-PAREN
            | QUOTE <S-exp>
            
<ATOM>  ::= SYMBOL | INT | FLOAT | STRING 
            | NIL | T | LEFT-PAREN RIGHT-PAREN

<ATOM> = SYMBOL | NUMBER | NIL AND T STRING | ()
*/



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

  
  Token(tokenType type,string value, int line, int column){
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
    mcolumn = 1;
  } // Scanner()

  char getTheChar(){ // 要這個function是因為getchar時要更新line跟column，很多地方到要用到。
    char c = cin.get();
    if( c == '\n'){
      mline = mline + 1;
      mcolumn = 1;
    } // if

    else
      mcolumn = mcolumn + 1;
    return c;
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

    return mpeekToken;
  } // peekToken()

  Token scanToken(){
    skipWhiteSpaceAndComment(); // 先跳過空白的Token
    int sline = mline; // start的line和column
    int scolumn = mcolumn; 
    if( cin.eof() == true )
      return Token(ERR, "EOF1" , sline , scolumn );
    char c = getTheChar();
    string str;
    str = str + c; //先加入
    char nextchar = cin.peek(); //先PEEK

    if( c == '(' ){ // ( 左刮要peek下一個是不是右刮)
      if( nextchar == ')'){
        c = getTheChar(); // 左右刮是NIL
        return Token(NIL, "()", sline, scolumn);
      } // if
      return Token(LP, "(", sline, scolumn);
    } // if
    else if( c == ')' )
      return Token(RP, ")", sline, scolumn);
    else if( c == '\'')
      return Token(QUOTE, "'", sline, scolumn);
    else if( c == '\"'){ // 雙括是字串***********\n要的話要throw error嗎
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
        return Token(ERR, "EOF2", mline , mcolumn );
      else
        return Token(STRING, str, sline, scolumn );
    } // else if

    else if( isNumber(c)  || c == '+' || c == '-' || c == '.' ){ // 數字相關，要peek下一個token
      bool haveDot = false;
      bool haveNumber = false;
      bool haveSym = false;
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

        else if (  isSymbolCharacter(nextchar) ){ // +-號且遇到非兩者的token代表著這是symbol
          haveSym = true;
          while( isSymbolCharacter(nextchar) ) {// 可以add進去
            c = getTheChar();
            str = str + c;
            nextchar = cin.peek();
          } // while
          break;
        } // else if 
        else 
          break;
        str = str + c;
        nextchar = cin.peek();
      } // while

      if( str == "." && haveDot == true && haveNumber == false ) // only dot
        return Token(DOT, ".", sline, scolumn);
      else if ( haveDot == true && haveNumber == true && haveSym == false )  //有福點數
        return Token(FLOAT, str, sline, scolumn);
      else if ( haveDot == false && haveNumber == true && haveSym == false ) // 只有數字
        return Token(INT, str, sline, scolumn);
      else // 其他的應該都是symbol
        return Token(SYM, str, sline, scolumn);
    } // else if

    else if( isSymbolCharacter( c ) ){ // symbol的條件
      while( isSymbolCharacter(nextchar) ) {// 可以add進去
        c = getTheChar();
        str = str + c;
        nextchar = cin.peek();
      } // while  

      if( str == "#f" || str == "nil" )
        return Token(NIL, str, sline, scolumn);
      else if( str == "" )

      else if( str == "" )
    } // else if

    else {
      str = str + " is the UNRECOGNIZE TOKEN(Error)";
      return Token(ERR, str, sline, scolumn);
    } // else 
  } // scanToken()

}; // Scanner
  

int main(){
  int utestnum = 0;
  char ch = '\0';
  cin >> utestnum; // read testnum
  cin >> ch; // read \n
  while( true ){
    break;
  } // 
  cout << "Welcome to OurScheme!" << endl << "> ";	
  return 0;
} // main


