package cs228hw4.graph;

import java.util.*;

public class CS228Dijkstra<S> implements Dijkstra<S> {

	private DiGraph<S> myGraph;
	private boolean hasRun;
	private HashMap<S, Node> vertices;

	public CS228Dijkstra(DiGraph<S> input) {
		this.myGraph = input;
		this.hasRun = false;
		vertices = new HashMap<S, Node>();

		setVertices();

	}

	private void setVertices() {
		Iterator<S> iter = this.myGraph.iterator();
		while (iter.hasNext()) {
			S temp = iter.next();
			vertices.put(temp, new Node(temp, Integer.MAX_VALUE));

		}

		for (S k : this.vertices.keySet()) {
			Node n = this.vertices.get(k);
			n.pred = null;
			n.distance = Integer.MAX_VALUE;
			n.procesed = false;
			n.added = false;
		}

	}


	
	public class Node {
		private S data;
		private Node pred;
		private int distance;
		private boolean procesed;
		private boolean added;

		public String toString() {
			String returnString = "data: " + this.data;
			if (pred == null) {
				returnString += "\npred: " + this.pred;
			} else {
				returnString += "\npred: " + this.pred.data;
			}
			returnString += "\nDistance: " + this.distance;
			returnString += "\nprocesed: " + this.procesed;
			returnString += "\nadded: " + this.added;
			returnString += "\n\n";

			return returnString;
		}

		private Node(S dataIn, int dist) {
			this.data = dataIn;
			this.distance = dist;
			this.pred = null;
			this.procesed = false;
			this.added = false;

		}

		private Comparator<Node> getComparator() {
			return new Compare();

		}

		
		public boolean equals (Node n) {
			
			
			
			return this.data.equals(n.data);
		}
		
		private class Compare implements Comparator<Node> {

			@Override
			public int compare(CS228Dijkstra<S>.Node o1, CS228Dijkstra<S>.Node o2) {

				return o1.distance - o2.distance;
			}
			
			
			

		}

	}

	/**
	 * Uses Dijkstra's shortest path algorithm to calculate and store the shortest
	 * paths to every vertex in the graph as well as the total costs of each of
	 * those paths. This should run in O(E log V) time, where E is the size of the
	 * edge set, and V is the size of the vertex set.
	 * 
	 * @param start the vertex from which shortest paths should be calculated
	 */
	@Override
	public void run(S startInput) {
		if (startInput == null || myGraph == null || !this.vertices.containsKey(startInput)) {
			throw new IllegalArgumentException();
		}
		
		//This has to run because it is assumed that everything is a certain way at the begining of the program
		if(this.hasRun) {this.setVertices();}
		
		Node temp = getNode(startInput);
		temp.distance = 0;
		temp.pred = null;
		temp.procesed = true;
		temp.added = true;

		PriorityQueue<Node> que = new PriorityQueue<Node>(temp.getComparator());
		

		que.add(this.getNode(startInput));

		while (!que.isEmpty()) {
			Node location = que.poll();

			Set<? extends S> neighborSet = myGraph.getNeighbors(location.data);

			Iterator<? extends S> iter = neighborSet.iterator();

			while (iter.hasNext()) {
				Node adjacent = getNode(iter.next());
				if (!adjacent.procesed) {

					if ((long) adjacent.distance > (long) location.distance
							+ (long) this.myGraph.getEdgeCost(location.data, adjacent.data)) {
						adjacent.distance = location.distance + this.myGraph.getEdgeCost(location.data, adjacent.data);
						adjacent.pred = location;
						
						
						if (!adjacent.added) {
							adjacent.added = que.add(adjacent);
						} else {
							que.remove(adjacent);
							que.add(adjacent);
						}

					}


					

				}
					
			}
			location.procesed = true;
		}

		this.hasRun = true;
	}



	private CS228Dijkstra<S>.Node getNode(S dataOfNode) {

		return this.vertices.get(dataOfNode);
	}

	/**
	 * Retrieve, in O(V) time, the pre-calculated shortest path to the given node.
	 * 
	 * @param vertex the vertex to which the shortest path, from the start vertex,
	 *               is being requested
	 * @return a list of vertices comprising the shortest path from the start vertex
	 *         to the given destination vertex, both inclusive or a null value if a path doesn't exist or you havn't run the program yet to calculate paths and distances
	 */

	@Override
	public List<S> getShortestPath(S vertex) {
		
		if(!this.hasRun ) {return null;}
		
		try {
			this.myGraph.getNeighbors(vertex);
		}catch (NullPointerException e) {
			return null;
		}
		
		if(this.vertices.get(vertex).distance == Integer.MAX_VALUE) {
			return null;
		}
		
		
		LinkedList<S> returnList = new LinkedList<S>();

		// this finds the vertex index and then gets the node from the list of nodes
		Node cur = this.vertices.get(vertex).pred;

		// A check just in case
		if (!this.vertices.get(vertex).data.equals(vertex)) {
			System.out.println("error in desgin of storing and indexing the nodes");
			return null;
		}

		returnList.addFirst(vertex);

		while (cur != null) {
			returnList.addFirst(cur.data);
			if(cur.distance == 0 ) {
				break;
			}
			if(cur.distance == Integer.MAX_VALUE || cur.distance < 0) {
				return null;
			}
			cur = cur.pred;
		}


		return returnList;
	}



	/**
	 * Retrieve, in constant time, the total cost to reach the given vertex from the
	 * start vertex via the shortest path. If there is no path, this value is
	 * Integer.MAX_VALUE. Integer.MAX_VALUE is also returned if the program hasn't run yet.
	 * 
	 * @param vertex the vertex to which the cost of the shortest path, from the
	 *               start vertex, is desired
	 * @return the cost of the shortest path to the given vertex from the start
	 *         vertex or Integer.MAX_VALUE if there is no path or if the node doesn't exsit
	 */
	@Override
	public int getShortestDistance(S vertex) {
		if(!this.hasRun) {return Integer.MAX_VALUE;}
		try {
			this.myGraph.getNeighbors(vertex);
		}catch (NullPointerException e) {
			return Integer.MAX_VALUE;
		}
		return this.vertices.get(vertex).distance;
	}

}
