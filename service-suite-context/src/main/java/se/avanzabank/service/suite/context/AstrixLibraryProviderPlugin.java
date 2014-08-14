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
package se.avanzabank.service.suite.context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.MetaInfServices;

import se.avanzabank.service.suite.provider.library.AstrixExport;
import se.avanzabank.service.suite.provider.library.AstrixLibraryProvider;

@MetaInfServices
public class AstrixLibraryProviderPlugin implements AstrixServiceProviderPlugin<AstrixLibraryProvider> {
	
	private Astrix astrix;
	
	public AstrixLibraryProviderPlugin() {
	}

	@Override
	public AstrixServiceProvider create(Class<?> descriptorHolder) {
		Object libraryProviderInstance = initInstanceProvider(descriptorHolder); // TODO: who manages lifecycle for the libraryProviderInstance?
		List<AstrixServiceFactory<?>> result = new ArrayList<>();
		for (Method m : descriptorHolder.getMethods()) {
			if (m.isAnnotationPresent(AstrixExport.class)) {
				result.add(new AstrixLibraryFactory<>(libraryProviderInstance, m, astrix));
			}
		}
		return new AstrixServiceProvider(result, descriptorHolder);
	}

	private Object initInstanceProvider(Class<?> descriptor) {
		try {
			return descriptor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Failed to init library provider: " + descriptor.getClass().getName(), e);
		}
	}

	@Override
	public Class<AstrixLibraryProvider> getProviderAnnotationType() {
		return AstrixLibraryProvider.class;
	}

	@Override
	public void setAstrix(Astrix astrix) {
		this.astrix = astrix;
	}

	@Override
	public void setPlugins(AstrixContext plugins) {
		
	}

}
