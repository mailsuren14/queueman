/**
 *     This file is part of QueueMan.
 *
 *        QueueMan is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    any later version.
 *
 *    QueueMan is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with QueueMan.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
 package edwardawebb.queueman.handlers;

import java.util.ArrayList;
import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import edwardawebb.queueman.classes.Disc;
import edwardawebb.queueman.classes.NetFlix;
import edwardawebb.queueman.classes.NetFlixQueue;

/*
 * I enjoy quiet evenings after being called by the factory, and long walks through XML
 */
public class RecommendationsHandler extends DefaultHandler {

	private NetFlix netflix;
	
	protected Disc tempMovie;

	private boolean inResultsTotal = false;
	private boolean inResultsPerPage = false;
	private boolean inId = false;
	private boolean inMessage = false;
	private boolean inRating = false;
	private boolean inPosition = false;
	private boolean inBoxArt = false;
	private boolean inSynopsis = false;
	private boolean inFormats = false;
	protected boolean inAvailability = false;
	protected boolean inCategory = false;
	private boolean inYear = false;
	private boolean inItem = false;
	private boolean inStatus = false;
	private boolean inSubCode = false;

	// temp variables
	protected int position;
	private String stitle;
	private String ftitle;
	private String synopsis;
	private String mpaaRating="";
	private String id;
	private String message;
	private int totalResults;
	
	private String uniqueID;
	private String boxArtUrl;
	private String year;
	private double rating;
	protected int statusCode = 0;
	private int subCode = 0;
	private boolean isAvailable = false;
	private ArrayList<String> mformats=new ArrayList<String>();

	// element names (set by sub classes)
	protected String itemElementName="recommendation";

	private Date availableFrom;

	private Date availableUntil;

	private String availability;
	private String discAvailabilityCategoryScheme = "http://api.netflix.com/categories/queue_availability";
	private String discMpaaRatingScheme = "http://api.netflix.com/categories/mpaa_ratings";
	private String discTvRatingScheme = "http://api.netflix.com/categories/tv_ratings";

	private boolean isInstant;

	public RecommendationsHandler(NetFlix netflix){
		this.netflix=netflix;
	}
	public void endDocument() {
		// Log.d("AddDiscQueueHandler","Reading results XML")
		netflix.recomemendedQueue.setTotalTitles(totalResults);
	}

	public void startElement(String uri, String name, String qName,
			Attributes atts) {
		String element = name.trim();
		if (element.equals("category")) {
			inCategory = true;
			if (atts.getValue("scheme").equals(discAvailabilityCategoryScheme)) {
				availability = atts.getValue("term");
				if (availability.equals("saved")) {
					isAvailable = false;
				} else {
					isAvailable = true;
				}
			}else if (atts.getValue("scheme").equals(discMpaaRatingScheme)
					|| atts.getValue("scheme").equals(discTvRatingScheme)) {
				mpaaRating = atts.getValue("label");				
			}
		} else if(element.equals("link") && atts.getValue("title").equals("synopsis")){
			//very poor way, but only way i could find to compare discs ascross queus.
			String href=atts.getValue("href");
			uniqueID=(String) href.subSequence(0,href.lastIndexOf("/") )   ;
		} else if (element.equals("availability")) {
			inAvailability = true;
			/*
			 * if(!isAvailable){ //if not available, find out when it was / is
			 * //availableFrom = new Date(atts.getValue("available_from"));
			 * //availableUntil = new Date(atts.getValue("available_until"));
			 * 
			 * }
			 */
		} else if (element.equals("delivery_formats")) {
			inFormats = true;
		} else if (element.equals("synopsis")) {
			inSynopsis = true;
		} else if (element.equals("id")) {
			inId = true;
		} else if (element.equals("release_year")) {
			inYear = true;
		} else if (element.equals("predicted_rating")) {
			inRating = true;
		} else if (element.equals("title")) {			
			stitle = atts.getValue("short");
			ftitle = atts.getValue("regular");
		} else if (element.equals("box_art")) {
			inBoxArt = true;
			boxArtUrl = atts.getValue("small");
		} else if (element.equals(itemElementName)) {
			inItem = true;
		} else if (element.equals("number_of_results")) {
			inResultsTotal = true;
		} else if (element.equals("results_per_page")) {
			inResultsPerPage = true;
		} else if (name.equals("status_code")) {
			inStatus = true;
		} else if (name.equals("sub_code")) {
			inSubCode = true;
		}
		
		if(inAvailability && inCategory){
			//
			mformats.add(atts.getValue("label"));
			if(atts.getValue("label").equals(NetFlixQueue.INSTANT_LABEL)) isInstant=true;
		}
		
	}

	public void endElement(String uri, String name, String qName)
			throws SAXException {
		String element = name.trim();
		if (element.equals("category")) {
			inCategory = false;
		} else if (element.equals("availability")) {
			inAvailability = false;
		} else if (element.equals("delivery_formats")) {
			inFormats = false;
		}  else if (element.equals("synopsis")) {
			inSynopsis = false;
		} else if (element.equals("id")) {
			inId = false;
		} else if (element.equals("release_year")) {
			inYear = false;
		} else if (element.equals("predicted_rating")) {
			inRating = false;
		}else if (element.equals("box_art")) {
			inBoxArt = false;
		} else if (element.equals(itemElementName)) {
			inItem = false;
			tempMovie = new Disc(id,uniqueID, stitle, ftitle, boxArtUrl, rating,
					synopsis, year, isAvailable);
			tempMovie.setAvailibilityText(availability);
			tempMovie.setFormats(new ArrayList<String>(mformats));
			tempMovie.setAvailableInstant(new Boolean(isInstant));
			tempMovie.setQueueType(NetFlixQueue.QUEUE_TYPE_RECOMMEND);
			tempMovie.setMpaaRating(new String(mpaaRating));
			mpaaRating="";
			mformats.clear();
			isInstant=false;
			if(!netflix.discQueue.getDiscs().contains(tempMovie)){
				//no pioitn in showing a title they already got.
				
				NetFlix.recomemendedQueue.add(tempMovie);
			}
			this.mformats.clear();
		} else if (element.equals("number_of_results")) {
			inResultsTotal = false;
		} else if (element.equals("results_per_page")) {
			inResultsPerPage = false;
		} else if (name.equals("status_code")) {
			inStatus = false;
		} else if (name.equals("sub_code")) {
			inSubCode = false;
		}
		// Log.d("QueueHandler","<<<endELement:" + element);

	}

	public void characters(char ch[], int start, int length) {
		// Log.d("QueueHandler",">>>characters:" );

		String chars = (new String(ch).substring(start, start + length));

		if (inId) {
			id = chars;
			// Log.d("QueueHandler","Id: " + id);
		} else if (inMessage) {
			message = chars;
		} else if (inResultsTotal) {
			totalResults = Integer.valueOf(chars);
		}  else if (inRating) {
			rating = Double.valueOf(chars);
		} else if (inSynopsis) {
			synopsis = (chars);
		} else if (inYear) {
			year = chars;
		} else if (inStatus) {
			statusCode = Integer.valueOf(chars);
		} else if (inSubCode) {
			subCode = Integer.valueOf(chars);

		}
		// Log.d("QueueHandler","<<<characters:" );

	}

	/**
	 * Get netflix api subcode
	 * 
	 * @return statusCode if subCode not set
	 */
	public int getSubCode() {
		if (this.subCode != 0) {
			return this.subCode;
		} else {
			return this.statusCode;
		}
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	public void setDiscAvailabilityCategoryScheme(
			String discAvailabilityCategoryScheme) {
		this.discAvailabilityCategoryScheme = discAvailabilityCategoryScheme;
	}
	
	public String getMessage() {
		return message;
	}


}
