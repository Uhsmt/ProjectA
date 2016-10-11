package com.example.ProjectA.Model;

public class ColorModel {

//	String x_address;
//	String y_address;
//	private int Red;
//	private int Green;
//	private int Blue;
	private int height = 510;
	private int width = 800;
	private int minpix = 10;

	public ColorModel(){
		//default
	}

//	public String getCell_name() {
//		return cell_name;
//	}
//
//	public String getX_address() {
//		return x_address;
//	}
//
//	public String getY_address() {
//		return y_address;
//	}
//
//	public int getRed() {
//		return Red;
//	}
//
//	public int getGreen() {
//		return Green;
//	}
//
//	public int getBlue() {
//		return Blue;
//	}
//
//	public void setCell_name(String cell_name) {
//		this.cell_name = cell_name;
//	}
//
//	public void setX_address(String x_address) {
//		this.x_address = x_address;
//	}
//
//	public void setY_address(String y_address) {
//		this.y_address = y_address;
//	}
//
//	public void setRed(int red) {
//		Red = red;
//	}
//
//	public void setGreen(int green) {
//		Green = green;
//	}
//
//	public void setBlue(int blue) {
//		Blue = blue;
//	}

	public int getheight(){
		return height;
	}

	public int getwidth(){
		return width;
	}

	public int getminpix(){
		return minpix;
	}

	public void setheight(int height){
		this.height = height;
	}

	public void setwidth(int width){
		this.width = width;
	}

	public void setminpix(int minpix){
		this.minpix = minpix;
	}

}
