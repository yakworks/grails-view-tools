## Description
utility helpers to locate views in the spring mvc context
- **ViewResourceLocator** for locating views in grails-app/views, plugins, and custom external paths.
- **GrailsWebEnvironment** for binding a mock request is one doesn't exist so that services can operate without a controller.

Used to locate View resources whether in development or WAR deployed mode from static
resources, custom resource loaders and binary plugins.
Loads from a local grails-app folder for dev and from WEB-INF in
development mode.

###ViewResourceLocator 
**Example Bean**
```groovy
viewResourceLocator(grails.plugin.viewtools.ViewResourceLocator) { bean ->
    //initial searchLocations
    searchLocations = [
        "classpath:templates/", // consistent with spring-boot defaults
        "file:/someLoc/my-templates/"
    ] 

    searchBinaryPlugins = false //whether to look in binary plugins, does not work in grails2

    // in dev mode there will be a groovyPageResourceLoader 
    // with base dir set to the running project
    //if(Environment.isDevelopmentEnvironmentAvailable()) <- better for Grails 3
    if(!application.warDeployed){ // <- grails2
        resourceLoader = ref('groovyPageResourceLoader') //adds to list, does not replace
    }

}
```

- **Resource locate(String uri)** : is the primary method and is used to find a view resource for a path. For example /foo/bar.xyz will search for /WEB-INF/grails-app/views/foo/bar.xyz in production and grails-app/views/foo/bar.xyz at development time. It also uses the the controller if called from a plugin to figure out where its located and finally does a brute force locate. Most of the logic is based on and uses what Grail's DefaultGroovyPageLocator does.
- **Resource getResource(String uri)** : also implements Springs [ResourceLoader](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/io/ResourceLoader.html) interface. This method works like a normal ResoruceLoader and **uri** can start with the standard _file:, classpath:, etc_


###GrailsWebEnvironment
GrailsWebEnvironment.bindRequestIfNull() methods are the ones of interest.
based on the RenderEnvironment in grails-rendering and private class in grails-mail
All this does is bind a mock request and mock response is one doesn't exist
deals with setting the WrappedResponseHolder.wrappedResponse as well


