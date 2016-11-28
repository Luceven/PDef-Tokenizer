package tokenizer;

/**

DESCRIPTION:

This class defines the structure of a Token object for production by the tokenizer and use by the parser.

STATE:

The state of an object includes a variable 'type', holding this token's TokenType value, and a variable 'name', which holds the String value of the token.  

	private TokenType type;  // type of this particular token
	private String    name;  // string of characters associated with
						     // this particular token
   
INTERFACE:

There are two components to the interface.  The following enumerated type defines the legal type names of tokens.  

	public enum TokenType { IDENT_T, TYPE_T, ASSIGN_T, COMMA_T,
	                        RCB_T, LCB_T, ERROR_T, EOT_T };

The interface of this class includes the constructor 
    
    	public Token(TokenType t, String s)

and the following methods.
		
	public TokenType getType()
		// Pre:  type has a value
		// Post: return type
		
	public String getName() 
		// Pre:  name has a value
		// Post: return name

	public String toString() 
		// Pre:  type and name have values
		// Post: return string containing character form of
		//       "type(name)"
   
HELPER METHODS:
   
There are no helper methods.

CLASS INVARIANT:  

The class has no invariant.
          
@author J. Mead -- August '09
*/

public class Token {

    // State
		
	public enum TokenType { IDENT_T, TYPE_T, ERROR_T, ASSIGN_T, 
	                        RCB_T, LCB_T, COMMA_T, EOF_T, LP_T, RP_T,
							ADD_T, SUB_T, MUL_T, DIV_T, MOD_T, INT_T, FLOAT_T, PERI_T };

	private TokenType  type;  // type of this particular token
	private String     name;  // string of characters associated with
					     	 // this particular token

    // Constructor
    
	public Token(TokenType t, String s) { type = t; name = s; }
  
    // Interface -- public methods
    
	public TokenType getType() { return type; }
		// Pre:  type has a value
		// Post: return type
	public String    getName() { return name; }
		// Pre:  name has a value
		// Post: return name

	public String toString() 
		// Pre:  type and name have values
		// Post: return string containing character form of
		//       "type(name)"
	{
		return type.toString() + "(  " + name + " )";
	}
}
