import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;


public class PathFinder {
    public static void main(String[] args)
    {
        List<Arc> arcs = genererNuageRoutier(10,0.5);

            Place init = arcs.get(0).getStart();
            Place fin = arcs.get(arcs.size() - 1).getFinish();
            List<Place> passage = cheminPlusCourt(arcs, init, fin);
        if(passage != null) {
            for (Place p : passage) {
                System.out.println(p.getNom());
            }
            dessinerNuage(arcs, passage, 1000,Color.YELLOW, "L:/Lab/gr1");
        }
    }
    public static List<Arc> genererNuage(int n)
    {
        Random r= new Random();
        List<Place> places = new ArrayList<>();
        List<Arc> arcs = new ArrayList<>();
        for(int i=0;i<n;i++)
        {
            places.add(new Place(Integer.toString(i),r.nextDouble(),r.nextDouble()));
        }
        for(Place p1 : places)
        {
            for(Place p2 : places)
            {
                if(!p1.equals(p2))
                {
                    double cout = Math.sqrt(Math.pow(p1.getX()-p2.getX(),2)+Math.pow(p1.getY()-p2.getY(),2));
                    Arc a1 = new Arc(p1,p2,cout);
                    arcs.add(a1);
                }
            }
        }
        return arcs;
    }
    public static List<Arc> genererNuageRoutier(int n, double p)
    {
        Random r= new Random();
        List<Place> places = new ArrayList<>();
        List<Arc> arcs = new ArrayList<>();
        for(int i=0;i<n;i++)
        {
            places.add(new Place(Integer.toString(i),r.nextDouble(),r.nextDouble()));
        }
        for(Place p1 : places)
        {
            for(Place p2 : places)
            {
                if(!p1.equals(p2))
                {
                    if(r.nextDouble()<p) {
                        double cout = Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
                        cout = cout*r.nextDouble();
                        Arc a1 = new Arc(p1, p2, cout);
                        arcs.add(a1);
                    }
                }
            }
        }
        return arcs;

    }

    public static void dessinerNuage(List<Arc> arcs,List<Place> passage, int dim,Color couleurPassage, String path)
    {
        BufferedImage bi = new
                BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        for(Arc arc : arcs)
        {
            Stroke str = new BasicStroke(10);
            g2d.setStroke(str);
            g2d.setColor(new Color((int)(arc.getCout()*255/Math.sqrt(2.0)),0,0));
            Place start = arc.getStart();
            Place finish = arc.getFinish();
            g2d.drawLine((int)(start.getX()*dim),(int)(start.getY()*dim),(int)(finish.getX()*dim),(int)(finish.getY()*dim));
        }
        for(int i=0;i<passage.size();i++)
        {
            Place p = passage.get(i);
            if(i+1<passage.size())
            {
                Place pnext = passage.get(i+1);
                Stroke str = new BasicStroke(3);
                g2d.setStroke(str);
                g2d.setColor(couleurPassage);
                g2d.drawLine((int)(p.getX()*dim),(int)(p.getY()*dim),(int)(pnext.getX()*dim),(int)(pnext.getY()*dim));
            }
            Stroke str = new BasicStroke(100);
            g2d.setStroke(str);
            g2d.setColor(Color.BLUE);
            g2d.drawString(p.getNom(),(int)(p.getX()*dim),(int)(p.getY()*dim));
        }

        try {
            File outputfile = new File(path+".png");
            ImageIO.write(bi, "png", outputfile);
        } catch (IOException e) {
            // handle exception
        }
    }

    public static List<Place> cheminPlusCourt(List<Arc> arcs, Place start, Place finish)
    {

        List<Place> placesExplorees =new ArrayList<>();
        Map<Place,Double> distances = initDistances(arcs);
        Map<Place,Place> predecesseurs = new Hashtable<>();
        distances.put(start,0.0);
        Place pp = start;
        do {
            List<Arc> accessible = adjacent(arcs,pp);
            majDistances(accessible,distances,predecesseurs);
            placesExplorees.add(pp);
            pp = getPlaceNonExploreePlusProche(placesExplorees,distances);
        }while(pp != null);
        return reconstituerChemin(predecesseurs,start,finish);
    }

    private static List<Place> reconstituerChemin(Map<Place,Place> predecesseurs,Place start, Place finish)
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

    private static void majDistances(List<Arc> accesibles, Map<Place,Double> distances,Map<Place,Place> predecesseurs)
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

    private static Map<Place,Double> initDistances(List<Arc> arcs)
    {
        Map<Place,Double> distances = new Hashtable<>();
        List<Place> places = getPlaces(arcs);
        for(Place pl : places)
        {
            distances.put(pl,Double.MAX_VALUE);
        }
        return distances;
    }

    private static List<Place> getPlaces(List<Arc> arcs)
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
    private static List<Arc> adjacent(List<Arc> arcs, Place start)
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
