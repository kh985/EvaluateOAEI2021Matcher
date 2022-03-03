package com.example.EvaluateOAEImatcher;

import de.uni_mannheim.informatik.dws.melt.matching_base.external.http.MatcherHTTPCall;
import de.uni_mannheim.informatik.dws.melt.matching_base.external.docker.MatcherDockerFile;
import de.uni_mannheim.informatik.dws.melt.matching_base.external.seals.MatcherSeals;
import de.uni_mannheim.informatik.dws.melt.matching_data.TrackRepository;
import de.uni_mannheim.informatik.dws.melt.matching_eval.ExecutionResultSet;
import de.uni_mannheim.informatik.dws.melt.matching_eval.Executor;
import de.uni_mannheim.informatik.dws.melt.matching_eval.evaluator.EvaluatorCSV;
import eu.sealsproject.platform.res.domain.omt.IOntologyMatchingToolBridge;
import de.uni_mannheim.informatik.dws.melt.matching_jena_matchers.external.matcher.SimpleStringMatcher;

import java.net.URI;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {
    
    
	//docker matchers
        MatcherDockerFile dockerMatcher1 = new MatcherDockerFile("alod2vecmatcher-1.0-web", new File("/home/omaima/OM2021systems/WEBbased/alod2vecmatcher-1.0-web-latest.tar.gz"));
        MatcherDockerFile dockerMatcher2 = new MatcherDockerFile("atmatcher-1.0-web", new File("/home/omaima/OM2021systems/WEBbased/atmatcher-1.0-web-latest.tar.gz"));
        
        MatcherDockerFile dockerMatcher3 = new MatcherDockerFile("logmap-melt-oaei-2021-web", new File("/home/omaima/OM2021systems/WEBbased/logmap-melt-oaei-2021-web-latest.tar.gz"));
        MatcherDockerFile dockerMatcher4 = new MatcherDockerFile("lsmatch-1.0-web", new File("/home/omaima/OM2021systems/WEBbased/lsmatch-1.0-web-latest.tar.gz"));
        
        MatcherDockerFile dockerMatcher5 = new MatcherDockerFile("wiktionarymatcher-1.0-web", new File("/home/omaima/OM2021systems/WEBbased/wiktionarymatcher-1.0-web-latest-003.tar.gz")); 
        
        
	//Seals matchers
        String java8command = "/usr/lib/jvm/java-8-openjdk-amd64/bin/java";
      
        MatcherSeals sealsMatcher1 = new MatcherSeals(new File("/home/omaima/OM2021systems/SealsMatchers/AMD"));
        sealsMatcher1.setJavaCommand(java8command);

        MatcherSeals sealsMatcher2 = new MatcherSeals(new File("/home/omaima/OM2021systems/SealsMatchers/AML.zip"));
        sealsMatcher2.setJavaCommand(java8command);


        MatcherSeals sealsMatcher3 = new MatcherSeals(new File("/home/omaima/OM2021systems/SealsMatchers/KGMatcher.zip"));
        sealsMatcher3.setJavaCommand(java8command);
        
        
	//Web matcher
	URI matcherServiceUri = new URI("http://ec2-18-207-252-203.compute-1.amazonaws.com/match/runmatcher_web_file");
        MatcherHTTPCall matcherURI = new MatcherHTTPCall(matcherServiceUri, true);
	    
	//Baseline matcher
        SimpleStringMatcher Baseline = new SimpleStringMatcher();

	//adding all matchers to a map (key: matcher name, value: matcher instance)
        Map<String, IOntologyMatchingToolBridge> matchers = new HashMap<>();
        
        
        matchers.put("alod2vecmatcher", dockerMatcher1);
        matchers.put("atmatcher", dockerMatcher2);
        matchers.put("logmap-melt", dockerMatcher3);
        matchers.put("lsmatch", dockerMatcher4);
        matchers.put("wiktionarymatcher", dockerMatcher5);
        matchers.put("AMD", sealsMatcher1);
        matchers.put("AML", sealsMatcher2);
        matchers.put("KGMatcher", sealsMatcher3);	
	matchers.put("ONTmap", matcherURI);
	matchers.put("Baseline", Baseline);

        
        //running the matcher on any task
        ExecutionResultSet results = Executor.run(TrackRepository.Knowledgegraph.CommonKG.getTestCase("nell-dbpedia"), matchers);


        Thread.sleep(20000); // just to be sure that all logs are written.
        
        
        //evaluating all systems
        EvaluatorCSV evaluatorCSV = new EvaluatorCSV(results);

        
        //closing docker containers
        dockerMatcher1.close();
	dockerMatcher2.close();
	dockerMatcher3.close();
	dockerMatcher4.close();
	dockerMatcher5.close();

        //writing evaluation results to disk
        evaluatorCSV.writeToDirectory();
    }
}
