import java.util.ArrayList;

public class SpacingEvaluator {
	
	ArrayList<Double> distances;
	final double INFINITY= 5000;
	boolean normalize= false;

	public double compute( ArrayList<double[]> solutions, double minPenalty, double minRobustness, double maxPenalty, double maxRobustness) {

		if (solutions.size()== 0)
			return 0;	
		distances= new ArrayList<Double>();
		
		double minDistance, distance, avgDistance;
		double spacingValue;
		
		// compute each solution's distance from other solutions and take the minimum
		for (int i=0; i< solutions.size(); i++){
			// compute this point's min distance:
			minDistance= INFINITY;
			for (int j=0; j< solutions.size(); j++){
				if (j==i) continue;
				distance= computeDistance(solutions.get(i), solutions.get(j), minPenalty, minRobustness, maxPenalty, maxRobustness);
				if (distance < minDistance)
					minDistance= distance;
			} // end i for
			distances.add(minDistance);
		} // end i for
		
		// Now compute average distance:
		double total= 0;
		for (double d: distances)
			total+= d;
		avgDistance= (total / distances.size());
		
		total= 0;
		for (double d: distances){
			total+= Math.pow((d - avgDistance),2);
		} // end d for each
		
		spacingValue= Math.sqrt((total / distances.size()));
		return spacingValue;
	}

	private double computeDistance(double[] point1, double[] point2, double minP, double minR, double maxP, double maxR) {
		// Note that this computation is different from Euclidean distance computation:
		if (normalize){
			return Math.abs((point2[0]-point1[0]) / (maxP - minP)) + Math.abs(point2[1]-point1[1] / (maxR - minR));
		}
		return Math.abs(point2[0]-point1[0]) + Math.abs(point2[1]-point1[1]);
	}
}
