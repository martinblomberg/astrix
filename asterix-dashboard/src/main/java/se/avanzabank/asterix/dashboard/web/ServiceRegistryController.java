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
package se.avanzabank.asterix.dashboard.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import se.avanzabank.asterix.context.AsterixServiceProperties;
import se.avanzabank.asterix.service.registry.client.AsterixServiceRegistryAdministrator;
import se.avanzabank.asterix.service.registry.server.AsterixServiceRegistryEntry;

@RestController
@RequestMapping("/services")
public class ServiceRegistryController {
	
	private AsterixServiceRegistryAdministrator serviceRegistryAdmin;
	
	@Autowired
	public ServiceRegistryController(AsterixServiceRegistryAdministrator serviceRegistryAdmin) {
		this.serviceRegistryAdmin = Objects.requireNonNull(serviceRegistryAdmin);
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public List<Service> services() {
		List<AsterixServiceRegistryEntry> services = serviceRegistryAdmin.listServices();
		List<Service> result = new ArrayList<>();
		for (AsterixServiceRegistryEntry entry : services) {
			Service s = new Service();
			s.setProvidedApi(entry.getServiceBeanType());
			s.setComponent(new AsterixServiceProperties(entry.getServiceProperties()).getComponent());
			s.setUrl(entry.getServiceProperties());
			result.add(s);
		}
		return result;
	}

	public static class Service {
		private String providedApi;
		private String component;
		private Map<String, String> url;

		public String getProvidedApi() {
			return providedApi;
		}

		public void setProvidedApi(String providedApi) {
			this.providedApi = providedApi;
		}

		public String getComponent() {
			return component;
		}

		public void setComponent(String component) {
			this.component = component;
		}
		
		public void setUrl(Map<String, String> url) {
			this.url = url;
		}
		
		public Map<String, String> getUrl() {
			return url;
		}


	}

}
