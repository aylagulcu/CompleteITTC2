import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HVI {

	public List<double[]> points;

	@SuppressWarnings("unused")
	public void getAllPoints(String fileName) throws NumberFormatException, IOException {
		// reads the file and adds all numeric data to the points array
		
		this.points= new ArrayList<double[]>();
		
		File fInput1= new File(fileName);
	
		BufferedReader br = new BufferedReader(new FileReader(fInput1));
		int instance; int run; int indID; double rank; double crowdDist; double P; double R;
		String[] temp;
		String line;
		
		double[] paretoArray;

		int indCount= 40;
		
		while ((line = br.readLine()) != null) {
			// [Instance:, 1, Run:, 0, ind:, 0.0, rank:, 1.0, crowdDist:, 1000.0, P:, 1719.0, R:, 24.26]
			// 0	1	2	3		4		5	6			7		8	9		10	11
			temp= line.split("\\s+");

			instance= (int) Double.parseDouble(temp[1].trim());
			run= (int) Double.parseDouble(temp[3].trim());
			indID= (int) Double.parseDouble(temp[5].trim());
			rank= Double.parseDouble(temp[7].trim());
			crowdDist= Double.parseDouble(temp[9].trim());
			P= Double.parseDouble(temp[11].trim());
			R= Double.parseDouble(temp[13].trim());

			if (P>= 500)
				continue;
			
			paretoArray= new double[7];
			paretoArray[0]= instance;
			paretoArray[1]= run;
			paretoArray[2]= indID;
			paretoArray[3]= rank;
			paretoArray[4]= crowdDist;
			paretoArray[5]= P;
			paretoArray[6]= R;
			this.points.add(paretoArray);
			
			System.out.println(	"Instance: "+ instance+ "Run: "+run+ "  ind: "+ indID+ "  penalty: "+ P+ "  robustness: "+ R); 

		} // end line loop
		br.close();

	}

	

}
