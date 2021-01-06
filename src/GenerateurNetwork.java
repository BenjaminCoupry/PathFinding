import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GenerateurNetwork {
    public static void main(String[] args)
    {
        GenerateurNetwork.genererVille(3,2000,0.3,0.05,0.4,
                0.01,0.1,0.1,0.01,2);
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
                    Arc a1 = new ArcCout(p1,p2,cout);
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
        List<ArcPhysique> arcs = new ArrayList<>();
        List<Arc> arcs_ = new ArrayList<>();
        for(int i=0;i<n;i++)
        {
            places.add(new Place(Integer.toString(i),r.nextDouble(),r.nextDouble()));
        }
        int nbCo = (int)(p*n*n);
        for(int i=0;i<nbCo;i++)
        {
            Place p1 = places.get(r.nextInt(n));
            Place p2 = places.get(r.nextInt(n));
            if(!p1.equals(p2))
            {
                ArcPhysique a1 = new ArcPhysique(p1, p2, r.nextDouble()+1);
                if(!a1.croise(arcs)) {
                    arcs.add(a1);
                }
            }
        }
        for(Arc a : arcs)
        {
            arcs_.add(a);
        }
        return arcs_;

    }
    private static double dist(Place start, Place finish)
    {
        return Math.sqrt(Math.pow(start.getX() - finish.getX(), 2) + Math.pow(start.getY() - finish.getY(), 2));
    }
    private static Place distMin(Place start, List<Place> finish)
    {
        double dist = Double.MAX_VALUE;
        Place p_min = null;
        for(Place p : finish)
        {
            if(!p.equals(start)) {
                double d = dist(start, p);
                if (d < dist) {
                    dist = d;
                    p_min = p;
                }
            }
        }
        return p_min;
    }

    public static ArcPhysique creerLiaisonDirecte(Place depart, Place arrivee, List<ArcPhysique> arcs, double vitesse)
    {
        ArcPhysique a1 = new ArcPhysique(depart, arrivee, vitesse);
        ArcPhysique a2 = new ArcPhysique( arrivee, depart, vitesse);
        if(!a1.croise(arcs)) {
            arcs.add(a1);
            arcs.add(a2);
        }
        return a1;
    }
    public static Place creerPlaceAleatoire(int N, Random r)
    {
        Place nouv = new Place(Integer.toString(N),r.nextDouble(),r.nextDouble());
        return nouv;
    }
    public static Place creerPlaceProche(int N,double etalementDist,List<Place> places, Random r)
    {
        Place base = places.get(r.nextInt(places.size()));
        double x = Math.max(0,Math.min(1,base.getX()+r.nextGaussian()*etalementDist));
        double y = Math.max(0,Math.min(1,base.getY()+r.nextGaussian()*etalementDist));
        Place nouv = new Place(Integer.toString(N),x,y);
        return nouv;
    }
    public static Place choisirPlaceAleatoire(List<Place> places, Random r)
    {
        return places.get(r.nextInt(places.size()));
    }
    public static int ajouterPlaceEtRelier(int N,Place nouv,List<Place> places, List<ArcPhysique> arcs, double vitesse)
    {
        Place plusProche = distMin(nouv, places);
        ArcPhysique a1 = new ArcPhysique(plusProche, nouv, vitesse);
        ArcPhysique a2 = new ArcPhysique(nouv, plusProche,vitesse);
        if(!a1.croise(arcs)) {
            arcs.add(a1);
            arcs.add(a2);
        }
        places.add(nouv);
        return N+1;
    }
    public static double calculerCoutTotal(List<? extends Arc> passage)
    {
        double sommeCout = 0;
        for(int j=0;j<passage.size();j++)
        {
            sommeCout += passage.get(j).getCout();
        }
        return sommeCout;
    }
    public static List<Arc> allerRetourSiPossible(Place dep, Place arr, List<ArcPhysique> arcs, double deltaVitesse)
    {
        InfoChemin ic = PathFinder.calculerDistances(arcs, dep);
        List<Place> accesibles = PathFinder.endroitsAccesibles(ic);
        if (accesibles.contains(arr)) {
            List<Arc> passage = PathFinder.reconstituerCheminArcs(ic.getPredecesseursArc(),dep,arr);
            elargirRoute(passage,deltaVitesse);
            return passage;
        }
        return null;
    }
    public static void prolongerReseau(List<ArcPhysique>arcs, Place dep, Place arr, double vitesseInit, double deltaVitesse)
    {
        InfoChemin ic = PathFinder.calculerDistances(arcs, dep);
        List<Place> accesibles = PathFinder.endroitsAccesibles(ic);
        Place plusProche = distMin(arr, accesibles);
        List<Arc> passage = PathFinder.reconstituerCheminArcs(ic.getPredecesseursArc(),dep,plusProche);
        ArcPhysique dernierePartie = creerLiaisonDirecte(plusProche,arr,arcs,vitesseInit);
        passage.add(dernierePartie);
        elargirRoute(passage,deltaVitesse);
        creerLiaisonDirecte(plusProche,arr,arcs,vitesseInit);
    }
    public static boolean emprunterReseau(List<ArcPhysique>arcs, Place dep, Place arr,double coutMaxCorrespondace, double vitesseInit, double deltaVitesse)
    {
        List<Arc> passage = allerRetourSiPossible(dep,arr,arcs,deltaVitesse);
        if (passage != null)
        {
            if(calculerCoutTotal(passage)>coutMaxCorrespondace)
            {
                creerLiaisonDirecte(dep, arr, arcs, vitesseInit);

            }
            return true;
        }
        return false;
    }

    public static void creerRoutesItineraire(List<ArcPhysique>arcs, Place dep, Place arr,double coutMaxCorrespondace, double vitesseInit, double deltaVitesse)
    {
        if(!emprunterReseau(arcs,dep,arr,coutMaxCorrespondace,vitesseInit,deltaVitesse))
        {
            prolongerReseau(arcs,dep,arr,vitesseInit,deltaVitesse);
            prolongerReseau(arcs,arr,dep,vitesseInit,deltaVitesse);
        }
    }

    public static void elargirRoute(List<Arc> passage, double deltaVitesse)
    {
        for(Arc a : passage)
        {
            ArcPhysique ap = (ArcPhysique)a;
            ap.setVitesse(ap.getVitesse()+deltaVitesse);
        }
    }

    public static List<Arc> genererVille(int nbInit,int nbIter, double probaExtensionLieu, double probaNouvLieu,
                                         double probaNouveauChemin,double etalementDist, double vitesseInit, double deltaVitesse,
                                         double distMaxLiaisonDirecte, double coutMaxCorrespondace)
    {
        int N=0;
        Random r = new Random();
        List<Place> places = new ArrayList<>();
        List<ArcPhysique> arcs = new ArrayList<>();
        List<Arc> arcs_ = new ArrayList<>();
        for(int i=0;i<nbInit;i++)
        {
            places.add(creerPlaceAleatoire(N,r));
            N++;
        }
        for(int i=0;i<nbIter;i++)
        {
            Place dep = choisirPlaceAleatoire(places,r);
            Place arr = choisirPlaceAleatoire(places,r);
            if(!dep.equals(arr))
            {
                if(r.nextDouble()<probaNouveauChemin && dist(dep,arr)<distMaxLiaisonDirecte)
                {
                    creerLiaisonDirecte(dep,arr,arcs,vitesseInit);
                }
                else {
                    creerRoutesItineraire(arcs,dep,arr,coutMaxCorrespondace,vitesseInit,deltaVitesse);
                }
            }

            Place nouv = null;
            if(r.nextDouble()<probaNouvLieu)
            {
                nouv = creerPlaceAleatoire(N,r);
            }else if(r.nextDouble()<probaExtensionLieu)
            {
                nouv = creerPlaceProche(N,etalementDist,places,r);
            }

            if(nouv != null)
            {
                N=ajouterPlaceEtRelier(N,nouv,places,arcs,vitesseInit);
            }

            for(Arc a : arcs)
            {
                arcs_.add(a);
            }

            if(i%20 ==0) {
                GenerateurNetwork.dessinerNuage(arcs_, null, 10000, Color.YELLOW, "L:/Lab/gr_" + i);
            }
        }

        return arcs_;
    }

    public static void dessinerNuage(List<Arc> arcs, List<Place> passage, int dim, Color couleurPassage, String path)
    {
        BufferedImage bi = new
                BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        for(Arc arc : arcs)
        {
            Stroke str = new BasicStroke(7);
            g2d.setStroke(str);
            g2d.setColor(new Color((int)((1.0-Math.exp(-arc.getCout()))*255/Math.sqrt(2.0)),0,0));
            Place start = arc.getStart();
            Place finish = arc.getFinish();
            g2d.drawLine((int)(start.getX()*dim),(int)(start.getY()*dim),(int)(finish.getX()*dim),(int)(finish.getY()*dim));
        }
        if(passage!= null) {
            for (int i = 0; i < passage.size(); i++) {
                Place p = passage.get(i);
                if (i + 1 < passage.size()) {
                    Place pnext = passage.get(i + 1);
                    Stroke str = new BasicStroke(3);
                    g2d.setStroke(str);
                    g2d.setColor(couleurPassage);
                    g2d.drawLine((int) (p.getX() * dim), (int) (p.getY() * dim), (int) (pnext.getX() * dim), (int) (pnext.getY() * dim));
                }
                Stroke str = new BasicStroke(100);
                g2d.setStroke(str);
                g2d.setColor(Color.BLUE);
                g2d.drawString(p.getNom(), (int) (p.getX() * dim), (int) (p.getY() * dim));
            }
        }

        try {
            File outputfile = new File(path+".png");
            ImageIO.write(bi, "png", outputfile);
        } catch (IOException e) {
            // handle exception
        }
    }
}
