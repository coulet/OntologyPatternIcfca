package loria.ontoaspatt.algorithm;

import java.util.ArrayList;
import java.util.TreeSet;

import loria.ontoaspatt.main.MainOntoPatterns;
import loria.ontoaspatt.algorithm.datastructure.AnnotationData;
import loria.ontoaspatt.model.ClosedConcept;

/**
 * This algorithm is an adaptation of the Close By One algorithm with ontology patterns.
 * Its implementation is also inspired form the MinIntChange algorithm designed and described in Kaytoue et al, IJCAI2011.
 * Extracts frequent closed ontology patterns.
 * @author adrien coulet
 * @author florent domenach
 * @author mehdi kaytoue
 * @version 13 Dec 2012
 * 
 */
public class CloseByOneOnto extends Algorithm{

	public int closedCount = 0;
	public ArrayList<ClosedConcept> L = new ArrayList<ClosedConcept>();

	public CloseByOneOnto(AnnotationData d, int minSup, String onto)
	{
		super(d,minSup,onto);
	}

	public void start ()
	{
		long beginAt = System.currentTimeMillis();
		
		for(String g:G){
			TreeSet<String> gSet = new TreeSet<String>();
			gSet.add(g);
			TreeSet<String>[] g_square = square(gSet);
			TreeSet<String> g_square_square = square(g_square);
	
			process(gSet, g, g_square_square, g_square);			
		}
		System.out.println(">> " + this.getClass().getCanonicalName() +"   - " + closedCount +" closed - " +
				(System.currentTimeMillis() - beginAt) + "ms.");
	}

	@SuppressWarnings("unchecked")
	private void process(TreeSet<String> A, String g, TreeSet<String> C, TreeSet<String>[] D) {
		if(isCanonical(C, A, g) && (C.size() > minSup)){
			L.add(new ClosedConcept(C,D));
			if (MainOntoPatterns.printResult)
				System.out.println("Closed " + C + " --- " + toStringPattern(D));
			
			closedCount++;
			TreeSet<String> H = new TreeSet<String>();
			H = getObjects();
			H=(TreeSet<String>) H.tailSet(g);
			H.removeAll(C);
			for(String f: H){
				TreeSet<String> Z = new TreeSet<String>();
				Z.addAll(C);
				Z.add(f);
				TreeSet<String>[] Y = new TreeSet[attCount];
				TreeSet<String> fSet = new TreeSet<String>();
				fSet.add(f);
				Y = meetSquare(D, square(fSet));
				TreeSet<String> X = new TreeSet<String>();
				X=square(Y);
				
				process(Z,f,X,Y);
			}
		}
		
	}

	/**
	 * Canonicity test
	 * @param C
	 * @param A
	 * @param g
	 * @return true, if C can be said to be canonically generated
	 * i.e., if no object before g has been added in A to obtain C.
	 * False otherwise.
	 */
	private boolean isCanonical(TreeSet<String> C, TreeSet<String> A, String g) {
		
		TreeSet<String> Cclone = new TreeSet<String>();
		for(String c:C){
			Cclone.add(c);
		}
		Cclone.removeAll(A);

		if(Cclone.size()>0){			
			for(String h:Cclone){
				if(index(h)<index(g)){
					return false;
				}
			}
		}		
		return true;
	}

	private int index(String g) {
		int gi = 0;
		for(String o:G){
			gi++;
			if(o.equals(g)) return gi;
		}
		return 0;
	}
}