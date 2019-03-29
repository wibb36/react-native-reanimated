package com.swmansion.reanimated.transitions;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.ChangeBounds;
import android.support.transition.ChangeTransform;
import android.support.transition.Transition;
import android.support.transition.TransitionSet;
import android.support.transition.TransitionValues;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

final class ChangeTransition extends Transition {
  private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";

  private static final ChangeTransform sChangeTransform;
  private static final ChangeBounds sChangeBounds = new ChangeBounds();

  static {
    sChangeTransform = new ChangeTransform();
    sChangeTransform.setReparent(false);
    sChangeTransform.setReparentWithOverlay(false);
  }

//  public ChangeTransition() {
//    setOrdering(ORDERING_TOGETHER);
//    ChangeTransform changeTransform = new ChangeTransform();
//    changeTransform.setReparent(false);
//    changeTransform.setReparentWithOverlay(false);
//    addTransition(changeTransform);
//    addTransition(new ChangeBounds());
//  }

  @Override
  public void captureStartValues(TransitionValues transitionValues) {
    if (transitionValues.view instanceof TransitioningView) {
      TransitioningView tv = (TransitioningView) transitionValues.view;
      if (!tv.isDisappearing() && !tv.isAppearing()) {
//        super.captureStartValues(transitionValues);
        sChangeTransform.captureStartValues(transitionValues);
        sChangeBounds.captureStartValues(transitionValues);
        if (transitionValues.values.containsKey(PROPNAME_BOUNDS)) {
          transitionValues.values.put(PROPNAME_BOUNDS, tv.getOldBounds());
        }
      }
    }
  }

  @Override
  public void captureEndValues(TransitionValues transitionValues) {
    if (transitionValues.view instanceof TransitioningView) {
      TransitioningView tv = (TransitioningView) transitionValues.view;
      if (!tv.isAppearing() && !tv.isDisappearing()) {
        sChangeTransform.captureEndValues(transitionValues);
        sChangeBounds.captureEndValues(transitionValues);
      }
    }
  }

  @Override
  public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
    if (endValues != null && endValues.view instanceof TransitioningView) {
      TransitioningView tv = (TransitioningView) endValues.view;

      Animator changeTransformAnimator = sChangeTransform.createAnimator(sceneRoot, startValues, endValues);
      TransitionUtils.configureAnimator(changeTransformAnimator, tv.changeTransition);

      Animator changeBoundsAnimator = sChangeBounds.createAnimator(sceneRoot, startValues, endValues);
      TransitionUtils.configureAnimator(changeBoundsAnimator, tv.changeTransition);

      if (changeTransformAnimator == null) {
        return changeBoundsAnimator;
      }
      if (changeBoundsAnimator == null) {
        return changeTransformAnimator;
      }
      AnimatorSet animatorSet = new AnimatorSet();
      animatorSet.playTogether(changeTransformAnimator, changeBoundsAnimator);
      return animatorSet;
    }
    return null;
  }
}
