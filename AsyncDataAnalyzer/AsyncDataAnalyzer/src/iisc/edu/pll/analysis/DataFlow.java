package iisc.edu.pll.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import iisc.edu.pll.data.DPath;
import iisc.edu.pll.data.ModuleEdge;
import iisc.edu.pll.data.ModuleNode;
import iisc.edu.pll.data.ModuleGraph;
import iisc.edu.pll.data.NodePair;
import iisc.edu.pll.data.Statement;
import iisc.edu.pll.data.lattice.TFunction;
import iisc.edu.pll.data.lattice.TFunctionFactory;

public class DataFlow {

	
	
	private HashMap<NodePair, HashSet<DPath>> CoverPaths;
	private String latticetype;
	private ArrayList<String> vars;
	private HashSet<String> messages;
	private ModuleGraph mainGraph;
	
	
	
	public DataFlow(String type, ArrayList<String> v, HashSet<String> messageSet, ModuleGraph g)
	{
		latticetype = type;
		vars = new ArrayList<>(v);
		messages = new HashSet<>(messageSet);
		mainGraph = g;
		CoverPaths = new HashMap<>();
		for(ModuleNode n1 : mainGraph.getNodes())
			for(ModuleNode n2 : mainGraph.getNodes())
			{
				NodePair pair = new NodePair(n1, n2);
				HashSet<DPath> pathset = new HashSet<>(); 
				if(n1.getId()== n2.getId())
					pathset.add(new DPath(latticetype, vars, messages)); //add empty path
				CoverPaths.put(pair, pathset);			
					
			}
	}
	
	public void StartAnalysis(){
		
		HashSet<DPath> addedpaths = new HashSet<>();
		do{
			addedpaths = new HashSet<>();
			for(NodePair pair : CoverPaths.keySet())
			{
				ModuleNode source  = pair.getSource();
				ModuleNode target = pair.getTarget();
				HashSet<DPath> pathset= CoverPaths.get(pair);
				if(pathset.isEmpty())
					continue;
				
				HashSet<ModuleNode> preds = mainGraph.getPredecessor(source);
				//for al lpreds, get the edges, compose the transfer function with the current path set, and check domination
				for(ModuleNode pred:preds)
				{
					ModuleEdge edge = getEdge(mainGraph, pred, source);
					for(DPath path : pathset)
					{
						TFunction fun = edge.gettFunc().compose(path.getFunc());
						HashMap<String, Integer> demand = getDemand(edge.getDelta(), path.getDemand());
						NodePair pair2 = getPair(pred, target);
						HashSet<DPath> pathset2 = CoverPaths.get(pair2);
						if(!Covered(fun, demand,pathset2)){
							DPath p = new DPath(latticetype, vars, messages);
							p.setFunc(fun);
							p.setDemand(demand);
							pathset2.add(p);
							addedpaths.add(p);
							//System.out.println("adding path : " + p +"for nodes " + pair2);
						}
					}
				}
			}
			
		}while(!addedpaths.isEmpty());
		
		
		
	}
	
	
	private NodePair getPair(ModuleNode source, ModuleNode target) {
		for(NodePair pair : CoverPaths.keySet())
		{
			if(pair.getSource().getId()== source.getId() && pair.getTarget().getId()== target.getId())
				return pair;
		}
		return null;
	}

	private boolean Covered(TFunction fun, HashMap<String, Integer> demand, HashSet<DPath> pathset) {
		
		HashSet<DPath> lowerDemPaths = getLowerDemandPaths(demand, pathset);
		TFunction joinedVal = TFunctionFactory.createFunction(latticetype,Statement.EMPTY, vars, new ArrayList<>());
		for(DPath path : lowerDemPaths)
		{
			joinedVal = (TFunction) joinedVal.join(path.getFunc());
		}
		
		if(joinedVal.isGreater(fun))
			return true;
		
		
		return false;
	}

	private HashSet<DPath> getLowerDemandPaths(HashMap<String, Integer> demand, HashSet<DPath> pathset) {
		HashSet<DPath> paths = new HashSet<>();
		
		for(DPath path :  pathset)
		{
			if(isHigher(demand, path.getDemand()))
			{
				paths.add(path);
			}
		}
		return paths;
	}

	private boolean isHigher(HashMap<String, Integer> demand, HashMap<String, Integer> pathDemand) {
		
		for(String msg : demand.keySet())
		{
			if(demand.get(msg) < pathDemand.get(msg))
				return false;
		}
		
		return true;
	}

	private HashMap<String, Integer> getDemand(HashMap<String, Integer> delta, HashMap<String, Integer> demand) {
		HashMap<String,Integer> newDemand = new HashMap<>();
		for(String msg : demand.keySet())
		{
			int del = delta.get(msg);
			int dem = demand.get(msg);
			int newDem = dem - del ;
			newDem = newDem >=0 ? newDem : 0;
			newDemand.put(msg, newDem);
		}
		
		
		return newDemand;
	}

	private ModuleEdge getEdge(ModuleGraph mainGraph, ModuleNode pred, ModuleNode source) {
		for(ModuleEdge edge : mainGraph.getEdges())
		{
			if(edge.getFrom().getId()==pred.getId() && edge.getTo().getId()== source.getId())
				return edge;
		}
		return null;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void writeResultToFile(String filename) {
		try{
			FileWriter writer = new FileWriter(filename);
			BufferedWriter bwriter = new BufferedWriter(writer);
			StringBuilder content = new StringBuilder();
			content.append("The final set of paths per pair of program points \n");
			content.append("total number of bins :" + CoverPaths.keySet().size() + "\n");
			content.append("\n\n");
			for(NodePair pair : CoverPaths.keySet())
			{
				content.append(pair.toString() + " :\n");
				for(DPath path : CoverPaths.get(pair))
				{
					content.append("-----------------------------------------------\n");
					content.append(path.toString() + "\n");
					content.append("-----------------------------------------------\n");
				}
				content.append("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n");
			}
			
			content.append("\n\n------------------------------------------------");
			
			bwriter.write(content.toString());
			bwriter.close();
			writer.close();
		}
		catch(IOException ioe)
		{
			System.out.println("error while writing to file " +filename);
		}
		
	}

}
