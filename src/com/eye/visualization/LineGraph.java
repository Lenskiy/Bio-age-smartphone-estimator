package com.eye.visualization;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;

import com.eye.hrvanalysis.RGBActivity;

public class LineGraph {

	private GraphicalView view;

	private TimeSeries dataset = new TimeSeries("");
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

	private XYSeriesRenderer renderer = new XYSeriesRenderer();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

	/* init setting */
	public LineGraph(String title, String time) {
		mDataset.addSeries(dataset);
		renderer.setFillPoints(true);
		mRenderer.setClickEnabled(false); // click X
		mRenderer.setZoomEnabled(false, false); // ZOOM X
		mRenderer.setPanEnabled(false, false); // MOVE X

		/* */
		renderer.setPointStyle(PointStyle.POINT);
		renderer.setColor(Color.rgb(100, 100, 100));
		int[] arr = { 0, 0, 0, 0 };
		mRenderer.setMargins(arr);

		mRenderer.setShowLabels(false);
		mRenderer.setShowLegend(false);
		mRenderer.setShowAxes(false);
		mRenderer.setAntialiasing(false);
		renderer.setLineWidth((int) Math.sqrt(RGBActivity.wWidth
				* RGBActivity.wHeight) / 180);
		
		mRenderer.addSeriesRenderer(renderer);
	}

	public GraphicalView getView(Context context) {
		view = ChartFactory.getLineChartView(context, mDataset, mRenderer);
		return view;
	}


	public synchronized void addNewPoints(ArrayList<Double> p, boolean ok) { 
		if (ok) {
			renderer.setLineWidth((int) Math.sqrt(RGBActivity.wWidth
					* RGBActivity.wHeight) / 180);
			mRenderer.setXAxisMax(p.size() - 2);
		}

		for (int i = 30; i < p.size(); i++) {
			double d = p.get(i);
			dataset.add(i, (int) d);
		}

	}
}