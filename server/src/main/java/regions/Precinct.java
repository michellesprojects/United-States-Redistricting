
package regions;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import controllers.Main;
import data.classes.*;
import javax.persistence.*;
import java.util.*;

@Entity
@Table (name = "PRECINCT")
public class Precinct {
    private String geoId;
    private String precinctName;
    private String precinctJSON;
    private String neighbors;
    private String stateName;
    private float centerX, centerY;
    private DemographicData demographicData;
    private List<ElectionData> electionDataCollection;
    private List<Comment> comments;
    private List<Record> records;
    private List<DataError> errors;
    private List<Neighbors> neighboringPrecincts;
    private PrecinctType pType;

    public Precinct(String geoId) {
        this.geoId = geoId;
    }

    public Precinct(){}

    public Precinct(String geoId, String precinctName, String precinctJSON, String stateName,
                    float centerX, float centerY, DemographicData demographicData,
                    List<ElectionData> electionDataCollection, List<Comment> comments, List<Record> records,
                    List<DataError> errors, List<Neighbors> neighboringPrecincts) {
        this.geoId = geoId;
        this.precinctName = precinctName;
        this.precinctJSON = precinctJSON;
        this.stateName = stateName;
        this.centerX = centerX;
        this.centerY = centerY;
        this.demographicData = demographicData;
        this.electionDataCollection = electionDataCollection;
        this.comments = comments;
        this.records = records;
        this.errors = errors;
        this.neighboringPrecincts = neighboringPrecincts;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "PRECINCT_TYPE")
    public PrecinctType getPType() { return pType; }
    public void setPType(PrecinctType pType) { this.pType = pType; }

    @OneToMany(targetEntity = Neighbors.class, mappedBy = "precinct", fetch = FetchType.LAZY)
    public List<Neighbors> getPrecinctNeighbors() { return neighboringPrecincts; }
    public void setPrecinctNeighbors(List<Neighbors> neighboringPrecincts) {
        this.neighboringPrecincts = neighboringPrecincts;
    }

    @Column(name = "CENTER_X")
    public float getCenterX() {
        return centerX;
    }
    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    @Column(name = "CENTER_Y")
    public float getCenterY() {
        return centerY;
    }
    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    @OneToOne(targetEntity = DemographicData.class, mappedBy = "precinct", orphanRemoval = true)
    @JsonManagedReference
    public DemographicData getDemographicData() {
        return demographicData;
    }
    public void setDemographicData(DemographicData demographicData) {
        this.demographicData = demographicData;
    }

    @OneToMany(targetEntity = ElectionData.class, mappedBy = "precinct", orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    public List<ElectionData> getElectionDataCollection() {
        return electionDataCollection;
    }
    public void setElectionDataCollection(List<ElectionData> electionDataCollection) {
        this.electionDataCollection = electionDataCollection;
    }

    @OneToMany(targetEntity = Comment.class, mappedBy = "precinct")
    @JsonManagedReference
    public List<Comment> getComments() {
        return comments;
    }
    public void setComments(List<Comment> comments) {
        this.comments = new LinkedList<>(comments);
    }

    @OneToMany(targetEntity = Record.class, mappedBy = "precinct", orphanRemoval = true)
    @JsonManagedReference
    public List<Record> getRecords() {
        return records;
    }
    public void setRecords(List<Record> records) {
        this.records = records;
    }

    @OneToMany(targetEntity = DataError.class, mappedBy = "precinct")
    @JsonBackReference
    public List<DataError> getErrors() {
        return errors;
    }
    public void setErrors(List<DataError> errors) {
        this.errors = errors;
    }

    @Id
    @Column(name = "GEO_ID")
    public String getGeoId() {
        return geoId;
    }
    public void setGeoId(String geoId) {
        this.geoId = geoId;
    }

    @Column(name = "NAME")
    public String getPrecinctName() {
        return precinctName;
    }
    public void setPrecinctName(String precinctName) {
        this.precinctName = precinctName;
    }

    @Lob
    @Column(name = "PRECINCT_GEOJSON")
    @JsonBackReference
    public String getPrecinctJSON() {
        return precinctJSON;
    }
    public void setPrecinctJSON(String precinctJSON) {
        this.precinctJSON = precinctJSON;
    }

    @Column(name = "STATE_NAME")
    public String getStateName() {
        return stateName;
    }
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public Neighbors removeNeighbor(String neighbor, Precinct slave){
        int i = 0;
        Neighbors n = null;
        boolean found = false;
        for (; i < neighboringPrecincts.size(); i++) {
            n = neighboringPrecincts.get(i);
            if (n.getNeighborGeoid().trim().equals(neighbor)) {
                found = true;
                break;
            }
        }
        if (!found)
            return null;
        this.neighboringPrecincts.remove(i);
        Main.em.remove(n);
        slave.removeNeighbor(this.geoId, this);
        return n;
    }

    public Boolean addNeighbor(String neighbor, Precinct slave){
        boolean contains = false;
        for (Neighbors n:neighboringPrecincts) {
            if (!neighbor.equals(this.geoId) && n.getNeighborGeoid().trim().equals(neighbor)){
                contains = true;
                break;
            }
        }
        if (!contains){
            this.neighboringPrecincts.add(new Neighbors(neighbor, this));
            slave.getPrecinctNeighbors().add(new Neighbors(this.getGeoId(), slave));
            return true;
        }else
            return null;
    }

    public Comment addComment(String comment){
        Comment c = new Comment(new Date(), comment);
        c.setPrecinct(this);
        ((LinkedList)this.comments).addFirst(c);
        return c;
    }

    public void combineDemographicData(Precinct slave){
        this.getDemographicData().setAsian(this.getDemographicData().getAsian()+slave.getDemographicData().getAsian());
        this.getDemographicData().setBlack(this.getDemographicData().getBlack()+slave.getDemographicData().getBlack());
        this.getDemographicData().setHawaiian(this.getDemographicData().getHawaiian()+slave.getDemographicData().getHawaiian());
        this.getDemographicData().setHispanicLatino(this.getDemographicData().getHispanicLatino()+slave.getDemographicData().getHispanicLatino());
        this.getDemographicData().setIndianAlaskan(this.getDemographicData().getIndianAlaskan()+slave.getDemographicData().getIndianAlaskan());
        this.getDemographicData().setWhite(this.getDemographicData().getWhite()+slave.getDemographicData().getWhite());
        this.getDemographicData().setPopulation(this.getDemographicData().getPopulation()+slave.getDemographicData().getPopulation());
    }
    public void combineElectionData(Precinct slave){
        for(ElectionData ed: this.getElectionDataCollection()){
            ElectionType type = ed.getType();
            int year = ed.getYear();
            ElectionData slaveElection = slave.findElection(type,year);
            if(slaveElection != null) {
                ed.setDemocraticVotes(ed.getDemocraticVotes() + slaveElection.getDemocraticVotes());
                ed.setRepublicanVotes(ed.getRepublicanVotes() + slaveElection.getRepublicanVotes());
            }
        }
    }

    public ElectionData findElection(ElectionType type, int year) {
        for (ElectionData e : this.getElectionDataCollection()) {
            if (e.getType().equals(type) && e.getYear() == year) {
                return e;
            }
        }
        return null;
    }

    public Neighbors findNeighbor(String neighborGeoId) {
        for (Neighbors n : neighboringPrecincts) {
            if (n.getNeighborGeoid().equals(neighborGeoId)) {
                return n;
            }
        }
        return null;
    }

    public String demographicAndElectionToString(){
        StringBuilder elist = new StringBuilder();
        for(int i=0; i<electionDataCollection.size(); i++){
            elist.append(electionDataCollection.get(0).toString());
        }
        elist.append(this.demographicData.toString());
        return elist.toString();
    }

    public StringBuilder commentsString(){
        StringBuilder clist = new StringBuilder();
        for(int i=0; i<comments.size(); i++){
            if(i != comments.size())
                clist.append(comments.get(i).toString()+", ");
            else
                clist.append(comments.get(i).toString());
        }
        return clist;
    }

    public String neighborsToString(){
        StringBuilder sb = new StringBuilder();
        for(Neighbors n: this.neighboringPrecincts){
            sb.append(n.getNeighborGeoid());
            sb.append(',');
        }
        if (sb.length() ==0)
            return "";
        return sb.substring(0, sb.length() - 1);
    }

    // changes precinct type, set all election and demographic data to zeros
    public void defineGhost(){
        this.getDemographicData().setPopulation(0);
        this.getDemographicData().setWhite(0);
        this.getDemographicData().setIndianAlaskan(0);
        this.getDemographicData().setHispanicLatino(0);
        this.getDemographicData().setHawaiian(0);
        this.getDemographicData().setBlack(0);
        this.getDemographicData().setAsian(0);
        this.setPType(PrecinctType.GHOST);
        for( ElectionData e: this.getElectionDataCollection()){
            e.setRepublicanVotes(0);
            e.setDemocraticVotes(0);
        }
    }
}