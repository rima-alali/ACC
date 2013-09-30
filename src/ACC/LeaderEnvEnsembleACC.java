package ACC;

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
			@In("coord.name") String cName,
			@In("coord.lSpeed") Double lSpeed,
			@In("coord.leaderGas") Double leaderGas,
			@In("coord.leaderBrake") Double leaderBrake,
			@In("member.name") String mName,
			@In("member.eLeaderGas") Double eLeaderGas,
			@In("member.eLeaderBrake") Double eLeaderBrake,
			@In("member.eLeaderSpeed") Double eLeaderSpeed
			){
		return true;
	}

	@KnowledgeExchange
	@PeriodicScheduling(50)
	public static void map(
			@In("coord.leaderGas") Double lGas,
			@In("coord.leaderBrake") Double lBrake,
			@In("coord.lastTime") Double lLTime,
			
			@In("member.eLeaderSpeed") Double eLSpeed,
			@In("member.lastTime") Double eTime,
			@In("member.timePeriod") Double etimePeriod,
			@In("member.minLAcc") Double eMinLAcc,
			@In("member.maxLAcc") Double eMaxLAcc,
			
			@Out("member.eLeaderGas") OutWrapper<Double> eLGas,
			@Out("member.eLeaderBrake") OutWrapper<Double> eLBrake,
			@Out("member.elCreationTime") OutWrapper<Double> eLTime,
			
			@Out("coord.lSpeed") OutWrapper<Double> speed,
			@Out("coord.creationTime") OutWrapper<Double> time,
			@Out("coord.eTimePeriod") OutWrapper<Double> leTimePeriod,
			@Out("coord.lMinAcc") OutWrapper<Double> lMinAcc,
			@Out("coord.lMaxAcc") OutWrapper<Double> lMaxAcc
			
		) {
		
		eLGas.value = lGas;
		eLBrake.value = lBrake;
		eLTime.value = lLTime;
		
		leTimePeriod.value = etimePeriod;
		speed.value = eLSpeed; 
		time.value = eTime;
		lMinAcc.value = eMinLAcc;
		lMaxAcc.value = eMaxLAcc;
	}
	
}

