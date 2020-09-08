/**
 * 
 */
package plugins.sonarqube_testplugin.measures;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Issue;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.api.CoreProperties;

import org.sonarqube.ws.Rules;
import org.sonarqube.ws.Rules.SearchResponse;
import org.sonarqube.ws.Rules.SysTags;
import org.sonarqube.ws.client.HttpConnector;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.WsClientFactories;
import org.sonarqube.ws.client.rules.SearchRequest;

import static plugins.sonarqube_testplugin.measures.TestMetrics.DEAD_CODE;
import static plugins.sonarqube_testplugin.settings.DeadCodeRulesSettings.DEADCODE_RULES_KEY;
import static plugins.sonarqube_testplugin.settings.WebAPITokenSettings.WEBAPI_TOKEN_KEY;
/**
 * @author Kévin Valleau
 *
 */

public class DeadCodeMeasureComputer
  implements MeasureComputer
{
	private static final Logger LOG = Loggers.get(DeadCodeMeasureComputer.class);
	private final List<String> localList;
	private final String defaultServerURL = "http://localhost:9000";

	
	
  // On va chercher la liste des règles dans les paramètres de sonarqube
  private final org.sonar.api.config.Configuration settings;
  
  public DeadCodeMeasureComputer(org.sonar.api.config.Configuration paramSettings)
  {
    settings = paramSettings;
    
    // On remplit la liste par défaut avec le paramètre de code mort
    // On mettra les règles de base extraites du JSON
    //TODO aller chercher la liste des regles dans le JSON via l'application console
   /* String str1 = paramSettings.get(DEADCODE_RULES_KEY).get();
    if (str1 == null) {
      str1 = "squid:S1481,squid:UselessImportCheck,squid:UnusedPrivateMethod,squid:UnusedProtectedMethod,squid:S1133,squid:S1068,squid:CallToDeprecatedMethod,cobol:COBOL.ParagraphEmptyCheck,cobol:S1461,findbugs:UUF_UNUSED_PUBLIC_OR_PROTECTED_FIELD,cobol:COBOL.FileUnusedCheck";
    }
    LOG.trace("Dead Code Rules from settings: " + str1);
    String[] arrayOfString1 = str1.split(",");
    localList = new ArrayList<String>();
    for (String str2 : arrayOfString1) {
    	localList.add(str2);
    }*/

    // On essaie de récupérer les paramètres en premier
	  
		  String urlServeur = paramSettings.get(CoreProperties.SERVER_BASE_URL).get();
		  if (urlServeur.isEmpty()) {
			  urlServeur = defaultServerURL;
		  }
		  String forceAuth = paramSettings.get(CoreProperties.CORE_FORCE_AUTHENTICATION_PROPERTY).get();
		  String tokenWebApi = paramSettings.get(WEBAPI_TOKEN_KEY).get();
		  // ça ça fonctionne
		  LOG.info("url du serveur " + urlServeur);
		  LOG.info("Token pour l'accès " + tokenWebApi);
		  LOG.info("Authentification forcée :" + forceAuth);

  
    // mais la liste locale des r`gles n'Est pas récupérée par la méthode compute
	  localList = getWebDeadCodeRules(urlServeur, forceAuth, tokenWebApi);
  }
  
	/**
	 * Méthode de définition
	 */
  public MeasureComputer.MeasureComputerDefinition define(MeasureComputer.MeasureComputerDefinitionContext paramMeasureComputerDefinitionContext)
  {
    MeasureComputer.MeasureComputerDefinition.Builder localBuilder = paramMeasureComputerDefinitionContext.newDefinitionBuilder();
    localBuilder = localBuilder.setOutputMetrics(DEAD_CODE.key());
    
    return localBuilder.build();
  }
  
  /**
   * Méthode de calcul
   */
  public void compute(MeasureComputer.MeasureComputerContext paramMeasureComputerContext)
  {
	//LOG.info("Exécute le calcul du code mort...");
    LOG.info("regles " + localList.get(0));
    int mesureCodeMort = 0;
    Component composant = paramMeasureComputerContext.getComponent();
    
    // Si c'est un fichier on cumule le nombre d'issues de code mort pour le fichier
    if ((composant).getType().equals(Component.Type.FILE))
    {
      Iterator<? extends Issue> localIterator = paramMeasureComputerContext.getIssues().iterator();
      while (localIterator.hasNext())
      {
        Issue issue = (Issue)localIterator.next();
        if ((localList.contains((issue).ruleKey().toString())) && (issueIsNotResolved(issue)))
        {
          mesureCodeMort++;
        }
      }
    }
    else
    {
      Iterator<Measure> localIterator = paramMeasureComputerContext.getChildrenMeasures(DEAD_CODE.key()).iterator();
      while (localIterator.hasNext())
      {
        Measure mesure = (Measure)localIterator.next();
        mesureCodeMort += mesure.getIntValue();
      }
    }
	LOG.info("Fin du calcul du code mort..." + composant.getKey() + ((Integer)mesureCodeMort).toString());
    paramMeasureComputerContext.addMeasure(DEAD_CODE.key(), ((Integer)mesureCodeMort).intValue());
  }
  
  private boolean issueIsNotResolved(Issue paramIssue)
  {
    return paramIssue.resolution() == null;
  }
  
 public List<String> getWebDeadCodeRules(String urlServeur, String authForce, String token) {
	 
	 LOG.info("recupere les regles depuis le web");
	 ArrayList<String> localArrayList = new ArrayList<String>();
	 HttpConnector httpConnector;
	 if (authForce.equals("true")) {
		 LOG.info("Connexion authentifiéee");
		 httpConnector = HttpConnector.newBuilder().token(token).url(urlServeur).build();
	 } else {
		 LOG.info("Connexion anonyme");
		 httpConnector = HttpConnector.newBuilder().url(urlServeur).build();
	 }

	 
	 LOG.info("crée la connexion");
     WsClient wsClient = WsClientFactories.getDefault().newClient(httpConnector);
     SearchRequest searchWsRequest = new SearchRequest();
     
     searchWsRequest.setActivation("true"); 
     searchWsRequest.setTags(asList("unused"));
     
     LOG.info("Lance la recherche");
     SearchResponse response = wsClient.rules().search(searchWsRequest);
     
     
     LOG.info("Lit le résultat");
     List<Rules.Rule> issuesList = response.getRulesList();
     for (Rules.Rule rule : issuesList) {
   	  if (rule.hasTags()) {
   		  
   		  SysTags tag = rule.getSysTags();
   		  if (tag.getSysTagsList().contains("unused")) {
   			  //System.out.println("unused " + rule.getKey().toString());
   			  localArrayList.add(rule.getKey());
   		  } /*else {
   			  
   			  System.out.println(rule.getKey().toString());
   			  for (String tg :tag.getSysTagsList()) {
   				  System.out.println(tg);
   			  }
   		  }*/
   	  }
         
     }
     return localArrayList;
 }

}
