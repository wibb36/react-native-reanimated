package com.swmansion.reanimated.transitions;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.support.transition.Fade;
import android.support.transition.Slide;
import android.support.transition.Transition;
import android.support.transition.Visibility;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableMap;

class TransitionUtils {
  static Animator configureAnimator(Animator animator, Transition transition) {
    if (transition == null || animator == null) {
      return animator;
    }
    long duration = transition.getDuration();
    if (duration >= 0) {
      animator.setDuration(duration);
    }
    long startDelay = transition.getStartDelay();
    if (startDelay >= 0) {
      animator.setStartDelay(startDelay);
    }
    TimeInterpolator interpolator = transition.getInterpolator();
    if (interpolator != null) {
      animator.setInterpolator(interpolator);
    }
    return animator;
  }

  static Visibility createTransition(String type) {
    if (type == null || "none".equals(type)) {
      return null;
    } else if ("fade".equals(type)) {
      return new Fade(Fade.IN | Fade.OUT);
    } else if ("scale".equals(type)) {
      return new Scale();
    } else if ("slide-top".equals(type)) {
      return new Slide(Gravity.TOP);
    } else if ("slide-bottom".equals(type)) {
      return new Slide(Gravity.BOTTOM);
    } else if ("slide-right".equals(type)) {
      return new Slide(Gravity.RIGHT);
    } else if ("slide-left".equals(type)) {
      return new Slide(Gravity.LEFT);
    }
    throw new JSApplicationIllegalArgumentException("Invalid transition type " + type);
  }

  static void configureTransition(Transition transition, ReadableMap params) {
    if (params.hasKey("durationMs")) {
      int durationMs = params.getInt("durationMs");
      transition.setDuration(durationMs);
    }
    if (params.hasKey("interpolation")) {
      String interpolation = params.getString("interpolation");
      if (interpolation.equals("easeIn")) {
        transition.setInterpolator(new AccelerateInterpolator());
      } else if (interpolation.equals("easeOut")) {
        transition.setInterpolator(new DecelerateInterpolator());
      } else if (interpolation.equals("easeInOut")) {
        transition.setInterpolator(new AccelerateDecelerateInterpolator());
      } else if (interpolation.equals("linear")) {
        transition.setInterpolator(new LinearInterpolator());
      } else {
        throw new JSApplicationIllegalArgumentException("Invalid interpolation type " + interpolation);
      }
    }
    if (params.hasKey("delayMs")) {
      int delayMs = params.getInt("delayMs");
      transition.setStartDelay(delayMs);
    }
  }

  public static void maybeExcludeChildren(View view, Transition transition) {
    if (view instanceof TransitioningView && ((TransitioningView) view).isExcludingChildren()) {
      transition.excludeChildren(view, true);
    }
  }
}
