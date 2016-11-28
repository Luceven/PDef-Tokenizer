import java.io.*;
import tokenizer.*;
import debug.*;


/**
This is the driver class which tests the Token and Tokenizer classes. The method main implements command line entry of both an input file name and a string of (character) arguments each of which signals a particular debugging capability:
    char     capability
    'e'      echo to the screen the input data as it is read 
    't'      turn on debugging code in the Tokenizer object
    
If the first argument is not a valid input file name then the program will terminate with an error message indicating the problem.

The output from main will be a sequence of lines, each containing the name of a token (from Token.TokenType) along with the string value of the token, the line number where it occurred, and the position on the line where its first character appeared.

@author J. Mead -- July '08
*/



public class PDef {

    public static void main(String[] args) {
    
        // local variables
                    
        BufferedReader in = null;   // the input character stream
        boolean echo      = false;  // if true the input is echoed --
                                    // value is 'false' by default
                                    // and set to true if 'e' appears
                                    // as a command line argument
        
        int numArgs = args.length;  // number of command line arguments
                                    // (doen't include command name)

        System.out.println("Yunjia Zeng");
        if (numArgs < 1) {
            // There must be a file name!
            System.out.println("Not enough arguments!\n");
            System.exit(0);
        }
        else  { 
            // args[0] is the data file name
            try { 
               in = new BufferedReader(new FileReader(args[0])); 
               if (numArgs > 1)  // args[1] holds debug flags
                  for (int i = 0; i != args[1].length(); i++) {
                      switch (args[1].charAt(i)) {
                      case 'e': echo    = true; break;
                      case 't': Debug.registerFlag ('t'); break;
                      }
                      // ignore invalid flag names
                  }
            }
            catch (FileNotFoundException e) {
               System.out.printf("Could not open file `%s'\n", args[0]);
               System.exit(0);
            }
        }

        Tokenizer tins = new Tokenizer(in, echo);
        
        System.out.println("\nTokens appearing in input file `" + args[0] + "'");
        System.out.println();

        Token t = tins.getNextToken();  // get the first token
        while (t.getType() != Token.TokenType.EOF_T) {
           // there's another interesting token -- print it
           System.out.println(t);
           t = tins.getNextToken();  // make progress toward termination
        }
        
        System.out.println(t);
        System.out.println( "\nAll done!\n");

    }

}
