package loria.ontoaspatt.meet;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * This convenience class enable to work with the ontology.
 * It implements 
 * the EOP
 * the LCS
 * the Meet
 * the Join
 * operations
 * @author adrien coulet
 * @version 13 Dec 2012
 * 
 */

public class OntoPatternOperations {

	static String NCIT = "nci_thesaurus";
	static String NCIT_PATH = "./data/ontology/Thesaurus_12.04e.owl";
	static String NCIT_IRI_STR = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#";
	static IRI    NCIT_IRI  = IRI.create(NCIT_IRI_STR);
	static String SEMANTIC_TYPE = "Semantic_Type";
	static String THING = "Thing";

	OWLOntologyManager manager;
	OWLOntology onto  = null;
	OWLDataFactory dataFactory;
	String ontoName;
	
	public OntoPatternOperations(String ontoName) {
		this.ontoName=ontoName;
		if(ontoName.equals(NCIT)){
			// onto is OWL
			String ontoPath=NCIT_PATH;
			this.loadOwlOntology(ontoPath);			
		}		
	}

	/**
	 * load an OWL ontology form the owl file
	 * @param ontoName2
	 */
	private void loadOwlOntology(String owlFilePath) {
		this.manager = OWLManager.createOWLOntologyManager();
		try {
			System.out.println(">> Load ontology from "+owlFilePath);
			System.out.println(">> Add the option -Xmx (e.g., -Xmx1000m) to increase the heap space in case of large ontologies.");
			this.onto = manager.loadOntologyFromOntologyDocument(new File(owlFilePath));
	        System.out.println(">> "+onto.getOntologyID()+ " loaded.");
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		dataFactory = onto.getOWLOntologyManager().getOWLDataFactory();		
	}

	public String getLcsId(String c1, String c2) {
		// get the path to the root of c1
		ArrayList<String> pathToTheRoot1 = new ArrayList<String>();
		pathToTheRoot1 = this.getPathToTheRoot(c1);
		// in some ontology there is no common root
		if(!pathToTheRoot1.get(pathToTheRoot1.size()-1).equals(THING)){
			
		}
		// get the path to the root of c2
		ArrayList<String> pathToTheRoot2 = new ArrayList<String>();
		pathToTheRoot2 = this.getPathToTheRoot(c2);
		String lcsString = this.getLcsFrom2Path(pathToTheRoot1, pathToTheRoot2);
		if (lcsString==null)
			lcsString=THING;
		return lcsString;
	}
	
	public String getLcsId(TreeSet<String> C) {
	
		String runningLcs = "";
		
		for(String c: C){
			if(runningLcs.equals("")){// first concept of the list
				runningLcs=c;
			}else{
				runningLcs=this.getLcsId(runningLcs, c);
			}						
		}
		return runningLcs;
	}
	
	private ArrayList<String> getPathToTheRoot(String c1) {

		OWLClass c = dataFactory.getOWLClass(IRI.create(NCIT_IRI.toString()+c1));
		//TreeSet<ArrayList<OWLClass>> parentPaths = new TreeSet<ArrayList<OWLClass>>();
		ArrayList<OWLClass> parentPaths = new ArrayList<OWLClass>();
		parentPaths.add(c);// required because the c itself can be the lcs
		ArrayList<String> parentPathsInString = new ArrayList<String>();
		parentPaths = this.getParentPaths(c, parentPaths);
		for(OWLClass ci: parentPaths){
			parentPathsInString.add(ci.toStringID().substring(NCIT_IRI.toString().length()));
		}
		return parentPathsInString;
	}

	private ArrayList<OWLClass> getParentPaths(OWLClass c, ArrayList<OWLClass> parents) {
		Set<OWLClassExpression> supC = c.getSuperClasses(onto);
		for(OWLClassExpression ce:supC){
			if(!ce.isAnonymous()){
				OWLClass sup = ce.asOWLClass();	
				parents.add(sup);
				parents = this.getParentPaths(sup, parents);
			}
		}		
		return parents;
	}
	
	private String getLcsFrom2Path(ArrayList<String> pathToTheRoot1,
			ArrayList<String> pathToTheRoot2) {

		for(String p1:pathToTheRoot1){
			for(String p2:pathToTheRoot2){
				if(p1.equals(p2)){					
					return p1;
				}
			}
		}
		
		return null;
	}

	public TreeSet<String> getEop(String c1, String c2) {

		// get the path to the root of c1
		ArrayList<String> pathToTheRoot1 = new ArrayList<String>();
		pathToTheRoot1 = this.getPathToTheRoot(c1);
		// get the path to the root of c2
		ArrayList<String> pathToTheRoot2 = new ArrayList<String>();
		pathToTheRoot2 = this.getPathToTheRoot(c2);
		
		String lcsString = this.getLcsFrom2Path(pathToTheRoot1, pathToTheRoot2);
		
		TreeSet<String> eop = new TreeSet<String>();
		eop = this.getEop(pathToTheRoot1, pathToTheRoot2, lcsString);
		
		return eop;
	}

	private TreeSet<String> getEop(ArrayList<String> pathToTheRoot1,
			ArrayList<String> pathToTheRoot2, String lcsString) {
		TreeSet<String> eop = new TreeSet<String>();
		
		// explore path1 to the lcs
		for(String p1:pathToTheRoot1){
			if(p1.equals(lcsString)){
				eop.add(lcsString); //we add it only once, then this is not done in the second loop
				break;
			}else{
				eop.add(p1);
			}
		}
		// explore path2 to the lcs
		for(String p2:pathToTheRoot2){
			if(p2.equals(lcsString)){
				break;
			}else{
				eop.add(p2);
			}
		}
		return eop;
	}

	public TreeSet<String> getEop(TreeSet<String> C) {
		TreeSet<String> eop = new TreeSet<String>();
		String previousc = "";
		ArrayList<String> previousPathToTheRoot = new ArrayList<String>();
		if(C.size()==1){
			eop.addAll(C); // the eop is the only concept itself
		}else{
			for(String c: C){
				if(previousc.equals("")){// first concept of the list
					previousc=c;
					previousPathToTheRoot = this.getPathToTheRoot(previousc);
				}else{
					// get the path to the root of c2
					ArrayList<String> pathToTheRoot2 = new ArrayList<String>();
					pathToTheRoot2 = this.getPathToTheRoot(c);
					// get the lcs
					String lcsString = this.getLcsFrom2Path(previousPathToTheRoot, pathToTheRoot2);
					eop.addAll(this.getEop(previousPathToTheRoot, pathToTheRoot2, lcsString));
					
					//update variable for the next iteration
					previousc=c;
					previousPathToTheRoot = this.getPathToTheRoot(previousc);
				}						
			}
		}
		return eop;
	}

	public TreeSet<String> meet(TreeSet<String> C1, TreeSet<String> C2) {
		TreeSet<String> meet = new TreeSet<String>();		
		if(C1.size()==1 && C1.first().equals("")){
			C1=new TreeSet<String>();
		}
		meet.addAll(this.getEop(C1));
		meet.addAll(this.getEop(C2));
		if(!C1.isEmpty() && !C2.isEmpty()){
			meet.addAll(this.getEop(this.getLcsId(C1), this.getLcsId(C2)));
		}
		return meet;
	}
	
	public TreeSet<String> join(TreeSet<String> C1, TreeSet<String> C2) {
		TreeSet<String> join = new TreeSet<String>();		
		join.addAll(this.getEop(C1));
		join.retainAll(this.getEop(C2));
		return join;
	}	
}
