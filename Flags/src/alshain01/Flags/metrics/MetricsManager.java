package alshain01.Flags.metrics;

import java.io.IOException;

import alshain01.Flags.Director;
import alshain01.Flags.Flags;
import alshain01.Flags.metrics.Metrics.Graph;

public class MetricsManager {
	
	public static void StartMetrics() {
		try {
		    Metrics metrics = new Metrics(Flags.instance);
	
		    // Land System Graph
		    Graph systemGraph = metrics.createGraph("Land System");
		    for(Director.LandSystem system : Director.LandSystem.values()){
		    	if(Director.getSystem() == system) {
			    	systemGraph.addPlotter(new Metrics.Plotter(system.getDisplayName()) {
			            @Override
			            public int getValue() {
		            		return 1;
			            }
			    	});
		    	}
		    }
		    
		    // Economy Graph
		    Graph econGraph = metrics.createGraph("Economy Enabled");
		    if(Flags.instance.economy == null) {	    	
		    	econGraph.addPlotter(new Metrics.Plotter("No") {
		    		@Override
		            public int getValue() {
	            		return 1;
		            }
		    	});
		    } else {
		    	econGraph.addPlotter(new Metrics.Plotter("Yes") {
		            @Override
		            public int getValue() {
		            	return 1;
			        }
			    });
		    }
		    		    
		    metrics.start();
		} catch (IOException e) {
		    Flags.instance.getLogger().info(e.getMessage());
		}
	}
}