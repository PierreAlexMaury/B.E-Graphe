package core;

public class Segment {
	
	private float delta_long;
	private float delta_lat;
	
	public Segment(float delta_long, float delta_lat) {
		this.delta_long = delta_long;
		this.delta_lat = delta_lat;
	}
	
	public float getDelta_long() {
		  return this.delta_long;
	}
	
	public float getDela_lat() {
		  return this.delta_lat;
	}
}
