package com.github.cojac.profiler;

import java.util.ArrayList;
import java.util.List;

public class ThrowableRecommendationContainer extends RuntimeException {
   private final List<RecommendationReport> recommendations;

   public ThrowableRecommendationContainer() {
      this.recommendations = new ArrayList<>();
   }

   public void addRecommendation(RecommendationReport recommendation) {
      this.recommendations.add(recommendation);
   }

   public List<RecommendationReport> getAllRecommendations() {
      return this.recommendations;
   }

   @Override
   public String getMessage() {
      StringBuilder message = new StringBuilder();

      for(RecommendationReport recommendation : recommendations) {
         message.append(recommendation.toString()).append("\n");
      }

      return message.toString();
   }
}
