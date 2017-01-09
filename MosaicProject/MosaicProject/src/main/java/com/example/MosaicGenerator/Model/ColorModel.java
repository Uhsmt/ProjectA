package com.example.MosaicGenerator.Model;

public class ColorModel {

	private int[][] red;// モザイク赤(1pixel)[縦][横]
	private int[][] green;// モザイク緑(1pixel)[縦][横]
	private int[][] blue;// モザイク青(1pixel)[縦][横]
	private int[][] h;// モザイク赤(1pixel)[縦][横]
	private int[][] s;// モザイク緑(1pixel)[縦][横]
	private int[][] v;// モザイク青(1pixel)[縦][横]

	public ColorModel(){
		//default
	}

	public int[][] getRed() {
		return red;
	}

	public void setRed(int[][] red) {
		this.red = red;
	}

	public int[][] getGreen() {
		return green;
	}

	public void setGreen(int[][] green) {
		this.green = green;
	}

	public int[][] getBlue() {
		return blue;
	}

	public void setBlue(int[][] blue) {
		this.blue = blue;
	}

	public int[][] getH() {
		return h;
	}

	public void setH(int[][] h) {
		this.h = h;
	}

	public int[][] getS() {
		return s;
	}

	public void setS(int[][] s) {
		this.s = s;
	}

	public int[][] getV() {
		return v;
	}

	public void setV(int[][] v) {
		this.v = v;
	}
	public int argb(int a,int r,int g,int b){
		    return a<<24 | r <<16 | g <<8 | b;
	}

}

