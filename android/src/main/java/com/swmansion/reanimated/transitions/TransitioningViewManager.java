package com.swmansion.reanimated.transitions;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.view.ReactViewGroup;

public class TransitioningViewManager extends TransitioningRootManager {

  @Override
  public String getName() {
    return "REATransitioningView";
  }

  @Override
  public ReactViewGroup createViewInstance(ThemedReactContext context) {
    return new TransitioningView(context);
  }

  @ReactProp(name = "disabled", defaultBoolean = false)
  public void setDisabled(TransitioningView view, boolean disabled) {
    view.setDisabled(disabled);
  }

  @ReactProp(name = "excludeChildren", defaultBoolean = false)
  public void setExcludeChildren(TransitioningView view, boolean excludeChildren) {
    view.setExcludeChildren(excludeChildren);
  }
}
