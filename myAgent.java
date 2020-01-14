package cs228hw4.game;

import java.awt.Color;
import java.io.File;
import java.util.*;

import cs228hw4.graph.*;
import edu.iastate.cs228.game.Agent;
import edu.iastate.cs228.game.GalaxyState;
import edu.iastate.cs228.game.SystemState;

public class myAgent implements Agent {
	private Color myColor;
	private Color opColor;
	private GalaxyState galState;
	private Queue<AgentAction> actionQue;
	private AgentAction[] returnArray;
	private int myEnergy;
	private boolean firstTurn;
	private List<SystemState> mySystems;
	private List<SystemState> opSystems;
	private List<SystemState> nSystems;
	private final int minimumEnergyStore = 3;
	private SystemState myLocation;
	private boolean increasedScan;
	private DiGraph<SystemState> galGraph;
	private int sysSize;
	private boolean wasKilled;
	private boolean debug = true;
	private int biasTowardStaying = 2;

	public myAgent() {
		myColor = new Color(0);
		opColor = new Color(1);
		this.galState = null;
		this.mySystems = new LinkedList<>();
		this.opSystems = new LinkedList<>();
		this.nSystems = new LinkedList<>();
		this.actionQue = new LinkedList<>();
		this.myEnergy = 0;
		this.firstTurn = true;
		this.returnArray = new AgentAction[3];
	}

	public void reSetUp() {
		this.galState = null;
		this.mySystems = new LinkedList<>();
		this.opSystems = new LinkedList<>();
		this.nSystems = new LinkedList<>();
		this.actionQue = new LinkedList<>();
		this.myEnergy = 0;
		this.firstTurn = true;
		this.returnArray = new AgentAction[3];

	}

	private int parseSize(String s) {
		char[] arr = s.toCharArray();
		String toReturn = "";

		for (int i = 1; i < arr.length; i++) {
			toReturn += arr[i];

		}
		try {
			return Integer.parseInt(toReturn);
		} catch (Exception e) {
			return 0;
		}
	}

	private void setUpFirstTurn() {
		this.firstTurn = false;
		this.sysSize = 20;

		setUp();
	}

	private DiGraph<SystemState> makeGraph(GalaxyState g) {
		cs228Graph<SystemState> toReturn = new cs228Graph<>();
		boolean tried = false;
		for (SystemState s : g.getSystems()) {
			if (this.sysSize < parseSize(s.getName())) {
				this.sysSize = parseSize(s.getName());
			}

			toReturn.addVertex(s);
		}

		for (SystemState s : g.getSystems()) {
			int[] c = s.getTravelCost();
			SystemState[] n = s.getNeighbors();

			if (c.length != n.length) {
				//if (debug)
					System.out.println("problems different amount of costs vs neibors");
			}
			for (int i = 0; i < c.length; i++) {
				if (c[i] == 0)
					continue;
				toReturn.addEdge(c[i], s, n[i]);
			}

		}

		return toReturn;

	}

	private void setUp() {
		this.galGraph = this.makeGraph(this.galState);
		Random rand = new Random();

		SystemState[] states = this.galState.getSystems();
		this.mySystems.clear();
		this.opSystems.clear();
		this.nSystems.clear();

		this.myLocation = galState.getCurrentSystemFor(myColor);

		for (SystemState s : states) {
			if (s.getOwner().equals(myColor)) {
				this.mySystems.add(s);
			} else if (s.getOwner().equals(opColor)) {
				this.opSystems.add(s);
			} else {
				this.nSystems.add(s);
			}

		}

		if (this.mySystems.size() / 1.5 > this.opSystems.size() && !this.increasedScan) {
			this.add(new SetScan(this.sysSize / 3), true);
			this.increasedScan = true;

		} else if (this.mySystems.size() / 1.5 > this.opSystems.size() && this.increasedScan && rand.nextInt(5) == 2) {
			this.add(new SetScan(this.sysSize / 3), true);
		}

		for (int i = 0; i < 3; i++) {

			this.returnArray[i] = this.actionQue.poll();

		}

	}

	@Override
	public File getAgentImage() {

		return null;
	}

	@Override
	public String getAgentName() {

		return "my Agent";
	}

	@Override
	public AgentAction[] getCommandTurnGrading(GalaxyState nextState, int energy) {
		if (debug)
			System.out.println("came here");

		if (wasKilled) {
			this.reSetUp();

		}

		this.wasKilled = true;

		if (energy < -4) {

			AgentAction[] temp = new AgentAction[3];
			temp[0] = new Capture(1);
			temp[1] = new ContinueCapture();
			temp[2] = new ContinueCapture();
			return temp;

		}

		this.returnArray = new AgentAction[3];

		try {
			// if(energy > 0)
			// throw new Exception();

			this.galState = nextState;
			this.myEnergy = energy;

			try {
				if (this.firstTurn) {
					setUpFirstTurn();
				} else {
					setUp();
				}
			} catch (Exception e) {
				if (debug)
					System.out.println("error in setup");
			}

			// this.checkToRefual();
///*

			if (debug)
				System.out.println("get moves");
			while (!full()) {
				this.checkToRefual();

				if (this.myEnergy < 5) {
					if (debug)
						System.out.println("energy Problems");
					if (this.onMySystem()) {
						add(new Refuel(), false);
						this.myEnergy += this.myLocation.getEnergyStored();
					}

					this.move(this.energyDepo());
					add(new Refuel(), false);
					this.myEnergy += this.myLocation.getEnergyStored();
					continue;

				} else if (this.opSystems.size() > 0) {
					if (debug)
						System.out.println("attack");

					try {
						this.move(this.weakest(this.myLocation, this.opSystems));
					} catch (Throwable t) {

						if (debug)
							System.out.println("can't find weakest op");
						add(new SetScan(this.myEnergy / 3), false);
						return panic();
					}

					this.fullCapture();
					continue;

				} else if (this.nSystems.size() > 0) {
					if (debug)
						System.out.println("grow stronger");
					try {
						this.move(this.weakest(this.myLocation, this.nSystems));

					} catch (Throwable t) {
						if (debug)
							System.out.println("\n\n\n\ncan't find weakest\n\n\n\n");
						add(new SetScan(this.myEnergy / 3), false);
						return panic();
					}
					this.fullCapture();
					continue;
				}
				if (debug)
					System.out.println("default");
				add(new SetScan(this.myEnergy / 3), false);

			}
			// */

			if (debug)
				System.out.println("good " + this.returnArray);
			if (debug)
				System.out.println(this.returnArray.length);
			if (debug)
				System.out.println(this.returnArray[0]);
			if (debug)
				System.out.println(this.returnArray[1]);
			if (debug)
				System.out.println(this.returnArray[2]);
			this.wasKilled = false;
			return this.returnArray;

		} catch (Exception e) {
			if (debug)
				System.out.println("\n\n\n\nerror" + e.toString());

			return panic();
		}

	}

	private AgentAction[] panic() {
		try {
			while (!this.full())
				if (debug)
					System.out.println("\n\n\n\n panic");
				add(new ContinueCapture(), false);

			return this.returnArray;
		} catch (Throwable t) {
			return Truepanic();
		}
	}

	private AgentAction[] Truepanic() {
		AgentAction[] toReturn = new AgentAction[3];
		toReturn[0] = new Capture(1);
		toReturn[1] = new ContinueCapture();
		toReturn[2] = new ContinueCapture();
		if (debug)
			System.out.println("Really bad " + toReturn);
		return toReturn;
	}

	private void move(List<SystemState> path) {

		for (int i = 1; i < path.size(); i++) {
			if (add(new Move(path.get(i).getName()), false)) {
				this.myLocation = path.get(i);
			}
		}

	}

	public class comp implements Comparator<SystemState> {
		CS228Dijkstra<SystemState> d;
		boolean toCapture;

		/**
		 * Creates a comparitor that checks which system is best for capture if op = 0
		 * or refule if op=1
		 * 
		 * @param inD
		 * @param toCapture
		 */
		public comp(CS228Dijkstra<SystemState> inD, boolean option) {

			this.d = inD;
			this.toCapture = option;

		}

		@Override
		public int compare(SystemState o1, SystemState o2) {

			if (this.toCapture) {
				return ((int) (d.getShortestDistance(o1) * biasTowardStaying) + o1.getCostToCapture())
						- ((int) (d.getShortestDistance(o2) * biasTowardStaying) + o2.getCostToCapture());
			} else {
				return (o1.getEnergyStored() - (int) (d.getShortestDistance(o1) * biasTowardStaying))
						- (o2.getEnergyStored() - (int) (d.getShortestDistance(o2) * biasTowardStaying));

			}

		}

	}

//*
	private List<SystemState> energyDepo() {

		CS228Dijkstra<SystemState> d = new CS228Dijkstra<SystemState>(this.galGraph);
		if (d == null) {
			if (debug)
				System.out.println("CS228Dijkstra problems");
		}

		d.run(this.myLocation);

		try {
			Comparator<? super SystemState> c = new comp(d, true);
			this.mySystems.sort(c);
		} catch (Throwable t) {

		}
		this.myEnergy -= d.getShortestDistance(this.mySystems.get(0));

		return d.getShortestPath(this.mySystems.get(0));

	}

	private List<SystemState> weakest(SystemState myLocation2, List<SystemState> targets) {

		CS228Dijkstra<SystemState> d = new CS228Dijkstra<SystemState>(this.galGraph);
		d.run(this.myLocation);
		try {
			Comparator<? super SystemState> c = new comp(d, true);
			targets.sort(c);
		} catch (Throwable t) {

		}

		this.myEnergy -= d.getShortestDistance(targets.get(0));

		return d.getShortestPath(targets.get(0));

	}
	// */

	private boolean onMySystem() {
		return this.myLocation.getOwner().equals(myColor);
	}

	private boolean full() {
		return this.returnArray[2] != null;
	}

	private boolean checkToRefual() {

		if (((this.myEnergy / 4 < this.myLocation.getEnergyStored() || this.myEnergy < this.minimumEnergyStore)
				&& this.onMySystem())) {
			add(new Refuel(), false);
			return true;
		}

		return false;

	}

	private boolean add(AgentAction a, boolean force) {

		if (this.returnArray[0] == null) {
			this.returnArray[0] = a;
			return true;
		} else if (this.returnArray[1] == null) {
			this.returnArray[1] = a;
			return true;
		} else if (this.returnArray[2] == null) {
			this.returnArray[2] = a;
			return true;
		} else if (force) {
			this.actionQue.add(a);
			return true;
		}
		return false;

	}

	private boolean fullCapture() {
		if (full() || onMySystem()) {
			return false;

		}
		if (this.myLocation.getCostToCapture() > this.myEnergy) {
			if (add(new Capture(this.myLocation.getCostToCapture()), false)) {
				for (int i = 0; i < this.myLocation.getCostToCapture() - (this.myEnergy); i++) {
					add(new ContinueCapture(), true);
				}
			}

		} else {
			add(new Capture(this.myLocation.getCostToCapture()), false);
		}

		return true;

	}

	@Override
	public AgentAction[] getCommandTurnTournament(GalaxyState arg0, int arg1) {

		AgentAction[] toReturn = (AgentAction[]) new Object[3];
		toReturn[0] = new ContinueCapture();
		toReturn[1] = new ContinueCapture();
		toReturn[2] = new ContinueCapture();
		return toReturn;

	}

	@Override
	public String getFirstName() {

		return "Nicholas";
	}

	@Override
	public String getLastName() {
		return "Krabbenhoft";
	}

	@Override
	public String getStuID() {
		return "599199618";
	}

	@Override
	public String getUsername() {
		return "nk3";
	}

	@Override
	public boolean inTournament() {
		return true;
	}

	@Override
	public void setColor(Color arg0) {
		this.myColor = arg0;

	}

	@Override
	public void setOpponentColor(Color arg0) {
		this.opColor = arg0;
	}

}
