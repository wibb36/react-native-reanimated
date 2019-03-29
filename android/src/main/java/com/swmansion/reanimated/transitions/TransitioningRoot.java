package com.swmansion.reanimated.transitions;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.transition.TransitionValues;
import android.support.transition.Visibility;
import android.view.ViewGroup;

import com.facebook.react.views.view.ReactViewGroup;

public class TransitioningRoot extends ReactViewGroup {

  private static class FilteringTransitionSet extends TransitionSet {
    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
      TransitionUtils.maybeExcludeChildren(transitionValues.view, this);
      super.captureStartValues(transitionValues);
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
      TransitionUtils.maybeExcludeChildren(transitionValues.view, this);
      super.captureEndValues(transitionValues);
    }

    @Override
    public @Nullable Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues, @Nullable TransitionValues endValues) {
      if (startValues != null && startValues.view instanceof TransitioningView) {
        if (((TransitioningView) startValues.view).isDisabled()) {
          return null;
        }
      }
      if (endValues != null && endValues.view instanceof TransitioningView) {
        if (((TransitioningView) endValues.view).isDisabled()) {
          return null;
        }
      }
      return super.createAnimator(sceneRoot, startValues, endValues);
    }
  }

  public Visibility inTransition = null;
  public Visibility outTransition = null;
  public Transition changeTransition = null;

  public TransitioningRoot(Context context) {
    super(context);
  }

  public void requestAnimateNextTransition() {
    TransitionSet transition = new FilteringTransitionSet()
            .setOrdering(TransitionSet.ORDERING_SEQUENTIAL)
            .addTransition(new InOutTransition(InOutTransition.OUT, outTransition))
            .addTransition(new ChangeTransition(changeTransition))
            .addTransition(new InOutTransition(InOutTransition.IN, inTransition));
    TransitionManager.beginDelayedTransition(this, transition);
  }

}
