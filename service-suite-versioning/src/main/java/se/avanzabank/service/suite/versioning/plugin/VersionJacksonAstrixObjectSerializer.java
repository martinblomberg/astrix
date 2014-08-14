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
package se.avanzabank.service.suite.versioning.plugin;

import java.util.ArrayList;
import java.util.List;

import se.avanzabank.service.suite.context.AstrixObjectSerializer;
import se.avanzabank.service.suite.provider.versioning.AstrixJsonApiMigration;
import se.avanzabank.service.suite.provider.versioning.AstrixObjectMapperConfigurer;
import se.avanzabank.service.suite.provider.versioning.AstrixVersioned;
import se.avanzabank.service.suite.versioning.JsonObjectMapper;
import se.avanzabank.service.suite.versioning.VersionedJsonObjectMapper.VersionedObjectMapperBuilder;

public class VersionJacksonAstrixObjectSerializer implements AstrixObjectSerializer {

	private JsonObjectMapper objectMapper;
	private int version;

	public VersionJacksonAstrixObjectSerializer(AstrixVersioned versioningInfo) {
		Class<? extends AstrixJsonApiMigration>[] apiMigrationFactories = versioningInfo.apiMigrations();
		Class<? extends AstrixObjectMapperConfigurer> objectMapperConfigurerFactory = versioningInfo.objectMapperConfigurer();
		this.version = versioningInfo.version();
		this.objectMapper = buildObjectMapper(apiMigrationFactories, objectMapperConfigurerFactory);
	}

	private JsonObjectMapper buildObjectMapper(
			Class<? extends AstrixJsonApiMigration>[] apiMigrationFactories,
			Class<? extends AstrixObjectMapperConfigurer> objectMapperConfigurerFactory) {
		try {
			AstrixObjectMapperConfigurer astrixObjectMapperConfigurer = objectMapperConfigurerFactory.newInstance();
			List<AstrixJsonApiMigration> apiMigrations = new ArrayList<>();
			for (Class<? extends AstrixJsonApiMigration> apiMigrationFactory : apiMigrationFactories) {
				apiMigrations.add(apiMigrationFactory.newInstance());
			}
			VersionedObjectMapperBuilder objectMapperBuilder = new VersionedObjectMapperBuilder(apiMigrations);
			astrixObjectMapperConfigurer.configure(objectMapperBuilder);
			return JsonObjectMapper.create(objectMapperBuilder.build());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Failed to init JsonObjectMapper", e);
		} finally {
		}
	}

	@Override
	public <T> T deserialize(Object element, Class<T> type, int fromVersion) {
		return objectMapper.deserialize((String) element, type, fromVersion);
	}

	@Override
	public Object serialize(Object element, int version) {
		return objectMapper.serialize(element, version);
	}

	@Override
	public int version() {
		return version;
	}

}
