package adapters;

import java.util.*;

public class StateFeature {
    private List<PrecinctFeature> features;
    private String name;
    private String type = "FeatureCollection";
    private Map<String, Object> properties;

    public StateFeature() {
        this.features = new ArrayList<>();
        this.properties = new HashMap<>();
    }

    public StateFeature(String name) {
        this.name = name;
    }

    public List<PrecinctFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<PrecinctFeature> features) {
        this.features = features;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getProperties() { return properties; }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public void addProperty(String key, Object value) { this.properties.put(key, value);}

    public void addFeature(PrecinctFeature pw) { this.features.add(pw);}

    public void loadStateFeature(){
    }
    public void changePrecinctFeature(String geoid){

    }
}
