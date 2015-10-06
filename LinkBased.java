import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("rawtypes")
class CompareValue implements Comparator
{
    
    Map rmap;
    
    public CompareValue(Map rmap) {
        this.rmap = rmap;
    }
    
    @SuppressWarnings("unchecked")
    public int match(Object keyA, Object keyB) {
        Comparable value1 = (Comparable) rmap.get(keyA);
        Comparable value2 = (Comparable) rmap.get(keyB);
        return value2.compareTo(value1);
    }
    
    @Override
    public int compare(Object o1, Object o2) {
        // TODO Auto-generated method stub
        return 0;
    }
}

public class LinkBased
{
    public class doc
    {
        public String url;
        public int edges;
        HashMap<doc, Integer> hashmap = new HashMap<doc, Integer>();
        
        //metadata
        
        public float ealt; //eastern most lat
        public	float wlat; //western most lat
        public	float slat; //southernmost lat
        public	float nlat; //northernmost lat
        
        public List<String>features;//instruments
        public List<String>keys;//keywords
        public List<String>locations;//locations
        
        public float xc;
        public float yc;
        
        public void setedges(int edges)
        {
            this.edges=edges;
        }
        public void sethashmap(doc doc,int edges)
        {
            this.hashmap.put(doc, edges);
        }
        
    }
    
    
    public static List<doc>  populate() throws IOException
    {
        String firstline = null, mystr;
        List<doc> docs= new ArrayList<doc>();
        File doc1 = new File("solrindex");
        FileReader fr = null;
        try {
            fr = new FileReader("C:\\Stuff\\workspace\\Linkbased\\src\\inp.txt");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        BufferedReader br = new BufferedReader(fr);
        StringBuffer stringBuffernw = new StringBuffer();
        
        //int lineno=0;
        firstline="dummy";
        for(int obj=0;obj<4;obj++)
        {
            LinkBased l=new LinkBased();
            doc x=l.new doc();
            
            firstline=br.readLine();
            mystr = firstline.replaceAll("\\s","");
            String c[]=mystr.split(":");
            x.url=c[1];
            
            firstline=br.readLine().replaceAll("\\s","");
            c=firstline.split(":");
            x.ealt=Float.parseFloat(c[1]);
            
            firstline=br.readLine().replaceAll("\\s","");
            c=firstline.split(":");
            String str=c[1];
            x.features=Arrays.asList(str.split("\\s*,\\s*"));
            
            firstline=br.readLine().replaceAll("\\s","");
            c=firstline.split(":");
            String kw=c[1];
            x.keys=Arrays.asList(kw.split("\\s*,\\s*"));
            
            firstline=br.readLine().replaceAll("\\s","");
            c=firstline.split(":");
            x.wlat=Float.parseFloat(c[1]);
            
            firstline=br.readLine().replaceAll("\\s","");
            c=firstline.split(":");
            String loc=c[1];
            x.locations=Arrays.asList(loc.split("\\s*,\\s*"));
            
            firstline=br.readLine().replaceAll("\\s","");
            c=firstline.split(":");
            x.slat=Float.parseFloat(c[1]);
            
            firstline=br.readLine().replaceAll("\\s","");
            c=firstline.split(":");
            x.nlat=Float.parseFloat(c[1]);
            
            //System.out.println("blah:"+x.features);
            
            x.xc=(x.ealt+x.wlat)/2;
            x.yc=(x.nlat+x.slat)/2;
            docs.add(x);
        }
        
        return docs;
        
    }
    
    
    public static void ckw(doc doc1,doc doc2)
    {
        Collection<String> col1=doc1.keys;
        Collection<String> col2=doc2.keys;
        Collection<String>sm= new HashSet<String>(col1);
        System.out.println(col1);
        System.out.println(col2);
        sm.retainAll(col2);
        System.out.println(sm);
        
    }
    
    public static void cloc(doc doc1,doc doc2)
    {
        Collection<String> col1=doc1.locations;
        Collection<String> col2=doc2.locations;
        Collection<String>sm= new HashSet<String>(col1);
        System.out.println(col1);
        System.out.println(col2);
        sm.retainAll(col2);
        System.out.println(sm);
        
    }
    
    public static void cfeat(doc doc1,doc doc2)
    {
        Collection<String> col1=doc1.features;
        Collection<String> col2=doc2.features;
        Collection<String>sm= new HashSet<String>(col1);
        System.out.println(col1);
        System.out.println(col2);
        sm.retainAll(col2);
        System.out.println(sm);
        
    }
    
    public static void finaldist(doc doc1,doc doc2)
    {
        float x1,x2,y1,y2;
        x1=doc1.xc;
        x2=doc2.xc;
        y1=doc1.yc;
        y2=doc2.yc;
        
    }
    public static void finalorder(Map<Float, List<doc>>pr)
    {
        
        for(Map.Entry<Float,List<doc>> entry : pr.entrySet())
        {
            List<doc>dl=entry.getValue();
            List<String>idl=new ArrayList<String>();
            for(doc d:dl)
            {
                idl.add(d.url);
            }
            System.out.println(Arrays.toString(idl.toArray())+":"+entry.getKey());
            
        }
        
        
    }
    
    
    
    public static void main(String[] args)
    {
        
        
        List<doc> docs;
        
        LinkBased link=new LinkBased();
        try {
            docs = populate();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //graph created
        HashMap<doc , Float>rank=new HashMap<doc, Float>();
        HashMap<doc , Float>new_rank=new HashMap<doc, Float>();
        for(int i=0;i<4;i++)
        {
            
            rank.put(document_list[i], 1.0f);
        }
        //System.out.println(rank.size());
        //finalorder(rank);
        float d=0.85f;
        float sum;
        while(true)
        {
            new_rank.clear();
            for (int i=0;i<4;i++)
            {
                
                sum=0f;
                
                for (Map.Entry<doc, Integer> entry : document_list[i].hashmap.entrySet())
                {
                    //System.out.println(entry.getKey() + "/" + entry.getValue());
                    sum+=rank.get(entry.getKey())/entry.getKey().edges;
                }
                sum*=d;
                sum+=(1-d);
                new_rank.put(document_list[i],sum);
                
            }
            //finalorder(new_rank);
            if(rank.equals(new_rank))
            {
                break;
            }
            
            else
            {
                rank.clear();
                for (Map.Entry<doc, Float> entry : new_rank.entrySet())
                {
                    rank.put(entry.getKey(), entry.getValue());
                    
                }
                
            }
            
        }
        
        //now ranks are computed
        HashMap<Float, List<doc>>ua=new HashMap<Float, List<LinkBased.doc>>();
        for (Map.Entry<doc, Float> entry : rank.entrySet())
        {
            ua.put(entry.getValue(), new ArrayList<doc>());
        }
        for (Map.Entry<doc, Float> entry : rank.entrySet())
        {
            //if(ua.get(entry.getValue()).isEmpty()==true)
            {
                ua.get(entry.getValue()).add(entry.getKey());
            }
            
        }
        
        TreeMap<Float, List<doc>> rmap = new TreeMap<Float, List<doc>>(ua);
        Map<Float, List<doc>> maprev = rmap.descendingMap();
        
        finalorder(maprev);
        
        
    }
}
