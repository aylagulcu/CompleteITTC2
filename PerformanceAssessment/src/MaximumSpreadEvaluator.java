import java.util.ArrayList;

public class MaximumSpreadEvaluator {

	public double compute( ArrayList<double[]> points, double minPenalty, double minRobustness, double maxPenalty, double maxRobustness) {
		// normalized euclidean distance is being computed:
		if (points.size() == 0)
			return 0;
		
		double Pdistance= Math.pow((getMaxP(points)-getMinP(points)) / (maxPenalty-minPenalty),2);
		double Rdistance= Math.pow((getMaxR(points)-getMinR(points)) / (maxRobustness-minRobustness),2);
				
		return Math.sqrt((Pdistance+Rdistance) / 2);
	}

	private double getMaxP(ArrayList<double[]> points) {
		double maxP= points.get(0)[0];
		for (double[] d: points)
			if (d[0] > maxP)
				maxP= d[0];
		return maxP;
	}
	
	private double getMinP(ArrayList<double[]> points) {
		double minP= points.get(0)[0];
		for (double[] d: points)
			if (d[0] < minP)
				minP= d[0];
		return minP;
	}
	
	private double getMaxR(ArrayList<double[]> points) {
		double maxR= points.get(0)[1];
		for (double[] d: points)
			if (d[1] > maxR)
				maxR= d[1];
		return maxR;
	}
	
	private double getMinR(ArrayList<double[]> points) {
		double minR= points.get(0)[1];
		for (double[] d: points)
			if (d[1] < minR)
				minR= d[1];
		return minR;
	}
	
	
	

}
