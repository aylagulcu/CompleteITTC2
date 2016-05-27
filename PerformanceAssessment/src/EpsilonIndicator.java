import java.math.BigDecimal;
import java.util.ArrayList;

public class EpsilonIndicator extends Indicator {

	// the following computes Iepsilon(fin,init), returns <1 if fin is better than init
	public Double compute( ArrayList<double[]> fin, ArrayList<double[]> init) {
		if (init.size()==0 || fin.size()==0)
			return 0.0;
		
		// keep the distance of each point in Init solutions from the points in Fin solutions:
		ArrayList<Double> initDistances= new ArrayList<Double>();
		ArrayList<Double> t1;

		for (double[] initPoint: init){
			// compute initPoint's distance from all points in rank1FinalSolutions
			t1= new ArrayList<Double>();
			for (double[] finPoint: fin){
				t1.add(Math.max(finPoint[0]/initPoint[0], finPoint[1]/initPoint[1]));
			} // end for each	
			
			// now, find the minimum for initPoint by using t2:
			double min= t1.get(0);
			for (double d : t1)
				if (d< min)
					min= d;
			initDistances.add(min);
		} // end for each
		assert initDistances.size()== init.size();
		
		if (initDistances.size()==0) return 0.0;
		double max= initDistances.get(0); // this will be our epsilon value!
		for (Double e: initDistances)
			if (e> max)
				max= e;

		BigDecimal bd= new BigDecimal(String.valueOf(max));
		bd= bd.setScale(2, BigDecimal.ROUND_UP);
		return bd.doubleValue();
	}

}
