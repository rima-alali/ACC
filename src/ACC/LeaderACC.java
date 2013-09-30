package ACC;

import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.Out;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.knowledge.Component;
import cz.cuni.mff.d3s.deeco.knowledge.OutWrapper;

public class LeaderACC extends Component {

	public String name;
	public Double lSpeed = 0.0;
	public Double leaderGas = 0.0;
	public Double leaderBrake = 0.0;
	public Double lastTime = 0.0;
	public Double initTime = 0.0;
	public Double creationTime = 0.0;
	public Double lastSpeedError = 0.0;
	public Double integratorSpeedError = 0.0;
	public Double es = 0.0;

	public Double currentLAcc = 0.0;
	public Double currentLPos = 0.0;
	public Double minLAcc = 0.0;
	public Double maxLAcc = 0.0;
	public Double timePeriod = 0.0;// why I would need it?
	public Double creationTimePeriod = 0.0; // why I would need ir?

	
	protected static final double kp = 0.05; 
	protected static final double ki = 0.000228325; 
	protected static final double kd = 0;
	protected static final double kt = 0.01;
	protected static final LookupTable driverSpeed = new LookupTable();
	protected static final double secNanoSecFactor = 1000000000;
	

	public LeaderACC() {
		name = "L";
		initTime = System.nanoTime()/secNanoSecFactor;
		driverSpeed.put(0.0, 90);
		driverSpeed.put(10.0, 90);
		driverSpeed.put(20.0, 90);
		driverSpeed.put(30.0, 150);
		driverSpeed.put(40.0, 170);
		driverSpeed.put(50.0, 90);
		driverSpeed.put(60.0, 90);
		driverSpeed.put(70.0, 90);
		driverSpeed.put(80.0, 90);
		driverSpeed.put(90.0, 90);
		driverSpeed.put(400.0, 90);
	}

	
	@Process
	@PeriodicScheduling(100)
	public static void speedControl(
			@In("lSpeed") Double currentSpeed,
			@In("minLAcc") Double minLAcc,
			@In("maxLAcc") Double maxLAcc,
			@In("initTime") Double initTime,
			@In("creationTime") Double creationTime,
			@Out("leaderGas") OutWrapper<Double> lGas,
			@Out("leaderBrake") OutWrapper<Double> lBrake,
			@InOut("lastTime") OutWrapper<Double> lastTime,
			@InOut("timePeriod") OutWrapper<Double> timePeriod,
			@InOut("lastSpeedError") OutWrapper<Double> lastSpeedError,
			@InOut("integratorSpeedError") OutWrapper<Double> integratorSpeedError,
			@InOut("es") OutWrapper<Double> es			
		) {

		System.out.println("minLAcc : "+minLAcc+"  ,  maxLAcc :"+maxLAcc);
		//I have to consider the offset of all the process?
		timePeriod.value = 0.1;//((System.nanoTime()/secNanoSecFactor) - initTime) -lastTime.value;//0.1;
		double speedError = driverSpeed.get(lastTime.value) - currentSpeed;
		integratorSpeedError.value += (ki * speedError + kt * es.value) * timePeriod.value; 
		double pid = kp * speedError + integratorSpeedError.value + kd * (speedError - lastSpeedError.value)/timePeriod.value;
		es.value = saturate(pid) - pid;
		pid = saturate(pid);
		
		if(pid >= 0){
			lGas.value = pid;
			lBrake.value = 0.0;
		}else{
			lGas.value = 0.0;
			lBrake.value = -pid;
		}
		
		lastSpeedError.value = speedError;
		lastTime.value += timePeriod.value;
//		System.out.println("#####  leader last time :"+lastTime.value+" the creation :"+creationTime+"  the oldness: "+(lastTime.value-creationTime));
	}


	private static double saturate(double pid) {
		if(pid > 1) pid = 1;
		else if(pid < -1) pid = -1;
		return pid;
	}
	
}
