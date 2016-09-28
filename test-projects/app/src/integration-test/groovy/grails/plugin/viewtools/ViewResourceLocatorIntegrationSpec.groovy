package grails.plugin.viewtools

import grails.test.mixin.integration.Integration
import grails.transaction.*
import grails.util.Environment
//import org.grails.web.servlet.mvc.GrailsWebRequest
//import org.grails.web.util.GrailsApplicationAttributes
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.util.StringUtils
import spock.lang.*

import org.springframework.core.io.Resource
import foobar.DemoController

//@Integration
@Rollback
class ViewResourceLocatorIntegrationSpec extends Specification  {

	ViewResourceLocator viewResourceLocator
	DemoController controller
    def grailsApplication

    StringWriter writer = new StringWriter()

    def setup() {
//        GrailsWebEnvironment.bindRequestIfNull(grailsApplication.mainContext,writer)
//        controller = new foobar.FooPluginController()
//        controller.viewResourceLocator = viewResourceLocator
    }

     def cleanup() {
     }

    void "simple find"() {
        //the classpath:customeviews should be setup in the bean
        when:
        Resource res = viewResourceLocator.locate('/foo/index.md')

        then:
        assert res
        res.getURI().toString().endsWith("/foo/index.md")

    }

     void "find override on external search path"() {
         //the classpath:customeviews should be setup in the bean
         when:
         Resource res = viewResourceLocator.locate('/foo/override.md.ftl')

         then:
         assert res
         res.getURI().toString().endsWith("view-templates/foo/override.md.ftl")

     }

     void "test classpath conf/customviews/customview.hbr"() {
         //the classpath:customeviews should be setup in the bean
         when:
         Resource res = viewResourceLocator.locate('testAppViewToolsGrailsAppConf.hbr')

         then:
         assert res
         res.getURI().toString().endsWith( "testAppViewToolsGrailsAppConf/testAppViewToolsGrailsAppConf.hbr")

     }

    void "view in plugin from classpath"() {
        when:
        Resource res = viewResourceLocator.locate('/fooPlugin/index.md')

        then:
        //its a full scan in grails 2
        assert res.exists()
        //works in grails3
        //res.getURI().toString().endsWith( "classpath:/fooPlugin/index.md")
        assert viewResourceLocator.locate('spock/lang/Specification.class')
    }

    void "view with plugin controller in request"() {
        when:
        //println Environment.grailsVersion
        grailsApplication.mainContext.getBeanDefinitionNames().each {
            println it
        }
        GrailsWebEnvironment.bindRequestIfNull(grailsApplication.mainContext, writer)
        def controller = grailsApplication.mainContext.getBean("foobar.FooPluginController")
        def request = GrailsWebRequest.lookup()?.getCurrentRequest()
        request.setAttribute(GrailsApplicationAttributes.CONTROLLER, controller)
        //controller.viewResourceLocator = viewResourceLocator
        Resource res = viewResourceLocator.locate('/fooPlugin/index.md')

        then:
        String uri = StringUtils.cleanPath(res.getURI().toString())
        String toCompare = "foobar-plugin/grails-app/views/fooPlugin/index.md"
        String toCompare2 = "foobar-plugin-0.1.jar!/fooPlugin/index.md"
        uri.endsWith( toCompare) || uri.endsWith( toCompare2)
    }

    void "view using plugin path"() {
        when:
        //controller.viewResourceLocator = viewResourceLocator
        Resource res = viewResourceLocator.locate('/plugins/foobar-plugin-0.1/fooPlugin/index.md')

        then:
        String uri = StringUtils.cleanPath(res.getURI().toString())
        String toCompare = "foobar-plugin/grails-app/views/fooPlugin/index.md"
        String toCompare2 = "foobar-plugin-0.1.jar!/fooPlugin/index.md"
        uri.endsWith( toCompare) || uri.endsWith( toCompare2)
    }


}
