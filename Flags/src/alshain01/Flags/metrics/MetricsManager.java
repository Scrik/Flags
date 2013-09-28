package alshain01.Flags.metrics;

import java.io.IOException;

import alshain01.Flags.Director;
import alshain01.Flags.Flags;
import alshain01.Flags.metrics.Metrics.Graph;

public class MetricsManager {
	
	public static void StartMetrics() {
		try {
		    Metrics metrics = new Metrics(Flags.instance);
	
		    // Construct a graph, which can be immediately used and considered as valid
		    Graph claimGraph = metrics.createGraph("Land System");
		    for(Director.LandSystem system : Director.LandSystem.values()){
		    	if(Director.getSystem() == system) {
			    	claimGraph.addPlotter(new Metrics.Plotter(system.getDisplayName()) {
			            @Override
			            public int getValue() {
		            		return 1;
			            }
			    	});
		    	}
		    }
		    		    
		    metrics.start();
		} catch (IOException e) {
		    Flags.instance.getLogger().info(e.getMessage());
		}
	}
}