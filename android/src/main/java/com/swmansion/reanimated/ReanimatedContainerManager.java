package com.swmansion.reanimated;

import android.content.Context;
import android.transition.TransitionManager;
import android.util.Log;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.view.ReactViewGroup;
import com.facebook.react.views.view.ReactViewManager;

public class ReanimatedContainerManager extends ReactViewManager {

  private static final class ReanimatedContainer extends ReactViewGroup {

    public ReanimatedContainer(Context context) {
      super(context);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
      Log.e("CAT", "CONTAINER ON LAYOUT!");
      super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void requestLayout() {
      Log.e("CAT", "CONTAINER REQ LAYOUT");
      super.requestLayout();
    }
  }

  @Override
  public String getName() {
    return "ReanimatedContainer";
  }

  @Override
  public ReactViewGroup createViewInstance(ThemedReactContext context) {
    return new ReanimatedContainer(context);
  }

//  @Override
//  public boolean needsCustomLayoutForChildren() {
//    Log.e("CAT", "NEEDS?");
//    TransitionManager.beginDelayedTransition(this);
//    return false;
////    return true;
//  }
}
