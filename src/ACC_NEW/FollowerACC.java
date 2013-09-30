package ACC_NEW;


import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.Out;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.knowledge.Component;
import cz.cuni.mff.d3s.deeco.knowledge.OutWrapper;

public class FollowerACC extends Component {

public String name;
public Double currentFPos = 0.0;     //... calculated here +: currentFSpeed * eTimePeriod
public Double currentFSpeed = 0.0;   //... calculated here +: fAcc * eTimePeriod
public Double fAcc = 0.0;            //by env. via ensemble
public Double eTimePeriod = 0.0;     //by env. via ensemble
public Double fCreationTime = 0.0;   //by env. via ensemble


public Double followerGas = 0.0 ;
public Double followerBrake = 0.0;
public Double lastTime = 0.0;


public Double currentLPos = 60.0;    //... calculated here +: currentLSpeed * lETimePeriod
public Double currentLSpeed = 0.0;   //... calculated here +: lAcc * lETimePeriod
public Double lAcc = 0.0;            //by leader via ensemble
public Double minLPos = 0.0;    	 //... calculated here +: minLSpeed * lETimePeriod       
public Double maxLPos = 0.0;		 //... calculated here +: maxLSpeed * lETimePeriod
public Double minLSpeed = 0.0;		 //... calculated here +: minLAcc * lETimePeriod
public Double maxLSpeed = 0.0;		 //... calculated here +: maxLAcc * lETimePeriod
public Double minLAcc = 0.0;		 //by leader via ensemble
public Double maxLAcc = 0.0;		 //by leader via ensemble
public Double lETimePeriod = 0.0;	 //by leader via ensemble
public Double lTimePeriod = 0.0;	 //by leader via ensemble
public Double lCreationTime = 0.0;   //by leader via ensemble
public Double lECreationTime = 0.0;  //by leader via ensemble


public Double lInitTime = 0.0;		 //by leader via ensemble
public Double eInitTime = 0.0;       //by env. via ensemble
public Double initTime = 0.0;
public Double integratorError = 0.0;
public Double es = 0.0;


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
@PeriodicScheduling(1000)
public static void speedControl(

		@In("fAcc") Double currentFAcc,
		@In("eTimePeriod") Double eTimePeriod,
		@In("lAcc") Double currentLAcc,
		@In("lETimePeriod") Double lETimePeriod,
		
		@In("initTime") Double initTime,
		@In("fCreationTime") Double creationTime,
		@In("lCreationTime") Double lCreationTime,
		@In("lTimePeriod") Double lTimePeriod,
		@In("lECreationTime") Double lECreationTime,
		@In("eInitTime") Double eInitTime,
	

		@Out("followerGas") OutWrapper<Double> fGas,
		@Out("followerBrake") OutWrapper<Double> fBrake,
		
		
		@InOut("currentFPos") OutWrapper<Double> currentFPos,
		@InOut("currentLPos") OutWrapper<Double> currentLPos,
		@InOut("currentFSpeed") OutWrapper<Double> currentFSpeed,
		@InOut("currentLSpeed") OutWrapper<Double> currentLSpeed,

		@InOut("lastTime") OutWrapper<Double> lastTime,
		@InOut("integratorError") OutWrapper<Double> integratorError,
		@InOut("es") OutWrapper<Double> es,	
		@InOut("minLPos") OutWrapper<Double> minLPos,
		@InOut("maxLPos") OutWrapper<Double> maxLPos,
		@InOut("minLSpeed") OutWrapper<Double> minLSpeed,
		@InOut("maxLSpeed") OutWrapper<Double> maxLSpeed,
		@InOut("minLAcc") OutWrapper<Double> minLAcc,
		@InOut("maxLAcc") OutWrapper<Double> maxLAcc	
		) {
	
	double timePeriod = 1.0; //((System.nanoTime()/secNanoSecFactor) - initTime) -lastTime.value;//system time
//	System.out.println(" currentFAcc : "+currentFAcc + " etimeperiod :"+eTimePeriod+"  ,  currentLAcc : "+currentLAcc+"  letimeperiod : "+lETimePeriod);
//	currentFSpeed.value += currentFAcc * eTimePeriod;
//	currentLSpeed.value += currentLAcc * lETimePeriod;
//	System.out.println(" lspeed :"+currentLSpeed.value+", fspeed :"+currentFSpeed.value);
	
//	currentFPos.value += currentFSpeed.value * eTimePeriod;
//	currentLPos.value += currentLSpeed.value * lETimePeriod;
	
//	System.out.println(" lpos :"+currentLPos.value+", fpos :"+currentFPos.value);
	
	Double[] valuesBoundries= {0.0,0.0,0.0,0.0,0.0,0.0};
	minLSpeed.value = minLAcc.value * lETimePeriod;
	maxLSpeed.value = maxLAcc.value * lETimePeriod;
	minLPos.value = minLSpeed.value * lETimePeriod;
	maxLPos.value = maxLSpeed.value * lETimePeriod;
	
	valuesBoundries[0] = minLPos.value;
	valuesBoundries[1] = maxLPos.value;
	valuesBoundries[2] = minLSpeed.value;
	valuesBoundries[3] = maxLSpeed.value;
	valuesBoundries[4] = minLAcc.value;
	valuesBoundries[5] = maxLAcc.value;
	
//	Double phase = Math.abs(initTime - eInitTime);// (delay period) should be with the respect to /inittime/s
	Double dt = 3.0; //lastTime.value - lECreationTime;  //  delay    
	Double[] newVals = boundries(valuesBoundries);
	minLPos.value = currentLPos.value + newVals[0]*dt;        //minPos += minSpeed*dt;
	maxLPos.value = currentLPos.value + newVals[1]*dt;        //maxPos += maxSpeed*dt;
	minLSpeed.value = currentLSpeed.value + newVals[2]*dt;    //minSpeed += minAcc*dt;
	maxLSpeed.value = currentLSpeed.value + newVals[3]*dt;    //maxSpeed += maxAcc*dt;
	System.out.println("... pos: min "+minLPos.value+" ... max "+maxLPos.value);
	System.out.println("... speed: min "+minLSpeed.value+" ... max "+maxLSpeed.value);
	System.out.println("... acc : min "+minLAcc.value+" ... max "+maxLAcc.value);

	Double b= 0.0;
	if(minLPos.value > 0.0)
		b=checkSafety(valuesBoundries, currentFPos.value);   //the difference with the min bound and the max bound
	System.out.println("minLPos :"+minLPos.value+"  maxPos : "+maxLPos.value+"  b: "+b);
//	if(b == -1.0){
//		fGas.value = 0.0;
//		fBrake.value = 1.0;
//	
//	}else{
	
		double distanceError = - 50 + (currentLPos.value - currentFPos.value);
		double pidD = kpD * distanceError;
		double error = pidD + currentLSpeed.value - currentFSpeed.value;
		integratorError.value += (ki * error + kt * es.value) * timePeriod;
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
//	}
	lastTime.value += timePeriod;
//	System.out.println("##### Follower last time :"+lastTime.value+" the creation :"+creationTime+" the oldness follower: "+(lastTime.value-creationTime));
//	System.out.println(" the leader creation:"+lCreationTime+" the oldness :"+(lastTime.value-lCreationTime));

}


private static double saturate(double pid) {
	if(pid > 1) pid = 1;
	else if(pid < -1) pid = -1;
	return pid;
}

private static Double checkSafety(Double[] valuesBoundries,Double fPos){

	if(fPos >= (valuesBoundries[0] - 40)) return -1.0;
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

private static Double[] estimation(Double pos,Double speed,Double acc, Double dt){
	Double[] values = null;
	values[1] = speed + acc*dt; // new speed
	values[0] = pos + speed*dt + acc*dt*dt/2; // new pos

	return values;
}

}
