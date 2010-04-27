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



import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edwardawebb.queueman.queues.MutableQueue;
import edwardawebb.queueman.queues.Queue;
/*
 * I enjoy quiet evenings after being called by the factory, and long walks through XML
 */
public class MoveQueueHandler extends DefaultHandler {
	private String eTag;
	
	private boolean inETag=false;
	private boolean inStatus=false;
	private boolean inMessage=false;
	
	private int oldPosition;
	private boolean inPosition=false;
	private int position;
	protected int statusCode=0;
	private int subCode=0;
	private boolean inSubCode;
	private String message="";

	private MutableQueue queue;

	public MoveQueueHandler(MutableQueue queue,int oldPosition) {
		this.oldPosition=oldPosition;
		this.queue = queue;
	}
	
	public void startElement(String uri, String name, String qName,	Attributes atts) {
		if (name.trim().equals("status_code")){
			inStatus=true;
		}else if (name.trim().equals("etag")){
			inETag=true;
		}else if (name.trim().equals("position")){
			inPosition=true;
		}else if (name.equals("status_code")){
        	inStatus=true;
        }else if (name.equals("sub_code")){
        	inSubCode=true;
        }else if (name.equals("message")) {
			inMessage = true;
		}
	}

	//we pnly want to update the local q when downlaoing disc q.
	public void endElement(String uri, String name, String qName)throws SAXException {
		if(name.trim().equals("queue_item") && statusCode == 201){	
			//add additional format info and save movie to search q
			queue.reorder(oldPosition, position);
		}else if (name.trim().equals("status_code")){
			inStatus=false;
		}else if (name.trim().equals("etag")){
			inETag=false;
		}else if (name.trim().equals("position")){
			inPosition=false;
		}else if (name.equals("status_code")){
        	inStatus=false;
        }else if (name.equals("sub_code")){
        	inSubCode=false;
        }else if (name.equals("message")) {
			inMessage = false;
		}
	}

	public void characters(char ch[], int start, int length) {
		String chars = (new String(ch).substring(start, start + length));
		if(inETag){
			eTag=chars;
			queue.seteTag(eTag);
		}else if(inPosition){
			position=Integer.parseInt(chars);
		}else if(inStatus){
			statusCode=Integer.parseInt(chars);
		}else if(inSubCode){
			subCode=Integer.parseInt(chars);
		}else if (inMessage) {
			message= chars;
		}
	}
	/**
	 * Get netflix api subcode
	 * @param currentCode if not values set in XML
	 * @return statusCode if subCode not set
	 */
	public int getSubCode(int currentCode){
		if(this.subCode!=0){
			return this.subCode;
		}else if(this.statusCode != 0){
			return this.statusCode;
		}else{
			return currentCode;
		}
	}

	public int getStatusCode(){
		return this.statusCode;
	}


	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

}
