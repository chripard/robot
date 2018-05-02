package domain;

import java.util.ArrayList;

import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import simbad.sim.Agent;

	
public class MyRobot extends Agent {
	    private int[] goal;
	    private int[][] blocks;
	    private double t;
	    private boolean rotate;
	    private double dist;
		private int size;
		private int[] init;
		private Vector3d fgoal;
		private ArrayList<int[]> path;
		private int[] initvar;
		private int initx;
		private int inity;
		private int timer = 0;
		
		
	    
	    public MyRobot (Vector3d position, String name, int size, int[] init, int[] goal, int[][] blocks) 
	    {
	        super(position,name);
	        this.size = size;
	        this.init = init;
	        this.goal = goal;
	        this.blocks = blocks;
	        this.initx = init[0];
	        this.inity = init[1];
	    }
	    
	    public void initBehavior() 
	    {	
	    	
	    }
	    
	    public double getAngletoPoint(Vector3d p)
	    {
	        double f;
	        if ((p.x==0) && (p.z==0))
	            f=0;
	        else
	        {
	            if (p.x==0)
	            {
	               if (p.z>0) 
	                   f = Math.PI/2;
	               else
	                   f = -Math.PI/2;
	            }
	            else
	            {
	                if (p.x>0)
	                    f = Math.atan(p.z/p.x);
	                else
	                    f = Math.atan(p.z/p.x)+Math.PI;
	            }
	            if (f<0)
	                f+=Math.PI*2;
	        }
	        return f;    
	    }
	    
	    public Vector3d getLocalCoords(Vector3d p)
	    {
	        Vector3d a = new Vector3d();
	        Point3d r = new Point3d();
	        double th = getAngle();        
	        double x,y,z;
	        getCoords(r);
	        x=p.getX() - r.x;
	        z=-p.getZ()+ r.z;        
	        a.setX(x*Math.cos(th) + z*Math.sin(th));
	        a.setZ(z*Math.cos(th) - x*Math.sin(th));
	        a.setY(p.y);
	        return a;
	    }    
	    
	    public double getAngle()
	    {
	        double angle=0;
	        double msin; 
	        double mcos;              
	        Transform3D m_Transform3D=new Transform3D();
	        this.getRotationTransform(m_Transform3D);        
	        Matrix3d m1 = new Matrix3d();
	        m_Transform3D.get( m1 );                
	        msin=m1.getElement( 2, 0 );
	        mcos=m1.getElement( 0, 0 );        
	        if (msin<0)
	        {
	            angle = Math.acos(mcos);
	        }
	        else            
	        {
	            if (mcos<0)
	            {
	                angle = 2*Math.PI-Math.acos(mcos);
	            }
	            else
	            {            
	                angle = -Math.asin(msin);
	            }
	        }
	        while (angle<0)
	            angle+=Math.PI*2;
	        return angle;
	    }    
	    
	    public void performBehavior() 
	    { 	
	    	
	    	timer = decTimer(timer); // timer to control the behavior of the method
	    	double lt;	    		
	    	
	    	if(timer < 0) {	    	
	    		timer = 120;
	    		path = AStar.run(size, size, initx, inity, goal[0], goal[1], blocks);
	    		if (path==null) System.exit(1); // if no possible path exists, exit.
	    		if (path.size()!=0) {	    		
	    			Vector3d lg;
	    			initvar = path.get(0); // get coordinates of the first step
	    			initx = initvar[0];  
	    			inity = initvar[1];	    
	    			fgoal = transCoords(path.get(0),size); // transform coordinates for simbad world
	    			lg = getLocalCoords(fgoal);
	    			double ph = this.getAngletoPoint(lg);        
	    			double rv=0.5;  
	    			if (ph>Math.PI)
	    			{
	    				ph = ph - Math.PI*2;
	    				rv *= -1;
	    			}
	    			lt=this.getLifeTime();
	    			t= ph/rv+lt;
	    			rotate=true;
	    			dist = Math.sqrt(lg.x*lg.x + lg.z*lg.z);
	    			this.setRotationalVelocity(rv);
	    			}	    		
	    		}
	    		    	
	    		lt=this.getLifeTime();
	    		
	    		if (lt>=t)
	    		{
	    			if (rotate)
	    			{	    				
	                	this.setRotationalVelocity(0);
	                	rotate=false;
	                	double tv = 1.5;
	                	t = dist/tv +lt;
	                	this.setTranslationalVelocity(tv);
	    			}
	    			else {
	    				this.setTranslationalVelocity(0);
	    			}  
	    		} 		    	
	    	}
	    	
	  
	    
	    
	  private Vector3d transCoords(int[] coord, int size) {
		  int x = coord[0];
		  int y = coord[1];	
		  double avg = (double)size/2;
		  double x2 = avg*(-1);					
		  double transX = (double)x+x2 + 0.5;
		  double transY = (double)y+x2 + 0.5;			  
		  return new Vector3d(coords(transX,transY));  
	  }
	  
	  private Vector3d coords(double x, double y) {
			return new Vector3d(-y, 0, -x);
	  }
	  
	  private int decTimer(int timer) {
		  timer--;
		  return timer;
	  }

}

