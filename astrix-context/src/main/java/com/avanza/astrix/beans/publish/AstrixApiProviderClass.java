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
package com.avanza.astrix.beans.publish;

import java.lang.annotation.Annotation;
import java.util.Objects;
/**
 * 
 * @author Elias Lindholm (elilin)
 *
 */
public final class AstrixApiProviderClass {
	
	public static AstrixApiProviderClass create(Class<?> providerClass) {
		return new AstrixApiProviderClass(providerClass);
	}
	private Class<?> providerClass;

	private AstrixApiProviderClass(Class<?> annotationHolder) {
		this.providerClass = annotationHolder;
	}
	
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		return providerClass.isAnnotationPresent(annotationClass);
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return providerClass.getAnnotation(annotationClass);
	}
	
	public String getName() {
		return this.providerClass.getName();
	}

	public Class<?> getProviderClass() {
		return providerClass;
	}

	@Override
	public int hashCode() {
		return Objects.hash(providerClass);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AstrixApiProviderClass other = (AstrixApiProviderClass) obj;
		return Objects.equals(providerClass, other.providerClass);
	}
	
	@Override
	public final String toString() {
		return getName();
	}
	
}