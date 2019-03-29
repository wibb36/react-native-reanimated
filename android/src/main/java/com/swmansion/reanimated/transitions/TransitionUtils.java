package com.swmansion.reanimated.transitions;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.support.transition.Transition;

class TransitionUtils {
  static Animator configureAnimator(Animator animator, Transition transition) {
    if (transition == null || animator == null) {
      return animator;
    }
    long duration = transition.getDuration();
    if (duration >= 0) {
      animator.setDuration(duration);
    }
    TimeInterpolator interpolator = transition.getInterpolator();
    if (interpolator != null) {
      animator.setInterpolator(interpolator);
    }
    return animator;
  }
}
