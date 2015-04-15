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
package com.avanza.astrix.serviceunit;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.avanza.astrix.beans.core.AstrixSettings;
import com.avanza.astrix.beans.factory.AstrixBeanKey;
import com.avanza.astrix.beans.publish.AstrixPublishedBeans;
import com.avanza.astrix.beans.publish.AstrixPublishedBeansAware;
import com.avanza.astrix.beans.registry.AstrixServiceRegistryClient;
import com.avanza.astrix.beans.service.AstrixServiceProperties;
import com.avanza.astrix.config.DynamicConfig;
import com.avanza.astrix.config.DynamicLongProperty;
import com.avanza.astrix.core.ServiceUnavailableException;
/**
 * The service registry worker is a server-side component responsible for continuously publishing 
 * all exported services from the current application onto the service registry.
 * 
 * @author Elias Lindholm (elilin)
 * 
 */
public class ServiceRegistryExporterWorker extends Thread implements AstrixPublishedBeansAware {
	
	private List<ServiceRegistryExportedService> exportedServices = new CopyOnWriteArrayList<>();
	private AstrixServiceRegistryClient serviceRegistryClient;
	private final Logger log = LoggerFactory.getLogger(ServiceRegistryExporterWorker.class);
	private final DynamicLongProperty exportIntervallMillis;		  
	private final DynamicLongProperty serviceLeaseTimeMillis;
	private final DynamicLongProperty retryIntervallMillis;

	public ServiceRegistryExporterWorker(DynamicConfig config) {
		this.exportIntervallMillis = AstrixSettings.SERVICE_REGISTRY_EXPORT_INTERVAL.getFrom(config);
		this.retryIntervallMillis = AstrixSettings.SERVICE_REGISTRY_EXPORT_RETRY_INTERVAL.getFrom(config);
		this.serviceLeaseTimeMillis = AstrixSettings.SERVICE_REGISTRY_LEASE.getFrom(config);
	}
	
	public void startServiceExporter() {
		if (exportedServices.isEmpty()) {
			log.info("No ServiceExporters configured. No services will be published to service registry");
			return;
		}
		start();
	}
	
	@PreDestroy
	public void destroy() {
		interrupt();
	}

	@Override
	public void run() {
		while (!interrupted()) {
			long sleepTimeUntilNextAttempt = this.exportIntervallMillis.get();
			try {
				exportProvidedServcies();
			} catch (ServiceUnavailableException e) {
				// Not bound to service registry
				sleepTimeUntilNextAttempt = this.retryIntervallMillis.get();
				log.info(String.format("Failed to export services to registry. Sleeping %s millis until next attempt.", sleepTimeUntilNextAttempt), e);
			} catch (Exception e) {
				log.info(String.format("Failed to export services to registry. Sleeping %s millis until next attempt.", sleepTimeUntilNextAttempt), e);
			} 
			try {
				sleep(sleepTimeUntilNextAttempt);
			} catch (InterruptedException e) {
				interrupt();
			}
		}
	}

	private void exportProvidedServcies() {
		for (ServiceRegistryExportedService exportedService : exportedServices) {
			AstrixServiceProperties serviceProperties = exportedService.exportServiceProperties();
			serviceRegistryClient.register(serviceProperties.getApi(), serviceProperties, serviceLeaseTimeMillis.get());
			log.debug("Exported to service registry. service={} properties={}", serviceProperties.getApi().getName(), serviceProperties);
			if (exportedService.exportsAsyncApi()) {
				serviceProperties = exportedService.exportAsyncServiceProperties();
				serviceRegistryClient.register(serviceProperties.getApi(), serviceProperties, serviceLeaseTimeMillis.get());
				log.debug("Exported to service registry. service={} properties={}", serviceProperties.getApi().getName(), serviceProperties);
			}
		}
	}
	
	public void addServiceBuilder(
			ServiceRegistryExportedService serviceRegistryExportedService) {
		this.exportedServices.add(serviceRegistryExportedService);
	}

	@Override
	public void setAstrixBeans(AstrixPublishedBeans beans) {
		this.serviceRegistryClient = beans.getBean(AstrixBeanKey.create(AstrixServiceRegistryClient.class));
	}
	

}
