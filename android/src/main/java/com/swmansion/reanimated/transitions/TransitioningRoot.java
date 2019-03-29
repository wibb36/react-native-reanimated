package com.swmansion.reanimated.transitions;

import android.content.Context;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.transition.Visibility;

import com.facebook.react.views.view.ReactViewGroup;

public class TransitioningRoot extends ReactViewGroup {

  public Visibility inTransition = null;
  public Visibility outTransition = null;
  public Transition changeTransition = null;

  public TransitioningRoot(Context context) {
    super(context);
  }

  public void requestAnimateNextTransition() {
    TransitionSet transition = new TransitionSet()
            .setOrdering(TransitionSet.ORDERING_SEQUENTIAL)
            .addTransition(new InOutTransition(InOutTransition.OUT, outTransition))
            .addTransition(new ChangeTransition())
            .addTransition(new InOutTransition(InOutTransition.IN, inTransition));
    TransitionManager.beginDelayedTransition(this, transition);
  }

}
