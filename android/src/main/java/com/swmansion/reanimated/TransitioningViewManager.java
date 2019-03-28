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

import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.view.ReactViewGroup;
import com.facebook.react.views.view.ReactViewManager;

public class TransitioningViewManager extends ReactViewManager {

  private static final class InOutTransition extends Visibility {
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
        if (tv.appearing && tv.inTransition != null) {
          tv.inTransition.captureStartValues(transitionValues);
        }
        if (tv.disappearing && tv.outTransition != null) {
          tv.outTransition.captureStartValues(transitionValues);
        }
        transitionValues.values.put(PROPNAME_VISIBILITY, tv.appearing ? View.GONE : View.VISIBLE);
      }
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
      super.captureEndValues(transitionValues);
      if (transitionValues.view instanceof TransitioningView) {
        TransitioningView tv = (TransitioningView) transitionValues.view;
        if (tv.appearing && tv.inTransition != null) {
          tv.inTransition.captureEndValues(transitionValues);
        }
        if (tv.disappearing && tv.outTransition != null) {
          tv.outTransition.captureEndValues(transitionValues);
        }
        transitionValues.values.put(PROPNAME_VISIBILITY, tv.disappearing ? View.GONE : View.VISIBLE);
        tv.appearing = false;
        tv.disappearing = false;
      }
    }

    @Override
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
      if (view instanceof TransitioningView) {
        TransitioningView tv = (TransitioningView) view;
        if (tv.inTransition != null) {
          return tv.inTransition.onAppear(sceneRoot, view, startValues, endValues);
        }
      }
      return null;
    }

    @Override
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
      if (view instanceof TransitioningView) {
        TransitioningView tv = (TransitioningView) view;
        if (tv.outTransition != null) {
          return tv.outTransition.onDisappear(sceneRoot, view, startValues, endValues);
        }
      }
      return null;
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
    public Visibility inTransition = null;
    public Visibility outTransition = null;

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
      disappearing = true;
      TransitionManager.beginDelayedTransition((ViewGroup) getParent(), sTransition);
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

  private static Visibility createTransition(String type) {
    if (type == null || "none".equals(type)) {
      return null;
    } else if ("fade".equals(type)) {
      return new Fade(Fade.IN | Fade.OUT);
    } else if ("scale".equals(type)) {
      return new Scale();
    } else if ("slide-top".equals(type)) {
      return new Slide(Gravity.TOP);
    } else if ("slide-bottom".equals(type)) {
      return new Slide(Gravity.BOTTOM);
    } else if ("slide-right".equals(type)) {
      return new Slide(Gravity.RIGHT);
    } else if ("slide-left".equals(type)) {
      return new Slide(Gravity.LEFT);
    }
    throw new JSApplicationIllegalArgumentException("Invalid transition type " + type);
  }

  @ReactProp(name = "inTransition")
  public void setInTransition(TransitioningView view, String type) {
    view.inTransition = createTransition(type);
  }

  @ReactProp(name = "outTransition")
  public void setOutTransition(TransitioningView view, String type) {
    view.outTransition = createTransition(type);
  }

}
