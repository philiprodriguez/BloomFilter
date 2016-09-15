/*
Philip Rodriguez
Bloom Filter Implementation
9/14/2016
*/

import java.math.*;
import java.util.*;
public class BloomFilter<AnyType>
{
	boolean[][] tables;
	
	/*
		Initialize our tables to prime sizes, starting with the first prime
		after tableSizeStart. Make numTables of these tables.
	*/
	public BloomFilter(int tableSizeStart, int numTables)
	{
		BigInteger sizeThing = new BigInteger(""+tableSizeStart);
		tables = new boolean[numTables][];
		for(int i = 0; i < numTables; i++)
		{
			sizeThing = sizeThing.nextProbablePrime();
			tables[i] = new boolean[Integer.parseInt(sizeThing.toString())];
		}
	}
	
	/*
		Add val to the bloom filter.
	*/
	public void add(AnyType val)
	{
		int hashValue = (int)Math.abs(val.hashCode());
		
		for(int i = 0; i < tables.length; i++)
		{
			tables[i][hashValue % (tables[i].length)] = true;
		}
	}
	
	/*
		Returns true if there is a 100-falsePositiveRate() percent chance that
		val has been inserted into our bloom filter.
	*/
	public boolean contains(AnyType val)
	{
		int hashValue = (int)Math.abs(val.hashCode());
		
		for(int i = 0; i < tables.length; i++)
		{
			if (!tables[i][hashValue % (tables[i].length)])
			{
				return false;
			}
		}
		return true;
	}
	
	public double falsePositiveRate()
	{
		double fprAll = 1.0;
		
		for(int i = 0; i < tables.length; i++)
		{
			fprAll *= (double)trueCount(tables[i])/(double)tables[i].length;
		}
		
		return fprAll*100.0;
	}
	
	private int trueCount(boolean[] table)
	{
		int res = 0;
		for(int i = 0; i < table.length; i++)
		{
			if (table[i])
				res++;
		}
		return res;
	}
	
	
	/*
		This main method is just here to take the bloom filter out for a test drive!
	*/
	public static void main(String[] args)
	{
		int dataLength = 1000, numTables = 10, startTableSize = 10000, numberOfFalsePositiveChecks = 10000;
		
		BloomFilter<String> filter = new BloomFilter<>(startTableSize, numTables);
		HashSet<String> stringsThatShouldBeThere = new HashSet<String>();
		Scanner scan = new Scanner(System.in);
		
		System.out.println("How many random strings of length 1000 would you like to insert?");
		int numInserts = scan.nextInt();
		
		System.out.println("Inserting...");
		for(int i = 0; i < numInserts; i++)
		{
			String insert = randomString(dataLength);
			filter.add(insert);
			stringsThatShouldBeThere.add(insert);
		}
		
		//Calculate actual false positive rate with many strings!
		double actualFPR = 0.0;
		for(int i = 0; i < numberOfFalsePositiveChecks; i++)
		{
			String insert = randomString(dataLength);
			if (filter.contains(insert) && !stringsThatShouldBeThere.contains(insert))
			{
				actualFPR += 1.0;
			}
		}
		actualFPR /= numberOfFalsePositiveChecks;
		actualFPR *= 100.0;
		
		System.out.format("Predicted false positive rate = %10.5f%%", filter.falsePositiveRate());
		System.out.println();
		System.out.format("Actual false positive rate = %10.5f%%", actualFPR);
		System.out.println();
		
		//Calculating space used in memory...
		double bloomFilterSize = 0;
		for(int i = 0; i < filter.tables.length; i++)
		{
			bloomFilterSize += filter.tables[i].length;
		}
		
		double dataSize = numInserts*dataLength;
		
		System.out.println("The space taken up by this bloom filter is " + (bloomFilterSize/1024.0) + " kilobytes.");
		System.out.println("The space taken up by the data inserted is " + (dataSize/1024.0) + " kilobytes.");
		System.out.println("The bloom filter is " + (bloomFilterSize/dataSize) + " times the size of the data.");
	}
	
	/*
		Returns a random string of lowercase letters of length length.
	*/
	public static String randomString(int length)
	{
		StringBuilder result = new StringBuilder(length);
		for(int i = 0; i < length; i++)
		{
			result.append((char)(((int)(Math.random()*26))+'a'));
		}
		return result.toString();
	}
}
