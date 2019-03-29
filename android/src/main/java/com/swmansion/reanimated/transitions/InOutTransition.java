package com.swmansion.reanimated.transitions;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.support.annotation.NonNull;
import android.support.transition.Transition;
import android.support.transition.TransitionValues;
import android.support.transition.Visibility;
import android.view.View;
import android.view.ViewGroup;

final class InOutTransition extends Visibility {
  static final String PROPNAME_VISIBILITY = "android:visibility:visibility";

  public static final int IN = Visibility.MODE_IN;
  public static final int OUT = Visibility.MODE_OUT;

  InOutTransition(int mode) {
    setMode(mode);
  }

  @Override
  public void captureStartValues(@NonNull TransitionValues transitionValues) {
    super.captureStartValues(transitionValues);
    if (transitionValues.view instanceof TransitioningView) {
      TransitioningView tv = (TransitioningView) transitionValues.view;
      if (tv.isAppearing() && tv.inTransition != null) {
        tv.inTransition.captureStartValues(transitionValues);
      }
      if (tv.isDisappearing() && tv.outTransition != null) {
        // visiblity OUT transform does not handle translated views well. Below we workaround this
        // issue by resetting translation for the duration of params capture which then it turn
        // allow for the OUT transform to capture preview that is properly translated. We restore
        // transform props right after that's done
        float prevTransX = tv.getTranslationX();
        float prevTransY = tv.getTranslationY();
        tv.setTranslationX(0);
        tv.setTranslationY(0);
        tv.outTransition.captureStartValues(transitionValues);
        tv.setTranslationX(prevTransX);
        tv.setTranslationY(prevTransY);
      }
      transitionValues.values.put(PROPNAME_VISIBILITY, tv.isAppearing() ? View.GONE : View.VISIBLE);
    }
  }

  @Override
  public void captureEndValues(@NonNull TransitionValues transitionValues) {
    super.captureEndValues(transitionValues);
    if (transitionValues.view instanceof TransitioningView) {
      TransitioningView tv = (TransitioningView) transitionValues.view;
      if (tv.isAppearing() && tv.inTransition != null) {
        tv.inTransition.captureEndValues(transitionValues);
      }
      if (tv.isDisappearing() && tv.outTransition != null) {
        tv.outTransition.captureEndValues(transitionValues);
      }
      transitionValues.values.put(PROPNAME_VISIBILITY, tv.isDisappearing() ? View.GONE : View.VISIBLE);
    }
  }

  private Animator configureAnimator(Animator animator, Transition transition) {
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

  @Override
  public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
    if (view instanceof TransitioningView) {
      TransitioningView tv = (TransitioningView) view;
      if (tv.inTransition != null) {
        return configureAnimator(tv.inTransition.onAppear(sceneRoot, view, startValues, endValues), tv.inTransition);
      }
    }
    return null;
  }

  @Override
  public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
    if (view instanceof TransitioningView) {
      TransitioningView tv = (TransitioningView) view;
      if (tv.outTransition != null) {
        return configureAnimator(tv.outTransition.onDisappear(sceneRoot, view, startValues, endValues), tv.outTransition);
      }
    }
    return null;
  }
}
