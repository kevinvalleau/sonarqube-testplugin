 /**
 * 
 */
package plugins.sonarqube_testplugin.measures;

import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.measures.CoreMetrics;

import static plugins.sonarqube_testplugin.measures.TestMetrics.COMPLEXITY_DENSITY;
/**
 * @author Kévin Valleau
 * @version 1.0
 *
 */
public class DensityComplexityMeasureComputer implements MeasureComputer {
  
	/**
	 * Méthode de définition
	 */
	  public MeasureComputerDefinition define(MeasureComputer.MeasureComputerDefinitionContext paramMeasureComputerDefinitionContext)
	  {
	    MeasureComputer.MeasureComputerDefinition.Builder localBuilder = paramMeasureComputerDefinitionContext.newDefinitionBuilder();
	    // On importe les mesures dont on a besoin
	    // voir https://docs.sonarqube.org/latest/user-guide/metric-definitions/
	    // On importe les définitions des métriques standard à l'aide de coremetrics
	    localBuilder.setInputMetrics(CoreMetrics.NCLOC_KEY, CoreMetrics.COMPLEXITY_KEY);
	    
	    // On donne les mesures de sortie
	    localBuilder.setOutputMetrics(COMPLEXITY_DENSITY.key());
	    
	    
	    return localBuilder.build();
	  }
	  
	  /**
	   * Méthode de calcul
	   */
	  public void compute(MeasureComputer.MeasureComputerContext paramMeasureComputerContext)
	  {
		// Attention, sonarqube calcule le nombre physique de lignes et pas le nombre logique
		Measure nbLignesCode = paramMeasureComputerContext.getMeasure(CoreMetrics.NCLOC_KEY);
		Measure complexiteCyclomatique = paramMeasureComputerContext.getMeasure(CoreMetrics.COMPLEXITY_KEY);
		  
	    if (((nbLignesCode != null) && (complexiteCyclomatique != null)) && (nbLignesCode.getIntValue() != 0))
	    {
	    	// La complexité cyclomatique n'est pas calculée sur les tests
	    	Float mesure = Float.valueOf((float)complexiteCyclomatique.getIntValue() / nbLignesCode.getIntValue());
 	        paramMeasureComputerContext.addMeasure(COMPLEXITY_DENSITY.key(), mesure.floatValue());
	    }
	  }
}
