 /**
 * 
 */
package plugins.sonarqube_testplugin.measures;

import java.util.List;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import static java.util.Arrays.asList;

/**
 * Test plugin metrics definition.
 *
 * @author Kévin Valleau
 * @version 1.0
 */
public class TestMetrics implements Metrics {

    /**
     * Définition d'une métrique
     */
    public static final Metric<Float> COMPLEXITY_DENSITY =
        new Metric.Builder(
            "complexity_density",
            "Densité de complexité",
            Metric.ValueType.FLOAT)
            .setDescription("La densité de complexité (complexité cyclomatique / nombres de lignes de code)")
            .setDirection(Metric.DIRECTION_BETTER)
            .setQualitative(true)
            .setDomain(CoreMetrics.DOMAIN_COMPLEXITY)
            .create();

    public static final Metric<Integer> DEAD_CODE = 
    		new Metric.Builder(
    				"dead_code_measure", 
    				"Code mort", 
    				Metric.ValueType.INT)
    				.setDescription("Dead code based on rules for unused members, methods, classes, ...")
    				.setDirection(Metric.DIRECTION_WORST)
    				.setQualitative(false)
    				.setDomain(CoreMetrics.DOMAIN_GENERAL)
    				.create();
    /**
     * Définition de la liste des métriques.
     *
     * @return la liste des métriques du plugin
     */
    public List<Metric> getMetrics() {
        return asList(
        		COMPLEXITY_DENSITY, DEAD_CODE);
    }
}
