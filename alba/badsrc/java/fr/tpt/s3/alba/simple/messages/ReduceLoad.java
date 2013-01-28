package fr.tpt.s3.alba.simple.messages;

import fr.tpt.s3.alba.simple.roles.LoadBalancer;
import fr.tpt.s3.alba.simple.roles.Prosumer;
import fr.tpt.s3.cirrus.agent.R;

public class ReduceLoad extends LoadBalancingOrder {

	public ReduceLoad(
			R<LoadBalancer> loadBalancer, 
			LoadPriority priority,
			R<Prosumer> prosumer
	) {
		super(loadBalancer, priority, prosumer);
	}

}
