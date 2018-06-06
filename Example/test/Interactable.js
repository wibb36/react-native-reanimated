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
  diff,
  divide,
  call,
  not,
  pow,
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

function runSpring(clock, finished, value, velocity, dest) {
  const state = {
    finished: finished,
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
    cond(state.finished, [
      set(state.finished, 0),
      set(state.velocity, velocity),
      set(state.position, value),
      set(config.toValue, dest),
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

function springBehavior(dt, target, obj, anchor, tension = 300) {
  const dx = sub(target.x, anchor.x);
  const ax = divide(multiply(-1, tension, dx), obj.mass);
  return set(obj.vx, add(obj.vx, multiply(dt, ax)));
}

function frictionBehavior(dt, target, obj, damping = 0.7) {
  return set(obj.vx, multiply(obj.vx, pow(damping, multiply(60, dt))));
}

function anchorBehavior(dt, target, obj, anchor) {
  const dx = sub(anchor.x, target.x);
  const run = set(obj.vx, divide(dx, dt));
  return {
    anchorX,
    run,
  };
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
    const dragging = new Value(0);

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

    const clock = new Clock();

    const tossedX = transX; //add(transX, multiply(props.dragToss, dragVX));

    const dt = divide(diff(clock), 1000);

    const obj = {
      vx: new Value(0),
      mass: 1,
    };
    const target = {
      x: transX,
    };

    const behaviors1 = [];
    const behaviors2 = [];
    const behaviors3 = [];

    const addSpring = (anchor, tension) => {
      behaviors1.push(springBehavior(dt, target, obj, anchor, tension));
    };

    const addFriction = damping => {
      behaviors2.push(frictionBehavior(dt, target, obj, damping));
    };

    const dragAnchor = { x: new Value(0) };
    let dragBehavior;
    if (props.dragWithSpring) {
      const { tension, damping } = props.dragWithSpring;
      dragBehavior = springBehavior(dt, target, obj, dragAnchor, tension);
      addFriction(damping);
    } else {
      dragBehavior = anchorBehavior(dt, target, obj, dragAnchor);
    }

    if (props.springPoints) {
      props.springPoints.forEach(pt => {
        addSpring(pt, pt.tension);
        if (pt.damping) {
          addFriction(pt.damping);
        }
      });
    }

    // behaviors can go under one of three buckets depending on their priority
    // we append to each bucket but in Interactable behaviors get added to the
    // front, so we join in reverse order and then reverse the array.
    const behaviors = [...behaviors3, ...behaviors2, ...behaviors1].reverse();

    this._transX = cond(
      eq(state, State.ACTIVE),
      [
        startClock(clock),
        set(dragging, 1),
        set(dragAnchor.x, dragX),
        cond(dt, [dragBehavior, ...behaviors]),
        set(transX, add(transX, multiply(obj.vx, dt))),
      ],
      [
        set(
          transX,
          runSpring(
            clock,
            dragging,
            transX,
            obj.vx,
            snapTo(tossedX, props.snapPoints)
          ),
          0
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
