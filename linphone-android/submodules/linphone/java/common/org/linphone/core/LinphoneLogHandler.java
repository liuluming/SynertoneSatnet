/*
LinphoneLogHandler.java
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
 * Interface to implement for handling liblinphone log.
 * <br> use {@link LinphoneCoreFactory#setLogHandler(LinphoneLogHandler)}
 *
 */
public interface LinphoneLogHandler {
	int Debug=1;
	int Trace=1<<1;
	int Info=1<<2;
	int Warn=1<<3;
	int Error=1<<4;
	int Fatal=1<<5;
	
	/**
	 * Method invoked for each traces
	 * @param loggerName
	 * @param level
	 * @param levelString
	 * @param msg
	 * @param e
	 */
    void log(String loggerName, int level, String levelString, String msg, Throwable e);
}
