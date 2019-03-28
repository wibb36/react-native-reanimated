package com.swmansion.reanimated;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.transition.ChangeBounds;
import android.support.transition.Fade;
import android.support.transition.Slide;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.transition.TransitionValues;
import android.support.transition.Visibility;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.view.ReactViewGroup;
import com.facebook.react.views.view.ReactViewManager;

public class TransitioningViewManager extends ReactViewManager {

  private static final class InOutTransition extends Visibility {
    static final String PROPNAME_VISIBILITY = "android:visibility:visibility";
    static final String PROPNAME_SCREEN_POSITION = "android:slide:screenPosition";

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
        transitionValues.values.put(PROPNAME_VISIBILITY, tv.appearing ? View.GONE : View.VISIBLE);
      }
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
      super.captureEndValues(transitionValues);
      if (transitionValues.view instanceof TransitioningView) {
        TransitioningView tv = (TransitioningView) transitionValues.view;
        transitionValues.values.put(PROPNAME_VISIBILITY, tv.disappearing ? View.GONE : View.VISIBLE);
        tv.appearing = false;
        tv.disappearing = false;
      }
      View view = transitionValues.view;
      int[] position = new int[2];
      view.getLocationOnScreen(position);
      transitionValues.values.put(PROPNAME_SCREEN_POSITION, position);
    }

    @Override
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
      return new Slide(Gravity.RIGHT).onAppear(sceneRoot, view, startValues, endValues);
    }

    @Override
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
      return new Fade(Fade.OUT).onDisappear(sceneRoot, view, startValues, endValues);
    }
  }

  private static final class ChangeTransition extends Transition {
    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {

    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {

    }
  }

  private static final class MyTransitionSet extends TransitionSet {
    public MyTransitionSet() {
      addTarget(TransitioningView.class);
      setOrdering(ORDERING_SEQUENTIAL);
      addTransition(new InOutTransition(InOutTransition.OUT)).
              addTransition(new ChangeBounds().setInterpolator(new FastOutSlowInInterpolator()).setDuration(2000)).
              addTransition(new InOutTransition(InOutTransition.IN));
    }
  }

  private static final Transition sTransition = new MyTransitionSet();

  private static final class TransitioningView extends ReactViewGroup {

    public boolean appearing = true;
    public boolean disappearing = false;

    public TransitioningView(Context context) {
      super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      Log.e("CAT", "CHILD ON MEASURE");
      TransitionManager.beginDelayedTransition((ViewGroup) getParent(), sTransition);
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      TransitionManager.beginDelayedTransition((ViewGroup) getParent(), sTransition);
      disappearing = true;
    }
  }

  @Override
  public String getName() {
    return "REATransitioningView";
  }

  @Override
  public ReactViewGroup createViewInstance(ThemedReactContext context) {
    return new TransitioningView(context);
  }

  enum TRANSITION_TYPES {
    SLIDE_TOP,
    SLIDE_BOTTOM,
    SLIDE_RIGHT,
    SLIDE_LEFT
  }

  @ReactProp(name = "inTransition")
  public void setInTransition(TransitioningView view, String inTransition) {

  }

//  @Override
//  public boolean needsCustomLayoutForChildren() {
//    Log.e("CAT", "NEEDS?");
//    TransitionManager.beginDelayedTransition(this);
//    return false;
////    return true;
//  }
}
