package adapters;

import java.util.HashMap;

public class PrecinctFeature {
    private Geometry geometry;
    private String type = "Feature";
    private HashMap<String, Object> properties;

    public PrecinctFeature() {
        this.properties = new HashMap<>();
    }
    public Geometry getGeometry() {
        return geometry;
    }
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, Object> properties) {
        this.properties = properties;
    }
    public void addProperty(String key, Object value) {
        this.properties.put(key, value);
    }
}
