package com.eye.visualization;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.la4j.LinearAlgebra;
import org.la4j.inversion.MatrixInverter;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;

import android.os.Environment;
import android.util.Log;


public class HRV {

	public static ArrayList<Double> kernel = new ArrayList<Double>();
	public static ArrayList<Double> kernel2 = new ArrayList<Double>();

	private HRV() {
		kernel.add(-0.002235312068283);
		kernel.add(-0.00288916764534007);
		kernel.add(-0.000403639016343188);
		kernel.add(-0.000899401799401541);
		kernel.add(-0.0104008777042711);
		kernel.add(-0.0153713562262457);
		kernel.add(-0.00370331315243428);
		kernel.add(-0.00468614142890943);
		kernel.add(-0.0391084191328681);
		kernel.add(-0.0502022967524287);
		kernel.add(-0.00229744122791406);
		kernel.add(0.00336077172655702);
		kernel.add(-0.0997139727559987);
		kernel.add(-0.131125833084103);
		kernel.add(0.0986868761868759);
		kernel.add(0.400454929433710);
		kernel.add(0.400454929433710);
		kernel.add(0.0986868761868759);
		kernel.add(-0.131125833084103);
		kernel.add(-0.0997139727559987);
		kernel.add(0.00336077172655702);
		kernel.add(-0.00229744122791406);
		kernel.add(-0.0502022967524287);
		kernel.add(-0.0391084191328681);
		kernel.add(-0.00468614142890943);
		kernel.add(-0.00370331315243428);
		kernel.add(-0.0153713562262457);
		kernel.add(-0.0104008777042711);
		kernel.add(-0.000899401799401541);
		kernel.add(-0.000403639016343188);
		kernel.add(-0.00288916764534007);
		kernel.add(-0.002235312068283);

		kernel2.add(-0.00321263379562721);
		kernel2.add(-0.003764598959188);
		kernel2.add(-0.00483869399602229);
		kernel2.add(-0.00605626562632100);
		kernel2.add(-0.00669781036035200);
		kernel2.add(-0.00580375268516567);
		kernel2.add(-0.00235519093592408);
		kernel2.add(0.00449855894869551);
		kernel2.add(0.0152125109384457);
		kernel2.add(0.0296694294244274);
		kernel2.add(0.0470976022575351);
		kernel2.add(0.0661090791698004);
		kernel2.add(0.0848605035229295);
		kernel2.add(0.101313105284754);
		kernel2.add(0.113546827799631);
		kernel2.add(0.120070731815926);
		kernel2.add(0.120070731815926);
		kernel2.add(0.113546827799631);
		kernel2.add(0.101313105284754);
		kernel2.add(0.0848605035229295);
		kernel2.add(0.0661090791698004);
		kernel2.add(0.0470976022575351);
		kernel2.add(0.0296694294244274);
		kernel2.add(0.0152125109384457);
		kernel2.add(0.00449855894869551);
		kernel2.add(-0.00235519093592408);
		kernel2.add(-0.00580375268516567);
		kernel2.add(-0.00669781036035200);
		kernel2.add(-0.00605626562632100);
		kernel2.add(-0.00483869399602229);
		kernel2.add(-0.003764598959188);
		kernel2.add(-0.00321263379562721);
	}

	// ******************* singleton pattern *****************************
	private static HRV instance = new HRV();

	public static HRV getInstance() {
		return instance;
	}

	// ******************* get HRV **************************************
	public ArrayList<Double> getHRV(ArrayList<Double> ecg_orig) {
		Log.e("L", kernel.size() + "");
		Log.e("L2", kernel2.size() + "");
		Log.e("ecg_orig", ecg_orig.size() + "");
		try{
			return findHRV(optimize(findRealsignMaxima(filtfilt(ecg_orig, kernel),
					findFilterMaxima(filtfilt(findPeak2Peak(filtfilt(ecg_orig, kernel)),
							kernel2))), ecg_orig));
		}
		catch(Exception e){}
		return null;
	}

	// ******************* find HRV *************************************
	private ArrayList<Double> findHRV(ArrayList<Double> realsignMaxima) {
		ArrayList<Double> hrv = new ArrayList<Double>();
		for (int i = 1; i < realsignMaxima.size(); i++) {
			if((realsignMaxima.get(i) - realsignMaxima.get(i - 1))/30>0&&(realsignMaxima.get(i) - realsignMaxima.get(i - 1))/30<3);
				hrv.add((realsignMaxima.get(i) - realsignMaxima.get(i - 1))/30);
		}
		return hrv;
	}
	
	public static double getpNN50(ArrayList<Double> hrv){
		int NN50 = 0;
		for(int i = 0; i < hrv.size()-1; i++)
		{
			if(Math.abs(hrv.get(i+1)-hrv.get(i))>0.05)
				NN50++;
		}
		return 100*(double)NN50/(hrv.size()-1);
	}

	public ArrayList<Double> optimize(ArrayList<Integer> realsignMaxima, ArrayList<Double> ecg_orig ){
		try{
			ArrayList<Double> filtered = filtfilt(ecg_orig, kernel);
			ArrayList<Double> optimizedmaxima = new ArrayList<Double>();;
			ArrayList<Integer> minima = new ArrayList<Integer>();
			for(int i=0; i<realsignMaxima.size(); i++){
				if(i==0){
					int minpos = 0;;
					for(int j=1; j<realsignMaxima.get(0); j++){
						if((filtered.get(j+1)-filtered.get(j))>0 && 
								(filtered.get(j+1)-filtered.get(j))/(filtered.get(j)-filtered.get(j-1))<0){
							minpos = j;
						}
					}
					minima.add(minpos);
				}
				else{
					int minpos = 0;;
					for(int j=realsignMaxima.get(i-1); j<realsignMaxima.get(i); j++){
						if((filtered.get(j+1)-filtered.get(j))>0 && 
								(filtered.get(j+1)-filtered.get(j))/(filtered.get(j)-filtered.get(j-1))<0){
							minpos = j;
						}
					}
					minima.add(minpos);
				}
			}
			for(int i=0; i<realsignMaxima.size(); i++){
				if(i ==realsignMaxima.size() - 1){
					int minpos = 0;;
					for(int j=minima.get(i); j<realsignMaxima.size() - 1; j++){
						if((filtered.get(j+1)-filtered.get(j))>0 && 
								(filtered.get(j+1)-filtered.get(j))/(filtered.get(j)-filtered.get(j-1))<0){
							minpos = j;
						}
					}
					minima.add(minpos);
				}
				else{
					ArrayList<Double> temp = new ArrayList<Double>();
					double oppos = 0;
					for(int j=minima.get(i); j<minima.get(i+1); j++){
						temp.add(filtered.get(j));
					}
					oppos = Poly.optimize(temp);
					oppos = oppos + minima.get(i);
					optimizedmaxima.add(oppos);
				}
			}

			return optimizedmaxima;
		}
		catch(Exception e){
		}
		return null;
	}


	// ******************** find peak2peak ******************************
	public ArrayList<Double> findPeak2Peak(ArrayList<Double> filtered) {

		ArrayList<Double> peak2peak = new ArrayList<Double>();
		for(int i = 0; i < 5; i++){
			peak2peak.add(0.0);
		}
		for (int j = 5; j < filtered.size() - 5; j++) {
			Double minimum = 0.0;
			Double maximum = 0.0;

			for (int i = j - 5; i <= j + 5; i++) { // find maximum
				if (i == j - 5)
					maximum = filtered.get(i) > filtered.get(i + 1) ? filtered
							.get(i) : filtered.get(i + 1);
							else
								maximum = maximum > filtered.get(i) ? maximum : filtered
										.get(i);
			}

			for (int i = j - 5; i <= j + 5; i++) { // find minimum
				if (i == j - 5)
					minimum = filtered.get(i) < filtered.get(i + 1) ? filtered
							.get(i) : filtered.get(i + 1);
							else
								minimum = minimum < filtered.get(i) ? minimum : filtered
										.get(i);
			}

			peak2peak.add(maximum - minimum);
		}
		return peak2peak;
	}

	// ******************** find Filtered Maxima ************************
	public ArrayList<Integer> findFilterMaxima(ArrayList<Double> filtered) {
		ArrayList<Integer> maxima = new ArrayList<Integer>();
		for (int i = 3; i < filtered.size() - 4; i++) { // range is +-three,
			// because of
			// startNum=0, i=3
			if (filtered.get(i - 3) < filtered.get(i - 2)
					&& filtered.get(i - 2) < filtered.get(i - 1)
					&& filtered.get(i - 1) < filtered.get(i)
					&& filtered.get(i + 1) < filtered.get(i)
					&& filtered.get(i + 2) < filtered.get(i + 1)
					&& filtered.get(i + 3) < filtered.get(i + 2)) {
				maxima.add(i);
			}
		}
		return maxima;
	}

	// ******************** find Filtered
	public ArrayList<Integer> findFilterMinimum(ArrayList<Double> filtered) {
		ArrayList<Integer> min = new ArrayList<Integer>();
		for (int i = 3; i < filtered.size() - 4; i++) {
			if (filtered.get(i - 3) < filtered.get(i - 2)
					&& filtered.get(i - 2) > filtered.get(i - 1)
					&& filtered.get(i - 1) > filtered.get(i)
					&& filtered.get(i + 1) > filtered.get(i)
					&& filtered.get(i + 2) > filtered.get(i + 1)
					&& filtered.get(i + 3) > filtered.get(i + 2)) {
				min.add(i);
			}
		}
		return min;
	}

	// ******************** find realsignal Maxima with FilterMaxima *****
	public ArrayList<Integer> findRealsignMaxima(ArrayList<Double> realsignal,
			ArrayList<Integer> filteredMaxima) {

		ArrayList<Integer> maxima = new ArrayList<Integer>();
		int win = 5; // window is five
		for (int j = 0; j < filteredMaxima.size(); j++) {
			int temp = 0;
			for (int i = filteredMaxima.get(j) - win > 0 ? filteredMaxima
					.get(j) - win : 0; i <= filteredMaxima.get(j) + win; i++) {
				if (i == filteredMaxima.get(j) - win || i == 0)
					temp = realsignal.get(i) > realsignal.get(i + 1) ? i
							: i + 1;
					else
						temp = realsignal.get(temp) > realsignal.get(i) ? temp : i;

			}
			maxima.add(temp);
		}
		return maxima;
	}

	// *********************** find convolution ***************************
	public ArrayList<Double> conv(ArrayList<Double> signal,
			ArrayList<Double> kernel) {
		ArrayList<Double> result = new ArrayList<Double>();
		ArrayList<Double> sign = new ArrayList<Double>();
		for (int i = 0; i < signal.size() + kernel.size(); i++) {
			result.add(0.0);
			if (i < kernel.size()) {
				sign.add(0.0);
			} else {
				sign.add(signal.get(i - kernel.size()));
			}
		}
		for (int i = 0; i < sign.size(); i++) {
			result.set(i, 0.0);
			for (int j = 0; j < kernel.size(); j++) {
				if (i - j >= 0)
					result.set(i,
							result.get(i) + sign.get(i - j) * kernel.get(j));
				else {
					break;
				}
			}
		}
		for (int i = 0; i <= kernel.size(); i++) {
			result.remove(0.0);
		}
		result.remove(result.size() - 1);
		return result;
	}

	// ************************** get kernel2 *****************************
	public ArrayList<Double> getKernel2() {
		return kernel2;
	}

	static public void saveToFile(String filename, ArrayList<Double> arr, int k) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.size(); i++) {
			sb.append(arr.get(i) + " ");
		}
		try { // file
			File root = new File(Environment.getExternalStorageDirectory(), "Notes");

			if (!root.exists()) {
				root.mkdirs();
			}
			File gpxfile = new File(root, filename + ".txt");
			Log.e("filename", filename + ".txt");
			FileWriter writer = new FileWriter(gpxfile);
			writer.append(sb.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("??", "??");
		}
	}

	static public void saveToFile(String filename, ArrayList<Integer> arr) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.size(); i++) {
			sb.append(arr.get(i) + " ");
		}

		try {
			File root = new File(Environment.getExternalStorageDirectory(),"Notes");
			if (!root.exists()) {
				root.mkdirs();
			}
			File gpxfile = new File(root, filename + ".txt");
			FileWriter writer = new FileWriter(gpxfile);
			writer.append(sb.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	static public void saveToFile(String filename, ArrayList<Integer> arr, boolean isInt) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.size(); i++) {
			sb.append(arr.get(i) + " ");
		}
		try { // file
			File root = new File(Environment.getExternalStorageDirectory(),
					"Notes");
			if (!root.exists()) {
				root.mkdirs();
			}
			File gpxfile = new File(root, filename + ".txt");
			Log.e("filename", filename + ".txt");
			FileWriter writer = new FileWriter(gpxfile);
			writer.append(sb.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("??", "??");
		}
	}
	//******************filtfilt***************
	public static ArrayList<Double> filtfilt(ArrayList<Double> signal1, ArrayList<Double> kernel1){
		double[] s = new double[signal1.size()];
		double[] ba = new double[kernel1.size()];
		double[] aa = new double[kernel1.size()];
		double[] z = new double[kernel1.size()-1];

		for(int  i = 0; i < s.length; i++){
			s[i] = signal1.get(i); 
		}
		for(int  i = 0; i < ba.length; i++){
			ba[i] = kernel1.get(i); 
		}
		Vector signalv = new BasicVector(s);
		Vector bv = new BasicVector(ba);
		Vector av = new BasicVector(ba.length);
		for(int i = 0; i < ba.length; i++){
			if(i == 0){
				av.set(i, 1);
				aa[i] = 1;
			}
			else{
				av.set(i, 0);
				aa[i] = 0;
			}
		}
		Matrix signal = signalv.toColumnMatrix();
		Matrix b = bv.toColumnMatrix();
		Matrix a = av.toColumnMatrix();
		int nop = s.length;
		Matrix zii = new Basic2DMatrix(31, 31);
		Matrix rr = new Basic2DMatrix(31, 1);
		Matrix zi = new Basic2DMatrix(31, 1);
		for(int i = 0; i < ba.length - 1; i++){
			for(int j = 0; j < ba.length - 1; j++){
				if(i == j)
					zii.set(i, j, 1);
				else if(i == j - 1)
					zii.set(i, j, -1);
			}
		}
		for(int i = 0; i < ba.length - 1; i++){
			rr.set(i, 0, b.get(i + 1, 0) - b.get(0, 0)*a.get(i + 1, 0));
		}

		MatrixInverter inverter = zii.withInverter(LinearAlgebra.GAUSS_JORDAN);
		zi = inverter.inverse().multiply(rr);
		for(int i = 0; i < kernel1.size()-1; i++){
			z[i] = zi.get(i, 0);
		}
		int nfact = 3*(ba.length - 1);
		Matrix sigex = new Basic2DMatrix(s.length + nfact*2, 1);

		for(int i = nfact; i>0; i--){
			sigex.set(nfact - i, 0, 2*signal.get(0, 0) - signal.get(i, 0));
		}
		for(int i = 0; i<s.length; i++){
			sigex.set(nfact+i, 0, signal.get(i, 0));
		}
		for(int i = s.length - 2; i>s.length - nfact - 2; i--){
			sigex.set(2*s.length + nfact - 2 - i, 0, 2*signal.get(s.length - 1, 0) - signal.get(i, 0));
		}

		double[] y = new double[signal1.size()+2*nfact];
		double[] y1 = new double[signal1.size()+2*nfact];
		double[] y2 = new double[signal1.size()+2*nfact];
		double[] y3 = new double[signal1.size()+2*nfact];
		for(int i = 0; i < y.length; i++)
			y[i] = sigex.get(i, 0);
		ArrayList<Double> filtered = new ArrayList<Double>(); //aa (a), ba (b), z (zi), y (sigex)

		y1 = filter(ba, aa, z, y);
		for(int i = 0; i<y.length; i++){
			y2[i] = y1[y.length-i-1];
		}
		y3 = filter(ba, aa, z, y2);

		for(int i = y3.length - nfact - 1; i > nfact - 1; i--)
			filtered.add(y3[i]);	
		return filtered;

	}
	//**************** filter ************************
	public static double[] filter(double b[], double a[], double z[], double x[]){
		double[] y = new double[x.length];
		double[] zc = new double[z.length + 1];
		for(int i = 0; i < z.length; i++)
			zc[i] = z[i]*x[0];
		for(int i = 0; i < y.length; i++)
			y[i] = 0;
		int n = a.length;
		zc[zc.length-1] = 0;
		for(int i = 0; i < x.length; i++){
			y[i] = b[0]*x[i] + zc[0];
			for(int j = 1; j < n; j++){
				zc[j - 1] = b[j]*x[i] + zc[j] - a[j]*y[i];
			}
		}
		return y;
	}
}