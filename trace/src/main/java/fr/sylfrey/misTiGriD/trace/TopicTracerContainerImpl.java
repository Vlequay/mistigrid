package fr.sylfrey.misTiGriD.trace;

import java.util.LinkedList;
import java.util.List;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.japi.Creator;
import fr.sylfrey.akka.ActorSystemProvider;
import fr.sylfrey.misTiGriD.trace.framework.ActorContainer;
import fr.sylfrey.misTiGriD.trace.framework.Consumer;
import fr.sylfrey.misTiGriD.trace.framework.topic.Topic;

//@Component(name="TopicTracer",immediate=true)
public class TopicTracerContainerImpl {

//	@Bind
	public void bindActorSystem(ActorSystemProvider provider) {
		actorSystem = provider.getSystem();		
	}
	
//	@Bind(aggregate=true)
	public void bindTopic(ActorContainer<Topic<Object>> container) {
		if (!(container.actor() instanceof Topic)) { return; }
		topics.add(container.actor());
		if (tracer!=null) {
			container.actor().subscribe(tracer);
		}
	}
	
//	@Validate
	public void start() {
		tracer = TypedActor.get(actorSystem).typedActorOf(
				new TypedProps<TopicTracerImpl>(
						Consumer.class, 
						new Creator<TopicTracerImpl>() {
							public TopicTracerImpl create() {
								return new TopicTracerImpl(logger);
							}
						}));
		for (Topic<Object> topic : topics) {
			topic.subscribe(tracer);
		}
	}

//	@Invalidate
	public void stop() {
		TypedActor.get(actorSystem).stop(tracer);
	}
	
//	@Requires
	public Tracer logger;
	
		
	public ActorSystem actorSystem;
	public List<Topic<Object>> topics = new LinkedList<Topic<Object>>();
		
	
	private Consumer<Object> tracer;

}
