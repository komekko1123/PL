// 啟航日 

#include <stdio.h>
#include <iostream>
#include <vector>
#include <string>
#include <sstream> // 用stringstream，把int to str 
#include <map> 
#include <exception> //錯誤處理
#include <iomanip> // for setprecision(cout到小數第三位)
#include <ctype.h> // 用來看symbol是否可Print
#include <cstdlib>   // for atoi 和 atof
#include <functional> // 使用函數指標
#include <unordered_set> // 用來放保留字的

using namespace std;
class Sexp;
class SexpError;
class Environ;
bool isNumber(char c){
  if( c >= '0' && c <= '9' )
    return true;
  else 
    return false;
} // isNumber()

bool isSymbolCharacter(char c){
  if ( c == ' ' || c == '\t' || c == '\n' || c == '\'' || c == '\"' || c == '(' || c == ')' || c == ';' ) // whitespace and illegal character
    return false;
  if( isprint(c) ) // c的函數庫，用來看可不可print
    return true;
  else
    return false;
} // isSymbol


bool isReservedWord(string str){ // 因為不能define到這些元素，所以寫一個function出來看是不是。
  static const unordered_set<string> reserved = { // static是只會有一個，const是不會更改(因為是保留字)，加上用set比較快。
    // 保留字和function，沒有else，因為else只有在if時才有意義。
    "quote", "and", "or", "begin", "if", "cond",
    "define", "lambda", "set!", "let", "exit", "clean-environment",
    "verbose", "verbose?",
    // 找List元素和創造的東西
    "cons", "car", "cdr", "list",
    // 判定是不是
    "atom?", "pair?", "list?", "null?",
    "integer?", "real?", "number?", "string?", "boolean?", "symbol?",
    "eqv?", "equal?", "error-object?",
    // 算數
    "+", "-", "*", "/",
    ">", ">=", "<", "<=", "=",
    // 邏輯和T/F這些等等
    "not","nil","#f","#t","()",
    // 字串操作
    "string-append", "string>?", "string<?", "string=?", 
    "symbol->string", "number->string", "display-string",
    // I/O & 計算
    "read", "write", "newline", "eval", 
    "create-error-object", 
    // 可能是funcion;
    "set!"
  };
  return reserved.find(str) != reserved.end(); // 如果是保留字找到就會是true，不是保留字就回傳false
} // isReservedWord



enum tokenType{
  NOP, // NOTYPE
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
  NUMBER,
  SYMBOL,
  ERROR,
  FUNCTION,
  LIST,
  FUNCTION_LAMBDA, // 3/25新增()
  PAIR, // 3/28新增()
  NOTYPE,
};



class Token {
public:
  tokenType type;  
  string value;
  int num;
  float f_num;
  bool m_bool;
  int mline;
  int mcolumn;
  Token(){
    type = NOP;
    value = "";
    num = 0;
    f_num = 0;
    mline = 0;
    mcolumn = 0;
    m_bool = false;
  } // Token()

  
  Token(tokenType typeE,string valueE, int line, int column){
    type = typeE;
    value = valueE;
    mline = line;
    mcolumn = column;
    if( type == INT )
      this->num = atoi( value.c_str() );
    else if( type == FLOAT )
     this->f_num = atof( value.c_str() );
  } // Token()
}; // Token



class Sexp{
public:
  SexpType type;  
  tokenType mtype;
  string value;
  string errormessage;
  float number;
  vector<Sexp> Slist;
  vector<string> paras;
  function<Sexp(const vector<Sexp>&)> funptr; // 函數指標 
  static long nextID; // 3月28日(解決eqv要記憶體位址的問題!!!!!!!)
  long uniqueID;  // 3月28日(解決eqv要記憶體位址的問題!!!!!!!)

  Sexp(){  
    type = NOTYPE;
    mtype = NOP;
    value = "";
    number = 0;
    Slist.clear();
    paras.clear();
    uniqueID = nextID++;
  } // 3/28
  Sexp(SexpType Stype, string theValue ){
    type = Stype;
    value = theValue;
    mtype = ERR;
    uniqueID = nextID++;
  } // ERROR用到

  Sexp(SexpType Stype, vector<string> parameter ){
    type = Stype;
    paras = parameter;
    uniqueID = nextID++;
    mtype = NOP;
  } // ERROR用到

  Sexp(SexpType Stype, string theValue, vector<Sexp> theList ){
    number = 0;
    type = Stype;
    value = theValue;
    Slist = theList;
    uniqueID = nextID++;
    mtype = ERR;
  } // define error用的到


  Sexp(SexpType Stype, vector<Sexp> theList){
    type = Stype;
    Slist = theList;
    uniqueID = nextID++;
    mtype = NOP;
  } // LIST的模式

  Sexp(SexpType Stype, function<Sexp(const vector<Sexp>&)> fptr, string str){
    value = str; // 3/28順便記一下function的名字
    type = Stype;
    funptr = fptr;
    uniqueID = nextID++;
    mtype = NOP;
  } // 指向函數指標


  Sexp(SexpType Stype, tokenType theMtype,string theValue ){ // 感覺應該想好再寫的
    type = Stype;
    value = theValue;
    mtype = theMtype;
    if( mtype == INT || mtype == FLOAT ) 
      number = atof(value.c_str()); // 架構怪了atof
    if( mtype == FLOAT ) {
      stringstream ss;
      ss << fixed << setprecision(3) << number;
      value = ss.str();
    } // if
    //其他默認symbol
    uniqueID = nextID++;
  } // Sexp()
}; // Sexp
long Sexp::nextID = 1; // 3/28新增

  Sexp RebuildSexp( string message, Sexp token){ // 為了解錯誤訊息token
    token.errormessage = message;
    return token;
  } // RebuildSexp

class ErrorCondition : public exception {
  public:
      Sexp SexpErr;
      Token tErr;
      int line;
      int column;
      int errornum;
      ErrorCondition( Token t, Sexp Exp, int err, int l, int c){
        line = l;
        column = c;
        errornum = err;
        SexpErr = Exp;
        tErr = t;
      } // ErrorCondition

      void PrintError(){
        if( tErr.mline == 0 )
          tErr.mline = 1;
        if ( errornum == 1)
          cout << "ERROR (no more input) : END-OF-FILE encountered";
        else if ( errornum == 2 ){
          cout << "ERROR (no closing quote) : END-OF-LINE encountered at Line "<< tErr.mline << " Column " << tErr.mcolumn << endl;
        } // else if
        else if ( errornum == 3 ){
          cout << "ERROR (unexpected token) : atom or '(' expected when token at Line "<< tErr.mline << " Column " << tErr.mcolumn  << " is >>" << tErr.value << "<<" << endl;
        } // else if
        else if ( errornum == 4){
          cout << "ERROR (unexpected token) : ')' expected when token at Line "<< tErr.mline << " Column " << tErr.mcolumn  << " is >>" << tErr.value << "<<" << endl;
        } // else if
        else if ( errornum == 5)
          cout << "test error";
      } // PrintError

      
};

class SignalLight{
public:
  string value;
  SignalLight(){  
  }
  SignalLight(string theValue ){
    value = theValue;
  } //
}; // 

class Scanner {
public:
  bool m_havePeek;
  Token mpeekToken;
  int mline;
  int mcolumn;
  Scanner(){
    m_havePeek = false;
    mline = 1;
    mcolumn = 1;
  } // Scanner()

  void clear(){ //遇到error了之類的
    mpeekToken = Token();
    mline = 0;
    mcolumn = 1;
    char c = '\0';
    while( cin.good() ){ // cin,good的是可以讀的輸入，型別也同
      char c = cin.peek();
      if ( c == '\n'  ) // 換行就是正常狀況
        break; // 等同getchar()
      else if ( c == ';' ) // 註解就換行不會有奇怪狀況
        break;
      else if ( c == ' ' || c == '\t'){ // 這是要看是不是同一個的奇怪狀況
        getTheChar();
      } // else if
      else { // 有token怪怪的，mline要是1開頭
        mline = 1;
        break;
      } // else 
    } // while
    m_havePeek = false;
  } // clear

  char getTheChar(){ // 要這個function是因為getchar時要更新line跟column，很多地方到要用到。
    if ( cin.eof() )
      throw ErrorCondition(Token(),Sexp(),1,0,0);
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
      throw ErrorCondition(Token(),Sexp(),1,0,0);
    char c = getTheChar();
    string str;
    str = str + c; //先加入
    char nextchar = cin.peek(); //先PEEK

    if( c == '(' ){ // ( 左刮要peek下一個是不是右刮)
      skipWhiteSpaceAndComment();
      nextchar = cin.peek(); //先PEEK
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
      bool closed = false;
      while( nextchar != '\n' ){ // 如果遇到換行的話
        char c = getTheChar();
        if ( c == '\"' ){
          closed = true;
          str = str + c ;
          break;
        } // if

        if( c == '\\' ){
          char nextchar = cin.peek(); // 要看下一個是不是4個char，不是就是/
          if ( nextchar == '\\' ){
            getTheChar();
            str = str + "\\";
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
            str = str + "\"";
          } // else if

          else
            str = str + c;
        } // if

        else
          str = str + c;
        nextchar = cin.peek();
      } // while

      if ( closed == false ){ // eof因為沒有"
        Token errortoken = Token(ERR, "HELLO", mline , mcolumn );
        throw ErrorCondition(errortoken, Sexp(), 2, mline, mcolumn);
      } // if
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
      else if( str == "#t" || str == "t" )
        return Token(T, str, sline, scolumn);
      else
        return Token(SYM, str, sline, scolumn);
    } // else if

    else {
      str = str + " is the UNRECOGNIZE TOKEN(Error)"; // 應該是不會有這種情況
      Token terr = Token(ERR, str, sline, scolumn);
      throw ErrorCondition(terr,Sexp(),0,mline, mcolumn);
    } // else 
  } // scanToken()


  void encounterError(){
    char c = cin.peek();
    while( cin.good() ){ // cin,good的是可以讀的輸入，型別也同
      char c = cin.peek();
      if ( c == '\n' || c == EOF  ) // until read the \n
        break; // 等同getchar()
      else
        getTheChar();
    } // while  
  } // encounterError()

}; // Scanner


class Parser{
public:
  Scanner *sc;
  vector<Token> tokenlist;
  vector<Sexp> SexpList;  
  int index; // index to final token
  
  Parser(Scanner *scaner){
    sc = scaner;
    index = 0;
  } // Parser

  void getTokenToList(){
    Token t  = sc->getToken();
    if( tokenlist.size() == 0 )
      index = 0; 
    else
      index++;
    tokenlist.push_back(t);
  } // getTokenToList

  void startParsing(){
    this->clear();  
    getTokenToList();
    int i = 0;
    Sexp sexpToken;
    if ( isSExp() == true){
      sexpToken = changeToSexpList(i);
      SexpList.push_back(sexpToken);
    } // if
    else
      throw ErrorCondition(Token(),Sexp(),0,0, 0);
    
  } // startParsing

  bool isSExp(){ // 寫完才發現不用BOOL
    if ( tokenlist[index].type == LP ){

      getTokenToList();
      isSExp();
      getTokenToList();
      while( true){
        if ( tokenlist[index].type == DOT ){
          getTokenToList(); // 
          isSExp();
          getTokenToList(); // 拿右括
          if ( tokenlist[index].type != RP ){
            Token terr = tokenlist[index];
            terr.type = ERR;
            throw ErrorCondition(terr,Sexp(),4,terr.mline, terr.mcolumn);  // 沒有右瓜的UNEXPECTED TOKEN
          } // if
          
          return true; //有右刮就是合格語法
        } // if

        else if ( tokenlist[index].type == RP )
          return true;
        else {
          isSExp();  
          getTokenToList();
        } // else
      } // while
      return true;
    } // if

    else if( tokenlist[index].type == QUOTE ){
      getTokenToList(); // quote接一個token去做Sexp(遞迴)
      isSExp(); // 發現到用throw error好像就不用if了  
    } // else if

    else if( tokenlist[index].type == SYM || tokenlist[index].type == INT || tokenlist[index].type == FLOAT
       || tokenlist[index].type == STRING || tokenlist[index].type == NIL || tokenlist[index].type == T ){ // 這條長長的是Atom
      return true;
    } // else if

    else { // unexpect token了      
      Token terr = tokenlist[index];
      terr.type = ERR;
      throw ErrorCondition(terr,Sexp(),3,terr.mline, terr.mcolumn);
    } // else
     
    return true;
  } // isSExp


  Sexp changeToSexpList(int &loc){
    if( ( loc > index ) || tokenlist.size() == 0 )
      throw ErrorCondition(Token(),Sexp(),5,0, 0);
    Token t = tokenlist[loc];
    loc = loc + 1;
    Sexp s;
    s.mtype = t.type;
    s.value = t.value;
    if( t.type == QUOTE){ // '的定義是(quote Sexp)所以就先抓個sexp然後再把quote和sexp放入成list這樣
      Sexp innerSexp = changeToSexpList(loc);
      s.type = SYMBOL;
      vector<Sexp> sslist;
      sslist.push_back(s);
      sslist.push_back(innerSexp);
      return Sexp(LIST,sslist);
    } // if

    else if ( t.type == LP){
      vector<Sexp> sslist;
      Sexp backSexp;

      bool haveDot = false;
      Sexp dotSexp;
      dotSexp.type = SYMBOL;
      dotSexp.mtype = DOT;
      dotSexp.value = ".";


      while( loc < tokenlist.size() && tokenlist[loc].type != RP ){
        if( tokenlist[loc].type == DOT ){
          if( sslist.size() == 0){ // 實際上應該不會有這個問題，因為已經parser完了
            throw ErrorCondition(Token(),Sexp(),4,2002,1123);
          } // if
          
          loc = loc + 1; //去除DOT
          haveDot = true;

          backSexp = changeToSexpList( loc );
          if (tokenlist[loc].type != RP )  // 正常來講肯定要是這個元素，且確定文法寫走不到這。
            throw ErrorCondition(Token(),Sexp(),4,9999,8888);
          break;
        } // if

        Sexp innerSexp = changeToSexpList(loc);
        sslist.push_back( innerSexp );
      } // while

      loc = loc + 1; // 去除右刮號

      if ( haveDot == true){
        if ( backSexp.type == LIST || backSexp.type == PAIR ){ // 存LIST( 第三題的錯誤可能是這，要把list的內容全部拉進來..pretty print真的問題一堆)
          for( int i = 0 ; i < backSexp.Slist.size(); i++){ // 3/29 寫到project2新增了pair型別後，第三題的錯誤又來了。出現在第15題
            Sexp temp = backSexp.Slist[i];
            sslist.push_back(temp);
          } // for   
          return Sexp(LIST,sslist);
        } // if
        
        else if( backSexp.mtype == NIL ){ // 連存的不存
          return Sexp(LIST,sslist);
        } // else if

        else { // 其他就默認是atom，所以會像(1 . 2 ) 要存1 和 .和 2 
          sslist.push_back(dotSexp);
          sslist.push_back(backSexp);
          return Sexp(PAIR,sslist);  // 3/28新增把它變成PAIR(其實就是improper list)
        } // else
      } // if

      else { //寫到後面才發現確定文法不出錯了，不用作錯誤檢查
        return Sexp(LIST, sslist);
      } // else
    } // else if

    else if ( t.type == INT || t.type == FLOAT ){
      s.type = NUMBER;
      s.number = atof(t.value.c_str());
      return s;
    } // else if

    else if ( t.type == NIL || t.type == T || t.type == SYM || t.type == STRING ){
      s.type = SYMBOL;
      return s;
    } // else if

    else {
      cout << "maybe has wrong( "<< tokenlist[loc].value << ")" << endl;
      return Sexp();
    } // else  
  } // changeToSexpList

  vector<Sexp> getSexpList(){
    return SexpList;
  } // getTokenList

  Sexp getSexp(){
    return SexpList[SexpList.size()-1];
  } // getTokenList
  
  void clear(){
    tokenlist.clear();
  } // clear

};

// 用function pointer呼叫的function群
bool checkIsINT( const vector<Sexp> & ss ){
  for( int i = 0 ; i < ss.size() ; i++ ){
    if( ss[i].type == NUMBER && ss[i].mtype != INT )
      return false;
    else if ( ss[i].type != NUMBER && ss[i].mtype == INT ){
      return false;
    } // else if
    else if ( ss[i].type != NUMBER )
      return false;
  } // for
  return true;  
} // checkIsINT

bool checkIsFloat( const vector<Sexp> & ss){ // 先確定過int了代表float就可以直接過
  for( int i = 0 ; i < ss.size() ; i++ ){
    if ( ss[i].type != NUMBER ){
      return false;
    } // if
  } // for
  return true;  
} // checkIsFloat

bool checkIsString( const vector<Sexp> & ss){ // CHECK_STRING
  for( int i = 0 ; i < ss.size() ; i++ ){
    if ( ss[i].mtype != STRING ){
      return false;
    } // else if
  } // for
  return true;  
} // checkIsString

bool checkSexpEqual(const Sexp &a, const Sexp &b){ //其實只是把equal?拿來用，用遞迴來解決list跟pair(improper list)
  if( a.uniqueID == b.uniqueID ){  
    return true;
  } // if

  if( a.type != b.type )
    return false; 
  if( a.type == LIST || a.type == PAIR ){
    if (a.Slist.size() != b.Slist.size()) 
      return false;
    for(int i = 0 ; i < a.Slist.size() ; i++){
      if ( ! checkSexpEqual(a.Slist[i], b.Slist[i]) )
        return false;
    } // for
    return true;
  } // if

  else if( a.mtype == STRING ){
    if( a.value == b.value )
      return true;
    else  
      return false; 
  } // else if
  else if( a.type == NUMBER ){
    if( atof(a.value.c_str()) == atof(b.value.c_str()) )
      return true;
    else  
      return false; 
  } // else if

  else if( a.type == FUNCTION ){
    if( a.value == b.value )
      return true;
    else
      return false; 
  } // else if

  else if( a.type == FUNCTION_LAMBDA ){
    if( a.value == b.value )
      return true;
    else
      return false; 
  } // else if
  
  else if( a.mtype == NIL || a.mtype == T )
    return true;
  else if( a.type == SYMBOL ){ // 感覺不太可能走到這
    if( a.value == b.value )
      return true;
    else
      return false; 
  } // else if

  else // 感覺不會去比較error物件把
    return false; 
  cout << "到這邊可能有錯" << endl;
  return false;
} // checkSexpEqual


Sexp doAdd(const vector<Sexp> & selist  ){
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : +"); // argument量
  float f = 0;
  int a = 0;
  stringstream ss;
  if( checkIsINT(selist) == true ){
    a = atoi( selist[0].value.c_str() );
    for( int i = 1; i < selist.size() ; i++ )
      a += atoi(selist[i].value.c_str());
    ss << a;
    return Sexp(NUMBER,INT,ss.str());
  } // if

  else if ( checkIsFloat(selist) == true ){
    f = atof(selist[0].value.c_str());
    for( int i = 1; i < selist.size() ; i++ )
      f += atof(selist[i].value.c_str());
    ss << f;
    return Sexp(NUMBER,FLOAT,ss.str());
  } // else if    

  else {
    for(int i = 0 ; i < selist.size(); i++){
      if ( selist[i].type != NUMBER && ( selist[i].type == LIST || selist[i].type == PAIR ) ){
        throw Sexp(ERROR,"ERROR (+ with incorrect argument type) : ", selist[i].Slist);
      } // if  

      else if( selist[i].type != NUMBER )
        throw RebuildSexp("ERROR (+ with incorrect argument type) : ",selist[i]); // 丟錯誤物件  
    } // for
  } // else
  return Sexp(ERROR,"UNKNOWN FUNCTION ERROR");
} // add

Sexp doMinus(const vector<Sexp> & selist  ){
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : -"); // 
  float f = 0;
  int a = 0;
  stringstream ss;
  if( checkIsINT(selist) == true ){
    a = atoi( selist[0].value.c_str() );
    for( int i = 1; i < selist.size() ; i++ )
      a -= atoi(selist[i].value.c_str());
    ss << a;
    return Sexp(NUMBER,INT,ss.str());
  } // if

  else if ( checkIsFloat(selist) == true ){
    f = atof(selist[0].value.c_str());
    for( int i = 1; i < selist.size() ; i++ )
      f -= atof(selist[i].value.c_str());
    ss << f;
    return Sexp(NUMBER,FLOAT,ss.str());
  } // else if    

  else {
    for(int i = 0 ; i < selist.size(); i++){
      if ( selist[i].type != NUMBER && ( selist[i].type == LIST || selist[i].type == PAIR ) ){
        throw Sexp(ERROR,"ERROR (- with incorrect argument type) : ", selist[i].Slist);
      } // if  

      else if( selist[i].type != NUMBER )
        throw RebuildSexp("ERROR (- with incorrect argument type) : ",selist[i]); // 丟錯誤物件 
    } // for
  } // else
  return Sexp(ERROR,"UNKNOWN FUNCTION ERROR");
} // minus

Sexp doMultlipy(const vector<Sexp> & selist  ){
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : *"); // 
  float f = 0;
  int a = 0;
  stringstream ss;
  if( checkIsINT(selist) == true ){
    a = atoi( selist[0].value.c_str() );
    for( int i = 1; i < selist.size() ; i++ )
      a *= atoi(selist[i].value.c_str());
    ss << a;
    return Sexp(NUMBER,INT,ss.str());
  } // if

  else if ( checkIsFloat(selist) == true ){
    f = atof(selist[0].value.c_str());
    for( int i = 1; i < selist.size() ; i++ )
      f *= atof(selist[i].value.c_str());
    ss << f;
    return Sexp(NUMBER,FLOAT,ss.str());
  } // else if    

  else { // 可以是nil不能是string
    for(int i = 0 ; i < selist.size(); i++){
      if ( selist[i].type != NUMBER && ( selist[i].type == LIST || selist[i].type == PAIR ) ){
        throw Sexp(ERROR,"ERROR (* with incorrect argument type) : ", selist[i].Slist);
      } // if  

      else if( selist[i].type != NUMBER )
        throw RebuildSexp("ERROR (* with incorrect argument type) : ", selist[i]); // 丟錯誤物件 
    } // for
  } // else
  return Sexp(ERROR,"UNKNOWN FUNCTION ERROR");
} // multlipy

Sexp doDivide(const vector<Sexp> & selist  ){
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : /"); // 
  float f = 0;
  int a = 0;
  stringstream ss;
  if( checkIsINT(selist) == true ){
    a = atoi( selist[0].value.c_str() );
    for( int i = 1; i < selist.size() ; i++ ){
      if( selist[i].value == "0")
        throw Sexp(ERROR,"ERROR (division by zero) : /");
      a /= atoi(selist[i].value.c_str());
    } // for
    ss << a;
    return Sexp(NUMBER,INT,ss.str());
  } // if

  else if ( checkIsFloat(selist) == true ){
    f = atof(selist[0].value.c_str());
    for( int i = 1; i < selist.size() ; i++ ){
      float vf = atof(selist[i].value.c_str());
      if( vf == 0 )
        throw Sexp(ERROR,"ERROR (division by zero) : /");
      f /= vf;
    } // for
    ss << f;
    return Sexp(NUMBER,FLOAT,ss.str());
  } // else if    

  else {
    for(int i = 0 ; i < selist.size(); i++){
      if ( selist[i].type != NUMBER && ( selist[i].type == LIST || selist[i].type == PAIR ) ){
        throw Sexp(ERROR,"ERROR (/ with incorrect argument type) : ", selist[i].Slist);
      } // if  

      else if( selist[i].type != NUMBER )
        throw RebuildSexp("ERROR (/ with incorrect argument type) : ",selist[i]); // 丟錯誤物件  
    } // for
  } // else
  return Sexp(ERROR,"UNKNOWN FUNCTION ERROR");
} // divide


Sexp isNot(const vector<Sexp> & selist  ){
  if( selist.size() != 1  )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : not"); // 
  if( selist[0].mtype != NIL)
    return Sexp(SYMBOL, NIL, "nil");
  else
    return Sexp(SYMBOL, T, "#t");
} // isNot


Sexp cons(const vector<Sexp> & selist  ){ // 3/29 ERROR (level of CLEAN-ENVIRONMENT)抓到原因了。原因就是
  if( selist.size() != 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : cons"); // 
  vector<Sexp> temp;
  temp.push_back(selist[0]);
  
  if( selist[1].value == "()" || selist[1].value == "nil" || selist[1].value == "#f" || selist[1].mtype == NIL )
    return Sexp(LIST,temp);
  else if ( selist[1].type == LIST || selist[1].type == PAIR ) {
    for( int i = 0 ; i < selist[1].Slist.size() ; i++){
      temp.push_back(selist[1].Slist[i]);  
    } // for
    return Sexp(LIST,temp); // 3/28 improper list
  } // else

  else {
    Sexp dotSexp = Sexp(SYMBOL,DOT,".");
    temp.push_back(dotSexp);
    temp.push_back(selist[1]);
    return Sexp(PAIR,temp); // 3/28 improper list
  } // else
} // add

Sexp createList(const vector<Sexp> & selist  ){ // 空list是否回傳nil!!*******************
  vector<Sexp> temp;
  for( int i = 0 ; i < selist.size() ; i++){
    temp.push_back(selist[i]);
  } // for
  
  if( temp.size() == 0)
    return Sexp(SYMBOL,NIL,"nil");
  else
    return Sexp(LIST,temp);
} // add


Sexp car(const vector<Sexp> & selist  ){ //拿第一個元素
  if( selist.size() != 1 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : car"); // 
  if( selist[0].type != LIST && selist[0].type != PAIR )
    throw RebuildSexp("ERROR (car with incorrect argument type) : ", selist[0]); // 丟錯誤物件  
  return selist[0].Slist[0];
} // add

Sexp cdr(const vector<Sexp> & selist  ){ //拿剩下元素
  if( selist.size() != 1 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : cdr"); // 
  if( selist[0].type != LIST && selist[0].type != PAIR )
    throw RebuildSexp("ERROR (cdr with incorrect argument type) : ", selist[0]); // 丟錯誤物件  

  bool withdot = false;
  if( selist[0].Slist.size() == 3 && selist[0].type == PAIR ){ // ( 1 . 2 )會把dot稀釋掉，其他情況不會因為dot只能接一個sexp
    return selist[0].Slist[2];  //回傳第三個
  } // if
  
  else if(  selist[0].Slist.size() < 2)
    return Sexp(SYMBOL, NIL, "nil"); // 1和0 被去除後就是nil

  vector<Sexp> temp;
  for( int i = 1 ; i < selist[0].Slist.size() ; i++){
    temp.push_back(selist[0].Slist[i]);
    if( selist[0].Slist[i].value == "." || selist[0].Slist[i].mtype == DOT )
      withdot = true;
  } // for

  if( withdot == true) 
    return Sexp(PAIR,temp); // ( 1 2 . 3) 這樣的話去除第一個就是 ( 2 . 3 )這樣
  else
    return Sexp(LIST,temp);
} // add

Sexp predictAtom(const vector<Sexp> & selist  ){
  if( selist.size() != 1 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : atom?"); //
  if( ( selist[0].type == SYMBOL && selist[0].mtype != NIL ) || selist[0].type == NUMBER )
    return Sexp(SYMBOL,T,"#t");
  else
    return Sexp(SYMBOL,NIL,"nil");  
} // add

Sexp predictPair(const vector<Sexp> & selist  ){
  if( selist.size() != 1 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : pair?"); //
  if( selist[0].type == LIST || selist[0].type == PAIR )
    return Sexp(SYMBOL,T,"#t");
  else
    return Sexp(SYMBOL,NIL,"nil");  
} // add

Sexp predictList(const vector<Sexp> & selist  ){
  if( selist.size() != 1 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : List?"); //
  if( selist[0].type == LIST )
    return Sexp(SYMBOL,T,"#t");
  else
    return Sexp(SYMBOL,NIL,"nil"); 
} // add

Sexp predictNull(const vector<Sexp> & selist  ){
  if( selist.size() != 1 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : null?"); //
  if( selist[0].mtype == NIL )
    return Sexp(SYMBOL,T,"#t");
  else
    return Sexp(SYMBOL,NIL,"nil"); 
} // add

Sexp predictInteger(const vector<Sexp> & selist  ){
  if( selist.size() != 1 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : integer?"); //
  if( checkIsINT(selist) )
    return Sexp(SYMBOL,T,"#t");
  else
    return Sexp(SYMBOL,NIL,"nil"); 
} // add

Sexp predictNumber(const vector<Sexp> & selist  ){
  if( selist.size() != 1 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : number?"); //
  if( checkIsFloat(selist) ) // 本來float的設計是看不是int的number，所以這樣設計。
    return Sexp(SYMBOL,T,"#t");
  else
    return Sexp(SYMBOL,NIL,"nil"); 
} // add

Sexp predictReal(const vector<Sexp> & selist  ){
  if( selist.size() != 1 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : real?"); //
  if( checkIsFloat(selist) )
    return Sexp(SYMBOL,T,"#t");
  else
    return Sexp(SYMBOL,NIL,"nil"); 
} // add


Sexp predictString(const vector<Sexp> & selist  ){
  if( selist.size() != 1 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : string?"); //
  if( selist[0].mtype == STRING )
    return Sexp(SYMBOL,T,"#t");
  else
    return Sexp(SYMBOL,NIL,"nil"); 
} // add


Sexp predictBoolean(const vector<Sexp> & selist  ){
  if( selist.size() != 1 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : boolean?"); //
  if( selist[0].mtype == NIL || selist[0].mtype == T )
    return Sexp(SYMBOL,T,"#t");
  else
    return Sexp(SYMBOL,NIL,"nil"); 
} // add

Sexp predictSymbol(const vector<Sexp> & selist  ){
  if( selist.size() != 1 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : boolean?"); //
  if( selist[0].mtype == SYM )
    return Sexp(SYMBOL,T,"#t");
  else
    return Sexp(SYMBOL,NIL,"nil"); 
} // add

Sexp greaterThan(const vector<Sexp> & selist  ){
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : >"); //
  if( ! checkIsFloat(selist) ){
    for(int i = 0 ; i < selist.size(); i++){
      if ( selist[i].type != NUMBER && ( selist[i].type == LIST || selist[i].type == PAIR ) ){
        throw Sexp(ERROR,"ERROR (> with incorrect argument type) : ", selist[i].Slist);
      } // if  

      else if( selist[i].type != NUMBER )
        throw RebuildSexp("ERROR (> with incorrect argument type) : ",selist[i]); // 丟錯誤物件  
    } // for
  } // if

  if( checkIsINT(selist) == true ){
    for( int i = 0; i < selist.size() -1 ; i++ ){
      if( atoi(selist[i].value.c_str()) <= atoi(selist[i+1].value.c_str()) )
        return Sexp(SYMBOL,NIL,"nil"); 
    } // for
  } // if

  else if ( checkIsFloat(selist) == true ){
    for( int i = 0; i < selist.size() - 1 ; i++ ){
      if( atof(selist[i].value.c_str()) <= atof(selist[i+1].value.c_str()) )
        return Sexp(SYMBOL,NIL,"nil"); 
    } // for
  } // else if    
  return Sexp(SYMBOL,T,"#t");
} // add


Sexp greaterEqual(const vector<Sexp> & selist  ){
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : >="); //
  if( ! checkIsFloat(selist) ){
    for(int i = 0 ; i < selist.size(); i++){
      if ( selist[i].type != NUMBER && ( selist[i].type == LIST || selist[i].type == PAIR ) ){
        throw Sexp(ERROR,"ERROR (>= with incorrect argument type) : ", selist[i].Slist);
      } // if  

      else if( selist[i].type != NUMBER )
        throw RebuildSexp("ERROR (>= with incorrect argument type) : ",selist[i]); // 丟錯誤物件  
    } // for
  } // if

  if( checkIsINT(selist) == true ){
    for( int i = 0; i < selist.size() - 1 ; i++ ){
      if( atoi(selist[i].value.c_str()) < atoi(selist[i+1].value.c_str()) )
        return Sexp(SYMBOL,NIL,"nil"); 
    } // for
  } // if

  else if ( checkIsFloat(selist) == true ){
    for( int i = 0; i < selist.size() - 1 ; i++ ){
      if( atof(selist[i].value.c_str()) < atof(selist[i+1].value.c_str()) )
        return Sexp(SYMBOL,NIL,"nil"); 
    } // for
  } // else if    
  return Sexp(SYMBOL,T,"#t");
} // add

Sexp lessThan(const vector<Sexp> & selist  ){
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : <"); //
  if( ! checkIsFloat(selist) ){
    for(int i = 0 ; i < selist.size(); i++){
      if ( selist[i].type != NUMBER && ( selist[i].type == LIST || selist[i].type == PAIR ) ){
        throw Sexp(ERROR,"ERROR (< with incorrect argument type) : ", selist[i].Slist);
      } // if  

      else if( selist[i].type != NUMBER )
        throw RebuildSexp("ERROR (< with incorrect argument type) : ",selist[i]); // 丟錯誤物件  
    } // for
  } // if

  if( checkIsINT(selist) == true ){
    for( int i = 0; i < selist.size() - 1 ; i++ ){
      if( atoi(selist[i].value.c_str()) > atoi(selist[i+1].value.c_str()) )
        return Sexp(SYMBOL,NIL,"nil"); 
    } // for
  } // if

  else if ( checkIsFloat(selist) == true ){
    for( int i = 0; i < selist.size() - 1; i++ ){
      if( atof(selist[i].value.c_str()) > atof(selist[i+1].value.c_str()) )
        return Sexp(SYMBOL,NIL,"nil"); 
    } // for
  } // else if    
  return Sexp(SYMBOL,T,"#t");
} // add

Sexp lessEqual(const vector<Sexp> & selist  ){
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : <="); //
  if( ! checkIsFloat(selist) ){
    for(int i = 0 ; i < selist.size(); i++){
      if ( selist[i].type != NUMBER && ( selist[i].type == LIST || selist[i].type == PAIR ) ){
        throw Sexp(ERROR,"ERROR (<= with incorrect argument type) : ", selist[i].Slist);
      } // if  

      else if( selist[i].type != NUMBER )
        throw RebuildSexp("ERROR (<= with incorrect argument type) : ",selist[i]); // 丟錯誤物件  
    } // for
  } // if

  if( checkIsINT(selist) == true ){
    for( int i = 0; i < selist.size() - 1 ; i++ ){
      if( atoi(selist[i].value.c_str()) > atoi(selist[i+1].value.c_str()) )
        return Sexp(SYMBOL,NIL,"nil"); 
    } // for
  } // if

  else if ( checkIsFloat(selist) == true ){
    for( int i = 0; i < selist.size() - 1 ; i++ ){
      if( atof(selist[i].value.c_str()) > atof(selist[i+1].value.c_str()) )
        return Sexp(SYMBOL,NIL,"nil"); 
    } // for
  } // else if    
  return Sexp(SYMBOL,T,"#t");
} // add


Sexp isSame(const vector<Sexp> & selist  ){
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : ="); //
  if( ! checkIsFloat(selist) ){
    for(int i = 0 ; i < selist.size(); i++){
      if ( selist[i].type != NUMBER && ( selist[i].type == LIST || selist[i].type == PAIR ) ){
        throw Sexp(ERROR,"ERROR (= with incorrect argument type) : ", selist[i].Slist);
      } // if  

      else if( selist[i].type != NUMBER )
      throw RebuildSexp("ERROR (= with incorrect argument type) : ",selist[i]); // 丟錯誤物件  
    } // for
  } // if

  if( checkIsINT(selist) == true ){
    for( int i = 0; i < selist.size() - 1 ; i++ ){
      if( atoi(selist[i].value.c_str()) != atoi(selist[i+1].value.c_str()) )
        return Sexp(SYMBOL,NIL,"nil"); 
    } // for
  } // if

  else if ( checkIsFloat(selist) == true ){
    for( int i = 0; i < selist.size() - 1 ; i++ ){
      if( atof(selist[i].value.c_str()) != atof(selist[i+1].value.c_str()) )
        return Sexp(SYMBOL,NIL,"nil"); 
    } // for
  } // else if    
  return Sexp(SYMBOL,T,"#t");
} // add

Sexp stringAppend(const vector<Sexp> & selist  ){ // 第二題告訴我要銜接時要扣除後面跟前面的"
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : string-append"); //
  if( ! checkIsString(selist) ){
    for(int i = 0 ; i < selist.size(); i++){
      if ( selist[i].mtype != STRING && ( selist[i].type == LIST || selist[i].type == PAIR ) ){
        throw Sexp(ERROR,"ERROR (string-append with incorrect argument type) : ", selist[i].Slist);
      } // if  

      else if( selist[i].mtype != STRING )
        throw RebuildSexp("ERROR (string-append with incorrect argument type) : ",selist[i]); // 丟錯誤物件   
    } // for
  } // if

  string str = selist[0].value;
  for( int i = 1; i < selist.size() ; i++ ){
    str = str.substr(0, str.size()-1); // 扣除最後的"
    string str2 = selist[i].value.substr(1, selist[i].value.size()); // 扣除最前面的"
    str = str + str2;
  } // for
  return Sexp(SYMBOL,STRING,str);
} // add

Sexp isStringGreaterThan(const vector<Sexp> & selist  ){
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : string>?"); //
  if( ! checkIsString(selist) ){
    for(int i = 0 ; i < selist.size(); i++){
      if ( selist[i].mtype != STRING && ( selist[i].type == LIST || selist[i].type == PAIR ) ){
        throw Sexp(ERROR,"ERROR (string>? with incorrect argument type) :", selist[i].Slist);
      } // if  

      else if( selist[i].mtype != STRING )
        throw RebuildSexp("ERROR (string>? with incorrect argument type) : ",selist[i]); // 丟錯誤物件  
    } // for
  } // if

  for( int i = 0; i < selist.size() - 1 ; i++ ){
    if( selist[i].value <= selist[i+1].value )
      return Sexp(SYMBOL,NIL,"nil"); 
  } // for

  return Sexp(SYMBOL,T,"#t");
} // add

Sexp isStringLessThan(const vector<Sexp> & selist  ){
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : string<?"); //
  if( ! checkIsString(selist) ){
    for(int i = 0 ; i < selist.size(); i++){
      if ( selist[i].mtype != STRING && ( selist[i].type == LIST || selist[i].type == PAIR ) ){
        throw Sexp(ERROR,"ERROR (string<? with incorrect argument type) :", selist[i].Slist);
      } // if  

      else if( selist[i].mtype != STRING )
        throw RebuildSexp("ERROR (string<? with incorrect argument type) : ",selist[i]); // 丟錯誤物件 
    } // for
  } // if

  for( int i = 0; i < selist.size() - 1 ; i++ ){
    if( selist[i].value >= selist[i+1].value )
      return Sexp(SYMBOL,NIL,"nil"); 
  } // for

  return Sexp(SYMBOL,T,"#t");
} // add

Sexp isStringSame(const vector<Sexp> & selist  ){
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : string=?"); //
  if( ! checkIsString(selist) ){
    for(int i = 0 ; i < selist.size(); i++){
      if ( selist[i].mtype != STRING && ( selist[i].type == LIST || selist[i].type == PAIR ) ){
        throw Sexp(ERROR,"ERROR (string=? with incorrect argument type) :", selist[i].Slist);
      } // if  

      else if( selist[i].mtype != STRING )
        throw RebuildSexp("ERROR (string=? with incorrect argument type) : ",selist[i]); // 丟錯誤物件   
    } // for
  } // if

  for( int i = 0; i < selist.size() - 1 ; i++ ){
    if( selist[i].value != selist[i+1].value )
      return Sexp(SYMBOL,NIL,"nil"); 
  } // for

  return Sexp(SYMBOL,T,"#t");
} // add

Sexp isEqv(const vector<Sexp> & selist  ){ 
/*
; eqv? returns "true" only when the two being compared
; objects are atoms (except in the case of strings)
; or when the two being compared objects "occupy the
; same memory space".

; equal?, on the other hand, is the usual notion of 
; equality comparison
*/
  if( selist.size() != 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : eqv?"); 
  if( selist[0].type != selist[1].type ){
    return Sexp(SYMBOL,NIL,"nil"); 
  } // if

  if( selist[0].uniqueID == selist[1].uniqueID ){  
    return Sexp(SYMBOL,T,"#t");
  } // if

  if( selist[0].type == LIST || selist[0].type == PAIR || selist[0].mtype == STRING )
    return Sexp(SYMBOL,NIL,"nil"); 
  else if( selist[0].type == NUMBER ){
    if( atof(selist[0].value.c_str()) == atof(selist[1].value.c_str()) )
      return Sexp(SYMBOL,T,"#t");
    else
      return Sexp(SYMBOL,NIL,"nil"); 
  } // else if

  else if( selist[0].type == FUNCTION ){
    if( selist[0].value == selist[1].value )
      return Sexp(SYMBOL,T,"#t");
    else
      return Sexp(SYMBOL,NIL,"nil"); 
  } // else if

  else if( selist[0].type == FUNCTION_LAMBDA ){
    if( selist[0].value == selist[1].value )
      return Sexp(SYMBOL,T,"#t");
    else
      return Sexp(SYMBOL,NIL,"nil"); 
  } // else if
  
  else if( selist[0].mtype == NIL || selist[0].mtype == T )
    return Sexp(SYMBOL,T,"#t");
  else if( selist[0].type == SYMBOL ){ // 感覺不太可能走到這
    if( selist[0].value == selist[1].value )
      return Sexp(SYMBOL,T,"#t");
    else
      return Sexp(SYMBOL,NIL,"nil"); 
  } // else if

  else // 感覺不會去比較error物件把
    return Sexp(SYMBOL,NIL,"nil"); 
} // isEqv

Sexp isEqual(const vector<Sexp> & selist  ){
  if( selist.size() != 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : equal?"); 
  if ( checkSexpEqual(selist[0],selist[1]) )
    return Sexp(SYMBOL,T,"#t");
  else
    return Sexp(SYMBOL,NIL,"nil"); 
} // isEqual  

Sexp isQuote(const vector<Sexp> & selist ){
  if( selist.size() != 1 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : quote?"); 
  return selist[0];
} // 

Sexp doExit(const vector<Sexp> & selist){ // 這是想到的寫法(把正解寫在計算中，所以跑到這=錯誤的level)
  throw Sexp(ERROR,"(level of EXIT)"); // 丟錯誤物件  
} // doExit

Sexp doCleanEnviron(const vector<Sexp> & selist){ //  3/29這是想到的寫法(把正解寫在計算中，所以跑到這=錯誤的level)
  throw Sexp(ERROR,"ERROR (level of CLEAN-ENVIRONMENT)"); // 丟錯誤物件
} // doExit

bool expectedFunctionArgcNum( Sexp fun,int argcSize ) {
  if( fun.value == "+" || fun.value == "-" || fun.value == "*" || fun.value == "/" || fun.value == ">" 
      || fun.value == ">=" || fun.value == "<" || fun.value == "<=" || fun.value == "=" || fun.value == "string-append"
      ||  fun.value == "string<?" ||  fun.value == "string>?" ||  fun.value == "string=?" ) { // +-*/
    if( argcSize < 2)
      return false;
    else  
      return true;
  } // if

  else if( fun.value == "car" || fun.value == "cdr" || fun.value == "atom?" || fun.value == "pair?" 
           || fun.value == "list?" || fun.value == "null?" || fun.value == "integer?" || fun.value == "real?" 
           || fun.value == "number?" || fun.value == "string?" || fun.value == "boolean?" || fun.value == "symbol?" 
           || fun.value == "not"  || fun.value == "quote") {
    if( argcSize != 1)
      return false;
    else  
      return true;
  } // else if

  else if( fun.value == "cons" || fun.value == "eqv?" || fun.value == "equal?") {
    if( argcSize != 2)
      return false;
    else  
      return true;
  } // else if

  else if( fun.value == "list" ) {
    return true;
  } // else if

  else{
    cout << "可能要除錯";
    return false;
  } // else
    
} // expectedFunctionArgcNum

class Environ{ //設計是這是變數的放置區，
  public:  
    map<string,Sexp> env; 
    Environ * Parent; // 設計是讓local指向global
    Environ(){
      Parent = NULL;
      buildinFunction();
    } // Environ
    
    Environ(Environ * upperParent){
      Parent = upperParent;
    } // 指向parent

    void buildinFunction(){
      // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      // error的判斷(怎樣的寫法應該算什麼樣的error？)是以HowToWriteOurScheme.doc為準
      // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      // Below are the primitives (and features) that your system should implement.
      // (括號內的數字指的是這個function可接受的argument的數目 - i.e., the number of
      // arguments that this function can take)
      // 1. Constructors
      env["cons"] = Sexp(FUNCTION, cons, "cons");
      env["list"] = Sexp(FUNCTION, createList, "list");
      // 4. Part accessors
      env["car"] = Sexp(FUNCTION, car, "car");
      env["cdr"] = Sexp(FUNCTION, cdr, "cdr");
      // 5. Primitive predicates (all functions below can only take 1 argument)
      env["atom?"] = Sexp(FUNCTION, predictAtom, "atom?");
      env["pair?"] = Sexp(FUNCTION, predictPair, "pair?");
      env["list?"] = Sexp(FUNCTION, predictList, "list?");
      env["null?"] = Sexp(FUNCTION, predictNull, "null?");
      env["integer?"] = Sexp(FUNCTION, predictInteger, "integer?");
      env["real?"]  = Sexp(FUNCTION, predictReal, "real?");
      env["number?"] = Sexp(FUNCTION, predictNumber, "number?");
      env["string?"] = Sexp(FUNCTION, predictString, "string?");
      env["boolean?"] = Sexp(FUNCTION, predictBoolean, "boolean?");
      env["symbol?"] = Sexp(FUNCTION, predictSymbol, "symbol?");
      // 6. Basic arithmetic, logical and string operations
      env["+"] = Sexp(FUNCTION, doAdd, "+");
      env["-"] = Sexp(FUNCTION, doMinus, "-");
      env["*"] = Sexp(FUNCTION, doMultlipy, "*");
      env["/"] = Sexp(FUNCTION, doDivide, "/");
      // ; all functions below can take 2 or more arguments
      env[">"]  = Sexp(FUNCTION, greaterThan, ">");
      env[">="] = Sexp(FUNCTION, greaterEqual, ">=");
      env["<"] = Sexp(FUNCTION, lessThan, "<");
      env["<="] = Sexp(FUNCTION, lessEqual, "<=");
      env["="] = Sexp(FUNCTION, isSame, "=");
      env["string-append"] = Sexp(FUNCTION, stringAppend, "string-append");
      env["string>?"] = Sexp(FUNCTION, isStringGreaterThan, "string>?");
      env["string<?"] = Sexp(FUNCTION, isStringLessThan, "string<?");
      env["string=?"] = Sexp(FUNCTION, isStringSame, "string=?");
      // 7. Eqivalence tester
      env["eqv?"] = Sexp(FUNCTION, isEqv, "eqv?");
      env["equal?"] = Sexp(FUNCTION, isEqual, "equal?");
      env["not"] = Sexp(FUNCTION, isNot, "not");
      // 3/29新增(把下面兩者寫成function)
      env["clean-environment"] = Sexp(FUNCTION, doCleanEnviron, "clean-environment");
      env["exit"] = Sexp(FUNCTION, doExit, "exit");
      env["quote"] = Sexp(FUNCTION, isQuote, "quote"); // quote寫成function
    } // buildinFunction 
  
    void doTheDefine(string str, Sexp arg){
      env[str] = arg;
    } // doTheDefine 

    void doTheDefineFunction(string name, vector<string> paras, Sexp body){
    //  env[str] 
    } // doTheDefineFunction

    Sexp retrieveEnv( Sexp arg ){
      if( env.find(arg.value) != env.end()   )
        return env[arg.value]; // 找到了返回環境
      else if( Parent != NULL ){ //沒找到找往global，parent往上找。
        return Parent->retrieveEnv(arg);  
      } //else if
      else
        throw Sexp(ERROR,"ERROR (unbound symbol) : " + arg.value); 
    } // doTheDefine
  
    void updateEnv( string str, Sexp arg ){
      if( env.find(str) != env.end()   )
        env[str] = arg; 
      else if( Parent != NULL ){ //沒找到找innerEnv
        return Parent->updateEnv(str,arg);  
      } //else if
      else
        throw Sexp(ERROR,"ERROR (unbound symbol) : " + str ); // updating error
    } // retrieveEnv
    
    void clearEnv(){ //應該是清除後放入buildinFunc就好
      env.clear();
      buildinFunction();
    } // clearEnv
    Environ * extendEnv(  Environ* upperParent, vector<string> mParas, vector<Sexp> mArgs){
      Environ* newEnv = new Environ(upperParent); // init
      for( int i = 0 ; i < mParas.size() ; i++ ){
        newEnv->env[mParas[i]] = mArgs[i]; 
      } // for
      return newEnv;
    } // extendEnv
  }; // Env

  
class Evaluate {
  public:
    Sexp sTree; // 這個是存好的SexpTree
    int level; // 3/29新增，處理這個錯誤ERROR (level of CLEAN-ENVIRONMENT)
    void setSexp( Sexp exp){
      sTree = exp;
      level = 0;
    } // setTable
  
    Sexp evaluting( Environ * inEnv, Sexp ss ){  // 3/29改掉在這一層try-catch會使錯誤在遞迴時被接收導致收不到錯誤。
      level++;
      if( ss.mtype == NIL || ss.mtype == T || ss.mtype == STRING || ss.type == NUMBER || ss.type == ERROR
                          || ss.type == FUNCTION  || ss.type == FUNCTION_LAMBDA  ) // 寫PROJECT 1以為這些也可以變形，難受了，不知道要不要FUNCTION
        return ss; 
      else if( ss.type == SYMBOL ){ //拿symbol，dot可能會有bug
        return inEnv->retrieveEnv(ss);
      } // if

      else if( ss.type == LIST ){ // 這邊有特殊形式(ai說有)quote,define,lambda,set!,and,or,begin,if,cond,let的function不能先求值，剩下的就要傳參後傳值
        if( ss.Slist.size() == 0) // 為什麼list函數可以接受空list!!!!!!!!!
          return Sexp(SYMBOL,NIL,"nil");
        Sexp temp = ss.Slist[0];
        // special function
        if( temp.mtype == QUOTE ) // QUOTE就拿下一個Sexp
          return ss.Slist[1];
        else if( temp.value == "define" ){ // define語句 define a  b(body)
          if( level != 1 ){ //3/29第8題
            throw Sexp(ERROR,"ERROR (level of DEFINE)");
          } // if

          if( ss.Slist.size() != 3 ) // define 參數量 = 2 ==> 就是2是define symbol，3是 
            throw Sexp(ERROR,"ERROR (DEFINE format) : ", ss.Slist);
          if( ss.Slist[1].type == SYMBOL ){ // 定義SYMBOL
            if( isReservedWord(ss.Slist[1].value) ){
              throw Sexp(ERROR,"ERROR (DEFINE format) : ", ss.Slist); //碰到保留字了 不知道要回傳list還是錯誤的保留字
            } // if

            ss.Slist[2] = evaluting(inEnv,ss.Slist[2]); // 不知道可不可以這樣寫。
            inEnv->doTheDefine(ss.Slist[1].value,ss.Slist[2]);
            ss.Slist[1].value += " defined";
            return ss.Slist[1]; //返回這個symbol印出a defined。
            //印出a defined;
          } // if

          else if( ss.Slist[1].type == LIST ){ // 定義FUNCTION (define (a b) 2) => (define a ( lambda (b) 2) ) 要改語法
            if( ss.Slist[1].Slist.size() == 0 || ss.Slist[1].Slist[0].type != SYMBOL ){ // 應該不會有這種狀況存在
              throw Sexp(ERROR,"ERROR (DEFINE format) : ", ss.Slist); //碰到保留字了 不知道要回傳list還是錯誤的保留字
            } // if
            
            vector<string> parameters;
            for( int i = 1 ; i < ss.Slist[1].Slist.size() ; i++){
              if(  ss.Slist[1].Slist[i].type == SYMBOL && ( ! isReservedWord( ss.Slist[1].Slist[i].value )) ){ // 應該不會有這種狀況存在
                throw Sexp(ERROR,"ERROR (DEFINE format) : ", ss.Slist); //碰到保留字了 不知道要回傳list還是錯誤的保留字
              } // if
              parameters.push_back( ss.Slist[1].Slist[i].value ); //放入parameter
            } // for 
            
            Sexp changetoLambdaExpr = Sexp(FUNCTION_LAMBDA,parameters);
            changetoLambdaExpr.Slist.push_back(evaluting(inEnv,ss.Slist[2])); // 放body
            changetoLambdaExpr.value = ss.Slist[1].Slist[0].value; 
            inEnv->doTheDefine(ss.Slist[1].Slist[0].value, changetoLambdaExpr.Slist[0]);
            return changetoLambdaExpr;
            //印出producured a defined;
          } // else if

          else
            throw Sexp(ERROR,"ERROR (DEFINE format) : ", ss.Slist); // 如果是list定義的是function那字元，只會是symbol or func其他都是err
        } // define
        else if( temp.value == "lambda" ){ // lambda這個有夠難處理，計畫中打算先讓他變成function然後要呼叫他時把，list to body，並加上args
          if( ss.Slist.size() < 3 ) // lambda 參數量最少要有三個 => lambda () exp~~ 
            throw Sexp(ERROR,"ERROR (lambda format) : ", ss.Slist);
          ss.type = FUNCTION_LAMBDA; 
          vector<string> parameters;
          if( ss.Slist[1].type == LIST ){
            if( ss.Slist[1].Slist.size() == 0 ){
              ss.Slist[1].type = SYMBOL;
              ss.Slist[1].mtype = NIL;
              ss.Slist[1].value = "nil";
            } // if
            
            else { // 很多args
              for( int i = 0 ; i < ss.Slist[1].Slist.size(); i++){
                if( ss.Slist[1].Slist[i].type != SYMBOL || isReservedWord(ss.Slist[1].Slist[i].value )  ){
                  throw Sexp(ERROR,"ERROR (LAMBDA format) : ", ss.Slist);
                } // if
                parameters.push_back(ss.Slist[1].Slist[i].value);
              } // for
              ss.paras = parameters;
            } // else
          } // if

          else if( ss.Slist[1].mtype != NIL) // 不是列表或是nil 就是error了
            throw Sexp(ERROR,"ERROR (LAMBDA format) : ", ss.Slist);
          
          vector<Sexp> lambdaBody;
          for( int i = 2 ; i < ss.Slist.size() ; i++){
            lambdaBody.push_back(ss.Slist[i]);
          } // 開始存Sexp(這是body的成分)
          Sexp tempsexp = Sexp(LIST,lambdaBody); // 為了存body在slist裡面但是又不想要毀掉結構(其實是不知道怎麼在class加入自己)，所以方法時把slist存body一個sexp在讓其展開。
          ss.Slist.clear();
          ss.Slist.push_back(tempsexp);
          return ss;           
        } // else if 

        else if( temp.value == "set!" ){ // set!
          if( ss.Slist.size() < 3 ){
            throw Sexp(ERROR,"ERROR (incorrect number of arguments) : set!", ss.Slist);
          } // if

          if( ss.Slist[1].type != SYMBOL )
            throw Sexp(ERROR,"ERROR (Set! format) : ", ss.Slist);
          ss.Slist[2] = evaluting( inEnv, ss.Slist[2]);
          inEnv->doTheDefine(ss.Slist[1].value, ss.Slist[2]);
          return ss.Slist[2];
        } // else if

        else if( temp.value == "cond" ){ // cond語法(cond (條件 exp1 exp2) (條件2 exp~)(最後條件如果是else的話(全false才執行) exp!) )
          if( ss.Slist.size() < 2 ){
            throw Sexp(ERROR,"ERROR (COND format) : ", ss.Slist);
          } // if

          for( int i = 1 ; i < ss.Slist.size() ; i++){ // 先檢查型式在開始計算
            if( ss.Slist[i].type != LIST ){
              throw Sexp(ERROR,"ERROR (COND format) : ", ss.Slist);
            } //if

            if( ss.Slist[i].Slist.size() <= 1) // 如果只有(條件)沒有exp的話
              throw Sexp(ERROR,"ERROR (COND format) : ", ss.Slist);
          } // for

          for( int i = 1 ; i < ss.Slist.size() ; i++){
            if( i == ss.Slist.size() -1 ){ // 這代表著要看else表達式，這段感覺寫得很冗餘
                if( ss.Slist[i].Slist[0].value == "else" || ss.Slist[i].Slist[0].value == "\"else\""){ // 如果已經是else的話就不要計算(因為會unbound symbol沒有的話，要計算條件)
                  for( int j = 1 ; j < ss.Slist[i].Slist.size() ; j++ ){ // 內部list
                    if( j == ss.Slist[i].Slist.size()-1 ){ // 條件句
                      return evaluting(inEnv,ss.Slist[i].Slist[j]);
                    } // if
    
                    else // 不是最後就do the 計算
                      evaluting(inEnv,ss.Slist[i].Slist[j]); 
                  } // for
                } // if
            } // if

            Sexp tempNode  = evaluting(inEnv, ss.Slist[i].Slist[0]);
            if( tempNode.mtype == NIL ) // 就不執行條件句false
              continue;

            for( int j = 1 ; j < ss.Slist[i].Slist.size() ; j++ ){ // 內部list
              if( j == ss.Slist[i].Slist.size()-1 ){ // 條件句
                return evaluting(inEnv,ss.Slist[i].Slist[j]);
              } // if

              else // 不是最後就do the 計算
                evaluting(inEnv,ss.Slist[i].Slist[j]); 
            } // for
          } // for

          throw Sexp(ERROR,"ERROR (no return value) : ",ss.Slist);
        } // else if 

        else if( temp.value == "if" ){ // ( if pred expr1 expr2) (if pred expr1)
          if( ss.Slist.size() < 3 || ss.Slist.size() > 4  ){
            throw Sexp(ERROR,"ERROR (incorrect number of arguments) : if");
          } // if
          
          Sexp tempNode = evaluting(inEnv,ss.Slist[1]); // pred
          if( tempNode.mtype != NIL){
            return evaluting(inEnv, ss.Slist[2]);
          } // if

          else{
            if( ss.Slist.size() == 4 ){
              return evaluting(inEnv, ss.Slist[3]);
            } // if
            else
              throw Sexp(ERROR,"ERROR (no return value) : ", ss.Slist);
          } // else
        } // else if 

        else if( temp.value == "let" ){ // let語法 (let (( symbol1 sexp1 )(sym2 sexp2) ...) body1 body2)
          if( ss.Slist.size() < 3  || ss.Slist[1].type != LIST ) // let 參數量最少要有三個 => let (symbol sexp) body1 
            throw Sexp(ERROR,"ERROR (let format) : ", ss.Slist);  
          vector<string> symbol_string;
          vector<Sexp> SexpValues;
          for( int i = 0 ; i < ss.Slist[1].Slist.size() ; i++){ // (( symbol1 sexp1 )(sym2 sexp2) ...)
            if( ss.Slist[1].Slist[i].type != LIST || ss.Slist[1].Slist[i].Slist.size() != 2 || ss.Slist[1].Slist[i].Slist[0].type != SYMBOL )
              throw Sexp(ERROR,"ERROR (let format) : ", ss.Slist);  
            if( isReservedWord( ss.Slist[1].Slist[i].Slist[0].value ) )
              throw Sexp(ERROR,"ERROR (let format) : ", ss.Slist);  
            symbol_string.push_back( ss.Slist[1].Slist[i].Slist[0].value);
            SexpValues.push_back( evaluting(inEnv,ss.Slist[1].Slist[i].Slist[1]) );
          } // for

          Environ * localVar = new Environ(inEnv); // 放入LocalVariable去定義
          for(int i = 0 ; i < symbol_string.size() ; i++){
            localVar->doTheDefine(symbol_string[i],SexpValues[i]);
          } // for

          for(int i = 2; i < ss.Slist.size() ; i++ ){
            if( i == ss.Slist.size()-1 )
              return evaluting(localVar, ss.Slist[i]);
            evaluting(localVar, ss.Slist[i]);
          } // for
        } // else if 

        else if( temp.value == "and" ){ // and語法 (and expr1 expr2遇到nil就停，沒有就回傳最後一個值)
          if( ss.Slist.size() < 2  ) // and 參數量最少要有二個 => and expr1
            throw Sexp(ERROR,"ERROR (and format) : ", ss.Slist); 
          for( int i = 1 ; ss.Slist.size() ; i++){
            ss.Slist[i] = evaluting(inEnv, ss.Slist[i]);
            if( ss.Slist[i].mtype == NIL)
              return Sexp(SYMBOL,NIL,"nil");
            else if( i == ss.Slist.size() - 1 )
              return ss.Slist[i];
          } // for
        } // else if

        else if( temp.value == "or" ){ // or語法 反向and ( or expr1 expr2遇到不是nil就停，沒有就回傳最後一個值)
          if( ss.Slist.size() < 2  ) // or 參數量最少要有二個 => or expr1
            throw Sexp(ERROR,"ERROR (or format) : ", ss.Slist);   
          for( int i = 1 ; ss.Slist.size() ; i++){
            ss.Slist[i] = evaluting(inEnv, ss.Slist[i]);
            if( ss.Slist[i].mtype != NIL)
              return ss.Slist[i];
            else if( i == ss.Slist.size() - 1 )
              return ss.Slist[i];
          } // for
        } // else if 

        else if( temp.value == "begin" ){ // begin語法 begin expr1 expr2然後返回最後一個expr，就begin卻返回最後??
          if( ss.Slist.size() < 2  ) // begin 參數量最少要有二個 => or expr1
            throw Sexp(ERROR,"ERROR (incorrect number of arguments) : begin");       
          for( int i = 1 ; ss.Slist.size() ; i++){
            ss.Slist[i] = evaluting(inEnv, ss.Slist[i]);
            if( i == ss.Slist.size() - 1 )
              return ss.Slist[i];
          } // for
        } // else if 

        else if( temp.value == "clean-environment" ){ // 同下
          if( ss.Slist.size() == 1 && level == 1){              
            inEnv->clearEnv();
            throw SignalLight("clean-environment"); //就return
          } // if
          else if( level != 1)
            throw Sexp(ERROR,"ERROR (level of CLEAN-ENVIRONMENT)");
          else 
            throw Sexp(ERROR,"ERROR (incorrect number of arguments) : clean-environment");        
        }  // else if 

        else if( temp.value == "exit" ){   // 3/29變成function
          if( ss.Slist.size() == 1 && level == 1){
            throw SignalLight("exit"); //就return， 3/29新想法原本的return遇到了quote (exit) 後要列出的是exit，所以用return後兩者相等分不出區別，所以用throw
          } // if    
          else if( level != 1)
            throw Sexp(ERROR,"ERROR (level of EXIT)");
          else 
            throw Sexp(ERROR,"ERROR (incorrect number of arguments) : exit");  
        }  // else if 
        
        // normal function
        temp = evaluting(inEnv,temp); // 這可能是個symbol，變成function形式
        vector<Sexp> argc;
        if( temp.type == FUNCTION ){
          bool expectArgument = expectedFunctionArgcNum(temp,ss.Slist.size()-1) ;  
          if( expectArgument == false ){
            if( ss.Slist.size() > 1 ){ // 太多要改了，所以讓拋錯誤這一段交給function處理，處理方法是放入沒有計算的，然後就會因為數量err了
              for(int i = 1 ; i < ss.Slist.size() ; i++ ){
                argc.push_back(ss.Slist[i]);
              } // for
            } // if
          } // if
          
          else {
            if( temp.value == "quote"){
              return ss.Slist[1]; 
            } // if

            if( ss.Slist.size() > 1 ){
              for(int i = 1 ; i < ss.Slist.size() ; i++ ){
                ss.Slist[i] = evaluting(inEnv,ss.Slist[i]);
                argc.push_back(ss.Slist[i]);
              } // for
            } // if
          } // else

          return temp.funptr(argc);
        } // if

        else if( temp.type == FUNCTION_LAMBDA){
          if( ss.Slist.size() - 1 != temp.paras.size() )
            throw Sexp(ERROR,"ERROR (incorrect number of arguments) : lambda", ss.Slist);

          if( ss.Slist.size() > 1 ){ // do the 計算
            for(int i = 1 ; i < ss.Slist.size() ; i++ ){
              ss.Slist[i] = evaluting(inEnv,ss.Slist[i]);
              argc.push_back(ss.Slist[i]);
            } // for
          } // if   

          Environ * localVar = inEnv->extendEnv(inEnv,temp.paras,argc); // 放入LocalVariable去定義
          return evaluting( localVar, temp.Slist[0]);
        } // else if

        else if( argc.size() == 0 ){ // 代表著temp不是function
          if( temp.type == LIST || temp.type == PAIR)
            throw Sexp(ERROR,"ERROR (attempt to apply non-function) : ", temp.Slist);
          else
            throw Sexp(ERROR,"ERROR (attempt to apply non-function) : " + temp.value);
        } // else if
        else // 可能throw error之類的
          cout << "錯誤情況需要debug" << endl;
      } // else if  
      
      else if( ss.type == PAIR ){ // 3/28新增for ( 1 . 2 ) 這種狀況。
        throw Sexp(ERROR,"ERROR (non-list) : ", ss.Slist);
      } // else if
      else 
        return Sexp(ERROR,"Unknown Type For Debug"); // Untype for debug
      return Sexp(ERROR,"Unknown Type For Debug");
    } // evaluing

    void project1(int &endsignal){
      if( sTree.type == LIST){
        if( sTree.Slist.size() == 1 ){
          if( sTree.Slist[0].value == "exit" ){
            endsignal = 1;
            return;
          } // if 
        } // if
      } // if
  
      prettyPrint(sTree,0);
    } // project1
    
    void project2(Environ *globalglobal ,int &endsignal){ // 3/29把try catch移入這一層
      Sexp ce = Sexp();
      try{
        ce = evaluting( globalglobal, sTree ); // complete expression
      } // try
      catch(Sexp error){
        if( error.Slist.size() > 0 ){ // 肯定是list,pair
          cout << error.value;
          error.type = LIST;
          prettyPrint(error,0);
        } // if
        else if( error.type == ERROR) // 特別處理處理正常型別
          cout << error.value  << endl;
        else if( error.type != ERROR){
          cout << error.errormessage;
          prettyPrint(error,0);
          // 特別處理處理正常型別
        } // else if
        return;
      } // catch

      catch(SignalLight SL) {
        if( SL.value == "exit"){
          endsignal = 1;
        } // if        
        else if( SL.value == "clean-environment"){
          ce = Sexp(SYMBOL,STRING,"environment cleaned");
          prettyPrint(ce,0);
        } // else if

        return;
      } // catch
      prettyPrint(ce,0);
    } // project1

    void prettyPrint(Sexp s,int M){
      int i = 0, j = 0;
      if ( s.type == LIST || s.type == PAIR){ 
        cout << "( "; 
        for( i = 0 ; i < s.Slist.size() ; i++ ){ // 後續的都在M+2
          if ( i > 0 )
            cout << "  ";
          prettyPrint(s.Slist[i],M + 2);
          for (j = 0; j < M ; j++)
            cout << " ";
        } // for 
  
        cout << ")" << endl;
      } // if
  
      else { // ATOM 不是List代表沒有左右刮，是symbol(可能到時候要換型別)或是number就默認直接print 
        if( s.mtype == INT )
          cout << atoi(s.value.c_str()) << endl;
        else if( s.mtype == FLOAT )
          cout << fixed << setprecision(3) << s.number << endl;
        else if( s.mtype == NIL ) 
          cout << "nil" << endl;
        else if( s.mtype == T ) 
          cout << "#t" << endl;
        else if( s.mtype == QUOTE)
          cout << "quote" << endl;
        else if ( s.type == FUNCTION) { // 3/29新增
          string str = "#<procedure " + s.value + ">";
          cout << str << endl;
        } // else

        else if( s.type == FUNCTION_LAMBDA) { // 3/29新增
          string str = "#<procedure lambda>";
          cout << str << endl;
        } // else
        
        else  // 那應該就是symbol了
          cout << s.value << endl;
      } // else 
    } // prettyPrint
  
    void clear(){
      sTree = Sexp();
      level = 0;
    } // clear
  }; // Evalute
  
  
  



int main(){
  int utestnum = 0;
  char ch = '\0';
  cin >> utestnum ; // read testnum，不用讀換行why!
  int endsignal = 0;
  Scanner *scanner = new Scanner();
  Parser parser(scanner);
  Evaluate evaluate;
  Environ *globalglobal = new Environ();
  cout << "Welcome to OurScheme!" << endl ;	
  while( true ){
    try {
      cout << endl << "> ";
      parser.startParsing();
      evaluate.setSexp(parser.getSexp());
      // evaluate.project1(endsignal);
      evaluate.project2(globalglobal,endsignal);
      if ( endsignal == 1 )
        break;
      scanner->clear();
      parser.clear();
      evaluate.clear();
    } // try

    
    catch( ErrorCondition e ){
      e.PrintError();
      if( e.errornum == 1) // 跳出EOF
        break;
      // cout << "TokenError at line " << e.line << ", column " << e.column << ": " << e.what() << endl;
      scanner->encounterError();
      scanner->clear();
      parser.clear();
      evaluate.clear();
    } // catch
  } // while
  cout << endl << "Thanks for using OurScheme!";	
  return 0;
} // main


 