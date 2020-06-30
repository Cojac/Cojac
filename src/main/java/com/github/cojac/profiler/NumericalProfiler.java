package com.github.cojac.profiler;

import com.github.cojac.models.wrappers.SymbolicExpression;

import java.util.*;

public class NumericalProfiler {

   private static class RecommendationWithProfile {
      Recommendation recommendation;
      ProfileData profile;

      public RecommendationWithProfile(Recommendation recommendation, ProfileData profile) {
         this.recommendation = recommendation;
         this.profile = profile;
      }
   }

   private final SortedMap<StackTraceElement, RecommendationWithProfile> profile;
   private final SortedMap<StackTraceElement, Recommendation> directMatches;

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
      for (Map.Entry<StackTraceElement, Recommendation> entry : directMatches.entrySet()) {
         StackTraceElement stack = entry.getKey();
         Recommendation recommendation = entry.getValue();
         System.out.println(stack.toString() + " : " + recommendation.getRecommendation());
      }

      for(Map.Entry<StackTraceElement, RecommendationWithProfile> entry : profile.entrySet()) {
         StackTraceElement stack = entry.getKey();
         ProfileData data = entry.getValue().profile;
         Recommendation recommendation = entry.getValue().recommendation;
         data.finish();

         if(recommendation.isRelevant(data)) {
            System.out.println(stack.toString() + " : " + recommendation.getRecommendation());
            System.out.println("\t" + data.toString());
         } else {
            System.out.println("not relevant");
            System.out.println("\t" + stack.toString() + " : " + recommendation.getRecommendation());
            System.out.println("\t\t" + data.toString());
         }
      }

      directMatches.clear();
      profile.clear();
   }
}
