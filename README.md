# A navigation system for a robot using Simbad (Java 3D robot simulator).

The system's input is a .txt file that contains information about the dimensions of the space, 
the number of the obstacles contained in the space and their exact positions, the initial position of the robot
and lastly the robot's goal position.

e.g

9 9
eeReeeeeee
eeeeeeXeee
eeeΧeeXeee
eeeeeeXeee
eXXXXXXeee
eXeeeeeeee
eXeeeeeeee
eXXXXXeeee
eeeeeeeGee

# Robot Navigation

The robot holds in memory the positions of the obstacles contained in the space along with its own position and computes
the path to the goal position using state space search implemented with A* algorithm. The euristic function
used in the A* algorithm considers both the distance between the states and the density of the obstacles
in the subsection defined by the next possible move.
