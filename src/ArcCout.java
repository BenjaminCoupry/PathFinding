public class ArcCout implements Arc{
    Place start;
    Place finish;
    double cout;

    public ArcCout(Place start, Place finish, double cout) {
        this.start = start;
        this.finish = finish;
        this.cout = cout;
    }

    public Place getStart() {
        return start;
    }

    public void setStart(Place start) {
        this.start = start;
    }

    public Place getFinish() {
        return finish;
    }

    public void setFinish(Place finish) {
        this.finish = finish;
    }

    public double getCout() {
        return cout;
    }

    public void setCout(double cout) {
        this.cout = cout;
    }
}
