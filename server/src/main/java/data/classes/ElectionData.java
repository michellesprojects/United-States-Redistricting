package data.classes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import regions.Precinct;
import regions.State;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="ELECTION_DATA")
public class ElectionData implements Serializable {
    private Precinct precinct;
    private State state;
    private long id;
    private int year;
    private ElectionType type;
    private int republicanVotes, democraticVotes;

    public ElectionData(){}

    public ElectionData(Precinct precinct, State state, long id, int year, ElectionType type, int republicanVotes, int democraticVotes) {
        this.precinct = precinct;
        this.state = state;
        this.id = id;
        this.year = year;
        this.type = type;
        this.republicanVotes = republicanVotes;
        this.democraticVotes = democraticVotes;
    }

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "STATE")
    public State getState() {
        return state;
    }
    public void setState(State state) {
        this.state = state;
    }

    @Id
    @GeneratedValue	(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "PRECINCT")
    @JsonBackReference
    public Precinct getPrecinct() {
        return precinct;
    }
    public void setPrecinct(Precinct precinct) {
        this.precinct = precinct;
    }

    @Column(name = "YEAR")
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "ELECTION_TYPE")
    public ElectionType getType() {
        return type;
    }
    public void setType(ElectionType type) {
        this.type = type;
    }

    @Column(name = "REPUBLICAN_VOTES")
    public int getRepublicanVotes() {
        return republicanVotes;
    }
    public void setRepublicanVotes(int republicanVotes) {
        this.republicanVotes = republicanVotes;
    }

    @Column(name = "DEMOCRATIC_VOTES")
    public int getDemocraticVotes() {
        return democraticVotes;
    }
    public void setDemocraticVotes(int democraticVotes) {
        this.democraticVotes = democraticVotes;
    }

    @Override
    public String toString() {
        return "ElectionData{" +
                "precinct=" + precinct +
                ", state=" + state +
                ", id=" + id +
                ", year=" + year +
                ", type=" + type +
                ", republicanVotes=" + republicanVotes +
                ", democraticVotes=" + democraticVotes +
                '}';
    }
}