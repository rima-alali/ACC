package ACC;


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
			@In("coord.name") String cName,
			@In("coord.fSpeed") Double fSpeed,
			@In("coord.followerGas") Double followerGas,
			@In("coord.followerBrake") Double followerBrake,
			@In("member.name") String mName,
			@In("member.lastTime") Double eTime,
			@In("member.eFollowerGas") Double eFollowerGas,
			@In("member.eFollowerBrake") Double eFollowerBrake,
			@In("member.eFollowerSpeed") Double eFollowerSpeed
			){
		return true;
	}

	@KnowledgeExchange
	@PeriodicScheduling(50)
	public static void map(
			@In("coord.followerGas") Double fGas,
			@In("coord.followerBrake") Double fBrake,
			@In("member.eFollowerSpeed") Double eFSpeed,
			@In("member.lastTime") Double eTime,
			@In("coord.lastTime") Double fLastTime,
			@Out("member.eFollowerGas") OutWrapper<Double> ensFGas,
			@Out("member.eFollowerBrake") OutWrapper<Double> ensFbrake,
			@Out("coord.fSpeed") OutWrapper<Double> speed,
			@Out("member.efCreationTime") OutWrapper<Double> eFTime,
			@Out("coord.fCreationTime") OutWrapper<Double> time
		) {
	
		ensFGas.value=fGas;
		ensFbrake.value=fBrake;
		speed.value = eFSpeed;
		eFTime.value = fLastTime;
		time.value = eTime;

	}
}

