package edu.cmu.cs.lti.relationFilter;



import java.util.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

import edu.stanford.nlp.util.StringUtils;

import java.io.*;
import java.net.*;
import java.util.Properties;



/*****************************************************************************
 * A server for Stanford Dependency Parser.
 * Implemented for Transtac Nov 2008 evaluation
 * 
 * @version $Id: DEPServer.java $
 * @author
 *      Nguyen Bach<BR>
 *      nbach@cs.cmu.edu 
 *
*****************************************************************************/



public class DEPServer {

  //// Constants
//////////////////////////////////////////////////////////////

  /**
   * Debugging toggle.
   */
  private boolean DEBUG = true;

  /**
   * The listener socket of this server.
   */
  private final ServerSocket LISTENER;

  /**
   * The classifier that does the actual tagging.
   */
  //  private CMMClassifier NER = CMMClassifier.getClassifier("/home.local/tkbuser/ner-2004-06-16/ner.eng2004.gz");
  //private final AbstractSequenceClassifier NER ;
    
  //private LexicalizedParser DEP = new LexicalizedParser("englishFactored.ser.gz");
  
  private LexicalizedParser DEP;


  //// Constructors
///////////////////////////////////////////////////////////

  /**
   * Creates a new named entity recognizer server on the specified port.
   * @param port the port this DEPServer listens on.
   */
  public DEPServer(int port, LexicalizedParser  asc) throws IOException {
    DEP = asc;
    LISTENER = new ServerSocket(port);
  }

  
  //// Constructors
///////////////////////////////////////////////////////////

/**
* Creates a new named entity recognizer server on the specified port.
* @param port the port this DEPServer listens on.
*/
public DEPServer(int port, String model) throws IOException {
	
	DEP = new LexicalizedParser (model);
		
	//DEP.setOptionFlags(new String[]{"-maxLength", "100", "-retainTmpSubcategories"});	
	
	DEP.setOptionFlags(new String[]{"-maxLength", "120", "-outputFormatOptions", "basicDependencies"});	

	
	LISTENER = new ServerSocket(port);
	
	System.out.println("[DEPServer] is ready at port " + port);
}
  
  
  //// Public Methods
/////////////////////////////////////////////////////////

  /**
   * Runs this named entity recognizer server.
   */
  public void run() {
    Socket client = null;
    while (true) {
        try {
          client = LISTENER.accept();
          if (DEBUG) {
            System.out.println("[DEPServer] Accepted request from " + client.getInetAddress().getHostName());
          }
          new Session(client);
        }
        catch (Exception e1) {
          System.err.println("[DEPServer] couldn't accept");
          e1.printStackTrace(System.err);
          try {
            client.close();
          }
          catch (Exception e2) {
            System.err.println("[DEPServer] couldn't close client");
            e2.printStackTrace(System.err);
          }
        }
    }
  }


  //// Inner Classes
//////////////////////////////////////////////////////////

  /**
   * A single user session, accepting one request, processing it, and sending 
   * back the results.
   */
  private class Session extends Thread {

  //// Instance Fields
////////////////////////////////////////////////////////      

    /**
     * The socket to the client.
     */
    private Socket client;

    /**
     * The input stream from the client.
     */
    private BufferedReader in;

    /**
     * The output stream to the client.
     */
    private PrintWriter out;


    //// Constructors
///////////////////////////////////////////////////////////

    private Session(Socket socket) throws IOException {
      client = socket;
      in = new BufferedReader(new InputStreamReader(client.getInputStream()));
      out = new PrintWriter(client.getOutputStream());
      start();
    }


    //// Public Methods
/////////////////////////////////////////////////////////

    /**
     * Runs this session by reading a string, tagging it, and writing back the result.
     */
    public void run() {
      if (DEBUG) {System.out.println("[DEPServer] Created new session");}      
      
      String input = null;
      try {
        input = in.readLine();
        if (DEBUG) {
          System.out.println("[DEPServer] Receiving: \"" + input + "\"");
        }
      }
      catch (IOException e) {
        System.err.println("[DEPServer] Session: couldn't read input");
        e.printStackTrace(System.err);
      }
      catch (NullPointerException npe) {
        System.err.println("[DEPServer] Session: connection closed by peer");
        npe.printStackTrace(System.err);
      }
      if (! (input == null)) {
		String[] sent = input.split(" ");
		Tree parse = (Tree) DEP.apply(Arrays.asList(sent));
		
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		Collection tdl = gs.typedDependenciesCollapsed();
		
		String compact = tdl.toString();
		
		//typed dependencies
		//TreePrint tp = new TreePrint("typedDependenciesCollapsed");
		
		TreePrint tp0   = new TreePrint("wordsAndTags");						
		StringWriter wt = new StringWriter();
		PrintWriter pwt = new PrintWriter(wt);
		tp0.printTree(parse, pwt);
		String wordtag = wt.toString();		
		if (DEBUG) { System.out.println("[DEPSever] Sending: \n" + wordtag ); }

		TreePrint tp    = new TreePrint("dependencies");						
		StringWriter wr = new StringWriter();
		PrintWriter pw  = new PrintWriter(wr);
		tp.printTree(parse, pw);
		String dep = wr.toString();		
		if (DEBUG) { System.out.println("[DEPSever] Sending: \n" + dep ); }
		
		TreePrint tp1 = new TreePrint("penn");
    	StringWriter wr1 = new StringWriter();
		PrintWriter pw1  = new PrintWriter(wr1);
		tp1.printTree(parse, pw1);
		String syntac = wr1.toString();		
		if (DEBUG) { System.out.println("[DEPSever] Sending: \n" + syntac); };

		TreePrint tp2 = new TreePrint("typedDependencies", "basicDependencies", tlp);
    	StringWriter wr2 = new StringWriter();
		PrintWriter pw2  = new PrintWriter(wr2);
		tp2.printTree(parse, pw2);
		String typedep = wr2.toString();		
		if (DEBUG) { System.out.println("[DEPSever] Sending: \n" + typedep); };

        String output = wordtag + syntac + dep + typedep;
        
		if (DEBUG) { System.out.println("[DEPSever] Sending: \n" + output); };

		out.print(output);		
        out.flush();
        wr.flush();
		wt.flush();
		wr1.flush();
		wr2.flush();
	  }
      close();
    }

    /**
     * Terminates this session gracefully.
     */
    private void close() {
      try {
        in.close();
        out.close();
        client.close();
      }
      catch (Exception e) {
        System.err.println("[DEPServer] Session: can't close session");
        e.printStackTrace(System.err);
      }
    }

  }


  private static final String USAGE = "Transtac Nov 2008 Eval: English Dependency Parser Server" +
  		"\nUsage: DEPServer -loadModel [path_to_your_model] portNumber" +
  		"\nExample: java -mx700m -cp 'stanford-parser.jar:' DEPServer -loadModel englishFactored.ser.gz 1234" +
  		"\nTest server by: telnet localhost 1234 ; then enter a sentence\n";

  /**
   * Starts this server on the specified port.  The classifier used can be 
   * either a default one stored in the jar file from which this code is
   * invoked or you can specify it as a filename or as another classifier
   * resource name, which must correspond to the name of a resource in the 
   * /classifiers/ directory of the jar file.
   * <p>
   * Usage: <code>java -mx700m -cp 'stanford-parser.jar:' DEPServer -loadModel portNumber</code>
   */
  public static void main (String[] args) throws Exception {
    Properties props = StringUtils.argsToProperties(args);
    String loadFile = props.getProperty("loadModel");
    
    String portStr = props.getProperty("");
    if (portStr == null || portStr.equals("")) {
      System.err.println(USAGE);
      System.exit(1);
    }

    LexicalizedParser  asc;
    
    String model;
    
    if (loadFile != null && ! loadFile.equals("")) {
    	//asc = new LexicalizedParser (loadFile);
    	//asc.setOptionFlags(new String[]{"-maxLength", "100", "-retainTmpSubcategories"});
    	
    	model = loadFile;
    } 
    else{
    	//asc = new LexicalizedParser ("englishFactored.ser.gz");
    	//asc.setOptionFlags(new String[]{"-maxLength", "100", "-retainTmpSubcategories"});
    	
    	model = "englishFactored.ser.gz";
    }

    int port = 0;
    try {
      port = Integer.parseInt(portStr);
    } catch (NumberFormatException e) {
      System.err.println("Non-numerical port");
      System.err.println(USAGE);
      System.exit(1);
    }
        
    //new DEPServer(port, asc).run();
    
    new DEPServer(port, loadFile).run();
    
  }

}
