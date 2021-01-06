import java.awt.geom.Line2D;
import java.util.List;

public class ArcPhysique implements Arc{
    Place start;
    Place finish;
    double vitesse;

    public ArcPhysique(Place start, Place finish, double vitesse) {
        this.start = start;
        this.finish = finish;
        this.vitesse = vitesse;
    }

    @Override
    public Place getStart() {
        return start;
    }

    @Override
    public void setStart(Place start) {
    this.start = start;
    }

    @Override
    public Place getFinish() {
        return finish;
    }

    @Override
    public void setFinish(Place finish) {
    this.finish = finish;
    }

    @Override
    public double getCout() {
        double dist = Math.sqrt(Math.pow(start.getX() - finish.getX(), 2) + Math.pow(start.getY() - finish.getY(), 2));
        return dist/getVitesse();
    }

    public double getVitesse() {
        return vitesse;
    }

    public void setVitesse(double vitesse) {
        this.vitesse = vitesse;
    }

    public boolean croise(ArcPhysique arc)
    {
boolean egal = getStart().equals(arc.getStart()) && getFinish().equals(arc.getFinish());
        if(egal)
        {
            return true;
        }
        if((getStart().equals(arc.getStart())||getStart().equals(arc.getFinish()) ||
                getFinish().equals(arc.getStart())||getFinish().equals(arc.getFinish())))
        {
            //Chainage different d'intersection
            return false;
        }
        return Line2D.linesIntersect(getStart().getX(),getStart().getY(),getFinish().getX(),getFinish().getY(),
                arc.getStart().getX(),arc.getStart().getY(),arc.getFinish().getX(),arc.getFinish().getY());
    }
    public boolean croise(List<ArcPhysique> arcs)
    {
        for(ArcPhysique a : arcs)
        {
            if(croise(a))
            {
                return true;
            }
        }
        return false;
    }



}
