import java.lang.reflect.Array;

public class GenSet<T extends Comparable<T>> {
	private T[] genericSet;
	private int[] indexMapping;

	@SuppressWarnings("unchecked")
	public GenSet(int arrayLength) {
	    genericSet = (T[]) Array.newInstance((Class<T>) Comparable.class, arrayLength);
	    newIndexMapping();
	}
	
	public GenSet(T[] TArray) {
	    genericSet = TArray;
	    newIndexMapping();
	}

	public T get(int index) {
	    return genericSet[index];
	}
	
	public void set(int index, T newValue) {
	    genericSet[index] = newValue;
	}
	
	public int size() {
		return genericSet.length;
	}
	
	public void switchIndexMapping(int i, int j) {
		int temp = indexMapping[i];
		
		indexMapping[i] = indexMapping[j];
		indexMapping[j] = temp;
	}
	
	public int getIndexMapping(int i) {
		return indexMapping[i];
	}
	
	public void setIndexMapping(int i, int newIndex) {
		indexMapping[i] = newIndex;
	}
	
	public void newIndexMapping() {
		indexMapping = new int[this.size()];
		
		for (int i = 0; i < this.size(); i++) {
			indexMapping[i] = i;
		}
	}
	
	public GenSet<T> rearrange() {
		GenSet<T> newGenSet = new GenSet<T>(this.size());
		
		for (int i = 0; i < this.size(); i++) {
			newGenSet.set(i, genericSet[indexMapping[i]]);
		}
		
		return newGenSet;
	}
	
	public void print() {
		System.out.print("{");
		
		for (int i = 0; i < this.size() - 1; i++) {
			System.out.print(this.get(i).toString() + ", ");
		}
		
		System.out.println(this.get(this.size() - 1).toString() + "}");
	}
}
