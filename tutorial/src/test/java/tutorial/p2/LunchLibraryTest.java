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
package tutorial.p2;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;

import tutorial.p2.api.LunchRestaurantFinder;
import tutorial.p2.api.LunchSuggester;

import com.avanza.astrix.context.AstrixConfigurer;
import com.avanza.astrix.context.AstrixContext;
import com.avanza.astrix.context.AstrixDirectComponent;

public class LunchLibraryTest {
	
	private AstrixContext astrix;
	
	@After
	public void after() {
		astrix.destroy();
	}
	
	@Test
	public void astrixDirectComponentAllowsBindingToObjectsInSameProcess() throws Exception {
		LunchRestaurantFinder restaurantFinder = Mockito.mock(LunchRestaurantFinder.class);
		String serviceUri = AstrixDirectComponent.registerAndGetUri(LunchRestaurantFinder.class, restaurantFinder);

		AstrixConfigurer configurer = new AstrixConfigurer();
		configurer.set("restaurantFinderUri", serviceUri);
		configurer.setBasePackage("tutorial.p2");
		astrix = configurer.configure();
		
		LunchSuggester lunchSuggester = astrix.getBean(LunchSuggester.class);

		Mockito.stub(restaurantFinder.getAllRestaurants()).toReturn(Arrays.asList("Pontus!"));
		assertEquals("Pontus!", lunchSuggester.randomLunchRestaurant());
	}

}