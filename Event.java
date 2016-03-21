/**
 * Author: Zuolin Li, 998829351
 *         Hanyuan Dong, 998983129
 * ECS152A Project1 Phase1
 * Simulation Analysis of a Network Protocol
 */

package ECS152A;// can be delete,eclipse package match;

public class Event implements Comparable<Event>{
	public double eventTime;
	public double serviceTime;
	public int eType;//0 is arrival, 1 is depart;
	@Override
	//a method to sort a arrayList by its eventTime;
	public int compareTo(Event t){
		double comparetime=((Event)t).eventTime;
		return (int) ((this.eventTime-comparetime)*1000);
	}
}
