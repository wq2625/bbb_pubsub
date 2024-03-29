/**
* BigBlueButton open source conferencing system - http://www.bigbluebutton.org/
* 
* Copyright (c) 2012 BigBlueButton Inc. and by respective authors (see below).
*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License as published by the Free Software
* Foundation; either version 3.0 of the License, or (at your option) any later
* version.
* 
* BigBlueButton is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License along
* with BigBlueButton; if not, see <http://www.gnu.org/licenses/>.
*
*/
package org.bigbluebutton.core.service.recorder;

import java.util.HashMap;

/**
 * Abstract class for all events that need to be recorded.
 * @author Richard Alam
 *
 */
public abstract class RecordEvent {
	protected final HashMap<String, String> eventMap = new HashMap<String, String>();
	
	protected final static String MODULE = "module";
	protected final static String TIMESTAMP = "timestamp";
	protected final static String MEETING = "meetingId";
	protected final static String EVENT = "eventName";
	
	public final String getMeetingID() {
		return eventMap.get(MEETING);
	}

	/**
	 * Set the module that generated the event.
	 * @param module
	 */
	public final void setModule(String module) {
		eventMap.put(MODULE, module);
	}
	
	/**
	 * Set the timestamp of the event.
	 * @param timestamp
	 */
	public final void setTimestamp(long timestamp) {
		eventMap.put(TIMESTAMP, Long.toString(timestamp));
	}
	
	/**
	 * Set the meetingId for this particular event.
	 * @param meetingId
	 */
	public final void setMeetingId(String meetingId) {
		eventMap.put(MEETING, meetingId);
	}
	
	/**
	 * Set the name of the event.
	 * @param event
	 */
	public final void setEvent(String event) {
		eventMap.put(EVENT, event);
	}
	
		
	/**
	 * Convert the event into a Map to be recorded.
	 * @return
	 */
	public final HashMap<String, String> toMap() {
		return eventMap;
	}
	
	@Override
	public String toString() {
		return eventMap.get(MODULE) + " " + eventMap.get(EVENT);
	}
}
