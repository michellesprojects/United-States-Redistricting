
package data.classes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import regions.Precinct;
import regions.State;

import javax.persistence.*;

@Entity
@Table(name = "DATA_ERROR")
public class DataError {
    private long ErrorId;
    private Precinct precinct;
    private State state;
    private ErrorType errorType;
    private float centerX, centerY;

    public DataError(){}

    public DataError(long errorId, Precinct precinct, State state, ErrorType errorType, float centerX, float centerY) {
        ErrorId = errorId;
        this.precinct = precinct;
        this.state = state;
        this.errorType = errorType;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    @ManyToOne
    @JoinColumn(name = "STATE")
    @JsonBackReference
    public State getState() {
        return state;
    }
    public void setState(State state) {
        this.state = state;
    }

    @Id
    @GeneratedValue	(strategy = GenerationType.IDENTITY)
    @Column(name = "ERROR_ID")
    public long getErrorId() {
        return ErrorId;
    }
    public void setErrorId(long errorId) {
        ErrorId = errorId;
    }

    @Column(name = "CENTER_X")
    public float getCenterX() {
        return centerX;
    }
    public void setCenterX(float centerX) { this.centerX = centerX; }

    @Column(name = "CENTER_Y")
    public float getCenterY() {
        return centerY;
    }
    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PRECINCT")
    @JsonManagedReference
    public Precinct getPrecinct() {
        return precinct;
    }
    public void setPrecinct(Precinct precinct) {
        this.precinct = precinct;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "ERROR_TYPE")
    public ErrorType getErrorType() {
        return errorType;
    }
    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }
}
