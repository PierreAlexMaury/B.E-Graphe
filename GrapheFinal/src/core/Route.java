package core;

import java.util.ArrayList;

import base.Descripteur;

public class Route {

	private Noeud dest_node;
	private Noeud src_node;
	private int longueur_route;
	private Descripteur descr;
	private ArrayList <Segment> segments;

	public Noeud getsrc_node () {
		return this.src_node;		
	}

	public Noeud getdest_node () {
		return this.dest_node;		
	}

	public int getlongueur_route () {
		return this.longueur_route;
	}

	public Descripteur getdescr() {
		return this.descr;
	}

	public Route () {
		
	}
	
	public Route (Noeud srcnode, Noeud destnode, int lgr, Descripteur descr) {
		this.src_node=srcnode;
		this.dest_node=destnode;
		this.longueur_route=lgr;
		this.descr=descr;
		this.segments = new ArrayList <Segment> ();
	}

	public void addSegment(Segment seg) {
		this.segments.add(seg);
	}

}
