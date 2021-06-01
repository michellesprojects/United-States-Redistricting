package controllers;

import java.io.*;
import java.util.List;
import data.classes.Comment;
import adapters.StateFeature;
import data.classes.ErrorType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;
import regions.Precinct;

@RestController
public class Controller {
    @CrossOrigin
    @GetMapping("/states")
    public Object getStates() {
        Object obj = null;
        try {
            obj = new JSONParser().parse(new FileReader("/src/main/java/shapes/state_shapes.json"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @CrossOrigin
    @GetMapping("/state")
    public StateFeature getState(@RequestParam(value = "name") String  name) {
        //State s = Model.selectState(name.toUpperCase());
        //return s;
        StateFeature s = Model.stateToStateFeature.get(name);
        return s;
    }

    @CrossOrigin
    @GetMapping("/precinct")
    public Precinct getPrecinct(@RequestParam(value = "geoid") String geoid) {
        return Model.selectPrecinct(geoid);
    }

    @CrossOrigin
    @GetMapping("/errors")
    public Object getErrors(@RequestParam(value = "state") String state, @RequestParam(value = "type") String errorType){
        System.out.println(state+errorType);
        Object x = Model.selectErrorsByType(state, ErrorType.valueOf(errorType.toUpperCase()));
        return x;
    }


    @CrossOrigin
    @PostMapping(path = "/precinct/merge", consumes= "application/json")
    public Precinct mergePrecincts(@RequestBody JSONObject obj) {
        Precinct response = Model.mergePrecincts((String)obj.get("master"), (String) obj.get("slave"),
                (String) obj.get("new_geometry"), (String) obj.get("comment"));
        return response;
    }

    @CrossOrigin
    @PostMapping(path = "/precinct/add/neighbor", consumes= "application/json")
    public String addNeighbor(@RequestBody JSONObject obj) {
        return Model.addNeighbor((String)obj.get("master"), (String) obj.get("slave"), (String) obj.get("comment"));
    }

    @CrossOrigin
    @PostMapping(path = "/precinct/remove/neighbor", consumes= "application/json")
    public String removeNeighbor(@RequestBody JSONObject obj) {
        String s =  Model.removeNeighbor((String)obj.get("master"), (String) obj.get("slave"), (String) obj.get("comment"));
        return s;
    }

    @CrossOrigin
    @PostMapping(path = "/precinct/defineGhost", consumes= "application/json")
    public Precinct defineGhost(@RequestBody JSONObject obj) {
        Precinct response = Model.defineGhost((String)obj.get("master"), (String) obj.get("comment"));
        return response;
    }

    @CrossOrigin
    @PostMapping(path = "/precinct/add/comment")
    public List<Comment> addCommentToPrecinct(@RequestBody JSONObject obj){
        String geoid = (String)obj.get("precinct");
        String comment = (String)obj.get("comment");
        return Model.addComment(geoid, null, comment);
    }

    @CrossOrigin
    @PostMapping(path = "/state/add/comment")
    public List<Comment> addCommentState(@RequestBody JSONObject obj){
        String state = (String)obj.get("state");
        String comment = (String)obj.get("comment");
        List<Comment> s =   Model.addComment(null,state, comment);
        return s;
    }

    @CrossOrigin
    @PostMapping(path = "/precinct/update/shape")
    public Boolean updateShapePrecinct(@RequestBody JSONObject obj){
        String geoid = (String)obj.get("geoid");
        String newShape = (String)obj.get("newShape");
        return Model.updateShapePrecinct(geoid,newShape);
    }

    @CrossOrigin
    @GetMapping("/natioal_parks")
    public Object getNationalParks(@RequestParam(value = "state") String  state) {
        String path = "src/main/java/shapes/" + state.toLowerCase() + "_national_parks.json";
        Object obj = null;
        try {
            obj = new JSONParser().parse(new FileReader(path));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @CrossOrigin
    @GetMapping("/errors/delete")
    public Boolean deleteError(@RequestParam(value = "id") String  id) {
        return Model.deleteError(Long.parseLong(id));
    }

    @CrossOrigin
    @PostMapping(path = "/precinct/update/shape", consumes= "application/json")
    public Boolean updateShape(@RequestBody JSONObject obj) {
        return Model.updateShapePrecinct((String)obj.get("geoid"), (String) obj.get("shape"));
    }



}

