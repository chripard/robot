package domain;
import java.util.*;


public class AStar {
	
    public static final int DIAGONAL_COST = 14; //cost if the next square is diagonal
    public static final int V_H_COST = 10; //cost if the next square is either up,down,right,left
    
    static class Cell {  
        double heuristicCost = 0; //Heuristic cost
        double finalCost = 0; //G+H
        int i, j;
        Cell parent; 
        
        Cell(int i, int j){
            this.i = i;
            this.j = j; 
        }
        
        @Override
        public String toString(){
            return "["+this.i+", "+this.j+"]";
        }
    }
    
    //Blocked cells are just null Cell values in grid
    static Cell [][] grid = new Cell[5][5];
    
    static PriorityQueue<Cell> open;
     
    static boolean closed[][];
    static int startI, startJ;
    static int endI, endJ;
            
    public static void setBlocked(int i, int j){
        grid[i][j] = null;
    }
    
    public static void setStartCell(int i, int j){
        startI = i;
        startJ = j;
    }
    
    public static void setEndCell(int i, int j){
        endI = i;
        endJ = j; 
    }
    
    static void checkAndUpdateCost(Cell current, Cell t, double d){
        if(t == null || closed[t.i][t.j])return;
        double t_final_cost = t.heuristicCost+d;
        
        boolean inOpen = open.contains(t);
        if(!inOpen || t_final_cost<t.finalCost){
            t.finalCost = t_final_cost;
            t.parent = current;
            if(!inOpen)open.add(t);
        }
    }
    
    public static void AStarAlgo(){ 
        
        //add the start location to open list.
        open.add(grid[startI][startJ]);
        
        Cell current;
        
        while(true){ 
            current = open.poll();
            if(current==null)break;
            closed[current.i][current.j]=true; 

            if(current.equals(grid[endI][endJ])){
                return; 
            } 

            Cell t;  
            if(current.i-1>=0){
                t = grid[current.i-1][current.j];
                checkAndUpdateCost(current, t, current.finalCost+V_H_COST); 

                if(current.j-1>=0){                      
                    t = grid[current.i-1][current.j-1];
                    checkAndUpdateCost(current, t, current.finalCost+DIAGONAL_COST); 
                }

                if(current.j+1<grid[0].length){
                    t = grid[current.i-1][current.j+1];
                    checkAndUpdateCost(current, t, current.finalCost+DIAGONAL_COST); 
                }
            } 

            if(current.j-1>=0){
                t = grid[current.i][current.j-1];
                checkAndUpdateCost(current, t, current.finalCost+V_H_COST); 
            }

            if(current.j+1<grid[0].length){
                t = grid[current.i][current.j+1];
                checkAndUpdateCost(current, t, current.finalCost+V_H_COST); 
            }

            if(current.i+1<grid.length){
                t = grid[current.i+1][current.j];
                checkAndUpdateCost(current, t, current.finalCost+V_H_COST); 

                if(current.j-1>=0){
                    t = grid[current.i+1][current.j-1];
                    checkAndUpdateCost(current, t, current.finalCost+DIAGONAL_COST); 
                }
                
                if(current.j+1<grid[0].length){
                   t = grid[current.i+1][current.j+1];
                    checkAndUpdateCost(current, t, current.finalCost+DIAGONAL_COST); 
                }  
            }
        } 
    }
    public static ArrayList<int[]> run(int x, int y, int si, int sj, int ei, int ej, int[][] blocked){

         //Reset
        grid = new Cell[x][y];
        closed = new boolean[x][y];
        open = new PriorityQueue<>((Object o1, Object o2) -> {
             Cell c1 = (Cell)o1;
             Cell c2 = (Cell)o2;

             return c1.finalCost<c2.finalCost?-1:
                     c1.finalCost>c2.finalCost?1:0;
         });
        //Set start position
        setStartCell(si, sj);  
        
        //Set End Location
        setEndCell(ei, ej); 
        
        
        for(int i=0;i<x;++i){        	
           for(int j=0;j<y;++j){
        	   int blcounter = 0;
               grid[i][j] = new Cell(i, j);
               int dx = endI-i;
               int dy = endJ-j;
               double d = Math.sqrt(Math.pow(dx,2)+Math.pow(dy, 2)); // Pythagorean Theorem   
               
               /* find the number of blocks
                * contained in the rectangle
                */
               for (int k=0; k < blocked.length;k ++) {
            	   if(blocked[k][0] >= i && blocked[k][1]>=j)
            		   blcounter++;								
               }
               // divide with the number of blocks contained in the whole grid
               double blocksNum = (double)blcounter/(double)blocked.length;    
               // add above value multiplied by d to Manhattan distance 
               grid[i][j].heuristicCost = Math.abs(i-endI)+Math.abs(j-endJ)+(d*blocksNum);
           }
        }
        grid[si][sj].finalCost = 0;
        
        /*
          Set blocked cells. Simply set the cell values to null
          for blocked cells.
        */
        for(int i=0;i<blocked.length;++i){
            setBlocked(blocked[i][0], blocked[i][1]);
        }
        
        AStarAlgo();
        ArrayList<Cell> path = new ArrayList<Cell>();
         
        if(closed[endI][endJ]){
            //Trace back the path 
             Cell current = grid[endI][endJ];
             while(current.parent!=null){
                 path.add(0, current);
                 current = current.parent;
             } 
   
            ArrayList<int[]> path_coordinates = new ArrayList<int[]>();
 			int counter = 0;
 			for (Cell c : path) {
 				path_coordinates.add(new int[2]);
 				path_coordinates.get(counter)[0] = c.i;
 				path_coordinates.get(counter)[1] = c.j;
 				counter++;
 			}
 			
 			return path_coordinates;

             
        } else System.out.println("\nNo possible path");
        		return null; 
    }
}

