/*
LinPhoneCallLog.java
Copyright (C) 2010  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package org.linphone.core;
/**
 * Call data records object
 *
 */
import java.util.Vector;


/**
 * Object representing a call log.
 *
 *
**/
public interface LinphoneCallLog {
	/**
	 * Represents call status
	 *
	 */
    class CallStatus {

		static private Vector<CallStatus> values = new Vector<CallStatus>();
		private final int mValue;
		private final String mStringValue;
		/**
		 * Call success.
		 */
		public final static CallStatus Success = new CallStatus(0,"Success");
		/**
		 * Call aborted.
		 */
		public final static CallStatus Aborted = new CallStatus(1,"Aborted");
		/**
		 * missed incoming call.
		 */
		public final static CallStatus Missed = new CallStatus(2,"Missed");
		/**
		 * remote call declined.
		 */
		public final static CallStatus Declined = new CallStatus(3,"Declined");

		/**
		 * The call was aborted before being advertised to the application - for protocol reasons
		 */
		public final static CallStatus EarlyAborted = new CallStatus(4,"Early Aborted");

		/**
		 * The call was answered on another device
		 */
		public final static CallStatus AcceptedElsewhere = new CallStatus(5,"Accepted Elsewhere");

		/**
		 * The call was declined on another device
		 */
		public final static CallStatus DeclinedElsewhere = new CallStatus(6,"Declined Elsewhere");


		private CallStatus(int value,String stringValue) {
			mValue = value;
			values.addElement(this);
			mStringValue=stringValue;
		}
		public static CallStatus fromInt(int value) {

			for (int i=0; i<values.size();i++) {
				CallStatus state = values.elementAt(i);
				if (state.mValue == value) return state;
			}
			throw new RuntimeException("CallStatus not found ["+value+"]");
		}
		public String toString() {
			return mStringValue;
		}
		public int toInt() {
			return mValue;
		}
	}

	/**
	 * Originator of the call as a LinphoneAddress object.
	 * @return LinphoneAddress
	 */
    LinphoneAddress getFrom();
	/**
	 * Destination of the call as a LinphoneAddress object.
	 * @return
	 */
    LinphoneAddress getTo();
	/**
	 * The direction of the call
	 * @return CallDirection
	 */
    CallDirection getDirection();
	/**
	 * get status of this call
	 * @return CallStatus
	 */
    CallStatus getStatus();

	/**
	 * A human readable String with the start date/time of the call
	 * @return String
	 */
    String getStartDate();

	/**
	 * A  timestamp of the start date/time of the call in milliseconds since January 1st 1970
	 * @return  long
	 */
    long getTimestamp();

	/**
	 * The call duration, in seconds
	 * @return int
	 */
    int getCallDuration();
	/**
	 *  Call id from signaling
	 * @return the SIP call-id.
	 */
    String getCallId();
	/**
	 * Tells whether the call was a call to a conference server
	 * @return true if the call was a call to a conference server
	 */
    boolean wasConference();
}
