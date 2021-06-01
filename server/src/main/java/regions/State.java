
package regions;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import data.classes.Comment;
import data.classes.DataError;
import data.classes.DemographicData;
import data.classes.ElectionData;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.Lob;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
@Entity
@Table(name = "STATE")
public class State {
    private String name;
    private String geoJSON;
    private float centerX, centerY;
    private List<Comment> comments;
    private DemographicData demographicData;
    private List<ElectionData> electionData;
    private List<DataError> dataErrors;

    public State(String name, String geoJSON, float centerX, float centerY, List<Comment> comments, DemographicData demographicData, List<ElectionData> electionData, List<DataError> dataErrors) {
        this.name = name;
        this.geoJSON = geoJSON;
        this.centerX = centerX;
        this.centerY = centerY;
        this.comments = comments;
        this.demographicData = demographicData;
        this.electionData = electionData;
        this.dataErrors = dataErrors;
    }
    public State(){}

    @OneToMany(targetEntity = DataError.class, mappedBy = "state", cascade = CascadeType.ALL)
    @JsonBackReference
    public List<DataError> getDataErrors() { return dataErrors; }
    public void setDataErrors(List<DataError> dataErrors) { this.dataErrors = dataErrors; }

    @OneToMany(targetEntity = ElectionData.class, mappedBy = "state",cascade = CascadeType.ALL)
    @JsonManagedReference
    public List<ElectionData> getElectionData() { return electionData; }
    public void setElectionData(List<ElectionData> electionData) { this.electionData = electionData; }

    @OneToOne(targetEntity = DemographicData.class, mappedBy = "state", orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    public DemographicData getDemographicData() { return demographicData; }
    public void setDemographicData(DemographicData demographicData) { this.demographicData = demographicData; }

    @Id
    @Column(name = "NAME")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Column(name = "CENTER_X")
    public float getCenterX() { return centerX; }
    public void setCenterX(float centerX) { this.centerX = centerX; }

    @Column(name = "CENTER_Y")
    public float getCenterY() { return centerY; }
    public void setCenterY(float centerY) { this.centerY = centerY; }

    @Lob
    @Column(name = "STATE_GEOJSON")
    public String getGeoJSON() { return geoJSON; }
    public void setGeoJSON(String geoJSON) { this.geoJSON = geoJSON; }

    @OneToMany(targetEntity = Comment.class, mappedBy = "state")
    @JsonManagedReference
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = new LinkedList<>(comments); }

    public Comment addComment(String comment) {
        Comment c = new Comment(new Date(), comment);
        c.setState(this);
        ((LinkedList)this.comments).addFirst(c);
        return c;
    }
}