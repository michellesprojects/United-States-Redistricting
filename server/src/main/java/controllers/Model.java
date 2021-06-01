package controllers;

import adapters.Geometry;
import adapters.PrecinctFeature;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import data.classes.*;
import adapters.StateFeature;
import regions.Precinct;
import regions.State;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.persistence.Query;

public class Model {
    public static HashMap<String, StateFeature> stateToStateFeature = new HashMap<String, StateFeature>();
    @PersistenceContext
    //public static EntityManager em;
    public static Query q;

    public static List<State> selectAllStates(){
        List<State> s =  (List<State>) Main.em.createQuery("SELECT s FROM State s").getResultList();
        return s;
    }

    public static State selectState(String stateName){
        State s = Main.em.find(State.class, stateName);
        return s;
    }

    public static Precinct selectPrecinct(String geoId){
        return Main.em.find(Precinct.class, geoId);
    }

    public static List<String> getNeighborGeoIDs(String precinctGeoid){
        q = Main.em.createQuery("SELECT s.neighborGeoid FROM Neighbors s, Precinct p WHERE p.geoId LIKE: pg AND p = s.precinct")
                .setParameter("pg", "%"+precinctGeoid+"%");
        List<String> res = q.getResultList();
        return res;
    }

    public static String getPrecinctNeighbors(String geoId){
        return Main.em.createQuery("SELECT neighbors FROM Precinct WHERE geoId LIKE: gi")
                .setParameter("gi","%"+geoId+"%").setMaxResults(1).getSingleResult().toString();
    }

    public static boolean deletePrecinct(Precinct p){
        try{
            Main.em.remove(p);
            Main.em.flush();
            return  true;
        }catch (Exception e) {
            return false;
        }
    }

    public static Precinct mergePrecincts(String masterGeoid, String slaveGeoid, String newPrecinctJSON, String comment){
        try{
            Main.em.getTransaction().begin();
            Precinct master = selectPrecinct(masterGeoid);
            Precinct slave = selectPrecinct(slaveGeoid);
            if (master == null || slave == null)
                return null;
            // since merging involves changes in diff type of data, diff record are created for each change
            Record r1 = new Record(RecordType.NEIGHBOR, new Date(), master, comment);
            Record r2 = new Record(RecordType.DATA, new Date(), master, comment);
            Record r3 = new Record(RecordType.GEOGRAPHIC, new Date(), master, comment);
            r1.setOldNeighbors(master.neighborsToString());
            r2.setOldData(master.demographicAndElectionToString());
            r3.setOldJson(master.getPrecinctJSON());
            master.setPrecinctJSON(newPrecinctJSON);
            List<Neighbors> neighborsList = slave.getPrecinctNeighbors();

            Neighbors[] arr = new Neighbors[neighborsList.size()];
            arr = neighborsList.toArray(arr);
            for(int i =0; i<arr.length; i++){
                Neighbors n = arr[i];
                Precinct p = selectPrecinct(n.getNeighborGeoid());
                master.addNeighbor(n.getNeighborGeoid(), p);
                p.removeNeighbor(slaveGeoid, slave);
            }

            master.combineDemographicData(slave);
            master.combineElectionData(slave);
            //remove neighbors from DB
            for(Neighbors n: neighborsList){ n.setPrecinct(null);}
            deletePrecinct(slave);
            r1.setNewNeighbors(master.neighborsToString());
            r2.setNewData(master.demographicAndElectionToString());
            r3.setNewJson(master.getPrecinctJSON());
            Main.em.persist(r1);
            Main.em.persist(r2);
            Main.em.persist(r3);
            //Main.em.flush();
            Main.em.getTransaction().commit();
            loadStateFeature(master.getStateName());
            return master;
        } catch (NullPointerException e){
            return null;
        }
    }

    public static String addNeighbor(String masterGeoid, String slaveGeoid, String comment){
       try {
           Precinct master = selectPrecinct(masterGeoid);
           Precinct slave = selectPrecinct(slaveGeoid);
           Main.em.getTransaction().begin();
           Record record = new Record(RecordType.NEIGHBOR, new Date(), master, comment);
           record.setOldNeighbors(master.neighborsToString());
           if (master.addNeighbor(slaveGeoid, slave) == null)
               return null;
           record.setNewNeighbors(master.neighborsToString());
           Main.em.persist(record);
           Main.em.getTransaction().commit();
           return master.neighborsToString();
       }catch (NullPointerException e){
           return  null;
       }

    }

    public static String removeNeighbor(String masterGeoid, String slaveGeoid, String comment){
        try{
            Precinct master = selectPrecinct(masterGeoid);
            Precinct slave = selectPrecinct(slaveGeoid);
            Main.em.getTransaction().begin();
            Record record = new Record(RecordType.NEIGHBOR, new Date(), master, comment);
            record.setOldNeighbors(master.neighborsToString());
            Neighbors n = master.removeNeighbor(slaveGeoid, slave);
            if (n == null)
                return null;
            record.setNewNeighbors(master.neighborsToString());
            Main.em.persist(record);
            Main.em.getTransaction().commit();
            return master.neighborsToString();
        }catch (NullPointerException e){
            return null;
        }
    }

    public static List<DataError> selectErrorsByType(String state, ErrorType type){
        q = Main.em.createQuery("SELECT e FROM DataError e, State s WHERE s.name = '"+state+"' AND e.errorType LIKE: et AND e.state = s")
                .setParameter("et", type);
        return q.getResultList();
    }

    public static boolean deleteError(long errorId){
        Main.em.getTransaction().begin();
        DataError error = Main.em.find(DataError.class, errorId);
        error.setPrecinct(null);
        try {
            Main.em.remove(error);
        } catch(EntityExistsException e){
            return false;
        } catch(IllegalArgumentException e){ //if the instance is not an entity
            return false;
        } catch(NoResultException e){
            return false;
        }
        Main.em.getTransaction().commit();
        return true;
    }

    public static List<Comment> addComment(String geoid, String state, String comment){
        Main.em.getTransaction().begin();
        if (geoid != null){
            Precinct master = selectPrecinct(geoid);
            Comment c = master.addComment(comment);
            Main.em.persist(c);
            Main.em.getTransaction().commit();
            return master.getComments();
        }
        if (state != null){
            State master = selectState(state.toUpperCase());
            Comment c = master.addComment(comment);
            Main.em.persist(c);
            Main.em.getTransaction().commit();
            return master.getComments();
        }
        return null;
    }

    public static Boolean updateShapePrecinct(String geoid, String newShape){
        try{
            Main.em.getTransaction().begin();
            Precinct p = (Precinct) Main.em.createQuery("SELECT p FROM Precinct p WHERE p.geoId = '"+geoid+"'").getSingleResult();
            p.setPrecinctJSON(newShape);
            Main.em.getTransaction().commit();
            loadStateFeature(p.getStateName().toUpperCase());
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public static Precinct defineGhost(String geoid, String comment){
        try{
            Precinct p = selectPrecinct(geoid);
            Main.em.getTransaction().begin();
            Record record = new Record(RecordType.DATA, new Date(), p, comment);
            record.setOldData(p.demographicAndElectionToString());
            p.defineGhost();
            record.setNewData(p.demographicAndElectionToString());
            Main.em.persist(record);
            Main.em.getTransaction().commit();
            return p;
        }catch (NullPointerException e){
            return null;
        }
    }

    //updates local copy of precinct shapes for each of state,
    //use only after some shapes are changed(i.e. merging, editing shape, add new)
    public static void loadStateFeature(String stateAbbr){
        final String stateAbb = stateAbbr;
        new Thread(new Runnable() {
            public void run() {
                State state = selectState(stateAbb);
                q = Main.em.createQuery("SELECT geoId,precinctJSON from Precinct WHERE stateName LIKE: sn")
                        .setParameter("sn", "%" + stateAbb + "%");
                List<Object> resultList = q.getResultList();
                Gson g = new Gson();
                StateFeature sw = new StateFeature();
                sw.setName(state.getName());
                //sw.addProperty("demographicData", state.getDemographicData());
                //sw.addProperty("electionData", state.getElectionData());
                //sw.addProperty("dataErrors", state.getDataErrors());
                sw.addProperty("comments", state.getComments());
                sw.addProperty("centerY", state.getCenterX());
                sw.addProperty("centerX", state.getCenterY());
                for (Object o : resultList) {
                    Object[] obrArr = (Object[]) o;
                    String geoId = (String) obrArr[0];
                    String temp2 = (String) obrArr[1];
                    Geometry geo;
                    try {
                        geo = g.fromJson(temp2, Geometry.class);
                    } catch (JsonSyntaxException e) {
                        continue;
                    }
                    PrecinctFeature pw = new PrecinctFeature();
                    pw.setGeometry(geo);
                    pw.addProperty("geoid", geoId);
                    sw.addFeature(pw);
                }
                stateToStateFeature.put(state.getName(), sw);
            }
        }).start();
    }



}