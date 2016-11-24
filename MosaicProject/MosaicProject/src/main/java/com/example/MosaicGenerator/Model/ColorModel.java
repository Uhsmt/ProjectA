package com.example.MosaicGenerator.Model;

public class ColorModel {

//	String x_address;
//	String y_address;
	private int Red;
	private int Green;
	private int Blue;

	private int Hvalue;
	private int Svalue;
	private int Vvalue;

	private int height = 510;
	private int width = 800;
	private int minpix = 10;


	public ColorModel(){
		//default
	}

	public ColorModel(int H,int S,int V){
		this.Hvalue = H;
		this.Svalue = S;
		this.Vvalue = V;

	}


	public int getRed() {
		return Red;
	}


	public void setRed(int red) {
		Red = red;
	}


	public int getGreen() {
		return Green;
	}


	public void setGreen(int green) {
		Green = green;
	}


	public int getBlue() {
		return Blue;
	}


	public void setBlue(int blue) {
		Blue = blue;
	}


	public int getHvalue() {
		return Hvalue;
	}


	public void setHvalue(int hvalue) {
		Hvalue = hvalue;
	}


	public int getSvalue() {
		return Svalue;
	}


	public void setSvalue(int svalue) {
		Svalue = svalue;
	}


	public int getVvalue() {
		return Vvalue;
	}


	public void setVvalue(int vvalue) {
		Vvalue = vvalue;
	}


	public int getHeight() {
		return height;
	}


	public void setHeight(int height) {
		this.height = height;
	}


	public int getWidth() {
		return width;
	}


	public void setWidth(int width) {
		this.width = width;
	}


	public int getMinpix() {
		return minpix;
	}


	public void setMinpix(int minpix) {
		this.minpix = minpix;
	}


}
