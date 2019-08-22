
public class Proj04_AVLNode <K extends Comparable, V> {
	
	public K key;
	public V value;
	public int count;
	public Proj04_AVLNode<K, V> left, right;
	
	public Proj04_AVLNode(K key, V value) {
		this.key = key;
		this.value = value;
		this.count = 1;
	
	}
	

	
	
}
