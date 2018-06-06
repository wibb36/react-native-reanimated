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
  const dy = sub(target.y, anchor.y);
  const ay = divide(multiply(-1, tension, dy), obj.mass);
  return {
    x: set(obj.vx, add(obj.vx, multiply(dt, ax))),
    y: set(obj.vy, add(obj.vy, multiply(dt, ay))),
  };
}

function frictionBehavior(dt, target, obj, damping = 0.7) {
  const friction = pow(damping, multiply(60, dt));
  return {
    x: set(obj.vx, multiply(obj.vx, friction)),
    y: set(obj.vy, multiply(obj.vy, friction)),
  };
}

function anchorBehavior(dt, target, obj, anchor) {
  const dx = sub(anchor.x, target.x);
  const dy = sub(anchor.y, target.y);
  return {
    x: set(obj.vx, divide(dx, dt)),
    y: set(obj.vy, divide(dy, dt)),
  };
}

export default class Interactable extends Component {
  static defaultProps = {
    dragToss: 0.1,
  };

  constructor(props) {
    super(props);

    const drag = { x: new Value(0), y: new Value(0) };
    const state = new Value(-1);

    this._onGestureEvent = event([
      {
        nativeEvent: {
          translationX: drag.x,
          translationY: drag.y,
          // velocityX: dragVX,
          state: state,
        },
      },
    ]);

    const target = { x: new Value(0), y: new Value(0) };

    const clock = new Clock();

    // const tossedX = transX; //add(transX, multiply(props.dragToss, dragVX));

    const dt = divide(diff(clock), 1000);

    const obj = {
      vx: new Value(0),
      vy: new Value(0),
      mass: 1,
    };

    const behaviorBuckets = [[], [], []];

    const addSpring = (anchor, tension) => {
      behaviorBuckets[0].push(springBehavior(dt, target, obj, anchor, tension));
    };

    const addFriction = damping => {
      behaviorBuckets[1].push(frictionBehavior(dt, target, obj, damping));
    };

    const dragAnchor = { x: new Value(0), y: new Value(0) };
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
    const allBehaviors = [
      ...behaviorBuckets[2],
      ...behaviorBuckets[1],
      ...behaviorBuckets[0],
    ].reverse();
    const behaviors = {
      x: allBehaviors.map(b => b.x),
      y: allBehaviors.map(b => b.y),
    };

    const trans = (x, vx, drag, anchor, dragBehavior, behaviors) => {
      const dragging = new Value(0);
      return cond(
        eq(state, State.ACTIVE),
        [
          startClock(clock),
          set(dragging, 1),
          set(anchor, drag),
          cond(dt, [dragBehavior, ...behaviors]),
          set(x, add(x, multiply(vx, dt))),
        ],
        set(x, runSpring(clock, dragging, x, vx, snapTo(x, props.snapPoints)))
      );
    };

    this._transX = trans(
      target.x,
      obj.vx,
      drag.x,
      dragAnchor.x,
      dragBehavior.x,
      behaviors.x
    );
    this._transY = trans(
      target.y,
      obj.vy,
      drag.y,
      dragAnchor.y,
      dragBehavior.y,
      behaviors.y
    );
  }
  render() {
    const { children, style, horizontalOnly, verticalOnly } = this.props;
    return (
      <PanGestureHandler
        maxPointers={1}
        onGestureEvent={this._onGestureEvent}
        onHandlerStateChange={this._onGestureEvent}>
        <Animated.View
          style={[
            style,
            {
              transform: [
                {
                  translateX: !verticalOnly && this._transX,
                  translateY: !horizontalOnly && this._transY,
                },
              ],
            },
          ]}>
          {children}
        </Animated.View>
      </PanGestureHandler>
    );
  }
}
