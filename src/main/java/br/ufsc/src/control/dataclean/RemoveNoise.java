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

	public void findRemoveNoise(Set<Integer> tids, double speed) throws DBConnectionException, SQLException {
		for (Integer tid : tids) {
			Trajectory traj = persistencia.fetchTrajectory(tid, configTraj, configTraj.getColumnName("TID"));
			if(configTraj.isRemoveNoiseFromFirst()){
				removeFromFirst(traj, speed);
			}else if(configTraj.isRemoveNoiseFromSecond()){
				removeFromSecond(traj, speed);
			}
		}
	}

	private void removeFromFirst(Trajectory traj, double speed) throws DBConnectionException, SQLException {
		Trajectory t = traj;
		while(hasExtremeNoiseTraj(t, speed))
			 t = removeFirstExtremeNoise(traj, speed);
	}
	
	private Trajectory removeFirstExtremeNoise(Trajectory T, double maxSpeed) throws DBConnectionException, SQLException {
        int i = 0;
        List<Integer> gids = new ArrayList<Integer>();
        while (i < T.length() - 1) {
            TPoint p1 = T.getPoint(i);
            TPoint p2 = T.getPoint(i + 1);
            long timeDiff = (p2.getTime() - p1.getTime()) / 1000;
            double distance = Utils.euclidean(p1, p2);
            double speed = distance / (double) timeDiff;
            if (speed > maxSpeed) {
            	gids.add(T.getPoint(i).getGid());
                T.getPoints().remove(i);
            } else {
                i++;
            }

        }
        persistencia.deleteByGids(gids,configTraj.getTableNameOrigin());
        return T;
    }
	
	private void removeFromSecond(Trajectory traj, double speed) throws DBConnectionException, SQLException {
		Trajectory t = traj;
		while(hasExtremeNoiseTraj(t, speed))
			t = removeSecondExtremeNoise(traj, speed);
	}
	
	private Trajectory removeSecondExtremeNoise(Trajectory T, double maxSpeed) throws DBConnectionException, SQLException {
        int i = 0;
        List<Integer> gids = new ArrayList<Integer>();
        while (i < T.length() - 1) {
            TPoint p1 = T.getPoint(i);
            TPoint p2 = T.getPoint(i + 1);
            long timeDiff = (p2.getTime() - p1.getTime()) / 1000;
            double distance = Utils.euclidean(p1, p2);
            double speed = distance / (double) timeDiff;
            if (speed > maxSpeed) {
                gids.add(T.getPoint(i+1).getGid());
                T.getPoints().remove(i+1);
            } else {
                i++;
            }
        }
        persistencia.deleteByGids(gids,configTraj.getTableNameOrigin());
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

}