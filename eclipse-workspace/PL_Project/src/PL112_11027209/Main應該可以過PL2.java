package PL112_11027209;
import java.util.*;
// import java.io.EOFException;
import CYICE.*;

// 任務 搞定表，錯誤處理(檢查ident的地方)
// scanner的要看到底, parser和undefined的就重來

abstract class G{   
  public static int s_utestnum; // debug用
  public static int s_quit; // quit用的
  public static ICEInputStream sin = null;
  public static ArrayList<Functions> s_Fun = null;
  public static ArrayList<Identifier> s_Var = null; // 全域變數的放置處     下面的還有ListVariable(char name[])/ ListFunc
  public static ArrayList<Functions> s_SpecialFun = null; // ListAllVariables() / ListAllFunctions()
  public static ArrayList<Identifier> s_SpecialVar = null;  // Cin / Cout
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
  //  public final static int COUT = 51; // COUT
  //  public final static int CIN = 52; // CIN
  public final static int UNKNOWN = 16926; // 未知NODE
  
  public static void Init() throws Throwable {
    // sin = new ICEInputStream( );  // 這行可以改要讀什麼txtttttttttt
    sin = new ICEInputStream( "p3final.txt" );  // 這行可以改要讀什麼txtttttttttt
    s_Fun = new ArrayList<Functions>();
    s_Var = new ArrayList<Identifier>(); 
    s_SpecialFun = new ArrayList<Functions>(); 
    s_SpecialVar = new ArrayList<Identifier>();
    s_quit = 0;
    
    Identifier ide = new Identifier( 3, 0, "cin", false );
    s_SpecialVar.add( ide );
    ide = new Identifier( 3, 0, "cout", false );
    s_SpecialVar.add( ide );
    Functions fun = new Functions( "ListAllVariables", 8 ); // 8是void
    s_SpecialFun.add( fun );
    fun = new Functions( "ListAllFunctions", 8 ); // 8是void
    s_SpecialFun.add( fun );
    fun = new Functions( "ListVariable", 8 ); // 8是void
    s_SpecialFun.add( fun );
    fun = new Functions( "ListFunction", 8 ); // 8是void
    s_SpecialFun.add( fun );
    fun = new Functions( "Done", 8 ); // 8是void
    s_SpecialFun.add( fun );
    
  } // Init()
  
  public static String ChangeEnumToStr( int num ) {
    if ( num == INT )
      return "int"; 
    else if ( num == FLOAT )
      return "float";
    else if ( num == VOID )
      return "void";
    else if ( num == BOOL )
      return "bool";
    else if ( num == CHAR )
      return "char";
    else
      return "123";
  } // ChangeEnumToStr()
  
  public static boolean CheckIDInTable( String str ) throws Throwable {
    int i = 0;
    for ( i = 0 ; i < s_Fun.size() ; i++ ) {
      if ( s_Fun.get( i ).mname.equals( str ) ) // 有相同的字代表已被定義 
        return true;
    } // for
    
    for ( i = 0 ; i < s_Var.size() ; i++ ) {
      if ( s_Var.get( i ).mname.equals( str ) ) // 有相同的字代表已被定義 
        return true;
    } // for
    
    for ( i = 0 ; i < s_SpecialFun.size() ; i++ ) {
      if ( s_SpecialFun.get( i ).mname.equals( str ) ) // 有相同的字代表已被定義 
        return true;
    } // for
    
    for ( i = 0 ; i < s_SpecialVar.size() ; i++ ) {
      if ( s_SpecialVar.get( i ).mname.equals( str ) ) // 有相同的字代表已被定義 
        return true;
    } // for
    
    return false;
  } // CheckIDInTable()
  
  public static boolean CheckVarInTable( String str ) throws Throwable {
    int i = 0;    
    for ( i = 0 ; i < s_Var.size() ; i++ ) {
      if ( s_Var.get( i ).mname.equals( str ) ) // 有相同的字代表已被定義 
        return true;
    } // for
    
    for ( i = 0 ; i < s_SpecialVar.size() ; i++ ) {
      if ( s_SpecialVar.get( i ).mname.equals( str ) ) // 有相同的字代表已被定義 
        return true;
    } // for
    
    return false;
  } // CheckVarInTable()
  
  public static boolean CheckFunInTable( String str ) throws Throwable {
    int i = 0;
    for ( i = 0 ; i < s_Fun.size() ; i++ ) {
      if ( s_Fun.get( i ).mname.equals( str ) ) // 有相同的字代表已被定義 
        return true;
    } // for
    
    for ( i = 0 ; i < s_SpecialFun.size() ; i++ ) {
      if ( s_SpecialFun.get( i ).mname.equals( str ) ) // 有相同的字代表已被定義 
        return true;
    } // for
      
    return false;
  } // CheckFunInTable()
  
  
  public static void UpdateIDInTable( Identifier id ) throws Throwable {
    Identifier ide = new Identifier( id.mtype, id.marrnum, id.mname, id.mcheckarr );
    if ( CheckIDInTable( ide.mname ) == true ) {
      for ( int i = 0 ; i < s_Var.size() ; i++ ) { // 找到值並更改
        if ( ide.mname.equals( s_Var.get( i ).mname ) ) { // 找到同字串
          s_Var.set( i, ide );  
          System.out.println( "> New definition of " + ide.mname + " entered ..." );
        } // if
      } // for
    } // if
    
    else { // 只接放進去
      s_Var.add( ide );  
      System.out.println( "> Definition of " + ide.mname + " entered ..." );
    } // else
  } // UpdateIDInTable() 
  
  public static void UpdateFunInTable( Functions funs ) throws Throwable {
    ArrayList<Token> tklist = new ArrayList<Token>();
    ArrayList<Identifier> idlist = new ArrayList<Identifier>();
    Token tt;
    Identifier id;
    for ( int j = 0 ; j < funs.mtokenlist.size() ; j++ ) {
      tt = funs.mtokenlist.get( j );
      Token t = new Token( tt.mtype, tt.mvalue, tt.mline, tt.mcolumn );
      tklist.add( t );
    } // for
    
    for ( int j = 0 ; j < funs.mlocal.size() ; j++ ) {
      id = funs.mlocal.get( j );
      Identifier ide = new Identifier( id.mtype, id.marrnum, id.mname, id.mcheckarr );
      idlist.add( ide );
    } // for
   
    Functions fun = new Functions( funs.mname, funs.mtype, idlist, tklist );
    if ( CheckIDInTable( fun.mname ) == true ) {
      for ( int i = 0 ; i < s_Fun.size() ; i++ ) { // 找到值並更改
        if ( fun.mname.equals( s_Fun.get( i ).mname ) ) { // 找到同字串
          s_Fun.set( i, fun );  
          System.out.println( "> New definition of " + fun.mname + "() entered ..." ); // 可能會出錯 先留著
        } // if
      } // for
    } // if
    
    else { // 只接放進去
      
      s_Fun.add( fun );
      System.out.println( "> Definition of " + fun.mname + "() entered ..." ); // 可能會出錯 先留著
    } // else
    
  } // UpdateFunInTable() 
  
  public static Identifier GetIDInTable( String str ) throws Throwable {
    Identifier ide = null;
    int i = 0;
    for ( i = 0 ; i < s_Var.size() ; i++ ) {
      if ( s_Var.get( i ).mname.equals( str ) ) // 有相同的字代表已被定義 
        ide = s_Var.get( i );
    } // for
    
    for ( i = 0 ; i < s_SpecialVar.size() ; i++ ) {
      if ( s_SpecialVar.get( i ).mname.equals( str ) ) // 有相同的字代表已被定義 
        ide = s_SpecialVar.get( i );
    } // for
    
    return ide;
  } // GetIDInTable() 
  
  public static Functions GetFunInTable( String str ) throws Throwable {
    Functions fun = null;
    int i = 0;
    for ( i = 0 ; i < s_Fun.size() ; i++ ) {
      if ( s_Fun.get( i ).mname.equals( str ) ) // 有相同的字代表已被定義 
        fun = s_Fun.get( i );
    } // for
    
    for ( i = 0 ; i < s_SpecialFun.size() ; i++ ) {
      if ( s_SpecialFun.get( i ).mname.equals( str ) ) // 有相同的字代表已被定義 
        fun = s_SpecialFun.get( i );
    } // for
    
    return fun;
  } // GetFunInTable() 
  
  
  
  public static void CYPrint( String str ) throws Throwable {
    byte[] binary = str.getBytes( "Big5" );
    System.out.write( binary, 0, binary.length );
  } // CYPrint()
  
  public static void ErrorPrint( boolean a, boolean b, boolean c, Token t  ) {
    int line = t.mline;
    if ( line == 0 )
      line = 1;
    if ( a == true ) {
      System.out.print( "Line "+ line + " : Unrecognized token with first char : " +
                        '\'' + t.mvalue + '\'' + '\n' );
    } // if
    else if ( b == true ) {
      System.out.print( "Line "+ line + " : Unexpected token : " + '\'' + t.mvalue + '\'' + '\n' );
    } // else if

    else if ( c == true ) {
      System.out.print( "Line "+ line + " : undefined identifier : " + '\'' + t.mvalue + '\'' + '\n' );
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
  public int marrnum;
  public double mnumvalue;
  public String mname;
  public String mvalue;
  public ArrayList<String> marr;
  public boolean mcheckarr; // check int or double
  public boolean mcbref; // check is r
  public 
  Identifier() {
    this.marrnum = 0;
    this.mtype = 0;
    this.mname = new String();
    this.mvalue = new String();
    this.mcheckarr = false;
    this.mcbref = false;
    mnumvalue = 0;
  } // Identifier()
  
  Identifier( int type, int arrnum, String mname, boolean mcheckarr ) {
    this.mtype = type;
    this.mname = mname;
    this.mcheckarr = mcheckarr;
    this.marrnum = arrnum;
    this.mcbref = false;
    mnumvalue = 0;
  } // Identifier()  
} // class Identifier

class Functions {
  public String mname;
  public int mtype;
  public ArrayList<Token> mtokenlist;
  public ArrayList<Identifier> mlocal; // localIDtable
  Functions() {
    this.mname = new String();
    this.mtype = 0;
    this.mlocal = new ArrayList<Identifier>();
    this.mtokenlist = new ArrayList<Token>();
  } // Functions()
  
  Functions( String mname, int mtype ) {
    this.mname = mname;
    this.mtype = mtype;
    this.mlocal = new ArrayList<Identifier>();
    this.mtokenlist = new ArrayList<Token>();
  } // Functions()   
  
  
  Functions( String mname, int mtype, ArrayList<Identifier> mlocal, ArrayList<Token> mtokenlist  ) {
    this.mname = mname;
    this.mtype = mtype;
    this.mlocal = mlocal;
    this.mtokenlist = mtokenlist;
  } // Functions()  
  
} // class Functions



class ScanError extends Exception {
  ScanError( char c, int line ) {
    if ( line == 0 )
      line = 1;
    System.out.print( "Line "+ line + " : Unrecognized token with first char : " +
                      '\'' + c + '\'' + '\n' );  
  } // ScanError()
} // class ScanError

class EOFError extends Exception {
  EOFError() {
    
  } // EOFError()
} // class EOFError

class SCANNER {
  public LinkedHashMap<String,String> mLHM = new LinkedHashMap<String,String>();
  public ArrayList<Token> mTokenlist = new ArrayList<Token>(); // 放每個token 用完刪除remove_all
  public Token mnow_token = null;
  public int m_tokenNum = 0 ; // token
  public int ms_lineNumber = 0;
  public int ms_columnNumber = 1;
  public char m_nextchar = '\0';
  public Token FetchToken() throws Throwable {
    if ( mnow_token != null ) {
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
      throw new EOFError();
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
        return new Token( G.IDENT, str, ms_lineNumber, ms_columnNumber  );    
      else if ( str.equals( "cout" ) )
        return new Token( G.IDENT, str, ms_lineNumber, ms_columnNumber  );  
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
        throw new ScanError( '.', ms_lineNumber );
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
            throw new EOFError();
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
      
      first = m_nextchar;
      PeekNextChar();
      str = str + first;
      return new Token( G.CONSTANT, str, ms_lineNumber, ms_columnNumber  );    
    } // else if
   
    else if ( first == '\'' ) { // 這裡需要再看看
      if ( m_nextchar == '\n' )
        throw new ScanError( '\'', ms_lineNumber );
      PeekNextChar();
      str = str + first;
      if ( m_nextchar != '\'' )
        throw new ScanError( '\'', ms_lineNumber );
      PeekNextChar();
      first = m_nextchar;
      str = str + first;
      return new Token( G.CONSTANT, str, ms_lineNumber, ms_columnNumber  );    
    } // else if
    
    else
      throw new ScanError( first, ms_lineNumber );
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
    ms_columnNumber++;
  } // PeekNextChar()
  
  public char GetNextNonWhiteSpaceChar() throws Throwable {
    if ( m_nextchar == '\n' ) {
      ms_lineNumber++;
      ms_columnNumber = 1;
    } // if
    
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
      if ( m_nextchar == '\0' ) // eof ***
        throw new EOFError(); // 可能會有ˇ問題
    } // while
    
    PeekNextChar();
  } // EncounterError()
  
  
  public void Linereset() {
    ms_lineNumber = 0;
  } // Linereset()
  
  public void LineReset( int line ) { // 可能有錯***********
    ms_lineNumber = line;
  } // LineReset()
} // class SCANNER

class PARSER {
  public ArrayList<Token> mTokenList = null ;
  public ArrayList<Token> mtempList = null;
  public ArrayList<Identifier> mIdentList = null; // ide/function跑完後傳
  public SCANNER msc = null; // myscanner
  public String mstr_rs = new String();
  public int mTCt; // 主要的計數器(index)
  public int m_nowCommand; // 現在在command的第幾個
  public boolean mError1 = false; // 字元錯誤
  public boolean mError2 = false; // 語法錯誤
  public boolean mError3 = false; // 未定義錯誤
  public boolean mExecute = false; // 讀到分號或是quit的處理
  public boolean mfuncnow = false; // 現在是fun的話var是local的
  public boolean mdeclare = false; // 宣告 (指的是function裡面的內部變數)不是定義
  
  public PARSER( SCANNER sc ) {
    mTokenList = new ArrayList<Token>();
    mIdentList = new ArrayList<Identifier>();
    this.msc = sc;
    mTCt = 0; // token計數器
    m_nowCommand = 0;
    mtempList = null;
  } // PARSER()
  
  public void FetchToken() throws Throwable {
    if ( mTokenList.size() == 0 ||  mTCt == mTokenList.size() ) { // token不夠需抓
      mTokenList.add( msc.FetchToken() );    
    } // if
    
    else if ( mTCt == mTokenList.size()- 1 ) { // token不夠需抓
      mTokenList.add( msc.FetchToken() );
      mTCt++;
    } // if
    
    else if ( mTCt < mTokenList.size() ) {  // 有token就把count往前挪，不抓
      mTCt++;
    } // else if
  } // FetchToken()
  
  
  public void ParserIt()  throws Throwable {
    ArrayList<Token> tempp;
    Identifier t;
    if ( mTokenList.size() == 0 )
      FetchToken();
    if ( GoUser_Input() == true ) { 
      mExecute = true;

      if ( mTCt != mTokenList.size() - 1 ) { // 代表著有else
        ArrayList<Token> subList1 = new ArrayList<Token>(); 
        ArrayList<Token> subList2 = new ArrayList<Token>(); 
        for ( int i = 0 ; i < mTokenList.size() ; i++ ) {
          if ( i > mTCt ) 
            subList2.add( mTokenList.get( i ) );
          else
            subList1.add( mTokenList.get( i ) );
        } // for
        
        mTokenList = subList1;
        mtempList = subList2;
      } // if
      
      if ( m_nowCommand == 1 ) { // definition放在這處理
        if ( mTokenList.get( 2 ).mtype == G.LSP ) { // 小括號是FUNCTION
          tempp = mTokenList;
          Functions fun = new Functions( tempp.get( 1 ).mvalue, tempp.get( 0 ).mtype, mIdentList, tempp ); 
          G.UpdateFunInTable( fun );
        } // if
               
        else {
          int i = 0;
          for ( i = 1 ; i < mTokenList.size()-1 ; i++  ) {
            if ( mTokenList.get( i ).mtype == G.IDENT ) {
              if ( mTokenList.get( i+1 ).mtype == G.LMP ) { // 是中括號代表是array
                t = new Identifier( mTokenList.get( 0 ).mtype, 
                                    Integer.parseInt( mTokenList.get( i+2 ).mvalue ),
                                    mTokenList.get( i ).mvalue, true );
                i = i + 3; // 跑到]括號的index
              } // if
              
              else // 是逗號或者分號那就沒差了
                t = new Identifier( mTokenList.get( 0 ).mtype, 0, mTokenList.get( i ).mvalue, false );
              G.UpdateIDInTable( t );
            } // if
          } // for
        } // else  
          
      } // if
    } // if
    
    if ( ErrorDetect() == true ) {
      ErrorHandle();
      this.ClearIt(); // **
    } // if
  } // ParserIt()
  
  public void GoNextRound() throws Throwable {
    mExecute = false;
    if ( mtempList != null ) {
      Token t = mtempList.get( 0 );
      int templine = t.mline - 1;
      for ( int i = 0; i < mtempList.size() ; i++ ) { // reset line
        Token mt = mtempList.get( i );
        mt.mline = mt.mline - templine;
        mtempList.set( i, mt );
      } // for
      
      Token mt = mtempList.get( mtempList.size()-1 );
      msc.LineReset( mt.mline );
      mIdentList.clear();      
      mTokenList = mtempList;
      mtempList = null;
      mTCt = 0;
      m_nowCommand = 0;
    } // if
    
    else {
      this.ClearIt();
    } // else
  } // GoNextRound()
    
  
  public boolean IsSpaceCase() throws Throwable { 
    
    if ( 
         mTokenList.get( mTCt ).mtype == G.AND || mTokenList.get( mTCt ).mtype == G.AND  || 
         mTokenList.get( mTCt ).mtype == G.AND ) {
      return true;
    } // if
    
    else
      return false;  
  } // IsSpaceCase()
  
  
  public boolean GoUser_Input() throws Throwable {
    if ( IsType_Specifier() == true || mTokenList.get( mTCt ).mtype == G.VOID ) {
      m_nowCommand = 1;
      if ( GoDefinition() == true ) { // 上面的條件是Go Definition       
        return true;
      } // if
      
      else 
        return false;
    } // if 
    
    else if ( IsStatement() == true  ) { // 下一回合在看看
      if ( GoStatement() == true ) { // 上面的條件是Go Definition
        m_nowCommand = 2;
        return true;
      } // if
      
      else 
        return false;
    } // else if
    
    
    else {
      mError2 = true;
      return false;
    } // else
  } // GoUser_Input()
  
  public boolean GoDefinition() throws Throwable {
    if ( IsType_Specifier() == true ) {
      GoType_Specifier();
      if ( mTokenList.get( mTCt ).mtype == G.IDENT ) {
        mstr_rs = mTokenList.get( mTCt ).mvalue;
        FetchToken();
        if ( GoFunction_Definition_Or_Declarators() == true )
          return true;
        else
          return false;
      } // if
      
      else {  // 不是ident
        mError2 = true;
        return false;
      } // else
    } // if
    
    else if ( mTokenList.get( mTCt ).mtype == G.VOID ) {
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype == G.IDENT ) {
        FetchToken();
        if ( GoFunction_Definition_Without_ID() == true )
          return true;
        else
          return false;
      } // if
      
      else {  // 不是ident
        mError2 = true;
        return false;
      } // else
    } // else if
    
    else { // error
      mError2 = true;
      return false;
    } // else
  } // GoDefinition()
  
  public boolean GoType_Specifier() throws Throwable { // 這個加
    if ( IsType_Specifier() == true ) {
      FetchToken();
      return true;  
    } // if
    
    else { 
      mError2 = true;  
      return false;
    } // else
  } // GoType_Specifier()
  
  public boolean GoFunction_Definition_Or_Declarators() throws Throwable { // 
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
      mError2 = true;
      return false;
    } // else   
  } // GoFunction_Definition_Or_Declarators()
  
  public boolean GoRest_Of_Declarators() throws Throwable {
    int typetemp = mTCt-2;
    Identifier t;
    if ( mTokenList.get( mTCt ).mtype == G.LMP ) {
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype != G.CONSTANT ) { // 是數字就對不是就是error
        mError2 = true;
        return false;
      } // if
       
      if ( mdeclare == true ) { // for 區域變數
        t = new Identifier( mTokenList.get( typetemp ).mtype, 
                            Integer.parseInt( mTokenList.get( mTCt ).mvalue ),
                            mTokenList.get( mTCt-2 ).mvalue, true );
        mIdentList.add( t );
      } // if
      
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype != G.RMP ) { // 是數字就對不是就是error
        mError2 = true;
        return false;
      } // if
          
      FetchToken();
    } // if  
     
    else if ( mdeclare == true ) { // for 區域變數
      t = new Identifier( mTokenList.get( typetemp ).mtype, 0, mTokenList.get( mTCt-1 ).mvalue, false );
      mIdentList.add( t );  
    } // else if
    
    while ( mTokenList.get( mTCt ).mtype == G.CM  ) { // 可有可無
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype != G.IDENT  ) { // 是數字就對不是就是error
        mError2 = true;
        return false;
      } // if 
      

      FetchToken();
      if ( mTokenList.get( mTCt ).mtype == G.LMP ) {
        FetchToken();
        if ( mTokenList.get( mTCt ).mtype != G.CONSTANT ) { // 是數字就對不是就是error
          mError2 = true;
          return false;
        } // if
        
        if ( mdeclare == true ) { // for 區域變數
          t = new Identifier( mTokenList.get( typetemp ).mtype, 
                              Integer.parseInt( mTokenList.get( mTCt ).mvalue ),
                              mTokenList.get( mTCt-2 ).mvalue, true );
          mIdentList.add( t );
        } // if
        
        FetchToken();
        if ( mTokenList.get( mTCt ).mtype != G.RMP ) { // 是數字就對不是就是error
          mError2 = true;
          return false;
        } // if
            
        FetchToken();
      } // if
      
      else if ( mdeclare == true ) { // for 區域變數
        t = new Identifier( mTokenList.get( typetemp ).mtype, 0, mTokenList.get( mTCt-1 ).mvalue, false );
        mIdentList.add( t );  
      } // else if
    } // while
    
    if (  mTokenList.get( mTCt ).mtype == G.SC  ) {
      return true;  
    } // if

    else { // error
      mError2 = true;
      return false;
    } // else
  } // GoRest_Of_Declarators()
  
  public boolean GoFunction_Definition_Without_ID() throws Throwable {
    mfuncnow = true;    
    if ( mTokenList.get( mTCt ).mtype == G.LSP )  // 先左刮
      FetchToken();     
    else { // error
      mError2 = true;
      return false;
    } // else
    
    if ( mTokenList.get( mTCt ).mtype == G.VOID  ) { 
      FetchToken();
    } // if
    
    else if ( IsType_Specifier() == true ) { // formal_parameter_list開頭是type_specifer
      if ( GoFormal_Parameter_List() == false ) // 這樣if應該還是會跑
        return false;
    } // else if
    
    if ( mTokenList.get( mTCt ).mtype == G.RSP )  
      FetchToken();      
    else { // error
      mError2 = true;
      return false;
    } // else
     
    if ( GoCompound_Statement() == true )
      return true;
    else
      return false;
  } // GoFunction_Definition_Without_ID()
  
  public boolean GoFormal_Parameter_List() throws Throwable {
    int typetemp = mTCt;
    Identifier t;
    int have_half_and = 0;
    if ( GoType_Specifier() != true ) {
      mError2 = true;
      return false;
    } // if  
     
    if ( mTokenList.get( mTCt ).mtype == G.HALF_AND ) {
      FetchToken();
      have_half_and = 1;
    } // if
      
    if ( mTokenList.get( mTCt ).mtype != G.IDENT ) {
      mError2 = true;
      return false;
    } // if
    
    
    FetchToken();
    if ( mTokenList.get( mTCt ).mtype == G.LMP ) {
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype != G.CONSTANT ) { // 是數字就對不是就是error
        mError2 = true;
        return false;
      } // if
        
      t = new Identifier( mTokenList.get( typetemp ).mtype, 
                          Integer.parseInt( mTokenList.get( mTCt ).mvalue ),
                          mTokenList.get( mTCt-2 ).mvalue, true );
      if ( have_half_and == 1 ) { // 他有call by reference
        t.mcbref = true;  
        have_half_and = 0;
      } // if
      
      mIdentList.add( t );
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype != G.RMP ) { // 是數字就對不是就是error
        mError2 = true; 
        return false;
      } // if
          
      FetchToken();
    } // if  
    
    else { // 特別寫去定義ident
      t = new Identifier( mTokenList.get( typetemp ).mtype, 0, mTokenList.get( mTCt-1 ).mvalue, false );
      if ( have_half_and == 1 ) { // 他有call by reference
        t.mcbref = true;  
        have_half_and = 0;
      } // if
      
      mIdentList.add( t );
    } // else
    
    while ( mTokenList.get( mTCt ).mtype == G.CM  ) { // 這個while 感覺是塞上面的東西  
      FetchToken();
      typetemp = mTCt;
      if ( GoType_Specifier() != true ) {
        mError2 = true;
        return false;
      } // if  
      
      
      if ( mTokenList.get( mTCt ).mtype == G.HALF_AND ) {
        FetchToken(); 
        have_half_and = 1;
      } // if
      
      if ( mTokenList.get( mTCt ).mtype != G.IDENT ) {
        mError2 = true;
        return false;
      } // if
      
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype == G.LMP ) {
        FetchToken();
        if ( mTokenList.get( mTCt ).mtype != G.CONSTANT ) { // 是數字就對不是就是error
          mError2 = true;
          return false;
        } // if
        
        t = new Identifier( mTokenList.get( typetemp ).mtype, 
                            Integer.parseInt( mTokenList.get( mTCt ).mvalue ),
                            mTokenList.get( mTCt-2 ).mvalue, true );
        
        if ( have_half_and == 1 ) { // 他有call by reference
          t.mcbref = true;  
          have_half_and = 0;
        } // if
        
        mIdentList.add( t ); // 現在位置在constant
        
        FetchToken();
        if ( mTokenList.get( mTCt ).mtype != G.RMP ) { // 是數字就對不是就是error
          mError2 = true;
          return false;
        } // if
            
        FetchToken();
      } // if  
        
      else { // 特別寫去定義ident
        t = new Identifier( mTokenList.get( typetemp ).mtype, 0, mTokenList.get( mTCt-1 ).mvalue, false );
        if ( have_half_and == 1 ) { // 他有call by reference
          t.mcbref = true;  
          have_half_and = 0;
        } // if
        
        mIdentList.add( t );
      } // else
        
    } // while
    
    return true;
  } // GoFormal_Parameter_List()
  
  public boolean GoCompound_Statement() throws Throwable {
    int goback = mIdentList.size();
    if ( mTokenList.get( mTCt ).mtype != G.LBP ) {
      mError2 = true;
      return false;
    } // if
    
    FetchToken();
    while ( IsType_Specifier() || IsStatement() ) { // declaration是type開頭
      if ( IsType_Specifier() == true ) {
        if ( GoDeclaration() != true ) {
          return false;
        } // if
      } // if
      
      else if ( IsStatement() == true ) {
        if ( GoStatement() != true ) {
          return false;
        } // if
      } // else if
      
      FetchToken();
    } // while
    
    if ( mTokenList.get( mTCt ).mtype != G.RBP ) {
      mError2 = true;
      return false;   
    } // if
    
    else { // 最後一個是右大刮
      while ( goback < mIdentList.size() ) { // 要退回
        mIdentList.remove( mIdentList.size() - 1 );  
      } // while
     
      return true;
    } // else
  } // GoCompound_Statement()
  
  public boolean GoDeclaration() throws Throwable {
    mdeclare = true;
    if ( GoType_Specifier() == false ) {
      mError2 = true;
      return false;
    } // if
    
    
    if ( mTokenList.get( mTCt ).mtype != G.IDENT ) {
      mError2 = true;
      return false;
    } // if
    
    FetchToken();
    if ( mTokenList.get( mTCt ).mtype == G.LMP || mTokenList.get( mTCt ).mtype == G.SC || 
         mTokenList.get( mTCt ).mtype == G.CM ) {
      if ( GoRest_Of_Declarators() == true ) { // 中左刮或逗號或分號就是GoFWithID
        mdeclare = false;
        return true;
      } // if
      
      else 
        return false;
    } // if
    
    else { // error
      mError2 = true;
      return false;
    } // else      
    
  } // GoDeclaration()
  
  public boolean GoStatement() throws Throwable {
    if (  mTokenList.get( mTCt ).mtype == G.SC ) {
      return true;
    } // if
    
    else if (  IsExpression() == true ) {
      if ( GoExpression()  != true ) { // 中左刮或逗號或分號就是GoFWithID
        return false;
      } // if   
      
      if (  mTokenList.get( mTCt ).mtype == G.SC ) {
        return true;
      } // if      
      
      else {
        mError2 = true;
        return false;
      } // else
    } // else if
    
    else if (  mTokenList.get( mTCt ).mtype == G.RETURN ) {
      FetchToken();
      if (  IsExpression() == true ) {
        if ( GoExpression() == false ) { // 中左刮或逗號或分號就是GoFWithID
          return false;  
        } // if
      } // if
     
      if (  mTokenList.get( mTCt ).mtype == G.SC ) {
        return true;
      } // if
     
      else {
        mError2 = true;
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
      FetchToken();
      if (  mTokenList.get( mTCt ).mtype != G.LSP ) {
        mError2 = true;
        return false;
      } // if
      
      FetchToken();
      if (  GoExpression() == false ) {
        return false;
      } // if
           
      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mError2 = true;
        return false;
      } // if
      
      FetchToken();
      if ( GoStatement() == false ) {
        return false;
      } // if
        
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype == G.ELSE ) {
        FetchToken();
        if ( GoStatement() == false ) { // ****這邊可能有錯誤
          return false;
        } // if
      } // if
      
      else {
        mTCt = mTCt - 1;
      } // else
      
      return true;
    } // else if
    
    else if (  mTokenList.get( mTCt ).mtype == G.WHILE ) {
      FetchToken();
      if (  mTokenList.get( mTCt ).mtype != G.LSP ) {
        mError2 = true;
        return false;
      } // if
      
      FetchToken();
      if (  GoExpression() == false ) {
        return false;
      } // if
      
      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mError2 = true;
        return false;
      } // if     
      
      FetchToken();
      if ( GoStatement() == false ) { // ****這邊可能有錯誤  
        return false;
      } // if

      return true;
    } // else if
    
    else if (  mTokenList.get( mTCt ).mtype == G.DO ) {
      FetchToken();
      if (  GoStatement() == false ) {
        return false;
      } // if  
      
      if ( mTokenList.get( mTCt ).mtype != G.WHILE ) { // ****這邊可能有錯誤
        mError2 = true;
        return false;
      } // if  下面是while
             
      FetchToken();
      if (  mTokenList.get( mTCt ).mtype != G.LSP ) {
        mError2 = true;
        return false;
      } // if
      
      FetchToken();
      if (  GoExpression() == false ) {
        return false;
      } // if
   
      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mError2 = true;        
        return false;
      } // if     
      
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype != G.SC ) { // ****這邊可能有錯誤
        mError2 = true;
        return false;
      } // if
      
      else {
        return true; // 最後是sc所以正確
      } // else
    } // else if  
    
    else {
      mError2 = true;  
      return false;  
    } // else 
    
    
  } // GoStatement()
  
  public boolean GoExpression() throws Throwable {
    if ( GoBasic_Expression() == false ) {  
      return false;
      
    } // if
    
    while ( mTokenList.get( mTCt ).mtype == G.CM ) {
      FetchToken();
      if ( GoBasic_Expression() == false ) { 
        return false;
      } // if
    } // while
    
    return true;
  } // GoExpression()
  
   
  public boolean GoBasic_Expression() throws Throwable {
    
    if ( mTokenList.get( mTCt ).mtype == G.IDENT ) {
      Token idt = mTokenList.get( mTCt );
      FetchToken();
      // 看看是不是undefine應該是這邊把...      
      if ( mTokenList.get( mTCt ).mtype == G.LSP ) { // 就猜是fun 
        if ( G.CheckFunInTable( idt.mvalue ) == false && mstr_rs.equals( idt.mvalue ) == false  ) {
          mError3 = true;  // 猜fun check define
          return false;  
        } // if
      } // if
      
      else if ( G.CheckVarInTable( idt.mvalue ) == false && CheckInLocal( idt.mvalue ) == false  ) { 
        mError3 = true;  // 反之猜 var check define
        return false;
      }  // else if
      
      if ( GoRest_Of_Identifier_Started_Basic_Exp() == true )
        return true;
      else {
        return false;
      } // else
    } // if
    
    else if ( mTokenList.get( mTCt ).mtype == G.PP ||  mTokenList.get( mTCt ).mtype == G.MM  ) {
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype == G.IDENT ) {
        Token idt = mTokenList.get( mTCt );
        FetchToken();
        if ( G.CheckIDInTable( idt.mvalue ) == false && CheckInLocal( idt.mvalue ) == false  ) { 
          mError3 = true;  // check define
          return false;
        }  // if
        
        if ( GoRest_Of_PPMM_Identifier_Started_Basic_Exp() == true )
          return true;
        else {
          return false;
        } // else
      } // if
      
      else {  // 不是ide
        mError2 = true;
        return false;
      } // else     
    } // else if

    else if ( IsSign() == true ) {
      FetchToken();
      while ( IsSign() == true ) {
        FetchToken();
      } // while
      
      if ( GoSigned_Unary_Exp() == false ) {
        return false;
      } // if
      
      
      // 可能要FETCHTOKEN
      if ( GoRomce_And_Romloe() == true )
        return true;
      else {
        return false;
      } // else 
    } // else if 
    
    else if ( mTokenList.get( mTCt ).mtype == G.CONSTANT ) {
      FetchToken(); // 可能要FETCHTOKEN
      if ( GoRomce_And_Romloe() == true )
        return true;
      else {
        return false;
      } // else 
    } // else if 
    
    else if ( mTokenList.get( mTCt ).mtype == G.LSP ) {
      FetchToken();
      if (  GoExpression() != true ) {
        return false;
      } // if

      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mError2 = true;
        return false;
      } // if     
      
      FetchToken(); // 可能要FETCHTOKEN
      if ( GoRomce_And_Romloe() == true )
        return true;
      else {
        return false;
      } // else 
    } // else if 
    
    
    else {
      mError2 = true;
      return false;
    } // else    
  } // GoBasic_Expression()
  
  public boolean GoRest_Of_Identifier_Started_Basic_Exp() throws Throwable {
    if ( mTokenList.get( mTCt ).mtype == G.LSP ) {
      FetchToken(); // 可能要FETCHTOKEN 
      
      if ( IsExpression() == true ) {
        if (  GoActual_Parameter_List() != true ) {
          return false;
        } // if
      } // if
      
      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mError2 = true;
        return false;
      } // if     
      
      FetchToken(); // 可能要FETCHTOKEN
      if ( GoRomce_And_Romloe() == true )
        return true;
      else
        return false;
    } // if 
    
    
    else if (  mTokenList.get( mTCt ).mtype == G.LMP ) {
      FetchToken(); // 可能要FETCHTOKEN
      if (  GoExpression() != true ) {
        return false;
      } // if
  
      if (  mTokenList.get( mTCt ).mtype != G.RMP ) {
        mError2 = true;
        return false;
      } // if     
      
      FetchToken(); // 可能要FETCHTOKEN
    } // else if
    
    if ( IsAssignment_Operator() == true ) {
      GoAssignment_Operator();

      if ( GoBasic_Expression() == false ) {
        return false;  
      } // if 
      
      return true;
    } // if
    
    else { // 這可能會有錯****
      if ( mTokenList.get( mTCt ).mtype == G.PP || mTokenList.get( mTCt ).mtype == G.MM ) {
        FetchToken();
      } // if
      
      if ( GoRomce_And_Romloe() == true ) 
        return true;  
      else {
        return false;
      } // else
    } // else
    
    // 想一下要不要再放一個else去看case2
    
  } // GoRest_Of_Identifier_Started_Basic_Exp() 
 
  public boolean GoRest_Of_PPMM_Identifier_Started_Basic_Exp() throws Throwable {
    if (  mTokenList.get( mTCt ).mtype == G.LMP ) {
      FetchToken();
      if (  GoExpression() != true ) {
        return false;
      } // if
  
      if (  mTokenList.get( mTCt ).mtype != G.RMP ) {
        mError2 = true;
        return false;
      } // if     
      
      FetchToken();
    } // if
    
    if ( GoRomce_And_Romloe() == true )
      return true;     
    else {
      return false;    
    } // else
  } // GoRest_Of_PPMM_Identifier_Started_Basic_Exp()
  
  public boolean GoSign() throws Throwable {
    if ( IsSign() == true ) {
      FetchToken();
      return true;  
    } // if
    
    else { 
      mError2 = true;  
      return false;
    } // else
  } // GoSign()
  
  public boolean GoActual_Parameter_List() throws Throwable {
    if ( GoBasic_Expression() == false ) {
      return false;
    } // if
    
    while ( mTokenList.get( mTCt ).mtype == G.CM ) {
      FetchToken();
      if ( GoBasic_Expression() == false ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoActual_Parameter_List()
  
  public boolean GoAssignment_Operator() throws Throwable {
    if ( IsAssignment_Operator() == true ) {
      FetchToken(); // 可能要FETCHTOKEN
      return true;  
    } // if
    
    else { 
      mError2 = true;  
      return false;
    } // else
  } // GoAssignment_Operator()
  
  public boolean GoRomce_And_Romloe() throws Throwable {
    if ( GoRest_Of_Maybe_Logical_OR_Exp() == false ) {
      return false;
    } // if
    
    if ( mTokenList.get( mTCt ).mtype == G.QM ) {
      FetchToken();
      if ( GoBasic_Expression() != true ) {
        return false;
      } // if
      
      if (  mTokenList.get( mTCt ).mtype != G.CL ) {
        mError2 = true;
        return false;  
      } // if
      
      FetchToken();
      if ( GoBasic_Expression() != true ) {
        return false;
      } // if
    } // if
    
    return true;
  } // GoRomce_And_Romloe()
  
  public boolean GoRest_Of_Maybe_Logical_OR_Exp() throws Throwable {
    if ( GoRest_Of_Maybe_Logical_AND_Exp() == false ) {
      return false;
    } // if
  
    while ( mTokenList.get( mTCt ).mtype == G.OR ) {
      FetchToken();
      if ( GoMaybe_Logical_AND_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Logical_OR_Exp()
  

  public boolean GoMaybe_Logical_AND_Exp() throws Throwable {
    if ( GoMaybe_Bit_OR_Exp() == false ) {
      return false;
    } // if

    while ( mTokenList.get( mTCt ).mtype == G.AND ) {
      FetchToken();
      if ( GoMaybe_Bit_OR_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true; 
  } // GoMaybe_Logical_AND_Exp()
  
  public boolean GoRest_Of_Maybe_Logical_AND_Exp() throws Throwable {

    if ( GoRest_Of_Maybe_Bit_OR_Exp() == false ) {
      return false;
    } // if
    
    while ( mTokenList.get( mTCt ).mtype == G.AND ) {
      FetchToken();
      if ( GoMaybe_Bit_OR_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Logical_AND_Exp()
  
  public boolean GoMaybe_Bit_OR_Exp() throws Throwable {
    if ( GoMaybe_Bit_Ex_OR_Exp() == false ) {
      return false;
    } // if
    
    while ( mTokenList.get( mTCt ).mtype == G.HALF_OR ) {
      FetchToken();
      if ( GoMaybe_Bit_Ex_OR_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoMaybe_Bit_OR_Exp()
  
  public boolean GoRest_Of_Maybe_Bit_OR_Exp() throws Throwable {
  
    if ( GoRest_Of_Maybe_Bit_Ex_OR_Exp() == false ) {
      mError2 = true;
      return false;
    } // if

    while ( mTokenList.get( mTCt ).mtype == G.HALF_OR ) {
      FetchToken();
      if ( GoMaybe_Bit_Ex_OR_Exp() != true ) {
        mError2 = true;
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Bit_OR_Exp()
  
  
  public boolean GoMaybe_Bit_Ex_OR_Exp() throws Throwable {

    if ( GoMaybe_Bit_AND_Exp() == false ) {
      return false;
    } // if

    while ( mTokenList.get( mTCt ).mtype == G.HAT ) {
      FetchToken();
      if ( GoMaybe_Bit_AND_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoMaybe_Bit_Ex_OR_Exp()
  
  public boolean GoRest_Of_Maybe_Bit_Ex_OR_Exp() throws Throwable {

    if ( GoRest_Of_Maybe_Bit_AND_Exp() == false ) {
      return false;
    } // if

    while ( mTokenList.get( mTCt ).mtype == G.HAT ) {
      FetchToken();
      if ( GoMaybe_Bit_AND_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Bit_Ex_OR_Exp()
  
  public boolean GoMaybe_Bit_AND_Exp() throws Throwable {
    if ( GoMaybe_Equality_Exp() == false ) {
      return false;
    } // if

    while ( mTokenList.get( mTCt ).mtype == G.HALF_AND ) {
      FetchToken();
      if ( GoMaybe_Equality_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoMaybe_Bit_AND_Exp()
  
  public boolean GoRest_Of_Maybe_Bit_AND_Exp() throws Throwable {
    if ( GoRest_Of_Maybe_Equality_Exp() == false ) {
      return false;
    } // if

    while ( mTokenList.get( mTCt ).mtype == G.HALF_AND ) {
      FetchToken();
      if ( GoMaybe_Equality_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Bit_AND_Exp()
  
  public boolean GoMaybe_Equality_Exp() throws Throwable {
    if ( GoMaybe_Relational_Exp() == false )  {
      return false;
    } // if

    while ( mTokenList.get( mTCt ).mtype == G.EQ || mTokenList.get( mTCt ).mtype == G.NEQ  ) {
      FetchToken();
      if ( GoMaybe_Relational_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoMaybe_Equality_Exp()
  
  public boolean GoRest_Of_Maybe_Equality_Exp() throws Throwable {
    if ( GoRest_Of_Maybe_Relational_Exp() == false ) {
      return false;
    } // if
    
    while ( mTokenList.get( mTCt ).mtype == G.EQ  || mTokenList.get( mTCt ).mtype == G.NEQ  ) {
      FetchToken();
      if ( GoMaybe_Relational_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Equality_Exp()
  
  public boolean GoMaybe_Relational_Exp() throws Throwable {
    if ( GoMaybe_Shift_Exp() == false ) {
      return false;
    } // if
    
    while (  mTokenList.get( mTCt ).mtype == G.GE  || mTokenList.get( mTCt ).mtype == G.LE ||
            mTokenList.get( mTCt ).mtype == G.GT  || mTokenList.get( mTCt ).mtype == G.LT ) {
      FetchToken();
      if ( GoMaybe_Shift_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoMaybe_Relational_Exp()
  
  public boolean GoRest_Of_Maybe_Relational_Exp() throws Throwable {
    if ( GoRest_Of_Maybe_Shift_Exp() == false ) {
      return false;
    } // if
    
    while (  mTokenList.get( mTCt ).mtype == G.GE  || mTokenList.get( mTCt ).mtype == G.LE ||
            mTokenList.get( mTCt ).mtype == G.GT  || mTokenList.get( mTCt ).mtype == G.LT ) {
      FetchToken();
      if ( GoMaybe_Shift_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Relational_Exp()
  
  public boolean GoMaybe_Shift_Exp() throws Throwable {
    if ( GoMaybe_Additive_Exp() == false ) {
      return false;
    } // if
    
    while (  mTokenList.get( mTCt ).mtype == G.LS  || mTokenList.get( mTCt ).mtype == G.RS ) {
      FetchToken();
      if ( GoMaybe_Additive_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoMaybe_Shift_Exp()
  
  public boolean GoRest_Of_Maybe_Shift_Exp() throws Throwable {
    if ( GoRest_Of_Maybe_Additive_Exp() == false ) {
      return false;
    } // if
    
    while (  mTokenList.get( mTCt ).mtype == G.LS  || mTokenList.get( mTCt ).mtype == G.RS ) {
      FetchToken();
      if ( GoMaybe_Additive_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Shift_Exp()
  
  public boolean GoMaybe_Additive_Exp() throws Throwable {
    if ( GoMaybe_Mult_Exp() == false ) {
      return false;
    } // if
    
    while ( mTokenList.get( mTCt ).mtype == G.PLUS || mTokenList.get( mTCt ).mtype == G.MINUS ) {
      FetchToken();
      if ( GoMaybe_Mult_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoMaybe_Additive_Exp()
  
  public boolean GoRest_Of_Maybe_Additive_Exp() throws Throwable {
    if ( GoRest_Of_Maybe_Mult_Exp() == false ) {
      return false;
    } // if
   
    while ( mTokenList.get( mTCt ).mtype == G.PLUS || mTokenList.get( mTCt ).mtype == G.MINUS ) {
      FetchToken();
      if ( GoMaybe_Mult_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Additive_Exp()
  
  public boolean GoMaybe_Mult_Exp() throws Throwable {
    if ( GoUnary_Exp() == false ) {
      return false;
    } // if
    
    if ( GoRest_Of_Maybe_Mult_Exp() == false ) {
      return false;
    } // if
    
    return true;
  } // GoMaybe_Mult_Exp()
  
  public boolean GoRest_Of_Maybe_Mult_Exp() throws Throwable {
    while ( mTokenList.get( mTCt ).mtype == G.STAR || mTokenList.get( mTCt ).mtype == G.DIV ||
            mTokenList.get( mTCt ).mtype == G.PERCENT ) {
      FetchToken();
      if ( GoUnary_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Mult_Exp()
  
 
  public boolean GoUnary_Exp() throws Throwable {
    if ( IsSign() == true ) {
      FetchToken();
      while ( IsSign() == true ) {
        FetchToken();
      } // while
      
      if ( GoSigned_Unary_Exp() == false ) {
        return false;    
      } // if
      
      return true;
    } // if
      
    else if ( mTokenList.get( mTCt ).mtype == G.PP ||  mTokenList.get( mTCt ).mtype == G.MM  ) {
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype == G.IDENT ) {
        Token idt = mTokenList.get( mTCt );
        FetchToken();
        // 看看是不是undefine應該是這邊把...     
        if ( G.CheckVarInTable( idt.mvalue ) == false && CheckInLocal( idt.mvalue ) == false  ) { 
          mError3 = true;  // 反之猜 var check define
          return false;
        }  // if
        

        if (  mTokenList.get( mTCt ).mtype == G.LMP ) {
          FetchToken();
          if (  GoExpression() != true ) {
            return false;
          } // if
      
          if (  mTokenList.get( mTCt ).mtype != G.RMP ) {
            mError2 = true;
            return false;
          } // if     
          
          FetchToken();
        } // if
        
        return true;
      } // if
      
      else {
        mError2 = true;
        return false;
      } // else
    } // else if

    else {
      if ( GoUnsigned_Unary_Exp() == false ) {
        return false;    
      } // if
        
      return true;  
    } // else
  } // GoUnary_Exp()
  
  public boolean GoSigned_Unary_Exp() throws Throwable {
    if ( mTokenList.get( mTCt ).mtype == G.IDENT ) {
      Token idt = mTokenList.get( mTCt );
      FetchToken();
      // 看看是不是undefine應該是這邊把...      
      if ( mTokenList.get( mTCt ).mtype == G.LSP ) { // 就猜是fun 
        if ( G.CheckFunInTable( idt.mvalue ) == false && mstr_rs.equals( idt.mvalue ) == false  ) {
          mError3 = true;  // 猜fun check define
          return false;  
        } // if
      } // if
      
      else if ( G.CheckVarInTable( idt.mvalue ) == false && CheckInLocal( idt.mvalue ) == false  ) { 
        mError3 = true;  // 反之猜 var check define
        return false;
      }  // else if
      
        // 加了undefine
      
      if ( mTokenList.get( mTCt ).mtype == G.LSP ) {
        FetchToken(); // 可能要FETCHTOKEN 
        
        if ( IsExpression() == true ) {
          if (  GoActual_Parameter_List() != true ) {
            return false;
          } // if
        } // if
        
        if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
          mError2 = true;
          return false;
        } // if     
        
        FetchToken(); // 可能要FETCHTOKEN
      } // if
      
      else if (  mTokenList.get( mTCt ).mtype == G.LMP ) {
        FetchToken();
        if (  GoExpression() != true ) {
          return false;
        } // if
    
        if (  mTokenList.get( mTCt ).mtype != G.RMP ) {
          mError2 = true;
          return false;
        } // if     
        
        FetchToken();
      } // else if
      
      return true;
    } // if
    
    else if ( mTokenList.get( mTCt ).mtype == G.CONSTANT ) {
      FetchToken();
      return true;
    } // else if 
    
    else if ( mTokenList.get( mTCt ).mtype == G.LSP ) {
      FetchToken(); // 可能要FETCHTOKEN 
      if (  GoExpression() != true ) {
        return false;
      } // if
  
      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mError2 = true;
        return false;
      } // if     
      
      FetchToken(); // 可能要FETCHTOKEN
      return true;
    } // else if
    
    else {
      mError2 = true;
      return false;
    } // else
  } // GoSigned_Unary_Exp()
  
  public boolean GoUnsigned_Unary_Exp() throws Throwable {
    if ( mTokenList.get( mTCt ).mtype == G.IDENT ) {
      Token idt = mTokenList.get( mTCt );
      FetchToken();
      // 看看是不是undefine應該是這邊把...      
      if ( mTokenList.get( mTCt ).mtype == G.LSP ) { // 就猜是fun 
        if ( G.CheckFunInTable( idt.mvalue ) == false && mstr_rs.equals( idt.mvalue ) == false  ) {
          mError3 = true;  // 猜fun check define
          return false;  
        } // if
      } // if
      
      else if ( G.CheckVarInTable( idt.mvalue ) == false && CheckInLocal( idt.mvalue ) == false  ) { 
        mError3 = true;  // 反之猜 var check define
        return false;
      }  // else if
        // 加了undefine
      
      if ( mTokenList.get( mTCt ).mtype == G.LSP ) {
        FetchToken(); // 可能要FETCHTOKEN 
        
        if ( IsExpression() == true ) {
          if (  GoActual_Parameter_List() != true ) {
            return false;
          } // if
        } // if
    
        if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
          mError2 = true;
          return false;
        } // if     
        
        FetchToken(); // 可能要FETCHTOKEN
      } // if
      
      else {
        if (  mTokenList.get( mTCt ).mtype == G.LMP ) {
          FetchToken();
          if (  GoExpression() != true ) {
            return false;
          } // if
      
          if (  mTokenList.get( mTCt ).mtype != G.RMP ) {
            mError2 = true;
            return false;
          } // if     
          
          FetchToken();
        } // if
        
        if (  mTokenList.get( mTCt ).mtype == G.PP || mTokenList.get( mTCt ).mtype == G.MM )
          FetchToken();
      } // else
      
      return true;
    } // if
    
    else if ( mTokenList.get( mTCt ).mtype == G.CONSTANT ) {
      FetchToken();
      return true;
    } // else if 
    
    else if ( mTokenList.get( mTCt ).mtype == G.LSP ) {
      FetchToken(); // 可能要FETCHTOKEN 
      if (  GoExpression() != true ) {
        return false;
      } // if
  
      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mError2 = true;
        return false;
      } // if     
      
      FetchToken(); // 可能要FETCHTOKEN
      return true;
    } // else if
    
    else {
      mError2 = true;
      return false;
    } // else
  } // GoUnsigned_Unary_Exp()
  
  
  public boolean IsSign() throws Throwable {
    if ( mTokenList.get( mTCt ).mtype == G.PLUS || mTokenList.get( mTCt ).mtype == G.MINUS ||
         mTokenList.get( mTCt ).mtype == G.HALF_NEQ )
      return true;
    else 
      return false;
    
  } // IsSign() 
  
  public boolean IsStatement() throws Throwable { // 看statement的前綴字
    if ( mTokenList.get( mTCt ).mtype == G.SC     || IsExpression() ||
         mTokenList.get( mTCt ).mtype == G.RETURN || IsCompound_Statement()  || 
         mTokenList.get( mTCt ).mtype == G.IF  ||  mTokenList.get( mTCt ).mtype == G.WHILE  ||  
         mTokenList.get( mTCt ).mtype == G.DO )
      return true;
    else 
      return false;
  } // IsStatement() 
  
  public boolean IsCompound_Statement() throws Throwable { // expression就是basic_expression
    if ( mTokenList.get( mTCt ).mtype == G.LBP )
      return true;
    else 
      return false;
  } // IsCompound_Statement() 
  
  public boolean IsExpression() throws Throwable { // expression就是basic_expression
    if ( mTokenList.get( mTCt ).mtype == G.IDENT    || mTokenList.get( mTCt ).mtype == G.PP ||
         mTokenList.get( mTCt ).mtype == G.MM   ||  mTokenList.get( mTCt ).mtype == G.CONSTANT  || 
         mTokenList.get( mTCt ).mtype == G.LSP || IsSign() )      
      return true;
    else 
      return false;
  } // IsExpression() 
  
  
  public boolean IsType_Specifier() throws Throwable {
    if ( mTokenList.get( mTCt ).mtype == G.INT || mTokenList.get( mTCt ).mtype == G.CHAR ||
         mTokenList.get( mTCt ).mtype == G.FLOAT || mTokenList.get( mTCt ).mtype == G.STRING  || 
         mTokenList.get( mTCt ).mtype == G.BOOL  )
      return true;
    else 
      return false;
  } // IsType_Specifier() 
  
  public boolean IsAssignment_Operator() throws Throwable {
    if ( mTokenList.get( mTCt ).mtype == G.HALF_EQ || mTokenList.get( mTCt ).mtype == G.TE ||
         mTokenList.get( mTCt ).mtype == G.DE       || mTokenList.get( mTCt ).mtype == G.RE  || 
         mTokenList.get( mTCt ).mtype == G.PE       || mTokenList.get( mTCt ).mtype == G.ME  )
      return true;
    else 
      return false;
  } // IsAssignment_Operator() 
  
  
  public void ClearIt() throws Throwable {
    mTCt = 0;
    m_nowCommand = 0;
    mTokenList.clear();  
    mError1 = false; // 字元錯誤
    mError2 = false; // 語法錯誤
    mError3 = false; // 未定義錯誤
    mExecute = false; // 讀到分號或是quit的處理
    mdeclare = false;
    mstr_rs = new String();
    mIdentList.clear();
    msc.Linereset();
  } // ClearIt()
  
  public boolean CheckInLocal( String str ) throws Throwable {
    for ( int i = 0 ; i < mIdentList.size() ; i++ ) {
      if ( mIdentList.get( i ).mname.equals( str ) ) // 有相同的字代表已被定義 
        return true;
    } // for
    
    return false;
  } // CheckInLocal()
  
  public void ErrorClean() throws Throwable {
    msc.EncounterError();  
    this.ClearIt();     
    mtempList = null;
    mfuncnow = false;
  } // ErrorClean()

  
  public boolean ErrorDetect() throws Throwable {
    if ( mTokenList.size() <= 0 ) // 應該是不會有這種狀況
      return false;
    Token t = mTokenList.get( mTokenList.size() - 1 ); // 拿最後一項token = 最新讀的
    if ( t.mtype == G.UNKNOWN ) // 順便檢查Unrecognized
      mError1 = true;
    if ( mError1 || mError2 || mError3 )
      return true;
    else
      return false;
  } // ErrorDetect()
  
  public void ErrorHandle() throws Throwable {
    
    if ( mTokenList.size() <= 0 ) // 應該是不會有這種狀況
      return;
    Token t = mTokenList.get( mTokenList.size() - 1 ); // 拿最後一項token = 最新讀的
    if ( t.mtype == G.EOF ) { // 順便檢查Unrecognized
      throw new Exception();
    } // if
    
    if ( mError1 || mError2 ) {
      G.ErrorPrint( mError1, mError2, mError3, t  ) ;
      msc.EncounterError();  
      this.ClearIt();
    } // if
    
    else if ( mError3 ) { // undefine比較難處理
      Token f = null;
      Token temp = null;
      int doEncounterError = 0;
      f = mTokenList.get( mTokenList.size() - 2 ); // 拿到undefine token
      temp = mTokenList.get( mTokenList.size() - 1 ); // 拿到last token
      G.ErrorPrint( mError1, mError2, mError3, f  ) ;
      
      if ( f.mline == temp.mline ) { // 代表說同條線全部清除
        msc.EncounterError();  
        this.ClearIt();    
      } // if
      
      else { // 不同條代表要當下次的開頭
        mtempList = new ArrayList<Token>();
        mtempList.add( mTokenList.get( mTokenList.size() - 1 ) );
        mTokenList.clear();
        if ( mtempList != null ) {
          Token tt = mtempList.get( 0 );
          t.mline = 1;
          mtempList.set( 0, t );
          msc.LineReset( t.mline );
          mIdentList.clear();      
          mTokenList = mtempList;
          mtempList = null;
          mTCt = 0;
          m_nowCommand = 0;
          mError1 = false; // 字元錯誤
          mError2 = false; // 語法錯誤
          mError3 = false; // 未定義錯誤
          mExecute = false; // 讀到分號或是quit的處理
          mdeclare = false;
          mstr_rs = new String();
        } // if  
      } // else
    } // else if 
  } // ErrorHandle()
  
  
  
  public boolean GetExecuteState() {
    return mExecute;  
  } // GetExecuteState()
  
  public ArrayList<Token> GetTable() {
    return mTokenList;  
  } // GetTable()
  
  public int GetCommand() {
    return m_nowCommand;  
  } // GetCommand()
} // class PARSER

class Evalutor {
  public ArrayList<Token> mTL = null ; // token_list
  public int mcheck; // 1是definition, 2是statement
  public int mTCt = 0; // 計數器index
  public Evalutor() {   
    mTL = new ArrayList<Token>();
  }  // Evalutor()
  
 
  public void Evalute() throws Throwable  {
    GoListFunction();
    String str = new String();
    if ( mTL.get( mTCt ).mvalue.equals( "ListVariable" ) )
      GoListVariable();
    else if ( mTL.get( mTCt ).mvalue.equals( "ListFunction" ) )
      GoListFunction();
    else if ( mTL.get( mTCt ).mvalue.equals( "ListAllFunctions" ) )
      GoListAllFunctions();
    else if ( mTL.get( mTCt ).mvalue.equals( "ListAllVariables" ) )
      GoListAllVariables();
    else if ( mTL.get( mTCt ).mvalue.equals( "Done" ) ) {
      if ( GoDone() == true )
        return;
    } // if
    
    else { // goReparser
      
    } // else
    
    if ( mcheck == 2 ) // 只有一個分號的狀況
      System.out.println( "Statement executed ..." );
    mTCt = 0;
  } // Evalute()
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  public boolean GoDone() {
    if ( mTL.get( mTCt ).mvalue.equals( "Done" ) ) {
      mTCt++;
      if ( mTL.get( mTCt ).mtype == G.LSP && mTL.get( mTCt+1 ).mtype == G.RSP 
           && mTL.get( mTCt+2 ).mtype == G.SC ) { // (
        G.s_quit = 1;
        return true;
      } // if
    } // if
    
    return false;
  }  // GoDone()
 
  public void GoListVariable() throws Throwable {
    Identifier ide;
    if ( mTL.get( mTCt ).mvalue.equals( "ListVariable" ) ) {
      mTCt++;
      if ( mTL.get( mTCt ).mtype == G.LSP && mTL.get( mTCt+1 ).mtype == G.CONSTANT &&  
           mTL.get( mTCt+1 ).mvalue.charAt( 0 ) == '"' && mTL.get( mTCt+2 ).mtype == G.RSP && 
           mTL.get( mTCt+3 ).mtype == G.SC ) { // (
        String str = mTL.get( mTCt+1 ).mvalue ;
        str = str.substring( 1, str.length()-1 ); // 拿到變數的名字
        ide = G.GetIDInTable( str );  
        if ( ide.mcheckarr == false ) // 不是array
          System.out.println( G.ChangeEnumToStr( ide.mtype ) + " " + ide.mname + " ;"  );
        else
          System.out.println( G.ChangeEnumToStr( ide.mtype ) + " " + ide.mname + 
                              "[ " + ide.marrnum + "] ;" );
      } // if
    } // if
  }  // GoListVariable()
  
  public void GoListAllVariables() throws Throwable {
    boolean has_swap = false;
    boolean break_loop = false;
    for ( int i = 0 ; i < G.s_Var.size() && break_loop == false ; i++ ) {
      has_swap = false;
      for ( int j = 0 ; j < G.s_Var.size() - i - 1 ; j++ ) {
        Identifier temp1 = G.s_Var.get( j );
        Identifier temp2 = G.s_Var.get( j+1 );
        if ( temp1.mname.compareTo( temp2.mname ) > 0  ) {
          has_swap = true;
          G.s_Var.set( j, temp2 ) ; // do swap
          G.s_Var.set( j+1, temp1 ); // do swap
        } // if
      } // for
      
      if ( has_swap == false )
        break_loop = true ;
    } // for
    
    for ( int i = 0 ; i < G.s_Var.size() ; i++ ) {
      System.out.println( G.s_Var.get( i ).mname );  
    } // for
  } // GoListAllVariables()
  
  public void GoListFunction() throws Throwable { // 等等繼續改
    Functions fun;
    
    int get_dwie = 0;
    boolean check_noLBP = false; // if()或是while() i =0這種
    boolean check_dwie = false; // do while if else  
    boolean can_peek = false;
    boolean linestart = true;
    ArrayList <Integer> checkdowhile = new ArrayList<Integer>();
    
    int spacenum = 0; // 要空白的個數
    int tpeek = 0;
    int donothing = 0;
    if ( mTL.get( mTCt ).mvalue.equals( "ListFunction" ) ) {
      mTCt++;
      if ( mTL.get( mTCt ).mtype == G.LSP && mTL.get( mTCt+1 ).mtype == G.CONSTANT &&  
           mTL.get( mTCt+1 ).mvalue.charAt( 0 ) == '"' && mTL.get( mTCt+2 ).mtype == G.RSP && 
           mTL.get( mTCt+3 ).mtype == G.SC ) { // (
        String str = mTL.get( mTCt+1 ).mvalue ;
        str = str.substring( 1, str.length()-1 ); // 拿到變數的名字
        fun = G.GetFunInTable( str ); // print東西地獄
        mTL = fun.mtokenlist;
        for ( int i = 0 ; i < fun.mtokenlist.size() ; i++ ) {
          if ( linestart == true ) {
            for ( int k = 0 ; k < spacenum ; k++ )
              System.out.print( " " );  
            linestart = false;
          } // if
          
          str = mTL.get( i ).mvalue; 
          if ( i+1 != mTL.size()  ) { // 不是最後一向
            tpeek = mTL.get( i+1 ).mtype; // peek下一項
            can_peek = true;
          } // if
          
          else 
            can_peek = false;
          System.out.print( str );  
          if ( str.equals( ";" ) ) {
            System.out.print( "\n" );
            if ( check_noLBP == true ) { // 沒有大括號就不是dowhil
              spacenum = spacenum - 2;
              check_noLBP = false; 
            } // if
            
            if ( can_peek == true && tpeek == G.RBP )
              spacenum = spacenum - 2 ;
            linestart = true;     
          } // if
          
          else if ( str.equals( "{" ) ) { // 遇到大刮都換行
            System.out.print( "\n" );
            linestart = true;  
            spacenum = spacenum + 2;
          } // else if
          
          else if ( str.equals( "}" ) ) {
            System.out.print( "\n" );
            if ( can_peek == true && tpeek == G.RBP )
              spacenum = spacenum - 2 ;
            linestart = true;
          } // else if

          
          else if ( can_peek == true ) {
            if ( str.equals( "if" ) || str.equals( "else" ) || str.equals( "while" ) ) {
              check_dwie = true;
              get_dwie =  mTL.get( i ).mtype;
            } // if
            
            if ( str.equals( "do" ) ) {
              Integer intoj = new Integer( spacenum );
              checkdowhile.add( intoj ); // 想法是遇到do就把spacenum放入
              check_dwie = true;           // 當遇到while兩者spacenum相同
              get_dwie = mTL.get( i ).mtype;
            } // if
            
            if ( str.equals( "(" ) && tpeek == G.RSP ) // 不要print空白
              donothing = 0;
            else if ( ( str.equals( "]" ) || str.equals( ")" ) || mTL.get( i ).mtype == G.IDENT )
                      && tpeek == G.CM ) 
              donothing = 0;
            else if ( ( str.equals( "++" ) || str.equals( "--" ) ) &&  tpeek == G.IDENT  ) // 不要print空白      
              donothing = 0; // ++ -- [
            else if ( mTL.get( i ).mtype == G.IDENT &&  ( tpeek == G.PP || tpeek == G.MM 
                                                          ||  tpeek == G.LMP || tpeek == G.LSP ) )     
              donothing = 0;  
            else if ( mTL.get( i ).mtype == G.RSP && check_dwie == true   ) {
              Integer intoj = new Integer( spacenum );
              if ( get_dwie == G.WHILE && checkdowhile.indexOf( intoj ) != -1 ) { 
                checkdowhile.remove( checkdowhile.indexOf( intoj ) );  // 先檢查特例 do while(的while)
                System.out.print( " " );
              } // if
              
              else if ( tpeek != G.LBP ) { // 沒有大刮是一行的
                check_noLBP = true;
                spacenum = spacenum + 2;
                linestart = true;
                System.out.print( "\n" );
              } // if
              
              else
                System.out.print( " " ); // 平常例子
            } // else if
            

            
            else    
              System.out.print( " " );
          } // if
        } // for
      } // if
    } // if
  }  // GoListFunction()
  
  public void GoListAllFunctions() throws Throwable {
    boolean has_swap = false;
    boolean break_loop = false;
    for ( int i = 0 ; i < G.s_Fun.size() && break_loop == false ; i++ ) {
      has_swap = false;
      for ( int j = 0 ; j < G.s_Fun.size() - i - 1 ; j++ ) {
        Functions temp1 = G.s_Fun.get( j );
        Functions temp2 = G.s_Fun.get( j+1 );
        if ( temp1.mname.compareTo( temp2.mname ) > 0  ) {
          has_swap = true;
          G.s_Fun.set( j, temp2 ); // do swap
          G.s_Fun.set( j+1, temp1 ); // do swap
        } // if
      } // for
      
      if ( has_swap == false )
        break_loop = true ;
    } // for
    
    for ( int i = 0 ; i < G.s_Fun.size() ; i++ ) {
      System.out.println( G.s_Fun.get( i ).mname + "()" );  
    } // for
  } // GoListAllFunctions()
  
  public void SetTable( ArrayList<Token> table, int a ) {
    mTL = table;    
    mcheck = a;
  }  // SetTable()
 
  
 
  
  
  public void ClearIt() {
    mTL.clear();
    mcheck = 0;
  } // ClearIt()
} // class Evalutor()






class Main {

  public static void main( String[] args ) throws Throwable {
    G.Init();
    SCANNER sc = new SCANNER();
    PARSER ps = new PARSER( sc );
    Evalutor eu = new Evalutor();
    G.s_utestnum = G.sin.ReadInt(); // 留著debug用
    G.sin.ReadChar(); // 讀掉換行
    G.CYPrint( "Our-C running...\n" );
    while ( ! G.sin.AtEOF() && G.s_quit == 0 ) {
      try {
        ps.ParserIt();  
        int c = ps.GetCommand();
        if ( ps.GetExecuteState() == true ) {
          eu.SetTable( ps.GetTable(), c ); // 數字是跑statement or def or 其他之類d
          eu.Evalute();
          ps.GoNextRound();  
        } // if
      } // try
      
      catch ( ScanError e ) {
        ps.ErrorClean();
      } // catch
      
      catch ( EOFError e ) {
        G.s_quit = 1;
        G.CYPrint( "EOF\n" );  
      } // catch 
    } // while
    
    
    
    
    G.CYPrint( "> Our-C exited ..." );
    return; 
  } // main()

} // class Main














/*
 public boolean GoType_Specifier() throws Throwable { // 這個加
    if ( IsType_Specifier() == true ) {
      FetchToken();
      return true;  
    } // if
    
    else { 
      mError2 = true;  
      return false;
    } // else
  } // GoType_Specifier()
  
  public boolean GoFunction_Definition_Or_Declarators() throws Throwable { // 
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
      mError2 = true;
      return false;
    } // else   
  } // GoFunction_Definition_Or_Declarators()
  
  public boolean GoRest_Of_Declarators() throws Throwable {
    int typetemp = mTCt-2;
    Identifier t;
    if ( mTokenList.get( mTCt ).mtype == G.LMP ) {
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype != G.CONSTANT ) { // 是數字就對不是就是error
        mError2 = true;
        return false;
      } // if
       
      if ( mdeclare == true ) { // for 區域變數
        t = new Identifier( mTokenList.get( typetemp ).mtype, 
                            Integer.parseInt( mTokenList.get( mTCt ).mvalue ),
                            mTokenList.get( mTCt-2 ).mvalue, true );
        mIdentList.add( t );
      } // if
      
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype != G.RMP ) { // 是數字就對不是就是error
        mError2 = true;
        return false;
      } // if
          
      FetchToken();
    } // if  
     
    else if ( mdeclare == true ) { // for 區域變數
      t = new Identifier( mTokenList.get( typetemp ).mtype, 0, mTokenList.get( mTCt-1 ).mvalue, false );
      mIdentList.add( t );  
    } // else if
    
    while ( mTokenList.get( mTCt ).mtype == G.CM  ) { // 可有可無
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype != G.IDENT  ) { // 是數字就對不是就是error
        mError2 = true;
        return false;
      } // if 
      

      FetchToken();
      if ( mTokenList.get( mTCt ).mtype == G.LMP ) {
        FetchToken();
        if ( mTokenList.get( mTCt ).mtype != G.CONSTANT ) { // 是數字就對不是就是error
          mError2 = true;
          return false;
        } // if
        
        if ( mdeclare == true ) { // for 區域變數
          t = new Identifier( mTokenList.get( typetemp ).mtype, 
                              Integer.parseInt( mTokenList.get( mTCt ).mvalue ),
                              mTokenList.get( mTCt-2 ).mvalue, true );
          mIdentList.add( t );
        } // if
        
        FetchToken();
        if ( mTokenList.get( mTCt ).mtype != G.RMP ) { // 是數字就對不是就是error
          mError2 = true;
          return false;
        } // if
            
        FetchToken();
      } // if
      
      else if ( mdeclare == true ) { // for 區域變數
        t = new Identifier( mTokenList.get( typetemp ).mtype, 0, mTokenList.get( mTCt-1 ).mvalue, false );
        mIdentList.add( t );  
      } // else if
    } // while
    
    if (  mTokenList.get( mTCt ).mtype == G.SC  ) {
      return true;  
    } // if

    else { // error
      mError2 = true;
      return false;
    } // else
  } // GoRest_Of_Declarators()
  
  public boolean GoFunction_Definition_Without_ID() throws Throwable {
    mfuncnow = true;    
    if ( mTokenList.get( mTCt ).mtype == G.LSP )  // 先左刮
      FetchToken();     
    else { // error
      mError2 = true;
      return false;
    } // else
    
    if ( mTokenList.get( mTCt ).mtype == G.VOID  ) { 
      FetchToken();
    } // if
    
    else if ( IsType_Specifier() == true ) { // formal_parameter_list開頭是type_specifer
      if ( GoFormal_Parameter_List() == false ) // 這樣if應該還是會跑
        return false;
    } // else if
    
    if ( mTokenList.get( mTCt ).mtype == G.RSP )  
      FetchToken();      
    else { // error
      mError2 = true;
      return false;
    } // else
     
    if ( GoCompound_Statement() == true )
      return true;
    else
      return false;
  } // GoFunction_Definition_Without_ID()
  
  public boolean GoFormal_Parameter_List() throws Throwable {
    int typetemp = mTCt;
    Identifier t;
    int have_half_and = 0;
    if ( GoType_Specifier() != true ) {
      mError2 = true;
      return false;
    } // if  
     
    if ( mTokenList.get( mTCt ).mtype == G.HALF_AND ) {
      FetchToken();
      have_half_and = 1;
    } // if
      
    if ( mTokenList.get( mTCt ).mtype != G.IDENT ) {
      mError2 = true;
      return false;
    } // if
    
    
    FetchToken();
    if ( mTokenList.get( mTCt ).mtype == G.LMP ) {
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype != G.CONSTANT ) { // 是數字就對不是就是error
        mError2 = true;
        return false;
      } // if
        
      t = new Identifier( mTokenList.get( typetemp ).mtype, 
                          Integer.parseInt( mTokenList.get( mTCt ).mvalue ),
                          mTokenList.get( mTCt-2 ).mvalue, true );
      if ( have_half_and == 1 ) { // 他有call by reference
        t.mcbref = true;  
        have_half_and = 0;
      } // if
      
      mIdentList.add( t );
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype != G.RMP ) { // 是數字就對不是就是error
        mError2 = true; 
        return false;
      } // if
          
      FetchToken();
    } // if  
    
    else { // 特別寫去定義ident
      t = new Identifier( mTokenList.get( typetemp ).mtype, 0, mTokenList.get( mTCt-1 ).mvalue, false );
      if ( have_half_and == 1 ) { // 他有call by reference
        t.mcbref = true;  
        have_half_and = 0;
      } // if
      
      mIdentList.add( t );
    } // else
    
    while ( mTokenList.get( mTCt ).mtype == G.CM  ) { // 這個while 感覺是塞上面的東西  
      FetchToken();
      typetemp = mTCt;
      if ( GoType_Specifier() != true ) {
        mError2 = true;
        return false;
      } // if  
      
      
      if ( mTokenList.get( mTCt ).mtype == G.HALF_AND ) {
        FetchToken(); 
        have_half_and = 1;
      } // if
      
      if ( mTokenList.get( mTCt ).mtype != G.IDENT ) {
        mError2 = true;
        return false;
      } // if
      
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype == G.LMP ) {
        FetchToken();
        if ( mTokenList.get( mTCt ).mtype != G.CONSTANT ) { // 是數字就對不是就是error
          mError2 = true;
          return false;
        } // if
        
        t = new Identifier( mTokenList.get( typetemp ).mtype, 
                            Integer.parseInt( mTokenList.get( mTCt ).mvalue ),
                            mTokenList.get( mTCt-2 ).mvalue, true );
        
        if ( have_half_and == 1 ) { // 他有call by reference
          t.mcbref = true;  
          have_half_and = 0;
        } // if
        
        mIdentList.add( t ); // 現在位置在constant
        
        FetchToken();
        if ( mTokenList.get( mTCt ).mtype != G.RMP ) { // 是數字就對不是就是error
          mError2 = true;
          return false;
        } // if
            
        FetchToken();
      } // if  
        
      else { // 特別寫去定義ident
        t = new Identifier( mTokenList.get( typetemp ).mtype, 0, mTokenList.get( mTCt-1 ).mvalue, false );
        if ( have_half_and == 1 ) { // 他有call by reference
          t.mcbref = true;  
          have_half_and = 0;
        } // if
        
        mIdentList.add( t );
      } // else
        
    } // while
    
    return true;
  } // GoFormal_Parameter_List()
  
  public boolean GoCompound_Statement() throws Throwable {
    int goback = mIdentList.size();
    if ( mTokenList.get( mTCt ).mtype != G.LBP ) {
      mError2 = true;
      return false;
    } // if
    
    FetchToken();
    while ( IsType_Specifier() || IsStatement() ) { // declaration是type開頭
      if ( IsType_Specifier() == true ) {
        if ( GoDeclaration() != true ) {
          return false;
        } // if
      } // if
      
      else if ( IsStatement() == true ) {
        if ( GoStatement() != true ) {
          return false;
        } // if
      } // else if
      
      FetchToken();
    } // while
    
    if ( mTokenList.get( mTCt ).mtype != G.RBP ) {
      mError2 = true;
      return false;   
    } // if
    
    else { // 最後一個是右大刮
      while ( goback < mIdentList.size() ) { // 要退回
        mIdentList.remove( mIdentList.size() - 1 );  
      } // while
     
      return true;
    } // else
  } // GoCompound_Statement()
  
  public boolean GoDeclaration() throws Throwable {
    mdeclare = true;
    if ( GoType_Specifier() == false ) {
      mError2 = true;
      return false;
    } // if
    
    
    if ( mTokenList.get( mTCt ).mtype != G.IDENT ) {
      mError2 = true;
      return false;
    } // if
    
    FetchToken();
    if ( mTokenList.get( mTCt ).mtype == G.LMP || mTokenList.get( mTCt ).mtype == G.SC || 
         mTokenList.get( mTCt ).mtype == G.CM ) {
      if ( GoRest_Of_Declarators() == true ) { // 中左刮或逗號或分號就是GoFWithID
        mdeclare = false;
        return true;
      } // if
      
      else 
        return false;
    } // if
    
    else { // error
      mError2 = true;
      return false;
    } // else      
    
  } // GoDeclaration()
  
  public boolean GoStatement() throws Throwable {
    if (  mTokenList.get( mTCt ).mtype == G.SC ) {
      return true;
    } // if
    
    else if (  IsExpression() == true ) {
      if ( GoExpression()  != true ) { // 中左刮或逗號或分號就是GoFWithID
        return false;
      } // if   
      
      if (  mTokenList.get( mTCt ).mtype == G.SC ) {
        return true;
      } // if      
      
      else {
        mError2 = true;
        return false;
      } // else
    } // else if
    
    else if (  mTokenList.get( mTCt ).mtype == G.RETURN ) {
      FetchToken();
      if (  IsExpression() == true ) {
        if ( GoExpression() == false ) { // 中左刮或逗號或分號就是GoFWithID
          return false;  
        } // if
      } // if
     
      if (  mTokenList.get( mTCt ).mtype == G.SC ) {
        return true;
      } // if
     
      else {
        mError2 = true;
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
      FetchToken();
      if (  mTokenList.get( mTCt ).mtype != G.LSP ) {
        mError2 = true;
        return false;
      } // if
      
      FetchToken();
      if (  GoExpression() == false ) {
        return false;
      } // if
           
      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mError2 = true;
        return false;
      } // if
      
      FetchToken();
      if ( GoStatement() == false ) {
        return false;
      } // if
        
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype == G.ELSE ) {
        FetchToken();
        if ( GoStatement() == false ) { // ****這邊可能有錯誤
          return false;
        } // if
      } // if
      
      else {
        mTCt = mTCt - 1;
      } // else
      
      return true;
    } // else if
    
    else if (  mTokenList.get( mTCt ).mtype == G.WHILE ) {
      FetchToken();
      if (  mTokenList.get( mTCt ).mtype != G.LSP ) {
        mError2 = true;
        return false;
      } // if
      
      FetchToken();
      if (  GoExpression() == false ) {
        return false;
      } // if
      
      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mError2 = true;
        return false;
      } // if     
      
      FetchToken();
      if ( GoStatement() == false ) { // ****這邊可能有錯誤  
        return false;
      } // if

      return true;
    } // else if
    
    else if (  mTokenList.get( mTCt ).mtype == G.DO ) {
      FetchToken();
      if (  GoStatement() == false ) {
        return false;
      } // if  
      
      if ( mTokenList.get( mTCt ).mtype != G.WHILE ) { // ****這邊可能有錯誤
        mError2 = true;
        return false;
      } // if  下面是while
             
      FetchToken();
      if (  mTokenList.get( mTCt ).mtype != G.LSP ) {
        mError2 = true;
        return false;
      } // if
      
      FetchToken();
      if (  GoExpression() == false ) {
        return false;
      } // if
   
      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mError2 = true;        
        return false;
      } // if     
      
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype != G.SC ) { // ****這邊可能有錯誤
        mError2 = true;
        return false;
      } // if
      
      else {
        return true; // 最後是sc所以正確
      } // else
    } // else if  
    
    else {
      mError2 = true;  
      return false;  
    } // else 
    
    
  } // GoStatement()
  
  public boolean GoExpression() throws Throwable {
    if ( GoBasic_Expression() == false ) {  
      return false;
      
    } // if
    
    while ( mTokenList.get( mTCt ).mtype == G.CM ) {
      FetchToken();
      if ( GoBasic_Expression() == false ) { 
        return false;
      } // if
    } // while
    
    return true;
  } // GoExpression()
  
   
  public boolean GoBasic_Expression() throws Throwable {
    
    if ( mTokenList.get( mTCt ).mtype == G.IDENT ) {
      Token idt = mTokenList.get( mTCt );
      FetchToken();
      // 看看是不是undefine應該是這邊把...      
      if ( mTokenList.get( mTCt ).mtype == G.LSP ) { // 就猜是fun 
        if ( G.CheckFunInTable( idt.mvalue ) == false && mstr_rs.equals( idt.mvalue ) == false  ) {
          mError3 = true;  // 猜fun check define
          return false;  
        } // if
      } // if
      
      else if ( G.CheckVarInTable( idt.mvalue ) == false && CheckInLocal( idt.mvalue ) == false  ) { 
        mError3 = true;  // 反之猜 var check define
        return false;
      }  // else if
      
      if ( GoRest_Of_Identifier_Started_Basic_Exp() == true )
        return true;
      else {
        return false;
      } // else
    } // if
    
    else if ( mTokenList.get( mTCt ).mtype == G.PP ||  mTokenList.get( mTCt ).mtype == G.MM  ) {
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype == G.IDENT ) {
        Token idt = mTokenList.get( mTCt );
        FetchToken();
        if ( G.CheckIDInTable( idt.mvalue ) == false && CheckInLocal( idt.mvalue ) == false  ) { 
          mError3 = true;  // check define
          return false;
        }  // if
        
        if ( GoRest_Of_PPMM_Identifier_Started_Basic_Exp() == true )
          return true;
        else {
          return false;
        } // else
      } // if
      
      else {  // 不是ide
        mError2 = true;
        return false;
      } // else     
    } // else if

    else if ( IsSign() == true ) {
      FetchToken();
      while ( IsSign() == true ) {
        FetchToken();
      } // while
      
      if ( GoSigned_Unary_Exp() == false ) {
        return false;
      } // if
      
      
      // 可能要FETCHTOKEN
      if ( GoRomce_And_Romloe() == true )
        return true;
      else {
        return false;
      } // else 
    } // else if 
    
    else if ( mTokenList.get( mTCt ).mtype == G.CONSTANT ) {
      FetchToken(); // 可能要FETCHTOKEN
      if ( GoRomce_And_Romloe() == true )
        return true;
      else {
        return false;
      } // else 
    } // else if 
    
    else if ( mTokenList.get( mTCt ).mtype == G.LSP ) {
      FetchToken();
      if (  GoExpression() != true ) {
        return false;
      } // if

      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mError2 = true;
        return false;
      } // if     
      
      FetchToken(); // 可能要FETCHTOKEN
      if ( GoRomce_And_Romloe() == true )
        return true;
      else {
        return false;
      } // else 
    } // else if 
    
    
    else {
      mError2 = true;
      return false;
    } // else    
  } // GoBasic_Expression()
  
  public boolean GoRest_Of_Identifier_Started_Basic_Exp() throws Throwable {
    if ( mTokenList.get( mTCt ).mtype == G.LSP ) {
      FetchToken(); // 可能要FETCHTOKEN 
      
      if ( IsExpression() == true ) {
        if (  GoActual_Parameter_List() != true ) {
          return false;
        } // if
      } // if
      
      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mError2 = true;
        return false;
      } // if     
      
      FetchToken(); // 可能要FETCHTOKEN
      if ( GoRomce_And_Romloe() == true )
        return true;
      else
        return false;
    } // if 
    
    
    else if (  mTokenList.get( mTCt ).mtype == G.LMP ) {
      FetchToken(); // 可能要FETCHTOKEN
      if (  GoExpression() != true ) {
        return false;
      } // if
  
      if (  mTokenList.get( mTCt ).mtype != G.RMP ) {
        mError2 = true;
        return false;
      } // if     
      
      FetchToken(); // 可能要FETCHTOKEN
    } // else if
    
    if ( IsAssignment_Operator() == true ) {
      GoAssignment_Operator();

      if ( GoBasic_Expression() == false ) {
        return false;  
      } // if 
      
      return true;
    } // if
    
    else { // 這可能會有錯****
      if ( mTokenList.get( mTCt ).mtype == G.PP || mTokenList.get( mTCt ).mtype == G.MM ) {
        FetchToken();
      } // if
      
      if ( GoRomce_And_Romloe() == true ) 
        return true;  
      else {
        return false;
      } // else
    } // else
    
    // 想一下要不要再放一個else去看case2
    
  } // GoRest_Of_Identifier_Started_Basic_Exp() 
 
  public boolean GoRest_Of_PPMM_Identifier_Started_Basic_Exp() throws Throwable {
    if (  mTokenList.get( mTCt ).mtype == G.LMP ) {
      FetchToken();
      if (  GoExpression() != true ) {
        return false;
      } // if
  
      if (  mTokenList.get( mTCt ).mtype != G.RMP ) {
        mError2 = true;
        return false;
      } // if     
      
      FetchToken();
    } // if
    
    if ( GoRomce_And_Romloe() == true )
      return true;     
    else {
      return false;    
    } // else
  } // GoRest_Of_PPMM_Identifier_Started_Basic_Exp()
  
  public boolean GoSign() throws Throwable {
    if ( IsSign() == true ) {
      FetchToken();
      return true;  
    } // if
    
    else { 
      mError2 = true;  
      return false;
    } // else
  } // GoSign()
  
  public boolean GoActual_Parameter_List() throws Throwable {
    if ( GoBasic_Expression() == false ) {
      return false;
    } // if
    
    while ( mTokenList.get( mTCt ).mtype == G.CM ) {
      FetchToken();
      if ( GoBasic_Expression() == false ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoActual_Parameter_List()
  
  public boolean GoAssignment_Operator() throws Throwable {
    if ( IsAssignment_Operator() == true ) {
      FetchToken(); // 可能要FETCHTOKEN
      return true;  
    } // if
    
    else { 
      mError2 = true;  
      return false;
    } // else
  } // GoAssignment_Operator()
  
  public boolean GoRomce_And_Romloe() throws Throwable {
    if ( GoRest_Of_Maybe_Logical_OR_Exp() == false ) {
      return false;
    } // if
    
    if ( mTokenList.get( mTCt ).mtype == G.QM ) {
      FetchToken();
      if ( GoBasic_Expression() != true ) {
        return false;
      } // if
      
      if (  mTokenList.get( mTCt ).mtype != G.CL ) {
        mError2 = true;
        return false;  
      } // if
      
      FetchToken();
      if ( GoBasic_Expression() != true ) {
        return false;
      } // if
    } // if
    
    return true;
  } // GoRomce_And_Romloe()
  
  public boolean GoRest_Of_Maybe_Logical_OR_Exp() throws Throwable {
    if ( GoRest_Of_Maybe_Logical_AND_Exp() == false ) {
      return false;
    } // if
  
    while ( mTokenList.get( mTCt ).mtype == G.OR ) {
      FetchToken();
      if ( GoMaybe_Logical_AND_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Logical_OR_Exp()
  

  public boolean GoMaybe_Logical_AND_Exp() throws Throwable {
    if ( GoMaybe_Bit_OR_Exp() == false ) {
      return false;
    } // if

    while ( mTokenList.get( mTCt ).mtype == G.AND ) {
      FetchToken();
      if ( GoMaybe_Bit_OR_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true; 
  } // GoMaybe_Logical_AND_Exp()
  
  public boolean GoRest_Of_Maybe_Logical_AND_Exp() throws Throwable {

    if ( GoRest_Of_Maybe_Bit_OR_Exp() == false ) {
      return false;
    } // if
    
    while ( mTokenList.get( mTCt ).mtype == G.AND ) {
      FetchToken();
      if ( GoMaybe_Bit_OR_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Logical_AND_Exp()
  
  public boolean GoMaybe_Bit_OR_Exp() throws Throwable {
    if ( GoMaybe_Bit_Ex_OR_Exp() == false ) {
      return false;
    } // if
    
    while ( mTokenList.get( mTCt ).mtype == G.HALF_OR ) {
      FetchToken();
      if ( GoMaybe_Bit_Ex_OR_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoMaybe_Bit_OR_Exp()
  
  public boolean GoRest_Of_Maybe_Bit_OR_Exp() throws Throwable {
  
    if ( GoRest_Of_Maybe_Bit_Ex_OR_Exp() == false ) {
      mError2 = true;
      return false;
    } // if

    while ( mTokenList.get( mTCt ).mtype == G.HALF_OR ) {
      FetchToken();
      if ( GoMaybe_Bit_Ex_OR_Exp() != true ) {
        mError2 = true;
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Bit_OR_Exp()
  
  
  public boolean GoMaybe_Bit_Ex_OR_Exp() throws Throwable {

    if ( GoMaybe_Bit_AND_Exp() == false ) {
      return false;
    } // if

    while ( mTokenList.get( mTCt ).mtype == G.HAT ) {
      FetchToken();
      if ( GoMaybe_Bit_AND_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoMaybe_Bit_Ex_OR_Exp()
  
  public boolean GoRest_Of_Maybe_Bit_Ex_OR_Exp() throws Throwable {

    if ( GoRest_Of_Maybe_Bit_AND_Exp() == false ) {
      return false;
    } // if

    while ( mTokenList.get( mTCt ).mtype == G.HAT ) {
      FetchToken();
      if ( GoMaybe_Bit_AND_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Bit_Ex_OR_Exp()
  
  public boolean GoMaybe_Bit_AND_Exp() throws Throwable {
    if ( GoMaybe_Equality_Exp() == false ) {
      return false;
    } // if

    while ( mTokenList.get( mTCt ).mtype == G.HALF_AND ) {
      FetchToken();
      if ( GoMaybe_Equality_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoMaybe_Bit_AND_Exp()
  
  public boolean GoRest_Of_Maybe_Bit_AND_Exp() throws Throwable {
    if ( GoRest_Of_Maybe_Equality_Exp() == false ) {
      return false;
    } // if

    while ( mTokenList.get( mTCt ).mtype == G.HALF_AND ) {
      FetchToken();
      if ( GoMaybe_Equality_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Bit_AND_Exp()
  
  public boolean GoMaybe_Equality_Exp() throws Throwable {
    if ( GoMaybe_Relational_Exp() == false )  {
      return false;
    } // if

    while ( mTokenList.get( mTCt ).mtype == G.EQ || mTokenList.get( mTCt ).mtype == G.NEQ  ) {
      FetchToken();
      if ( GoMaybe_Relational_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoMaybe_Equality_Exp()
  
  public boolean GoRest_Of_Maybe_Equality_Exp() throws Throwable {
    if ( GoRest_Of_Maybe_Relational_Exp() == false ) {
      return false;
    } // if
    
    while ( mTokenList.get( mTCt ).mtype == G.EQ  || mTokenList.get( mTCt ).mtype == G.NEQ  ) {
      FetchToken();
      if ( GoMaybe_Relational_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Equality_Exp()
  
  public boolean GoMaybe_Relational_Exp() throws Throwable {
    if ( GoMaybe_Shift_Exp() == false ) {
      return false;
    } // if
    
    while (  mTokenList.get( mTCt ).mtype == G.GE  || mTokenList.get( mTCt ).mtype == G.LE ||
            mTokenList.get( mTCt ).mtype == G.GT  || mTokenList.get( mTCt ).mtype == G.LT ) {
      FetchToken();
      if ( GoMaybe_Shift_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoMaybe_Relational_Exp()
  
  public boolean GoRest_Of_Maybe_Relational_Exp() throws Throwable {
    if ( GoRest_Of_Maybe_Shift_Exp() == false ) {
      return false;
    } // if
    
    while (  mTokenList.get( mTCt ).mtype == G.GE  || mTokenList.get( mTCt ).mtype == G.LE ||
            mTokenList.get( mTCt ).mtype == G.GT  || mTokenList.get( mTCt ).mtype == G.LT ) {
      FetchToken();
      if ( GoMaybe_Shift_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Relational_Exp()
  
  public boolean GoMaybe_Shift_Exp() throws Throwable {
    if ( GoMaybe_Additive_Exp() == false ) {
      return false;
    } // if
    
    while (  mTokenList.get( mTCt ).mtype == G.LS  || mTokenList.get( mTCt ).mtype == G.RS ) {
      FetchToken();
      if ( GoMaybe_Additive_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoMaybe_Shift_Exp()
  
  public boolean GoRest_Of_Maybe_Shift_Exp() throws Throwable {
    if ( GoRest_Of_Maybe_Additive_Exp() == false ) {
      return false;
    } // if
    
    while (  mTokenList.get( mTCt ).mtype == G.LS  || mTokenList.get( mTCt ).mtype == G.RS ) {
      FetchToken();
      if ( GoMaybe_Additive_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Shift_Exp()
  
  public boolean GoMaybe_Additive_Exp() throws Throwable {
    if ( GoMaybe_Mult_Exp() == false ) {
      return false;
    } // if
    
    while ( mTokenList.get( mTCt ).mtype == G.PLUS || mTokenList.get( mTCt ).mtype == G.MINUS ) {
      FetchToken();
      if ( GoMaybe_Mult_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoMaybe_Additive_Exp()
  
  public boolean GoRest_Of_Maybe_Additive_Exp() throws Throwable {
    if ( GoRest_Of_Maybe_Mult_Exp() == false ) {
      return false;
    } // if
   
    while ( mTokenList.get( mTCt ).mtype == G.PLUS || mTokenList.get( mTCt ).mtype == G.MINUS ) {
      FetchToken();
      if ( GoMaybe_Mult_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Additive_Exp()
  
  public boolean GoMaybe_Mult_Exp() throws Throwable {
    if ( GoUnary_Exp() == false ) {
      return false;
    } // if
    
    if ( GoRest_Of_Maybe_Mult_Exp() == false ) {
      return false;
    } // if
    
    return true;
  } // GoMaybe_Mult_Exp()
  
  public boolean GoRest_Of_Maybe_Mult_Exp() throws Throwable {
    while ( mTokenList.get( mTCt ).mtype == G.STAR || mTokenList.get( mTCt ).mtype == G.DIV ||
            mTokenList.get( mTCt ).mtype == G.PERCENT ) {
      FetchToken();
      if ( GoUnary_Exp() != true ) {
        return false;
      } // if
    } // while
    
    return true;
  } // GoRest_Of_Maybe_Mult_Exp()
  
 
  public boolean GoUnary_Exp() throws Throwable {
    if ( IsSign() == true ) {
      FetchToken();
      while ( IsSign() == true ) {
        FetchToken();
      } // while
      
      if ( GoSigned_Unary_Exp() == false ) {
        return false;    
      } // if
      
      return true;
    } // if
      
    else if ( mTokenList.get( mTCt ).mtype == G.PP ||  mTokenList.get( mTCt ).mtype == G.MM  ) {
      FetchToken();
      if ( mTokenList.get( mTCt ).mtype == G.IDENT ) {
        Token idt = mTokenList.get( mTCt );
        FetchToken();
        // 看看是不是undefine應該是這邊把...     
        if ( G.CheckVarInTable( idt.mvalue ) == false && CheckInLocal( idt.mvalue ) == false  ) { 
          mError3 = true;  // 反之猜 var check define
          return false;
        }  // if
        

        if (  mTokenList.get( mTCt ).mtype == G.LMP ) {
          FetchToken();
          if (  GoExpression() != true ) {
            return false;
          } // if
      
          if (  mTokenList.get( mTCt ).mtype != G.RMP ) {
            mError2 = true;
            return false;
          } // if     
          
          FetchToken();
        } // if
        
        return true;
      } // if
      
      else {
        mError2 = true;
        return false;
      } // else
    } // else if

    else {
      if ( GoUnsigned_Unary_Exp() == false ) {
        return false;    
      } // if
        
      return true;  
    } // else
  } // GoUnary_Exp()
  
  public boolean GoSigned_Unary_Exp() throws Throwable {
    if ( mTokenList.get( mTCt ).mtype == G.IDENT ) {
      Token idt = mTokenList.get( mTCt );
      FetchToken();
      // 看看是不是undefine應該是這邊把...      
      if ( mTokenList.get( mTCt ).mtype == G.LSP ) { // 就猜是fun 
        if ( G.CheckFunInTable( idt.mvalue ) == false && mstr_rs.equals( idt.mvalue ) == false  ) {
          mError3 = true;  // 猜fun check define
          return false;  
        } // if
      } // if
      
      else if ( G.CheckVarInTable( idt.mvalue ) == false && CheckInLocal( idt.mvalue ) == false  ) { 
        mError3 = true;  // 反之猜 var check define
        return false;
      }  // else if
      
        // 加了undefine
      
      if ( mTokenList.get( mTCt ).mtype == G.LSP ) {
        FetchToken(); // 可能要FETCHTOKEN 
        
        if ( IsExpression() == true ) {
          if (  GoActual_Parameter_List() != true ) {
            return false;
          } // if
        } // if
        
        if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
          mError2 = true;
          return false;
        } // if     
        
        FetchToken(); // 可能要FETCHTOKEN
      } // if
      
      else if (  mTokenList.get( mTCt ).mtype == G.LMP ) {
        FetchToken();
        if (  GoExpression() != true ) {
          return false;
        } // if
    
        if (  mTokenList.get( mTCt ).mtype != G.RMP ) {
          mError2 = true;
          return false;
        } // if     
        
        FetchToken();
      } // else if
      
      return true;
    } // if
    
    else if ( mTokenList.get( mTCt ).mtype == G.CONSTANT ) {
      FetchToken();
      return true;
    } // else if 
    
    else if ( mTokenList.get( mTCt ).mtype == G.LSP ) {
      FetchToken(); // 可能要FETCHTOKEN 
      if (  GoExpression() != true ) {
        return false;
      } // if
  
      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mError2 = true;
        return false;
      } // if     
      
      FetchToken(); // 可能要FETCHTOKEN
      return true;
    } // else if
    
    else {
      mError2 = true;
      return false;
    } // else
  } // GoSigned_Unary_Exp()
  
  public boolean GoUnsigned_Unary_Exp() throws Throwable {
    if ( mTokenList.get( mTCt ).mtype == G.IDENT ) {
      Token idt = mTokenList.get( mTCt );
      FetchToken();
      // 看看是不是undefine應該是這邊把...      
      if ( mTokenList.get( mTCt ).mtype == G.LSP ) { // 就猜是fun 
        if ( G.CheckFunInTable( idt.mvalue ) == false && mstr_rs.equals( idt.mvalue ) == false  ) {
          mError3 = true;  // 猜fun check define
          return false;  
        } // if
      } // if
      
      else if ( G.CheckVarInTable( idt.mvalue ) == false && CheckInLocal( idt.mvalue ) == false  ) { 
        mError3 = true;  // 反之猜 var check define
        return false;
      }  // else if
        // 加了undefine
      
      if ( mTokenList.get( mTCt ).mtype == G.LSP ) {
        FetchToken(); // 可能要FETCHTOKEN 
        
        if ( IsExpression() == true ) {
          if (  GoActual_Parameter_List() != true ) {
            return false;
          } // if
        } // if
    
        if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
          mError2 = true;
          return false;
        } // if     
        
        FetchToken(); // 可能要FETCHTOKEN
      } // if
      
      else {
        if (  mTokenList.get( mTCt ).mtype == G.LMP ) {
          FetchToken();
          if (  GoExpression() != true ) {
            return false;
          } // if
      
          if (  mTokenList.get( mTCt ).mtype != G.RMP ) {
            mError2 = true;
            return false;
          } // if     
          
          FetchToken();
        } // if
        
        if (  mTokenList.get( mTCt ).mtype == G.PP || mTokenList.get( mTCt ).mtype == G.MM )
          FetchToken();
      } // else
      
      return true;
    } // if
    
    else if ( mTokenList.get( mTCt ).mtype == G.CONSTANT ) {
      FetchToken();
      return true;
    } // else if 
    
    else if ( mTokenList.get( mTCt ).mtype == G.LSP ) {
      FetchToken(); // 可能要FETCHTOKEN 
      if (  GoExpression() != true ) {
        return false;
      } // if
  
      if (  mTokenList.get( mTCt ).mtype != G.RSP ) {
        mError2 = true;
        return false;
      } // if     
      
      FetchToken(); // 可能要FETCHTOKEN
      return true;
    } // else if
    
    else {
      mError2 = true;
      return false;
    } // else
  } // GoUnsigned_Unary_Exp()
  
  
  public boolean IsSign() throws Throwable {
    if ( mTokenList.get( mTCt ).mtype == G.PLUS || mTokenList.get( mTCt ).mtype == G.MINUS ||
         mTokenList.get( mTCt ).mtype == G.HALF_NEQ )
      return true;
    else 
      return false;
    
  } // IsSign() 
  
  public boolean IsStatement() throws Throwable { // 看statement的前綴字
    if ( mTokenList.get( mTCt ).mtype == G.SC     || IsExpression() ||
         mTokenList.get( mTCt ).mtype == G.RETURN || IsCompound_Statement()  || 
         mTokenList.get( mTCt ).mtype == G.IF  ||  mTokenList.get( mTCt ).mtype == G.WHILE  ||  
         mTokenList.get( mTCt ).mtype == G.DO )
      return true;
    else 
      return false;
  } // IsStatement() 
  
  public boolean IsCompound_Statement() throws Throwable { // expression就是basic_expression
    if ( mTokenList.get( mTCt ).mtype == G.LBP )
      return true;
    else 
      return false;
  } // IsCompound_Statement() 
  
  public boolean IsExpression() throws Throwable { // expression就是basic_expression
    if ( mTokenList.get( mTCt ).mtype == G.IDENT    || mTokenList.get( mTCt ).mtype == G.PP ||
         mTokenList.get( mTCt ).mtype == G.MM   ||  mTokenList.get( mTCt ).mtype == G.CONSTANT  || 
         mTokenList.get( mTCt ).mtype == G.LSP || IsSign() )      
      return true;
    else 
      return false;
  } // IsExpression() 
  
  
  public boolean IsType_Specifier() throws Throwable {
    if ( mTokenList.get( mTCt ).mtype == G.INT || mTokenList.get( mTCt ).mtype == G.CHAR ||
         mTokenList.get( mTCt ).mtype == G.FLOAT || mTokenList.get( mTCt ).mtype == G.STRING  || 
         mTokenList.get( mTCt ).mtype == G.BOOL  )
      return true;
    else 
      return false;
  } // IsType_Specifier() 
  
  public boolean IsAssignment_Operator() throws Throwable {
    if ( mTokenList.get( mTCt ).mtype == G.HALF_EQ || mTokenList.get( mTCt ).mtype == G.TE ||
         mTokenList.get( mTCt ).mtype == G.DE       || mTokenList.get( mTCt ).mtype == G.RE  || 
         mTokenList.get( mTCt ).mtype == G.PE       || mTokenList.get( mTCt ).mtype == G.ME  )
      return true;
    else 
      return false;
  } // IsAssignment_Operator() 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 */


