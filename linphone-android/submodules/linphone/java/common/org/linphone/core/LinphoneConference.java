/*
LinphoneConference.java
Copyright (C) 2015  Belledonne Communications, Grenoble, France

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

import org.linphone.core.LinphoneAddress;
import java.util.List;

/**
 * Interface to manipulate a running conference
 */
public interface LinphoneConference {
	/**
	 * Get the URIs of all participants of the conference
	 */
    LinphoneAddress[] getParticipants();
	/**
	 * Remove a participant from the conference
	 * @param uri The URI of the participant to remove
	 * @return 0 if succeed, -1 if not.
	 */
    int removeParticipant(LinphoneAddress uri);
}
