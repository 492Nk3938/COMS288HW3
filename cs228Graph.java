package cs228hw4.graph;

import java.util.Iterator;
import java.util.Set;
import java.util.*;

public class cs228Graph<V> implements DiGraph<V> {

	// TO get the cost it is the first value to the secound value i.e.
	// myGraph[start][stop]
	ArrayList<ArrayList<Integer>> myGraph;
	HashMap<V, Integer> VtoIndex;
	HashMap<Integer, V> IndextoV;
	
	
	
	
	public String toString() {
		String returnString = "  ";
		
		for(int i = 0; i < this.myGraph.size(); i++){
			returnString += this.IndextoV.get(i) + "  ";
		}
		returnString += "\n";
		
		
		for(int i = 0; i < this.myGraph.size(); i++){
			returnString += this.IndextoV.get(i);
			returnString += this.myGraph.get(i).toString();
			returnString += "\n";
		}
		
		
		
		
		
		
		
		return returnString;
	}
	
	
	

	public cs228Graph () {
		myGraph = new ArrayList<ArrayList<Integer>>();
		VtoIndex = new HashMap<V, Integer>();
		IndextoV = new HashMap<Integer, V>() ;
	}
	
	
	public void addEdge(int cost, V v1, V v2) {
		this.validate(v1,v2);
		
		
		myGraph.get(VtoIndex.get(v1)).set(VtoIndex.get(v2), cost);
		
		
		
		
		
		
		
	}
	
	
	public void addVertex(V v1) {
		
		this.VtoIndex.put(v1, this.myGraph.size());
		this.IndextoV.put(this.myGraph.size(), v1);
		ArrayList<Integer> newArr = new ArrayList<Integer>();
		
		
		
		
		for(int i = 0; i < myGraph.size(); i++) {
			newArr.add( -1);
		}
		
		
		
		myGraph.add(newArr);
		
		for(ArrayList<Integer> a : myGraph) {
			a.add(-1);
		}
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Retrieves the collection of vertices that terminate an edge for which the
	 * given vertex is the initial vertex.
	 * 
	 * @param vertex the vertex for which the neighbors are desired
	 * @return a Collection of the neighbors of the given vertex
	 * @throws IllegalArgumentException if the given vertex does not exist in the
	 *                                  graph or is null
	 */
	@Override
	public Set<? extends V> getNeighbors(V vertex) {
		validate(vertex);

		Set<V> returnSet = new HashSet<V>();
		int indexOfVertex = VtoIndex.get(vertex);

		// This iterates over all of the given vertexes colum and returns any value
		// thats above -1 which is where it is set if it isn't a neibor
		for (int i = 0; i < this.myGraph.get(indexOfVertex).size(); i++) {

			if (this.myGraph.get(indexOfVertex).get(i) > -1) {
				returnSet.add(this.IndextoV.get(i));
			}

		}

		return returnSet;
	}

	
	
	
	private void validate(V vertex) {
		if (vertex == null || !VtoIndex.containsKey(vertex)) {
			throw new IllegalArgumentException();
		}
	}

	
	
	
	private void validate(V vertex1, V vertex2) {
		if (vertex1 == null || vertex2 == null || !VtoIndex.containsKey(vertex1)
				|| !VtoIndex.containsKey(vertex2)) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Retrieves the cost of the edge between the given vertices.
	 * 
	 * @param start the initial vertex of the edge
	 * @param end   the terminal vertex of the edge
	 * @return the cost of this edge
	 * @throws IllegalArgumentException if either of the given nodes does not exist
	 *                                  or is null or they do exist but there is no
	 *                                  edge between them
	 */
	@Override
	public int getEdgeCost(V start, V end) {
		validate(start, end);

		int indexStart = this.VtoIndex.get(start);
		int indexEnd = this.VtoIndex.get(end);

		if (this.myGraph.get(indexStart).get(indexEnd) == -1) {
			throw new IllegalArgumentException();
		}

		return this.myGraph.get(indexStart).get(indexEnd);
	}

	
	/**
	 * Get the number of vertices in this graph.
	 * 
	 * @return the number of vertices in this graph
	 */

	@Override
	public int numVertices() {

		return VtoIndex.size();
	}

	/**
	 * Returns an iterator over the vertices in this graph. This iterator will not
	 * allow modification of the graph.
	 * 
	 * @see java.lang.Iterable#iterator()
	 */

	@Override
	public Iterator<V> iterator() {
		return VtoIndex.keySet().iterator();
	}

}
