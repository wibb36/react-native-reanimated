package com.swmansion.reanimated.transitions;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.support.transition.TransitionValues;
import android.support.transition.Visibility;
import android.view.View;
import android.view.ViewGroup;

final class InOutTransition extends Visibility {
  public static final int IN = Visibility.MODE_IN;
  public static final int OUT = Visibility.MODE_OUT;

  private Visibility mDefaultTransition;

  InOutTransition(int mode, Visibility defaultTransition) {
    setMode(mode);
    mDefaultTransition = defaultTransition;
    if (defaultTransition != null) {
      setPropagation(defaultTransition.getPropagation());
    }
  }

  @Override
  public void captureStartValues(@NonNull TransitionValues transitionValues) {
    if (transitionValues.view instanceof TransitioningView) {
      TransitioningView tv = (TransitioningView) transitionValues.view;
      if ((getMode() & IN) != 0 && tv.inTransition != null) {
        tv.inTransition.captureStartValues(transitionValues);
        return;
      } else if ((getMode() & OUT) != 0 && tv.outTransition != null) {
        tv.outTransition.captureStartValues(transitionValues);
        return;
      }
    }
    if (mDefaultTransition != null) {
      mDefaultTransition.captureStartValues(transitionValues);
    }
  }

  @Override
  public void captureEndValues(@NonNull TransitionValues transitionValues) {
    if (transitionValues.view instanceof TransitioningView) {
      TransitioningView tv = (TransitioningView) transitionValues.view;
      if ((getMode() & IN) != 0 && tv.inTransition != null) {
        tv.inTransition.captureEndValues(transitionValues);
        return;
      } else if ((getMode() & OUT) != 0 && tv.outTransition != null) {
        tv.outTransition.captureEndValues(transitionValues);
        return;
      }
    }
    if (mDefaultTransition != null) {
      mDefaultTransition.captureEndValues(transitionValues);
    }
  }

  @Override
  public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
    if (view instanceof TransitioningView) {
      TransitioningView tv = (TransitioningView) view;
      if (tv.inTransition != null) {
        return TransitionUtils.configureAnimator(tv.inTransition.onAppear(sceneRoot, view, startValues, endValues), tv.inTransition);
      }
    }
    if (mDefaultTransition != null) {
      return mDefaultTransition.onAppear(sceneRoot, view, startValues, endValues);
    }
    return super.onAppear(sceneRoot, view, startValues, endValues);
  }

  @Override
  public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
    if (view instanceof TransitioningView) {
      TransitioningView tv = (TransitioningView) view;
      if (tv.outTransition != null) {
        return TransitionUtils.configureAnimator(tv.outTransition.onDisappear(sceneRoot, view, startValues, endValues), tv.outTransition);
      }
    }
    if (mDefaultTransition != null) {
      return mDefaultTransition.onDisappear(sceneRoot, view, startValues, endValues);
    }
    return super.onDisappear(sceneRoot, view, startValues, endValues);
  }
}
