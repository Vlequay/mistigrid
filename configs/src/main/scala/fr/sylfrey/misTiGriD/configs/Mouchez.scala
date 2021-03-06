/*******************************************************************************
 * Copyright (c) 2013 EDF. This software was developed with the 
 * collaboration of Télécom ParisTech (Sylvain Frey).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Sylvain Frey - initial API and implementation
 ******************************************************************************/
package fr.sylfrey.misTiGriD.configs.mouchez

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import org.apache.felix.ipojo.annotations.Bind
import org.apache.felix.ipojo.annotations.Component
import org.apache.felix.ipojo.annotations.Instantiate
import org.apache.felix.ipojo.annotations.Validate
import org.apache.felix.ipojo.annotations.Invalidate
import org.apache.felix.ipojo.annotations.Requires
import org.apache.felix.ipojo.ComponentInstance
import scala.concurrent.{Future, ExecutionContext}
import fr.sylfrey.misTiGriD.deploy._
import org.osgi.framework.BundleContext
import fr.sylfrey.misTiGriD.wrappers.BundleContextProvider

/**
 * Sample house configuration:
 * 6 rooms, 6 smart heaters, 5 smart lamps.
 */
@Component
@Instantiate(name="ConfigMouchez")
class Config(  
  @Requires houseFactory : HouseFactory,
  @Requires metaFactory : MetaFactory,
  @Requires contextProvider: BundleContextProvider) {
  
  implicit lazy val ec = ExecutionContext.Implicits.global 
  
  lazy val instances = ListBuffer[ComponentInstance]()
    
  private def spawn(factoryName: String, items: (String, Any)*): Unit = {
    metaFactory.spawn(factoryName, items: _*).future onSuccess {
      case componentInstance => instances += componentInstance
    }
  }
    
  @Validate def start() = {
    
    val context = contextProvider.get()
    
    // House level definitions: atmosphere, electrical aggregators.
    val atmosphere = "atmosphere"
    val atmosphereModel = Atmosphere(name=atmosphere, isManual=true, temperature=18.5f, minTemperature=12, maxTemperature=22)

    val aggregator = "houseAggregator"
    val hasRemoteParent = context.getProperty("MisTiGriD.hasRemoteParent") match {
      case "true" => true
      case "false" => false
      case incorrect => println("### invalid configuration: " + incorrect); false
    }
    val remoteParentURL = context.getProperty("MisTiGriD.remoteParentURL")
    // typical value: "akka://MisTiGriD@host:4004/user/districtAggregator"
    // cf. config.properties
    val aggregatorModel = Aggregator(
        name = aggregator, 
        actorPath = aggregator, 
        hasRemoteParent = hasRemoteParent, 
    	remoteParentURL = remoteParentURL)
             
    val aggregatorController = "houseAggregatorController"
    val loadTopic = "houseLoadTopic"
    val houseLoadManager = "houseLoadManager"
    val loadManagerURI = "akka://MisTiGriD/user/" + houseLoadManager
    val districtLoadManagerURI = context.getProperty("MisTiGriD.districtLoadManagerURI")      
    // typical value: "akka://MisTiGriD@localhost:4004/user/districtLoadManager"
    // cf. config.properties
             
      
    ////////////////////
    // IDENTIFICATORS //
    ////////////////////
      
    // Room identificators.
    val kitchen = "kitchen"
    val bathroom = "bathroom"
    val room = "room"
    val livingroom = "livingroom"
    val entrance = "entrance"
    val wc = "wc"

    // Wall identificators.
    // Naming convention here: id = initial1 + initial2 + n/s/e/w
    // (north/south/east/west) where the initials 
    // designate the separated objects identificators; 
    // if there is no conflict, the last letter is optional.
    // Example: akn = Atmosphere Kitchen North.
    val akn = "wall_akn"
    val ab  = "wall_ab"
    val arn = "wall_arn"
    val rae = "wall_rae"
    val lae = "wall_lae"
    val las = "wall_las"
    val alw = "wall_alw"
    val akw = "wall_akw"
    val kb  = "wall_kb"
    val rl  = "wall_rl"
    val ele = "wall_ele"
    val kw  = "wall_kw"
    val bw  = "wall_bw"
    val wr  = "wall_wr"
    val br  = "wall_br"
    val er  = "wall_er"
    val els = "wall_els"
    val ae  = "wall_ae"
    val ke  = "wall_ke"
    val we  = "wall_we"
    
    // Heater identificators.
    val heaterKitchen = "heater_" + kitchen
    val heaterRoom = "heater_" + room
    val heaterLR1 = "heater_" + livingroom + "_1"
    val heaterLR2 = "heater_" + livingroom + "_2"
    val heaterBathroom = "heater_" + bathroom
    val heaterEntrance = "heater_" + entrance
        
    // Lamp identificators -> moved to lamps configuration
//    val lamp1 = "lamp_1"
//    val lamp2 = "lamp_2"
//    val lamp3 = "lamp_3"
//    val lamp4 = "lamp_4"
//    val lamp5 = "lamp_5"

      
      
      
      
    ////////////////////
    // SPECIFICATIONS //
    ////////////////////      
      
      
    // Wall specifications.
    val walls = Map[String, Wall](
      // wallID -> Wall(surfacicHeatConductance, openness, openHeatConductance, length, list of neighbours)
      akn -> Wall(0.01f, false, 0.05f, 3, List(atmosphere, kitchen)),
      ab  -> Wall(0.01f, false, 0.05f, 2, List(atmosphere, bathroom)),
      arn -> Wall(0.01f, false, 0.05f, 4, List(atmosphere, room)),
      rae -> Wall(0.01f, false, 0.05f, 5, List(room, atmosphere)),
      lae -> Wall(0.01f, false, 0.05f, 4, List(livingroom, atmosphere)),
      las -> Wall(0.01f, false, 0.05f, 9, List(livingroom, atmosphere)),
      alw -> Wall(0.01f, false, 0.05f, 3, List(atmosphere, livingroom)),
      akw -> Wall(0.01f, false, 0.05f, 4, List(atmosphere, kitchen)),
      kb  -> Wall(0.01f, false, 0.05f, 3, List(kitchen, bathroom)),
      rl  -> Wall(0.01f, false, 0.05f, 4, List(room, livingroom)),
      ele -> Wall(0.01f, false, 0.05f, 1, List(entrance, livingroom)),
      kw  -> Wall(0.01f, false, 0.05f, 1, List(kitchen, wc)),
      bw  -> Wall(0.01f, false, 0.05f, 2, List(bathroom, wc)),
      wr  -> Wall(0.01f, false, 0.05f, 1, List(wc, room)),
      br  -> Wall(0.01f, false, 0.05f, 3, List(bathroom, room)),
      er  -> Wall(0.01f, false, 0.05f, 1, List(entrance, room)),
      els -> Wall(0.01f, false, 0.05f, 5, List(entrance, livingroom)),
      ae  -> Wall(0.01f, false, 0.05f, 2, List(atmosphere, entrance)),
      ke  -> Wall(0.01f, false, 0.05f, 3, List(kitchen, entrance)),
      we  -> Wall(0.01f, false, 0.05f, 2, List(wc, entrance)))
      
    // Room specifications.
    val rooms = Map[String, ThermicObject](
      // roomID -> ThermicObject(initialTemperature, heatCapacity, list of walls and heaters)
      kitchen    -> ThermicObject(24, 12, List(akw, akn, kb, kw, ke, heaterKitchen)),
      bathroom   -> ThermicObject(24, 6,  List(ab, br, bw, kb, heaterBathroom)),
      room       -> ThermicObject(24, 20, List(arn, rae, rl, er, wr, br, heaterRoom)),
      livingroom -> ThermicObject(24, 31, List(els, ele, rl, lae, las, alw, heaterLR1, heaterLR2)),
      entrance   -> ThermicObject(24, 10, List(ke, we, er, ele, els, ae, heaterEntrance)),
      wc         -> ThermicObject(24, 2,  List(bw, wr, we, kw)))
      
    // Heater (and heater manager) specifications.
    val heaters = Map[String, Tuple2[Heater, HeaterManager]](
      // heaterID -> ...
      heaterKitchen  -> Tuple2(
          // Heater(initialProsumedPower, heatConductance, efficiency, maxPower, aggregatorID, roomID)
          Heater(0, 0.02f, 0.1f, 200f, aggregator, kitchen),	
          HeaterManager(
              actorPath = heaterKitchen  + "_manager", 
              period = 400,
              requiredTemperature = 22,
              prosumerStatus = "Flexible",
              houseLoadManagerURI = loadManagerURI,
              heater = heaterKitchen,
              room = kitchen,
              kp = 40, ki = 0, kd = 0)),
      heaterRoom     -> Tuple2(
          Heater(0, 0.02f, 0.1f, 200f, aggregator, room),
          HeaterManager(
              actorPath = heaterRoom     + "_manager", 
              period = 400,
              requiredTemperature = 22,
              prosumerStatus = "Flexible",
              houseLoadManagerURI = loadManagerURI,
              heater = heaterRoom, 
              room = room,
              kp = 40, ki = 0, kd = 0)),
      heaterLR1      -> Tuple2(
          Heater(0, 0.02f, 0.1f, 200f, aggregator, livingroom),
          HeaterManager(
              actorPath = heaterLR1      + "_manager", 
              period = 400,
              requiredTemperature = 22,
              prosumerStatus = "Flexible",
              houseLoadManagerURI = loadManagerURI,
              heater = heaterLR1,
              room = livingroom,
              kp = 40, ki = 0f, kd = 0)),
      heaterLR2      -> Tuple2(
          Heater(0, 0.02f, 0.1f, 200f, aggregator, livingroom),
          HeaterManager(
              actorPath = heaterLR2      + "_manager", 
              period = 400,
              requiredTemperature = 22,
              prosumerStatus = "Flexible",
              houseLoadManagerURI = loadManagerURI,
              heater = heaterLR2,
              room = livingroom,
              kp = 40, ki = 0, kd = 0)),
      heaterBathroom -> Tuple2(
          Heater(0, 0.02f, 0.1f, 200f, aggregator, bathroom),
          HeaterManager(
              actorPath = heaterBathroom + "_manager", 
              period = 400,
              requiredTemperature = 22,
              prosumerStatus = "Flexible",
              houseLoadManagerURI = loadManagerURI,
              heater = heaterBathroom,
              room = bathroom,
              kp = 40, ki = 0, kd = 0)),
      heaterEntrance -> Tuple2(
          Heater(0, 0.02f, 0.1f, 200f, aggregator, entrance),
          HeaterManager(
              actorPath = heaterEntrance + "_manager", 
              period = 400,
              requiredTemperature = 22,
              prosumerStatus = "Flexible",
              houseLoadManagerURI = loadManagerURI,
              heater = heaterEntrance,
              room = entrance,
              kp = 40, ki = 0, kd = 0)))
 
    // Lamp (and lamp manager) specifications -> moved to lamps configuration
    val lamps = Map[String, Lamp]()
//      // lampID -> (maxPower, aggregator, loadManagerURI)  
//      lamp1 -> Lamp(100, aggregator, loadManagerURI),
//      lamp2 -> Lamp(100, aggregator, loadManagerURI),
//      lamp3 -> Lamp(100, aggregator, loadManagerURI),
//      lamp4 -> Lamp(100, aggregator, loadManagerURI),
//      lamp5 -> Lamp(100, aggregator, loadManagerURI)
//    )
      

    /////////////
    // LAYOUTS //
    /////////////

    // Spatial organisation of rooms.
    val roomLayouts = Map[String, Dim](
      // roomID -> Dim(x, y, width, height, z)
      kitchen    -> Dim(  50,  50, 300, 400, 5),
      bathroom   -> Dim( 350,  50, 200, 300, 5),
      room       -> Dim( 550,  50, 400, 500, 5),
      livingroom -> Dim(  50, 550, 900, 400, 3),
      entrance   -> Dim(  50, 450, 500, 200, 5),
      wc         -> Dim( 350, 350, 200, 100, 5))  

    // Spatial organisation of heaters.
    val heaterLayouts = Map[String, Tuple2[String,Pos]](
      // heaterID -> (roomID, Pos(x, y, z))
      heaterKitchen 	-> (kitchen, 	Pos(60, 	250, 10)),
      heaterBathroom 	-> (bathroom, 	Pos(360, 	200, 10)),
      heaterRoom 		-> (room, 		Pos(790, 	400, 10)),
      heaterLR1	 		-> (livingroom, Pos(790, 	770, 10)),
      heaterLR2	 		-> (livingroom, Pos(60, 	770, 10)),
      heaterEntrance 	-> (entrance, 	Pos(70, 	550, 10)))

    // Spatial organisation of doors (not intuitive, I know).
    val wallLayouts = Map[String, Dim](
      // wallID -> Dim(x, y, width, height, z)
      br 	-> Dim(540, 250, 10, 80, 6),
      er 	-> Dim(550, 460, 10, 80, 6),
      els   -> Dim(250, 640, 80, 10, 6),
      ae 	-> Dim( 50, 550, 10, 80, 6),
      ke 	-> Dim(160, 440, 80, 10, 6),
      we 	-> Dim(360, 440, 80, 10, 6))
    
    // Spatial organisation of lamps -> moved to lamps configuration
    val lampLayouts = Map[String, Tuple2[Int,Int]]()
//      // lampID -> (x, y)  
//      lamp1 -> (240, 230),
//      lamp2 -> (580, 330),
//      lamp3 -> (720, 150),
//      lamp4 -> (360, 760),
//      lamp5 -> (610, 760)
//    )
          
      
    houseFactory.make(
      atmosphereModel,
      aggregatorModel,
      walls, wallLayouts,
      rooms, roomLayouts,
      heaters, heaterLayouts,
      lamps, lampLayouts).foreach( _.future onSuccess {
        case componentInstance => instances += componentInstance
    })
       
	
    // Load manager specification and layout.
    
    spawn("HouseLoadManagerDeployer",
      "instance.name" -> houseLoadManager,
      "actorPath" -> houseLoadManager,
      "maxConsumption" -> "-800",
      "hysteresisThreshold" -> "300",
      "loadReductionDelta" -> "500",
      "acceptableLoadRange" -> "1000",
      "prosumerStatus" -> "Flexible",
      "period" -> "50",
      "hasParent" -> hasRemoteParent.toString,
      "districtLoadManagerURI" -> districtLoadManagerURI,
      "requires.from" -> metaFactory.&("aggregator" -> aggregator))

    spawn("LoadManagerLayout",
      "instance.name" -> (houseLoadManager + "Layout"),
      "layout.name" -> houseLoadManager,
      "x" -> "0",
      "y" -> "0",
      "width" -> "160",
      "height" -> "120",
      "layer" -> "10",
      "requires.from" -> metaFactory.&("manager" -> houseLoadManager))
  
  }
  
  @Invalidate def stop() : Unit = {
    instances.foreach( instance => { instance.stop; instance.dispose } )
    instances.clear
  }
    
}
