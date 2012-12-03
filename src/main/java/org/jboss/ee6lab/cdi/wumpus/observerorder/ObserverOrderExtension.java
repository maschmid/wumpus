package org.jboss.ee6lab.cdi.wumpus.observerorder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.inject.spi.ProcessObserverMethod;

public class ObserverOrderExtension implements Extension {
	
	private static class OrderedObserverMethod implements Comparable<OrderedObserverMethod> {
		
		public OrderedObserverMethod(int order, ObserverMethod observerMethod) {
			this.order = order;
			this.observerMethod = observerMethod;
		}
		
		public int getOrder() {
			return order;
		}
		public void setOrder(int order) {
			this.order = order;
		}
		public ObserverMethod getObserverMethod() {
			return observerMethod;
		}
		public void setObserverMethod(ObserverMethod observerMethod) {
			this.observerMethod = observerMethod;
		}
		private int order;
		private ObserverMethod observerMethod;
		
		@Override
		public int compareTo(OrderedObserverMethod o) {
			return order - o.order;
		}
	}
	
	Map<Type, SortedSet<OrderedObserverMethod>> observersMap = new HashMap<Type, SortedSet<OrderedObserverMethod>>();
	
	<T, X> void processObserverMethod(@Observes ProcessObserverMethod<T, X> pom) {
		
		System.out.println("XXX processObserverMethod on " + pom.getObserverMethod().toString());
				
		for (Annotation annotation : pom.getObserverMethod().getObservedQualifiers()) {
			if (annotation.annotationType().equals(ObserverOrder.class)) {
				System.out.println("XXX: found orderable observer!!!");
				
				ObserverOrder order = (ObserverOrder)annotation;
				int orderValue = order.value();
				Type type = pom.getObserverMethod().getObservedType();
				
				if (!observersMap.containsKey(type)) {
					observersMap.put(type, new TreeSet<OrderedObserverMethod> ());
				}
				
				for (Annotation observedQualifier : pom.getObserverMethod().getObservedQualifiers()) {
					System.out.println("XXX Observed qualifier: " + observedQualifier.toString());
				}
				
				observersMap.get(type).add(new OrderedObserverMethod(orderValue, pom.getObserverMethod()));
			}
		}
	}
	
	void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {
		System.out.println("XXX finished the scanning process");
		
		for (Map.Entry<Type, SortedSet<OrderedObserverMethod>> entry : observersMap.entrySet()) {
			final Type type = entry.getKey();
			final SortedSet<OrderedObserverMethod> observers = entry.getValue();
			
			abd.addObserverMethod(new ObserverMethod() {

				@Override
				public Class getBeanClass() {
					return NullObserver.class;
				}

				@Override
				public Type getObservedType() {
					return type;
				}

				@Override
				public Set getObservedQualifiers() {
					return new HashSet();
				}

				@Override
				public Reception getReception() {
					return Reception.ALWAYS;
				}

				@Override
				public TransactionPhase getTransactionPhase() {
					return TransactionPhase.IN_PROGRESS;
				}

				@Override
				public void notify(Object event) {
					for (OrderedObserverMethod observer : observers) {
						
						System.out.println("XXX notifying observer: " + observer.getObserverMethod().toString());
						
						observer.getObserverMethod().notify(event);
					}
				}
			});
		}
	}
}
