package com.eye.visualization;

import java.util.ArrayList;

import org.la4j.LinearAlgebra;
import org.la4j.decomposition.MatrixDecompositor;
import org.la4j.inversion.MatrixInverter;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;


public class Poly {
	static double sum = 0;
	static int count = 0;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Vector wave = new BasicVector(new double[]{0.3917, 0.4563, 0.5095, 0.5267, 0.5161, 0.4990, 0.4847, 0.4714, 0.4595, 0.4540, 0.4564, 0.4621, 0.4654, 0.4644, 0.4607, 0.4563, 0.4514, 0.4457, 0.4404, 0.4369, 0.4329, 0.4240, 0.4117});
		Vector xfitting = new BasicVector(4*(wave.length()-1)+1);
		Vector yfitting = new BasicVector(4*(wave.length()-1)+1);
		Vector xdata = new BasicVector(wave.length());
		for(int i=0; i<xfitting.length(); i++){
			xfitting.set(i, (double)i/4);
			yfitting.set(i, 0);
		}
		for(int i=0; i<xdata.length(); i++){
			xdata.set(i, i);
		}
		int n = 10;
		Matrix ydata = new Basic2DMatrix(wave.length(), 1);
		Matrix coef = new Basic2DMatrix(n + 1, 1);
		ydata.setColumn(0, wave);
		Matrix vand = new Basic2DMatrix(wave.length(), 11);
		for(int i = 0; i<wave.length(); i++){
			vand.set(i, n, 1);
			for(int j = 0; j<n; j++)
				vand.set(i, j, Math.pow(xdata.get(i)+1,(double)(n-j)));
		}
		MatrixDecompositor decompositor = vand.withDecompositor(LinearAlgebra.QR);
		Matrix[] dec = decompositor.decompose();
		Matrix Q = dec[0]; Matrix R = dec[1];
		MatrixInverter inverter = R.withInverter(LinearAlgebra.GAUSS_JORDAN);
		Matrix B = inverter.inverse(LinearAlgebra.DENSE_FACTORY); 
		coef = B.multiply(Q.transpose().multiply(ydata), LinearAlgebra.DENSE_FACTORY);  
		for(int i = 0; i < xfitting.length(); i++){
			for(int j = 0; j < n+1; j++)
				yfitting.set(i, yfitting.get(i) + (coef.get(j, 0)*Math.pow(xfitting.get(i), (n - j))));
		}
		double max = yfitting.get(0); int maxi = 0;
		for(int i = 1; i < xfitting.length(); i++){
			if(yfitting.get(i)>max){
				max = yfitting.get(i);
				maxi = i;
			}
		}
	}
	public static double optimize(ArrayList<Double> arwave){
		Vector wave = new BasicVector(arwave.size());
		for(int  i = 0; i < arwave.size(); i++){
			wave.set(i, (arwave.get(i)));
		}
		if(wave.length()>=11)
		{
			Vector xfitting = new BasicVector(4*(wave.length()-1)+1);
			Vector yfitting = new BasicVector(4*(wave.length()-1)+1);
			Vector xdata = new BasicVector(wave.length());
			for(int i=0; i<xfitting.length(); i++){
				xfitting.set(i, (double)i/4);
				yfitting.set(i, 0);
			}
			for(int i=0; i<xdata.length(); i++){
				xdata.set(i, i);
			}
			int n = 10;
			Matrix ydata = new Basic2DMatrix(wave.length(), 1);
			Matrix coef = new Basic2DMatrix(n + 1, 1);
			ydata.setColumn(0, wave);
			Matrix vand = new Basic2DMatrix(wave.length(), 11);
			for(int i = 0; i<wave.length(); i++){
				vand.set(i, n, 1);
				for(int j = 0; j<n; j++)
					vand.set(i, j, Math.pow(xdata.get(i)+1,(double)(n-j)));
			}
			MatrixDecompositor decompositor = vand.withDecompositor(LinearAlgebra.QR);
			Matrix[] dec = decompositor.decompose();
			Matrix Q = dec[0]; Matrix R = dec[1];
			MatrixInverter inverter = R.withInverter(LinearAlgebra.GAUSS_JORDAN);
			Matrix B = inverter.inverse(LinearAlgebra.DENSE_FACTORY); 
			coef = B.multiply(Q.transpose().multiply(ydata), LinearAlgebra.DENSE_FACTORY);
			for(int i = 0; i < xfitting.length(); i++){
				for(int j = 0; j < n+1; j++)
					yfitting.set(i, yfitting.get(i) + (coef.get(j, 0)*Math.pow(xfitting.get(i), (n - j))));
			}
			int fp = 0;
			for(int i = 1; i < xfitting.length()-3; i++){
				if((yfitting.get(i+1)-yfitting.get(i))>0&&(yfitting.get(i+2)-yfitting.get(i+1))<0){
					fp = i+1;
					break;
				}
			}
			int sp = -1;
			for(int i = fp; i < yfitting.length()-3; i++){
				if((yfitting.get(i+1)-yfitting.get(i))>0&&(yfitting.get(i+2)-yfitting.get(i+1))<0){
					sp = i + 1;
					break;
				}
			}
			if(sp == -1){
				for(int i = fp; i < yfitting.length()-4; i++){
					if((yfitting.get(i+3)-2*yfitting.get(i+2)+yfitting.get(i+1))<0&&(yfitting.get(i+2)-2*yfitting.get(i+1)+yfitting.get(i))>0){
						sp = i + 2;
						break;
					}
				}
			}
			if(sp!=-1){
				sum +=  (double)(sp - fp)/120;
				count++;
//				Log.e("SI", (double)(sp - fp)/120+"");
			}
			return (double)fp/4;
		}
		else if(wave.length()!=0){
			double max = wave.get(0); int fp = 0;
			for(int i = 0; i < wave.length(); i++){
				if(wave.get(i)>max){
					max = wave.get(i);
					fp = i;
				}
			}
			return (double)fp/4;
		}
		return 0;
	}
	static int getAge(){
		double PWV = (6 - 3.27)*(11/6);
		return (int) (PWV - 2.4)*10;
	}

}
