/*
 * Copyright 2014-2015 Avanza Bank AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.avanzabank.service.suite.remoting.client;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/**
 * 
 * @author Elias Lindholm (elilin)
 *
 */
final class RoutingKeyMethodCache {
	
	private final ConcurrentMap<Class<?>, CachedRoutingKeyMethod> routingKeyMethodByType = new ConcurrentHashMap<>();
	private final RoutingKeyMethodScanner scanner = new RoutingKeyMethodScanner();
	
	public Method getRoutingKeyMethod(Class<?> spaceObjectClass) {
		CachedRoutingKeyMethod cachedMethod = routingKeyMethodByType.get(spaceObjectClass);
		if (cachedMethod != null) {
			return cachedMethod.get(); 
		}
		Method routingKeyMethod = scanner.getRoutingKeyMethod(spaceObjectClass);
		routingKeyMethodByType.putIfAbsent(spaceObjectClass, new CachedRoutingKeyMethod(routingKeyMethod));
		return routingKeyMethod;
	}
	
	private static class CachedRoutingKeyMethod {
		private Method method;
		public CachedRoutingKeyMethod(Method method) {
			this.method = method;
		}
		public Method get() {
			return method;
		}
	}
	

}
