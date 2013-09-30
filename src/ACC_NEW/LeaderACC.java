package ACC_NEW;

import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.Out;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.knowledge.Component;
import cz.cuni.mff.d3s.deeco.knowledge.OutWrapper;

public class LeaderACC extends Component {

public String name;

public Double lPos = 0.0;         //...calculated here +: lSpeed*eTimePeriod
public Double lSpeed = 0.0;       //...calculated here +: lAcc*eTimePeriod
public Double lAcc = 0.0;         //from env. via ensemble
public Double lMinAcc = 0.0;      //from env. via ensemble
public Double lMaxAcc = 0.0;      //from env. via ensemble
public Double eTimePeriod = 0.0;  //from env. via ensemble
public Double creationTime = 0.0; //from env. via ensmble

public Double leaderGas = 0.0;
public Double leaderBrake = 0.0;
public Double lastTime = 0.0;

public Double initTime = 0.0;
public Double eInitTime =0.0;     //is it important ???
public Double timePeriod = 0.0;
public Double lastSpeedError = 0.0;
public Double integratorSpeedError = 0.0;
public Double es = 0.0;

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
@PeriodicScheduling(1000)
public static void speedControl(
	@In("lAcc") Double lAcc,
	@In("initTime") Double initTime,
	@In("eTimePeriod") Double eTimePeriod,
	@In("creationTime") Double creationTime,
	
	@Out("leaderGas") OutWrapper<Double> lGas,
	@Out("leaderBrake") OutWrapper<Double> lBrake,
	
	@InOut("lPos") OutWrapper<Double> lPos,
	@InOut("lSpeed") OutWrapper<Double> lSpeed,
	@InOut("lastTime") OutWrapper<Double> lastTime,
	@InOut("timePeriod") OutWrapper<Double> timePeriod,
	@InOut("lastSpeedError") OutWrapper<Double> lastSpeedError,
	@InOut("integratorSpeedError") OutWrapper<Double> integratorSpeedError,
	@InOut("es") OutWrapper<Double> es	
) {

	timePeriod.value = 1.0;//((System.nanoTime()/secNanoSecFactor) - initTime) -lastTime.value;//0.1;
	lSpeed.value += lAcc*eTimePeriod;
	double speedError = driverSpeed.get(lastTime.value) - lSpeed.value;
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
//	System.out.println("##### leader last time :"+lastTime.value+" the creation :"+creationTime+" the oldness: "+(lastTime.value-creationTime));
	
}
	
	
private static double saturate(double pid) {
	if(pid > 1) pid = 1;
	else if(pid < -1) pid = -1;
	return pid;
}

}