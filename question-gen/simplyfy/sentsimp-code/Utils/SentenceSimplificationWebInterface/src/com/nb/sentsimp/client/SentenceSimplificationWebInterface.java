package com.nb.sentsimp.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;

import com.google.gwt.user.client.Window;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label; 
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Button; 
import com.google.gwt.user.client.ui.HTML; 
import com.google.gwt.user.client.ui.RootPanel; 
import com.google.gwt.user.client.ui.TextArea; 
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SentenceSimplificationWebInterface implements EntryPoint {
		
	private VerticalPanel mainPanel = new VerticalPanel();
	private HTML errorHtml = new HTML();
	private Label longsentLabel1 = new Label("The long sentence\n");
	private Label longsentLabel = new Label();	
	
	private VerticalPanel addPanelTextArea = new VerticalPanel();
	private TextArea newShortSentTextArea = new TextArea();
	private Button addSentTextAreaButton = new Button("Submit short sentences");
	
	private String assignmentId;	
	private String workingsent = "";
	
	private int startTime;
	private int endTime;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {		
			
//		if(getUserAgent().contains("msie"))	{			
//			errorHtml.setHTML("Internet Explorer is NOT SUPPORTED, please use firefox/chrome/safari");
//			errorHtml.addStyleName("error");
//			RootPanel.get("sentsimp").add(errorHtml);
//			return;
//		}
		
		// Retrieve the current working sentence and Amazon assignmentID, 
		workingsent  = Window.Location.getParameter("sent");		
		assignmentId = Window.Location.getParameter("assignmentId");		
		
		if(assignmentId == null || workingsent == null){			
			errorHtml.setHTML("The URL is invalid, what you see below is just a demo.");
			errorHtml.addStyleName("info");
			try {
				workingsent = "John lives in Seattle, works for Amazon, and is a fund raiser of a charity organization ";
				assignmentId = "123";
			} catch (Exception e) {				
			}
		}else{
		}

		// See if it has been accepted		
		if(assignmentId.equals("ASSIGNMENT_ID_NOT_AVAILABLE")){
			errorHtml.setHTML("You have not yet accept, the HIT is in the preview mode");
			errorHtml.addStyleName("info");
			addSentTextAreaButton.setEnabled(false);
			addSentTextAreaButton.setText("You have not yet accept, the HIT is in the preview mode");
		}		
		
		// A label to display the long sentence		 
		longsentLabel.setText(workingsent);
		longsentLabel.addStyleName("longsentLabelStyle");
		longsentLabel1.addStyleName("longsentLabelStyle1");		
				
		// Assemble Add Text Area panel.
		newShortSentTextArea.setCharacterWidth(120); 
		newShortSentTextArea.setVisibleLines(7);
		newShortSentTextArea.setText(workingsent);
		addPanelTextArea.add(newShortSentTextArea);		 
		addPanelTextArea.add(addSentTextAreaButton);
		addPanelTextArea.addStyleName("addPanel");
		
		// Assemble Main panel.
		mainPanel.add(errorHtml);
		mainPanel.add(longsentLabel1);
		mainPanel.add(longsentLabel);
		mainPanel.add(addPanelTextArea);		
		
		// Associate the Main panel with the HTML host page. 
		RootPanel.get("sentsimp").add(mainPanel);
		
		// Move cursor focus to the input text area. 
		newShortSentTextArea.setFocus(true);
		
		
		Date date = new Date();
        startTime = (int) (date.getTime() * .001);
        
		// Listen for mouse events on the Add button.
//		addSentButton.addClickHandler( new ClickHandler() {
//			public void onClick (ClickEvent event){
//				addShortSentence();				
//			}			
//		} );
//		
//		// Listen for keyboard events in the input box. 
//		newShortSentTextBox.addKeyPressHandler( new KeyPressHandler(){
//			public void onKeyPress( KeyPressEvent event){
//				if (event.getCharCode() == KeyCodes.KEY_ENTER){
//					addShortSentence();
//				}				
//			}
//		});
		
		// Listen for mouse events on the Add button in text area.
		addSentTextAreaButton.addClickHandler( new ClickHandler() {
			public void onClick (ClickEvent event){
				addShortSentenceTextArea();				
			}			
		} );		

	}
	
	
	/** 
	* Checking short sentences into TextArea. 
	* Submission is executed when the user clicks the addSentButtonTextArea 
	**/	
	private void addShortSentenceTextArea() {
		final String sent = newShortSentTextArea.getText().trim();; 
		newShortSentTextArea.setFocus(true);
		
		ArrayList<String> shortsentences = new ArrayList<String>();
		
		// At least 2 short sentences		
		shortsentences = stringToLine(sent);
		if ( shortsentences.size() < 2 ){			
			errorHtml.setHTML("Not valid! The number of provided short sentences is " + shortsentences.size() + 
					".<br /> The task has NOT been completed.");
			errorHtml.setStyleName("error");			
			newShortSentTextArea.selectAll();
			return;
		}
		
		// A short sentence must have at lest 3 words		
		for (Iterator<String> it = shortsentences.iterator(); it.hasNext(); ) { 
			String s = it.next(); 
			if ( wordcount( s ) < 3) {				
				errorHtml.setHTML("Not valid! '" + s + "' is a too short sentence." +
									"<br /> The task has NOT been completed.");
				errorHtml.setStyleName("error");
				newShortSentTextArea.selectAll(); 
				return;
			} 				
		}			
		
		String combineshortsentences = "";
		for (Iterator<String> it = shortsentences.iterator(); it.hasNext(); ) { 
			String s = it.next();	
			combineshortsentences += s.trim() + " ";
		}
		
		// Word edit distance between the original long sentence and short sentences
		// reject if score > 1/2 original length 
		// or too low < 4
		
		int ld =  LevenshteinDistance( workingsent.split("\\s+"), combineshortsentences.split("\\s+"));
		if ( ld < 2 ){			
			errorHtml.setHTML("Not valid! Please put more efforts to construct shorter sentences! " + " [code "+ld + "]" +
								".<br /> The task has NOT been completed.");
			errorHtml.setStyleName("error");
			newShortSentTextArea.selectAll();
			return;
		}else if ( ld > (workingsent.split("\\s+").length / 2) ){			
			errorHtml.setHTML("Not valid! Please construct shorter sentences such that they have close meaning with the original long one! " + " [code "+ld + "]" + 
								".<br /> The task has NOT been completed.");
			errorHtml.setStyleName("error");
			newShortSentTextArea.selectAll();
			return;
		}
		
		// Add the short sentence to the table 
		// but don't add the short sentence if it's already in the table.
		
		String mergeshortsentences = "";
		for (Iterator<String> it = shortsentences.iterator(); it.hasNext(); ) { 
			String s = it.next();	
			mergeshortsentences += s.trim() + " SENTBREAK ";
		}
		
		//Submit results 
		Submit(mergeshortsentences);
		
		// Successfully done		
		errorHtml.setHTML("The task has been completed. Thank you very much and please wait for our review.");
		errorHtml.setStyleName("success");
		
		
	}
	
	/**
	 * Submit result back to Amazon MTurk
	 **/
	protected void Submit(String ss) {		
		Date date = new Date();
        endTime = (int) (date.getTime() * .001) - startTime;        
        
		StringBuffer postData = new StringBuffer();
		postData.append("http://workersandbox.mturk.com/mturk/externalSubmit?");
	
		postData.append(URL.encode("ss")).append("=").append(URL.encode(ss));
		postData.append("&");
		postData.append(URL.encode("assignmentId")).append("=").append(URL.encode(assignmentId));
		
		postData.append("&");
		postData.append(URL.encode("time")).append("=").append(URL.encode(Integer.toString(endTime)));
		postData.append("&");
		postData.append(URL.encode("lang")).append("=").append(URL.encode(getUserDisplayLanguage()));
		postData.append("&");
		postData.append(URL.encode("city")).append("=").append(URL.encode(getGeobytesCity()));		
		postData.append("&");
		postData.append(URL.encode("region")).append("=").append(URL.encode(getGeobytesRegion()));
		postData.append("&");
		postData.append(URL.encode("country")).append("=").append(URL.encode(getGeobytesCountry()));		
		
		Window.open(postData.toString(), "_self", "");
		//Window.alert(postData.toString());		
	}
	
	/**
	 * Compute string edit distance between 2 string arrays  
	 **/
	private int LevenshteinDistance(String[] s1, String[] s2) {		 		
		int m = s1.length;
		int n = s2.length;
		int cost;
		
		// d is a table with m+1 rows and n+1 columns declare		
		int d[][] = new int[m+1][n+1];
				
		for (int i = 0; i <= m; i++) { d[i][0] = i; }
		for (int j = 0; j <= n; j++) { d[0][j] = j; }		
		
		for (int j = 1; j <= n; j++ ){ 
			for (int i = 1; i <= m; i++) {		
				if ( s1[i-1].equalsIgnoreCase(s2[j-1]) ) {					
					cost = 0;
				}  
				else { cost = 1; }
				
				d[i][j] = minimum ( d[i-1][j] + 1, d[i][j-1] + 1, d[i-1][j-1] + cost);								
			} 
		}
		return d[m][n];
	}
	
	/**
	* Get minimum of three values 
	**/
	private int minimum (int a, int b, int c) { 
		int mi; 
		mi = a; 
		if (b < mi) { mi = b; } 
		if (c < mi) { mi = c; } 
		return mi; 
	}
	
	/** 
	 * From a string to lines, remove empty lines.
	 **/
	private  ArrayList<String> stringToLine(String st){
		String[] sents = st.split("\n");
		ArrayList<String> noemptysents = new ArrayList<String>();
		
		int i = 0;		
		while (i < sents.length){
			if ( wordcount(sents[i]) > 0){
				noemptysents.add(sents[i]);				
			}
			i++;
		}		
	    return noemptysents;		
	}

	/** 
	 * Counting how many words in a string. 
	 * GWT doest not support java regex 
	 **/
	private long wordcount(String line){
		long numWords = 0;		
		int index = 0;
	    boolean prevWhiteSpace = true;
	    
	    while(index < line.length()){
	    	char c = line.charAt(index++);	    	
	    	boolean currWhiteSpace;
	    	if (c == ' '){
	    		currWhiteSpace = true;
	    	}else{
	    		currWhiteSpace = false;
	    	}
	    	if(prevWhiteSpace && !currWhiteSpace){
	    		numWords++;
	    	}
	    	prevWhiteSpace = currWhiteSpace;
	    }
	    return numWords;		
	}
	
	/**
	 * Get URL string using JSNI
	 **/
	public static native String getHref() /*-{
		return $wnd.location.href;
	}-*/;
	
	/**
	 * Get user browser information
	 **/	
	public static native String getUserAgent() /*-{
		return navigator.userAgent.toLowerCase();
	}-*/;

	/**
	 * Get user information from Geobytes
	 **/
	public static native String getGeobytesCountry() /*-{
		return $wnd.sGeobytesCountry;
	}-*/;

	public static native String getGeobytesCity() /*-{
		return $wnd.sGeobytesCity;
	}-*/;
	
	public static native String getGeobytesRegion() /*-{
		return $wnd.sGeobytesRegion;
	}-*/;	

	public static native String getUserDisplayLanguage() /*-{
		return $wnd.navigator.language ? $wnd.navigator.language : $wnd.navigator.userDisplayLanguage;
	}-*/;

}
