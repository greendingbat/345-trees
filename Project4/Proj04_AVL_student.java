import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Proj04_AVL_student<K extends Comparable<K>, V> implements Proj04_Dictionary<K, V> {

	private String debugStr;
	private Proj04_AVLNode<K, V> root;
	private int dotNum;

	public Proj04_AVL_student(String string) {
		this.root = null;
		this.debugStr = string;
		this.dotNum = 1;

	}

	@Override
	public void set(K key, V value) {
		if (get(key) == null) { // key isn't in tree
			root = set_helper(root, key, value, false);
		} else { // key is in tree
			root = set_helper(root, key, value, true);
		}

	}

	private Proj04_AVLNode<K, V> set_helper(Proj04_AVLNode<K, V> curr, K key, V value, boolean alreadyIn) {
		if (curr == null) { // tree was empty, create new node
			return new Proj04_AVLNode<K, V>(key, value);
		} else if (curr.key.compareTo(key) == 0) { // key is already in tree, overwrite value
			curr.value = value;
		} else if (curr.key.compareTo(key) > 0) { // node's key is greater, traverse left
			if (!alreadyIn) {
				curr.count++;
			}
			curr.left = set_helper(curr.left, key, value, alreadyIn);
			rebalanceSet(curr);

		} else { // node's key is smaller, traverse right
			if (!alreadyIn) {
				curr.count++;
			}
			curr.right = set_helper(curr.right, key, value, alreadyIn);
			rebalanceSet(curr);
		}

		return curr;
	}

	////// BALANCE METHODS \\\\\\
	private void rebalanceSet(Proj04_AVLNode<K, V> curr) {
		if (curr == null) {
			return;
		}
		if (height(curr.left) - height(curr.right) > 1) { // Left subtree bigger. Rotate right to fix
			if (heavySide(curr) != heavySide(curr.left)) {
				// Rotate left at curr's grandchild (two rotation case)
				rotateLeft(curr.left);
			}
			// Rotate right at curr
			rotateRight(curr);

		} else if (height(curr.left) - height(curr.right) < -1) { // Right subtree bigger. Rotate left to fix
			if (heavySide(curr) != heavySide(curr.right)) {
				// Rotate right at curr's grandchild (two rotation case)
				rotateRight(curr.right);
			}
			// Rotate left at curr
			rotateLeft(curr);
		} else { // Tree is balanced at this node
			return;
		}

	}

	private void rebalanceRemove(Proj04_AVLNode<K, V> curr) {
		if (curr == null) {
			return;
		}
		if (height(curr.left) - height(curr.right) > 1) { // Left subtree bigger. Rotate right to fix
			if (heavySide(curr.left) == '=') {
				// "Get away" with single rotation
				rotateRight(curr);
			} else {
				rebalanceSet(curr);	// This is sloppy, I know			
			}

		} else if (height(curr.left) - height(curr.right) < -1) { // Right subtree bigger. Rotate left to fix
			if (heavySide(curr.right) == '=') {
				// "Get away" with single rotation
				rotateLeft(curr);
			} else {
				rebalanceSet(curr); // Sloppy to have the same code twice, especially when one version calls the other
			}

		} else { // Tree is balanced at this node
			return;
		}

	}

	private int height(Proj04_AVLNode<K, V> curr) {
		if (curr == null) {
			return -1;
		} else if (curr.count == 1) {
			return 0;
		} else {
			return 1 + Math.max(height(curr.left), height(curr.right));
		}
	}

	private char heavySide(Proj04_AVLNode<K, V> curr) {
		if (height(curr.left) > height(curr.right)) {
			return '>';
		} else if (height(curr.left) < height(curr.right)) {
			return '<';
		} else {
			return '=';
		}

	}

	@Override
	public V get(K key) {
		return get_helper(root, key);
	}

	private V get_helper(Proj04_AVLNode<K, V> curr, K key) {
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
		if (get(key) == null) {
			return;
		}

		root = remove_helper(root, key);

	}

	private Proj04_AVLNode<K, V> remove_helper(Proj04_AVLNode<K, V> curr, K key) {
		if (curr == null) { // node was null. either tree was empty or the value was not found
			return null;
		} else if (curr.key.compareTo(key) < 0) { // node's key is smaller than search key, traverse right
			curr.count--;
			curr.right = remove_helper(curr.right, key);
			rebalanceRemove(curr);
		} else if (curr.key.compareTo(key) > 0) { // node's key is greater than search key, traverse left
			curr.count--;
			curr.left = remove_helper(curr.left, key);
			rebalanceRemove(curr);
		} else { // node was found, remove it
			if (curr.left == null && curr.right == null) {
				// CASE 1: Node is a leaf
				curr = null;
			} else if (curr.left == null) {
				// CASE 2: Node has 1 child (right)
				curr = curr.right;
			} else if (curr.right == null) {
				// CASE 2: Node has 1 child (left)
				curr = curr.left;
			} else {
				// CASE 3: Node has 2 children
				Proj04_AVLNode<K, V> pred = findPred(curr.left);
				curr.key = pred.key;
				curr.value = pred.value;
				curr.count--;
				curr.left = remove_helper(curr.left, pred.key);
				rebalanceRemove(curr);

			}
		}
		return curr;
	}

	private Proj04_AVLNode<K, V> findPred(Proj04_AVLNode<K, V> root) {
		if (root.right == null) {
			return root;
		} else {
			return findPred(root.right);
		}
	}

	@Override
	public int getSize() {
		if (root == null) {
			return 0;
		} else {
			return root.count;
		}
	}

	////// ROTATION METHODS \\\\\\

	public void rotateLeft(Proj04_AVLNode<K, V> at) {
		if (at == null) {
			return;
		}
		root = rotateLeft_helper(at);
	}

	private Proj04_AVLNode<K, V> rotateLeft_helper(Proj04_AVLNode<K, V> curr) {

		if (curr.right != null) {
			// create pointers to subtrees
			Proj04_AVLNode<K, V> subtreeOne = curr.left;
			Proj04_AVLNode<K, V> subtreeTwo = curr.right.left;
			Proj04_AVLNode<K, V> subtreeThree = curr.right.right;
			int newCount = 1;
			if (subtreeOne != null) {
				newCount += subtreeOne.count;
			}
			if (subtreeTwo != null) {
				newCount += subtreeTwo.count;
			}

			curr.left = new Proj04_AVLNode<K, V>(curr.key, curr.value);
			curr.left.count = newCount;
			curr.left.left = subtreeOne;
			curr.left.right = subtreeTwo;

			// copy right child up to curr
			curr.key = curr.right.key;
			curr.value = curr.right.value;
			curr.right = subtreeThree;

		}
		return curr;
	}

	public void rotateRight(Proj04_AVLNode<K, V> at) {
		if (at == null) {
			return;
		}
		root = rotateRight_helper(at);
	}

	private Proj04_AVLNode<K, V> rotateRight_helper(Proj04_AVLNode<K, V> curr) {

		if (curr.left != null) {
			// create pointers to subtrees
			Proj04_AVLNode<K, V> subtreeOne = curr.left.left;
			Proj04_AVLNode<K, V> subtreeTwo = curr.left.right;
			Proj04_AVLNode<K, V> subtreeThree = curr.right;
			int newCount = 1;
			if (subtreeTwo != null) {
				newCount += subtreeTwo.count;
			}
			if (subtreeThree != null) {
				newCount += subtreeThree.count;
			}

			curr.right = new Proj04_AVLNode<K, V>(curr.key, curr.value);
			curr.right.count = newCount;
			curr.right.left = subtreeTwo;
			curr.right.right = subtreeThree;

			// copy left child up to curr
			curr.key = curr.left.key;
			curr.value = curr.left.value;
			curr.left = subtreeOne;
		}
		return curr;
	}

	@Override
	public void inOrder(K[] keysOut, V[] valuesOut, String[] auxOut) {
		inOrder_helper(root, 0, keysOut, valuesOut, auxOut);

	}

	private int inOrder_helper(Proj04_AVLNode<K, V> curr, int index, K[] keysOut, V[] valuesOut, String[] auxOut) {
		if (curr == null) {
			return 0;
		} else {
			if (curr.left != null) {
				index = inOrder_helper(curr.left, index, keysOut, valuesOut, auxOut);
			}
			keysOut[index] = curr.key;
			valuesOut[index] = curr.value;
			auxOut[index] = "h=" + height(curr);
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

	private int postOrder_helper(Proj04_AVLNode<K, V> curr, int index, K[] keysOut, V[] valuesOut, String[] auxOut) {
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
			auxOut[index] = "h=" + height(curr);
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

	private void printNode(Proj04_AVLNode<K, V> curr, PrintWriter out) {
		if (curr == null) {
			return;
		}

		String nodeName = "node_" + curr.hashCode();
		out.println("   " + nodeName + " [label=\"" + curr.key + "\\n" + curr.value + "\\ncount=" + curr.count
				+ "\\nheight=" + height(curr) + "\"];");
		if (curr.left != null) {
			String leftName = "node_" + curr.left.hashCode();
			out.println("   " + nodeName + " -> " + leftName + " [label=L taildir=sw];");
		} else if (curr.left == null && curr.right != null) {
			String leftName = nodeName + "_L";
			out.println("   " + leftName + " [label=nullLeft style=invis];");
			out.println("   " + nodeName + " -> " + leftName + " [style=invis];");
		}
		if (curr.right != null) {
			String rightName = "node_" + curr.right.hashCode();
			out.println("   " + nodeName + " -> " + rightName + " [label=R taildir=se];");
		} else if (curr.right == null && curr.left != null) {
			String rightName = nodeName + "_R";
			out.println("   " + rightName + " [label=nullRight style=invis];");
			out.println("   " + nodeName + " -> " + rightName + " [style=invis];");
		}
		printNode(curr.left, out);
		printNode(curr.right, out);
	}

}
