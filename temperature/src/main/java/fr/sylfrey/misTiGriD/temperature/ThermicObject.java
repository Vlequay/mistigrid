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
package fr.sylfrey.misTiGriD.temperature;

import fr.sylfrey.misTiGriD.environment.Namable;

/**
 * A thermic object has a temperature and exchanges heat 
 * with its neighbours through Walls.
 * @author syl
 *
 */
public interface ThermicObject extends Namable {
	
	/**
	 * @return the current temperature of this ThermiObject, in °C.
	 */
    public float getCurrentTemperature();
    
}
