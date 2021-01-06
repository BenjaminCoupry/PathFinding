import java.util.Hashtable;
import java.util.Map;

public class InfoChemin {
    Map<Place,Double> distances;
    Map<Place,Place> predecesseurs;
    Map<Place,Arc> predecesseursArc;

    public InfoChemin(Map<Place, Double> distances, Map<Place, Place> predecesseurs, Map<Place, Arc> predecesseursArc) {
        this.distances = distances;
        this.predecesseurs = predecesseurs;
        this.predecesseursArc = predecesseursArc;
    }


    public Map<Place, Double> getDistances() {
        return distances;
    }

    public void setDistances(Map<Place, Double> distances) {
        this.distances = distances;
    }

    public Map<Place, Place> getPredecesseurs() {
        return predecesseurs;
    }

    public void setPredecesseurs(Map<Place, Place> predecesseurs) {
        this.predecesseurs = predecesseurs;
    }

    public Map<Place, Arc> getPredecesseursArc() {
        return predecesseursArc;
    }

    public void setPredecesseursArc(Map<Place, Arc> predecesseursArc) {
        this.predecesseursArc = predecesseursArc;
    }
}
