package ECS152APHASE2;

/**
 * Author: Zuolin Li, 998829351
 *         Hanyuan Dong, 998983129
 * ECS152A Project1 Phase1
 * Simulation Analysis of a Network Protocol
 */

// can be delete,eclipse package match;

public class Event implements Comparable<Event>{
	public double eventTime;
	public int eType;//0 is arrival, 1 is token;
	public int source;//host number;
	public int dest; // destination of the package
	public int size; // size of the arrival event package
	public int position; //position of token
	public boolean free; //1 is free, 0 is not
	public int steps;
	
	@Override
	//a method to sort a arrayList by its eventTime;
	public int compareTo(Event t){
		double comparetime=((Event)t).eventTime;
		return (int) ((this.eventTime-comparetime)*100000000);
	}
}