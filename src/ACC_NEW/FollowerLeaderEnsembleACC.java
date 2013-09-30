package ACC_NEW;

import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.Out;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.ensemble.Ensemble;
import cz.cuni.mff.d3s.deeco.knowledge.OutWrapper;



public class FollowerLeaderEnsembleACC extends Ensemble {

@Membership
public static boolean membership(
		@In("coord.lAcc") Double fLAcc,
		@In("coord.minLAcc") Double fMinLAcc,
		@In("coord.maxLAcc") Double fMaxLAcc,
		@In("coord.followerGas") Double fGas,
		@In("coord.followerBrake") Double fBrake,

		@In("member.lAcc") Double lAcc,
		@In("member.lMinAcc") Double lMinAcc,
		@In("member.lMaxAcc") Double lMaxAcc,
		@In("member.leaderGas") Double lGas,
		@In("member.leaderBrake") Double lBrake
	){
	return true;
}

@KnowledgeExchange
@PeriodicScheduling(200)
public static void map(
	@In("member.lPos") Double lPos,
	@In("member.lSpeed") Double lSpeed,
	@In("member.lAcc") Double lAcc,
	@In("member.lMinAcc") Double lMinAcc,
	@In("member.lMaxAcc") Double lMaxAcc,
	@In("member.eTimePeriod") Double lETimePeriod,
	@In("member.creationTime") Double lECreationTime,
	@In("member.timePeriod") Double lTimePeriod,
	@In("member.lastTime") Double lLastTime,
	@In("member.initTime") Double lInitTime,

	@Out("coord.currentLPos") OutWrapper<Double> fCurrentLPos,
	@Out("coord.currentLSpeed") OutWrapper<Double> fCurrentLSpeed,
	@Out("coord.lAcc") OutWrapper<Double> fLAcc,
	@Out("coord.minLAcc") OutWrapper<Double> fMinLAcc,
	@Out("coord.maxLAcc") OutWrapper<Double> fMaxLAcc,
	@Out("coord.lETimePeriod") OutWrapper<Double> fLETimePeriod,
	@Out("coord.lECreationTime") OutWrapper<Double> fLECreationTime,
	@Out("coord.lTimePeriod") OutWrapper<Double> fLTimePeriod,
	@Out("coord.lCreationTime") OutWrapper<Double> fLCreationTime,
	@Out("coord.lInitTime") OutWrapper<Double> fLInitTime
) {

	fCurrentLPos.value = lPos;
	fCurrentLSpeed.value = lSpeed;
	fLAcc.value = lAcc;
	fMinLAcc.value = lMinAcc;
	fMaxLAcc.value = lMaxAcc;
	fLETimePeriod.value = lETimePeriod;
	fLECreationTime.value = lECreationTime;
	fLTimePeriod.value =  lTimePeriod;
	fLCreationTime.value = lLastTime;
	fLInitTime.value = lInitTime;
//	System.out.println("%%%%%%%%%%%%%%%% f "+fLAcc.value+"  ,  l "+lAcc);
	
}

}


