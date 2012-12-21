package loria.ontoaspatt.algorithm.datastructure;

import java.io.File;
import java.util.TreeSet;

/**
 * A class to represent a numerical data set
 * @author adrien coulet
 * @author florent domenach
 * @author mehdi kaytoue *
 */
public class AnnotationData
{
	// an array for storing data values: first dimension corresponds to objects
	// second dimension corresponds to attributes. value[g][m] is m(g) in the paper,
	// that is the value of object g for attribute m
	public TreeSet<String>[][] values = null;
	
	// the filename from which data are read
	public File baseFileName = null;
	
	// object labels
	public String[] lineId = null; 

	//	attribute labels
	public String[] columnId = null;

	public double domainSize = 0; 
}




