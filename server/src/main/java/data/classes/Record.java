package data.classes;
import com.fasterxml.jackson.annotation.JsonBackReference;
import regions.Precinct;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="RECORD")
public class Record {
    private long id;
    private Date timestamp;
    private Precinct precinct;
    private RecordType type;
    private String comment;
    private String oldJson, newJson, oldNeighbors, newNeighbors, oldData, newData;

    public Record() { }

    public Record(RecordType type, Date timestamp, Precinct precinct, String comment){
        this.type = type;
        this.timestamp = timestamp;
        this.precinct = precinct;
        this.comment = comment;
    }

    @Lob
    @Column(name = "OLD_JSON")
    public String getOldJson() { return oldJson; }
    public void setOldJson(String oldJson) { this.oldJson = oldJson; }

    @Lob
    @Column(name = "NEW_JSON")
    public String getNewJson() { return newJson; }
    public void setNewJson(String newJson) { this.newJson = newJson; }

    @Lob
    @Column(name = "OLD_NEIGHBORS")
    public String getOldNeighbors() { return oldNeighbors; }
    public void setOldNeighbors(String oldNeighbors) { this.oldNeighbors = oldNeighbors; }

    @Lob
    @Column(name = "NEW_NEIGHBORS")
    public String getNewNeighbors() { return newNeighbors; }
    public void setNewNeighbors(String newNeighbors) { this.newNeighbors = newNeighbors; }

    @Lob
    @Column(name = "OLD_DATA")
    public String getOldData() { return oldData; }
    public void setOldData(String oldData) { this.oldData = oldData; }

    @Lob
    @Column(name = "NEW_DATA")
    public String getNewData() { return newData; }
    public void setNewData(String newData) { this.newData = newData; }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIMESTAMP")
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    @ManyToOne
    @JoinColumn(name = "PRECINCT_GEOID")
    @JsonBackReference
    public Precinct getPrecinct() { return precinct; }
    public void setPrecinct(Precinct precinct) { this.precinct = precinct; }

    @Column(name = "TYPE")
    public RecordType getType() { return type; }
    public void setType(RecordType type) { this.type = type; }

    @Column(name = "COMMENT")
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}