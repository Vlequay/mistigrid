package fr.sylfrey.misTiGriD.deploy

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import org.apache.felix.ipojo.annotations.Bind
import org.apache.felix.ipojo.annotations.Component
import org.apache.felix.ipojo.annotations.Instantiate
import org.apache.felix.ipojo.annotations.Validate
import org.apache.felix.ipojo.annotations.Invalidate
import org.apache.felix.ipojo.ComponentInstance
import scala.concurrent.{Future, ExecutionContext}

@Component
@Instantiate
class Config {
  
  private var houseFactory : HouseFactory = null
  implicit val ec = ExecutionContext.Implicits.global
  
  @Bind
  def bindHouseFactory(factory : HouseFactory) : Unit = {
    this.houseFactory = factory
  }
  
  @Validate
  def start() = {
    val atmosphere = "atmosphere"
    val atmosphereModel = Atmosphere(name=atmosphere, isManual=true, temperature=20, minTemperature=12, maxTemperature=22)

    val aggregator = "mouchezAggregator"
    val aggregatorModel = Aggregator(
        name = aggregator, 
        actorPath = aggregator, 
        hasRemoteParent = false, 
    	remoteParentURL = "akka://MisTiGriD@i137.194.22.175:4004/user/districtAggregator")
             
    val aggregatorController = "mouchezAggregatorController"
    val loadTopic = "mouchezLoadTopic"
    val houseLoadManager = "houseLoadManager"
    val loadManagerURI = "akka://MisTiGriD/user/" + houseLoadManager
//    val loadHierarch = "loadHierarch" // !! don't change this one, cf. Archiver
             
    val kitchen = "kitchen"
    val bathroom = "bathroom"
    val room = "room"
    val livingroom = "livingroom"
    val entrance = "entrance"
    val wc = "wc"

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
    
    val heaterKitchen = "heater_" + kitchen
    val heaterRoom = "heater_" + room
    val heaterLR1 = "heater_" + livingroom + "_1"
    val heaterLR2 = "heater_" + livingroom + "_2"
    val heaterBathroom = "heater_" + bathroom
    val heaterEntrance = "heater_" + entrance

    val walls = Map[String, Wall](
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
      
    val rooms = Map[String, TH](
      kitchen    -> TH(24, 12, List(akw, akn, kb, kw, ke, heaterKitchen)),
      bathroom   -> TH(24, 6,  List(ab, br, bw, kb, heaterBathroom)),
      room       -> TH(24, 20, List(arn, rae, rl, er, wr, br, heaterRoom)),
      livingroom -> TH(24, 31, List(els, ele, rl, lae, las, alw, heaterLR1, heaterLR2)),
      entrance   -> TH(24, 10, List(ke, we, er, ele, els, ae, heaterEntrance)),
      wc         -> TH(24, 2,  List(bw, wr, we, kw)))
      
    val heaters = Map[String, Tuple2[Heater, HeaterManager]](
      heaterKitchen  -> Tuple2(
          Heater(0, 0.05f, 0.1f, 400f, aggregator, kitchen),	
          HeaterManager(
              actorPath = heaterKitchen  + "_manager", 
              period = 50,
              requiredTemperature = 22,
              prosumerStatus = "Flexible",
              houseLoadManagerURI = loadManagerURI,
              heater = heaterKitchen,
              room = kitchen)),
      heaterRoom     -> Tuple2(
          Heater(0, 0.05f, 0.1f, 400f, aggregator, room),
          HeaterManager(
              actorPath = heaterRoom     + "_manager", 
              period = 50,
              requiredTemperature = 22,
              prosumerStatus = "Flexible",
              houseLoadManagerURI = loadManagerURI,
              heater = heaterRoom, 
              room = room)),
      heaterLR1      -> Tuple2(
          Heater(0, 0.05f, 0.1f, 400f, aggregator, livingroom),
          HeaterManager(
              actorPath = heaterLR1      + "_manager", 
              period = 50,
              requiredTemperature = 22,
              prosumerStatus = "Flexible",
              houseLoadManagerURI = loadManagerURI,
              heater = heaterLR1,
              room = livingroom)),
      heaterLR2      -> Tuple2(
          Heater(0, 0.05f, 0.1f, 400f, aggregator, livingroom),
          HeaterManager(
              actorPath = heaterLR2      + "_manager", 
              period = 50,
              requiredTemperature = 22,
              prosumerStatus = "Flexible",
              houseLoadManagerURI = loadManagerURI,
              heater = heaterLR2,
              room = livingroom)),
      heaterBathroom -> Tuple2(
          Heater(0, 0.05f, 0.1f, 400f, aggregator, bathroom),
          HeaterManager(
              actorPath = heaterBathroom + "_manager", 
              period = 50,
              requiredTemperature = 22,
              prosumerStatus = "Flexible",
              houseLoadManagerURI = loadManagerURI,
              heater = heaterBathroom,
              room = bathroom)),
      heaterEntrance -> Tuple2(
          Heater(0, 0.05f, 0.1f, 400f, aggregator, entrance),
          HeaterManager(
              actorPath = heaterEntrance + "_manager", 
              period = 50,
              requiredTemperature = 22,
              prosumerStatus = "Flexible",
              houseLoadManagerURI = loadManagerURI,
              heater = heaterEntrance,
              room = entrance)))
 
      
      

    ////////////
    // LAYOUTS //
    ////////////

    val roomLayouts = Map[String, Dim](
      kitchen    -> Dim(  50,  50, 300, 400, 5),
      bathroom   -> Dim( 350,  50, 200, 300, 5),
      room       -> Dim( 550,  50, 400, 500, 5),
      livingroom -> Dim(  50, 550, 900, 400, 3),
      entrance   -> Dim(  50, 450, 500, 200, 5),
      wc         -> Dim( 350, 350, 200, 100, 5))  

    val heaterLayouts = Map[String, Tuple2[String,Pos]](
      heaterKitchen 	-> (kitchen, 	Pos(150, 250, 10)),
      heaterBathroom 	-> (bathroom, 	Pos(350, 250, 10)),
      heaterRoom 		-> (room, 	Pos(750, 450, 10)),
      heaterLR1	 	-> (livingroom, Pos(850, 650, 10)),
      heaterLR2	 	-> (livingroom, Pos(250, 750, 10)),
      heaterEntrance 	-> (entrance, 	Pos(450, 550, 10)))

    val wallLayouts = Map[String, Dim](
      br 	-> Dim(540, 250, 10, 80, 6),
      er 	-> Dim(550, 460, 10, 80, 6),
      els   -> Dim(250, 640, 80, 10, 6),
      ae 	-> Dim( 50, 550, 10, 80, 6),
      ke 	-> Dim(160, 440, 80, 10, 6),
      we 	-> Dim(360, 440, 80, 10, 6))
    
          
    houseFactory.make(
      atmosphereModel,
      aggregatorModel,
      walls, wallLayouts,
      rooms, roomLayouts,
      heaters, heaterLayouts).foreach( _.future onSuccess {
        case componentInstance => instances += componentInstance
    })
      
	
    houseFactory.spawn("HouseLoadManagerDeployer",
      "instance.name" -> houseLoadManager,
      "actorPath" -> houseLoadManager,
      "maxConsumption" -> "-2600",
      "hysteresisThreshold" -> "300",
      "prosumerStatus" -> "Flexible",
      "period" -> "500",
      "requires.from" -> houseFactory.&("aggregator" -> aggregator)
    ).future onSuccess {
        case componentInstance => instances += componentInstance
    }

//    houseFactory.spawn("Topic",
//      "instance.name" -> loadTopic,
//      "topicPath" -> loadTopic
//    )
    
//    houseFactory.spawn("LoadHierarch",
//      "instance.name" -> loadHierarch,
//      "actorPath" -> loadHierarch,
//      "period" -> "500",
//      "highThreshold" -> "-2600",
//      "requires.from" -> houseFactory.&("aggregator" -> aggregator)
//    )
  
  }
  
  val instances = ListBuffer[ComponentInstance]()
  
  @Invalidate def stop() : Unit = {
    instances.foreach( instance => { instance.stop; instance.dispose } )
    instances.clear
  }
  
}