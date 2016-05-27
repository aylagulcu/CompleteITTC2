import java.math.BigDecimal;
import java.util.ArrayList;

public class HVIndicator extends Indicator {


	public double compute(ArrayList<double[]> solutionFront, double maxP, double maxR) {
	
		double[] temp;
		ArrayList<double[]> rank1Solutions= new ArrayList<double[]>();
		for (int i=0; i< solutionFront.size(); i++){
			temp= new double[2];
			temp[0]= solutionFront.get(0)[0];
			temp[1]= solutionFront.get(0)[1];
			rank1Solutions.add(temp);
		} // end i for

		
		// sort individuals: start from upper left corner; end at the lower right corner:
		// we applied weak robustness: z' dominates z'' if it is equal in all objectives and better in at least one. 
		int bestInd= 0;
		int index= 0;
		ArrayList<double[]> sortedSolutions= new ArrayList<double[]>();
		int lenIndividuals= rank1Solutions.size();
		while (index< lenIndividuals){
			bestInd= 0;
			for (int i= 1; i< rank1Solutions.size(); i++){
				if (rank1Solutions.get(i)[1] > rank1Solutions.get(bestInd)[1] ){
					bestInd= i;
				} // end if
			} // end for
			temp= new double[2];
			temp[0]= rank1Solutions.get(bestInd)[0];
			temp[1]= rank1Solutions.get(bestInd)[1];
			sortedSolutions.add(temp);

			rank1Solutions.remove(bestInd);
			index+= 1;	
		}; // end while

		// now compute HVI:
		double[] pointsHVContribution= new double[sortedSolutions.size()];
		double h= 0;

		if (sortedSolutions.size()== 0) return 0;
			
		temp= sortedSolutions.get(0);
		h= (maxP-temp[0])* Math.abs((maxR- temp[1]));
		pointsHVContribution[0]= h;

		double[] tempPrevious;

		for (int i=1; i< sortedSolutions.size(); i++){
			tempPrevious= sortedSolutions.get(i-1);
			temp= sortedSolutions.get(i);
			assert (maxP-temp[0]) >=0;
			h= (maxP-temp[0])* Math.abs(tempPrevious[1]- temp[1]);
			assert h>= 0;
			pointsHVContribution[i]= h;
		} // end i for

		double totalHV= 0;
		for (double d: pointsHVContribution)
			totalHV+= d;
		
		BigDecimal bd= new BigDecimal(String.valueOf(totalHV));
		bd= bd.setScale(2, BigDecimal.ROUND_UP);
		return bd.doubleValue();
	}

}
