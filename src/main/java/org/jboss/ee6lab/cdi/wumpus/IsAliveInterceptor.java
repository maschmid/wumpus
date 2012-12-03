package org.jboss.ee6lab.cdi.wumpus;

import java.io.Serializable;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@IsAlive
@Interceptor
public class IsAliveInterceptor implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	@Current
	Instance<Player> currentPlayerInstance;
	
	@AroundInvoke
	public Object manage(InvocationContext ic) throws Exception {
		
		if (currentPlayerInstance.get().isAlive()) {
			// I'am doing science and I'am still alive!	
			return ic.proceed();
		}
		
		return null;
	}

}
