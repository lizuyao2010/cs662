package edu.cmu.cs.lti.relationFilter;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.util.Properties;
import edu.stanford.nlp.util.StringUtils;

/**
 * This class listen send input sentence to parser server and receive information back from the server
 * 
 */
public class DEPClient { 
	public DEPClient() {}
	
	private static void communicateWithDEPServer(String host, int port, String inputStr, String outCoNLL) throws IOException {
		
		if (host == null) { host = "localhost"; }
		
		//use it to write out CoNLL file
		BufferedWriter outfile = null;
		if (outCoNLL != "") {
			
			try{
				outfile = new BufferedWriter(new FileWriter(outCoNLL));
			} 
			catch (FileNotFoundException ex) { ex.printStackTrace();} 
			catch (IOException ex) { ex.printStackTrace(); }
		}
		
		//interactive mode
		if (inputStr == null || inputStr.equals("")) {
			BufferedReader stdIn = new BufferedReader( new InputStreamReader(System.in));
			
			System.out.println("Input some text and press RETURN to POS tag it, or just RETURN to finish.");
			
			for (String userInput; (userInput = stdIn.readLine()) != null && ! userInput.matches("\\n?"); ) { 
				try { 
					Socket socket = new Socket(host, port);
					
					PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
									
					BufferedReader in = new BufferedReader(new InputStreamReader( socket.getInputStream()));
					
					
					// send string to socket 
					out.println(userInput); 
					
					// Print the results receiving from DEPServer 
					
					String line = "";
					while ( ( line = in.readLine() ) != null ){
						System.out.println(line);					
					}
					
					//System.out.println(in.readLine());
					
					in.close();								
					socket.close();
					
				} catch (UnknownHostException e) { 
					System.err.print("Cannot find host: "); 
					System.err.println(host); return; 
				} catch (IOException e) { 
					System.err.print("I/O error in the connection to: "); 
					System.err.println(host); return; 
				} 
			} 
			stdIn.close();			
		}
		else{  //batch mode, parsing sentences in a file
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(inputStr)));
				String userInput;
				while ((userInput = bufferedReader.readLine()) != null) {					
					try {
						Socket socket = new Socket(host, port);
						
						PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
										
						BufferedReader in = new BufferedReader(new InputStreamReader( socket.getInputStream()));
						
						//send string to socket 
						out.println(userInput);
						
						//array of input words, use for CoNLL format
						String[] sent = userInput.split(" ");						
						int[] dv = new int[Arrays.asList(sent).size()];						
						for (int i=0; i < Arrays.asList(sent).size(); i++) { dv[i] = 0;}
						
						// Print the results receiving from DEPServer					
						String line = "";
						while ( ( line = in.readLine() ) != null ){
							
							if (outCoNLL == null || outCoNLL.equals("")) {
								//stdout 
								System.out.println(line);								
							}
							else{ // output as CoNLL format
								
								//regex for non-typed dependencies
								String patternStr = "^(.+?)\\((.*)\\-(\\d+)\\,(.*)\\-(\\d+)\\)$";
								
								// Compile and use regular expression 
								Pattern pattern = Pattern.compile(patternStr); 
								Matcher matcher = pattern.matcher(line); 
								boolean matchFound = matcher.find(); 
								
								if (matchFound) {										
									//DEBUG
									//System.out.println("DType="+ matcher.group(1)+ 
									//		"; father=" + matcher.group(2) + 
									//		"; fatherPost=" + matcher.group(3) +
									//		"; child=" + matcher.group(4) +
									//		"; childPost=" + matcher.group(5));
									
									dv[ Integer.parseInt(matcher.group(5)) - 1 ] = Integer.parseInt(matcher.group(3));									
								}
							}
						}
						
						//write to a file with CoNLL format 
						if (outCoNLL != "") {
							try {
								for (int i=0; i < Arrays.asList(sent).size(); i++) {
									int j = i + 1;
									outfile.write(j + "\t" + sent[i] + "\t_\t_\t_\t_\t" + dv[i] + "\t_\t_\t_\n");
								}
								outfile.write("\n");
							} catch (IOException e) { 
								System.err.print("Cannot write to file " + outCoNLL);
								return;
							} 
						} 
						
						//terminate socket connection
						in.close();								
						socket.close();						
						
					}catch (UnknownHostException e) {
						System.err.print("Cannot find host: ");
						System.err.println(host); return;
					} catch (IOException e) {
						System.err.print("I/O error in the connection to: ");
						System.err.println(host); return;
					}
				}
				bufferedReader.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		//close CoNLL file writer
		if (outfile != null ) { outfile.close(); }
	} 
	
	private static final String USAGE = "Transtac Nov 2008 Eval: English Dependency Parser Client" +
		"\nUsage: DEPClient -host [server name] -port [portNumber] -infile [English text file] -outCoNLL [write Dependency tree to this file in CoNLL format]" +
		"\nExample: java -mx700m -cp 'stanford-parser.jar:' DEPClient -host localhost -port 1234 -infile English";
	
	public static void main (String[] args) throws Exception {
		Properties props = StringUtils.argsToProperties(args);
		
	    String hostStr = props.getProperty("host");
	    if (hostStr == null || hostStr.equals("")) {
	    	System.err.println(USAGE);
	    	System.exit(1);
	    }
	
	    String portStr = props.getProperty("port");
	    if (portStr == null || portStr.equals("")) {
	      System.err.println(USAGE);
	      System.exit(1);
	    }
	    int port = 0;
	    try {
	      port = Integer.parseInt(portStr);
	    } catch (NumberFormatException e) {
	      System.err.println("Non-numerical port");
	      System.err.println(USAGE);
	      System.exit(1);
	    }
	    
	    String inputStr = props.getProperty("infile");
	    
	    //write the dependency tree into a CoNLL file format
	    String outCoNLL = props.getProperty("outCoNLL");
	    if (outCoNLL == null || outCoNLL.equals("")) {
	    	outCoNLL = "";
	    }
		communicateWithDEPServer(hostStr, port, inputStr, outCoNLL);
	}
} 
