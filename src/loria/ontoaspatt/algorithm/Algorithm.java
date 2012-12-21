package loria.ontoaspatt.algorithm;

import java.util.TreeSet;

import loria.ontoaspatt.algorithm.datastructure.AnnotationData;
import loria.ontoaspatt.meet.OntoPatternOperations;

/**
 * This abstract class represents an algorithm with common methods and initialization
 * @author adrien coulet
 * @author florent domenach
 * @author mehdi kaytoue
 */
public abstract class Algorithm {

	TreeSet<String> [][] data;
	String onto;
	OntoPatternOperations myMeet;
	int objCount,attCount;
	int minSup = 1;
	AnnotationData conData;
	TreeSet<String> G = new TreeSet<String>();

	/**
	 * Constructor
	 * @param data
	 * @param minSup
	 * @param onto 
	 */
	public Algorithm(AnnotationData  d, int minSup, String onto)
	{
		this.conData = d;
		this.data = d.values;
		this.objCount = data.length;
		this.attCount = data[0].length;
		this.minSup = minSup;
		this.onto = onto;
		G = getObjects();
		myMeet = new OntoPatternOperations(onto);
	}
	
	public Algorithm(){}
	
	/**
	 * The algorithm. 
	 */ 	
	public abstract void start();
	
	/**
	 * The first operator of the Galois connection
	 * 
	 * @param gSet a set of object
	 * @return its description, i.e., a set of object for each attributes
	 */
	@SuppressWarnings("unchecked")
	public TreeSet<String>[] square (TreeSet<String> gSet)
	{
		TreeSet<String>[] square = new TreeSet[attCount];
		for(int i = 0; i < attCount; i++){
			square[i] = new TreeSet<String>();
			boolean firstObject = true;
			for(String g: gSet){
				TreeSet<String> delta = new TreeSet<String>(); 
				if(firstObject){
					firstObject = false;
					square[i] = myMeet.getEop(delta(g, i));
				}else{
					delta = delta(g, i);
					square[i]=myMeet.meet(square[i], delta);
				}
			}
		}
		return square;
	}
	
	/**
	 * The second operator of the Galois connection
	 * 
	 * @param d an object description
	 * 
	 * @return the image of d
	 */
	public TreeSet<String> square(TreeSet<String>[] d)
	{
		TreeSet<String> C = new TreeSet<String>();		

		for(String g: getObjects()){
			boolean addObject = true;
			for (int i = 0; i < attCount && addObject; i++) {
				TreeSet<String> D = new TreeSet<String>();
				D = myMeet.getEop(d[i]);
				for(String c:myMeet.getEop(delta(g,i))){
					if(!D.contains(c)){
						addObject = false;
					}
				}
			}
			if(addObject){
				C.add(g);
			}
		}
		return C;
	}
	
	/**
	 * return the description of an object g for the attribute i
	 * @param g, an object
	 * @param i, an attribute
	 * @return delta(g), the description of the object 
	 */
	private TreeSet<String> delta(String g, int i) {
		int count=0;
		for(String objId: conData.lineId){
			if(g.equals(objId)){
				return data[count][i];
			}
			count++;
		}
		return null;
	}

	/**
	 * The meet operation over patterns
	 * @param d1, one description
	 * @param d2, another description
	 * @return the smaller description (interval) 
	 * that includes both d1 and d2
	 * 
	 */
	@SuppressWarnings("unchecked")
	public TreeSet<String>[] meetSquare(TreeSet<String>[] d1,
			TreeSet<String>[] d2) {
		TreeSet<String>[] meet = new TreeSet[attCount];
		for(int i = 0; i < attCount; i++){
			meet[i] = new TreeSet<String>();			
			meet[i]=myMeet.meet(d1[i], d2[i]);			
		}
		return meet;
	}
	
	/**
	 * Builds a string representation <{x,x}, ..., {x,x,x}> 
	 * for a given concept list pattern
	 * @param pattern an ontology pattern
	 * @return its string representation
	 */
	public String toStringPattern(TreeSet<String>[] pattern)
	{
		String res = "<";
		for (int i = 0; i < pattern.length; i++){

			res += "{";
			for(String c: pattern[i]){
				res += c+",";
			}
			if(!res.endsWith("{"))
				res=res.substring(0,res.length()-1); //remove the last ,
			res += "}";
		}
		return res + ">";
	}
	
	/**
	 * Return the complete set of object of the context.
	 * @return G
	 */
	public TreeSet<String> getObjects(){
		TreeSet<String> G = new TreeSet<String>(); 
		for(String g:conData.lineId){
			G.add(g);
		}
		return G;
	}
}
