package com.swmansion.reanimated.transitions;

import android.support.transition.Fade;
import android.support.transition.Slide;
import android.support.transition.Transition;
import android.support.transition.Visibility;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.view.ReactViewGroup;
import com.facebook.react.views.view.ReactViewManager;

public class TransitioningViewManager extends ReactViewManager {

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

  private static void configureTransition(Transition transition, ReadableMap params) {
    if (params.hasKey("durationMs")) {
      int durationMs = params.getInt("durationMs");
      transition.setDuration(durationMs);
    }
    if (params.hasKey("interpolation")) {
      String interpolation = params.getString("interpolation");
      if (interpolation.equals("easeIn")) {
        transition.setInterpolator(new AccelerateInterpolator());
      } else if (interpolation.equals("easeOut")) {
        transition.setInterpolator(new DecelerateInterpolator());
      } else if (interpolation.equals("easeInOut")) {
        transition.setInterpolator(new AccelerateDecelerateInterpolator());
      } else if (interpolation.equals("linear")) {
        transition.setInterpolator(new LinearInterpolator());
      } else {
        throw new JSApplicationIllegalArgumentException("Invalid interpolation type " + interpolation);
      }
    }
  }

  @ReactProp(name = "inTransition")
  public void setInTransition(TransitioningView view, ReadableMap params) {
    if (params == null) {
      view.inTransition = null;
    } else {
      String type = params.getString("type");
      Visibility transition = createTransition(type);
      configureTransition(transition, params);
      view.inTransition = transition;
    }
  }

  @ReactProp(name = "outTransition")
  public void setOutTransition(TransitioningView view, ReadableMap params) {
    if (params == null) {
      view.outTransition = null;
    } else {
      String type = params.getString("type");
      Visibility transition = createTransition(type);
      configureTransition(transition, params);
      view.outTransition = transition;
    }
  }

  @ReactProp(name = "changeTransition")
  public void setChangeTransition(TransitioningView view, ReadableMap params) {
//    view
  }

  @Override
  public void setTransform(ReactViewGroup view, ReadableArray matrix) {
    Log.e("CAT", "UPDATE TRANSFORM");
    ((TransitioningView) view).initTransitioningInParent();
    super.setTransform(view, matrix);
  }
}
