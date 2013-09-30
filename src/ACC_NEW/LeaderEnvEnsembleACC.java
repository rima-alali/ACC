package ACC_NEW;

import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.Out;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.ensemble.Ensemble;
import cz.cuni.mff.d3s.deeco.knowledge.OutWrapper;



public class LeaderEnvEnsembleACC extends Ensemble {

@Membership
public static boolean membership(
		@In("coord.leaderGas") Double lGas,
		@In("coord.leaderBrake") Double lBrake,
		@In("coord.lAcc") Double lAcc,
		@In("coord.lMinAcc") Double lMinAcc,
		@In("coord.lMaxAcc") Double lMaxAcc,
		
		@In("member.lAcceleration") Double eLAcceleration,
		@In("member.lMinAcc") Double eLMinAcc,
		@In("member.lMaxAcc") Double eLMaxAcc,
		@In("member.eLeaderGas") Double eLGas,
		@In("member.eLeaderBrake") Double eLBrake
	){
	return true;
	}

@KnowledgeExchange
@PeriodicScheduling(200)
public static void map(
		@In("coord.leaderGas") Double lGas,
		@In("coord.leaderBrake") Double lBrake,
		@In("coord.lastTime") Double lLTime,
		@In("coord.initTime") Double lInitTime,
		
		@In("member.lPos") Double eLPos,
		@In("member.eLeaderSpeed") Double eLeaderSpeed,
		@In("member.lAcceleration") Double eLAcceleration,
		@In("member.lMinAcc") Double eLMinAcc,
		@In("member.lMaxAcc") Double eLMaxAcc,
		@In("member.timePeriod") Double eTimePeriod,
		@In("member.lastTime") Double eTime,
		@In("member.initTime") Double eInitTime,
		
		
		@Out("member.eLeaderGas") OutWrapper<Double> eLGas,
		@Out("member.eLeaderBrake") OutWrapper<Double> eLBrake,
		@Out("member.elCreationTime") OutWrapper<Double> eLCreationTime,
		@Out("member.eLInitTime") OutWrapper<Double> eLInitTime,

		@Out("coord.lPos") OutWrapper<Double> lPos,
		@Out("coord.lSpeed") OutWrapper<Double> lSpeed,
		@Out("coord.lAcc") OutWrapper<Double> lAcc,
		@Out("coord.lMinAcc") OutWrapper<Double> lMinAcc,
		@Out("coord.lMaxAcc") OutWrapper<Double> lMaxAcc,
		@Out("coord.eTimePeriod") OutWrapper<Double> lETimePeriod,
		@Out("coord.creationTime") OutWrapper<Double> lCreationTime,
		@Out("coord.eInitTime") OutWrapper<Double> lEInitTime
		
	) {
	
	eLGas.value = lGas;
	eLBrake.value = lBrake;
	eLCreationTime.value = lLTime;
	eLInitTime.value = lInitTime;
	
	lPos.value = eLPos;
	lSpeed.value = eLeaderSpeed;
	lAcc.value = eLAcceleration;
	lMinAcc.value = eLMinAcc;
	lMaxAcc.value = eLMaxAcc;
	lETimePeriod.value = eTimePeriod;
	lCreationTime.value = eTime;
	lEInitTime.value = eInitTime;
	
}

}

