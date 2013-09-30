package ACC_NEW;


import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.Out;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.ensemble.Ensemble;
import cz.cuni.mff.d3s.deeco.knowledge.OutWrapper;



public class FollowerEnvEnsembleACC extends Ensemble {

@Membership
public static boolean membership(
		@In("coord.followerGas") Double fGas,
		@In("coord.followerBrake") Double fBrake,
		@In("coord.lastTime") Double fLastTime,
		@In("coord.initTime") Double fInitTime,
		
		@In("member.fAcceleration") Double eFAcceleration,
		@In("member.lastTime") Double eLastTime,
		@In("member.timePeriod") Double eTimePeriod,
		@In("member.initTime") Double eInitTime,
		
		@In("member.eFollowerGas") Double ensFGas,
		@In("member.eFollowerBrake") Double ensFbrake,
		@In("member.efCreationTime") Double eFCreationTime,
		@In("member.eFInitTime") Double eFInitTime,
		
		@In("coord.fAcc") Double fAcc,
		@In("coord.fCreationTime") Double fCreationTime,
		@In("coord.eTimePeriod") Double fETimePeriod,
		@In("coord.eInitTime") Double fEInitTime		
		){
	
		return true;
}

@KnowledgeExchange
@PeriodicScheduling(200)
public static void map(
	@In("coord.followerGas") Double fGas,
	@In("coord.followerBrake") Double fBrake,
	@In("coord.lastTime") Double fLastTime,
	@In("coord.initTime") Double fInitTime,
	
	@In("member.fPos") Double eFPos,
	@In("member.eFollowerSpeed") Double eFollowerSpeed,
	@In("member.fAcceleration") Double eFAcceleration,
	@In("member.lastTime") Double eLastTime,
	@In("member.timePeriod") Double eTimePeriod,
	@In("member.initTime") Double eInitTime,
	
	@Out("member.eFollowerGas") OutWrapper<Double> ensFGas,
	@Out("member.eFollowerBrake") OutWrapper<Double> ensFbrake,
	@Out("member.efCreationTime") OutWrapper<Double> eFCreationTime,
	@Out("member.eFInitTime") OutWrapper<Double> eFInitTime,
	
	@Out("coord.currentFPos") OutWrapper<Double> fCurrentFPos,
	@Out("coord.currentFSpeed") OutWrapper<Double> fCurrentFSpeed,
	@Out("coord.fAcc") OutWrapper<Double> fAcc,
	@Out("coord.fCreationTime") OutWrapper<Double> fCreationTime,
	@Out("coord.eTimePeriod") OutWrapper<Double> fETimePeriod,
	@Out("coord.eInitTime") OutWrapper<Double> fEInitTime
) {

	ensFGas.value = fGas;
	ensFbrake.value = fBrake;
	eFCreationTime.value = fLastTime;
	eFInitTime.value = fInitTime;
	
	fCurrentFPos.value = eFPos;
	fCurrentFSpeed.value = eFollowerSpeed;
	fAcc.value = eFAcceleration;
	fCreationTime.value = eLastTime;
	fETimePeriod.value = eTimePeriod;
	fEInitTime.value = eInitTime;
	
}
}