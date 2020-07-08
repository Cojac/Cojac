package com.github.cojac.profiler;

public class RecommendationReport {
   private static final String TAG = "COJAC : ";

   private final Recommendation recommendation;
   private final StackTraceElement stackTrace;
   private final ProfileData data;
   private final String message;

   public RecommendationReport(Recommendation recommendation, StackTraceElement stackTrace,
                                  ProfileData data) {
      this.recommendation = recommendation;
      this.stackTrace = stackTrace;
      this.data = data;

      message = TAG + stackTrace.toString() + " :\n" +
              "\t" + recommendation.getRecommendation() + "\n" +
              (recommendation.hasCustomStringProvider() ? "\tAdditional information : " + recommendation.getCustomString(data) + "\n" : "") +
              "\tRaw data : " + data.toString();
   }

   public RecommendationReport(Recommendation recommendation, StackTraceElement stackTrace) {
      this.recommendation = recommendation;
      this.stackTrace = stackTrace;
      this.data = null;

      message = TAG + stackTrace.toString() + " :\n" +
              "\t" + recommendation.getRecommendation();
   }

   public Recommendation getRecommendation() {
      return recommendation;
   }

   public StackTraceElement getRecommendationStackTrace() {
      return stackTrace;
   }

   public ProfileData getData() {
      return data;
   }

   @Override
   public String toString() {
      return message;
   }
}
