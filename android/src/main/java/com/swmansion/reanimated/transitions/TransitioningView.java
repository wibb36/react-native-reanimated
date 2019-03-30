package com.swmansion.reanimated.transitions;

import android.content.Context;
import android.support.transition.Transition;
import android.support.transition.Visibility;

import com.facebook.react.views.view.ReactViewGroup;

final class TransitioningView extends TransitioningRoot {

  private boolean mDisabled = false;
  private boolean mExcludeChildren = false;

  public TransitioningView(Context context) {
    super(context);
  }

  public boolean isDisabled() {
    return mDisabled;
  }

  public void setDisabled(boolean disabled) {
    mDisabled = disabled;
  }

  public boolean isExcludingChildren() {
    return mExcludeChildren;
  }

  public void setExcludeChildren(boolean excludeChildren) {
    mExcludeChildren = excludeChildren;
  }
}
