package core;


public class Label_Star extends Label implements Comparable<Label_Star>{

	private double estimation;
	
	
	// Rayon de la terre en metres
	private static final double rayon_terre = 6378137.0 ;

	/**
	 *  Calcule de la distance orthodromique - plus court chemin entre deux points ������ la surface d'une sph������re
	 *  @param long1 longitude du premier point.
	 *  @param lat1 latitude du premier point.
	 *  @param long2 longitude du second point.
	 *  @param lat2 latitude du second point.
	 *  @return la distance entre les deux points en metres.
	 *  Methode ������������crite par Thomas Thiebaud, mai 2013
	 */
	public static double distance(float long1, float lat1, float long2, float lat2) {
		double sinLat = Math.sin(Math.toRadians(lat1))*Math.sin(Math.toRadians(lat2));
		double cosLat = Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2));
		double cosLong = Math.cos(Math.toRadians(long2-long1));
		return rayon_terre*Math.acos(sinLat+cosLat*cosLong);
	}


	public Label_Star (Noeud Source, Noeud Dest){
		super(Source.getNumero());
		if (!temps)
		this.estimation=distance(Source.getLong(),Source.getLat(),Dest.getLong(),Dest.getLat());
		else
			this.estimation=60*(distance(Source.getLong(),Source.getLat(),Dest.getLong(),Dest.getLat()))/(1000*Graphe.vitesse_max);
	}

	public int compareTo(Label_Star aux) 
	{
		//if (test)
		//System.out.println("compareTo de Label_Star");
		
		//test=false;
		
		double diff =  (this.getcout() + this.getEstimation()) - (aux.getcout()  + aux.getEstimation());	
		if (diff < 0)
			return -1;
		if (diff>0)
			return 1;
		diff=this.getEstimation() - aux.getEstimation();
		if (diff < 0)
			return -1;
		if (diff>0)
			return 1;
		return 0;
	}

	public double getEstimation(){
		return this.estimation;
	}


	public void setEstimation(double estimation) {
		this.estimation = estimation;
	}
}
