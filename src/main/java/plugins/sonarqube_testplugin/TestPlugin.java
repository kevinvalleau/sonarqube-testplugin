/**
 * 
 */
package plugins.sonarqube_testplugin;


import org.sonar.api.Plugin;
import plugins.sonarqube_testplugin.measures.*;
import plugins.sonarqube_testplugin.settings.DeadCodeRulesSettings;
import plugins.sonarqube_testplugin.settings.WebAPITokenSettings;


/**
 * @author Kévin Valleau
 *
 */
public class TestPlugin implements Plugin {


	@Override
	public void define(Context context) {

		// Ajout des settings
		context.addExtensions(DeadCodeRulesSettings.getProperties());
		context.addExtensions(WebAPITokenSettings.getProperties());
	    // tutorial on measures
	    context
	      .addExtensions(TestMetrics.class, 
	    		  DensityComplexityMeasureComputer.class,
	    		  DeadCodeMeasureComputer.class, TestSensor.class);

	   
	  }
		
	}

