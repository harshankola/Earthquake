/**
 * 
 */
package com.paad.earthquake;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.location.Location;

/**
 * @author Harrsh
 *
 */
public class Quake {

	private Date date;
	private String details;
	private Location location;
	private double magnitude;
	private String link;
	
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @return the magnitude
	 */
	public double getMagnitude() {
		return magnitude;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param date
	 * @param details
	 * @param location
	 * @param magnitude
	 * @param link
	 */
	public Quake(Date date, String details, Location location,
			double magnitude, String link) {
		this.date = date;
		this.details = details;
		this.location = location;
		this.magnitude = magnitude;
		this.link = link;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
		String dateString = sdf.format(date);
		return dateString + ": " + magnitude + " " + details;
	}

}
