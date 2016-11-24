package com.example.MosaicGenerator.Model;

public class MosaicModel {

	private int[][] red;// モザイク赤(1pixel)[縦][横]
	private int[][] green;// モザイク緑(1pixel)[縦][横]
	private int[][] blue;// モザイク青(1pixel)[縦][横]
	private int[][] mozaicred;// モザイク指定コマ用[縦][横]
	private int[][] mozaicgreen;// モザイク指定コマ用[縦][横]
	private int[][] mozaicblue;// モザイク指定コマ用[縦][横]


	private int[][] h;// モザイク赤(1pixel)[縦][横]
	private int[][] s;// モザイク緑(1pixel)[縦][横]
	private int[][] v;// モザイク青(1pixel)[縦][横]
	private int[][] mozaich;// モザイク指定コマ用[縦][横]
	private int[][] mozaics;// モザイク指定コマ用[縦][横]
	private int[][] mozaicv;// モザイク指定コマ用[縦][横]



	public MosaicModel(){
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



	public int[][] getMozaicred() {
		return mozaicred;
	}



	public void setMozaicred(int[][] mozaicred) {
		this.mozaicred = mozaicred;
	}



	public int[][] getMozaicgreen() {
		return mozaicgreen;
	}



	public void setMozaicgreen(int[][] mozaicgreen) {
		this.mozaicgreen = mozaicgreen;
	}



	public int[][] getMozaicblue() {
		return mozaicblue;
	}



	public void setMozaicblue(int[][] mozaicblue) {
		this.mozaicblue = mozaicblue;
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



	public int[][] getMozaich() {
		return mozaich;
	}



	public void setMozaich(int[][] mozaich) {
		this.mozaich = mozaich;
	}



	public int[][] getMozaics() {
		return mozaics;
	}



	public void setMozaics(int[][] mozaics) {
		this.mozaics = mozaics;
	}



	public int[][] getMozaicv() {
		return mozaicv;
	}



	public void setMozaicv(int[][] mozaicv) {
		this.mozaicv = mozaicv;
	}


}

