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
package se.avanzabank.asterix.gs;

import java.util.Collections;
import java.util.List;

import org.kohsuke.MetaInfServices;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import se.avanzabank.asterix.context.AsterixApiDescriptor;
import se.avanzabank.asterix.provider.component.AsterixServiceRegistryComponents;
import se.avanzabank.asterix.service.registry.client.AsterixServiceProperties;
import se.avanzabank.asterix.service.registry.client.AsterixServiceRegistryComponent;
import se.avanzabank.asterix.service.registry.server.ServiceRegistryExporter;

@MetaInfServices(AsterixServiceRegistryComponent.class)
public class AsterixGsComponent implements AsterixServiceRegistryComponent {
	
	@Override
	public <T> T createService(AsterixApiDescriptor apiDescriptor, Class<T> type, AsterixServiceProperties serviceProperties) {
		if (!GigaSpace.class.isAssignableFrom(type)) {
			throw new IllegalStateException("Programming error, attempted to create: " + type);
		}
		return type.cast(GsBinder.createGsFactory(serviceProperties).create()); // TODO: fault tolerance
	}

	@Override
	public Class<? extends ServiceRegistryExporter> getServiceExporterClass() {
		return GigaSpaceServiceRegistryExporter.class;
	}
	
	@Override
	public List<String> getComponentDepenencies() {
		return Collections.emptyList();
	}

	@Override
	public String getName() {
		return AsterixServiceRegistryComponents.GS;
	}
	
	@Override
	public void registerBeans(BeanDefinitionRegistry registry) {
		
	}
	
}