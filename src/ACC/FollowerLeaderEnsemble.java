package ACC;

import cz.cuni.mff.d3s.deeco.annotations.In;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.KnowledgeExchange;
import cz.cuni.mff.d3s.deeco.annotations.Membership;
import cz.cuni.mff.d3s.deeco.annotations.Out;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.ensemble.Ensemble;
import cz.cuni.mff.d3s.deeco.knowledge.OutWrapper;

public class FollowerLeaderEnsemble {

		@Membership
		public static boolean membership(
				@In("coord.name") String fName,
				@In("coord.leaderSpeed") Double leaderSpeed,
				@In("coord.currentLAcc") Double fLAcc,
				@In("coord.currentLPos") Double fLPos,
				@In("coord.lastTime") Double fLastTime,
				@In("member.name") String lName,
				@In("member.lSpeed") Double lSpeed,
				@In("member.currentLAcc") Double lAcc,
				@In("member.currentLPos") Double lPos,
				@In("member.lastTime") Double lLastTime
				){
			return true;
		}

		@KnowledgeExchange
		@PeriodicScheduling(50)
		public static void map(
				@Out("coord.leaderSpeed") OutWrapper<Double> leaderSpeed,
				@Out("coord.currentLAcc") OutWrapper<Double> fLAcc,
				@Out("coord.currentLPos") OutWrapper<Double> fLPos,
				@Out("coord.lCreationTime") OutWrapper<Double> fLLastTime,
				@Out("coord.lTimePeriod") OutWrapper<Double> fLTimePeriod,
				@Out("coord.minLAcc") OutWrapper<Double> fMinLAcc,
				@Out("coord.maxLAcc") OutWrapper<Double> fMaxLAcc,
				
				@In("member.lSpeed") Double lSpeed,
				@In("member.currentLAcc") Double lAcc,
				@In("member.minLAcc") Double minLAcc,
				@In("member.maxLAcc") Double maxLAcc,
				@In("member.currentLPos") Double lPos,
				@In("member.lastTime") Double lLastTime,
//				@In("member.timePeriod") Double timePeriod,
				@In("member.creationTimePeriod") Double creationTimePeriod

			) {
		
			leaderSpeed.value = lSpeed;
			fLAcc.value = lAcc;
			fMinLAcc.value = minLAcc;
			fMaxLAcc.value = maxLAcc;
			fLPos.value = lPos;
			fLLastTime.value = lLastTime;
			fLTimePeriod.value = creationTimePeriod;

		}
}
