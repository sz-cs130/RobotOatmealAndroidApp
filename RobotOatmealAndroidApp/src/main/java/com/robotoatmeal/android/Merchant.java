package com.robotoatmeal.android;

public class Merchant implements Comparable<Merchant>
{
	public int id;
	public String name;
	
	public int compareTo(Merchant other)
	{
		return this.name.compareTo(other.name);
	}
}