import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Proj04_RedBlack_student<K extends Comparable<K>, V> implements Proj04_Dictionary<K, V> {
	private String debugStr;
	private Proj04_RedBlackNode<K, V> root;
	private int dotNum;
	private int size;

	public Proj04_RedBlack_student(String string, Proj04_234Node<K, V> root) {
		this.debugStr = string;
		this.size = 0;
		this.dotNum = 1;
		this.root = null;
		
		insertFrom234(root);
		
		// Create RB nodes for all children that exist
		
		
	}

	private void insertFrom234(Proj04_234Node<K, V> curr) {
		// Create RB nodes for any keys that exist
		if (curr.key2 != null) {
			set(curr.key2, curr.val2);
		}
		if (curr.key1 != null) {
			set(curr.key1, curr.val1);
		}
		if (curr.key3 != null) {
			set(curr.key3, curr.val3);
		}
		
		if (curr.child1 != null) {
			insertFrom234(curr.child1);
		}
		if (curr.child2 != null) {
			insertFrom234(curr.child2);
		}
		if (curr.child3 != null) {
			insertFrom234(curr.child3);
		}
		if (curr.child4 != null) {
			insertFrom234(curr.child4);
		}

	}

	public Proj04_RedBlack_student(String string) {
		this.root = null;
		this.debugStr = string;
		this.dotNum = 1;
		this.size = 0;
	}

	@Override
	public void set(K key, V value) {
		root = set_helper(root, key, value);
		checkRedOnRed(root);
		root.color = 'b';

	}

	// ADDING NODE:
	// recurse down to insertion point, "splitting" any full nodes:
	// If curr is black and has two red children, bubble red up
	// Check for any red-on-red issues

	private Proj04_RedBlackNode<K, V> set_helper(Proj04_RedBlackNode<K, V> curr, K key, V value) {
		checkBubbleRedUp(curr);
		if (curr == null) { // tree was empty, create new node
			this.size++;
			return new Proj04_RedBlackNode<K, V>(key, value);
		} else if (curr.key.compareTo(key) == 0) { // key is already in tree, overwrite value
			curr.value = value;
		} else if (curr.key.compareTo(key) > 0) { // node's key is greater, traverse left
			curr.count++;
			curr.left = set_helper(curr.left, key, value);
			checkRedOnRed(curr);
		} else { // node's key is smaller, traverse right
			curr.count++;
			curr.right = set_helper(curr.right, key, value);
			checkRedOnRed(curr);
		}

		return curr;
	}

	// "Splitting" node
	private void checkBubbleRedUp(Proj04_RedBlackNode<K, V> curr) {
		if (curr == null) {
			return;
		}
		if (getColor(curr) == 'b' && getColor(curr.left) == 'r' && getColor(curr.right) == 'r') {
			curr.color = 'r';
			curr.left.color = 'b';
			curr.right.color = 'b';
		}
	}

	// Traverse and fix any red-on-red problems with rotation
	private void checkRedOnRed(Proj04_RedBlackNode<K, V> curr) {
		if (curr == null) {
			return;
		}

		if (curr.left != null) { // Check left subtree, if it exists
			if (getColor(curr.left) == 'r' && getColor(curr.left.left) == 'r') {
				// LL red case
				rotateRight(curr);
				curr.color = 'b';
			} else if (getColor(curr.left) == 'r' && getColor(curr.left.right) == 'r') {
				// LR red case
				rotateLeft(curr.left);
				rotateRight(curr);
				curr.color = 'b';

			}
		}

		if (curr.right != null) { // Check right subtree, if it exists
			if (getColor(curr.right) == 'r' && getColor(curr.right.right) == 'r') {
				// RR red case
				rotateLeft(curr);
				curr.color = 'b';
			} else if (getColor(curr.right) == 'r' && getColor(curr.right.left) == 'r') {
				// RL red case
				rotateRight(curr.right);
				rotateLeft(curr);
				curr.color = 'b';
			}
		}

	}

	/// ROTATION METHODS \\\
	public void rotateLeft(Proj04_RedBlackNode<K, V> at) {
		if (at == null) {
			return;
		}
		rotateLeft_helper(at);
	}

	private Proj04_RedBlackNode<K, V> rotateLeft_helper(Proj04_RedBlackNode<K, V> curr) {

		if (curr.right != null) {
			// create pointers to subtrees
			Proj04_RedBlackNode<K, V> subtreeOne = curr.left;
			Proj04_RedBlackNode<K, V> subtreeTwo = curr.right.left;
			Proj04_RedBlackNode<K, V> subtreeThree = curr.right.right;
			int newCount = 1;
			if (subtreeOne != null) {
				newCount += subtreeOne.count;
			}
			if (subtreeTwo != null) {
				newCount += subtreeTwo.count;
			}

			curr.left = new Proj04_RedBlackNode<K, V>(curr.key, curr.value);
			// curr.left.color = getColor(curr);
			curr.left.count = newCount;
			curr.left.left = subtreeOne;
			curr.left.right = subtreeTwo;

			// copy right child up to curr
			curr.key = curr.right.key;
			curr.color = getColor(curr.right);
			curr.value = curr.right.value;
			curr.right = subtreeThree;

		}
		return curr;
	}

	public void rotateRight(Proj04_RedBlackNode<K, V> at) {
		if (at == null) {
			return;
		}
		rotateRight_helper(at);
	}

	private Proj04_RedBlackNode<K, V> rotateRight_helper(Proj04_RedBlackNode<K, V> curr) {

		if (curr.left != null) {
			// create pointers to subtrees
			Proj04_RedBlackNode<K, V> subtreeOne = curr.left.left;
			Proj04_RedBlackNode<K, V> subtreeTwo = curr.left.right;
			Proj04_RedBlackNode<K, V> subtreeThree = curr.right;
			int newCount = 1;
			if (subtreeTwo != null) {
				newCount += subtreeTwo.count;
			}
			if (subtreeThree != null) {
				newCount += subtreeThree.count;
			}

			curr.right = new Proj04_RedBlackNode<K, V>(curr.key, curr.value);
			// curr.right.color = getColor(curr);
			curr.right.count = newCount;
			curr.right.left = subtreeTwo;
			curr.right.right = subtreeThree;

			// copy left child up to curr
			curr.key = curr.left.key;
			curr.color = getColor(curr.left);
			curr.value = curr.left.value;
			curr.left = subtreeOne;

		}
		return curr;
	}

	@Override
	public V get(K key) {
		return get_helper(root, key);
	}

	private V get_helper(Proj04_RedBlackNode<K, V> curr, K key) {
		if (curr == null) { // node was null. either tree was empty or the value was not found
			return null;
		} else if (curr.key.compareTo(key) == 0) { // key is found, return value
			return (V) curr.value;
		} else if (curr.key.compareTo(key) > 0) { // node's key is greater than search key, traverse left
			return get_helper(curr.left, key);
		} else { // node's key is smaller than search key, traverse right
			return get_helper(curr.right, key);
		}
	}

	@Override
	public void remove(K key) {
		return;
	}

	@Override
	public int getSize() {
		return this.size;
	}

	private char getColor(Proj04_RedBlackNode<K, V> curr) {
		if (curr == null) {
			return 'b';
		} else {
			return curr.color;
		}

	}

	@Override
	public void inOrder(K[] keysOut, V[] valuesOut, String[] auxOut) {
		inOrder_helper(root, 0, keysOut, valuesOut, auxOut);

	}

	private int inOrder_helper(Proj04_RedBlackNode<K, V> curr, int index, K[] keysOut, V[] valuesOut, String[] auxOut) {
		if (curr == null) {
			return 0;
		} else {
			if (curr.left != null) {
				index = inOrder_helper(curr.left, index, keysOut, valuesOut, auxOut);
			}
			keysOut[index] = curr.key;
			valuesOut[index] = curr.value;
			auxOut[index] = "" + curr.color;
			index++;
			if (curr.right != null) {
				index = inOrder_helper(curr.right, index, keysOut, valuesOut, auxOut);
			}
			return index;
		}
	}

	@Override
	public void postOrder(K[] keysOut, V[] valuesOut, String[] auxOut) {
		postOrder_helper(root, 0, keysOut, valuesOut, auxOut);

	}

	private int postOrder_helper(Proj04_RedBlackNode<K, V> curr, int index, K[] keysOut, V[] valuesOut,
			String[] auxOut) {
		if (curr == null) {
			return 0;
		} else {
			if (curr.left != null) {
				index = postOrder_helper(curr.left, index, keysOut, valuesOut, auxOut);
			}
			if (curr.right != null) {
				index = postOrder_helper(curr.right, index, keysOut, valuesOut, auxOut);
			}
			keysOut[index] = curr.key;
			valuesOut[index] = curr.value;
			auxOut[index] = "" + curr.color;
			index++;
			return index;
		}
	}

	@Override
	public void genDebugDot() {
		// This code generates a .dot file representing the current state of the heap.
		// Nodes are represented as their indices, with their content value displayed as
		// a label.
		// This is to keep wonky things from happening with duplicate values
		String fileName = debugStr + "_" + dotNum + ".dot";
		File dotFile = new File(fileName);
		PrintWriter out;
		try {
			out = new PrintWriter(dotFile);
			out.println("digraph {");

			printNode(root, out);

			out.println("}");
			out.close();
			dotNum++;
		} catch (FileNotFoundException e) {
			System.out.println("could not create " + fileName);
		}

	}

	private void printNode(Proj04_RedBlackNode<K, V> curr, PrintWriter out) {
		if (curr == null) {
			return;
		}
		String colorStr;
		String fontStr;
		if (curr.color == 'r') {
			colorStr = "red";
			fontStr = "black";
		} else {
			colorStr = "black";
			fontStr = "white";
		}

		String nodeName = "node_" + curr.hashCode();
		out.println("   " + nodeName + " [style=filled fillcolor=" + colorStr + " fontcolor=" + fontStr + " label=\""
				+ curr.key + "\\n" + curr.value + "\"];");
		if (curr.left != null) {
			String leftName = "node_" + curr.left.hashCode();
			out.println("   " + nodeName + " -> " + leftName + " [label=L taildir=sw];");
		} else {
			String leftName = nodeName + "_L";
			out.println("   " + leftName + " [label=nullLeft style=invis];");
			out.println("   " + nodeName + " -> " + leftName + " [style=invis];");
		}
		if (curr.right != null) {
			String rightName = "node_" + curr.right.hashCode();
			out.println("   " + nodeName + " -> " + rightName + " [label=R taildir=se];");
		} else {
			String rightName = nodeName + "_R";
			out.println("   " + rightName + " [label=nullRight style=invis];");
			out.println("   " + nodeName + " -> " + rightName + " [style=invis];");
		}
		printNode(curr.left, out);
		printNode(curr.right, out);
	}

}
