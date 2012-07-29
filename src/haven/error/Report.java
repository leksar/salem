/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven.error;

import java.util.*;
import java.util.Map.Entry;

import org.apxeolog.salem.HConfig;
import org.apxeolog.salem.HConst;

public class Report implements java.io.Serializable {
	private boolean reported = false;
	public final Throwable t;
	public final long time;
	public final Map<String, Object> props = new HashMap<String, Object>();

	public Report(Throwable t) {
		this.t = t;
		time = System.currentTimeMillis();
		Runtime rt = Runtime.getRuntime();
		props.put("mem.free", rt.freeMemory());
		props.put("mem.total", rt.totalMemory());
		props.put("mem.max", rt.maxMemory());
	}

	synchronized void join() throws InterruptedException {
		while (!reported)
			wait();
	}

	synchronized void done() {
		reported = true;
		notifyAll();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("REVISION=");
		builder.append(HConst.REVISION_NUMBER);
		builder.append("&BUILDDATE=");
		builder.append(HConst.BUILD_DATE);
		//builder.append("\n");
		builder.append("&TIME=");
		builder.append(time);
		//builder.append("\n");
		builder.append("&PROPS=");
		//builder.append("\n");
		for (Entry<String, Object> prop : props.entrySet()) {
			builder.append(prop.getKey());
			builder.append("=");
			builder.append(prop.getValue());
			builder.append(";");
		}
		builder.append("&EXCEPTION=");
		//builder.append("\n");
		builder.append(t.toString());
		//builder.append("\n");
		builder.append("&STACK TRACE=");
		//builder.append("\n");
		java.io.StringWriter w = new java.io.StringWriter();
		t.printStackTrace(new java.io.PrintWriter(w));
		builder.append(w.toString());
		return builder.toString();
	}
}
