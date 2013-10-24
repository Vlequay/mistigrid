/*******************************************************************************
 * Copyright (c) 2013 Sylvain Frey.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Sylvain Frey - initial API and implementation
 ******************************************************************************/
package fr.sylfrey.misTiGriD.trace.framework.data;

import java.io.Serializable;

import akka.actor.ActorPath;

public class PermissionRequest<Action> extends Message implements Serializable {

	public final Action action;
	
	public PermissionRequest(ActorPath author, Action action) {
		super(author);
		this.action = action;
	}
	
	@Override
	public String toString() {
		return "PermissionRequest [source=" + source 
				+ ", action=" + action.toString() + "]";
	}
	
	private static final long serialVersionUID = 1L;
	
}
