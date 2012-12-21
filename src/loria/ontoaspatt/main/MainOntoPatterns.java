package loria.ontoaspatt.main;

import java.util.TreeSet;

import loria.ontoaspatt.algorithm.CloseByOneOnto;
import loria.ontoaspatt.algorithm.datastructure.AnnotationData;
import loria.ontoaspatt.algorithm.datastructure.AnnotationDataReader;

/***
 * This class is the main entry point.
 * @author adrien coulet
 * @author florent domenach
 * @author mehdi kaytoue
 * @version 13 Dec 2012
 * 
 * To enable the loading of large ontoogies, such as NCIT, 
 * please add the option -Xmx1000m to increase 
 * the heap space allocated to the Java virtual machine.
 */

public class MainOntoPatterns {

	// Relative support, between 0 and 1	
	static double minSuppRelative = 0; 
	// Absolute support
	static int minSup = 4;

	// the file containing the annotations
	static String filename = "./data/context/ToyContext.dat";
	// the ontology defining the order
	static String onto;
	public static final String NCIT="nci_thesaurus";

	// A basic structure allowing access to data properties (objects, attributes, values)
	static AnnotationData dataStructure = null;
	
	// Set to true for printing the results.
	// Warning: this make worse the execution time, since printing in standard output each pattern 
	// when just generated is really not efficient in java.
	// Redirect output into a file is much more efficient.
	public static boolean printResult = true;
	
	public static void main(String[] args) throws Exception {
		
		/*** Read the data and minimum support ****/
		dataStructure = new AnnotationDataReader(filename).getData();
		
		TreeSet<String>[][] data =  dataStructure.values;
		System.out.println(">> Data read from file: "+  filename);
		if (minSuppRelative != -1)
			minSup = (int) ((double)data.length * minSuppRelative);
		System.out.println(">> Minimum support is " + minSup + "/"+ data.length );
		
		/*** Specify the ontology to use ****/
		onto=NCIT;
		
		/*** Frequent closed interval pattern extraction with MinIntChange****/
		new CloseByOneOnto(dataStructure,minSup,onto).start();
	}

}
