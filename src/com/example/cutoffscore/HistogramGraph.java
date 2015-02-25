package com.example.cutoffscore;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.CubicLineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.ScatterChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

public class HistogramGraph {
	
	public Intent getIntent(Context context){
		MainActivity activity = (MainActivity)context;
		//TODO
		XYSeries series = new XYSeries("Car speed near school");
		ArrayList<Double> listOfXValues = activity.getDbHandler().Get_Unique_XValues();
		Log.d("HistogramGraph","Inside  getIntent : val of listOfXValues "+ listOfXValues);
		for (double val : listOfXValues){
			series.add(val, activity.getDbHandler().Get_Coordinates_GroupedByXValue(val));
		}
		
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		renderer.setColor(Color.CYAN);
		renderer.setDisplayChartValues(true);
		renderer.setChartValuesSpacing(0.5f);
		renderer.setFillPoints(true);
		
		XYSeries series2 = new XYSeries("Normal Distribution");
		
		
		DescriptiveStatistics ds = activity.getDs();
		NormalDistribution norm = new NormalDistributionImpl(ds.getMean(),ds.getStandardDeviation());
		XYSeries series3 = null;
		if (activity.getDs()!=null){
			try{
			
			series2.add(norm.inverseCumulativeProbability((1 - 0.997)/2), 100 * norm.density(norm.inverseCumulativeProbability((1 - 0.997)/2)));
			
			series2.add(norm.inverseCumulativeProbability((1 - 0.95)/2),100 *  norm.density(norm.inverseCumulativeProbability((1 - 0.95)/2)));
			
			series2.add(norm.inverseCumulativeProbability((1 - 0.90)/2),100 *  norm.density(norm.inverseCumulativeProbability((1 - 0.90)/2)));
			
			series2.add(norm.inverseCumulativeProbability((1 - 0.68)/2),100 *  norm.density(norm.inverseCumulativeProbability((1 - 0.68)/2)));
			
			series2.add(norm.inverseCumulativeProbability(0.50), 100 * norm.density(norm.inverseCumulativeProbability(0.50)));
			series2.add(norm.inverseCumulativeProbability(0.68 + 0.16), 100 * norm.density(norm.inverseCumulativeProbability(0.68 + 0.16)));
			
			//series2.add(norm.inverseCumulativeProbability(0.90), 100 * norm.density(norm.inverseCumulativeProbability(0.90)));
			series2.add(norm.inverseCumulativeProbability(0.975),100 *  norm.density(norm.inverseCumulativeProbability(0.975)));
			series2.add(norm.inverseCumulativeProbability(0.997), 100 * norm.density(norm.inverseCumulativeProbability(0.997)));
			//series2.add(norm.inverseCumulativeProbability(activity.getCutoffPercentage()), 100 * norm.density(norm.inverseCumulativeProbability(activity.getCutoffPercentage())));
			double cutoffval = norm.inverseCumulativeProbability(activity.getCutoffPercentage());
			DecimalFormat myFormatter = new DecimalFormat("#####.###");
			series3 = new XYSeries("CutOff Value: " +myFormatter.format(cutoffval));
			series3.add(cutoffval, 100 * norm.density(cutoffval));
			
			Log.d("HistogramGraph","Inside getIntent : values of normal graph are :" + norm.inverseCumulativeProbability(1 - 0.997) + " , " +norm.density(norm.inverseCumulativeProbability(1 - 0.997)));
			Log.d("HistogramGraph","Inside getIntent : values of normal graph are :" + norm.inverseCumulativeProbability(1- 0.95)+ " , " +norm.density(norm.inverseCumulativeProbability(1 - 0.95)));
			Log.d("HistogramGraph","Inside getIntent : values of normal graph are :" + norm.inverseCumulativeProbability(1 - 0.90)+ " , " +norm.density(norm.inverseCumulativeProbability(1 - 0.90)));
			Log.d("HistogramGraph","Inside getIntent : values of normal graph are :" + norm.inverseCumulativeProbability(1 - 0.68) + " , " +norm.density(norm.inverseCumulativeProbability(1 - 0.68)));
			Log.d("HistogramGraph","Inside getIntent : values of normal graph are :" + norm.inverseCumulativeProbability(0.50) + " , " +norm.density(norm.inverseCumulativeProbability(0.5)));
			Log.d("HistogramGraph","Inside getIntent : values of normal graph are :" + norm.inverseCumulativeProbability(0.68)+ " , " +norm.density(norm.inverseCumulativeProbability(0.68)));
			Log.d("HistogramGraph","Inside getIntent : values of normal graph are :" + norm.inverseCumulativeProbability(0.90)+ " , " +norm.density(norm.inverseCumulativeProbability(0.90)));
			Log.d("HistogramGraph","Inside getIntent : values of normal graph are :" + norm.inverseCumulativeProbability(0.95)+ " , " +norm.density(norm.inverseCumulativeProbability(0.95)));
			Log.d("HistogramGraph","Inside getIntent : values of normal graph are :" + norm.inverseCumulativeProbability(0.997)+ " , " +norm.density(norm.inverseCumulativeProbability(0.997)));
			Log.d("HistogramGraph","Inside getIntent : values of normal graph are :" + norm.inverseCumulativeProbability(activity.getCutoffPercentage())+ " , " +norm.density(norm.inverseCumulativeProbability(activity.getCutoffPercentage())));
			}catch (Exception e){
				Log.d("HistogramGraph","Exception in getIntent"+e);
			}		
			
		}
		
		XYSeriesRenderer renderer2 = new XYSeriesRenderer();
		renderer2.setColor(Color.GREEN);
		renderer2.setDisplayChartValues(true);		
		renderer2.setFillPoints(true);
		
		XYSeriesRenderer renderer3 = new XYSeriesRenderer();
		renderer3.setColor(Color.RED);
		renderer3.setDisplayChartValues(true);
		renderer3.setFillPoints(true);
		renderer3.setHighlighted(true);
		renderer3.setPointStyle(PointStyle.CIRCLE);	
		
	    
	  
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series);
		dataset.addSeries(series2);
		dataset.addSeries(series3);
		
		XYMultipleSeriesRenderer mrenderer = new XYMultipleSeriesRenderer();
		mrenderer.setBarSpacing(0.5);
		mrenderer.addSeriesRenderer(renderer);
		mrenderer.addSeriesRenderer(renderer2);
		mrenderer.addSeriesRenderer(renderer3);
		mrenderer.setXTitle("Speed");
		mrenderer.setYTitle("Count");
		mrenderer.setZoomButtonsVisible(true);
		
		
		 String[] types = new String[] { BarChart.TYPE, CubicLineChart.TYPE, ScatterChart.TYPE};
		
		Intent intent = ChartFactory.getCombinedXYChartIntent(context, dataset, mrenderer,types,"Car Speed near School" );
				
		return intent;
	}

}
