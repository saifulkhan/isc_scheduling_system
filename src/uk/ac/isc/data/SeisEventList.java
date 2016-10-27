package uk.ac.isc.data;

import java.util.ArrayList;
import java.util.Observable;

 
public class SeisEventList extends Observable {

    private final ArrayList<SeisEvent> seisEventList;

    public SeisEventList() {
        seisEventList = new ArrayList<>();
    }

    /*public void setSeisEventList(ArrayList<SeisEvent> se) {
        this.seisEventList = se;
        setChanged();
    }*/

    public ArrayList<SeisEvent> getSeisEventList() {
        return this.seisEventList;
    }

    public void setChangeFlag() {
        setChanged();
    }

}
