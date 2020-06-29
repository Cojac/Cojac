package com.github.cojac.profiler;

import com.github.cojac.models.Reactions;
import com.github.cojac.models.wrappers.SymbolicExpression;

import java.util.*;

public class NumericalProfiler {

   private final SortedMap<StackTraceElement, ProfileData> profile;

   private static NumericalProfiler instance = null;

   public static NumericalProfiler getInstance() {
      if(instance == null) {
         instance = new NumericalProfiler();
      }
      return instance;
   }

   private NumericalProfiler() {
      profile = new TreeMap<>(Comparator.comparing(StackTraceElement::getFileName).thenComparingInt(StackTraceElement::getLineNumber));
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
      Reactions.react(r.getRecommendation());
      //System.out.println("Direct match : " + expr + " for recommendation " + r);
   }

   private void handleProfiledMatch(Recommendation r, SymbolicExpression expr) {
      StackTraceElement el = getLine();
      profile.putIfAbsent(el, new ProfileData());
      //System.out.println(r.getLatestLeftValue() + " | " + r.getLatestRightValue());
      r.updateTracking(profile.get(el));

      //System.out.println("Indirect match : " + expr + " for recommendation " + r);
   }

   public void onShutdown() {
      for(Map.Entry<StackTraceElement, ProfileData> entry : profile.entrySet()) {
         StackTraceElement stack = entry.getKey();
         ProfileData data = entry.getValue();

         data.finish();
         System.out.println(stack.toString() + " : " + data.toString());
      }
   }
}
