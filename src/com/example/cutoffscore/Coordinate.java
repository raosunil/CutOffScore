package com.example.cutoffscore;

import com.google.gson.Gson;

public class Coordinate {

    // private variables
	
	//input
	
	private int id;
	
    
    
    // variables
   private double xValue;
   
   private double yValue;

    public Coordinate() {
    }

    // constructor
    public Coordinate(int id, double xValue, double yValue
    		) {
	this.id = id;
	this.xValue = xValue;
	this.yValue = yValue;
    }

   
   

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public double getxValue() {
		return xValue;
	}

	public void setxValue(double xValue) {
		this.xValue = xValue;
	}

	public double getyValue() {
		return yValue;
	}

	public void setyValue(double yValue) {
		this.yValue = yValue;
	}



	
		
		@Override
		public String toString(){			
			return new Gson().toJson(this);
		}

}