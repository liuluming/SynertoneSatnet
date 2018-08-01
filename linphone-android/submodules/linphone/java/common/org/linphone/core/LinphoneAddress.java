/*
LinphoneAddress.java
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

import java.util.Vector;

/**
 * Object that represents a SIP address.
 * The LinphoneAddress is an opaque object to represents SIP addresses, ie the content of SIP's 'from' and 'to' headers. 
 * A SIP address is made of display name, username, domain name, port, and various uri headers (such as tags). 
 * It looks like 'Alice &lt;sip:alice@example.net&gt;'. The LinphoneAddress has methods to extract and manipulate all parts of the address. 
 * When some part of the address (for example the username) is empty, the accessor methods return null.
 * <br> Can be instanciated using both  {@link LinphoneCoreFactory#createLinphoneAddress(String, String, String)} or {@link LinphoneCoreFactory#createLinphoneAddress(String)} 
 * @author jehanmonnier
 *
 */
public interface LinphoneAddress {
	class TransportType {
		static private Vector<TransportType> values = new Vector<TransportType>();
		static public TransportType LinphoneTransportUdp = new TransportType(0, "LinphoneTransportUdp");       
		static public TransportType LinphoneTransportTcp = new TransportType(1, "LinphoneTransportTcp");
		static public TransportType LinphoneTransportTls = new TransportType(2, "LinphoneTransportTls");

		private final int mValue;
		private final String mStringValue;

		private TransportType(int value, String stringValue) {
			mValue = value;
			values.addElement(this);
			mStringValue = stringValue;
		}
		
		public static TransportType fromInt(int value) {
			for (int i = 0; i < values.size(); i++) {
				TransportType type = values.elementAt(i);
				if (type.mValue == value) return type;
			}
			throw new RuntimeException("state not found ["+value+"]");
		}
		
		public String toString() {
			return mStringValue;
		}
		
		public int toInt() {
			return mValue;
		}
	}

	/**
	 * Human display name
	 * @return null if not set
	 */
    String getDisplayName();
	/**
	 * userinfo 
	 * @return null if not set
	 */
    String getUserName();
	/**
	 * Domain name
	 * @return null if not set
	 */
    String getDomain();
	/**
	 * Port
	 * @return 0 if not set
	 */
    int getPort();
	/**
	 * set display name 
	 * @param name
	 */
    void setDisplayName(String name);
	/**
	 * set user name 
	 * @param username
	 */
    void setUserName(String username);
	/**
	 * set domain name 
	 * @param domain
	 */
    void setDomain(String domain);
	/**
	 * set port
	 * @param port, 0 if not set
	 */
    void setPort(int port);

	/**
	 * Removes address's tags and uri headers so that it is displayable to the user.
	**/
    void clean();
	
	/**
	 * 
	 * @return the address as a string.
	 */
    String asString();
	/**
	 * 
	 * @return the address without display name as a string.
	 */
    String asStringUriOnly();
	
	/**
	 * same as {@link #asString()}
	 * 
	 * */
    String toString();
	
	/**
	 * Gets the transport set in the address
	 * @return the transport
	 */
    TransportType getTransport();
	
	/**
	 * Sets the transport in the address
	 * @param transport the transport to set
	 */
    void setTransport(TransportType transport);
}
