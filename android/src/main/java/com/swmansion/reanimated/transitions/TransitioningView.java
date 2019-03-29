package com.swmansion.reanimated.transitions;

import android.content.Context;
import android.graphics.Rect;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.transition.TransitionValues;
import android.support.transition.Visibility;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.facebook.react.views.view.ReactViewGroup;

final class TransitioningView extends ReactViewGroup {

//  private static final class MyTransitionSet extends TransitionSet {
//    public MyTransitionSet() {
//      setOrdering(ORDERING_SEQUENTIAL);
//      addTarget(TransitioningView.class);
//
//      addTransition(new InOutTransition(InOutTransition.OUT))
//              .addTransition(new ChangeTransition())
//              .addTransition(new InOutTransition(InOutTransition.IN));
//    }
//
//    @Override
//    public void captureEndValues(TransitionValues transitionValues) {
//      super.captureEndValues(transitionValues);
//      View view = transitionValues.view;
//      if (view instanceof TransitioningView) {
//        ((TransitioningView) view).notifyEndCapture();
//      }
//    }
//  }
//
//  private static final Transition sTransition = new MyTransitionSet();

  public Visibility inTransition = null;
  public Visibility outTransition = null;
  public Transition changeTransition = null;

  private boolean mAppearing = true;
  private boolean mDisappearing = false;
  private int oldLeft;
  private int oldTop;
  private int oldRight;
  private int oldBottom;

  public TransitioningView(Context context) {
    super(context);
  }

  public void initTransitioningInParent() {
//    ViewParent parent = getParent();
//    if (parent != null) {
//      TransitionManager.beginDelayedTransition((ViewGroup) parent, sTransition);
//    }
  }

  public boolean isAppearing() {
    return mAppearing;
  }

  public boolean isDisappearing() {
    return mDisappearing;
  }

  void notifyEndCapture() {
    mAppearing = false;
    mDisappearing = false;
    oldLeft = getLeft();
    oldRight = getRight();
    oldTop = getTop();
    oldBottom = getBottom();
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    initTransitioningInParent();
  }

  public Rect getOldBounds() {
    return new Rect(oldLeft, oldTop, oldRight, oldBottom);
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    mDisappearing = true;
    initTransitioningInParent();
  }
}
