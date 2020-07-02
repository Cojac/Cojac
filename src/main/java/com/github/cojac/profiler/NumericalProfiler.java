package com.github.cojac.profiler;

import com.github.cojac.models.wrappers.SymbolicExpression;

import java.util.*;

public class NumericalProfiler {

   private static class RecommendationWithProfile {
      Recommendation recommendation;
      ProfileData profile;

      RecommendationWithProfile(Recommendation recommendation, ProfileData profile) {
         this.recommendation = recommendation;
         this.profile = profile;
      }
   }

   private final SortedMap<StackTraceElement, RecommendationWithProfile> profile;
   private final SortedMap<StackTraceElement, Recommendation> directMatches;
   private boolean throwInsteadOfPrint = false;

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
      profile = new TreeMap<>(compareByFilenameAndLine);
      directMatches = new TreeMap<>(compareByFilenameAndLine);

      Runtime.getRuntime().addShutdownHook(new Thread(this::onShutdown));
   }

   public void handle(SymbolicExpression expr) {
      for (Recommendation r : Recommendation.recommandations) {
         if (r.getPattern().match(expr)) {
            if (r.isDirectMatch()) {
               handleDirectMatch(r, expr);
            } else {
               handleProfiledMatch(r, expr);
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

   private void handleDirectMatch(Recommendation r, SymbolicExpression expr) {
      StackTraceElement el = getLine();
      directMatches.putIfAbsent(el, r);
      //System.out.println("Direct match : " + expr + " for recommendation " + r);
   }

   private void handleProfiledMatch(Recommendation r, SymbolicExpression expr) {
      StackTraceElement el = getLine();
      profile.putIfAbsent(el, new RecommendationWithProfile(r, new ProfileData()));
      //System.out.println(r.getLatestLeftValue() + " | " + r.getLatestRightValue());
      r.updateTracking(profile.get(el).profile);

      //System.out.println("Indirect match : " + expr + " for recommendation " + r);
   }

   public void onShutdown() {
      ThrowableRecommendationContainer recommendations = new ThrowableRecommendationContainer();

      for (Map.Entry<StackTraceElement, Recommendation> entry : directMatches.entrySet()) {
         StackTraceElement stack = entry.getKey();
         Recommendation recommendation = entry.getValue();
         recommendations.addRecommendation(new RecommendationReport(recommendation, stack));
      }

      for(Map.Entry<StackTraceElement, RecommendationWithProfile> entry : profile.entrySet()) {
         StackTraceElement stack = entry.getKey();
         ProfileData data = entry.getValue().profile;
         Recommendation recommendation = entry.getValue().recommendation;
         data.finish();

         if(recommendation.isRelevant(data)) {
            recommendations.addRecommendation(new RecommendationReport(recommendation, stack, data));
         } else {
            System.err.println("#### irrelevant ####");
            System.err.println(new RecommendationReport(recommendation, stack, data).toString());
         }
      }

      directMatches.clear();
      profile.clear();

      if(throwInsteadOfPrint) {
         if(!recommendations.getAllRecommendations().isEmpty())
            throw recommendations;
      } else {
         for(RecommendationReport recommendation : recommendations.getAllRecommendations()) {
            System.err.println(recommendation.toString());
         }
      }
   }

   public void setThrowInsteadOfPrint(boolean throwInsteadOfPrint) {
      this.throwInsteadOfPrint = throwInsteadOfPrint;
   }
}
