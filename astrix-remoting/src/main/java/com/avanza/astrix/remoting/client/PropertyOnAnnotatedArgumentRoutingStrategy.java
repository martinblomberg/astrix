/*
 * Copyright 2014 Avanza Bank AB
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
package com.avanza.astrix.remoting.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PropertyOnAnnotatedArgumentRoutingStrategy implements Router {
	
	private final int argumentIndex;
	private final Method propertyMethod;


	public PropertyOnAnnotatedArgumentRoutingStrategy(int argumentIndex, Method propertyMethod) {
		this.argumentIndex = argumentIndex;
		this.propertyMethod = propertyMethod;
	}

	@Override
	public RoutingKey getRoutingKey(Object[] args) {
		Object routingKey;
		try {
			routingKey = propertyMethod.invoke(args[argumentIndex]);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalArgumentException("Failed to route using method:" + propertyMethod, e);
		}
		return RoutingKey.create(routingKey);
	}
}