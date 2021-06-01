

package data.classes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import regions.Precinct;
import regions.State;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;

@Entity
@Table(name="DEMOGRAPHIC_DATA")
public class DemographicData {
    private long id;
    private int population, white, black, asian, hispanicLatino, indianAlaskan, hawaiian;
    private Precinct precinct;
    private State state;

    public DemographicData() {}

    public DemographicData(long id, int population, int white, int black, int asian, int hispanicLatino, int indianAlaskan, int hawaiian, Precinct precinct, State state) {
        this.id = id;
        this.population = population;
        this.white = white;
        this.black = black;
        this.asian = asian;
        this.hispanicLatino = hispanicLatino;
        this.indianAlaskan = indianAlaskan;
        this.hawaiian = hawaiian;
        this.precinct = precinct;
        this.state = state;
    }

    @Id
    @GeneratedValue	(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    public long getId() {return id;}
    public void setId(long id) {this.id = id; }

    @JsonBackReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "STATE_ID")
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }

    @JsonBackReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PRECINCT_GEOID")
    public Precinct getPrecinct() {return precinct;}
    public void setPrecinct(Precinct precinct) { this.precinct = precinct; }

    @Column(name = "TOTAL_POPULATION")
    public int getPopulation() { return population; }
    public void setPopulation(int population) { this.population = population; }

    @Column(name = "WHITE")
    public int getWhite() { return white; }
    public void setWhite(int white) { this.white = white; }

    @Column(name = "BLACK")
    public int getBlack() { return black; }
    public void setBlack(int black) { this.black = black; }

    @Column(name = "ASIAN")
    public int getAsian() { return asian; }
    public void setAsian(int asian) { this.asian = asian; }

    @Column(name = "HISPANIC_LATINO")
    public int getHispanicLatino() { return hispanicLatino; }
    public void setHispanicLatino(int hispanicLatino) { this.hispanicLatino = hispanicLatino; }

    @Column(name = "INDIAN_ALASKAN")
    public int getIndianAlaskan() { return indianAlaskan; }
    public void setIndianAlaskan(int indianAlaskan) { this.indianAlaskan = indianAlaskan; }

    @Column(name = "HAWAIIAN_PACIFIC")
    public int getHawaiian() { return hawaiian; }
    public void setHawaiian(int hawaiian) { this.hawaiian = hawaiian; }

    @Override
    public String toString() {
        return "DemographicData{" +
                "id=" + id +
                ", population=" + population +
                ", white=" + white +
                ", black=" + black +
                ", asian=" + asian +
                ", hispanicLatino=" + hispanicLatino +
                ", indianAlaskan=" + indianAlaskan +
                ", hawaiian=" + hawaiian +
                ", precinct=" + precinct +
                ", state=" + state +
                '}';
    }
}