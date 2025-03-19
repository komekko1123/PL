// 啟航日3/19 pj2開始
// 
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
using namespace std;
class Sexp;
class SexpError;
typedef Sexp (*FUNCTIONPTR)(const vector<Sexp>&);

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
  float number;
  vector<Sexp> Slist;
  FUNCTIONPTR funptr;
  Sexp(){}
  Sexp(SexpType Stype, string theValue ){
    type = Stype;
    value = theValue;
  } // ERROR用到
  Sexp(SexpType Stype, vector<Sexp> theList){
    type = Stype;
    Slist = theList;
  } // LIST的模式

  Sexp(SexpType Stype, FUNCTIONPTR fptr){
    Stype = FUNCTION;
    funptr = fptr;
  } // 指向函數指標

  Sexp(SexpType Stype, tokenType theMtype,string theValue ){ // 感覺應該想好再寫的
    type = Stype;
    value = theValue;
    mtype = theMtype;
    if( mtype == INT || mtype == FLOAT ) 
      number = atof(value.c_str()); // 架構怪了atof
    //其他默認symbol
  } // Sexp()


}; // Sexp

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
      if ( c != '\n'  ) // until read the \n
        getTheChar(); // 等同getchar()
      else
        break;
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
        if ( backSexp.type == LIST ){ // 存LIST( 第三題的錯誤可能是這，要把list的內容全部拉進來..pretty print真的問題一堆)
          for( int i = 0 ; i < backSexp.Slist.size(); i++){
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
          return Sexp(LIST,sslist);
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

/*
  SexpType type;  
  tokenType mtype;
  string value;
  double number;
  vector<Sexp> Slist;
  FUNCTIONPTR funptr;
*/
class Evaluate {
public:
  Sexp sTree; // 這個是存好的SexpTree  
  void setSexp( Sexp exp){
    sTree = exp;
  } // setTable

  Sexp evaluting( Environ * inEnv, Sexp ss ){
    try{ 
      if( ss.type == SYMBOL ){ //拿symbol，dot可能會有bug
        return inEnv->retrieveEnv(ss);
      } // if

      else if( ss.type == NUMBER )
        return ss;
      else if( ss.type == ERROR ){
        return ss;
      } // else if

      else if( ss.type == LIST ){ // 這邊有特殊形式(ai說有)quote,define,lambda,set!,and,or,begin,if,cond,let的function不能先求值，剩下的就要傳參後傳值
        if( ss.Slist.size() == 0) // 為什麼list函數可以接受空list!!!!!!!!!
          return ss;
        Sexp temp = ss.Slist[0];
        // special function
        if( temp.mtype == QUOTE ) // QUOTE就拿下一個Sexp
          return ss.Slist[1];
        else if( temp.value == "define") // define語句

        else if( ) // lambda
        
        else if( ) // set!

        else if( ) // cond

        else if( ) // if

        else if( ) // cond

        else if( ) // 不知道clean env

        else if( ) // 還有exit算不算，先放著 
        
        // normal function
        temp = evaluting(inEnv,temp); // 這可能是個symbol，變成function形式
        vector<Sexp> argc;
        if( ss.Slist.size() > 1 ){
          for(int i = 1 ; i < ss.Slist.size() ; i++ ){
            ss.Slist[i] = evaluting(inEnv,ss.Slist[i]);
            argc.push_back(ss.Slist[i]);
          } // for
        } // if

        if( temp.type == FUNCTION ){
          return temp.funptr(argc);
        } // if
        else if( arg.size() == 0 )
          return temp;
        else // 可能throw error之類的
          cout << "錯誤情況需要debug" << endl;
      } // else if  

    } // try

    catch(Sexp error){
      return error;
    } // catch
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
  
  void prettyPrint(Sexp s,int M){
    int i = 0, j = 0;
    if ( s.type == LIST ){ 
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
      else  // 那應該就是symbol了
        cout << s.value << endl;
    } // else 
  } // prettyPrint

  void clear(){
    sTree = Sexp();
  } // clear
}; // Evalute



class Environ{ //設計是這是變數的放置區，
  map<string,Sexp> env; 
  Environ * innerEnv;
  vector<Sexp> mArgc;
  vector<Sexp> mParas;
  Environ(){
    innerEnv = NULL;
    buildinFunction();
  } // Environ
  
  Environ(map<string,Sexp> env,  vector<Sexp> mParas,  vector<Sexp> mArgc){
  }
  void buildinFunction(){
    env["+"] = Sexp(&add);
  } // buildinFunction 
  map<string,Sexp>* retrieveEnv( Sexp arg ){
    auto it = env.find(arg.value);
    if( it != env.end()   )
      return &env; // 找到了返回環境
    else if( innerEnv != NULL ){ //沒找到找innerEnv
      return innerEnv->retrieveEnv(arg);  
    } //else if
    else
      throw Sexp(ERROR,"ERROR (Unbound Symbol)"); // 
  } // retrieveEnv

  
}; // Env

// 用function pointer呼叫的function群
bool checkIsINT( const vector<Sexp> & ss ){
  for( int i = 0 ; i < ss.size() ; i++ ){
    if( ss[i].type == NUMBER && ss[i].mtype != INT )
      return false;
    else if ( ss[i].type != NUMBER && ss[i].mtype == INT ){
      cout << "可能有bug" << endl;
      return false;
    } // else if
  } // for
  return true;  
} // checkIsINT

bool checkIsFloat( const vector<Sexp> & ss){ // 先確定過int了代表float就可以直接過
  for( int i = 0 ; i < ss.size() ; i++ ){
    if ( ss[i] != NUMBER ){
      cout << "不是數字" << endl;
      return false;
    } // else if
  } // for
  return true;  
} // checkIsFloat

Sexp add(const vector<Sexp> & selist  ){
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : +"); // 
  float f = 0;
  int a = 0;
  stringstream ss;
  if( checkIsINT(selist) == true ){
    a = selist[0];
    for( int i = 1; i < selist.size() ; i++ )
      a += atoi(selist[i].value.c_str());
    ss << a;
    return Sexp(NUMBER,INT,ss.str());
  } // if

  else if ( checkIsFloat(selist) == true ){
    f = selist[0];
    for( int i = 1; i < selist.size() ; i++ )
      f += atof(selist[i].value.c_str());
    ss << f;
    return Sexp(NUMBER,FLOAT,ss.str());
  } // else if    

  else {
    string str = "ERROR (+ with incorrect argument type) : "
    for(int i = 0 ; i < selist.size(); i++){
      if ( ss[i] != NUMBER ){
        str = str + ss[i].value;
        break;
      } // else if  
    } // for
    throw Sexp(ERROR,str); // 丟錯誤物件
  } // else
} // add

Sexp minus(const vector<Sexp> & selist  ){
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : -"); // 
  float f = 0;
  int a = 0;
  stringstream ss;
  if( checkIsINT(selist) == true ){
    a = selist[0];
    for( int i = 1; i < selist.size() ; i++ )
      a -= atoi(selist[i].value.c_str());
    ss << a;
    return Sexp(NUMBER,INT,ss.str());
  } // if

  else if ( checkIsFloat(selist) == true ){
    f = selist[0];
    for( int i = 1; i < selist.size() ; i++ )
      f -= atof(selist[i].value.c_str());
    ss << f;
    return Sexp(NUMBER,FLOAT,ss.str());
  } // else if    

  else {
    string str = "ERROR (- with incorrect argument type) : "
    for(int i = 0 ; i < selist.size(); i++){
      if ( ss[i] != NUMBER ){
        str = str + ss[i].value;
        break;
      } // else if  
    } // for
    throw Sexp(ERROR,str); // 丟錯誤物件
  } // else
} // minus

Sexp multlipy(const vector<Sexp> & selist  ){
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : *"); // 
  float f = 0;
  int a = 0;
  stringstream ss;
  if( checkIsINT(selist) == true ){
    a = selist[0];
    for( int i = 1; i < selist.size() ; i++ )
      a *= atoi(selist[i].value.c_str());
    ss << a;
    return Sexp(NUMBER,INT,ss.str());
  } // if

  else if ( checkIsFloat(selist) == true ){
    f = selist[0];
    for( int i = 1; i < selist.size() ; i++ )
      f *= atof(selist[i].value.c_str());
    ss << f;
    return Sexp(NUMBER,FLOAT,ss.str());
  } // else if    

  else {
    string str = "ERROR (* with incorrect argument type) : "
    for(int i = 0 ; i < selist.size(); i++){
      if ( ss[i] != NUMBER ){
        str = str + ss[i].value;
        break;
      } // else if  
    } // for
    throw Sexp(ERROR,str); // 丟錯誤物件
  } // else
} // multlipy

Sexp divide(const vector<Sexp> & selist  ){
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : /"); // 
  float f = 0;
  int a = 0;
  stringstream ss;
  if( checkIsINT(selist) == true ){
    a = selist[0];
    for( int i = 1; i < selist.size() ; i++ ){
      if( selist[i].value == "0")
        throw Sexp(ERROR,"ERROR (division by zero) : /");
      a /= atoi(selist[i].value.c_str());
    } // for
    ss << a;
    return Sexp(NUMBER,INT,ss.str());
  } // if

  else if ( checkIsFloat(selist) == true ){
    f = selist[0];
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
    string str = "ERROR (/ with incorrect argument type) : "
    for(int i = 0 ; i < selist.size(); i++){
      if ( ss[i] != NUMBER ){
        str = str + ss[i].value;
        break;
      } // else if  
    } // for
    throw Sexp(ERROR,str); // 丟錯誤物件
  } // else
} // divide


Sexp isNot(const vector<Sexp> & selist  ){
  if( selist.size() != 1  )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : Not"); // 
  if( selist[0].mtype != NIL)
    return Sexp(NIL,"SYMBOL","nil");
  else
    return Sexp(T,"SYMBOL","#t");
} // divide

Sexp isAnd(const vector<Sexp> & selist  ){
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : And"); // 

  for(int i = 0 ; i < selist.size(); i++){
    if ( ss[i].mtype == NIL ){
      return Sexp(NIL,"SYMBOL","nil");
    } // if
  } // for

  return selist[selist.size()-1];
} // divide

Sexp isOr(const vector<Sexp> & selist  ){
  if( selist.size() < 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : OR"); // 

  for(int i = 0 ; i < selist.size(); i++){
    if ( ss[i].mtype != NIL ){
      return selist[i];
    } // if
  } // for

  return Sexp(NIL,"SYMBOL","nil");
} // divide


Sexp cons(const vector<Sexp> & selist  ){
  if( selist.size() != 2 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : cons"); // 
  vector<Sexp> temp;
  temp.push_back(selist[0]);
  if( selist[1].value == "()" || selist[1].value == "nil" || selist[1].value == "#f" || selist[1].mtype == NIL )
    return Sexp(LIST,temp);
  else{
    Sexp dotSexp = Sexp(SYMBOL,DOT,".");
    temp.push_back(dotSexp);
    temp.push_back(selist[1]);
    return Sexp(LIST,temp);
  } // else
} // add

Sexp createList(const vector<Sexp> & selist  ){
  vector<Sexp> temp;
  for( int i = 0 ; i < selist.size() ; i++){
    temp.push_back(selist[i]);
  } // for
  return Sexp(LIST,temp);
} // add


Sexp car(const vector<Sexp> & selist  ){
  if( selist.size() != 1 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : cons"); // 
  return selist[0].Slist[0];
} // add

Sexp cdr(const vector<Sexp> & selist  ){
  if( selist.size() != 1 )
    throw Sexp(ERROR,"ERROR (incorrect number of arguments) : cons"); // 
  vector<Sexp> temp;
  for( int i = 1 ; i < selist[0].Slist.size() ; i++){
    temp.push_back(selist[0].Slist[i]);
  } // for
  return Sexp(LIST,temp);
} // add

Sexp predictAtom(const vector<Sexp> & selist  ){

} // add

Sexp predictPair(const vector<Sexp> & selist  ){

} // add

Sexp predictList(const vector<Sexp> & selist  ){

} // add

Sexp predictNull(const vector<Sexp> & selist  ){

} // add

Sexp predictInteger(const vector<Sexp> & selist  ){

} // add

Sexp predictNumber(const vector<Sexp> & selist  ){

} // add


Sexp predictString(const vector<Sexp> & selist  ){

} // add


Sexp predictBoolean(const vector<Sexp> & selist  ){

} // add

Sexp predictSymbol(const vector<Sexp> & selist  ){

} // add

Sexp predictSymbol(const vector<Sexp> & selist  ){

} // add

Sexp predictSymbol(const vector<Sexp> & selist  ){

} // add

Sexp greaterThan(const vector<Sexp> & selist  ){

} // add


Sexp greaterEqual(const vector<Sexp> & selist  ){

} // add

Sexp lessThan(const vector<Sexp> & selist  ){

} // add

Sexp lessEqual(const vector<Sexp> & selist  ){

} // add


Sexp isSame(const vector<Sexp> & selist  ){

} // add

Sexp stringAppend(const vector<Sexp> & selist  ){

} // add

Sexp isStringGreaterThan(const vector<Sexp> & selist  ){

} // add

Sexp isStringLessThan(const vector<Sexp> & selist  ){

} // add

Sexp isStringSame(const vector<Sexp> & selist  ){

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
} // add

Sexp isEqual(const vector<Sexp> & selist  ){

} // add  

// if cond begin

int main(){
  int utestnum = 0;
  char ch = '\0';
  cin >> utestnum ; // read testnum，不用讀換行why!
  int endsignal = 0;
  Scanner *scanner = new Scanner();
  Parser parser(scanner);
  Evaluate evaluate;
  cout << "Welcome to OurScheme!" << endl ;	
  while( true ){
    try {
      cout << endl << "> ";
      parser.startParsing();
      evaluate.setSexp(parser.getSexp());
      evaluate.project1(endsignal);
      if ( endsignal == 1)
        break;
    } // try

    
    catch( ErrorCondition e ){
      e.PrintError();
      if( e.errornum == 1) // 跳出EOF
        break;
      // cout << "TokenError at line " << e.line << ", column " << e.column << ": " << e.what() << endl;
      cin.clear(); 
      scanner->encounterError();
      scanner->clear();
      parser.clear();
      evaluate.clear();
    } // catch

    cin.clear();
    scanner->clear();
    parser.clear();
    evaluate.clear();
  } // while
  cout << endl << "Thanks for using OurScheme!";	
  return 0;
} // main


 