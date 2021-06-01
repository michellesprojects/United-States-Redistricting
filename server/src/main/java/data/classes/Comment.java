
package data.classes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import regions.Precinct;
import regions.State;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "COMMENT")
public class Comment {
    private long id;
    private Date date;
    private String text;
    private State state;
    private Precinct precinct;

    public Comment(){}
    public Comment(long id, Date date, String text) {
        this.id = id;
        this.date = date;
        this.text = text;
        this.state = null;
        this.precinct = null;
    }
    public Comment(Date date, String text) {
        this.date = date;
        this.text = text;
        this.state = null;
        this.precinct = null;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "TEXT")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TIMESTAMP")
    public Date getDate() {
        return date;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="STATE_NAME")
    @JsonBackReference
    public State getState() {
        return state;
    }
    public void setState(State state) {
        this.state = state;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="PRECINCT_GEOID")
    @JsonBackReference
    public Precinct getPrecinct() {
        return precinct;
    }

    public void setPrecinct(Precinct precinct) {
        this.precinct = precinct;
    }

    public void setDate(Date date) {
        this.date = date;
    }


}
