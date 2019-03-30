package com.swmansion.reanimated.transitions;

import android.support.transition.Transition;
import android.support.transition.Visibility;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.view.ReactViewGroup;
import com.facebook.react.views.view.ReactViewManager;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class TransitioningRootManager extends ReactViewManager {

  private static int COMMAND_ANIMATE_NEXT = 101;

  @Override
  public String getName() {
    return "REATransitioningRoot";
  }

  @Override
  public ReactViewGroup createViewInstance(ThemedReactContext context) {
    return new TransitioningRoot(context);
  }

  @ReactProp(name = "inTransition")
  public void setInTransition(TransitioningRoot view, ReadableMap params) {
    if (params == null) {
      view.inTransition = null;
    } else {
      String type = params.getString("type");
      Visibility transition = TransitionUtils.createTransition(type);
      TransitionUtils.configureTransition(transition, params);
      view.inTransition = transition;
    }
  }

  @ReactProp(name = "outTransition")
  public void setOutTransition(TransitioningRoot view, ReadableMap params) {
    if (params == null) {
      view.outTransition = null;
    } else {
      String type = params.getString("type");
      Visibility transition = TransitionUtils.createTransition(type);
      TransitionUtils.configureTransition(transition, params);
      view.outTransition = transition;
    }
  }

  @ReactProp(name = "changeTransition")
  public void setChangeTransition(TransitioningView view, ReadableMap params) {
    Transition transition = new DummyTransition();
    TransitionUtils.configureTransition(transition, params);
    view.changeTransition = transition;
  }

  @Override
  public Map<String, Integer> getCommandsMap() {
    Map<String, Integer> commands = new HashMap<>();
    commands.putAll(super.getCommandsMap());
    commands.put("animateNextTransition", COMMAND_ANIMATE_NEXT);
    return commands;
  }

  @Override
  public void receiveCommand(ReactViewGroup root, int commandId, @Nullable ReadableArray args) {
    super.receiveCommand(root, commandId, args);
    if (commandId == COMMAND_ANIMATE_NEXT) {
      ((TransitioningRoot) root).requestAnimateNextTransition();
    }
  }
}
