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
public class DeadCodeRulesSettings {
	  public static final String DEADCODE_RULES_KEY = "sonar.deadcode.rules";
	  public static final String DEADCODE_RULES_DEFAULT_VALUE = "csharpsquid:S1144,squid:S1481,squid:UselessImportCheck,squid:UnusedPrivateMethod,squid:UnusedProtectedMethod,squid:S1133,squid:S1068,squid:CallToDeprecatedMethod,cobol:COBOL.ParagraphEmptyCheck,cobol:S1461,findbugs:UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD,cobol:COBOL.FileUnusedCheck";

	  private DeadCodeRulesSettings() {
	    // only statics
	  }

	  public static List<PropertyDefinition> getProperties() {
	    return asList(PropertyDefinition.builder(DEADCODE_RULES_KEY)
	      .defaultValue(DEADCODE_RULES_DEFAULT_VALUE)
	      .category("Code mort")
	      .name("Règles pour code mort")
	      .description("Liste de règles séparées par des virgules pour les métriques de code mort.")
	      .build());
	  }

}
