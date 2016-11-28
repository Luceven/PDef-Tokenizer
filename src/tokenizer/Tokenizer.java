package tokenizer;

import java.io.BufferedReader;
import java.io.IOException;

import debug.*;

/**

DESCRIPTION:

This class implements a tokenizer which extracts token values from the input stream, where the tokens are defined by the following regular expression:

     Regular Expression    Token.TokenType value    Terminating character
            ,                    COMMA_T               any character
            =                    ASSIGN_T                     "
            {                    LCB_T                        "
            }                    RCB_T                        "
        int | float              TYPE_T              `,={}' or whitespace
        [a-zA-Z]+                IDENT_T                      "


STATE:

The state of an object includes the variable 'inFile', which holds references the input file in the form of a BufferedReader object, the boolean flag 'echo' indicating whether the input characters are echoed to standard out, and a variable 'debug' of the class TokenizerDebug which controls display of debug information.  

There are two definitions present for convenience: the value of the constant 'eofChar' is returned by 'getChar' when the end of file is detected, and the enumerated type StateName defines names for the states in finite state machine implemented by GetNextTokens.

    private BufferedReader inFile;    // the input stream
    private boolean        echo = false;  // if true the input is echoed
    private TokenizerDebug debug;     // object which controls the display
                                      // of debugging information from calls
                                      // to debug.show
                                      
    private static final char eofChar = (char)0;
    private enum StateName { START_S, ID_S, DONE_S };

   
INTERFACE:

The interface includes a constructor

The interface of this class includes the constructor 
    
    public Tokenizer (BufferedReader in, boolean echo)
    // Post: inFile == in AND this.echo == echo

and the following method, which identifies tokens on the input stream, returning the next one each time it is called.

    public Token getNextToken()
    // Pre:  inFile has a value
    // Post: inFile has initial blanks removed as well as the characters
    //       of the next token on inFile.  The tokens are determined by the
    //       finite state machine for the following regular expression
    //       Regular Expression
    //           ,               
    //           = 
    //           {   
    //           } 
    //       int | float 
    //        [a-zA-Z]+

   
HELPER METHODS:
   
There are x helper methods:

	private char getChar()
	// Pre:  ch is the character at the head of inFile
	// Post: inFile is original inFile with ch removed AND
	//       return ch -- Except
	//       if inFile.eof is true return tab character
	//       if ch is tab or eol return blank character

    private void putBackChar(char ch)
    // Pre:  inFile has a value
    // Post: inFile is the original inFile with ch added as its first character

CLASS INVARIANT:  

The class invariant indicates the input stream pointer is at the next character following the last token identified -- that means the first character in the file if no token has as yet been read.

    Class Invariant: 
       When GetNextToken is called the inFile pointer is at either 
       at the first character of the file (if no token has been read)
       or at the first character following the last token read from
       inFile.
          
@author J. Mead -- August '09
*/


public class Tokenizer{

    // Class Invariant: 
    //    When GetNextToken is called the inFile pointer is at either 
    //    at the first character of the file (if no token has been read)
    //    or at the first character following the last token read from
    //    inFile.
          
    // State
		
    private BufferedReader inFile;    // the input stream
    private boolean        echo = false;  // if true the input is echoed
    private TokenizerDebug debug;     // object which controls the display
                                      // of debugging information from calls
                                      // to debug.show

    private static final char eofChar = (char)0;
    private enum StateName { START_S, ID_S, DONE_S, ZERO_S, INT_S, FLOAT_S, PERI_S, ERROR_S};

    
    // Constructor
    
    public Tokenizer (BufferedReader in, boolean echo)
    // Post: inFile == in AND this.echo == echo
    {
            this.inFile = in;
            this.echo = echo;
            this.debug = new TokenizerDebug();		
    }
    
    // Interface -- public methods
    
    public Token getNextToken()
    // Pre:  inFile has a value
    // Post: inFile has initial blanks removed as well as the characters
    //       of the next token on inFile.  The tokens are determined by the
    //       finite state machine for the following regular expression
    //       Regular Expression
    //           ,               
    //           = 
    //           {   
    //           } 
    //       int | float 
    //        [a-zA-Z]+
    {
        debug.show(">>> Entering getNextToken");
        StateName state = StateName.START_S;
        
        Token.TokenType type = Token.TokenType.ERROR_T;
        String    name  = "";

        while (state != StateName.DONE_S) {
            char ch = getChar();
            switch (state) {
                case START_S:
                    debug.show("\t>>> Entering state -- START_S: ", ch);
                    if (ch == ' ') {
                        state = StateName.START_S;
                    }
                    else if (ch == eofChar) {
                        type  = Token.TokenType.EOF_T;
                        state = StateName.DONE_S;
                    }
                    else if (Character.isLetter(ch)) {
                        name += ch;
                        state = StateName.ID_S;
                    }
                    else if (ch == '0') {
                        name += ch;
                        state = StateName.ZERO_S;
                    }
                    else if (Character.isDigit(ch)) {
                        name += ch;
                        state = StateName.INT_S;
                    }
                    else if (ch == '.') {
                        name += ch;
                        state = StateName.PERI_S;
                    }
                    else { //Assert: illegal character
                        name += ch;
                        type = char2Token(ch);
                        state = StateName.DONE_S;
                    }
                    debug.show("\t<<< Leaving state -- START_S: ", ch);                       
                    break;
                case ID_S:
                    debug.show("\t>>> Entering state -- ID_S: ", ch);
                    if (Character.isLetter(ch)) {
                        name += ch;
                        state = StateName.ID_S; }
                    else {
                        putBackChar(ch); // since an epsilon-transition
                        type = string2Token(name);
                        state = StateName.DONE_S;
                    }
                    debug.show("\t<<< Leaving state -- ID_S: ", ch);                       
                    break;
                case ZERO_S:
                    debug.show("\t>>> Entering state -- ZERO_S", ch);
                    if (Character.isDigit(ch)) {
                        type = Token.TokenType.ERROR_T;
                        state = StateName.ERROR_S;
                    }
                    else {
                        type = Token.TokenType.INT_T;
                        state = StateName.ZERO_S;
                    }
                    debug.show("\t<<< Leaving state -- ZERO_S", ch);
                    break;
                case INT_S:
                    debug.show("\t>>> Entering state -- INT_S", ch);
                    if (ch == '.') {
                        name += ch;
                        type = Token.TokenType.PERI_T;
                        state = StateName.PERI_S;
                    }
                    else if (Character.isDigit(ch)){
                        name += ch;
                        type = Token.TokenType.INT_T;
                        state = StateName.INT_S;
                    }
                    else {
                        putBackChar(ch);
                        type = Token.TokenType.INT_T;
                        state = StateName.DONE_S;
                    }
                    debug.show("\t<<< Leaving state -- INT_S", ch);
                    break;
                case PERI_S:
                    debug.show("\t>>> Entering state -- PERI_S", ch);
                    if (Character.isDigit(ch)) {
                        type = Token.TokenType.FLOAT_T;
                        state = StateName.FLOAT_S;
                    }
                    else if (!Character.isDigit(ch)) {
                        type = Token.TokenType.ERROR_T;
                        state = StateName.DONE_S;
                    }
                    debug.show("\t<<< Leaving state -- PERI_S", ch);
                    break;
                case FLOAT_S:
                    debug.show("\t>>> Entering state -- FLOAT_S", ch);
                    if (Character.isDigit(ch)) {
                        type = Token.TokenType.FLOAT_T;
                        state = StateName.FLOAT_S;
                    }
                    else {
                        type = Token.TokenType.ERROR_T;
                        state = StateName.DONE_S;
                    }
                    debug.show("\t<<< Leaving state -- FLOAT_S", ch);
                    break;
                case DONE_S: // Should never get here!  For completeness.
                    debug.show("\t>>> Entering state -- DONE_S: ", ch);
                    debug.show("\t<<< Leaving state -- DONE_S: ", ch);
                    break;
            }

        }

        Token token = new Token(type, name);

        debug.show("<<< Leaving getNextToken");       
        return token;
    }
    
    // Helpers -- private methods

    private Token.TokenType char2Token(char ch) {
        //Pre: inFile has a value
        //Post: The four characters: "=", "{", "}", "," will be returned
        // to its token type. Any other character
        //that is invalid will be returned to ERROR_T.
        if (ch == '=') {
            return Token.TokenType.ASSIGN_T;
        }
        else if (ch == '{') {
            return Token.TokenType.LCB_T;
        }
        else if (ch == '}') {
            return Token.TokenType.RCB_T;
        }
        else if (ch == ',') {
            return Token.TokenType.COMMA_T;
        }
        else
            return Token.TokenType.ERROR_T;
    }

//    private Token.TokenType int2Token(String str) {
//        //Pre: str is a string of alphabetic or numeric characters
//        //Post: return "INT_T" if there are integers in the inFile
//        str = str.replaceAll("[^0-9]+", " ");
//        String[] tokens = str.split("\\s+");
//        int [] ary = new int [tokens.length];
//
//        int i = 0;
//        for (String token : tokens) {
//            ary[i++] = Integer.parseInt(token);
//        }
//        if (ary.length != 0) {
//            for (i = 0; i < ary.length; i++) {
//                return Token.TokenType.INT_T;
//            }
//        }
//        else {
//            return Token.TokenType.
//        }

//        for (int i = 0; i < splited.length; i++) {
//            try {
//                result[i] = Integer.parseInt(splited);
//            } catch (NumberFormatException nfe) {};
//        }
//    }

//    private Token.TokenType float2Token(char ch) {
//
//    }

    private Token.TokenType string2Token(String str) {
        //Pre: str is a string of alphabetic characters
        //Post: return TYPE_T if str == "int" or "float"
        //otherwise return IDENT_T

        if (str.equals("int")) {
            return Token.TokenType.TYPE_T;
        }
        else if (str.equals("float")) {
            return Token.TokenType.TYPE_T;
        }
        else
            return Token.TokenType.IDENT_T;
    }
    
	private char getChar()
	// Pre:  ch is the character at the head of inFile
	// Post: inFile is original inFile with ch removed AND
	//       return ch -- Except
	//       if inFile.eof is true return tab character
	//       if ch is tab or eol return blank character
	{
		char ch;

		int v = 0;
		
		try { inFile.mark(1); v = inFile.read(); }
		catch (IOException e) { 
			System.out.println("Problem reading open input file!"); 
			System.exit(0); 
		}

		if (v == -1)
			ch = eofChar;
		else {
			ch = (char)v;
			if (echo) System.out.print(ch);
			if (ch == '\n' || ch == '\t') ch = ' ';
		}

		return ch;
	}

    private void putBackChar(char ch)
    // Pre:  inFile has a value
    // Post: inFile is the original inFile with ch added as its first character
    {
       debug.show(">>> Entering putBackChar");   
              
       try { if (ch != eofChar) { inFile.reset(); }   }
       catch(IOException e)     { System.exit(0); }

       debug.show("<<< Leaving putBackChar");                             
    }


}
