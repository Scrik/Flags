package alshain01.Flags.metrics;

import java.io.IOException;

import alshain01.Flags.Director;
import alshain01.Flags.Flags;
import alshain01.Flags.metrics.Metrics.Graph;

public class MetricsManager {
	private MetricsManager(){}
	
	public static void StartMetrics() {
		try {
		    Metrics metrics = new Metrics(Flags.getInstance());
	
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
		    
		    // Flag groups installed
		    Graph groupGraph = metrics.createGraph("Flag Groups");
		    for(String group : Flags.getRegistrar().getFlagGroups()) {
		    	groupGraph.addPlotter(new Metrics.Plotter(group) {
		            @Override
		            public int getValue() {
	            		return 1;
		            }
		    	});
		    }
		    
		    // Border Patrol Status
		    Graph bpGraph = metrics.createGraph("BorderPatrol Enabled");
		    if(Flags.getInstance().getConfig().getBoolean("Flags.BorderPatrol.Enable")) {	    	
		    	bpGraph.addPlotter(new Metrics.Plotter("Enabled") {
		    		@Override
		            public int getValue() {
	            		return 1;
		            }
		    	});
		    } else {
		    	bpGraph.addPlotter(new Metrics.Plotter("Disabled") {
		            @Override
		            public int getValue() {
		            	return 1;
			        }
			    });
		    }
		    	
		    // Auto Update settings
		    Graph updateGraph = metrics.createGraph("Update Configuration");
		    if(!Flags.getInstance().getConfig().getBoolean("Flags.Update.Check")) {
		    	updateGraph.addPlotter(new Metrics.Plotter("No Updates") {
		            @Override
		            public int getValue() {
		            	return 1;
		            }
		    	});
		    } else if(!Flags.getInstance().getConfig().getBoolean("Flags.Update.Download")) {
		    	updateGraph.addPlotter(new Metrics.Plotter("Check for Updates") {
		            @Override
		            public int getValue() {
		            	return 1;
		            }
		    	});
			} else {
			   	updateGraph.addPlotter(new Metrics.Plotter("Download Updates") {
		            @Override
		            public int getValue() {
		            	return 1;
		            }
		    	});
			}
		    
		    // Economy Graph
		    Graph econGraph = metrics.createGraph("Economy Enabled");
		    if(Flags.getEconomy() == null) {	    	
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
		    Flags.getInstance().getLogger().info(e.getMessage());
		}
	}
}