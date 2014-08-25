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
package se.avanzabank.service.suite.bus.client;

import org.openspaces.remoting.Routing;



public interface AstrixServiceBus {
	
	// TODO: use separate interface for providers and consumers?
	
	<T> AstrixServiceProperties lookup(@Routing Class<T> type);
	
	<T> AstrixServiceProperties lookup(@Routing Class<T> type, String qualifier);
	
	<T> void register(@Routing Class<T> type, AstrixServiceProperties properties); // TODO: lease/renewal?
	
}
