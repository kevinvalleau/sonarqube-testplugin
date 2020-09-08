/**
 * 
 */
package plugins.sonarqube_testplugin.settings;

import static java.util.Arrays.asList;

import java.util.List;

import org.sonar.api.config.PropertyDefinition;

/**
 * @author Kévin Valleau
 *
 */
public class WebAPITokenSettings {
	public static final String WEBAPI_TOKEN_KEY = "sonar.webapi.token";
	
	private WebAPITokenSettings () {}
	
	  public static List<PropertyDefinition> getProperties() {
		    return asList(PropertyDefinition.builder(WEBAPI_TOKEN_KEY)
		      .defaultValue("")
		      .category("General")
		      .subCategory("General")
		      .name("Token Web API")
		      .description("Token pour que les measures custom des plugins puissent accéder aux serveur via l'API REST")
		      .build());
		  }
}
