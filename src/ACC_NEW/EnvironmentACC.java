package ACC_NEW;


import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.knowledge.Component;
import cz.cuni.mff.d3s.deeco.knowledge.OutWrapper;


public class EnvironmentACC extends Component {

public String name = "E";
public Double eFollowerGas = 0.0;   //by follower
public Double eFollowerBrake = 0.0; //by follower
public Double eLeaderGas = 0.0;     //by leader
public Double eLeaderBrake = 0.0;   //by leader

public Double fPos = 0.0;
public Double eFollowerSpeed = 0.0; 
public Double fAcceleration = 0.0;  

public Double lPos = 60.0;
public Double eLeaderSpeed = 0.0;
public Double lAcceleration = 0.0;
public Double lMinLPos = 0.0;    	 //... calculated here +: minLSpeed * lETimePeriod       
public Double lMaxLPos = 0.0;		 //... calculated here +: maxLSpeed * lETimePeriod
public Double lMinLSpeed = 0.0;		 //... calculated here +: minLAcc * lETimePeriod
public Double lMaxLSpeed = 0.0;		 //... calculated here +: maxLAcc * lETimePeriod
public Double lMinAcc = 0.0;
public Double lMaxAcc = 0.0;
public Double timePeriod = 0.0;

public Double efCreationTime = 0.0; // by follower
public Double elCreationTime = 0.0; // by leader
public Double lastTime = 0.0;
public Double initTime = 0.0;
public Double eLInitTime =0.0;
public Double eFInitTime =0.0;


protected static final LookupTable fTorques = new LookupTable();
protected static final LookupTable lTorques = new LookupTable();
protected static final LookupTable routeSlops = new LookupTable();
protected static final double fMass = 1000;
protected static final double lMass = 1000;
protected static final double g = 9.80665;
protected static final double secNanoSecFactor = 1000000000;


public EnvironmentACC() {
	initTime = System.nanoTime()/secNanoSecFactor;
	lTorques.put(0.0, 165.0);
	lTorques.put(8.0, 180.0);
	lTorques.put(20.0, 180.0);
	lTorques.put(28.0, 170.0);
	lTorques.put(40.0, 170.0);
	lTorques.put(60.0, 150.0);
	lTorques.put(80.0, 115.0);
	lTorques.put(100.0, 97.0);
	lTorques.put(120.0, 80.0);
	lTorques.put(140.0, 70.0);
	lTorques.put(160.0, 60.0);
	lTorques.put(180.0, 50.0);
	lTorques.put(200.0, 40.0);
	lTorques.put(100000.0, 1.0);
	
	fTorques.put(0.0, 165.0);
	fTorques.put(8.0, 180.0);
	fTorques.put(20.0, 180.0);
	fTorques.put(28.0, 170.0);
	fTorques.put(40.0, 170.0);
	fTorques.put(60.0, 150.0);
	fTorques.put(80.0, 115.0);
	fTorques.put(100.0, 97.0);
	fTorques.put(120.0, 80.0);
	fTorques.put(140.0, 70.0);
	fTorques.put(160.0, 60.0);
	fTorques.put(180.0, 50.0);
	fTorques.put(200.0, 40.0);
	fTorques.put(100000.0, 1.0);
	
	routeSlops.put(0.0, 0.0);
	routeSlops.put(1000.0, 0.0);
	routeSlops.put(2000.0, Math.PI/60);
	routeSlops.put(3000.0, Math.PI/30);
	routeSlops.put(4000.0, Math.PI/20);
	routeSlops.put(5000.0, Math.PI/15);
	routeSlops.put(6000.0, 0.0);
	routeSlops.put(7000.0, 0.0);
	routeSlops.put(8000.0, -Math.PI/18);
	routeSlops.put(9000.0, -Math.PI/36);
	routeSlops.put(10000.0, 0.0);
	routeSlops.put(11000.0, 0.0);
	routeSlops.put(12000.0, 0.0);
	routeSlops.put(13000.0, 0.0);
	routeSlops.put(14000.0, 0.0);
	routeSlops.put(15000.0, 0.0);
	routeSlops.put(100000.0, 0.0);
}	

@Process
@PeriodicScheduling(1000)
public static void environmentResponse(
		@In("initTime") Double initTime,
		@In("elCreationTime") Double elCreationTime,
		@In("efCreationTime") Double efCreationTime,
		
		@In("eLeaderGas") Double lGas,
		@In("eLeaderBrake") Double lBrake,
		@In("eFollowerGas") Double fGas,
		@In("eFollowerBrake") Double fBrake,
		
		@InOut("fPos") OutWrapper<Double> fPos,
		@InOut("eFollowerSpeed") OutWrapper<Double> fSpeed,
		@InOut("fAcceleration") OutWrapper<Double> fAcceleration,

		@InOut("lPos") OutWrapper<Double> lPos,
		@InOut("eLeaderSpeed") OutWrapper<Double> lSpeed,
		@InOut("lAcceleration") OutWrapper<Double> lAcceleration,
		@InOut("lMinAcc") OutWrapper<Double> lMinAcc,
		@InOut("lMaxAcc") OutWrapper<Double> lMaxAcc,
		@InOut("timePeriod") OutWrapper<Double> timePeriod,

		@InOut("lastTime") OutWrapper<Double> lastTime
		){

	timePeriod.value = 1.0;//((System.nanoTime()/secNanoSecFactor) - initTime) -lastTime.value;// 0.1;
//	System.out.println("%%%%%% system time :"+timePeriod);
	// ----------------------- leader ----------------------------------------------------------------------
	System.out.println("Speed leader : "+lSpeed.value+", pos : "+lPos.value+"... time :"+lastTime.value);
	double lFEng = lGas * lTorques.get(lSpeed.value) / 0.005;
	double lFResistance = lBrake * 10000;
	double lFEngResistance = 0.0005 * lSpeed.value;
	double lFHill = Math.sin(routeSlops.get(lPos.value)) * g * lMass;
	double lFFinal = lFEng - lFResistance - lFEngResistance - lFHill;
	lAcceleration.value = lFFinal / lMass;
	lMinAcc.value = (0.0 - lFResistance - lFEngResistance - lFHill)/lMass;
	lMaxAcc.value = (lFEng - 0.0 - lFEngResistance - lFHill)/lMass;
	lSpeed.value += lAcceleration.value * timePeriod.value;
	lPos.value += lSpeed.value * timePeriod.value;
	
	//------------------------ follower ---------------------------------------------------------------------
	System.out.println("Speed follower : "+fSpeed.value+", pos : "+fPos.value+"... time :"+lastTime.value);
	double fFEng = fGas * fTorques.get(fSpeed.value) / 0.005;
	double fFResistance = fBrake * 10000;
	double fFEngResistance = 0.0005 * fSpeed.value;
	double fFHill = Math.sin(routeSlops.get(lPos.value)) * g * fMass;
	double fFFinal = fFEng - fFResistance - fFEngResistance - fFHill;
	fAcceleration.value = fFFinal / fMass;
	fSpeed.value += fAcceleration.value * timePeriod.value; // just put the fSpeed directly
	fPos.value += fSpeed.value * timePeriod.value;
	
	//-----------------------------------------------------------------------------------------------------------
	
	System.out.println("................... distance : "+(lPos.value - fPos.value));
	lastTime.value += timePeriod.value;//0.1;
//	System.out.println("##### Environment leader last time :"+lastTime.value+" the creation :"+elCreationTime+" the oldness: "+(lastTime.value-elCreationTime));
//	System.out.println("##### Environment Follower last time :"+lastTime.value+" the creation :"+efCreationTime+" the oldness: "+(lastTime.value-efCreationTime));
	}
}