package com.swmansion.reanimated.transitions;

import android.support.transition.Transition;
import android.support.transition.Visibility;
import android.util.Log;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.NativeViewHierarchyOptimizer;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIViewOperationQueue;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.view.ReactViewGroup;
import com.facebook.react.views.view.ReactViewManager;

import javax.annotation.Nullable;

public class TransitioningViewManager extends ReactViewManager {

  @Override
  public String getName() {
    return "REATransitioningView";
  }

  @Override
  public ReactViewGroup createViewInstance(ThemedReactContext context) {
    return new TransitioningView(context);
  }

  @ReactProp(name = "inTransition")
  public void setInTransition(TransitioningView view, ReadableMap params) {
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
  public void setOutTransition(TransitioningView view, ReadableMap params) {
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
  public void setTransform(ReactViewGroup view, ReadableArray matrix) {
    Log.e("CAT", "UPDATE TRANSFORM");
    ((TransitioningView) view).initTransitioningInParent();
    super.setTransform(view, matrix);
  }

  @Override
  public void removeAllViews(ReactViewGroup parent) {
    Log.e("CAT", "REMOVE ALL VIEWS " + parent.getId());
    super.removeAllViews(parent);
  }

  private static class SomeShadowNode extends LayoutShadowNode {
    @Override
    public boolean dispatchUpdates(float absoluteX, float absoluteY, UIViewOperationQueue uiViewOperationQueue, @Nullable NativeViewHierarchyOptimizer nativeViewHierarchyOptimizer) {
      Log.e("CAT", "DISPATCH UPDATES " + this);
      return super.dispatchUpdates(absoluteX, absoluteY, uiViewOperationQueue, nativeViewHierarchyOptimizer);
    }
    //    @Override
//    public void markUpdated() {
//      Log.e("CAT", "SHADOW MARK UPDATED " + getChildCount() + " " + this);
//      super.markUpdated();
//    }
//
//    @Override
//    public void removeAndDisposeAllChildren() {
//      Log.e("CAT", "REMOVE AND DISPOSE " + getNativeChildCount() + " " + this);
//      super.removeAndDisposeAllChildren();
//      markUpdated();
//    }
//
//    @Override
//    public void onAfterUpdateTransaction() {
//      Log.e("CAT", "AFTER UPDATE " + this);
//      super.onAfterUpdateTransaction();
//    }
//
//    @Override
//    public void onCollectExtraUpdates(UIViewOperationQueue uiViewOperationQueue) {
//      Log.e("CAT", "COLLECT " + this);
//      super.onCollectExtraUpdates(uiViewOperationQueue);
//    }
  }

  @Override
  public LayoutShadowNode createShadowNodeInstance() {
    return new SomeShadowNode();
  }
}
