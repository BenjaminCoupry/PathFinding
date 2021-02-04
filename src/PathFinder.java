import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;


public class PathFinder {

    public static List<Place> cheminPlusCourt(List<? extends Arc> arcs, Place start, Place finish)
    {
        InfoChemin ic = calculerDistances(arcs,start);
        return reconstituerChemin(ic.getPredecesseurs(),start,finish);
    }
    public static List<Place> cheminPlusCourt(InfoChemin ic, Place start, Place finish)
    {
        return reconstituerChemin(ic.getPredecesseurs(),start,finish);
    }

    public static InfoChemin calculerDistances(List<? extends Arc> arcs, Place start)
    {
        List<Place> placesExplorees =new ArrayList<>();
        Map<Place,Double> distances = initDistances(arcs);
        Map<Place,Place> predecesseurs = new Hashtable<>();
        Map<Place,Arc> predecesseursArc = new Hashtable<>();
        distances.put(start,0.0);
        Place pp = start;
        do {
            List<Arc> accessible = adjacent(arcs,pp);
            majDistances(accessible,distances,predecesseurs,predecesseursArc);
            placesExplorees.add(pp);
            pp = getPlaceNonExploreePlusProche(placesExplorees,distances);
        }while(pp != null);
        return new InfoChemin(distances,predecesseurs,predecesseursArc);
    }

    public static List<Place> endroitsAccesibles(InfoChemin infoChemins)
    {
        List<Place> accessibles = new ArrayList<>();
        Map<Place,Double> distances = infoChemins.getDistances();
        for(Place p : distances.keySet())
        {
            if(distances.get(p)<Double.POSITIVE_INFINITY)
            {
                accessibles.add(p);
            }
        }
        return accessibles;
    }
    public static List<Place> reconstituerChemin(Map<Place,Place> predecesseurs,Place start, Place finish)
    {
        List<Place> chemin = new ArrayList<>();
        Place loc = finish;
        chemin.add(finish);
        while(!loc.equals(start))
        {
            if(!predecesseurs.containsKey(loc))
            {
                return null;
            }
            loc = predecesseurs.get(loc);
            chemin.add(loc);
        }
        Collections.reverse(chemin);
        return chemin;

    }
    public static List<Arc> reconstituerCheminArcs(Map<Place,Arc> predecesseurs,Place start, Place finish)
    {
        List<Arc> chemin = new ArrayList<>();
        Place loc = finish;
        while(!loc.equals(start))
        {
            if(!predecesseurs.containsKey(loc))
            {
                return null;
            }
            chemin.add(predecesseurs.get(loc));
            loc = predecesseurs.get(loc).getStart();
        }
        Collections.reverse(chemin);
        return chemin;

    }

    private static void majDistances(List<Arc> accesibles, Map<Place,Double> distances,Map<Place,Place> predecesseurs,Map<Place,Arc> predecesseursArc)
    {
        for(Arc arc : accesibles)
        {
            double distActuelle = distances.get(arc.getFinish());
            double distdepart = distances.get(arc.getStart());
            double coutTrans = arc.getCout();
            double distPot = distdepart+coutTrans;
            if(distPot<distActuelle)
            {
                predecesseurs.put(arc.getFinish(),arc.getStart());
                predecesseursArc.put(arc.getFinish(),arc);
                distances.put(arc.getFinish(),distPot);
            }
        }
    }


    private static Place getPlaceNonExploreePlusProche(List<Place> placesExplorees,Map<Place,Double> distances)
    {
        double dist = Double.MAX_VALUE;
        Place retour = null;
        for(Place p : distances.keySet())
        {
            if(!placesExplorees.contains(p))
            {
                if(distances.get(p)<dist)
                {
                    dist = distances.get(p);
                    retour = p;
                }
            }
        }
        return retour;
    }

    private static Map<Place,Double> initDistances(List<? extends Arc> arcs)
    {
        Map<Place,Double> distances = new Hashtable<>();
        List<Place> places = getPlaces(arcs);
        for(Place pl : places)
        {
            distances.put(pl,Double.POSITIVE_INFINITY);
        }
        return distances;
    }

    private static List<Place> getPlaces(List<? extends Arc> arcs)
    {
        List<Place> places = new ArrayList<>();
        for(Arc arc : arcs)
        {
            if(!places.contains(arc.getStart()))
            {
                places.add(arc.getStart());
            }
            if(!places.contains(arc.getFinish()))
            {
                places.add(arc.getFinish());
            }
        }
        return places;
    }
    private static List<Arc> adjacent(List<? extends Arc> arcs, Place start)
    {
        List<Arc> ret = new ArrayList<>();
        for(Arc arc : arcs)
        {
            if(arc.getStart().equals(start))
            {
                ret.add(arc);
            }
        }
        return ret;
    }
}
