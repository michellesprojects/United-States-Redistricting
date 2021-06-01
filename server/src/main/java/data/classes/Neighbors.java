package data.classes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import regions.Precinct;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "NEIGHBORS")
public class Neighbors {
    private String neighborGeoid;
    private Precinct precinct;
    private long id;

    public Neighbors(){}
    public Neighbors(String neighborGeoid, Precinct precinct){
        this.neighborGeoid = neighborGeoid;
        this.precinct = precinct;
    }
    public Neighbors(String neighborGeoid, Precinct precinct, long id) {
        this.neighborGeoid = neighborGeoid;
        this.precinct = precinct;
        this.id = id;
    }

    @Column(name = "NEIGHBOR_GEOID")
    public String getNeighborGeoid() { return neighborGeoid; }
    public void setNeighborGeoid(String neighborGeoid) { this.neighborGeoid = neighborGeoid; }

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "PRECINCT")
    public Precinct getPrecinct() { return precinct; }
    public void setPrecinct(Precinct precinct) { this.precinct = precinct; }

    @Id
    @GeneratedValue	(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
}