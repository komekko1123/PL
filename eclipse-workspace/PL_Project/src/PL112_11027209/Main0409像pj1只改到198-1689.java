package PL112_11027209;
import java.util.*;
import java.lang.Exception;
import CYICE.*;

abstract class G{   
  public static int s_utestnum; // debug用
  public static int s_quit; // quit用的
  public static ICEInputStream sin = null;
  public static ArrayList<Identifier> s_DefineTable = null; // 放定義字的表，因為parser跟execute都會用到
  // 上面是I/O  下面是ENUM區
  
  public final static int EOF = 61396;
  public final static int IDENT = 1;
  public final static int CONSTANT = 2;
  public final static int INT = 3;
  public final static int FLOAT = 4;
  public final static int CHAR = 5;
  public final static int BOOL = 6;
  public final static int STRING = 7; 
  public final static int VOID = 8; 
  public final static int IF = 9; 
  public final static int ELSE = 10;
  public final static int WHILE = 11; 
  public final static int DO = 12; 
  public final static int RETURN = 13; 
  public final static int LSP = 14; // SMALL P (
  public final static int RSP = 15; 
  public final static int LMP = 16; // MID P [
  public final static int RMP = 17;
  public final static int LBP = 18; // BIG P{
  public final static int RBP = 19; 
  public final static int PLUS = 20;  // +
  public final static int MINUS = 21; // -
  public final static int STAR = 22;  // *
  public final static int DIV = 23;   // /
  public final static int PERCENT = 24; // %
  public final static int HAT = 25; // ^
  public final static int GT   = 26; // greater than >
  public final static int LT = 27;  // lower than <
  public final static int GE = 28; // greater equal >=
  public final static int LE = 29; // lower equal <=
  public final static int EQ = 30; // ==
  public final static int NEQ = 31; // !=
  public final static int HALF_AND = 32; // &
  public final static int HALF_OR = 33;  // |
  public final static int HALF_EQ = 34; // =
  public final static int HALF_NEQ = 35; // !
  public final static int AND = 36; // &&
  public final static int OR = 37; // ||
  public final static int PE = 38; // +=
  public final static int ME = 39; // -=
  public final static int TE = 40; // *=
  public final static int DE = 41; // /=
  public final static int RE = 42; // %=
  public final static int PP = 43; // ++
  public final static int MM = 44; // -- 
  public final static int RS = 45; // >> 
  public final static int LS = 46; // <<
  public final static int SC = 47 ; // ;
  public final static int CM = 48; // , COMMA 
  public final static int QM = 49; // ? QUESTION MARK
  public final static int CL = 50; // : COLON  
  public final static int COUT = 51; // COUT
  public final static int CIN = 52; // CIN
  public final static int UNKNOWN = 16926; // 未知NODE
  
  public static void Init() throws Throwable {
    // sin = new ICEInputStream( );  // 這行可以改要讀什麼txtttttttttt
    sin = new ICEInputStream( "test2.txt" );  // 這行可以改要讀什麼txtttttttttt
    s_DefineTable = new ArrayList<Identifier>();
    s_quit = 0;
  } // Init()
  
  public static boolean CheckIDInTable( String str ) throws Throwable {
    for ( int i = 0 ; i < s_DefineTable.size() ; i++ ) {
      if ( s_DefineTable.get( i ).mvalue.equals( str ) ) // 有相同的字代表已被定義 
        return true;
    } // for
    
    return false;
  } // CheckIDInTable()
  
  public static void UpdateIDInTable( Identifier Ide ) throws Throwable {
    if ( CheckIDInTable( Ide.mvalue ) == true ) {
      for ( int i = 0 ; i < s_DefineTable.size() ; i++ ) { // 找到值並更改
        if ( Ide.mvalue.equals( s_DefineTable.get( i ).mvalue ) ) // 找到同字串
          s_DefineTable.set( i, Ide );  
      } // for
    } // if
    
    else // 只接放進去
      s_DefineTable.add( Ide );  
  } // UpdateIDInTable() 
  
  public static Identifier GetIDInTable( String str ) throws Throwable {
    Identifier ide = null;
    for ( int i = 0 ; i < s_DefineTable.size() ; i++ ) { // 找到值並更改
      if ( str.equals( s_DefineTable.get( i ).mvalue ) ) // 找到同字串
        ide = s_DefineTable.get( i );
    } // for
    
    return ide;
  } // GetIDInTable() 
  
  
  public static void CYPrint( String str ) throws Throwable {
    byte[] binary = str.getBytes( "Big5" );
    System.out.write( binary, 0, binary.length );
  } // CYPrint()
  
  public static void ErrorPrint( boolean a, boolean b, boolean c, Token t  ) {
    if ( a == true ) {
      System.out.print( "> Unrecognized token with first char : " + '\'' + t.mvalue + '\'' + '\n' );
      return;
    } // if
    else if ( b == true ) {
      System.out.print( "> Unexpected token : " + '\'' + t.mvalue + '\'' + '\n' );
    } // else if

    else if ( c == true ) {
      System.out.print( "> Undefined identifier : " + '\'' + t.mvalue + '\'' + '\n' );
    } // else if
  } // ErrorPrint() 
} // class G


class Token {
  public int mtype;
  public String mvalue;
  public int mline;
  public int mcolumn;
  Token() {
    this.mtype = 0 ;
    this.mcolumn = 0;
    this.mline = 0;
    this.mvalue = new String();
  } // Token()
  
  Token( int type, String value, int line, int column ) {
    this.mtype = type;
    this.mvalue = value;
    this.mline = line;
    this.mcolumn = column;
  } // Token()
} // class Token

class Identifier {
  public int mtype;
  public String mvalue;
  public double mnumvalue;
  public String mcheck; // check int or double
  Identifier() {
    this.mtype = 0;
    this.mvalue = new String();
    this.mnumvalue = 0;
    this.mcheck = new String();
  } // Identifier()
  
  Identifier( int type, String value, double numvalue, String mcheck ) {
    this.mtype = type;
    this.mvalue = value;
    this.mnumvalue = numvalue;
    this.mcheck = mcheck;
  } // Identifier()  
} // class Identifier





class SCANNER {
  public LinkedHashMap<String,String> mLHM = new LinkedHashMap<String,String>();
  public ArrayList<Token> mTokenlist = new ArrayList<Token>(); // 放每個token 用完刪除remove_all
  public Token mnow_token = null;
  public int m_tokenNum = 0 ; // token
  public int ms_lineNumber = 1;
  public int ms_columnNumber = 1;
  public char m_nextchar = '\0';
  public Token FetchToken() throws Throwable {
    if (mnow_token != null) {
      Token t = new Token( mnow_token.mtype, mnow_token.mvalue, 
                           mnow_token.mline, mnow_token.mcolumn  ); 
      mnow_token = null;
      return t;
    } // if
    
    char first = '\0';
    String str = new String(); 
    if ( IsSpace( m_nextchar ) ) // 不是whitespace 有包括\0
      m_nextchar = GetNextNonWhiteSpaceChar(); // 拿下一個非white的token
    first = m_nextchar;
    PeekNextChar();
    int check = 0;
    str = str + first;  
    if ( first == '\0' ) // EOF了
      return new Token( G.EOF, "", ms_lineNumber, ms_columnNumber  );   
    else if ( IsLetter( first ) ) { // 字母開頭
      while ( IsLetter( this.m_nextchar ) || IsNum( this.m_nextchar ) || m_nextchar == '_'   ) {
        first = m_nextchar; // 字母的話可以是字母數字下底線----是這三者的話
        str = str + first;
        PeekNextChar();   
      } // while
      
      if ( str.equals( "int" ) )
        return new Token( G.INT, str, ms_lineNumber, ms_columnNumber  );  
      else if ( str.equals( "float" ) )
        return new Token( G.FLOAT, str, ms_lineNumber, ms_columnNumber  );  
      else if ( str.equals( "char" ) )
        return new Token( G.CHAR, str, ms_lineNumber, ms_columnNumber  );  
      else if ( str.equals( "bool" ) )
        return new Token( G.BOOL, str, ms_lineNumber, ms_columnNumber  );  
      else if ( str.equals( "string" ) )
        return new Token( G.STRING, str, ms_lineNumber, ms_columnNumber  );  
      else if ( str.equals( "void" ) )
        return new Token( G.VOID, str, ms_lineNumber, ms_columnNumber  );  
      else if ( str.equals( "if" ) )
        return new Token( G.IF, str, ms_lineNumber, ms_columnNumber  );  
      else if ( str.equals( "else" ) )
        return new Token( G.ELSE, str, ms_lineNumber, ms_columnNumber  );  
      else if ( str.equals( "while" ) )
        return new Token( G.WHILE, str, ms_lineNumber, ms_columnNumber  );  
      else if ( str.equals( "do" ) )
        return new Token( G.DO, str, ms_lineNumber, ms_columnNumber  );  
      else if ( str.equals( "return" ) )
        return new Token( G.RETURN, str, ms_lineNumber, ms_columnNumber  );  
      else if ( str.equals( "cin" ) )
        return new Token( G.CIN, str, ms_lineNumber, ms_columnNumber  );    
      else if ( str.equals( "cout" ) )
        return new Token( G.COUT, str, ms_lineNumber, ms_columnNumber  );  
      else if ( str.equals( "true" ) )
        return new Token( G.CONSTANT, str, ms_lineNumber, ms_columnNumber  );  
      else if ( str.equals( "false" ) )
        return new Token( G.CONSTANT, str, ms_lineNumber, ms_columnNumber  );  
      else
        return new Token( G.IDENT, str, ms_lineNumber, ms_columnNumber  ); 
    } // else if
    
    else if ( IsNum( first ) ) { // 有可能是數字or小數
      if ( m_nextchar == '.' )
        check = check + 1;
      while ( ( IsNum( this.m_nextchar ) || this.m_nextchar == '.' ) && check < 2 ) { 
        first = m_nextchar; // 數字的話可以是.或是數字且只能有一個小數點----是這二者的話
        str = str + first;
        PeekNextChar();   
        if ( this.m_nextchar == '.' ) // 有一個點點 不能超過兩個
          check = check + 1; 
      } // while
      
      return new Token( G.CONSTANT, str, ms_lineNumber, ms_columnNumber  );
    } // else if 
    
    else if ( first == '.' ) { // ..後面只能是數字
      if ( ! IsNum( this.m_nextchar ) ) // 下一個一定要是數字才對
        return new Token( G.UNKNOWN, str, ms_lineNumber, ms_columnNumber  );
      while (  IsNum( this.m_nextchar ) ) { // 數字的話可以是.或是數字且只能有一個小數點
        first = m_nextchar; // 是這三者的話
        str = str + first;
        PeekNextChar();        
      } // while
      
      return new Token( G.CONSTANT, str, ms_lineNumber, ms_columnNumber  );
    } // else if
    
    else if ( first == '+' ) { // 符號
      if ( this.m_nextchar == '+' ) {
        PeekNextChar();
        return new Token( G.PP, "++", ms_lineNumber, ms_columnNumber  );
      } // if
      
      else if ( this.m_nextchar == '=' ) {
        PeekNextChar();
        return new Token( G.PE, "+=", ms_lineNumber, ms_columnNumber  );
      } // else if
      
      else
        return new Token( G.PLUS, "+", ms_lineNumber, ms_columnNumber  );
    } // else if
    
    else if ( first == '-' ) { // 符號
      if ( this.m_nextchar == '-' ) {
        PeekNextChar();
        return new Token( G.MM, "--", ms_lineNumber, ms_columnNumber  );
      } // if
      
      else if ( this.m_nextchar == '=' ) {
        PeekNextChar();
        return new Token( G.ME, "-=", ms_lineNumber, ms_columnNumber  );
      } // else if
      
      else
        return new Token( G.MINUS, "-", ms_lineNumber, ms_columnNumber  );
    } // else if
  
    else if ( first == '*' ) { // 符號
      if ( this.m_nextchar == '=' ) {
        PeekNextChar();
        return new Token( G.TE, "*=", ms_lineNumber, ms_columnNumber  );
      } // if
      
      else
        return new Token( G.STAR, "*", ms_lineNumber, ms_columnNumber  );
    } // else if
    
    else if ( first == '/' ) {  // 單個通常是註解
      if ( this.m_nextchar == '/' ) {
        while ( !G.sin.AtEOLN()  ) {
          PeekNextChar();
          if ( m_nextchar == '\0' ) // 註解到結束eof
            return new Token( G.EOF, "", ms_lineNumber, ms_columnNumber  );
        } // while
        
        m_nextchar = GetNextNonWhiteSpaceChar();
        return FetchToken(); // 註解就找下個去
      } // if
      
      else if ( this.m_nextchar == '=' ) {
        PeekNextChar();
        return new Token( G.DE, "/=", ms_lineNumber, ms_columnNumber  );
      } // else if
      
      else
        return new Token( G.DIV, "/", ms_lineNumber, ms_columnNumber );
    } // else if
  
    else if ( first == '%' ) { // 符號
      if ( this.m_nextchar == '=' ) {
        PeekNextChar();
        return new Token( G.RE, "%=", ms_lineNumber, ms_columnNumber  );
      } // if
      
      else
        return new Token( G.PERCENT, "%", ms_lineNumber, ms_columnNumber  );
    } // else if
    
    else if ( first == '^' )  // ^
      return new Token( G.HAT, "^", ms_lineNumber, ms_columnNumber  );
    else if ( first == '(' )
      return new Token( G.LSP, "(", ms_lineNumber, ms_columnNumber  );
    else if ( first == ')'  ) 
      return new Token( G.RSP, ")", ms_lineNumber, ms_columnNumber  );
    else if ( first == '[' )
      return new Token( G.LMP, "[", ms_lineNumber, ms_columnNumber  );
    else if ( first == ']'  ) 
      return new Token( G.RMP, "]", ms_lineNumber, ms_columnNumber  );
    else if ( first == '{' )
      return new Token( G.LBP, "{", ms_lineNumber, ms_columnNumber  );
    else if ( first == '}'  ) 
      return new Token( G.RBP, "}", ms_lineNumber, ms_columnNumber  );
    else if ( first == ';' )
      return new Token( G.SC, ";", ms_lineNumber, ms_columnNumber  );
    else if ( first == ',' )
      return new Token( G.CM, ",", ms_lineNumber, ms_columnNumber  );
    else if ( first == '?' )
      return new Token( G.QM, "?", ms_lineNumber, ms_columnNumber  );
    else if ( IsBo( first ) ) {
      if ( first == ':' ) 
        return new Token( G.CL, ":", ms_lineNumber, ms_columnNumber  );
      else if ( first == '<' ) { // <= 或 <> 或<
        if ( m_nextchar == '=' ) {
          PeekNextChar();
          return new Token( G.LE, "<=", ms_lineNumber, ms_columnNumber  );
        } // if
        
        else if ( m_nextchar == '<' ) {
          PeekNextChar();
          return new Token( G.LS, "<<", ms_lineNumber, ms_columnNumber  );  
        } // else if
        
        else
          return new Token( G.LT, "<", ms_lineNumber, ms_columnNumber  );
      } // else if
      
      else if ( first == '>' ) { // >= 或 > 
        if ( m_nextchar == '=' ) { 
          PeekNextChar();
          return new Token( G.GE, ">=", ms_lineNumber, ms_columnNumber  );
        } // if
        
        else if ( m_nextchar == '>' ) {
          PeekNextChar();
          return new Token( G.RS, ">>", ms_lineNumber, ms_columnNumber  );  
        } // else if
        
        else 
          return new Token( G.GT, ">", ms_lineNumber, ms_columnNumber  );
      } // else if
      
      else if ( first == '=' ) { //
        if ( m_nextchar == '=' ) {
          PeekNextChar();
          return new Token( G.EQ, "==", ms_lineNumber, ms_columnNumber );  
        } // if
        
        else
          return new Token( G.HALF_EQ, "=", ms_lineNumber, ms_columnNumber  );  
      } // else if
      
      else if ( first == '!' ) { //
        if ( m_nextchar == '=' ) {
          PeekNextChar();
          return new Token( G.NEQ, "!=", ms_lineNumber, ms_columnNumber );  
        } // if
        
        else
          return new Token( G.HALF_NEQ, "!", ms_lineNumber, ms_columnNumber  );            
      } // else if 
      
      else if ( first == '&' ) { //
        if ( m_nextchar == '&' ) {
          PeekNextChar();
          return new Token( G.AND, "&&", ms_lineNumber, ms_columnNumber );  
        } // if
        
        else
          return new Token( G.HALF_AND, "&", ms_lineNumber, ms_columnNumber  );            
      } // else if 
      
      else if ( first == '|' ) { //
        if ( m_nextchar == '|' ) {
          PeekNextChar();
          return new Token( G.OR, "||", ms_lineNumber, ms_columnNumber );  
        } // if
        
        else
          return new Token( G.HALF_OR, "|", ms_lineNumber, ms_columnNumber  );            
      } // else if 
    } // else if 
    
    else if ( first == '\"' ) {  // 這邊可能會有錯***
      while ( m_nextchar != '\"' ) { 
        first = m_nextchar; 
        str = str + first;
        PeekNextChar();
      } // while
      
      PeekNextChar();
      str = str + first;
      return new Token( G.CONSTANT, str, ms_lineNumber, ms_columnNumber  );    
    } // else if
   
    else if ( first == '\'' ) { // 這裡需要再看看
      if ( m_nextchar == '\n' )
        return new Token( G.UNKNOWN, str, ms_lineNumber, ms_columnNumber  ); 
      PeekNextChar();
      str = str + first;
      if ( m_nextchar != '\'' )
        return new Token( G.UNKNOWN, str, ms_lineNumber, ms_columnNumber  ); 
      PeekNextChar();
      str = str + first;
      return new Token( G.CONSTANT, str, ms_lineNumber, ms_columnNumber  );    
    } // else if
    
    else   
      return new Token( G.UNKNOWN, str, ms_lineNumber, ms_columnNumber  );
    Token t = new Token();
    return t;
  } // FetchToken()
  
  public void PeekNextChar() throws Throwable {
    CharObj cx = new CharObj( '\0' );
    if ( G.sin.ReadChar( cx ) == false   ) {  // EOF
      m_nextchar = '\0';
      return;
    } // if
    
    m_nextchar = cx.val;
    if ( m_nextchar == '\n' ) {
      ms_lineNumber++;
      ms_columnNumber = 1;
    } // if
    else
      ms_columnNumber++;
  } // PeekNextChar()
  
  public char GetNextNonWhiteSpaceChar() throws Throwable {
    CharObj cx = new CharObj( '\0' );
    while ( cx.val == '\0' || cx.val == '\t' || cx.val == ' ' ||  cx.val == '\n' ) {
      if ( G.sin.ReadChar( cx ) == false   ) {  // EOF
        return '\0';
      } // if
      
      if ( cx.val == '\n' ) {
        ms_lineNumber++;
        ms_columnNumber = 1;
      } // if
      
      else
        ms_columnNumber++;
    } // while       
    
    return cx.val;
  } // GetNextNonWhiteSpaceChar()
  
  public boolean IsLetter( char c ) throws Throwable {
    if ( ( c >= 'a' && c <= 'z' ) || (  c >= 'A' &&  c <= 'Z' )  )
      return true;
    else
      return false;
  } // IsLetter()
  
  public boolean IsNum( char c ) throws Throwable {
    if ( c >= '0' && c <= '9' )
      return true;
    else
      return false;
  } // IsNum()  
  
  public boolean IsSign( char c ) throws Throwable {
    if ( c == '+' || c == '-' )
      return true;
    else 
      return false;
  } // IsSign()
  
  public boolean IsSpace( char c ) throws Throwable {
    if ( c == '\t' || c == ' ' || c == '\n' || c == '\0'  )
      return true;
    else
      return false;
  } // IsSpace()
  
  public boolean IsBo( char c ) throws Throwable {
    if ( c == '>' || c == '<' ||  c == '=' || c == ':' || c == '!' || c == '&' || c == '|'  )
      return true;
    else
      return false;
  } // IsBo()
  
  public ArrayList<Token> GetTokenList() throws Throwable {
    return mTokenlist;
  } // GetTokenList()
   
  public void EncounterError() throws Throwable {
    if ( m_nextchar == '\n' )
      return;
    while ( !G.sin.AtEOLN()  ) {
      PeekNextChar();
      if ( m_nextchar == '\0' ) // eof
        return;
    } // while
    
    PeekNextChar();
  } // EncounterError()
  
  public Token PeekToken() throws Throwable {
    mnow_token = FetchToken();
    return mnow_token;
  } // PeekToken() 
  
  
} // class SCANNER

class PARSER {
  public ArrayList<Token> mTokenList = null ;
  public SCANNER msc = null; // myscanner
  public int mTCt; // 主要的計數器
  public int mTCt_test;
  public int m_nowCommand; // 現在在command的第幾個
  public boolean mError1 = false; // 字元錯誤
  public boolean mError2 = false; // 語法錯誤
  public boolean mError3 = false; // 未定義錯誤
  public boolean mExecute = false; // 讀到分號或是quit的處理
  public int[] mDyerror = new int[3]; // 動態error陣列(用來看command有沒有錯，語法分析用)
  public PARSER( SCANNER sc ) {
    mTokenList = new ArrayList<Token>();
    this.msc = sc;
    mTCt = 0; // token計數器
    m_nowCommand = 0;
    for ( int i = 0 ; i < 3 ; i++ ) {
      mDyerror[i] = 0;
    } // for
  } // PARSER()
  
  public void FetchToken() throws Throwable {
    mTCt = 0;
    if ( mTokenList.size() == 0 ) {
      mTokenList.add( msc.FetchToken() );    
    } // if
    
    else {
      mTokenList.add( msc.FetchToken() ) ;
    } // else
    
    for ( int i = 0 ; i < 3 ; i++ ) {
      mDyerror[i] = 0;
    } // for
  } // FetchToken()
  
  
  public void ParserIt()  throws Throwable {
    FetchToken();
    GoUser_Input();
    ErrorDetectAndHandle();  
  } // ParserIt()
  
  public boolean GoUser_Input() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( IsType_Specifier() == true || mTokenList.get( mTCt ).mtype == G.VOID ) {
      m_nowCommand = 0;
      if ( GoDefinition() == true ) { // 上面的條件是Go Definition
        mExecute = true;
        return true;
      } // if
      
      else 
        return false;
    } // if 
    
    else if ( IsStatement() == true  ) { // 下一回合在看看
      m_nowCommand = 1;
      if ( GoStatement() == true ) { // 上面的條件是Go Definition
        mExecute = true;
        return true;
      } // if
      
      return false;
    } // else if
    
    else {
      m_nowCommand = 2;
      mDyerror[m_nowCommand] = 1;
      return false;
    } // else
  } // GoUser_Input()
  
  public boolean GoDefinition() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoType_Specifier() == true ) {
      if ( mTokenList.get( mTCt ).mtype == G.IDENT ) {
        mTCt++;
        if ( mTCt >= mTokenList.size() )
          return false;
        if ( GoFunction_Definition_Or_Declarators() == true )
          return true;
        else
          return false;
      } // if
      
      else {  // 不是ident
        mDyerror[m_nowCommand] = 1;
        return false;
      } // else
    } // if
    
    else if ( mTokenList.get( mTCt ).mtype == G.VOID ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if ( mTokenList.get( mTCt ).mtype == G.IDENT ) {
        mTCt++;
        if ( GoFunction_Definition_Without_ID() == true )
          return true;
        else
          return false;
      } // if
      
      else {  // 不是ident
        mDyerror[m_nowCommand] = 1;
        return false;
      } // else
    } // else if
    
    else { // error
      mDyerror[m_nowCommand] = 1;
      mTCt++;
      return false;
    } // else
  } // GoDefinition()
  
  public boolean GoType_Specifier() throws Throwable { // 這個加
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( IsType_Specifier() == true ) {
      mTCt++;
      return true;  
    } // if
    else { 
      mDyerror[m_nowCommand] = 1;  
      return false;
    } // else
  } // GoType_Specifier()
  
  public boolean GoFunction_Definition_Or_Declarators() throws Throwable { // 
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( mTokenList.get( mTCt ).mtype == G.LSP ) { // 小左括號就是GoFWithID
      if ( GoFunction_Definition_Without_ID() == true )
        return true;
      else
        return false;
    } // if
    
    else if ( mTokenList.get( mTCt ).mtype == G.LMP || mTokenList.get( mTCt ).mtype == G.SC || 
        mTokenList.get( mTCt ).mtype == G.CM ) {
      if ( GoRest_Of_Declarators() == true ) // 中左刮或逗號或分號就是GoFWithID
        return true;
      else
        return false;
    } // else if
    
    else { // error
      mDyerror[m_nowCommand] = 1;
      mTCt++;
      return false;
    } // else
  } // GoFuncion_Definition_Or_Declarators()
  
  public boolean GoRest_Of_Declarators() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( mTokenList.get( mTCt ).mtype == G.LMP ) {
      mTCt++;      
      if ( mTCt >= mTokenList.size() )
        return false;
      if ( mTokenList.get( mTCt ).mtype != G.CONSTANT ) { // 是數字就對不是就是error
        mDyerror[m_nowCommand] = 1;
        return false;
      } // if
        
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if ( mTokenList.get( mTCt ).mtype != G.RMP ) { // 是數字就對不是就是error
        mDyerror[m_nowCommand] = 1; 
        return false;
      } // if
          
      mTCt++; // ************************這一段是前面的中括號(一次or可有可無)
    } // if  
     
    if ( mTCt >= mTokenList.size() )
      return false;   
    while ( mTokenList.get( mTCt ).mtype == G.CM  ) { // 可有可無
      mTCt++; 
      if ( mTCt >= mTokenList.size() )
        return false;
      if ( mTokenList.get( mTCt ).mtype != G.IDENT  ) { // 是數字就對不是就是error
        mDyerror[m_nowCommand] = 1;
        return false;
      } // if 
      
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if ( mTokenList.get( mTCt ).mtype == G.LMP ) {
        mTCt++;      
        if ( mTCt >= mTokenList.size() )
          return false;
        if ( mTokenList.get( mTCt ).mtype != G.CONSTANT ) { // 是數字就對不是就是error
          mDyerror[m_nowCommand] = 1;
          return false;
        } // if
          
        mTCt++;
        if ( mTCt >= mTokenList.size() )
          return false;
        if ( mTokenList.get( mTCt ).mtype != G.RMP ) { // 是數字就對不是就是error
          mDyerror[m_nowCommand] = 1; 
          return false;
        } // if
            
        mTCt++; 
        if ( mTCt >= mTokenList.size() )
          return false;
      } // if
    } // while
    
    if ( mTCt >= mTokenList.size() )
      return false;
    
    if (  mTokenList.get( mTCt ).mtype == G.SC  ) {
      mTCt++;
      return true;  
    } // else if

    else { // error
      mDyerror[m_nowCommand] = 1;
      mTCt++; //       **** 這可能要改
      return false;
    } // else
  } // GoRest_Of_Declarators()
  
  public boolean GoFunction_Definition_Without_ID() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    
    if ( mTokenList.get( mTCt ).mtype == G.LSP )  // 先左刮
      mTCt++ ;      
    else { // error
      mDyerror[m_nowCommand] = 1;
      mTCt++; //       **** 這可能要改
      return false;
    } // else
    
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( mTokenList.get( mTCt ).mtype == G.VOID  ) { 
      mTCt++;  
    } // if
    
    else if ( IsType_Specifier() == true ) { // formal_parameter_list開頭是type_specifer
      if ( GoFormal_Parameter_List() == false ) // 這樣if應該還是會跑
        return false;
    } // else if
    
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( mTokenList.get( mTCt ).mtype == G.RSP )  // 先左刮
      mTCt++ ;      
    else { // error
      mDyerror[m_nowCommand] = 1;
      mTCt++; //       **** 這可能要改
      return false;
    } // else
     
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoCompound_Statement() == true )
      return true;
    else
      return false;
  } // GoFuction_Definition_Without_ID()
  
  public boolean GoFormal_Parameter_List() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;   
    if ( GoType_Specifier() != true ) {
      mDyerror[m_nowCommand] = 1;
      mTCt++; //       **** 這可能要改
      return false;
    } // if  
    
    if ( mTCt >= mTokenList.size() )
      return false; 
    if ( mTokenList.get( mTCt ).mtype == G.HALF_AND ) {
      mTCt++;  
    } // if
      
    if ( mTCt >= mTokenList.size() )
      return false;   
    if ( mTokenList.get( mTCt ).mtype != G.IDENT ) {
      mDyerror[m_nowCommand] = 1;
      mTCt++; //       **** 這可能要改
      return false;
    } // if
    
    mTCt++;
    if ( mTCt >= mTokenList.size() )
      return false;   
    if ( mTokenList.get( mTCt ).mtype == G.LMP ) {
      mTCt++;      
      if ( mTCt >= mTokenList.size() )
        return false;
      if ( mTokenList.get( mTCt ).mtype != G.CONSTANT ) { // 是數字就對不是就是error
        mDyerror[m_nowCommand] = 1;
        return false;
      } // if
        
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if ( mTokenList.get( mTCt ).mtype != G.RMP ) { // 是數字就對不是就是error
        mDyerror[m_nowCommand] = 1; 
        return false;
      } // if
          
      mTCt++; // ************************這一段是前面的中括號(一次or可有可無)
    } // if  
        
    if ( mTCt >= mTokenList.size() )
      return false;   
        
    while ( mTokenList.get( mTCt ).mtype == G.CM  ) { // 這個while 感覺是塞上面的東西  
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;  
      if ( GoType_Specifier() != true ) {
        mDyerror[m_nowCommand] = 1;
        mTCt++; //       **** 這可能要改
        return false;
      } // if  
      
      if ( mTCt >= mTokenList.size() )
        return false; 
      if ( mTokenList.get( mTCt ).mtype == G.HALF_AND ) {
        mTCt++;  
      } // if
        
      if ( mTCt >= mTokenList.size() )
        return false;   
      if ( mTokenList.get( mTCt ).mtype != G.IDENT ) {
        mDyerror[m_nowCommand] = 1;
        mTCt++; //       **** 這可能要改
        return false;
      } // if
      
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;   
      if ( mTokenList.get( mTCt ).mtype == G.LMP ) {
        mTCt++;      
        if ( mTCt >= mTokenList.size() )
          return false;
        if ( mTokenList.get( mTCt ).mtype != G.CONSTANT ) { // 是數字就對不是就是error
          mDyerror[m_nowCommand] = 1;
          return false;
        } // if
          
        mTCt++;
        if ( mTCt >= mTokenList.size() )
          return false;
        if ( mTokenList.get( mTCt ).mtype != G.RMP ) { // 是數字就對不是就是error
          mDyerror[m_nowCommand] = 1; 
          return false;
        } // if
            
        mTCt++; 
        if ( mTCt >= mTokenList.size() )
          return false;  
      } // if  
    } // while
    
    return true;
  } // GoFormal_Parameter_List()
  
  public boolean GoCompound_Statement() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( mTokenList.get( mTCt ).mtype != G.LBP ) {
      mDyerror[m_nowCommand] = 1;
      mTCt++; //       **** 這可能要改
      return false;
    } // if
    
    mTCt++;
    if ( mTCt >= mTokenList.size() )
      return false;
    while ( IsType_Specifier() || IsStatement() ) { // declaration是type開頭
      if ( IsType_Specifier() == true ) {
        if ( GoDeclaration() != true )
          return false;
        if ( mTCt >= mTokenList.size() )
          return false;
      } // if
      
      else if ( IsStatement() == true ) {
        if ( GoStatement() != true )
          return false;
        if ( mTCt >= mTokenList.size() )
          return false;  
      } // else if
    } // while
    
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( mTokenList.get( mTCt ).mtype != G.RBP ) {
      mDyerror[m_nowCommand] = 1;
      mTCt++; //       **** 這可能要改
      return false;   
    } // if
    
    else { // 最後一個是右大刮
      mTCt++;
      return true;
    } // else
  } // GoCompound_Statement()
  
  public boolean GoDeclaration() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoType_Specifier() == false ) {
      mDyerror[m_nowCommand] = 1;
      mTCt++; //       **** 這可能要改
      return false;
    } // if
    
    if ( mTCt >= mTokenList.size() )
      return false;
    
    if ( mTokenList.get( mTCt ).mtype != G.IDENT ) {
      mDyerror[m_nowCommand] = 1;
      mTCt++; //       **** 這可能要改
      return false;
    } // if
    
    mTCt++;
    if ( mTCt >= mTokenList.size() )
      return false;
    
    if ( mTokenList.get( mTCt ).mtype == G.LMP || mTokenList.get( mTCt ).mtype == G.SC || 
        mTokenList.get( mTCt ).mtype == G.CM ) {
      if ( GoRest_Of_Declarators() == true ) // 中左刮或逗號或分號就是GoFWithID
        return true;
      else
        return false;
    } // if
    
    else { // error
      mDyerror[m_nowCommand] = 1;
      mTCt++;
      return false;
    } // else          
  } // GoDeclaration()
  
  public boolean GoStatement() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if (  mTokenList.get( mTCt ).mtype == G.SC ) {
      mTCt++;
      return true;
    } // if
    
    else if (  IsExpression() == true ) {
      if ( GoExpression()  == true ) // 中左刮或逗號或分號就是GoFWithID
        return true;
      else
        return false;
    } // else if
    
    else if (  mTokenList.get( mTCt ).mtype == G.RETURN ) {
     mTCt++;
     if (  IsExpression() == true ) {
       if ( GoExpression() == false ) // 中左刮或逗號或分號就是GoFWithID
         return false;  
       if ( mTCt >= mTokenList.size() )
         return false;
     } // if
     
     if (  mTokenList.get( mTCt ).mtype == G.SC ) {
       mTCt++;
       return true;
     } // if
     
     else {
       mDyerror[m_nowCommand] = 1;
       mTCt++;
       return false;  
     } // else
     
    } // else if
    
    else if (  mTokenList.get( mTCt ).mtype == G.LBP ) {
      if ( GoCompound_Statement() == true )
        return true;
      else
        return false;
    } // else if
    
    else if (  mTokenList.get( mTCt ).mtype == G.IF ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if (  mTokenList.get( mTCt ).mtype != G.LSP ) {
        mDyerror[m_nowCommand] = 1;
        mTCt++;  // 可能要改
        return false;
      } // if
      
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if (  IsExpression() == false ) {
        mDyerror[m_nowCommand] = 1;
        mTCt++;
        return false;
      } // if
      
      if ( GoExpression() == false )
        return false;
      if ( mTCt >= mTokenList.size() )
        return false;
      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mDyerror[m_nowCommand] = 1;
        mTCt++;
        return false;
      } // if
      
      if ( mTCt >= mTokenList.size() )
        return false;
      if ( IsStatement() == false ) { // ****這邊可能有錯誤
        mDyerror[m_nowCommand] = 1;
        mTCt++;
        return false;
      } // if
      
      if ( GoStatement() == false )
        return false;
      if ( mTCt >= mTokenList.size() )
        return false;
      if ( mTokenList.get( mTCt ).mtype == G.ELSE ) {
        mTCt++; 
        if ( IsStatement() == false ) { // ****這邊可能有錯誤
          mDyerror[m_nowCommand] = 1;
          mTCt++; 
          return false;
        } // if
        
        if ( GoStatement() == false )
          return false;
      } // if
      
      return true;
    } // else if
    
    else if (  mTokenList.get( mTCt ).mtype == G.WHILE ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if (  mTokenList.get( mTCt ).mtype != G.LSP ) {
        mDyerror[m_nowCommand] = 1;
        mTCt++;
        return false;
      } // if
      
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if (  IsExpression() == false ) {
        mDyerror[m_nowCommand] = 1;
        mTCt++;
        return false;
      } // if
      
      if ( GoExpression() == false )
        return false;
      if ( mTCt >= mTokenList.size() )
        return false;
      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mDyerror[m_nowCommand] = 1;
        mTCt++;
        return false;
      } // if     
      
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if ( IsStatement() == false ) { // ****這邊可能有錯誤
        mDyerror[m_nowCommand] = 1;
        mTCt++;  
        return false;
      } // if
      
      if ( GoStatement() == false )
        return false;
      else
        return true;
    } // else if
    
    else if (  mTokenList.get( mTCt ).mtype == G.DO ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if (  IsStatement() == false ) {
        mDyerror[m_nowCommand] = 1;
        mTCt++;
        return false;
      } // if  
      
      if ( GoStatement() == false )
        return false;
      if ( mTCt >= mTokenList.size() )
        return false;
      
      if ( mTokenList.get( mTCt ).mtype != G.WHILE ) { // ****這邊可能有錯誤
        mDyerror[m_nowCommand] = 1;
        mTCt++;  
        return false;
      } // if  下面是while
             
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if (  mTokenList.get( mTCt ).mtype != G.LSP ) {
        mDyerror[m_nowCommand] = 1;
        mTCt++;
        return false;
      } // if
      
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if (  IsExpression() == false ) {
        mDyerror[m_nowCommand] = 1;
        mTCt++;
        return false;
      } // if
      
      if ( GoExpression() == false )
        return false;
      if ( mTCt >= mTokenList.size() )
        return false;
      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mDyerror[m_nowCommand] = 1;
        mTCt++;
        return false;
      } // if     
      
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if ( mTokenList.get( mTCt ).mtype != G.SC ) { // ****這邊可能有錯誤
        mDyerror[m_nowCommand] = 1;
        mTCt++;  
        return false;
      } // if
      
      else {
        mTCt++;
        return true; //最後是sc所以正確
      } // else
    } // else if  
    
    else {
      mDyerror[m_nowCommand] = 1;
      mTCt++;  
      return false;  
    } // else 
  } // GoStatement()
  
  public boolean GoExpression() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if (  IsExpression() != true ) {
      mTCt++;
      mDyerror[m_nowCommand] = 1;
      return false;
    } // if
    
    if ( GoBasic_Expression() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    
    while ( mTokenList.get( mTCt ).mtype == G.CM ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if (  IsExpression() != true ) {
        mTCt++;
        mDyerror[m_nowCommand] = 1;
        return false;
      } // if
  
      if ( GoBasic_Expression() == false )
        return false;
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true;
  } // GoExpression()
  
  public boolean GoBasic_Expression() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    
    if ( mTokenList.get( mTCt ).mtype == G.IDENT ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if ( GoRest_Of_Identifier_Started_Basic_Exp() == true )
        return true;
      else 
        return false;
    } // if
    
    else if ( mTokenList.get( mTCt ).mtype == G.PP ||  mTokenList.get( mTCt ).mtype == G.MM  ) {
      mTCt++; // 是PP或MM
      if ( mTCt >= mTokenList.size() )
        return false;  
      if ( mTokenList.get( mTCt ).mtype == G.IDENT ) {
        mTCt++;
        if ( mTCt >= mTokenList.size() )
          return false;
        if ( GoRest_Of_PPMM_Identifier_Started_Basic_Exp() == true )
          return true;
        else 
          return false;
      } // if
      
      else { // error 不是ID
        mDyerror[m_nowCommand] = 1;
        mTCt++;
        return false;
      } // else       
    } // else if

    else if ( IsSign() == true ) {
      mTCt++; // 是PP或MM
      if ( mTCt >= mTokenList.size() )
        return false;  
      while ( IsSign() == true ) {
        mTCt++;  
        if ( mTCt >= mTokenList.size() )
          return false;
      } // while
      
      if ( GoSigned_Unary_Exp() == false )
        return false;
      if ( mTCt >= mTokenList.size() )
        return false;  
      if ( GoRomce_And_Romloe() == true )
        return true;
      else
        return false;
    } // else if 
    
    else if ( mTokenList.get( mTCt ).mtype == G.CONSTANT ) {
      mTCt++; // 是PP或MM
      if ( mTCt >= mTokenList.size() )
        return false;   
      if ( GoRomce_And_Romloe() == true )
        return true;
      else
        return false;
    } // else if 
    
    else if ( mTokenList.get( mTCt ).mtype == G.LSP ) {
      mTCt++; // 是PP或MM
      if ( mTCt >= mTokenList.size() )
        return false;  
      
      if (  IsExpression() != true ) {
        mTCt++;
        mDyerror[m_nowCommand] = 1;
        return false;
      } // if
  
      if ( GoExpression() == false )
        return false;
      if ( mTCt >= mTokenList.size() )
        return false;
      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mDyerror[m_nowCommand] = 1;
        mTCt++;
        return false;
      } // if     
      
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if ( GoRomce_And_Romloe() == true )
        return true;
      else
        return false;
    } // else if 
    
    
    else { // error
      mDyerror[m_nowCommand] = 1;
      mTCt++;
      return false;
    } // else          
  } // GoBasic_Expression()
  
  public boolean GoRest_Of_Identifier_Started_Basic_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( mTokenList.get( mTCt ).mtype == G.LSP ) {
      mTCt++; // 是PP或MM
      if ( mTCt >= mTokenList.size() )
        return false;  
      
      if (  IsExpression() != true ) {
        mTCt++;
        mDyerror[m_nowCommand] = 1;
        return false;
      } // if
  
      if ( GoActual_Parameter_List() == false )
        return false;
      if ( mTCt >= mTokenList.size() )
        return false;
      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mDyerror[m_nowCommand] = 1;
        mTCt++;
        return false;
      } // if     
      
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if ( GoRomce_And_Romloe() == true )
        return true;
      else
        return false;
    } // if 
    
    
    if (  mTokenList.get( mTCt ).mtype == G.LMP ) {
      mTCt++; // 是PP或MM
      if ( mTCt >= mTokenList.size() )
        return false;  
      
      if (  IsExpression() != true ) {
        mTCt++;
        mDyerror[m_nowCommand] = 1;
        return false;
      } // if
  
      if ( GoExpression() == false )
        return false;
      if ( mTCt >= mTokenList.size() )
        return false;
      if (  mTokenList.get( mTCt ).mtype != G.RMP ) {
        mDyerror[m_nowCommand] = 1;
        mTCt++;
        return false;
      } // if     
      
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
    } // if
    
    if ( IsAssignment_Operator() == true ) {
      GoAssignment_Operator();
      if ( mTCt >= mTokenList.size() )
        return false;    
      if ( IsExpression() == false ) {
        mDyerror[m_nowCommand] = 1;
        mTCt++;
        return false;  
      } // if 
      
      if ( GoExpression() == true )
        return true;
      else
        return false;
    } // if
    
    else { // 這可能會有錯****
      if ( mTokenList.get( mTCt ).mtype != G.PP || mTokenList.get( mTCt ).mtype != G.MM ) {
        mTCt++;
        if ( mTCt >= mTokenList.size() )
          return false;    
      } // if
      
      if ( GoRomce_And_Romloe() == true ) 
        return true;  
      else
        return false;
    } // else
  } // GoBasic_Expression() 
 
  public boolean GoRest_Of_PPMM_Identifier_Started_Basic_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if (  mTokenList.get( mTCt ).mtype == G.LMP ) {
      mTCt++; // 是PP或MM
      if ( mTCt >= mTokenList.size() )
        return false;  
      
      if (  IsExpression() != true ) {
        mTCt++;
        mDyerror[m_nowCommand] = 1;
        return false;
      } // if
  
      if ( GoExpression() == false )
        return false;
      if ( mTCt >= mTokenList.size() )
        return false;
      if (  mTokenList.get( mTCt ).mtype != G.RMP ) {
        mDyerror[m_nowCommand] = 1;
        mTCt++;
        return false;
      } // if     
      
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
    } // if
    
    if ( GoRomce_And_Romloe() == true )
      return true;     
    else
      return false;
  } // GoRest_Of_PPMM_Identifier_Started_Basic_Exp()
  
  public boolean GoSign() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( IsSign() == true ) {
      mTCt++;
      return true;  
    } // if
    else { 
      mDyerror[m_nowCommand] = 1;  
      return false;
    } // else
  } // GoSign()
  
  public boolean GoActual_Parameter_List() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if (  IsExpression() != true ) {
      mTCt++;
      mDyerror[m_nowCommand] = 1;
      return false;
    } // if
    
    if ( GoBasic_Expression() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    
    while ( mTokenList.get( mTCt ).mtype == G.CM ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if (  IsExpression() != true ) {
        mTCt++;
        mDyerror[m_nowCommand] = 1;
        return false;
      } // if
  
      if ( GoBasic_Expression() == false )
        return false;
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true;
  } // GoActual_Parameter_List()
  
  public boolean GoAssignment_Operator() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( IsAssignment_Operator() == true ) {
      mTCt++;
      return true;  
    } // if
    else { 
      mDyerror[m_nowCommand] = 1;  
      return false;
    } // else
  } // GoAssignment_Operator()
  
  public boolean GoRomce_And_Romloe() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoRest_Of_Maybe_Logical_OR_Exp() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( mTokenList.get( mTCt ).mtype != G.QM ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if ( IsExpression() != true ) {
        mTCt++;
        mDyerror[m_nowCommand] = 1;
        return false;
      } // if
      
      if ( GoBasic_Expression() == false )
        return false;
      if ( mTCt >= mTokenList.size() )
        return false;
      
      if (  mTokenList.get( mTCt ).mtype != G.CL ) {
        mTCt++;
        mDyerror[m_nowCommand] = 1;
        return false;  
      } // if
      
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;
      if ( IsExpression() != true ) {
        mTCt++;
        mDyerror[m_nowCommand] = 1;
        return false;
      } // if
      
      if ( GoBasic_Expression() == false )
        return false;
      if ( mTCt >= mTokenList.size() )
        return false;
    } // if
    
    return true;
  } // GoRomce_And_Romloe()
  
  public boolean GoRest_Of_Maybe_Logical_OR_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoRest_Of_Maybe_Logical_AND_Exp() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    while ( mTokenList.get( mTCt ).mtype != G.OR ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;

      if ( GoMaybe_Logical_AND_Exp() != true ) {
        return false;
      } // if
      
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Logical_OR_Exp()
  
  //**************機快*************
  public boolean GoMaybe_Logical_AND_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoMaybe_Bit_OR_Exp() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    while ( mTokenList.get( mTCt ).mtype != G.AND ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;

      if ( GoMaybe_Bit_OR_Exp() != true ) {
        return false;
      } // if
      
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true; 
  } // GoMaybe_Logical_AND_Exp()
  
  public boolean GoRest_Of_Maybe_Logical_AND_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoRest_Of_Maybe_Bit_OR_Exp() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    while ( mTokenList.get( mTCt ).mtype != G.AND ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;

      if ( GoMaybe_Bit_OR_Exp() != true ) {
        return false;
      } // if
      
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Logical_AND_Exp()
  
  public boolean GoMaybe_Bit_OR_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoMaybe_Bit_Ex_OR_Exp() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    while ( mTokenList.get( mTCt ).mtype != G.HALF_OR ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;

      if ( GoMaybe_Bit_Ex_OR_Exp() != true ) {
        return false;
      } // if
      
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true;
  } // GoMaybe_Bit_OR_Exp()
  
  public boolean GoRest_Of_Maybe_Bit_OR_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoRest_Of_Maybe_Bit_Ex_OR_Exp() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    while ( mTokenList.get( mTCt ).mtype != G.OR ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;

      if ( GoMaybe_Bit_Ex_OR_Exp() != true ) {
        return false;
      } // if
      
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Bit_OR_Exp()
  
  
  public boolean GoMaybe_Bit_Ex_OR_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoRest_Of_Maybe_Logical_AND_Exp() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    while ( mTokenList.get( mTCt ).mtype != G.OR ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;

      if ( GoMaybe_Logical_AND_Exp() != true ) {
        return false;
      } // if
      
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true;
  } // GoMaybe_Bit_Ex_OR_Exp()
  
  public boolean GoRest_Of_Maybe_Bit_Ex_OR_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoRest_Of_Maybe_Logical_AND_Exp() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    while ( mTokenList.get( mTCt ).mtype != G.OR ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;

      if ( GoMaybe_Logical_AND_Exp() != true ) {
        return false;
      } // if
      
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Bit_Ex_OR_Exp()
  
  public boolean GoMaybe_Bit_AND_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoRest_Of_Maybe_Logical_AND_Exp() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    while ( mTokenList.get( mTCt ).mtype != G.OR ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;

      if ( GoMaybe_Logical_AND_Exp() != true ) {
        return false;
      } // if
      
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true;
  } //  GoMaybe_Bit_AND_Exp()
  
  public boolean GoRest_Of_Maybe_Bit_AND_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoRest_Of_Maybe_Logical_AND_Exp() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    while ( mTokenList.get( mTCt ).mtype != G.OR ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;

      if ( GoMaybe_Logical_AND_Exp() != true ) {
        return false;
      } // if
      
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Bit_AND_Exp()
  
  public boolean GoMaybe_Equality_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoRest_Of_Maybe_Logical_AND_Exp() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    while ( mTokenList.get( mTCt ).mtype != G.OR ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;

      if ( GoMaybe_Logical_AND_Exp() != true ) {
        return false;
      } // if
      
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true;
  } //  GoMaybe_Equality_Exp ()
  
  public boolean GoRest_Of_Maybe_Equality_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoRest_Of_Maybe_Logical_AND_Exp() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    while ( mTokenList.get( mTCt ).mtype != G.OR ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;

      if ( GoMaybe_Logical_AND_Exp() != true ) {
        return false;
      } // if
      
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true;
  } //  GoRest_Of_Maybe_Equality_Exp()
  
  public boolean GoMaybe_Relational_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoRest_Of_Maybe_Logical_AND_Exp() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    while ( mTokenList.get( mTCt ).mtype != G.OR ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;

      if ( GoMaybe_Logical_AND_Exp() != true ) {
        return false;
      } // if
      
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true;
  } //  GoMaybe_Relational_Exp()
  
  public boolean GoRest_Of_Maybe_Relational_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoRest_Of_Maybe_Logical_AND_Exp() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    while ( mTokenList.get( mTCt ).mtype != G.OR ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;

      if ( GoMaybe_Logical_AND_Exp() != true ) {
        return false;
      } // if
      
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true;
  } //  GoRest_Of_Maybe_Relational_Exp()
  
  public boolean GoMaybe_Shift_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoRest_Of_Maybe_Logical_AND_Exp() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    while ( mTokenList.get( mTCt ).mtype != G.OR ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;

      if ( GoMaybe_Logical_AND_Exp() != true ) {
        return false;
      } // if
      
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true;
  } //  GoMaybe_Shift_Exp()
  
  public boolean GoRest_Of_Maybe_Shift_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoRest_Of_Maybe_Logical_AND_Exp() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    while ( mTokenList.get( mTCt ).mtype != G.OR ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;

      if ( GoMaybe_Logical_AND_Exp() != true ) {
        return false;
      } // if
      
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true;
  } //  GoRest_Of_Maybe_Shift_Exp()
  
  public boolean GoMaybe_Additive_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoRest_Of_Maybe_Logical_AND_Exp() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    while ( mTokenList.get( mTCt ).mtype != G.OR ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;

      if ( GoMaybe_Logical_AND_Exp() != true ) {
        return false;
      } // if
      
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true;
  } //  GoMaybe_Additive_Exp()
  
  public boolean GoRest_Of_Maybe_Additive_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( GoRest_Of_Maybe_Logical_AND_Exp() == false )
      return false;
    if ( mTCt >= mTokenList.size() )
      return false;
    while ( mTokenList.get( mTCt ).mtype != G.OR ) {
      mTCt++;
      if ( mTCt >= mTokenList.size() )
        return false;

      if ( GoMaybe_Logical_AND_Exp() != true ) {
        return false;
      } // if
      
      if ( mTCt >= mTokenList.size() )
        return false;
    } // while
    
    return true;
  } //  GoRest_Of_Maybe_Additive_Exp()
  
  public boolean GoMaybe_Mult_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    return true;
  } //  GoMaybe_Mult_Exp()
  
  public boolean GoUnary_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    return true;
  } //  GoMaybe_Additive_Exp()
  
  public boolean GoSigned_Unary_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    return true;
  } //  GoRest_Of_Maybe_Additive_Exp()
  
  public boolean GoUnsigned_Unary_Exp() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    return true;
  } //  GoRest_Of_Maybe_Additive_Exp()
  
  public boolean IsSign() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( mTokenList.get( mTCt ).mtype == G.PLUS || mTokenList.get( mTCt ).mtype == G.MINUS ||
        mTokenList.get( mTCt ).mtype == G.HALF_NEQ )
      return true;
    else 
      return false;
    
  } // IsSign() 
  
  public boolean IsStatement() throws Throwable { // 看statement的前綴字
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( mTokenList.get( mTCt ).mtype == G.SC     || IsExpression() ||
         mTokenList.get( mTCt ).mtype == G.RETURN || IsCompound_Statement()  || 
         mTokenList.get( mTCt ).mtype == G.IF  ||  mTokenList.get( mTCt ).mtype == G.WHILE  ||  
         mTokenList.get( mTCt ).mtype == G.DO )
      return true;
    else 
      return false;
  } // IsStatement() 
  
  public boolean IsCompound_Statement() throws Throwable { // expression就是basic_expression
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( mTokenList.get( mTCt ).mtype == G.LBP )
      return true;
    else 
      return false;
  } // IsStatement() 
  
  public boolean IsExpression() throws Throwable { // expression就是basic_expression
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( mTokenList.get( mTCt ).mtype == G.IDENT    || mTokenList.get( mTCt ).mtype == G.PP ||
         mTokenList.get( mTCt ).mtype == G.MM   ||  mTokenList.get( mTCt ).mtype == G.CONSTANT  || 
         mTokenList.get( mTCt ).mtype == G.LSP || IsSign() )
      return true;
    else 
      return false;
  } // IsStatement() 
  
  
  public boolean IsType_Specifier() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( mTokenList.get( mTCt ).mtype == G.INT || mTokenList.get( mTCt ).mtype == G.CHAR ||
        mTokenList.get( mTCt ).mtype == G.FLOAT || mTokenList.get( mTCt ).mtype == G.STRING  || 
        mTokenList.get( mTCt ).mtype == G.BOOL  )
      return true;
    else 
      return false;
  } // IsType_Specifier() 
  
  public boolean IsAssignment_Operator() throws Throwable {
    if ( mTCt >= mTokenList.size() )
      return false;
    if ( mTokenList.get( mTCt ).mtype == G.HALF_EQ || mTokenList.get( mTCt ).mtype == G.TE ||
        mTokenList.get( mTCt ).mtype == G.DE       || mTokenList.get( mTCt ).mtype == G.RE  || 
        mTokenList.get( mTCt ).mtype == G.PE       || mTokenList.get( mTCt ).mtype == G.ME  )
      return true;
    else 
      return false;
  } // IsType_Specifier() 
  
  
  public void ClearIt() throws Throwable {
    mTCt = 0;
    m_nowCommand = 0;
    mTokenList.clear();  
    mError1 = false; // 字元錯誤
    mError2 = false; // 語法錯誤
    mError3 = false; // 未定義錯誤
    mExecute = false; // 讀到分號或是quit的處理
    for ( int i = 0 ; i < 3 ; i++ ) {
      mDyerror[i] = 0;
    } // for
    
  } // ClearIt()
  
  
  public void ErrorDetectAndHandle() throws Throwable {
    if ( mTokenList.size() <= 0 ) // 應該是不會有這種狀況
      return;
    Token t = mTokenList.get( mTokenList.size() - 1 ); // 拿最後一項token = 最新讀的
    if ( t.mtype == G.UNKNOWN ) // 順便檢查Unrecognized
      mError1 = true;
    for ( int i = 0 ; i < 3 ; i++ ) {
      if ( mDyerror[i] == 1 )
        mError2 = true; 
    } // for
    
    if ( mError1 || mError2 ) {
      G.ErrorPrint( mError1, mError2, mError3, t  ) ;
      msc.EncounterError();  
      this.ClearIt();
    } // if
    
    else if ( mError3 ) { // undefine比較難處理
      Token f = null;
      int stoploop = 0;
      for ( int i = mTokenList.size() - 1 ; i >= 0 && stoploop == 0 ; i-- ) {
        f = mTokenList.get( i );
        if ( f.mtype == G.IDENT ) {
          if ( G.CheckIDInTable( f.mvalue ) == false )
            stoploop = 1;
        } // if
      } // for
      
      G.ErrorPrint( mError1, mError2, mError3, f  ) ;
      msc.EncounterError();  
      this.ClearIt();  
    } // else if 
  } // ErrorDetectAndHandle()
  
  public boolean GetExecuteState() {
    return mExecute;  
  } // GetExecuteState()
  
  public ArrayList<Token> GetTable() {
    return mTokenList;  
  } // GetTable()
} // class PARSER

class Evalutor {
  public ArrayList<Token> mExpressionTable = null ;
  public ArrayList<Token> mleft = null ;
  public ArrayList<Token> mleftPostfix = null ;
  public ArrayList<Token> mright = null ;
  public ArrayList<Token> mrightPostfix = null ;    
  public boolean mHas_middle = false;
  public String middle; 
  public Evalutor() {
    mHas_middle = false;
    mleft =  new ArrayList<Token>();
    mright =  new ArrayList<Token>();
    mExpressionTable = new ArrayList<Token>();
    mleftPostfix = new ArrayList<Token>() ;
    mrightPostfix = new ArrayList<Token>();
  }  // Evalutor()
  
  public void Evalute() throws Throwable {  //  id的判斷 ; 解後序==計算結果
    DelimterIt();
    InfixToPostfix();
    BooleanObj isDoubleleft = new BooleanObj( false );
    BooleanObj isDoubleright = new BooleanObj( false );
    BooleanObj divzeroerror = new BooleanObj( false );
    double resultLeft = 0;
    double resultRight = 0;
    double tolerance =  0.0001;
    if ( G.s_quit == 1 )
      return;
    if ( mHas_middle == true ) { // 沒右邊
      if ( middle.equals( ":=" ) ) { // 這代表著左式一定是ID 計算右式即可
        resultRight = CalPostfix( mrightPostfix, isDoubleright, divzeroerror );
        if ( divzeroerror.val == true ) {
          G.CYPrint( "Error\n" );  
          return;
        } // if
        
        Identifier ide = new Identifier();
        if ( isDoubleright.val == true ) {  // 右邊是有小數是double
          ide.mcheck = "DOUBLE";   
          System.out.printf( "> %.3f%n", resultRight );
        } // if
        
        else {
          ide.mcheck = "INT";
          System.out.printf( "> %.0f%n", resultRight );
        } // else
        
        ide.mtype = G.IDENT;
        ide.mvalue = mleft.get( 0 ).mvalue ; // 也就是ident
        ide.mnumvalue = resultRight;
        G.UpdateIDInTable( ide );  
      } // if
      
      else { // 要做布林運算
        resultLeft = CalPostfix( mleftPostfix, isDoubleleft, divzeroerror );
        resultRight = CalPostfix( mrightPostfix, isDoubleright, divzeroerror );
        if ( divzeroerror.val == true ) {
          G.CYPrint( "Error\n" );  
          return;
        } // if  
        
        double diff = 0;
        double absdiff = 0;
        if ( middle.equals( "=" ) ) {  // = 或 >= 或 > 或 <= 或 <> 或<
          diff = resultLeft - resultRight; 
          absdiff = diff;
          if ( diff < 0 ) // 取絕對值
            absdiff = -diff;
          if ( absdiff < tolerance  ) // 這代表著在差值內
            System.out.println( "> " + true );      
          else 
            System.out.println( "> " + false );        
        } // if
        
        else if ( middle.equals( ">=" ) ) {
          diff = resultLeft - resultRight; 
          absdiff = diff;
          if ( diff < 0 ) // 取絕對值
            absdiff = -diff;
          if ( diff > 0 || absdiff < tolerance  ) 
            System.out.println( "> " + true );      
          else 
            System.out.println( "> " + false );    
        } // else if
        
        else if ( middle.equals( ">" ) ) {
          diff = resultLeft - resultRight;
          absdiff = diff;
          if ( diff > tolerance ) // 可能需要改這行
            System.out.println( "> " + true );    
          else 
            System.out.println( "> " + false );      
        } // else if
        
        else if ( middle.equals( "<" ) ) {
          diff = resultRight - resultLeft;
          if ( diff > tolerance ) // 可能需要改這行
            System.out.println( "> " + true );    
          else 
            System.out.println( "> " + false );      
        } // else if
        
        else if ( middle.equals( "<=" ) ) {
          diff = resultRight - resultLeft;
          absdiff = diff;
          if ( diff < 0 ) // 取絕對值
            absdiff = -diff;
          if ( diff > 0 || absdiff < tolerance  ) 
            System.out.println( "> " + true );      
          else 
            System.out.println( "> " + false );    
        } // else if
        
        else if ( middle.equals( "<>" ) ) {
          diff = resultLeft - resultRight; 
          absdiff = diff;
          if ( diff < 0 ) // 取絕對值
            absdiff = -diff;
          if ( absdiff < tolerance  ) // 這代表著在差值內
            System.out.println( "> " + false );      
          else 
            System.out.println( "> " + true );    
        } // else if 
      } // else   
    } // if
    
    else {
      resultLeft = CalPostfix( mleftPostfix, isDoubleleft, divzeroerror );
      if ( divzeroerror.val == true ) {
        G.CYPrint( "Error\n" );  
        return;
      } // if
      
      else if ( isDoubleleft.val == true )
        System.out.printf( "> %.3f%n", resultLeft );      
      else // int的狀況
        System.out.printf( "> %.0f%n", resultLeft );  
    } // else
    
    
  } // Evalute() 
  
  public void DelimterIt() throws Throwable { // 分割式子
    int i = 0, go_right = 0;
    Token t;
    if ( mExpressionTable.get( 0 ).mvalue.equals( "quit" ) ) {
      G.s_quit = 1;
      return;
    } // if
    
    mExpressionTable.remove( mExpressionTable.size() - 1 ); // 去除分號 
    for ( i = 0 ; i < mExpressionTable.size() ; i++ ) { // 把式子分成 兩段 <左邊> <布林符號> <右邊>
      t = mExpressionTable.get( i );  
      if ( t.mtype == G.DS || t.mtype == G.BE ) { // 布林符號
        middle = t.mvalue;
        go_right = 1;
        mHas_middle = true; 
      } // if
        
      else {
        if ( go_right == 0 ) // 左式
          mleft.add( t );
        else // 右式
          mright.add( t );
      } // else  
    } // for
    
    
    if ( mleft.size() >= 2 ) { // 左邊 第0個是+-後面必定是num
      if ( mleft.get( 0 ).mvalue.equals( "+" ) )   // 語法正確下可以是[sign] Num
        mleft.remove( 0 );                         // 把其整合成一個 ( 正的不用更改前者)
      else if (  mleft.get( 0 ).mtype == G.SIGN  ) {
        t = mleft.get( 1 );        // *****之後有錯的話，先轉數字在inverse
        t.mvalue = "-" + t.mvalue ; // 幫數字前面補- ，使其變成負號，數字
        mleft.set( 1, t );
        mleft.remove( 0 );
      } // else if
      
      for ( i = 0 ; mleft.size() - i > 3 ; i++  ) {
        if (  mleft.get( i ).mtype != G.NUM && mleft.get( i ).mtype != G.IDENT 
             && mleft.get( i ).mtype != G.RP ) {
          if (  mleft.get( i + 1 ).mvalue.equals( "+" )  ) {
            if ( mleft.get( i + 2 ).mtype == G.NUM ) { // 可整合
              mleft.remove( i+1 );
              i = -1;
            } // if
          } // if
          
          else if ( mleft.get( i + 1 ).mtype == G.SIGN ) { // +號跟-號
            if ( mleft.get( i + 2 ).mtype == G.NUM ) { 
              t = mleft.get( i + 2 );        // *****之後有錯的話，先轉數字在inverse
              t.mvalue = "-" + t.mvalue ; // 幫數字前面補- ，使其變成負號，數字
              mleft.set( i + 2, t );
              mleft.remove( i+1 );
              i = -1;    
            } // if  
          } // else if
        } // if        
      } // for    
    } // if

    
    if ( mright.size() >= 2 ) { // 左邊 第0個是+-後面必定是num
      if ( mright.get( 0 ).mvalue.equals( "+" ) )   // 語法正確下可以是[sign] Num
        mright.remove( 0 );                         // 把其整合成一個 ( 正的不用更改前者)
      else if (  mright.get( 0 ).mtype == G.SIGN  ) {
        t = mright.get( 1 );        // *****之後有錯的話，先轉數字在inverse
        t.mvalue = "-" + t.mvalue ; // 幫數字前面補- ，使其變成負號，數字
        mright.set( 1, t );
        mright.remove( 0 );
      } // else if
      
      for ( i = 0 ; mright.size() - i > 3 ; i++  ) {
        if (  mright.get( i ).mtype != G.NUM && mright.get( i ).mtype != G.IDENT 
             && mright.get( i ).mtype != G.RP ) {
          if (  mright.get( i + 1 ).mvalue.equals( "+" ) ) {
            if ( mright.get( i + 2 ).mtype == G.NUM ) { // 可整合
              mright.remove( i+1 );
              i = -1;
            } // if
          } // if
          
          else if ( mright.get( i + 1 ).mtype == G.SIGN ) { // +號跟-號
            if ( mright.get( i + 2 ).mtype == G.NUM ) { 
              t = mright.get( i + 2 );        // *****之後有錯的話，先轉數字在inverse
              t.mvalue = "-" + t.mvalue ; // 幫數字前面補- ，使其變成負號，數字
              mright.set( i + 2, t );
              mright.remove( i+1 );
              i = -1;    
            } // if  
          } // else if
        } // if        
      } // for    
    } // if
    
  } // DelimterIt() 
  
  
  public void InfixToPostfix() throws Throwable {
    Stack<Token> st = new Stack<Token>();
    Token t;
    int i = 0;
    for ( i = 0 ; i < mleft.size() ; i++ ) {
      t =  mleft.get( i );
      if ( t.mtype == G.NUM ||  t.mtype == G.IDENT ) // 數字
        mleftPostfix.add( t );
      else if ( t.mtype == G.LP ) // (
        st.push( t );
      else if ( t.mtype == G.RP ) { // )
        while ( !st.empty() && st.peek().mtype != G.LP ) { // 右括號就把東溪放出
          mleftPostfix.add( st.pop() );  
        } // while
          
        if ( !st.empty() ) // 放出左括號
          st.pop(); 
      } // else if
      
      else if ( t.mtype == G.SIGN || t.mtype == G.DIV || t.mtype == G.STAR ) { // 運算子
        while ( !st.empty() && Priority( t.mvalue ) <= Priority( st.peek().mvalue ) ) {
          mleftPostfix.add( st.pop() );
        } // while
        
        st.push( t );
      } // else if
    } // for
    
    while ( !st.empty() ) {
      mleftPostfix.add( st.pop() );
    } // while
    
    if ( mHas_middle == true ) {
      for ( i = 0 ; i < mright.size() ; i++ ) {
        t =  mright.get( i );
        if ( t.mtype == G.NUM ||  t.mtype == G.IDENT ) // 數字
          mrightPostfix.add( t );
        else if ( t.mtype == G.LP ) // (
          st.push( t );
        else if ( t.mtype == G.RP ) { // )
          while ( !st.empty() && st.peek().mtype != G.LP ) { // 右括號就把東溪放出
            mrightPostfix.add( st.pop() );  
          } // while
            
          if ( !st.empty() ) // 放出右括號
            st.pop();
        } // else if
        
        else if ( t.mtype == G.SIGN || t.mtype == G.DIV || t.mtype == G.STAR ) { // 運算子
          while ( !st.empty() && Priority( t.mvalue ) <= Priority( st.peek().mvalue ) ) {
            mrightPostfix.add( st.pop() );
          } // while
          
          st.push( t );
        } // else if
      } // for  
      
      while ( !st.empty() ) {
        mrightPostfix.add( st.pop() );
      } // while
    } // if 
    
  } // InfixToPostfix()
  
  public double CalPostfix( ArrayList<Token> al, BooleanObj IsSmallDigit, 
                            BooleanObj IsDivZero ) throws Throwable {
    double result = 0;
    IsSmallDigit.val = false;  
    Token t;
    Identifier ide;
    Stack<DoubleObj> st = new Stack<DoubleObj>();
    
    if ( al.size() == 1 ) {
      t = al.get( 0 );    
      if ( t.mtype == G.NUM ) {
        if ( t.mvalue.contains( "." ) ) // 
          IsSmallDigit.val = true;
        DoubleObj d = new DoubleObj( Double.parseDouble( t.mvalue ) );
        return d.val;
      } // if
      
      else if ( t.mtype == G.IDENT ) {
        ide = G.GetIDInTable( t.mvalue );
        if ( ide.mcheck.equals( "DOUBLE" ) ) // 
          IsSmallDigit.val = true;
        DoubleObj de = new DoubleObj( ide.mnumvalue );
        return de.val;
      } // else if
    } // if
    

    
    for ( int i = 0; i < al.size() ; i++ ) { // cal postfix
      t = al.get( i );
      if ( t.mtype == G.NUM ) {
        if ( t.mvalue.contains( "." ) ) // 
          IsSmallDigit.val = true;
        DoubleObj d = new DoubleObj( Double.parseDouble( t.mvalue ) );
        st.push( d );
      } // if
      
      else if ( t.mtype == G.IDENT ) {
        ide = G.GetIDInTable( t.mvalue );
        if ( ide.mcheck.equals( "DOUBLE" ) ) // 
          IsSmallDigit.val = true;
        DoubleObj de = new DoubleObj( ide.mnumvalue );
        st.push( de );
      } // else if
      
      else if ( t.mtype == G.SIGN || t.mtype == G.STAR || t.mtype == G.DIV ) {
        DoubleObj op2 = null;
        DoubleObj op1 = null;
        op2 = st.pop();
        op1 = st.pop();
        double e  = 0;
        DoubleObj res = null;
        if ( t.mvalue.equals( "+" ) ) { // 加減好像不該合再一起的
          e = op1.val + op2.val;
          res = new DoubleObj( e );
          st.push( res );
        } // if
        
        else if ( t.mvalue.equals( "-" ) ) {
          e = op1.val - op2.val;
          res = new DoubleObj( e );
          st.push( res );
        } // else if
        
        else if ( t.mtype == G.STAR ) { // *號
          e = op1.val * op2.val;
          res = new DoubleObj( e );
          st.push( res );
        } // else if
        
        else if ( t.mtype == G.DIV ) {
          if ( op2.val == 0 ) {
            IsDivZero.val = true; // 除於0錯誤
            return 0;
          } // if
          
          e = op1.val / op2.val;
          res = new DoubleObj( e );
          st.push( res );
        } // else if
      } // else if
    } // for 
    
    result = st.pop().val; // 最後一個pop掉的值就是ans
    return result;  
  } // CalPostfix()
  
  public void SetTable( ArrayList<Token> table ) {
    mExpressionTable = table;    
  }  // SetTable()
  
  public int Priority( String op ) {
    if ( op.equals( "+" ) || op.equals( "-" ) )
      return 1;
    else if ( op.equals( "*" ) || op.equals( "/" ) )
      return 2;
    return 0;
  } // Priority()
  
  public void ClearIt() {
    mleft.clear();
    middle = "";
    mright.clear();
    mExpressionTable.clear();
    mHas_middle = false;
    mleftPostfix.clear();
    mrightPostfix.clear();
  } // ClearIt()
} // class Evalutor







class Main {

  public static void main( String[] args ) throws Throwable {
    G.Init();
    SCANNER sc = new SCANNER();
    PARSER ps = new PARSER( sc );
    Evalutor eu = new Evalutor();
    G.s_utestnum = G.sin.ReadInt(); // 留著debug用
    G.sin.ReadChar(); // 讀掉換行
    G.CYPrint( "Program starts...\n" );
    while ( ! G.sin.AtEOF() && G.s_quit == 0 ) {
      ps.ParserIt();  
      if ( ps.GetExecuteState() == true ) { // 這代表parser到可以解構了，也代表著要清除list的14
        eu.SetTable( ps.GetTable() );
        eu.Evalute();
        ps.ClearIt();
        eu.ClearIt();
      } // if
    } // while
    
    G.CYPrint( "> Program exits..." );
    return; 
  } // main()

} // class Main


