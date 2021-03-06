/*******************************************************************************
 * Copyright (c) 2013 EDF. This software was developed with the 
 * collaboration of Télécom ParisTech (Dragutin Brezak, Sylvain Frey).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Dragutin Brezak, Sylvain Frey - initial API and implementation
 ******************************************************************************/
package fr.sylfrey.misTiGriD.arduino.impl;


import java.util.ArrayList;
import java.util.List;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Unbind;

import fr.sylfrey.misTiGriD.arduino.serialConnection.ArduinoSerialWriterPort1;
import fr.sylfrey.misTiGriD.temperature.Opening;
import fr.sylfrey.misTiGriD.temperature.ThermicObject;

/**
 * I removed that it implements Wall because we don't need walls anymore.
 * This class is currently not being used since doors are not opened automatically.
 * @author dragutin,sylvain
 *
 */
@Component(name="ArduinoOpening",immediate=true)
@Provides
public class ArduinoOpening implements Opening {

	@Override
	public void open() {
		(new Thread(new ArduinoSerialWriterPort1(openWindowCode))).start();
		isOpen = true;
	}

	@Override
	public void close() {
		(new Thread(new ArduinoSerialWriterPort1(closeWindowCode))).start();
		isOpen = false;
	}

	@Override
	public boolean isOpen() {
		return isOpen;
	}

	@Override
	public boolean isClosed() {
		return !isOpen;
	}
	
//	@Override
//	public float getHeatConductance() {
//		return isOpen ? openHeatConductance : surfacicHeatConductance*size;
//	}
	
//	@Override
//	public List<ThermicObject> getNeighbours() {
//		return thermicNeighbours;
//	}
	
	@Property(name="instance.name")
	public String name;
	
	@Property
	public float surfacicHeatConductance;
	
	@Property
	public boolean isOpen;
	
	@Property
	public float openHeatConductance;
	
	@Property
	public float size;
	
	@Property
	public int openWindowCode;
	
	@Property
	public int closeWindowCode;

	
	//TODO I may not need this thermicneighbours
	@Bind(id="thermicNeighbours",aggregate=true)
	public void bindNeighbour(ThermicObject neighbour) {
		thermicNeighbours.add(neighbour);
	}

	@Unbind(id="thermicNeighbours")
	public void unbindThermicNeighbour(ThermicObject neighbour) {
		thermicNeighbours.remove(neighbour);
	}
	
	public List<ThermicObject> thermicNeighbours = new ArrayList<ThermicObject>();
	
}
