# Part 2 - Service Binding
The first part of the tutorial introduced how libraries can be created and consumed with Astrix. In this part we will introduce another type of api, the heart in all service based architectures, namely the service.

In this context, a service is something that is typically provided by a different process. Therefore, in order to consume a service, Astrix must first bind to the service, which is done in two steps:

1. Locate a provider of the given service. This involves identifying what `AstrixServiceComponent` to use to bind to the service and find all properties required by the `AstrixServiceComponent` to bind to the service.
2. Use the `AstrixServiceComponent` to bind to the given service.

There are two degrees of freedom involved here for a service provider. The first one is how a service is located. Out of the box Astrix supports two mechanisms for locating a service. One using configuration, `@AstrixConfigApi`, and another using the service-registry, `@AstrixServiceRegistryApi`, which will be introduced later. The second degree of freedom is how Astrix binds to a service, which done by the `AstrixServiceComponent`. Astrix comes with a number of service-component implementations out of the box which will be introduced throughout the tutorial. 

Astrix has an extendible service-binding mechanism through the `AstrixServiceComponent` interface. As an api-developer or consumer, you never use the `AstrixServiceComponent` directly. Rather its used behind the scenes by Astrix to bind to a provider of a given service. However, even if you don't intend to implement you own service-component it's good to have knowledge about the inner workings of service binding.

### Service binding using AstrixDirectComponent and @AstrixConfigApi
The first service-component we will cover is the `AstrixDirectComponent` which is a useful tool to support testing. It allows binding to a service provider within the same process, i.e. an ordinary object within the same jvm. The next example introduces the `AstrixDirectComponent` and `@AstrixConfigApi`.

In this example the api i split into one library, `LunchSuggester`, and one service, `LunchRestaruantFinder`. The api-descriptor exporting the `LunchRestaurantFinder` tells Astrix that the service should be located using configuration, and that the configuration entry is "restaurantFinderUri".

```java
public interface LunchSuggester {
	String randomLunchRestaurant();
}

@AstrixLibraryProvider
public class LunchLibraryProvider {
	@AstrixExport
	public LunchSuggester lunchSuggester(LunchRestaurantFinder restaurantFinder) {
		return new LunchSuggesterImpl(restaurantFinder);
	}
}
```
 

```java
public interface LunchRestaurantFinder {
	List<String> getAllRestaurants();
}

@AstrixConfigApi(
	exportedApis = LunchRestaurantFinder.class,
	entryName = "restaurantFinderUri"
)
public class LunchServiceProvider {
}
```

A unit test for the library api might look like this: 

```java
public class LunchLibraryTest {
	
	private AstrixContext astrix;
	
	@After
	public void after() {
		astrix.destroy();
	}
	
	@Test
	public void astrixDirectComponentAllowsBindingToObjectsInTheSameProcess() throws Exception {
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
```

In the test we want to stub out the `LunchRestaurantFinder` using Mockito. This could easily be done using the `TestAstrixConfigurer` introduced in part 1, but for the sake of illustration we will use the `AstrixDirectComponent`. We register the mock with the `AstrixDirectComponent` which returns a serviceUri. A serviceUri consists of two parts. The first part identifies the name of the service component, in this case `"direct"`. The second part is service-component specific and contains all properties required by the service-component to bind to the given service. `AstrixDirectComponent` requires an identifier for the target instance which is generated when we register the instance with the `AstrixDirectComponent`. Therefore a serviceUri identifying a service provided using the `AstrixDirectComponent` might look like this: `"direct:21"`.

When we configure Astrix we provide a setting, "restaurantFinderUri" with a value that contains the serviceUri to the `LunchRestaurantFinder` mock instance. When Astrix is requested to create an instance of `LunchRestaurantFinder` (which is done indirectly when we create the `LunchSuggester` bean in the test) the process goes like this:

1. Astrix sees that its a configuration-api (`@AstrixConfigApi`)
2. Astrix queries the configuration for the entry name defined by the annotation ("restaurantFinderUri") to get the serviceUri, lets say that its value is `"direct:21"`
3. Astrix parses the serviceUri to find what `AstrixServiceComponent` to use for binding, in this case `"direct"`
4. Astrix delegates service binding to `AstrixDirectComponent`, passing in all component-specific properties, in this case `"21"`
5. The `AstrixDirectComponent` queries its internal registry of objects and finds our mock instance and returns it


### Configuration
In the example above we use the configuration mechanism to locate the provider of `LunchRestaurantFinder`. The configuration mechanism in Astrix is hierarchical and an entry is resolved in the following order.

1. External configuration
2. Programmatic configuration set on AstrixConfigurer
3. META-INF/astrix/settings.properties
4. Default values

Astrix will used the first value found for a given setting. Hence the External Configuration takes precedence over the Programatic configuration and so on. The external configuration is pluggable by implementing ´AstrixExternalConfig´. By default Astrix will not use any external configuration.

TODO: configuration example?


### Stateful Astrix Beans
Every bean that is bound using an `AstrixServiceComponent` will be a "stateful" bean. `Astrix.getBean(BeanType.class)` always returns a proxy for a stateful bean (provided that there exists an api-descriptor exporting the given api). However, if the bean can't be bound the proxy will be in UNBOUND state in which it will throw a `ServiceUnavailableException` upon each invocation. Astrix will periodically attempt to bind the bean until successful.

The following example illustrates how a astrix-bean proxy goes from UNBOUND state to BOUND when a given service becomes available. It also illustrates usage of AsterixSettings as an external configuration provider which can be usefull in testing.

```java

public class AstrixBeanStateManagementTest {
	
	private AstrixSettings settings = new AstrixSettings();
	private AstrixContext astrix;
	
	@After
	public void after() {
		astrix.destroy();
	}
	
	@Test
	public void astrixManagesStateForEachServiceBean2() throws Exception {
		AstrixConfigurer configurer = new AstrixConfigurer();
		configurer.set(AstrixSettings.BEAN_REBIND_ATTEMPT_INTERVAL, 10); // 1.
		configurer.set(AstrixSettings.ASTRIX_CONFIG_URI, settings.getExternalConfigUri()); // 2.
		configurer.setBasePackage("tutorial.p2");
		astrix = configurer.configure();
		
		LunchSuggester lunchSuggester = astrix.getBean(LunchSuggester.class);

		try {
			lunchSuggester.randomLunchRestaurant(); // 3.
		} catch (ServiceUnavailableException e) {
			// No service available
		}
		
		LunchRestaurantFinder restaurantFinder = Mockito.mock(LunchRestaurantFinder.class);

		String serviceUri = AstrixDirectComponent.registerAndGetUri(LunchRestaurantFinder.class, restaurantFinder); // 4.
		settings.set("restaurantFinderUri", serviceUri); // 5.
		
		astrix.waitForBean(LunchSuggester.class, 2000); // 6.
		
		Mockito.stub(restaurantFinder.getAllRestaurants()).toReturn(Arrays.asList("Pontus!"));
		
		assertEquals("Pontus!", lunchSuggester.randomLunchRestaurant()); // 7.
	}
}

```

1. The BEAN_REBIND_ATTEMPT_INTERVAL determines how often Astrix will attempt to bind a given bean (millis).
2. The ASTRIX_CONFIG_URI provides a service-like uri to the external configuration source, in this case an instance of AstrixSettings.
3. Since the LunchSuggester uses LunchRestaurantFinder in background but currently configuration doesn't not contain a 'restarurantFinderUri'it will be in state UNBOUND
4. Register a mock instance for LunchRestaurantFinder in the direct-component
5. Add restaurantFinderUri entry to configuration pointing to the mock
6. Astrix allows us to wait for a bean to be bound. Note that we are waiting for a Library. Astrix is clever and detects that the library uses the LunchRestaurantFinder and therefore waits until the LunchRestaurantFinder is bound
7. Invoke the `LunchSuggester` library, which in turn `LunchRestaurantFinder` service which will be BOUND at this time.




TODO:

### Service Binding

* Illustrate service-binding with AstrixConfigApi and DirectComponent (DONE)
* Illustrate that a service must not be available when bean is created (DONE)
* Illustrate waiting for library bean to be bound (DONE)
* GsComponent


### Service Registry (Service discovery)
* Dynamic service discovery
* Relies on service-binding using AstrixServiceComponent

### Astrix configuration
* Extension points (DONE)
* Default hierarchy (DONE)