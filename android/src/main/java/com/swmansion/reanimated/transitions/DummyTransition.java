package com.swmansion.reanimated.transitions;

import android.support.annotation.NonNull;
import android.support.transition.Transition;
import android.support.transition.TransitionValues;

/**
 * This class is here just to serve as a data structure to carry interpolation
 * and duration config for CHANGE transitions.
 */
public class DummyTransition extends Transition {

  @Override
  public void captureStartValues(@NonNull TransitionValues transitionValues) {
    // no-op
  }

  @Override
  public void captureEndValues(@NonNull TransitionValues transitionValues) {
    // no-op
  }
}
