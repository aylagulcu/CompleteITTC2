import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class Test {

	public static void main(String[] args) throws NumberFormatException, IOException {
		
		/*
		HVI hvi= new HVI();
		
		String fileName= "../InstanceAllRuns/AllRunParetoResults.txt";

		hvi.getAllPoints(fileName);
		
		*/

//		double[][] solutionFront= getSolutionFront(hvi.points, instance, run);
		
		/*
		//Read the front from the files
		double[][] solutionFront = qualityIndicator.utils_.readFront("../InstanceAllRuns/Test.txt");

		
		for (int inst= 1; inst<= 21; inst++){
			for (int run= 0; run<30; run++){
				solutionFront= getSolutionFront(hvi.points, inst, run);

				System.out.println("Instance: "+ inst+"\tRun: "+run+ "\tHVIResult: "+ 0);
			}
		}
	*/
		
		
		// FINAL POP RUN2:
		double[][] solutionFrontFinal= {
				{	483	,	-3.0154550781	}	,
				{	294	,	-1.8783837891	}	,
				{	337	,	-1.5199902344	}	,
				{	645	,	-4.7340244141	}	,
				{	507	,	-3.3677792969	}	,
				{	420	,	-3.0743447266	}	,
				{	587	,	-2.5783837891	}	,
				{	520	,	-3.115703125	}	,
				{	583	,	-3.4978105469	}	,
				{	307	,	-1.7891689453	}	,
				{	470	,	-1.0040605469	}	,
				{	596	,	-3.0346337891	}	,
				{	457	,	-2.3236679688	}	,
				{	403	,	-1.8100625	}	,
				{	587	,	-3.359453125	}	,
				{	529	,	-4.1875214844	}	,
				{	496	,	-3.1217050781	}	,
				{	439	,	-0.5770976563	}	,
				{	556	,	-0.9374541016	}	,
				{	472	,	-1.6988486328	}	,
				{	676	,	-4.0045566406	}	,
				{	490	,	-0.2583476563	}	,
				{	448	,	-2.2269169922	}	,
				{	418	,	-1.1404550781	}	,
				{	525	,	-1.9885595703	}	,
				{	381	,	-1.4044892578	}	,
				{	664	,	-4.3402744141	}	,
				{	453	,	-2.0663125	}	,
				{	535	,	-2.9620253906	}	,
				{	516	,	-3.9081669922	}	,
				{	399	,	-1.7111679688	}	,
				{	676	,	-3.8712763672	}	,
				{	434	,	-2.4197421875	}	,
				{	399	,	-3.2688847656	}	,
				{	492	,	-2.1435234375	}	,
				{	441	,	-1.7154550781	}	,
				{	530	,	-4.3155224609	}	,
				{	405	,	-2.1579189453	}	,
				{	667	,	-4.6381308594	}	,
				{	402	,	-2.0754501953	}	,
		};

		for (int i=0; i< solutionFrontFinal.length; i++){
			for (int j=0; j< solutionFrontFinal[i].length; j++)
				System.out.print(solutionFrontFinal[i][j]+"\t");
			System.out.println();
		}
		
		double Pmax= 802; double Rmax= 1.679;
		System.out.println("HV is computed as: "+ computeHVI(solutionFrontFinal, Pmax, Rmax ));
		
		

	}

	private static double computeHVI(double[][] solutionFront, double maxP, double maxR) {
		int[] ranks= new int[solutionFront.length];
		
		// step1: Compute ranks:
		// Assign each individual a rank.
		// Each individual has a penalty and a robustness value. Compare these values...
		int dCount; // number of solutions dominating the current solution
		double p1; double r1;
		double p2; double r2;
		for (int i= 0; i< solutionFront.length; i++){
			dCount= 0; // number of solutions dominating i
			p1= solutionFront[i][0];
			r1= solutionFront[i][1];
			
			// find the number of individuals dominating this individual:
			for (int j= 0; j< solutionFront.length; j++){
				if (i==j) continue;
				p2= solutionFront[j][0];
				r2= solutionFront[j][1];
				if (p2<=p1 && r2<=r1){
					if (p2==p1 && r2==r1)
						continue; // Both values are equal. Probably, they are the same individuals
					// at least one objective should be smaller
					else if (p2<p1 || r2<r1)
						dCount+= 1; // jth solution dominates ith solution
				} // end if
			} // end j for
			ranks[i]= dCount + 1;
		} // end i for
		
		
		// print ranks:
		System.out.println("Now printing all individual ranks");
		for (int i: ranks)
			System.out.println(i);
		
		
		// step2: now choose only the individuals with rank 1:
		double[] temp;
		ArrayList<double[]> rank1Solutions= new ArrayList<double[]>();
		for (int i=0; i< solutionFront.length; i++){
			if (ranks[i]== 1){
				temp= new double[2];
				temp[0]= solutionFront[i][0];
				if (temp[0] >= 1000) 
					continue;
				temp[1]= solutionFront[i][1];
				rank1Solutions.add(temp);
			} // end if
		} // end i for
		
		System.out.println();
		System.out.println("Now printing the front 1 individuals");
		for (double[] d: rank1Solutions)
			System.out.println(d[0]+"\t"+ d[1]);
		
		
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
		
		
		System.out.println();
		System.out.println("Now printing the sortedSolutions individuals");
		for (double[] d: sortedSolutions)
			System.out.println(d[0]+"\t"+ d[1]);
		
		// now compute HVI:
		double[] pointsHVContribution= new double[sortedSolutions.size()];
		double h= 0;
		
		temp= sortedSolutions.get(0);
		h= (maxP-temp[0])* Math.abs((maxR- temp[1]));
		pointsHVContribution[0]= h;
		
		double[] tempPrevious;
		
		for (int i=1; i< sortedSolutions.size(); i++){
			tempPrevious= sortedSolutions.get(i-1);
			temp= sortedSolutions.get(i);
			h= (maxP-temp[0])* Math.abs(tempPrevious[1]- temp[1]);
			pointsHVContribution[i]= h;
		} // end i for
		
		System.out.println("Points HV contribution:");
		for (double d: pointsHVContribution)
			System.out.println(d);
		
		double totalHV= 0;
		for (double d: pointsHVContribution)
			totalHV+= d;
		
		System.out.println();
		System.out.println("Rank1 solutions P & R and HV contribution:");
		for (int t=0; t< sortedSolutions.size(); t++)
			System.out.println(sortedSolutions.get(t)[0]+"\t"+ sortedSolutions.get(t)[1]+"\t"+ pointsHVContribution[t]);
		
		
		return totalHV;
		
	}

	private static double[][] getSolutionFront(List<double[]> points, int inst, int run) {
		
		double[][] front= new double[40][2];
		
		double[] tempArray;
		int counter=0;
		for (int i=0; i< points.size(); i++){
			tempArray= points.get(i);
			if (tempArray[0]== inst && tempArray[1]== run){
				front[counter][0]= tempArray[5]; front[counter][1]= tempArray[6];
				counter++;
			} // end if
		} // end for

		
		return front;
	}

}
