package com.github.cojac.profiler;

public class ProfileData {
   double min = Double.POSITIVE_INFINITY;
   double max = Double.NEGATIVE_INFINITY;
   double mean = 0;
   double std = 0;
   double m2 = 0;
   int count = 0;

   boolean overflow = false;

   ProfileData left;
   ProfileData right;

   public void update(double newValue) {
      min = Math.min(min, newValue);
      max = Math.max(max, newValue);

      count++;

      double delta = newValue - mean;
      mean += delta / count;

      double delta2 = newValue - mean;
      m2 += delta * delta2;
   }

   public void updateChildren(double leftValue, double rightValue) {
      if(left == null) {
         left = new ProfileData();
      }
      if(right == null) {
         right = new ProfileData();
      }
      left.update(leftValue);
      right.update(rightValue);
   }


   public void finish() {
      std = Math.sqrt(m2 / count);
      if(left != null) {
         left.finish();
      }
      if(right != null) {
         right.finish();
      }
   }

   @Override
   public String toString() {
      String children = "";
      if(right != null && left != null) {
         children = " | {left child : " + left.toString() + "} | {right child : " + right.toString() + "}";
      }
      return String.format("min: %s | max: %s | mean: %s | std: %s | count: %d | overflow: %b" + children, min, max, mean, std, count, overflow);
   }
}