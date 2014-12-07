/************************************************************************
 *         Copyright (C) 2002-2003 The University of Sheffield          *
 *       Developed by Mark Greenwood <m.greenwood@dcs.shef.ac.uk>       *
 *                                                                      *
 * This program is free software; you can redistribute it and/or modify *
 * it under the terms of the GNU General Public License as published by *
 * the Free Software Foundation; either version 2 of the License, or    *
 * (at your option) any later version.                                  *
 *                                                                      *
 * This program is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of       *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the        *
 * GNU General Public License for more details.                         *
 *                                                                      *
 * You should have received a copy of the GNU General Public License    *
 * along with this program; if not, write to the Free Software          *
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.            *
 ************************************************************************/

package mark.chunking;

import java.io.*;
import java.net.*;
import java.util.*;

public class Chunker
{
	private static Chunker _instance;
	
	private List rules = new ArrayList();
	private Map chunkTags = new HashMap();
	

	public static Chunker instance(){
		if(_instance == null){
			try{
			_instance = new Chunker("resources/rules", "resources/pos_tag_dict");
			}catch(IOException e){
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return _instance;
	}

	/*
	public static void main(String args[]) throws Exception
	{
		Chunker c = new Chunker(args[0],args[1]);

		BufferedReader in = new BufferedReader(new FileReader(args[1]));

		String line = in.readLine();

		Map chunkTags = new HashMap();

		while (line != null)
		{
			if (!line.trim().equals(""))
			{
				String[] tags = line.split(" ");
				chunkTags.put(tags[0],tags[1]);
			}

			line = in.readLine();
		}

		in.close();

		in = new BufferedReader(new InputStreamReader(System.in));

		line = in.readLine();

		while (line != null)
		{
			String[] tokens = line.split(" ");

			List wl = new ArrayList();
			List tl = new ArrayList();
			List pl = new ArrayList();

			for (int i = 0 ; i < tokens.length ; ++i)
			{
				String[] data = tokens[i].split("/");

				wl.add(data[0]);
				pl.add(data[1]);

				String ct = (String)chunkTags.get(data[1]);

				if (ct == null) ct = "I";

				tl.add(ct);
			}

			tl = c.chunkSentence(wl,tl,pl);

			boolean inBaseNP = false;
			boolean lineBegin = true;

			for (int i = 0 ; i < wl.size() ; ++i)
			{
				String ct = (String)tl.get(i);

				if (inBaseNP)
				{
					if (ct.equals("B"))
					{
						System.out.print(" ] [");
					}
					else if (ct.equals("O"))
					{
						System.out.print(" ]");
						inBaseNP = false;
					}
				}
				else
				{
					if (ct.equals("B") || ct.equals("I"))
					{
						if (!lineBegin) System.out.print(" ");
						lineBegin = false;
						System.out.print("[");
						inBaseNP = true;
					}
				}
				if (!lineBegin) System.out.print(" ");
				lineBegin = false;
				System.out.print(wl.get(i) + "/" + pl.get(i));
			}

			if (inBaseNP)
			{
				System.out.print("]");
			}

			System.out.println();

			line = in.readLine();
		}
	}*/
	
	
	public String processSentence(String line){
		StringBuffer out = new StringBuffer();
		
		try{
			String[] tokens = line.split(" ");
			ArrayList<String> toks = new ArrayList<String>();
			String current = "";
			for(int k=0;k<tokens.length;k++){
				current = current+tokens[k];
				
				if(current.contains("/")){
					toks.add(current);
					current = "";
				}else{
					//System.out.format("DEBUG NPChunker: empty word at k = %d%n", k);
					current+=" ";
				}
			}
			
			tokens = new String[toks.size()];
			toks.toArray(tokens);			
	
			List wl = new ArrayList();
			List tl = new ArrayList();
			List pl = new ArrayList();
	
			for (int i = 0 ; i < tokens.length ; ++i)
			{
				String[] data = tokens[i].split("/");
				/*String tok = tokens[i];
				String[] data = new String[2]; 
				for(int j=0; j<tok.length();j++){
					if(tok.charAt(j)=='/'){
						if(tok.charAt(j-1)!='\\'){
							data[0]=tok.substring(0,j);
							data[1]=tok.substring(j+1);
						}
					}
				}*/
	
				wl.add(data[0]);
				pl.add(data[1]);
	
				String ct = (String)chunkTags.get(data[1]);
	
				if (ct == null) ct = "I";
	
				tl.add(ct);
			}
	
			tl = chunkSentence(wl,tl,pl);
	
			boolean inBaseNP = false;
			boolean lineBegin = true;
	
			for (int i = 0 ; i < wl.size() ; ++i)
			{
				String ct = (String)tl.get(i);
	
				if (inBaseNP)
				{
					if (ct.equals("B"))
					{
						out.append(" ] [");
					}
					else if (ct.equals("O"))
					{
						out.append(" ]");
						inBaseNP = false;
					}
				}
				else
				{
					if (ct.equals("B") || ct.equals("I"))
					{
						if (!lineBegin) out.append(" ");
						lineBegin = false;
						out.append("[");
						inBaseNP = true;
					}
				}
				if (!lineBegin) out.append(" ");
				lineBegin = false;
				out.append(wl.get(i) + "/" + pl.get(i));
			}
	
			if (inBaseNP)
			{
				out.append("]");
			}
	
			out.append("\n");
		}catch(Exception e){
			System.out.println("Chunker problem: "+ e.getMessage());
			out.append(line);
		}
	
		//System.out.println("DEBUG out = "+ out.toString());	
		return out.toString();
	}

	/**
	 * The only constructor that reads the rules from a URL.
	 * @param u the URL of the rules file.
	 **/
	public Chunker(String rulesFile, String tagsFile) throws IOException
	{
		
		URL u = (new File(rulesFile)).toURL();
		
		//Open up the rules file read for reading
		BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream()));

		//read in the first rule from the file
		String rule = in.readLine();

		while (rule != null)
		{
			//while there are still rules to process...

			if (!rule.trim().equals(""))
			{
				//create and add a rule to the list of rules
				rules.add(new Rule(rule));
			}

			//read in the next rule;
			rule = in.readLine();
		}
		
		in = new BufferedReader(new FileReader(tagsFile));

		String line = in.readLine();

		while (line != null)
		{
			if (!line.trim().equals(""))
			{
				String[] tags = line.split(" ");
				chunkTags.put(tags[0],tags[1]);
			}

			line = in.readLine();
		}

		in.close();
	}

	/**
	 * This is the method which does all the work and returns
	 * an updated set of chunk tags.
	 * @param words an ordered List of the words within the sentence.
	 * @param tags an ordered List of the chunk tags within the sentence.
	 * @param pos an ordered List of the POS tags within the sentence.
	 * @return an ordered List of the updated chunk tags for the sentence.
	 **/
	public List chunkSentence(List words, List tags, List pos)
	{
		//add the word/pos/tag that represents the end of
		//the sentence, cos some of the rules match against
		//the end of the sentence
		words.add("ZZZ");
		pos.add("ZZZ");
		tags.add("Z");

		//Get an iterator over the rules and loop
		//through them...
		Iterator it = rules.iterator();
		while (it.hasNext())
		{
			//create an empty list to hold the new
			//chunk tags for this iterations
			List newTags = new ArrayList();

			//get the next rule we are going to apply
			Rule r = (Rule)it.next();

			//loop over all the words in the sentence
			for (int i = 0 ; i < words.size() ; ++i)
			{
				if (r.match(i,words,tags,pos))
				{
					//if the rule matches against the current
					//word in the sentence then and the new tag
					//from the rule to the new tag list
					newTags.add(r.getNewTag());
				}
				else
				{
					//the rule didn't match so simply copy the
					//chunk tag that was already assigned
					newTags.add(tags.get(i));
				}
			}

			//now replace the old tags with the new ones ready
			//for running the next rule, this stops rule-chaining
			tags = newTags;
		}

		//remove the last token from each list as these
		//are not part of the original input sentence
		words.remove(words.size()-1);
		pos.remove(pos.size()-1);
		tags.remove(tags.size()-1);

		//return the final updated chunk tag lists
		return tags;
	}
}