import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Test {

	static String instNo; 
	static String fileHeader;
	
	static File file;	
	
	static int runs= 30;
	static int pop_size= 40;
	static int iterations= 31;

	// The following 6 list will be filled from the text files:
	static List<double[]> runInitPenalties; // from run 0 to run 29; all 40 (pop size) initial penalty values are kept
	static List<double[]> runFinalPenalties; // from run 0 to run 29; all 40 (pop size) final penalty values are kept
	
	static List<double[]> runInitRobValues; // from run 0 to run 29; all 40 (pop size) initial robustness values are kept
	static List<double[]> runFinalRobValues; // from run 0 to run 29; all 40 (pop size) final robustness values are kept
	
	static List<double[]> runInitHeuristicRobValues; // from run 0 to run 29; all 40 (pop size) initial robustness values are kept
	static List<double[]> runFinalHeuristicRobValues; // from run 0 to run 29; all 40 (pop size) final robustness values are kept
	
	static ArrayList<ArrayList<double[]>> runInitRank1Solutions; // from run 0 to run 29, all initial run Rank1 solutions are kept here
	static ArrayList<ArrayList<double[]>> runFinalRank1Solutions; // from run 0 to run 29, all final run Rank1 solutions are kept here
	
	static ArrayList<double[]> finalAggregateFront;
	static ArrayList<double[]> initAggregateFront;
	static ArrayList<double[]> aggregateFront;
	
	static List<Double> runInitialHV= new ArrayList<Double>();
	static List<Double> runFinalHV= new ArrayList<Double>();
	static List<Double> HVimprovementPercentage= new ArrayList<Double>();
	
	static List<Double> runEpsilonFinalInit= new ArrayList<Double>();
	
	static HVIndicator hvIndicator= new HVIndicator();
	static EpsilonIndicator eIndicator= new EpsilonIndicator();
	static EuclideanDistanceEvaluator eucEvaluator= new EuclideanDistanceEvaluator();
	static SpacingEvaluator spaceEvaluator= new SpacingEvaluator();
	static MaximumSpreadEvaluator maxSpreadEvaluator= new MaximumSpreadEvaluator();

	public static void main(String[] args) throws NumberFormatException, IOException {
		
		for (int instance= 1; instance<=1; instance++){
			System.out.println("Instance no: "+instance);
			instNo= String.valueOf(instance);
//			fileHeader= "../Runs22April/Instance"+ instNo;
			fileHeader= "../Runs19MayRealR/Instance"+ instNo;

			getPenalties();
			getRealRobustness();
			getHeuristicRobustness();

			getRank1Solutions(); // fills list: runInitRank1Solutions and runFinalRank1Solutions
			modifyRunRank1Solutions(); // Remove solutions with r<0, modifies the list filled with previous function
			
			printRank1Individuals();
			createFinalAggregateFront(); // fills lis: finalAggregateFront;
			createInitAggregateFront(); // fills lis: initAggregateFront;
			createAggregateFront();
			
			// Global maximum and minimum values are found:
			double globalMinP= computeMinPenalty(); // Minimum penalty among all runs, both initial and final
			double globalMinR= computeMinRobustness(); // Minimum robustness value among all runs, both initial and final 
			double globalMaxP= computeMaxPenalty(); // among initial and final rank1 individuals
			double globalMaxR= computeMaxRobustness(); // among initial and final rank1 individuals
			
			System.out.println("MaxP= "+ globalMaxP+ "\tMaxR= "+ globalMaxR);
			
			System.out.println("INITIAL RANK 1 INDIVIDUALS STATISTICS:");
			Writer outInitial = null;
			file= new File("../Runs19MayRealR/StatisticsInitial.txt");
			try {
				outInitial=  new BufferedWriter(new FileWriter(file, false )); // true: append mode.
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			outInitial.write("Instance "+ instance+": "+ System.getProperty( "line.separator"));	
			
			ArrayList<double[]> tempList;
			for (int r=0; r< runs; r++){
				tempList= runInitRank1Solutions.get(r);
				int pmin= (int)getMin(tempList, 0);
				double pavg= getAvg(tempList, 0);
				int pmax= (int)getMax(tempList, 0);
				int prange= pmax-pmin;
				
				double rmin= getMin(tempList, 1);
				double ravg= getAvg(tempList, 1);
				double rmax= getMax(tempList, 1);
				double rrange= rmax-rmin;
				
				BigDecimal bd= new BigDecimal(String.valueOf(rrange));
				bd= bd.setScale(2, BigDecimal.ROUND_UP);
				rrange= bd.doubleValue();
				
				outInitial.write(tempList.size()+"\t"+ pmin +"\t"+ pavg +"\t"+ pmax+"\t"+ prange +"\t" + 
						rmin +"\t"+ ravg +"\t"+ rmax+"\t"+ rrange+ System.getProperty( "line.separator"));		
			} // end r for
			printStatisticsInitial( globalMaxP, globalMaxR, outInitial); // statistics of statistics are being computed
			// now writing initial front statistics are finished.
			
			System.out.println("FINAL RANK 1 INDIVIDUALS:");
			Writer outFinal = null;
			file= new File("../Runs19MayRealR/StatisticsFinal.txt");
			try {
				outFinal=  new BufferedWriter(new FileWriter(file, false )); // true: append mode.
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			outFinal.write("Instance "+ instance+": "+ System.getProperty( "line.separator"));	
			for (int r=0; r< runs; r++){
				tempList= runFinalRank1Solutions.get(r);
				int pmin= (int)getMin(tempList, 0);
				double pavg= getAvg(tempList, 0);
				int pmax= (int)getMax(tempList, 0);
				int prange= pmax-pmin;
				
				double rmin= getMin(tempList, 1);
				double ravg= getAvg(tempList, 1);
				double rmax= getMax(tempList, 1);
				double rrange= rmax-rmin;
				
				BigDecimal bd= new BigDecimal(String.valueOf(rrange));
				bd= bd.setScale(2, BigDecimal.ROUND_UP);
				rrange= bd.doubleValue();
				
				outFinal.write(tempList.size()+"\t"+ pmin +"\t"+ pavg +"\t"+ pmax+"\t"+ prange +"\t" + 
						rmin +"\t"+ ravg +"\t"+ rmax+"\t"+ rrange+ System.getProperty( "line.separator"));		
			} // end r for
			printStatisticsFinal( globalMaxP, globalMaxR, outFinal );
			// now writing final front statistics are finished.

			
			ArrayList<double[]> tempFinal; ArrayList<double[]> tempInit;
			ArrayList<double[]> optimal= new ArrayList<double[]>();
			
			// Compute Hyper Volumes:
			outFinal.write("Hyper Volumes reference point R={Pmax,Rmax}:"+ System.getProperty( "line.separator"));	
			outFinal.write("Run\t"+ "HV(initialRun,R):\t"+ "HV(finalRun,R):\t"+ System.getProperty( "line.separator"));	
			for (int r=0; r< runs; r++){
				tempInit= runInitRank1Solutions.get(r);
				tempFinal= runFinalRank1Solutions.get(r);
				outFinal.write(r+"\t"+ hvIndicator.compute(tempInit, globalMaxP, globalMaxR)
					+"\t"+hvIndicator.compute(tempFinal,globalMaxP, globalMaxR)+ System.getProperty( "line.separator"));	
			} // end r for
			
			
			// Compute Epsilon Distances:
			double[] opt= {globalMinP, globalMinR}; // minimum
			optimal.add(opt);
			outFinal.write("Epsilon Distance Values (A,B):"+ System.getProperty( "line.separator"));	
			outFinal.write("Run\t"+ "Iepsilon(finalAgg,finalRun):\t"+"Iepsilon(finalRun,finalAgg):\t"+ "Iepsilon(optimal,finalRun):\t"+ "Iepsilon(finalRun,optimal):\t"+  "Iepsilon(finalRun,initRun):\t"+ "Iepsilon(initRun,finalRun):\t"+ System.getProperty( "line.separator"));	
			for (int r=0; r< runs; r++){
				tempInit= runInitRank1Solutions.get(r);
				tempFinal= runFinalRank1Solutions.get(r);
				outFinal.write(r+"\t"+ eIndicator.compute(finalAggregateFront,tempFinal)+"\t"+ eIndicator.compute(tempFinal,finalAggregateFront)
					+"\t"+eIndicator.compute(optimal,tempFinal)+"\t"+ eIndicator.compute(tempFinal,optimal)
					+"\t"+eIndicator.compute(tempFinal,tempInit) +"\t"+eIndicator.compute(tempInit,tempFinal)+ System.getProperty( "line.separator"));	
			} // end r for
			
			// Compute Euclidean Distances && Generational Distances:
			outFinal.write("Euclidean Distance Values"+ System.getProperty( "line.separator"));	
			outFinal.write("Run\t"+"InitMinDistance\t"+ "InitAvgDistance\t"+ "InitDistanceStDev\t"+ "InitGD\t"+
					"FinalMinDistance\t"+ "FinalAvgDistance\t"+ "FinalDistanceStDev\t"+ "FinalGD\t"+ System.getProperty( "line.separator"));
			for (int r=0; r< runs; r++){
				tempInit= runInitRank1Solutions.get(r);
				tempFinal= runFinalRank1Solutions.get(r);
				eucEvaluator.compute(aggregateFront, tempInit, tempFinal, globalMinP, globalMinR, globalMaxP, globalMaxR);	
				outFinal.write(r+"\t"+ eucEvaluator.getInitialMinDistance()+"\t"+ eucEvaluator.getInitialAvgDistance()+"\t"+ 
						eucEvaluator.getInitialDistanceStDev()+"\t"+ eucEvaluator.getInitGD()+"\t"+
						eucEvaluator.getFinalMinDistance()+"\t"+ eucEvaluator.getFinalAvgDistance()+"\t"+
						eucEvaluator.getFinalDistanceStDev()+"\t"+eucEvaluator.getFinalGD() + System.getProperty( "line.separator"));	
			} // end r for
			
			// Spacing Values are being computed:
			outFinal.write("Spacing Value (S):"+ System.getProperty( "line.separator"));	
			outFinal.write("Run\tInitial S:\tFinal S"+ System.getProperty( "line.separator"));
			for (int r=0; r< runs; r++){
				tempInit= runInitRank1Solutions.get(r);
				tempFinal= runFinalRank1Solutions.get(r);
				outFinal.write(r+"\t"+spaceEvaluator.compute(tempInit, globalMinP, globalMinR, globalMaxP, globalMaxR)+"\t"+
						spaceEvaluator.compute(tempFinal,globalMinP, globalMinR, globalMaxP, globalMaxR)+ System.getProperty( "line.separator"));	
			} // end r for
			// Aggregates are being computed:
			outFinal.write("Aggregate Lists:");	
			outFinal.write("Spacing in Aggregate Initial:\t"+ spaceEvaluator.compute(initAggregateFront, globalMinP, globalMinR, globalMaxP, globalMaxR)+ 
					System.getProperty( "line.separator"));	
			outFinal.write("Spacing in Aggregate Final:\t"+ spaceEvaluator.compute(finalAggregateFront, globalMinP, globalMinR, globalMaxP, globalMaxR)+ 
					System.getProperty( "line.separator"));	


			// Maximum Spread Values are being computed:
			outFinal.write("Maximum Spread Values (maximum= 1)"+ System.getProperty( "line.separator"));	
			outFinal.write("Run\t"+"InitialSpread\t"+ "FinalSpread\t" + System.getProperty( "line.separator"));
			for (int r=0; r< runs; r++){
				tempInit= runInitRank1Solutions.get(r);
				tempFinal= runFinalRank1Solutions.get(r);
				
				outFinal.write(r+"\t"+ maxSpreadEvaluator.compute(tempInit, globalMinP, globalMinR, globalMaxP, globalMaxR)+"\t"+
						maxSpreadEvaluator.compute(tempFinal,globalMinP, globalMinR, globalMaxP, globalMaxR)+ System.getProperty( "line.separator"));	
			} // end r for
			
			
			outInitial.close();
			outFinal.close();
		} // end instance for
		
	}
	
	
	private static void createAggregateFront() {
		// uses initAggregateFront and finalAggregateFront and creates a Grand Aggregate Front:
		aggregateFront= new ArrayList<double[]>();
		ArrayList<double[]> tempAgg= new ArrayList<double[]>();
		double[] tempArr;
		for (double[] t: initAggregateFront){
			tempArr= new double[2];
			tempArr[0]= t[0]; tempArr[1]=t[1];
			tempAgg.add(tempArr);
		}
		
		for (double[] t: finalAggregateFront){
			tempArr= new double[2];
			tempArr[0]= t[0]; tempArr[1]=t[1];
			tempAgg.add(tempArr);
		}
			
		// now modify finalAggregateFront so that only rank1 solutions remain:
		int[] ranks= new int[tempAgg.size()];
		
		// step1: Compute ranks
		// Assign each individual a rank based on P and R
		int dCount; // number of solutions dominating the current solution
		double p1; double r1;
		double p2; double r2;
		for (int i= 0; i< tempAgg.size(); i++){
			dCount= 0; // number of solutions dominating i
			p1= tempAgg.get(i)[0];
			r1= tempAgg.get(i)[1];

			// find the number of individuals dominating this individual:
			for (int j= 0; j< tempAgg.size(); j++){
				if (i==j) continue;
				p2= tempAgg.get(j)[0];
				r2= tempAgg.get(j)[1];
				
				if (p2<=p1 && r2<=r1){
					if (p2==p1 && r2==r1)
						continue; // Both values are equal. Probably, they are the same individuals
					// at least one objective should be better
					else if (p2<p1 || r2<r1)
						dCount+= 1; // jth solution dominates ith solution
				} // end if
			} // end j for
			ranks[i]= dCount + 1;
		} // end i for
		
		// step2: now choose only the individuals with rank 1:
		double[] temp;
		for (int i=0; i< tempAgg.size(); i++){
			if (ranks[i]== 1){
				temp= new double[2];
				temp[0]= tempAgg.get(i)[0];
				temp[1]= tempAgg.get(i)[1];
				aggregateFront.add(temp);
			} // end if
		} // end i for

		System.out.println("Total (Initial+Final) Aggregate Front: ");
		for (int i=0; i< aggregateFront.size(); i++)
			System.out.println(aggregateFront.get(i)[0]+"\t"+aggregateFront.get(i)[1]);
		System.out.println("Total Aggregate Front OK");
		
	}


	private static void createInitAggregateFront() {
		ArrayList<double[]> tempInitAggregateFront= new ArrayList<double[]>();
		initAggregateFront= new ArrayList<double[]>();

		ArrayList<double[]> tempList;
		double[] tempArr;
		
		for (int r=0; r< runs; r++){
			tempList= runInitRank1Solutions.get(r);
			for (double[] t: tempList){
				tempArr= new double[2];
				tempArr[0]= t[0]; tempArr[1]=t[1];
				tempInitAggregateFront.add(tempArr);
			} // end for
		} // end r for
		
		// now modify finalAggregateFront so that only rank1 solutions remain:
		int[] ranks= new int[tempInitAggregateFront.size()];
		
		// step1: Compute ranks
		// Assign each individual a rank based on P and R
		int dCount; // number of solutions dominating the current solution
		double p1; double r1;
		double p2; double r2;
		for (int i= 0; i< tempInitAggregateFront.size(); i++){
			dCount= 0; // number of solutions dominating i
			p1= tempInitAggregateFront.get(i)[0];
			r1= tempInitAggregateFront.get(i)[1];

			// find the number of individuals dominating this individual:
			for (int j= 0; j< tempInitAggregateFront.size(); j++){
				if (i==j) continue;
				p2= tempInitAggregateFront.get(j)[0];
				r2= tempInitAggregateFront.get(j)[1];
				
				if (p2<=p1 && r2<=r1){
					if (p2==p1 && r2==r1)
						continue; // Both values are equal. Probably, they are the same individuals
					// at least one objective should be better
					else if (p2<p1 || r2<r1)
						dCount+= 1; // jth solution dominates ith solution
				} // end if
			} // end j for
			ranks[i]= dCount + 1;
		} // end i for
		
		// step2: now choose only the individuals with rank 1:
		double[] temp;
		for (int i=0; i< tempInitAggregateFront.size(); i++){
			if (ranks[i]== 1){
				temp= new double[2];
				temp[0]= tempInitAggregateFront.get(i)[0];
				temp[1]= tempInitAggregateFront.get(i)[1];
				initAggregateFront.add(temp);
			} // end if
		} // end i for

		System.out.println("Initial Aggregate Front: ");
		for (int i=0; i< initAggregateFront.size(); i++)
			System.out.println(initAggregateFront.get(i)[0]+"\t"+initAggregateFront.get(i)[1]);
		System.out.println("Initial Aggregate Front OK");
	}


	private static void createFinalAggregateFront() {
		ArrayList<double[]> tempFinalAggregateFront= new ArrayList<double[]>();
		finalAggregateFront= new ArrayList<double[]>();

		ArrayList<double[]> tempList;
		double[] tempArr;
		
		for (int r=0; r< runs; r++){
			tempList= runFinalRank1Solutions.get(r);
			for (double[] t: tempList){
				tempArr= new double[2];
				tempArr[0]= t[0]; tempArr[1]=t[1];
				tempFinalAggregateFront.add(tempArr);
			} // end for
		} // end r for
		
		// now modify finalAggregateFront so that only rank1 solutions remain:
		int[] ranks= new int[tempFinalAggregateFront.size()];
		
		// step1: Compute ranks
		// Assign each individual a rank based on P and R
		int dCount; // number of solutions dominating the current solution
		double p1; double r1;
		double p2; double r2;
		for (int i= 0; i< tempFinalAggregateFront.size(); i++){
			dCount= 0; // number of solutions dominating i
			p1= tempFinalAggregateFront.get(i)[0];
			r1= tempFinalAggregateFront.get(i)[1];

			// find the number of individuals dominating this individual:
			for (int j= 0; j< tempFinalAggregateFront.size(); j++){
				if (i==j) continue;
				p2= tempFinalAggregateFront.get(j)[0];
				r2= tempFinalAggregateFront.get(j)[1];
				
				if (p2<=p1 && r2<=r1){
					if (p2==p1 && r2==r1)
						continue; // Both values are equal. Probably, they are the same individuals
					// at least one objective should be better
					else if (p2<p1 || r2<r1)
						dCount+= 1; // jth solution dominates ith solution
				} // end if
			} // end j for
			ranks[i]= dCount + 1;
		} // end i for
		
		// step2: now choose only the individuals with rank 1:
		double[] temp;
		for (int i=0; i< tempFinalAggregateFront.size(); i++){
			if (ranks[i]== 1){
				temp= new double[2];
				temp[0]= tempFinalAggregateFront.get(i)[0];
				temp[1]= tempFinalAggregateFront.get(i)[1];
				finalAggregateFront.add(temp);
			} // end if
		} // end i for

		System.out.println("Final Aggregate Front: ");
		for (int i=0; i< finalAggregateFront.size(); i++)
			System.out.println(finalAggregateFront.get(i)[0]+"\t"+finalAggregateFront.get(i)[1]);
		System.out.println("Final Aggregate Front OK");
	}


	private static void printStatisticsFinal(double maxP, double maxR, Writer outFinal) throws IOException {
		ArrayList<Double> frontSizeEachRun= new ArrayList<Double>(); // number of rank 1 solutions at each run is kept here.

		ArrayList<Double> frontMinPEachRun= new ArrayList<Double>();
		ArrayList<Double> frontMaxPEachRun= new ArrayList<Double>();
		ArrayList<Double> frontAvgPEachRun= new ArrayList<Double>();
		ArrayList<Double> frontRangePEachRun= new ArrayList<Double>();

		ArrayList<Double> frontMinREachRun= new ArrayList<Double>();
		ArrayList<Double> frontMaxREachRun= new ArrayList<Double>();
		ArrayList<Double> frontAvgREachRun= new ArrayList<Double>();
		ArrayList<Double> frontRangeREachRun= new ArrayList<Double>();

		ArrayList<double[]> tempList;
		for (int r=0; r< runs; r++){
			tempList= runFinalRank1Solutions.get(r);
			if (tempList.size()==0)
				continue;
			double minPen= tempList.get(0)[0];
			double maxPen= tempList.get(0)[0];
			double totalP= 0;
					
			double minRob= tempList.get(0)[1];
			double maxRob= tempList.get(0)[1];
			double totalR= 0;
			
			for (double[] point: tempList){
				if (point[0] < minPen)
					minPen= point[0];
				if (point[0] > maxPen)
					maxPen= point[0];
				totalP+= point[0];
				
				if (point[1] < minRob)
					minRob= point[1];
				if (point[1] > maxRob)
					maxRob= point[1];
				totalR+= point[1];
			} // end for
			
			// Now, record statistics:
			frontSizeEachRun.add((double) tempList.size());
			
			frontMinPEachRun.add(minPen);
			frontMaxPEachRun.add(maxPen);
			frontAvgPEachRun.add(totalP/ tempList.size());
			frontRangePEachRun.add(maxPen- minPen);
			
			frontMinREachRun.add(minRob);
			frontMaxREachRun.add(maxRob);
			frontAvgREachRun.add(totalR/ tempList.size());
			frontRangeREachRun.add(maxRob - minRob);
		} // end r for

		outFinal.write(getMin(frontSizeEachRun)+"\t"+ getMin(frontMinPEachRun)+"\t"+ getMin(frontAvgPEachRun)+ "\t"+ getMin(frontMaxPEachRun)+ "\t"+  getMin(frontRangePEachRun)+ "\t"+
				getMin(frontMinREachRun)+"\t"+ getMin(frontAvgREachRun)+ "\t"+ getMin(frontMaxREachRun)+ "\t"+  getMin(frontRangeREachRun)
				+ System.getProperty( "line.separator"));

		outFinal.write(getAvg(frontSizeEachRun)+"\t"+ getAvg(frontMinPEachRun)+"\t"+ getAvg(frontAvgPEachRun)+ "\t"+ getAvg(frontMaxPEachRun)+ "\t"+  getAvg(frontRangePEachRun)+ "\t"+
				getAvg(frontMinREachRun)+"\t"+ getAvg(frontAvgREachRun)+ "\t"+ getAvg(frontMaxREachRun)+ "\t"+  getAvg(frontRangeREachRun)
				+ System.getProperty( "line.separator"));

		outFinal.write(getMax(frontSizeEachRun)+"\t"+ getMax(frontMinPEachRun)+"\t"+ getMax(frontAvgPEachRun)+ "\t"+ getMax(frontMaxPEachRun)+ "\t"+  getMax(frontRangePEachRun)+ "\t"+
				getMax(frontMinREachRun)+"\t"+ getMax(frontAvgREachRun)+ "\t"+ getMax(frontMaxREachRun)+ "\t"+  getMax(frontRangeREachRun)
				+ System.getProperty( "line.separator"));
		
		outFinal.write(getMedian(frontSizeEachRun)+"\t"+ getMedian(frontMinPEachRun)+"\t"+ getMedian(frontAvgPEachRun)+ "\t"+ getMedian(frontMaxPEachRun)+ "\t"+  getMedian(frontRangePEachRun) +"\t"+
				getMedian(frontMinREachRun)+"\t"+ getMedian(frontAvgREachRun)+ "\t"+ getMedian(frontMaxREachRun)+ "\t"+  getMedian(frontRangeREachRun)
				+ System.getProperty( "line.separator"));
				
	}


	private static void printStatisticsInitial(double maxP, double maxR, Writer outInitial) throws IOException {
		
		ArrayList<Double> frontSizeEachRun= new ArrayList<Double>(); // number of rank 1 solutions at each run is kept here.

		ArrayList<Double> frontMinPEachRun= new ArrayList<Double>();
		ArrayList<Double> frontMaxPEachRun= new ArrayList<Double>();
		ArrayList<Double> frontAvgPEachRun= new ArrayList<Double>();
		ArrayList<Double> frontRangePEachRun= new ArrayList<Double>();

		ArrayList<Double> frontMinREachRun= new ArrayList<Double>();
		ArrayList<Double> frontMaxREachRun= new ArrayList<Double>();
		ArrayList<Double> frontAvgREachRun= new ArrayList<Double>();
		ArrayList<Double> frontRangeREachRun= new ArrayList<Double>();

		ArrayList<double[]> tempList;
		for (int r=0; r< runs; r++){
			tempList= runInitRank1Solutions.get(r);
			if (tempList.size()==0)
				continue;
			double minPen= tempList.get(0)[0];
			double maxPen= tempList.get(0)[0];
			double totalP= 0;
					
			double minRob= tempList.get(0)[1];
			double maxRob= tempList.get(0)[1];
			double totalR= 0;
			
			for (double[] point: tempList){
				if (point[0] < minPen)
					minPen= point[0];
				if (point[0] > maxPen)
					maxPen= point[0];
				totalP+= point[0];
				
				if (point[1] < minRob)
					minRob= point[1];
				if (point[1] > maxRob)
					maxRob= point[1];
				totalR+= point[1];
			} // end for
			
			// Now, record statistics:
			frontSizeEachRun.add((double) tempList.size());
			
			frontMinPEachRun.add(minPen);
			frontMaxPEachRun.add(maxPen);
			frontAvgPEachRun.add(totalP/ tempList.size());
			frontRangePEachRun.add(maxPen- minPen);
			
			frontMinREachRun.add(minRob);
			frontMaxREachRun.add(maxRob);
			frontAvgREachRun.add(totalR/ tempList.size());
			frontRangeREachRun.add(maxRob - minRob);
			
		} // end r for
		
		outInitial.write(getMin(frontSizeEachRun)+"\t"+ getMin(frontMinPEachRun)+"\t"+ getMin(frontAvgPEachRun)+ "\t"+ getMin(frontMaxPEachRun)+ "\t"+  getMin(frontRangePEachRun)+ "\t"+
				getMin(frontMinREachRun)+"\t"+ getMin(frontAvgREachRun)+ "\t"+ getMin(frontMaxREachRun)+ "\t"+  getMin(frontRangeREachRun)
				+ System.getProperty( "line.separator"));

		outInitial.write(getAvg(frontSizeEachRun)+"\t"+ getAvg(frontMinPEachRun)+"\t"+ getAvg(frontAvgPEachRun)+ "\t"+ getAvg(frontMaxPEachRun)+ "\t"+  getAvg(frontRangePEachRun)+ "\t"+
				getAvg(frontMinREachRun)+"\t"+ getAvg(frontAvgREachRun)+ "\t"+ getAvg(frontMaxREachRun)+ "\t"+  getAvg(frontRangeREachRun)
				+ System.getProperty( "line.separator"));

		outInitial.write(getMax(frontSizeEachRun)+"\t"+ getMax(frontMinPEachRun)+"\t"+ getMax(frontAvgPEachRun)+ "\t"+ getMax(frontMaxPEachRun)+ "\t"+  getMax(frontRangePEachRun)+ "\t"+
				getMax(frontMinREachRun)+"\t"+ getMax(frontAvgREachRun)+ "\t"+ getMax(frontMaxREachRun)+ "\t"+  getMax(frontRangeREachRun)
				+ System.getProperty( "line.separator"));
		
		outInitial.write(getMedian(frontSizeEachRun)+"\t"+ getMedian(frontMinPEachRun)+"\t"+ getMedian(frontAvgPEachRun)+ "\t"+ getMedian(frontMaxPEachRun)+ "\t"+  getMedian(frontRangePEachRun) +"\t"+
				getMedian(frontMinREachRun)+"\t"+ getMedian(frontAvgREachRun)+ "\t"+ getMedian(frontMaxREachRun)+ "\t"+  getMedian(frontRangeREachRun)
				+ System.getProperty( "line.separator"));
				
	}


	private static void printRank1Individuals() {
		ArrayList<double[]> tempList;
		System.out.println("PRINT INITIAL RANK 1 INDIVIDUALS:");
		for (int r=0; r< runs; r++){
			tempList= runInitRank1Solutions.get(r);
			System.out.println("Run:"+ r+ "(P&R)");
			for (double[] t: tempList)
				System.out.println(t[0]+"\t"+t[1]);
		} // end r for
		
		System.out.println("PRINT FINAL RANK 1 INDIVIDUALS:");
		for (int r=0; r< runs; r++){
			tempList= runFinalRank1Solutions.get(r);
			System.out.println("Run:"+ r+ "(P&R)");
			for (double[] t: tempList)
				System.out.println(t[0]+"\t"+t[1]);
		} // end r for
	
		
	}


	private static double getMax(ArrayList<double[]> tempList, int i) {
		if (tempList.size()== 0)
			return 5000;
		
		double max= tempList.get(0)[i];
		for (double[] d: tempList)
			if (d[i]> max)
				max= d[i];
		return max;
	}


	private static double getAvg(ArrayList<double[]> tempList, int i) {
		if (tempList.size()== 0)
			return 5000;
		double avg= 0;
		for (double[] d: tempList)
			avg+= d[i];
		
		avg= avg / tempList.size();
	
		BigDecimal bd= new BigDecimal(String.valueOf(avg));
		bd= bd.setScale(2, BigDecimal.ROUND_UP);
		return bd.doubleValue();
	}


	private static double getMin(ArrayList<double[]> tempList, int i) {
		if (tempList.size()== 0)
			return 5000;
		double min= tempList.get(0)[i];
		for (double[] d: tempList)
			if (d[i]< min)
				min= d[i];
		return min;
	}


	private static void modifyRunRank1Solutions() {
		ArrayList<double[]> originalList;
		ArrayList<double[]> newList;
		double[] tnew;
		for (int r=0; r< runs; r++){
			newList= new ArrayList<double[]>();
			originalList= runInitRank1Solutions.get(r);
			for (double[] t: originalList){
				if (t[1]>= 0){
					tnew= new double[2];
					tnew[0]= t[0]; tnew[1]=t[1];
					newList.add(tnew);
				}
			} // end for
			runInitRank1Solutions.set(r, newList);	
		} // end r for
		
		
		for (int r=0; r< runs; r++){
			newList= new ArrayList<double[]>();
			originalList= runFinalRank1Solutions.get(r);
			for (double[] t: originalList){
				if (t[1]>= 0){
					tnew= new double[2];
					tnew[0]= t[0]; tnew[1]=t[1];
					newList.add(tnew);
				}
			} // end for
			runFinalRank1Solutions.set(r, newList);	
		} // end r for

	}


	private static void getRank1Solutions() {
		runInitRank1Solutions= new ArrayList<ArrayList<double[]>>();
		runFinalRank1Solutions= new ArrayList<ArrayList<double[]>>();
		
		double[] tempP; double[] tempR; double[] tempPR;
				
		// get initial rank 1 solutions:
		for (int r=0; r< runs; r++){
			// for initial solution:
			tempP= runInitPenalties.get(r);
			tempR= runInitRobValues.get(r);
			
			ArrayList<double[]> solutionPRInitial= new ArrayList<double[]>();
			for (int i=0; i< pop_size; i++){
				tempPR= new double[2];
				tempPR[0]= tempP[i];
				
				BigDecimal bd= new BigDecimal(String.valueOf(tempR[i]));
				bd= bd.setScale(2, BigDecimal.ROUND_UP);
				tempR[i]= bd.doubleValue();
				
				tempPR[1]= tempR[i];
				solutionPRInitial.add(tempPR);
			} // end i for

			// find front individuals:
			int[] ranks= new int[solutionPRInitial.size()];

			// step1: Compute ranks:
			// Assign each individual a rank.
			// Each individual has a penalty and a robustness value. Compare these values...
			int dCount; // number of solutions dominating the current solution
			double p1; double r1;
			double p2; double r2;
			for (int i= 0; i< solutionPRInitial.size(); i++){
				dCount= 0; // number of solutions dominating i
				p1= solutionPRInitial.get(i)[0];
				r1= solutionPRInitial.get(i)[1];

				// find the number of individuals dominating this individual:
				for (int j= 0; j< solutionPRInitial.size(); j++){
					if (i==j) continue;
					p2= solutionPRInitial.get(j)[0];
					r2= solutionPRInitial.get(j)[1];
					
					if (p2<=p1 && r2<=r1){
						if (p2==p1 && r2==r1)
							continue; // Both values are equal. They may be the same individuals
						// at least one objective should be smaller
						else if (p2<p1 || r2<r1)
							dCount+= 1; // jth solution dominates ith solution
					} // end if
				} // end j for
				ranks[i]= dCount + 1;
			} // end i for

			// step2: now choose only the individuals with rank 1:
			double[] temp;
			ArrayList<double[]> rank1Solutions= new ArrayList<double[]>();
			for (int i=0; i< solutionPRInitial.size(); i++){
				if (ranks[i]== 1){
					temp= new double[2];
					temp[0]= solutionPRInitial.get(i)[0];
					temp[1]= solutionPRInitial.get(i)[1];
					rank1Solutions.add(temp);
				} // end if
			} // end i for

			runInitRank1Solutions.add(rank1Solutions);
		} // end r for
		
		double maxInitP= getMaxInitP(runInitRank1Solutions); // This will limit the final rank 1 solutions
		
		// get final rank 1 solutions:
		for (int r=0; r< runs; r++){
			// for initial solution:
			tempP= runFinalPenalties.get(r);
			tempR= runFinalRobValues.get(r);
			
			ArrayList<double[]> solutionPRInitial= new ArrayList<double[]>();
			for (int i=0; i< pop_size; i++){
				tempPR= new double[2];
				tempPR[0]= tempP[i];
				
				BigDecimal bd= new BigDecimal(String.valueOf(tempR[i]));
				bd= bd.setScale(2, BigDecimal.ROUND_UP);
				tempR[i]= bd.doubleValue();
				
				tempPR[1]= tempR[i];
//				if (tempPR[1] < 0)
//					continue;
				if (tempPR[0] > maxInitP)
					continue;
				solutionPRInitial.add(tempPR);
			} // end i for

			// find front individuals:
			int[] ranks= new int[solutionPRInitial.size()];

			// step1: Compute ranks:
			// Assign each individual a rank.
			// Each individual has a penalty and a robustness value. Compare these values...
			int dCount; // number of solutions dominating the current solution
			double p1; double r1;
			double p2; double r2;
			for (int i= 0; i< solutionPRInitial.size(); i++){
				dCount= 0; // number of solutions dominating i
				p1= solutionPRInitial.get(i)[0];
				r1= solutionPRInitial.get(i)[1];

				// find the number of individuals dominating this individual:
				for (int j= 0; j< solutionPRInitial.size(); j++){
					if (i==j) continue;
					p2= solutionPRInitial.get(j)[0];
					r2= solutionPRInitial.get(j)[1];
					
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

			// step2: now choose only the individuals with rank 1:
			double[] temp;
			ArrayList<double[]> rank1Solutions= new ArrayList<double[]>();
			for (int i=0; i< solutionPRInitial.size(); i++){
				if (ranks[i]== 1){
					temp= new double[2];
					temp[0]= solutionPRInitial.get(i)[0];
					temp[1]= solutionPRInitial.get(i)[1];
					rank1Solutions.add(temp);
				} // end if
			} // end i for

			runFinalRank1Solutions.add(rank1Solutions);
		} // end r for
	
	}


	private static double getMaxInitP(ArrayList<ArrayList<double[]>> runInitRank1Solutions2) {
		ArrayList<double[]> tempList= runInitRank1Solutions2.get(0);
		double worstP= tempList.get(0)[0];
		
		for (int r=0; r< runs; r++){
			tempList= runInitRank1Solutions2.get(r);
			for (double[] sol: tempList)
				if (sol[0]> worstP)
					worstP= sol[0];
		} // end r for
		
		return worstP;
	}


	private static double computeMaxPenalty() {
		// get initial max P among rank 1 individuals:

		double worstP= 0;
		
		ArrayList<double[]> tempList= runInitRank1Solutions.get(0);
		for (int r=0; r< runs; r++){
			tempList= runInitRank1Solutions.get(r);
			if (tempList.size()==0) continue;
			for (double[] sol: tempList)
				if (sol[0]> worstP)
					worstP= sol[0];
		} // end r for
		
		tempList= runFinalRank1Solutions.get(0);
		for (int r=0; r< runs; r++){
			tempList= runFinalRank1Solutions.get(r);
			for (double[] sol: tempList)
				if (sol[0]> worstP)
					worstP= sol[0];
		} // end r for
		
		return worstP;
	}

	private static double computeMinPenalty() {
		// get initial max P among rank 1 individuals:

		double bestP= 100000;
		
		ArrayList<double[]> tempList= runInitRank1Solutions.get(0);
		for (int r=0; r< runs; r++){
			tempList= runInitRank1Solutions.get(r);
			if (tempList.size()==0) continue;
			for (double[] sol: tempList)
				if (sol[0]< bestP)
					bestP= sol[0];
		} // end r for
		
		tempList= runFinalRank1Solutions.get(0);
		for (int r=0; r< runs; r++){
			tempList= runFinalRank1Solutions.get(r);
			for (double[] sol: tempList)
				if (sol[0]< bestP)
					bestP= sol[0];
		} // end r for
		
		return bestP;
	}
	
	private static double computeMaxRobustness() {
		// get initial max R among rank 1 individuals:
		double worstR= 0;
		ArrayList<double[]> tempList= runInitRank1Solutions.get(0);
		for (int r=0; r< runs; r++){
			tempList= runInitRank1Solutions.get(r);
			if (tempList.size()==0) continue;
			for (double[] sol: tempList)
				if (sol[1]> worstR)
					worstR= sol[1];
		} // end r for
		
		for (int r=0; r< runs; r++){
			tempList= runFinalRank1Solutions.get(r);
			for (double[] sol: tempList)
				if (sol[1]> worstR)
					worstR= sol[1];
		} // end r for
		
		BigDecimal bd= new BigDecimal(String.valueOf(worstR));
		bd= bd.setScale(2, BigDecimal.ROUND_UP);
		return bd.doubleValue();
	}

	private static double computeMinRobustness() {
		// get initial max R among rank 1 individuals:
		double bestR= 1000;
		ArrayList<double[]> tempList= runInitRank1Solutions.get(0);
		for (int r=0; r< runs; r++){
			tempList= runInitRank1Solutions.get(r);
			if (tempList.size()==0) continue;
			for (double[] sol: tempList)
				if (sol[1]< bestR)
					bestR= sol[1];
		} // end r for
		
		for (int r=0; r< runs; r++){
			tempList= runFinalRank1Solutions.get(r);
			for (double[] sol: tempList)
				if (sol[1]< bestR)
					bestR= sol[1];
		} // end r for
		
		BigDecimal bd= new BigDecimal(String.valueOf(bestR));
		bd= bd.setScale(2, BigDecimal.ROUND_UP);
		return bd.doubleValue();
	} 

	private static double getMin(ArrayList<Double> values) {
		if (values.size()== 0)
			return 5000;
		double min= values.get(0);
		for (double d: values)
			if (d< min)
				min= d;
		
		BigDecimal bd= new BigDecimal(String.valueOf(min));
		bd= bd.setScale(2, BigDecimal.ROUND_UP);
		return bd.doubleValue();
	}
	
	private static double getAvg(ArrayList<Double> values) {
		if (values.size()== 0)
			return 5000;
		
		double total= 0;
		double avg= 0;
		for (double d: values)
			total+= d;
		
		avg= (total/ values.size());
		
		BigDecimal bd= new BigDecimal(String.valueOf(avg));
		bd= bd.setScale(2, BigDecimal.ROUND_UP);
		return bd.doubleValue();
	}
	
	private static double getMax(ArrayList<Double> values) {
		if (values.size()== 0)
			return 5000;
		
		double max= values.get(0);
		for (double d: values)
			if (d> max)
				max= d;
		
		BigDecimal bd= new BigDecimal(String.valueOf(max));
		bd= bd.setScale(2, BigDecimal.ROUND_UP);
		return bd.doubleValue();
	}
	
	private static double getMedian(ArrayList<Double> values) {
		if (values.size()== 0)
			return 5000;
		double median;
		// find the value in the middle:
		ArrayList<Double> sorted= new ArrayList<Double>();
		for (double d: values)
			sorted.add(d);
		// sort ascending:
		double temp;
		for (int i=0; i< sorted.size();i++)
			for (int j=0; j< sorted.size(); j++ )
				if (sorted.get(j)< sorted.get(i)){
					temp= sorted.get(i);
					sorted.set(i,sorted.get(j));
					sorted.set(j,temp);
				}
		if (sorted.size()%2 == 0){
			int middle= sorted.size()/2;
			median= (sorted.get(middle)+ sorted.get(middle-1)) / 2;
		}
		else{
			int middle= sorted.size()/2;
			median= (sorted.get(middle));
		}	
		
		BigDecimal bd= new BigDecimal(String.valueOf(median));
		bd= bd.setScale(2, BigDecimal.ROUND_UP);
		return bd.doubleValue();	
	}
	

	private static void getHeuristicRobustness() throws IOException {
		runInitHeuristicRobValues=  new ArrayList<double[]>(); // for each run, 40 robustness values
		runFinalHeuristicRobValues= new ArrayList<double[]>(); // for each run, 40 robustness values
		
		File fInput1= new File(fileHeader+ 	"/ITTC2Simplified/OutputFiles/popRobustnessValues.txt");
		
		BufferedReader br = new BufferedReader(new FileReader(fInput1));
		String[] temp;
		String line;
		double[] robValues;
		int iterCounter= 1;
		
		while ((line = br.readLine()) != null) {
		   temp= line.split("[\\D&&[^.-]]+"); // split("[^0-9.-]+")
		   if (temp.length== pop_size){ // 40 individuals
			   if (iterCounter % iterations== 1){
				   robValues= new double[pop_size];
				   for (int i=0; i< pop_size; i++){
					   robValues[i]= Double.parseDouble(temp[i]);
				   } // end i for
				   runInitHeuristicRobValues.add(robValues); 
			   } // end if
			   if (iterCounter % iterations == 0){
				   robValues= new double[pop_size];
				   for (int i=0; i< pop_size; i++){
					   robValues[i]= Double.parseDouble(temp[i]);
				   } // end i for
				   runFinalHeuristicRobValues.add(robValues); 
			   } // end if
			   iterCounter++;
		   } // end if
		} // end line loop
		br.close();
		
		System.out.println("Heuristic Robustness Values:");
		double[] tt;
		for (int i= 0; i< runs; i++){
			tt= runInitHeuristicRobValues.get(i);
			System.out.print("Run "+ i+ "\t");
			for (int t=0; t< pop_size; t++)
				System.out.print(tt[t]+"\t");
			System.out.println();
		} // end i for
		
		
		for (int i= 0; i< runs; i++){
			tt= runFinalHeuristicRobValues.get(i);
			System.out.print("Run "+ i+ "\t");
			for (int t=0; t< pop_size; t++)
				System.out.print(tt[t]+"\t");
			System.out.println();
		} // end i for
		
		br.close();
		
	}
	
	private static void getRealRobustness() throws IOException {
		// only initial and final values have been recorded
		
		runInitRobValues=  new ArrayList<double[]>(); // for each run, 40 robustness values
		runFinalRobValues= new ArrayList<double[]>(); // for each run, 40 robustness values
		
		File fInput1= new File(fileHeader+ 	"/ITTC2Simplified/OutputFiles/popSecondRobustnessValues.txt");
		
		BufferedReader br = new BufferedReader(new FileReader(fInput1));
		String[] temp;
		String line;
		double[] robValues;
		int iterCounter= 1;
		boolean initRead= false;
		
		while ((line = br.readLine()) != null) {
		temp= line.split("[\\s]+"); // split("[^0-9.-]+")
		   if (temp.length== pop_size){ // 40 individuals
			   if (iterCounter % 2== 1){
				   robValues= new double[pop_size];
				   for (int i=0; i< pop_size; i++){
					   robValues[i]= Double.parseDouble(temp[i]);
				   } // end i for
				   runInitRobValues.add(robValues); 
				   initRead= true;
			   } // end if
			   else {
				   robValues= new double[pop_size];
				   for (int i=0; i< pop_size; i++){
					   robValues[i]= Double.parseDouble(temp[i]);
				   } // end i for
				   runFinalRobValues.add(robValues); 
			   } // end if
			   iterCounter++;
		   } // end if
		} // end line loop
		br.close();
		
		System.out.println("Real Robustness Values:");
		double[] tt;
		for (int i= 0; i< runs; i++){
			tt= runInitRobValues.get(i);
			System.out.print("Run "+ i+ "\t");
			for (int t=0; t< pop_size; t++)
				System.out.print(tt[t]+"\t");
			System.out.println();
		} // end i for
		
		
		for (int i= 0; i< runs; i++){
			tt= runFinalRobValues.get(i);
			System.out.print("Run "+ i+ "\t");
			for (int t=0; t< pop_size; t++)
				System.out.print(tt[t]+"\t");
			System.out.println();
		} // end i for
		
		br.close();
		
	}

	private static void getPenalties() throws IOException {
		runInitPenalties=  new ArrayList<double[]>(); // for each run, 40 penalty values
		runFinalPenalties= new ArrayList<double[]>(); // for each run, 40 penalty values
		
		File fInput1= new File(fileHeader+ 	"/ITTC2Simplified/OutputFiles/popPenaltyValues.txt");
	
		BufferedReader br = new BufferedReader(new FileReader(fInput1));
		String[] temp;
		String line;
		double[] penalties;
		int iterCounter= 1;
		
		while ((line = br.readLine()) != null) {
		   temp= line.split("[\\D]+");
		   if (temp.length== pop_size){ // 40 individuals
			   if (iterCounter % iterations== 1){
				   penalties= new double[pop_size];
				   for (int i=0; i< pop_size; i++){
					   penalties[i]= Double.parseDouble(temp[i]);
				   } // end i for
				   runInitPenalties.add(penalties); 
			   } // end if
			   if (iterCounter % iterations == 0){
				   penalties= new double[pop_size];
				   for (int i=0; i< pop_size; i++){
					   penalties[i]= Double.parseDouble(temp[i]);
				   } // end i for
				   runFinalPenalties.add(penalties); 
				   iterCounter= 0;
			   } // end if
			   iterCounter++;
		   } // end if
		} // end line loop
		br.close();
		
		
		System.out.println("Penalty Values:");
		double[] tt;
		for (int i= 0; i< runs; i++){
			tt= runInitPenalties.get(i);
			System.out.print("Run "+ i+ "\t");
			for (int t=0; t< pop_size; t++)
				System.out.print(tt[t]+"\t");
			System.out.println();
		} // end i for
		
		
		for (int i= 0; i< runs; i++){
			tt= runFinalPenalties.get(i);
			System.out.print("Run "+ i+ "\t");
			for (int t=0; t< pop_size; t++)
				System.out.print(tt[t]+"\t");
			System.out.println();
		} // end i for
		
		br.close();
		
	}

	
	
	
	
	
//	
//	private static void printRunParetoFront() throws IOException {
//		File fInput1= new File(fileHeader+ 	"/ITTC2Simplified/OutputFiles/ParetoFront.txt");
//		
//		Writer output = null;
//		File fileOut = new File("../InstanceAllRuns/AllRunParetoResults.txt");
//		
//		try {
//			output=  new BufferedWriter(new FileWriter(fileOut, true )); // true: append mode.
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	
//		BufferedReader br = new BufferedReader(new FileReader(fInput1));
//		double indID; double rank; double crowdDist; double P; double R;
//		String[] temp;
//		String line;
//		int run = 0;
//		
//		BigDecimal bd;
//		
//		int popSize= 40;
//		
//		while ((line = br.readLine()) != null) {
//		   temp= line.split(" ");
//		   if (temp[0].compareTo("Run")==0){
//			   for (int i=0; i< popSize; i++ ){
//				   line = br.readLine();
//				   temp= line.split("\\s+");
//				   indID= Double.parseDouble(temp[0].trim());
//				   rank= Double.parseDouble(temp[1].trim());
////				   bd= new BigDecimal(Double.parseDouble(temp[2].trim()));
////				   bd= bd.setScale(3, BigDecimal.ROUND_UP);
////				   
////				   crowdDist= bd.doubleValue();
//				   
//				   crowdDist= Double.parseDouble(temp[2].trim());
//				
//				   P= Double.parseDouble(temp[3].trim());
//				   R= Double.parseDouble(temp[4].trim());
//				   
//				   output.write("Instance: "+ instNo+ "\tRun: "+ run+ "\tind: "+indID+ "\trank: "+rank+"\tcrowdDist: "+crowdDist+"\tP: "+P+"\tR: "+R+ System.getProperty( "line.separator"));
//				   System.out.println("Run: "+ run+ "\tind: "+indID+ "\trank: "+rank+"\tcrowdDist: "+crowdDist+"\tP: "+P+"\tR: "+R);
//
//			   } // end i for
//			   run++;
//		   } // end if
//		} // end line loop
//		br.close();
//		
//		output.close();
//		
//	}
//
//

}
