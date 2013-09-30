package ACC;


import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.knowledge.Component;
import cz.cuni.mff.d3s.deeco.knowledge.OutWrapper;


public class EnvironmentACC extends Component {

	public String name = "E";
	public Double eLeaderGas = 0.0;
	public Double eLeaderBrake = 0.0;
	public Double eFollowerGas = 0.0;
	public Double eFollowerBrake = 0.0;
	public Double eLeaderSpeed = 0.0;
	public Double eFollowerSpeed = 0.0;
	public Double lastTime = 0.0;
	public Double initTime = 0.0;
	public Double elCreationTime = 0.0;
	public Double efCreationTime = 0.0;
	public Double fPos = 0.0;
	public Double lPos = 60.0;
	
	public Double minLAcc = 0.0;
	public Double maxLAcc = 0.0;
	public Double timePeriod = 0.0;
	
	
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
	@PeriodicScheduling(100)
	public static void environmentResponse(
			@In("eLeaderGas") Double lGas,
			@In("eLeaderBrake") Double lBrake,
			@In("eFollowerGas") Double fGas,
			@In("eFollowerBrake") Double fBrake,
			@In("initTime") Double initTime,
			@In("elCreationTime") Double elCreationTime,
			@In("efCreationTime") Double efCreationTime,
			@InOut("eLeaderSpeed") OutWrapper<Double> lSpeed,
			@InOut("eFollowerSpeed") OutWrapper<Double> fSpeed,
			@InOut("lastTime") OutWrapper<Double> lastTime,
	
			@InOut("fPos") OutWrapper<Double> fPos,
			@InOut("lPos") OutWrapper<Double> lPos,
			@InOut("minLAcc") OutWrapper<Double> minLAcc,
			@InOut("maxLAcc") OutWrapper<Double> maxLAcc,
			@InOut("timePeriod") OutWrapper<Double> timePeriod
			){

		timePeriod.value = 0.1;//((System.nanoTime()/secNanoSecFactor) - initTime) -lastTime.value;// 0.1;
//		System.out.println("%%%%%%     system time :"+timePeriod);
		// ----------------------- leader ----------------------------------------------------------------------
		System.out.println("Speed leader : "+lSpeed.value+",     pos : "+lPos.value+"...   time :"+lastTime.value);
		double lFEng = lGas * lTorques.get(lSpeed.value) / 0.005;
		double lFResistance = lBrake * 10000;
		double lFEngResistance = 0.0005 * lSpeed.value;
		double lFHill = Math.sin(routeSlops.get(lPos.value)) * g * lMass; 
		double lFFinal = force(lFEng, lFResistance, lFEngResistance, lFHill);
		System.out.println("leader ... "+lFFinal+" = "+lFEng+" - "+lFEngResistance+" - "+lFEngResistance+" - "+lFHill);
		double lAcceleration = lFFinal / lMass;
		minLAcc.value = force(0.0, lFResistance, lFEngResistance, lFHill)/lMass;
		maxLAcc.value = force(lFEng, 0.0, lFEngResistance, lFHill)/lMass;
		lSpeed.value += lAcceleration * timePeriod.value;
		lPos.value += lSpeed.value * timePeriod.value;
		System.out.println("env.  min:"+minLAcc.value+"  max:"+maxLAcc.value);
		//------------------------ follower ---------------------------------------------------------------------
		System.out.println("Speed follower : "+fSpeed.value+",       pos : "+fPos.value+"...   time :"+lastTime.value);
		double fFEng = fGas * fTorques.get(fSpeed.value) / 0.005;	
		double fFResistance = fBrake * 10000;
		double fFEngResistance = 0.0005 * fSpeed.value;
		double fFHill = Math.sin(routeSlops.get(fPos.value)) * g * fMass; 
		double fFFinal =force(fFEng, fFResistance, fFEngResistance, fFHill); // fFEng - fFResistance - fFEngResistance - fFHill;
		System.out.println("follower ... "+fFFinal+" = "+fFEng+" - "+fFResistance+" - "+fFEngResistance+" - "+fFHill);
		double fAcceleration = fFFinal / fMass;
		fSpeed.value += fAcceleration * timePeriod.value; // just put the fSpeed directly
		fPos.value += fSpeed.value * timePeriod.value; 
		
		//-----------------------------------------------------------------------------------------------------------
		
		System.out.println("...................  distance : "+(lPos.value - fPos.value));
		lastTime.value += timePeriod.value;//0.1;
		System.out.println("#####  Environment leader last time :"+lastTime.value+" the creation :"+elCreationTime+"  the oldness: "+(lastTime.value-elCreationTime));
		System.out.println("#####  Environment Follower last time :"+lastTime.value+" the creation :"+efCreationTime+"  the oldness: "+(lastTime.value-efCreationTime));
	}
	
	private static Double force(Double Eng, Double Resistance, Double EngResistance, Double Hill){
		return Eng - Resistance - EngResistance - Hill;
	}
}
