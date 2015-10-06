package ContentBasedRanking;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class ContentBased {

	static List<String> stopwords=new ArrayList<String>();
	public static HashMap<DocObject, HashMap<String, Float>> doc_to_tf = new HashMap<DocObject, HashMap<String, Float>>();
	public static HashMap<String, Integer> allwords_to_docs= new HashMap<String, Integer>();
	public static HashMap<String, Float> idf= new HashMap<String, Float>();
	public static HashMap<String,Float>query_tfidf=new HashMap<String, Float>();
	public static HashMap<DocObject,HashMap<String, Float>>doc_tfidf=new HashMap<DocObject, HashMap<String, Float>>();
	public static HashMap<DocObject,Float> cosine_similarity=new HashMap<DocObject,Float>();
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws Exception {
		// TODO Auto-genated method stub
		
		ReadLines("stopwords");
		int id, content;
		File doc1 = new File("solrindex");
		FileReader fr1 = new FileReader(doc1);
		BufferedReader b1 = new BufferedReader(fr1);
		StringBuffer stringBuffer1 = new StringBuffer();
		String line1, mysz2;
		//Read from the file
		String url=null;
		List<DocObject> docs= new ArrayList<DocObject>();
		line1 = b1.readLine();
		mysz2 = line1.replaceAll("\\s","");
		
		while (line1 != null) {
			DocObject doc11 = new DocObject();
			id=0;
			content=0;
			
			while ( id==0 || content==0 )
			{
				if(line1!=null)
				{
				String[] line2=mysz2.split("::");
				if(line2[0].equals("id"))
				{
					id=1;
					doc11.id=line2[1].toString();
				}
				else if(line2[0].equals("content"))
				{
					content=1;
					doc11.content=line2[1];
				}
				line1=b1.readLine();
				mysz2 = line1.replaceAll("\\s","");
				}
				else
				{
					break;
				}
				
			}
			docs.add(doc11);
		}
		
		
		
		for(DocObject doc : docs)
		{
			//cleanup here
			
		}
		
		//Calculating the term frequencies for each doc
		for(DocObject doc : docs)
		{
			HashMap<String, Float> term_to_freq= new HashMap<String, Float>();
			String[] tokens=doc.content.split(" ");
			
			for(String word: tokens)
			{
				
				if(term_to_freq.containsKey(word.toLowerCase()))
				{
					term_to_freq.put(word.toLowerCase(), term_to_freq.get(word) + 1);
				}
				else
				{
					term_to_freq.put(word.toLowerCase(),(float) 1);
				}
				
				
			}
			
			for(Map.Entry<String, Float> words: term_to_freq.entrySet())
			{
					term_to_freq.put(words.getKey().toLowerCase(), words.getValue()/tokens.length);
			}
			
			doc_to_tf.put(doc,term_to_freq);
		}
		
		//Mapping all words to the number of docs they are present in
		for(Map.Entry<DocObject, HashMap<String, Float>> entry : doc_to_tf.entrySet())
		{
			for(Map.Entry<String, Float> term_to_freq: entry.getValue().entrySet())
			{
				
				if(allwords_to_docs.containsKey(term_to_freq.getKey().toLowerCase()))
				{
					allwords_to_docs.put(term_to_freq.getKey().toLowerCase(), allwords_to_docs.get(term_to_freq.getKey().toLowerCase()) + 1);
				}
				else
				{
					allwords_to_docs.put(term_to_freq.getKey().toLowerCase(), 1);
				}		
			}
		}
		
		//IDF calculation
		for(Map.Entry<String, Integer> entry : allwords_to_docs.entrySet())
		{
			idf.put(entry.getKey(), (float) (1+(float)(Math.log(docs.size()/(float)entry.getValue()))));
		}
		
		
		//print the idf values
		/*for(Map.Entry<String, Float> entry : idf.entrySet())
		{
			System.out.println(entry.getKey() + " : " + entry.getValue());
			
		}*/
		
		//Parsing the query
		String query="";
		Scanner in = new Scanner(System.in);
		System.out.println("Enter a string");
		query = in.nextLine();
	    String []queryList=query.split(" ");
		
		//Calculating tf for query
		HashMap<String, Float> query_term_to_freq= new HashMap<String, Float>();
		for(String word: queryList)
		{
			
			if(query_term_to_freq.containsKey(word.toLowerCase()))
			{
				query_term_to_freq.put(word.toLowerCase(), query_term_to_freq.get(word) + 1);
			}
			else
			{
				query_term_to_freq.put(word.toLowerCase(),(float) 1);
			}
			
			
		}
		
		for(Map.Entry<String, Float> words: query_term_to_freq.entrySet())
		{
			query_term_to_freq.put(words.getKey().toLowerCase(), words.getValue()/queryList.length);
		}
		
		
		//tf-idf for query terms
		for(Map.Entry<String, Float> words: query_term_to_freq.entrySet())
		{
			if(idf.containsKey(words.getKey().toLowerCase()))
			{
				query_tfidf.put(words.getKey().toLowerCase(), words.getValue() * idf.get(words.getKey().toLowerCase()));
			}
			else
			{
				query_tfidf.put(words.getKey().toLowerCase(),(float) 0);
			}
			
		}
		
		//print the query tf idf values
		/*System.out.println("Query tf idf");
				for(Map.Entry<String, Float> entry : query_tfidf.entrySet())
				{
					System.out.println(entry.getKey() + " : " + entry.getValue());
					
				}*/
		
				
		//tf -idf for query terms in documents
		for(DocObject doc : docs)
		{
			HashMap<String, Float> tfidf= new HashMap<String, Float>();
			for(Map.Entry<String, Float> words: query_term_to_freq.entrySet())
			{
				if(idf.containsKey(words.getKey().toLowerCase()) && doc_to_tf.get(doc).containsKey(words.getKey().toLowerCase()))
				{
					tfidf.put(words.getKey().toLowerCase(), doc_to_tf.get(doc).get(words.getKey().toLowerCase()) * idf.get(words.getKey().toLowerCase()));
				}
				else
				{
					tfidf.put(words.getKey().toLowerCase(),(float) 0);
				}
			}
			doc_tfidf.put(doc, tfidf);
		}
		
		//print the tfidf values for document
		
		/*for(Map.Entry<DocObject, HashMap<String, Float>> entry : doc_tfidf.entrySet())
		{
			System.out.println(entry.getKey().id);
			for(Map.Entry<String, Float> words: entry.getValue().entrySet())
			{
				System.out.println(words.getKey() + " : " + words.getValue());
			}
			System.out.println();
		}*/
		
		double modquery=0;
		//Calculate modquery
		for(Map.Entry<String, Float> entry : query_tfidf.entrySet())
		{
			modquery+=Math.pow(entry.getValue(), 2);
		}
		modquery=Math.sqrt(modquery);
		//finding cosine similarity
		for(Map.Entry<DocObject, HashMap<String, Float>> entry : doc_tfidf.entrySet())
		{
			cosine_similarity.put(entry.getKey(), (float) (dotproduct(query_tfidf,doc_tfidf.get(entry.getKey()))/(moddocument(doc_tfidf.get(entry.getKey())) * modquery)));
		}
		
		//print cosine similarity		
		System.out.println("Cosine similarity");
		for(Map.Entry<DocObject,Float> entry : cosine_similarity.entrySet())
		{
			System.out.println(entry.getKey().id +" : " + entry.getValue());
		}
		
		//Sorting the pages based on similarty values
		HashMap<Float, DocObject>ultapr=new HashMap<Float,DocObject>();
        

		for (Map.Entry<DocObject, Float> entry : cosine_similarity.entrySet())
		        {
		            ultapr.put(entry.getValue(), entry.getKey());
		        }
		
		TreeMap<Float, DocObject> map = new TreeMap<Float, DocObject>(ultapr);
		
		System.out.println(Arrays.toString(map.entrySet().toArray()));
		
		Map<Float, DocObject> reversemap = map.descendingMap();
		
		
		System.out.println(Arrays.toString(reversemap.entrySet().toArray()));
		
		/*for(Map.Entry<DocObject, HashMap<String, Float>> entry : doc_tfidf.entrySet())
		{
			System.out.println(entry.getKey().id);
			for(Map.Entry<String, Float> words: entry.getValue().entrySet())
			{
				System.out.println(words.getKey() + " : " + words.getValue());
			}
			System.out.println();
		}*/
		
		
		//Printing the term frequencies for each doc
		/*for(Map.Entry<DocObject, HashMap<String, Float>> entry : doc_to_tf.entrySet())
		{
			System.out.println(entry.getKey().id);
			for(Map.Entry<String, Float> words: entry.getValue().entrySet())
			{
				System.out.println(words.getKey() + " : " + words.getValue());
			}
			System.out.println();
		}*/
	}
		
	private static double dotproduct(HashMap<String,Float> querytfidf, HashMap<String,Float> doctfidf)
	{
		double dotprod=0;
		for(Map.Entry<String, Float> entry : query_tfidf.entrySet())
		{
			dotprod+=(entry.getValue() * doctfidf.get(entry.getKey()));
		}
		return dotprod;
	}
	
	private static double moddocument(HashMap<String,Float> doctfidf)
	{
		double modquery=0;
		for(Map.Entry<String, Float> entry : doctfidf.entrySet())
		{
			modquery+=Math.pow(entry.getValue(), 2);
		}
		modquery=Math.sqrt(modquery);
		return modquery;
	}
	
	//To Read the stopwords
	private static void ReadLines(String fileName) throws IOException {
		// TODO Auto-generated method stub
		File doc1 = new File(fileName);
		FileReader fr1 = new FileReader(doc1);
		BufferedReader b1 = new BufferedReader(fr1);
		String line1;
		String url=null;
		//read line from the file
		while ((line1 = b1.readLine()) != null) {
			if(!(line1.equals("") || line1.equals(null)))
			{
				stopwords.add(line1.trim());
			}
		}
		
	}


	
}
