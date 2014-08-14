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
package se.avanzabank.service.suite.remoting.plugin;

import se.avanzabank.service.suite.context.AstrixObjectSerializer;
import se.avanzabank.service.suite.context.AstrixServiceFactory;
import se.avanzabank.service.suite.remoting.client.AstrixRemotingTransport;
import se.avanzabank.service.suite.remoting.client.AstrixServiceProxy;

public class AstrixRemotingServiceFactory<T> implements AstrixServiceFactory<T> {
	
	private Class<T> serviceApi;
	private AstrixRemotingTransportFactory astrixRemotingTransportFactory;
	private String targetSpaceName;
	private AstrixObjectSerializer objectSerializer;
	
	public AstrixRemotingServiceFactory(Class<T> serviceApi,
			AstrixRemotingTransportFactory astrixRemotingTransportFactory,
			String targetSpaceName, AstrixObjectSerializer objectSerializer) {
		this.serviceApi = serviceApi;
		this.astrixRemotingTransportFactory = astrixRemotingTransportFactory;
		this.targetSpaceName = targetSpaceName;
		this.objectSerializer = objectSerializer;
	}

	@Override
	public T create() {
		AstrixRemotingTransport remotingTransport = astrixRemotingTransportFactory.createRemotingTransport(targetSpaceName);
		return AstrixServiceProxy.create(serviceApi, remotingTransport, objectSerializer);
	}

	@Override
	public Class<T> getServiceType() {
		return serviceApi;
	}

}
