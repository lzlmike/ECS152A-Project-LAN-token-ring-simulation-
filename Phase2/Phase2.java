package ECS152APHASE2;
import java.util.*;
import java.text.DecimalFormat;

public class Phase2 {
	private static double Dp=1e-5;  //Propagation Delay
	private static double R=1.25e7;  //conert to bytes
	private static ArrayList<Event> GEL=new ArrayList<Event>();  //Global Event List
	private static ArrayList<Event> Frame=new ArrayList<Event>();  //Frame to store transfrring package
	
	
	public static void main(String[] args) {
		int[] host_Number={10,25};
		double[] arrival_Rate={0.01,0.05,0.1,0.2,0.3,0.5,0.6,0.7,0.8,0.9};
		for(int i=0;i<2;i++){
			for(int j=0;j<10;j++) runSimulation(host_Number[i],arrival_Rate[j]);
		}
	}
	
	
	public static void runSimulation(int hostNumber,double arivRate){
		GEL.clear();
		Frame.clear();
		Host[] hosts=new Host[hostNumber];        //Hosts with Host type
		double delay=0;
		double time=0;
		double throuSize=0;
		int count=0;                               //number of packets transfered
		initialize(hosts,hostNumber,arivRate);    //Initialize all the hosts' arrival events an the token position event
		
		while(count<500){                   //simulation start
			Event outEvent=GEL.remove(0);   //Pull out the event
			time=outEvent.eventTime;        //set the time to the event time
			
			if(outEvent.eType==0){        //if it is an arrival event
				hosts[outEvent.source].queue.add(outEvent);   //add the event to the queue of the host
				genArrEvent(outEvent,arivRate);				//generate next arrival event immediately and push to GEL;
			}
			else{                           // if it is a token events, time to transfer package;
				int bytes=0;               //set the transfered size to 0;
				int hostNum=outEvent.position;   //hostNum is the position of the token at. 
				
				if(outEvent.free==true){     //if it is a free token, which means we can transfer data	
					//double queueD=0;         //set queueing delay to 0;
					
					if(hosts[hostNum].queue.size()!=0){      //if there is package in the host 					
						while(!(hosts[hostNum].queue.isEmpty())){          //handle rest of package in the host
							Event temp=hosts[hostNum].queue.remove(0);  //remove the package in the host
							bytes+=temp.size;				//add the size of the package
							Frame.add(temp);				  //push the package into the frame
						}	
						genToken(outEvent,bytes,1,hostNumber);     //generate the next token, set the free to false(0)
					}													
					else{                                 //no package in the host
						genToken(outEvent,0,0,hostNumber);           //generate next token, and the token is still free.
					}
				}
				
				else{                                 //The token is not free, deliver packets
					bytes=outEvent.size;              //set bytes to size of the token or frame.
					if(hostNum==outEvent.source){      // check if the position is the orignal position
						Frame.clear();                 // if it is, clear the frame, and set bytes to 0;
						bytes=0;
					}
					for(int i=0;i<Frame.size();i++){  // check function check if the frame has package to send to the host
						if(Frame.get(i).dest==hostNum){  //if match
							Event copy=Frame.get(i);      //copy the event
							delay+=time-copy.eventTime;  // calculate the delay 
							throuSize+=copy.size;    // and the add to total package size
							count+=1;    // package send count +1
						} 
					}  
					genToken(outEvent,bytes,1,hostNumber);         //set another token events, default free is false, more detail in 
				}                                       //genToken function.
			}
		}
		
		System.out.println("Total Size:"+ throuSize);
		System.out.println("Delay: "+delay);
		System.out.println("Total Package: "+count);
		System.out.println("For arrival Rate: "+arivRate+" ,host number: "+hostNumber);
		System.out.println("ThroughPut: "+round(throuSize/time)+" bytes/second.");
		System.out.println("Average Packet delay: "+delay/count+" second.");
		System.out.println();
		
	}

	
	/*public static void printInfo(Event temp){      prin information of an event
		System.out.print("Source: "+temp.source);
		System.out.print(" EventTime: "+temp.eventTime);
		System.out.print(" Size: "+temp.size);
		System.out.print(" Dest: "+temp.dest);
		System.out.print(" Position: "+temp.position);
		System.out.print("Type: "+temp.eType);
		System.out.println();
	}*/
	
	public static void genToken(Event pre,int b,int free,int hostNumber){  //generate token events
		Event event=new Event();     
		event.eType=1;                  //set type to events
		event.source=pre.source;         //the starting token position stay the same
		event.eventTime=pre.eventTime+Dp+b/R;   //the time is pretime+Dp+Dt
		event.size=b;                    //set size,depends on bytes(b)
		event.free=false;                //default to false, not free
		event.steps=pre.steps+1;          //track if it come back to the original hosts
		event.position=(pre.position+1)%hostNumber;    //postion+1
		if(pre.steps==hostNumber || free==0){          //if it comes back to original, or the hosts has no package to send    
			event.source=(pre.source+1)%hostNumber;   // token starting position move on
			event.free=true;                     //set it to free
			event.steps=0;                  //steps to 0
		}
		GEL.add(event);                      // add the event to GEL and sort GEL based on time
		Collections.sort(GEL);
	}
	public static void genArrEvent(Event pre, double arivRate){    //create arrival events
		Event event=new Event();                 
		event.eType=0;                              //type to arrive
		event.source=pre.source;
		event.dest=(int)(Math.random()*10);                      
		while(event.dest==event.source) event.dest=(int)(Math.random()*10);
		event.eventTime=pre.eventTime+negEx(arivRate);
		event.size=(int)(Math.random()*1455);              
		GEL.add(event);                             //add to GEL and sort
		Collections.sort(GEL);
	}

	public static void initialize(Host[] hosts,int hostNumber, double arivRate){   //initialization 
		for(int i=0 ; i<hostNumber; i++){      //all the hosts, with arrival events
			hosts[i]=new Host();
			Event event=new Event();
			event.eType=0;
			event.source=i;
			event.dest=(int)(Math.random()*10);
			while(event.dest==i) event.dest=(int)(Math.random()*10);
			event.eventTime=negEx(arivRate);
			event.size=(int)(Math.random()*1455+64);
			GEL.add(event);
		}
		
		Event tokenP=new Event();                  // one token events
		tokenP.eType=1;  //token event
		tokenP.free=true;
		tokenP.position=1;
		tokenP.source=1;
		tokenP.eventTime=0;               //put it at the beginning of the GEL 
		GEL.add(tokenP);
		Collections.sort(GEL);	
	}
	
	public static double round(double d){     //round number to 4 decimal
		DecimalFormat temp=new DecimalFormat("#.####");
		return Double.valueOf(temp.format(d));
	}
	public static double negEx(double rate){   //....
		double u= Math.random();
		return ((-1/rate)*Math.log(1-u));
	}
}
