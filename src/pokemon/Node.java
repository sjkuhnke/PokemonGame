package pokemon;

import java.io.Serializable;

public class Node implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Move data;
    public Node next;

    public Node(Move data) { this.data = data; }

	public void addToEnd(Node node) {
		Node n = this;
		while (n.next != null) {
			n = n.next;
		}
		n.next = node;
	}
	
	public String toString() {
		return data.toString();
	}

}
