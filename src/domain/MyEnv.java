package domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import domain.MyRobot;
import simbad.gui.Simbad;
import simbad.sim.Box;
import simbad.sim.CherryAgent;
import simbad.sim.EnvironmentDescription;

public class MyEnv {
	
	private String inputFile;

	public MyEnv (String inputFile) {
		this.inputFile = inputFile;	
	}

	
	public void createEnv () {
				EnvironmentDescription environment = new EnvironmentDescription();
				try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) { 	
		        	String rpos=null;
		        	String gpos=null;
					String sCurrentLine;
					String firstline = br.readLine();
					String[] parts = firstline.split(" ");
					Integer width = Integer.parseInt(parts[0]);
					Integer length = Integer.parseInt(parts[1]);
					int[] init = null;
					int[] goal = null;
					List<int[]> bl = new ArrayList<int[]>();
					
					// set world size - compute axis values
					environment.setWorldSize(width);
					double avg = (double)width/2;
					double x = avg*(-1);					
					double row = x + 0.5;	
					int j=0;
					
					/* Hold coordinates for A* grid. Transform
					 * coordinates to match the newly created simbad world.
					 */
					List<String> blocks = new ArrayList<String>();
					while((sCurrentLine = br.readLine())!=null) {						
						for (int i=0 ; i < sCurrentLine.length(); i++){
							if(sCurrentLine.charAt(i)=='R')	{	
								 init = new int[]{j,i};					// A* coordinates			
								 rpos = row +","+ (i-(avg-0.5));		// Simbad coordinates
							}
							if(sCurrentLine.charAt(i)=='X') {
								 bl.add(new int[]{j,i});
								 blocks.add( row +","+ (i-(avg-0.5)));	
							}
							if(sCurrentLine.charAt(i)=='G') {
								 goal = new int[]{j,i};
								 gpos = row +","+ (i-(avg-0.5));	
							}
						}
						j++;
						row+=1;
					}
					
					int[][] bltable = listToArray(bl);
					String[] blocksArray = new String[ blocks.size() ];
					blocks.toArray(blocksArray);
					Vector3d[] blocksVec = new Vector3d[blocksArray.length];
					Vector3f boxvec = new Vector3f(0.5f, 0.5f, 0.5f);
					
					// add the blocks
					for (int i=0; i < blocksArray.length; i++) {						
						String [] coords = blocksArray[i].split(",");
						blocksVec[i] = coords(Double.parseDouble(coords[0]),Double.parseDouble(coords[1]));
						environment.add(new Box(blocksVec[i],boxvec,environment));
					}	
					
					// add robot and goal
					String[] rcoords = rpos.split(",");
					String[] gcoords = gpos.split(",");					
					Vector3d p = coords(Double.parseDouble(gcoords[0]),Double.parseDouble(gcoords[1]));
					CherryAgent ca = new CherryAgent(p, "goal", 0.2f);
					Vector3d ro = coords(Double.parseDouble(rcoords[0]),Double.parseDouble(rcoords[1]));
					MyRobot r = new MyRobot(ro, "my robot", width, init, goal, bltable);
					environment.add(ca);      
					environment.add(r);
					Simbad frame = new Simbad(environment, false);
				
		 		} catch (IOException e) {
		 				e.printStackTrace();
		 		}		
	}
	
	private int[][] listToArray(List<int[]> bl) {
		int [][] bltable = new int[bl.size()][];
		for (int k=0; k< bl.size(); k++){
			int [] rows = bl.get(k);
			int [] col = new int[rows.length];
			for (int l=0 ; l < rows.length ; l++)
				col[l] = rows[l];
			bltable[k] = col;						
		}
		return bltable;		
	}
	
	// transform coordinates for simbad representation
	private Vector3d coords(double x, double y) {
		return new Vector3d(-y, 0, -x);
	}
}