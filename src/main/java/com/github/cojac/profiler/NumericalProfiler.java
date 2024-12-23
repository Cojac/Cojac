package com.github.cojac.profiler;

import com.github.cojac.models.wrappers.SymbolicExpression;

import java.util.*;

public class NumericalProfiler {

   private static class RecommendationWithProfile {
      final Recommendation recommendation;
      final ProfileData profile;

      RecommendationWithProfile(Recommendation recommendation, ProfileData profile) {
         this.recommendation = recommendation;
         this.profile = profile;
      }
   }

   private final SortedMap<StackTraceElement, RecommendationWithProfile> profile;
   private final SortedMap<StackTraceElement, Recommendation> directMatches;
   private boolean throwRecommendations = false;
   private boolean verbose = false;
   private static final String VERBOSE_PREFIX = "|\t";

   private static final Comparator<StackTraceElement> compareByFilenameAndLine =
           Comparator.comparing(StackTraceElement::getFileName).thenComparingInt(StackTraceElement::getLineNumber);

   private static NumericalProfiler instance = null;

   public static NumericalProfiler getInstance() {
      if(instance == null) {
         instance = new NumericalProfiler();
      }
      return instance;
   }

   private NumericalProfiler() {
      profile = Collections.synchronizedSortedMap(new TreeMap<>(compareByFilenameAndLine));
      directMatches = Collections.synchronizedSortedMap(new TreeMap<>(compareByFilenameAndLine));

      Runtime.getRuntime().addShutdownHook(new Thread(this::onShutdown));
   }

   public void handle(SymbolicExpression expr) {
      for (Recommendation r : Recommendation.recommandations) {
         SymbExpressionGoal goal;
         if ((goal = r.getPattern().match(expr)) != null) {
            if (r.isDirectMatch()) {
               handleDirectMatch(r);
            } else {
               handleProfiledMatch(r, goal);
            }
         }
      }
   }

   private static StackTraceElement getLine() {
      StackTraceElement[] stack = new Throwable().getStackTrace();
      for (StackTraceElement stackTraceElement : stack) {
         String s = stackTraceElement.getClassName();
         if (!s.startsWith("com.github.cojac.models") && !s.startsWith("com.github.cojac.profiler")) {
            return stackTraceElement;
         }
      }
      return null;
   }

   private void handleDirectMatch(Recommendation r) {
      StackTraceElement el = getLine();
      directMatches.putIfAbsent(el, r);
      //System.out.println("Direct match : " + expr + " for recommendation " + r);
   }

   private void handleProfiledMatch(Recommendation r, SymbExpressionGoal goal) {
      StackTraceElement el = getLine();
      profile.putIfAbsent(el, new RecommendationWithProfile(r, new ProfileData()));
      //System.out.println(r.getLatestLeftValue() + " | " + r.getLatestRightValue());
      r.updateTracking(profile.get(el).profile, goal);
      //System.out.println("Indirect match : " + expr + " for recommendation " + r);
   }

   public void onShutdown() {
      ThrowableRecommendationContainer recommendations = new ThrowableRecommendationContainer();
      synchronized (directMatches) {
         for (Map.Entry<StackTraceElement, Recommendation> entry : directMatches.entrySet()) {
            StackTraceElement stack = entry.getKey();
            Recommendation recommendation = entry.getValue();
            RecommendationReport report = new RecommendationReport(recommendation, stack);
            recommendations.addRecommendation(report);
            System.err.println(report);
         }
      }
      synchronized (profile) {
         for (Map.Entry<StackTraceElement, RecommendationWithProfile> entry : profile.entrySet()) {
            StackTraceElement stack = entry.getKey();
            ProfileData data = entry.getValue().profile;
            Recommendation recommendation = entry.getValue().recommendation;
            data.finish();

            RecommendationReport reprot = new RecommendationReport(recommendation, stack, data);
            if (recommendation.isRelevant(data)) {
               recommendations.addRecommendation(reprot);
               System.err.println(reprot.toString());
            } else {
               if(verbose) {
                  System.err.println("#### irrelevant :");
                  String str = reprot.toString();
                  // add a prefix to each line of the recommendation
                  str = VERBOSE_PREFIX + str.replaceAll("(\r\n|\n)", "$1" + VERBOSE_PREFIX);
                  str = str.substring(0, str.length() - VERBOSE_PREFIX.length());
                  System.err.println(str);
               }
            }

         }
      }

      directMatches.clear();
      profile.clear();

      if(throwRecommendations) {
         if(!recommendations.getAllRecommendations().isEmpty())
            throw recommendations;
      }
   }

   public void setThrowRecommendations(boolean throwRecommendations) {
      this.throwRecommendations = throwRecommendations;
   }

   public void setVerbose(boolean verbose) {
      this.verbose = verbose;
   }
}
