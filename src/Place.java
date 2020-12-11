import java.util.Objects;

public class Place {
    String nom;
    double x;
    double y;

    public Place(String nom, double x, double y) {
        this.nom = nom;
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return Objects.equals(nom, place.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getNom() {
        return nom;
    }
}
