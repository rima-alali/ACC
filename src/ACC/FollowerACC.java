package ACC;


import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.Out;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.knowledge.Component;
import cz.cuni.mff.d3s.deeco.knowledge.OutWrapper;

public class FollowerACC extends Component {

	public String name;
	public Double followerGas = 0.0 ;
	public Double followerBrake = 0.0;

	public Double currentFPos = 0.0;//from env.
	public Double fSpeed = 0.0;//from env.
	public Double currentFAcc = 0.0;
	public Double fCreationTime = 0.0;//from env.
	public Double currentLPos = 0.0;//from the leader - should not know about the leader position at the first 60.0
	public Double leaderSpeed = 0.0;
	public Double currentLAcc = 0.0;//from the leader
	public Double lCreationTime = 0.0;
	
	public Double lastTime = 0.0;
	public Double timePeriod = 0.0;
	public Double initTime = 0.0;
	public Double integratorError = 0.0;
	public Double es = 0.0;
	
	public Double minLPos = 60.0;//calculated
	public Double maxLPos = 60.0;//calculated
	public Double minLSpeed = 0.0;//calculated
	public Double maxLSpeed = 0.0;//calculated
	public Double minLAcc = 0.0;//torque - leader
	public Double maxLAcc = 0.0;//torque - leader
	public Double lTimePeriod = 0.0;//leader from ensemble to calculate min/maxLSpeed and min/maxLPos
	
	protected static final double kpD = 0.193;
	protected static final double kp = 0.12631;
	protected static final double ki = 0.001;
	protected static final double kd = 0;
	protected static final double kt = 0.01;
	protected static final double secNanoSecFactor = 1000000000;
	
	
	public FollowerACC() {
		name = "F";
		initTime = System.nanoTime()/secNanoSecFactor;
	}
	
	
	@Process
	@PeriodicScheduling(100)
	public static void speedControl(
			@In("fSpeed") Double currentFSpeed,
			@In("leaderSpeed") Double currentLSpeed,
			@In("initTime") Double initTime,
			@In("fCreationTime") Double creationTime,
			@In("lCreationTime") Double lCreationTime,
			@In("lTimePeriod") Double lTimePeriod,
			@Out("followerGas") OutWrapper<Double> fGas,
			@Out("followerBrake") OutWrapper<Double> fBrake,
			@InOut("lastTime") OutWrapper<Double> lastTime,
			@InOut("timePeriod") OutWrapper<Double> timePeriod,
			@InOut("integratorError") OutWrapper<Double> integratorError,
			@InOut("es") OutWrapper<Double> es,			
			@InOut("currentFPos") OutWrapper<Double> currentFPos,
			@InOut("currentLPos") OutWrapper<Double> currentLPos,

			@InOut("minLPos") OutWrapper<Double> minLPos,
			@InOut("maxLPos") OutWrapper<Double> maxLPos,
			@InOut("minLSpeed") OutWrapper<Double> minLSpeed,
			@InOut("maxLSpeed") OutWrapper<Double> maxLSpeed,
			@InOut("minLAcc") OutWrapper<Double> minLAcc,
			@InOut("maxLAcc") OutWrapper<Double> maxLAcc
			) {
		
		timePeriod.value = 0.1;//((System.nanoTime()/secNanoSecFactor) - initTime) -lastTime.value;//system time
		
//		Double[] valuesBoundries= {0.0,0.0,0.0,0.0,0.0,0.0};
//		valuesBoundries[0] = minLPos.value;
//		valuesBoundries[1] = maxLPos.value;
//		valuesBoundries[2] = minLSpeed.value;
//		valuesBoundries[3] = maxLSpeed.value;
//		valuesBoundries[4] = minLAcc.value;
//		valuesBoundries[5] = maxLAcc.value;
//
//		Double[] newVals = boundries(valuesBoundries);
//		minLPos.value += newVals[0]*lTimePeriod;     //minPos += minSpeed*lTimePeriod;
//		maxLPos.value += newVals[1]*lTimePeriod;     //maxPos += maxSpeed*lTimePeriod;
//		minLSpeed.value += newVals[2]*lTimePeriod;   //minSpeed += minAcc*lTimePeriod;
//		maxLSpeed.value += newVals[3]*lTimePeriod;   //maxSpeed += maxAcc*lTimePeriod;
//		
//		Double b=checkSafety(valuesBoundries, currentFPos.value);//the difference with the min bound
//		System.out.println("minLPos :"+minLPos.value+"  maxPos : "+maxLPos.value+"  b: "+b);
//		if(b == 1.0){
//			fGas.value = 0.0;
//			fBrake.value = b;
//
//		}else{

			double distanceError = - 50 + (currentLPos.value - currentFPos.value); 
			double pidD = kpD * distanceError;
			double error = pidD + currentLSpeed - currentFSpeed; 
			integratorError.value += (ki * error + kt * es.value) * timePeriod.value; 
			double pid = kp * error + integratorError.value;
			es.value = saturate(pid) - pid;
			pid = saturate(pid);
			
			if(pid >= 0){
				fGas.value = pid;
				fBrake.value = 0.0;
			}else{
				fGas.value = 0.0;
				fBrake.value = -pid;
			}
			
//		}
		
		lastTime.value += timePeriod.value;
//		System.out.println("#####  Follower last time :"+lastTime.value+" the creation :"+creationTime+"  the oldness follower: "+(lastTime.value-creationTime));
//		System.out.println("                                              the leader creation:"+lCreationTime+"  the oldness :"+(lastTime.value-lCreationTime));
		
	}
	
	
	private static double saturate(double pid) {
		if(pid > 1) pid = 1;
		else if(pid < -1) pid = -1;
		return pid;
	}
	
	private static Double checkSafety(Double[] valuesBoundries,Double fPos){

		if((fPos > (valuesBoundries[0] - 40) )){
			return 1.0;
		}
		return 0.0;
	}
	
	private static Double[] boundries(Double[] valuesBoundries){
		Double[] v={0.0,0.0,0.0,0.0};
		v[0] = valuesBoundries[2]; //d/dt (minLPos) = minSpeed 
		v[1] = valuesBoundries[3]; //d/dt (maxLPos) = maxLSpeed
		v[2] = valuesBoundries[4]; //d/dt (minLSpeed) = minLAcc
		v[3] = valuesBoundries[5]; //d/dt (maxLSpeed) = maxLAcc
		return v;
	}
	
//	private static Double[] estimation(Double pos,Double speed,Double acc, Double dt){
//		Double[] values = null;
//		values[0] = pos + speed*dt + acc*dt*dt/2; // new pos
//		values[1] = speed + acc*dt; // new speed
//		return values;
//	}
}
