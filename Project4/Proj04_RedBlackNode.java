
public class Proj04_RedBlackNode<K extends Comparable, V> {

	public K key;
	public V value;
	public int count;
	public char color;
	public Proj04_RedBlackNode<K, V> left, right;

	public Proj04_RedBlackNode(K key, V value) {
		this.key = key;
		this.value = value;
		this.count = 1;
		this.color = 'r';

	}

}
