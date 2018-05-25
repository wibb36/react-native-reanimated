import React, { Component } from 'react';
import { StyleSheet, View } from 'react-native';

import Animated, { Easing } from 'react-native-reanimated';
import { PanGestureHandler, State } from 'react-native-gesture-handler';

const {
  set,
  cond,
  eq,
  add,
  sub,
  divide,
  call,
  multiply,
  lessThan,
  abs,
  defined,
  startClock,
  stopClock,
  clockRunning,
  block,
  timing,
  debug,
  spring,
  Value,
  Clock,
  event,
} = Animated;

function runSpring(clock, value, velocity, dest) {
  const state = {
    finished: new Value(0),
    velocity: new Value(0),
    position: new Value(0),
    time: new Value(0),
  };

  const config = {
    damping: 7,
    mass: 1,
    stiffness: 121.6,
    overshootClamping: false,
    restSpeedThreshold: 0.001,
    restDisplacementThreshold: 0.001,
    toValue: new Value(0),
  };

  return [
    cond(clockRunning(clock), 0, [
      set(state.finished, 0),
      set(state.velocity, velocity),
      set(state.position, value),
      set(config.toValue, dest),
      startClock(clock),
    ]),
    spring(clock, state, config),
    cond(state.finished, stopClock(clock)),
    state.position,
  ];
}

function snapTo(value, snapPoints) {
  const dist = new Value(0);
  const best = new Value(0);
  return [
    set(best, snapPoints[0].x),
    set(dist, abs(sub(value, snapPoints[0].x))),
    ...snapPoints.map(pt => {
      const newDist = abs(sub(value, pt.x));
      return cond(lessThan(newDist, dist), [
        set(dist, newDist),
        set(best, pt.x),
      ]);
    }),
    best,
  ];
}

export default class Interactable extends Component {
  static defaultProps = {
    dragToss: 0.1,
  };

  constructor(props) {
    super(props);

    const dragX = new Value(0);
    const dragVX = new Value(0);
    const state = new Value(-1);
    const prevState = new Value(-1);

    this._onGestureEvent = event([
      {
        nativeEvent: {
          translationX: dragX,
          velocityX: dragVX,
          state: state,
        },
      },
    ]);

    const transX = new Value();
    const prevDragX = new Value(0);

    const clock = new Clock();

    const tossedX = add(transX, multiply(props.dragToss, dragVX));

    const snapPoint = new Value(0);

    this._transX = cond(
      eq(state, State.ACTIVE),
      [
        stopClock(clock),
        set(transX, add(transX, sub(dragX, prevDragX))),
        set(prevDragX, dragX),
        set(prevState, state),
        transX,
      ],
      [
        cond(
          eq(prevState, State.ACTIVE),
          set(snapPoint, snapTo(debug('tossed', tossedX), props.snapPoints))
        ),
        set(prevDragX, 0),
        set(prevState, state),
        set(
          transX,
          cond(defined(transX), runSpring(clock, transX, dragVX, snapPoint), 0)
        ),
      ]
    );
  }
  render() {
    const { children, style } = this.props;
    return (
      <PanGestureHandler
        maxPointers={1}
        onGestureEvent={this._onGestureEvent}
        onHandlerStateChange={this._onGestureEvent}>
        <Animated.View
          style={[style, { transform: [{ translateX: this._transX }] }]}>
          {children}
        </Animated.View>
      </PanGestureHandler>
    );
  }
}
