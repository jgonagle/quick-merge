import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class QuickMerge<T extends Comparable<T>> {
	GenSet<T> gArray;
	
	//--normal merge sort, but merge operation done by inserting part 
	//  of one sorted set (the wedge set) into the other sorted set (the split set)
	//--the uninserted part of the wedge set and the "lower" half of 
	//  the split set form a new set to be merged, while the inserted part 
	//  of the wedge set and the "higher" half of the split set form a 
	//  different new set to be merged
	//--takes a GenSet of Comparable type as argument
	//--all operations are done on an associative array of indices of
	//  the original set so that elements are only moved once (at the end)
	//  in memory
	public int quickMergeSort(GenSet<T> gArray) {
		this.gArray = gArray;
			
		return QMSort(0, gArray.size() - 1);
	}
	
	//--the sort operation
	//--takes first and last indices of set to be sorted, inclusive, as arguments
	//--returns number of comparisons made
	private int QMSort(int first, int last) {
		//size of the first half of the set to be sorted, will be used
		//to split the total set into a wedge set and an split set
		int halfSize = (last - first + 1) / 2;
		int numComparisons = 0;
		
		//while there's still something to sort
		if (first < last) {		
			numComparisons += QMSort(first, first + halfSize - 1) +
							  QMSort(first + halfSize, last) +
							  quickMergeBinary(first, first + halfSize - 1, last);
		}
		
		return numComparisons;
	}
	
	//--the merge operation
	//--takes first and last indices of set to be sorted, inclusive, as arguments
	//--takes "middle" index, denoting the edge between the wedge set and the 
	//  split set, as an argument
	//--returns number of comparisons made
	private int quickMergeBinary(int first, int middle, int last) {
		int[] searchResults;
		
		//index of the middle of the wedge set
		int queryIndex;
		//index of the first and last elements of the 
		//split set (to be binary searched)
		int searchStart;
		int searchEnd;
		
		int numComparisons = 0;

		int firstSize = middle - first + 1;
		int secondSize = last - middle;
		
		if ((firstSize > 0) && (secondSize > 0)) {
			//makes sure the wedge set is larger than the split 
			//set (maintains minimum pivot ratio of 1:2)
			if (secondSize > firstSize) {
				//swap the starting indices of the wedge set and the split set
				//using constant memory
				rotateInPlace(first, middle, last);
				
				//update middle index
				middle = last - (middle - first + 1);
			}
			
			//the middle of the wedge set (used for comparisons in
			//the binary search of the split set)
			queryIndex = (first + middle) / 2;
			//start of the split set
			searchStart = middle + 1;
			//end of the split set
			searchEnd = last;
			
			//returns the index of the element in the split set closest
			//to the middle element of the wedge set as well as the
			//number of comparisons required
			searchResults = binaryQMSearch(queryIndex,
										   searchStart,
										   searchEnd);
			
			//determine the size of the "lower" half of the split set
			int rankAdjustment = searchResults[0] - searchStart;
			
			//switch the starting indices of the "higher" half of the wedge set
			//and the "lower" half of the split set
			rotateInPlace(queryIndex, middle, searchResults[0] - 1);
			
			//continue the merge operation on both "halves" created by the
			//wedge-split of the original set
			numComparisons += searchResults[1] +
							  quickMergeBinary(first, queryIndex - 1, queryIndex - 1 + rankAdjustment) + 
							  quickMergeBinary(queryIndex + rankAdjustment + 1, middle + rankAdjustment, last);
		}	
		
		return numComparisons;
	}
	

	//--takes the middle of the wedge set (to compare to in binary search) as an argument
	//--takes first and last indices of set to be binary searched, inclusive, as arguments
	//--returns the index of the element in the split set closest
	//  to the middle element of the wedge set as well as the
	//  number of comparisons required
	private int[] binaryQMSearch (int queryIndex, int searchStart, int searchEnd) {
		int lowerBound = searchStart;
		int higherBound = searchEnd;
		int numComparisons = 0;
		
		while (lowerBound <= higherBound) {
			int compareIndex = (lowerBound + higherBound) / 2;
			
			if (gArray.get(gArray.getIndexMapping(queryIndex))
				.compareTo(gArray.get(gArray.getIndexMapping(compareIndex))) == 1) {
				/*
				System.out.println(gArray.get(gArray.getIndexMapping(queryIndex)) + " > " +
								   gArray.get(gArray.getIndexMapping(compareIndex)));
				*/
				lowerBound = compareIndex + 1;
			}
			else
			{
				/*
				System.out.println(gArray.get(gArray.getIndexMapping(queryIndex)) + " <= " +
								   gArray.get(gArray.getIndexMapping(compareIndex)));
				*/
				higherBound = compareIndex - 1;
			}
			
			numComparisons++;
		}
		
		return (new int[] {lowerBound, numComparisons});
	}
	
	//swap the starting indices of the two "halves" of the set
	//e.g. for rotateInPlace(0, 5, 9) on {2, 7, 9, 4, 3, 6, 5, 1, 8, 10}
	//would return {5, 1, 8, 10, 2, 7, 9, 4, 3, 6}
	private void rotateInPlace(int first, int middle, int last) {
		int firstSize = middle - first + 1;
		int secondSize = last - middle;
		
		//while there's something to swap
		if ((firstSize > 0) && (secondSize > 0)) {
			//make sure that we're swapping the smaller set so that we don't
			//have bounds errors
			if (firstSize <= secondSize) {
				//the number of times to cascade the swap of the smaller
				//set with the larger set
				int numReps = secondSize / firstSize;
				
				//actually perform the swap
				for (int i = 0; i < (numReps * firstSize); i++) {
					gArray.switchIndexMapping(first + i, middle + 1 + i);
				}
				
				//perform again now that "most" of the elements have been swapped
				rotateInPlace(first + (numReps * firstSize), 
							  last - (secondSize % firstSize), 
							  last);
			}
			else {
				int numReps = firstSize / secondSize;
				
				for (int i = 0; i < (numReps * secondSize); i++) {
					gArray.switchIndexMapping(last - i, middle - i);
				}
				
				rotateInPlace(first, 
						  	  first - 1 + (firstSize % secondSize), 
						  	  last - (numReps * secondSize));
			}
		}		
	}
	
	//randomize the indices of the associative array of the elements 
	//of the input GenSet
	private static void randomizeGenSet(GenSet<Integer> rArray, boolean shuffled) {
		ArrayList<Integer> intList = new ArrayList<Integer>(rArray.size());
		
		for (int i = 0; i < rArray.size(); i++) {
			intList.add(i);
		}
		
		if (shuffled) {
			Collections.shuffle(intList);
			Collections.shuffle(intList);
			Collections.shuffle(intList);
			Collections.shuffle(intList);
			Collections.shuffle(intList);
		}
		
		for (int i = 0; i < intList.size(); i++) {
			rArray.set(i, intList.get(i));
		}
	}
	

	public static void main(String[] args) {
		QuickMerge<Integer> sorter = new QuickMerge<Integer>();
		
		long start, end;
		int randomComparisonsMade = 0, sortedComparisonsMade = 0, reversedComparisonsMade = 0;
                double randomTime = 0.0, sortedTime = 0.0, reversedTime = 0.0;
		int setSize = 1000000;
		int numRuns = 30;
		
		GenSet<Integer> randomTestSet = new GenSet<Integer>(setSize);
                GenSet<Integer> sortedTestSet = new GenSet<Integer>(setSize);
                GenSet<Integer> reversedTestSet = new GenSet<Integer>(setSize);
		
		for (int  n = 0; n < numRuns; n++) {
		
                    randomizeGenSet(randomTestSet, true);
                    
                    Integer[] sortedIntegerArray = new Integer[setSize];
                    Integer[] reversedIntegerArray = new Integer[setSize];
                    
                    for (int i = 0; i < setSize; i++) {
                        sortedIntegerArray[i] = i;
                        reversedIntegerArray[i] = setSize - i - 1;                    
                    }
                    
                    sortedTestSet = new GenSet<Integer>(sortedIntegerArray);
                    reversedTestSet = new GenSet<Integer>(reversedIntegerArray);
                    
                    start = System.currentTimeMillis();
                    randomComparisonsMade += sorter.quickMergeSort(randomTestSet);
                    end = System.currentTimeMillis();
                    randomTime += (end - start) / 1000.0;                    
                    
                    start = System.currentTimeMillis();
                    sortedComparisonsMade += sorter.quickMergeSort(sortedTestSet);
                    end = System.currentTimeMillis();
                    sortedTime += (end - start) / 1000.0;
                                        
                    start = System.currentTimeMillis();
                    reversedComparisonsMade += sorter.quickMergeSort(reversedTestSet);
                    end = System.currentTimeMillis();
                    reversedTime += (end - start) / 1000.0;                    
                }
                
                System.out.println(randomTestSet.size() + " randomized items sorted by QuickMerge in " + (randomComparisonsMade / numRuns) + " comparisons over " + (randomTime / numRuns) + " s on average");
                System.out.println(sortedTestSet.size() + " sorted items sorted by QuickMerge in " + (sortedComparisonsMade / numRuns) + " comparisons over " + (sortedTime / numRuns) + " s on average");
                System.out.println(reversedTestSet.size() + " reversed items sorted by QuickMerge in " + (reversedComparisonsMade / numRuns) + " comparisons over " + (reversedTime / numRuns) + " s on average");
	}
}
