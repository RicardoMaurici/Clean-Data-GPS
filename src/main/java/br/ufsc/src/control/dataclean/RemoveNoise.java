package br.ufsc.src.control.dataclean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.ufsc.src.control.Utils;
import br.ufsc.src.control.entities.TPoint;
import br.ufsc.src.control.entities.Trajectory;
import br.ufsc.src.persistencia.InterfacePersistencia;
import br.ufsc.src.persistencia.Persistencia;
import br.ufsc.src.persistencia.exception.DBConnectionException;

public class RemoveNoise {

	private ConfigTraj configTraj;
	private InterfacePersistencia persistencia;
	
	public RemoveNoise (InterfacePersistencia persistencia, ConfigTraj configTraj){
		this.configTraj = configTraj;
		this.persistencia = persistencia;
	}

	public void findRemoveNoise(Set<Integer> tids) throws DBConnectionException, SQLException {
		for (Integer tid : tids) {
			Trajectory traj = persistencia.fetchTrajectory(tid, configTraj, configTraj.getColumnName("TID"));
			if(configTraj.isRemoveNoiseFromFirst()){
				double speed = Double.parseDouble(configTraj.getSpeed());
				removeFromFirst(traj, speed);
			}else if(configTraj.isRemoveNoiseFromSecond()){
				double speed = Double.parseDouble(configTraj.getSpeed());
				removeFromSecond(traj, speed);
			}else if(configTraj.isDbscan()){
				dbscan(traj);
			}else if(configTraj.isMeanFilter()){
				
			}else if(configTraj.isMedianFilter()){
				
			}
		}
	}
	
	private void dbscan(Trajectory traj) throws DBConnectionException, SQLException{
		if(traj.length() < configTraj.getMinPoints())
			return;
        List<Integer> gidsToRemove = new ArrayList<Integer>();
        int minPoints = configTraj.getMinPoints();
        double maxDistance = configTraj.getDistancePoints();
        int i = 0;
        while (i < traj.length()) {
        	int nearPoints = 0;
        	TPoint p = traj.getPoint(i);
        	if(traj.hasNext(i)){
        		int numNoises = 0;
        		for(int cont = 1; i+cont < traj.length() && (numNoises < minPoints && nearPoints < minPoints); cont++){
        			TPoint pn = traj.getPoint(i+cont);
        			double distance = Utils.euclidean(p, pn);
        			if(distance <= maxDistance){
        				nearPoints++;
        				numNoises = 0;
        			}else
        				numNoises++;
        		}     	
        	}
        	if(nearPoints < minPoints && traj.hasPrevious(i)){
        		int numNoises = 0;
        		for(int cont = 1; i-cont >= 0 && (numNoises < minPoints && nearPoints < minPoints); cont++){
        			TPoint pn = traj.getPoint(i-cont);
        			double distance = Utils.euclidean(p, pn);
        			if(distance <= maxDistance){
        				nearPoints++;
        				numNoises = 0;
        			}else
        				numNoises++;
        		}
        	}
        	if(nearPoints < minPoints){
        		gidsToRemove.add(p.getGid());
        		traj.getPoints().remove(i);
        	}else
        		i++;
        }
        persistencia.deleteByGids(gidsToRemove, configTraj.getTableNameOrigin());
      
	}

	private void removeFromFirst(Trajectory traj, double speed) throws DBConnectionException, SQLException {
		Trajectory t = traj;
		while(hasExtremeNoiseTraj(t, speed))
			 t = removeFirstExtremeNoise(traj, speed);
	}
	
	private Trajectory removeFirstExtremeNoise(Trajectory T, double maxSpeed) throws DBConnectionException, SQLException {
        int i = 0;
        List<Integer> gidsToRemove = new ArrayList<Integer>();
        while (i < T.length() - 1) {
            TPoint p1 = T.getPoint(i);
            TPoint p2 = T.getPoint(i + 1);
            long timeDiff = (p2.getTime() - p1.getTime()) / 1000;
            double distance = Utils.euclidean(p1, p2);
            double speed = distance / (double) timeDiff;
            if (speed > maxSpeed) {
            	gidsToRemove.add(T.getPoint(i).getGid());
                T.getPoints().remove(i);
            } else {
                i++;
            }

        }
        persistencia.deleteByGids(gidsToRemove,configTraj.getTableNameOrigin());
        return T;
    }
	
	private void removeFromSecond(Trajectory traj, double speed) throws DBConnectionException, SQLException {
		Trajectory t = traj;
		while(hasExtremeNoiseTraj(t, speed))
			t = removeSecondExtremeNoise(traj, speed);
	}
	
	private Trajectory removeSecondExtremeNoise(Trajectory T, double maxSpeed) throws DBConnectionException, SQLException {
        int i = 0;
        List<Integer> gidsToRemove = new ArrayList<Integer>();
        while (i < T.length() - 1) {
            TPoint p1 = T.getPoint(i);
            TPoint p2 = T.getPoint(i + 1);
            long timeDiff = (p2.getTime() - p1.getTime()) / 1000;
            double distance = Utils.euclidean(p1, p2);
            double speed = distance / (double) timeDiff;
            if (speed > maxSpeed) {
                gidsToRemove.add(T.getPoint(i+1).getGid());
                T.getPoints().remove(i+1);
            } else {
                i++;
            }
        }
        persistencia.deleteByGids(gidsToRemove,configTraj.getTableNameOrigin());
        return T;
    }
	
	private boolean hasExtremeNoiseTraj(Trajectory T, double maxSpeed) {
		for (int i = 0; i < T.length() - 1; i++) {
			TPoint p1 = T.getPoint(i);
			TPoint p2 = T.getPoint(i + 1);
			long timeDiff = (p2.getTime() - p1.getTime()) / 1000;
			double distance = Utils.euclidean(p1, p2);
			double speed = distance / (double) timeDiff;;
			if (speed > maxSpeed) {
				return true;
	        }
	     }
	     return false;
	 }
	
	
	/*  int i = 0;
      while (i < traj.length()) {    //para cada point
      	TPoint p1 = traj.getPoint(i);
      	int numPoints = 0;
      	System.out.println("P1 "+p1.getGid());
      	
      	if(traj.hasNext(i)){ //se tem proximo
      		for(int z = 1; z <= configTraj.getMinPoints(); z++){
      			if(i+z < traj.length()){
      				TPoint p = traj.getPoint(i+z);
      				System.out.println("P "+p.getGid());
      				double distance  = Utils.euclidean(p1, p);
      				System.out.println(p1.getGid()+" "+ p.getGid()+" "+distance+" "+configTraj.getDistancePoints());
      				if(distance <= configTraj.getDistancePoints()){
      					System.out.println("incrementando");
      					numPoints++;
      				}
      			}else{
      				System.out.println("break");
      				break;
      			}
      		}
      		int numPrevious = 1;
      		if(traj.hasPrevious(i)){
	        		while(numPoints < configTraj.getMinPoints()){
	        			if(traj.hasPrevious(i-numPrevious-1)){
	        				TPoint p = traj.getPoint(i-numPrevious);
	        				System.out.println("numPrevious "+p.getGid());
	        				double distance  = Utils.euclidean(p1, p);
	        				if(distance <= configTraj.getDistanceMax()){
	        					System.out.println("incrementando");
	        					numPoints++;	
	        				}
	        			}else{
	        				break;
	        			}
	        			numPrevious++;
	        		}
      		}
      		System.out.println("aqui");
      		
      		
      	}else if(traj.hasPrevious(i)){
      		
      	}   	
      	System.out.println(" Final while "+numPoints);
      	if(numPoints < configTraj.getMinPoints())
      		System.out.println("Deletar "+p1.getGid() +" "+ numPoints);
      	
      	i++;
         

      }*/

}