package com.swmansion.reanimated.transitions;

import android.support.annotation.NonNull;
import android.support.transition.ChangeBounds;
import android.support.transition.ChangeTransform;
import android.support.transition.TransitionSet;
import android.support.transition.TransitionValues;

final class ChangeTransition extends TransitionSet {
  private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";

  public ChangeTransition() {
    setOrdering(ORDERING_TOGETHER);
    ChangeTransform changeTransform = new ChangeTransform();
    changeTransform.setReparent(false);
    changeTransform.setReparentWithOverlay(false);
    addTransition(changeTransform);
    addTransition(new ChangeBounds());
  }

  @Override
  public void captureStartValues(@NonNull TransitionValues transitionValues) {
    if (transitionValues.view instanceof TransitioningView) {
      TransitioningView tv = (TransitioningView) transitionValues.view;
      if (!tv.isDisappearing() && !tv.isAppearing()) {
        super.captureStartValues(transitionValues);
        if (transitionValues.values.containsKey(PROPNAME_BOUNDS)) {
          transitionValues.values.put(PROPNAME_BOUNDS, tv.getOldBounds());
        }
      }
    }
  }

  @Override
  public void captureEndValues(@NonNull TransitionValues transitionValues) {
    if (transitionValues.view instanceof TransitioningView) {
      TransitioningView tv = (TransitioningView) transitionValues.view;
      if (!tv.isAppearing() && !tv.isDisappearing()) {
        super.captureEndValues(transitionValues);
      }
    }
  }
}
