package controllers;

import com.google.gson.Gson;
import adapters.Geometry;
import adapters.PrecinctFeature;
import adapters.StateFeature;
import data.classes.DataError;
import data.classes.ErrorType;
import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import regions.State;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;

@SpringBootApplication
public class Main {
    public static Query q;
    public static EntityManagerFactory emf;
    public static EntityManager em;
    public static void main(String[] args) throws ParseException {
        emf = Persistence.createEntityManagerFactory("EM");
        em = emf.createEntityManager();
        em.getTransaction().begin();

        loadAllStates();
        List<DataError> x = Model.selectErrorsByType("KY", ErrorType.MULTIPOLYGON);
        System.out.println(x);
        em.getTransaction().commit();
        SpringApplication.run(Main.class, args);

    }

    public static void loadAllStates(){
        List<State> states = Model.selectAllStates();
        for(State s: states){
            //if (!s.getName().equals("NY"))
            loadState(s);
        }
    }

    public static void loadState(State state){
        q = em.createQuery("SELECT geoId,precinctJSON from Precinct WHERE stateName LIKE: sn")
                .setParameter("sn", "%"+state.getName()+"%");
        List<Object> resultList= q.getResultList();
        Gson g = new Gson();
        StateFeature sw = new StateFeature();
        sw.setName(state.getName());
        state.getDataErrors();
        //sw.addProperty("demographicData", state.getDemographicData());
        //sw.addProperty("electionData", state.getElectionData());
        //sw.addProperty("dataErrors", state.getDataErrors());
        sw.addProperty("comments", state.getComments());
        sw.addProperty("centerY", state.getCenterX());
        sw.addProperty("centerX", state.getCenterY());
        for (Object o: resultList){
            Object[] obrArr = (Object[]) o;
            String geoId = (String) obrArr[0];
            String temp2 = (String) obrArr[1];
            Geometry geo;
            try {
                geo = g.fromJson(temp2, Geometry.class);
            }
            catch ( com.google.gson.JsonSyntaxException e){
                continue;
            }
            PrecinctFeature pw = new PrecinctFeature();
            pw.setGeometry(geo);
            pw.addProperty("geoid", geoId);
            sw.addFeature(pw);
        }
        Model.stateToStateFeature.put(state.getName(), sw);
    }
}
