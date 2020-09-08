/**
 * 
 */
package plugins.sonarqube_testplugin.measures;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import static plugins.sonarqube_testplugin.measures.TestMetrics.DEAD_CODE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static java.util.Arrays.asList;

/**
 * @author Kévin Valleau
 *
 */
public class TestSensor implements Sensor {
	private static final Logger LOG = Loggers.get(TestSensor.class);
	private final CheckFactory checkFactory;
	private Checks checks;
	// La checkFactory pour vérifier les règles est disponible en injection
	public TestSensor(CheckFactory checkFactory) {
		this.checkFactory = checkFactory;
	}
	
	@Override
	  public void describe(SensorDescriptor descriptor) {
	    descriptor.name("Sensor de test pour voir comment ça fonctionne");
	  }

	 public List<String> getRules(SensorContext context) {
		 LOG.info("Recupere les regles");
		 List<String> rulesUnused = new ArrayList<String>();
		 Iterator<ActiveRule> itrules = context.activeRules().findAll().iterator();
		 while(itrules.hasNext()) {
			 ActiveRule rule = (ActiveRule)itrules.next();
			 LOG.info("regle " + rule.ruleKey().toString());
			 //En fait c'est vraiment les paramètres nécessaires genre max de truc etc et pas du tout les tags
			 
			 for (String key : rule.params().keySet()) {
				 LOG.info("   " + key + " : " + rule.param(key).toString());
			 }
			 /*if (rule.param("tags") !=null && rule.param("tags").contains("unused")) {
				 LOG.info("regle unused" + rule.ruleKey().toString());
				 rulesUnused.add(rule.ruleKey().toString());
			 }*/
		 }
		 
		 return rulesUnused;
	 }
	
	  @Override
	  public void execute(SensorContext context) {
		  try {
			  String urlServer = context.config().get("sonar.core.serverBaseURL").toString();
			  // Ça ça fonctionne
			  LOG.info("url du serveur " + urlServer);
			 // List<String> regles = getRules(context);
		  }catch(Exception ex) {
			 LOG.info("paramètre vide"); 
		  }
	
	
	    FileSystem fs = context.fileSystem();
	    // only "main" files, but not "tests"
	    
	    Iterable<InputFile> files = fs.inputFiles(fs.predicates().hasType(InputFile.Type.MAIN));
	    for (InputFile file : files) {
	    	
	      context.<Integer>newMeasure()
	        .forMetric(DEAD_CODE)
	        .on(file)
	        .withValue(file.file().getName().length())
	        .save();
	    }
	    
    
	  }
}
