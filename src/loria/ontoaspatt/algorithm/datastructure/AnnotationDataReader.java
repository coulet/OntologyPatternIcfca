package loria.ontoaspatt.algorithm.datastructure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;
import java.util.StringTokenizer;


/**
 * A class to read numerical data form tab separated ascii files.
 * @author adrien coulet
 * @author florent domenach
 * @author mehdi kaytoue
 */
public class AnnotationDataReader
{

	private AnnotationData d;

	/**
	 * Define the column index where starts numerical attributes (counted from zero)
	 */
	private static int columnStart = -1;

	/**
	 * Define the column index where ends numerical attributes (included)
	 */
	private static int columnEnd  = -1;

	/**
	 * Define the column index of object's ID (usually zero)
	 */
	private static int columnId    = -1;


	public AnnotationData getData()
	{
		return this.d;
	}


	@SuppressWarnings("unchecked")
	public AnnotationDataReader(String filename) throws IOException
	{

		this.findIndexes(new File(filename));

		BufferedReader reader = new BufferedReader(new FileReader(filename));
		d = new AnnotationData();
		d.baseFileName = new File(filename);
		StringTokenizer st;
		//Separator to the data Input
		String separator = "\t";



		// first line: header
		String line = reader.readLine();
		st = new StringTokenizer(line,separator);
		int currentToken = 0;
		d.columnId = new String[columnEnd-columnStart+1];
		int index = 0;

		while(st.hasMoreTokens()) 
		{


			if ( (currentToken >= columnStart) && (currentToken <= columnEnd))
			{
				d.columnId[index] = st.nextToken();
				index++;

			}
			else
			{
				st.nextToken();
			}

			currentToken++;
		}


		// others lines...
		TreeSet<String>[] dataLine = null;
		index = 0;
		int indexValue;
		int countLine = 0;

		while ( (line = reader.readLine()) != null )
		{
			countLine++;  
		}
		d.values = new TreeSet[countLine][];
		d.lineId = new String[countLine];

		reader = new BufferedReader(new FileReader(filename));
		line = reader.readLine();

		while ( (line = reader.readLine()) != null )
		{
			currentToken = 0;
			indexValue = 0;
			// Missing values are identified by "SeparatorSeparator". We replace by "SeparatorNANSeparator"
			String str_case1     = separator + separator;
			CharSequence cs1     = str_case1.subSequence(0, str_case1.length());
			String str_case1_alt = separator + Double.NaN + separator;

			while (line.contains(cs1))
				line = line.replaceAll(str_case1, str_case1_alt);

			// Other possibility: the missing value is the last value
			while (line.endsWith(separator))
				line = line.concat(String.valueOf(Double.NaN));

			st = new StringTokenizer(line,separator);
			dataLine = new TreeSet[columnEnd-columnStart];

			while(st.hasMoreTokens()) 
			{
				if (currentToken == columnId)
				{
					d.lineId[index] = st.nextToken();
				}
				else if ( (currentToken >= columnStart) && (currentToken <= columnEnd))
				{
					String conceptSetStr = st.nextToken();
					conceptSetStr = conceptSetStr.substring(1, conceptSetStr.length()-1); // remove { and }
					String[] conceptSetAr = conceptSetStr.split(";");
					TreeSet<String> concepts = new TreeSet<String>();
					for(String c: conceptSetAr){
						if(!c.equals(""))
							concepts.add(c);
					}
					dataLine[indexValue] = concepts;
					indexValue++;
				}
				else
				{
					st.nextToken();
				}

				currentToken++;
			}

			d.values[index] = dataLine;
			index++;
		}
		reader.close();
	}

	/**
	 * This method inspect the second line of the data file to infer indexes
	 * @param dataFile database
	 * @throws IOException
	 */
	public void findIndexes(File dataFile) throws IOException 
	{
		BufferedReader reader = new BufferedReader(new FileReader(dataFile));

		// if no first line => error. if it exist, we don't need it
		if (reader.readLine() == null)
			throw new IOException();


		// get the second line
		String line = reader.readLine();

		// if don't exist => there is no data to read!
		if (line == null)
		{
			throw new IOException();
		}

		// parse data line we assume ID is index 0 for the moment
		StringTokenizer st = new StringTokenizer(line, "\t");
		int s = 1;
		int e = 1;

		// skip ID col, we suppose it is index 0
		st.nextToken();

		// determine start index
		while( st.hasMoreTokens())
		{
			e++;
			st.nextToken();
		}
			
		columnEnd = e;
		columnId = 0;
		columnStart=s;
	}

}
