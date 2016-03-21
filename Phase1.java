/**
 * Author: Zuolin Li, 998829351
 *         Hanyuan Dong, 998983129
 * ECS152A Project1 Phase1
 * Simulation Analysis of a Network Protocol
 */

package ECS152A;// can be delete,eclipse package match.
import java.text.DecimalFormat;
import java.util.*;

public class Phase1 {

	private static int maxBuffer; 
	private static double arrivalR;
	private static double transR;
	private static int length = 0;
	private static double avgLength = 0;
	private static double ult = 0;
	private static int packageDropped = 0;
	private static double time=0;
	
	public static void main(String[] args) {
		//Initialization
		double busyTime=0;
		double packageArea=0;
		
		ArrayList<Event> GEL=new ArrayList<Event>();
		Queue<Event> queue=new LinkedList<Event>();
		
		/*transR=u=1
		  arrivalR=0.1,0.25,0.4,0.55,0.65,0.8,0.9 with infinite buffer size
		  arrivalR=0.2,0.4,0.6,0.8,0.9 with maxBuffer=1,20,50
		*/
		arrivalR=0.9;
		transR=1.0;
		maxBuffer=Integer.MAX_VALUE;
		
		//create the first arrival event and insert to GEL;
		Event event=new Event();
		event.eventTime=time+negExpDTime(arrivalR);
		event.serviceTime=negExpDTime(transR);
		event.eType=0;
		GEL.add(event);
		
		for(int i=0;i<100000;i++){
			//get the first event from GEL;
			Event evet=GEL.remove(0);
			//System.out.println("Time: "+time);
			packageArea+=length*(evet.eventTime-time);
			time=evet.eventTime;
			//printEvent(evet,length,packageArea);
			//if it is an arrival event;
			if(evet.eType==0){
				Event newEvent= new Event();
				newEvent.eType=0;
				newEvent.eventTime=time+negExpDTime(arrivalR);
				//System.out.println("event time: "+newEvent.eventTime);
				newEvent.serviceTime=negExpDTime(transR);
				GEL.add(newEvent);
				Collections.sort(GEL);
				busyTime=busyTime+newEvent.serviceTime;
				//System.out.println(busyTime);
				
				if(length==0){// empty queue;
					Event depart=new Event();
					depart.eType=1;
					depart.eventTime=time+newEvent.serviceTime;
					GEL.add(depart);
					Collections.sort(GEL);
					length++;
				}else if(maxBuffer>length-1){// the queue is not full;
					queue.add(newEvent);
					length++;
				}
				else{//the queue is full, drop the package;
					packageDropped++;
				}
			}
			//else must be departure;
			else{
				length--;
				if(length>0){ //if there are package in queue;
					Event outEvent=queue.remove();
					Event depart2=new Event();
					depart2.eType=1;
					depart2.eventTime=time+outEvent.serviceTime;
					GEL.add(depart2);
					Collections.sort(GEL);
				}  //if no package in queue, just continue;
			}
			
			//if(GEL.size()==0) System.out.println("all gone");;
		}
		//out-put statistics;
		//System.out.println(busyTime + " "+time);
		ult=roundTwoDecimal(busyTime/time);
		avgLength=roundTwoDecimal(packageArea/time);
		
		/*
		 *System.out.println("Event number remaining: "+GEL.size());
		 *while(GEL.size()!=0) printEvent(GEL.remove(0),length,packageArea);
		 */
		
		System.out.println("MAXBUFFER: " + maxBuffer);
		System.out.println("Arrival Rate(¦Ë): " + arrivalR);
		System.out.println("Transmission Rate(u): " + transR);
		System.out.println("Average queue length: " + avgLength);
		System.out.println("Utilization:" + ult*100 +"%");
		System.out.println("Package dropped: " + packageDropped);
		//System.out.println(negExpDTime(0.1));

	}
	/** testing if the order is right.
	public static void printEvent(Event evet,int l,double a){
		System.out.println("Event out: type: "+evet.eType+" EventTime: "+evet.eventTime 
				+" ServiceTime: "+evet.serviceTime+" Length: "+l+"Area: "+a);
	}*/
	
	//round double to 4 decimal;
	public static double roundTwoDecimal(double d){
		DecimalFormat temp=new DecimalFormat("#.####");
		return Double.valueOf(temp.format(d));
	}
	public static double negExpDTime(double rate){
		double u= Math.random();
		return ((-1/rate)*Math.log(1-u));
	}
}
