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
  pow,
  multiply,
  lessThan,
  abs,
  startClock,
  stopClock,
  debug,
  Value,
  Clock,
  event,
} = Animated;

const REST_SPEED_THRESHOLD = 0.001;
const DEFAULT_SNAP_TENSION = 300;
const DEFAULT_SNAP_DAMPING = 0.7;

function sq(x) {
  return multiply(x, x);
}

function snapDist(target, pt) {
  if (pt.y === undefined) {
    return sq(sub(target.x, pt.x));
  } else if (pt.x === undefined) {
    return sq(sub(target.y, pt.y));
  }
  return add(sq(sub(target.x, pt.x)), sq(sub(target.y, pt.y)));
}

function snapTo(target, snapPoints, best) {
  const dist = new Value(0);
  const snap = pt => [
    set(best.tension, pt.tension || DEFAULT_SNAP_TENSION),
    set(best.damping, pt.damping || DEFAULT_SNAP_DAMPING),
    set(best.x, pt.x || 0),
    set(best.y, pt.y || 0),
  ];
  return [
    set(dist, snapDist(target, snapPoints[0])),
    ...snap(snapPoints[0]),
    ...snapPoints.map(pt => {
      const newDist = snapDist(target, pt);
      return cond(lessThan(newDist, dist), [set(dist, newDist), ...snap(pt)]);
    }),
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

    const gesture = { x: new Value(0), y: new Value(0) };
    const state = new Value(-1);

    this._onGestureEvent = event([
      {
        nativeEvent: {
          translationX: gesture.x,
          translationY: gesture.y,
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

    const permBuckets = [[], [], []];

    const addSpring = (anchor, tension, buckets = permBuckets) => {
      buckets[0].push(springBehavior(dt, target, obj, anchor, tension));
    };

    const addFriction = (damping, buckets = permBuckets) => {
      buckets[1].push(frictionBehavior(dt, target, obj, damping));
    };

    const dragAnchor = { x: new Value(0), y: new Value(0) };
    const dragBuckets = [[], [], []];
    if (props.dragWithSpring) {
      const { tension, damping } = props.dragWithSpring;
      addSpring(dragAnchor, tension, dragBuckets);
      addFriction(damping, dragBuckets);
    } else {
      dragBuckets[0].push(anchorBehavior(dt, target, obj, dragAnchor));
    }

    if (props.springPoints) {
      props.springPoints.forEach(pt => {
        addSpring(pt, pt.tension);
        if (pt.damping) {
          addFriction(pt.damping);
        }
      });
    }

    const snapBuckets = [[], [], []];
    const snapAnchor = {
      x: new Value(0),
      y: new Value(0),
      tension: new Value(DEFAULT_SNAP_TENSION),
      damping: new Value(DEFAULT_SNAP_DAMPING),
    };
    const updateSnapTo = snapTo(target, props.snapPoints, snapAnchor);

    addSpring(snapAnchor, snapAnchor.tension, snapBuckets);
    addFriction(snapAnchor.damping, snapBuckets);

    // behaviors can go under one of three buckets depending on their priority
    // we append to each bucket but in Interactable behaviors get added to the
    // front, so we join in reverse order and then reverse the array.
    const sortBuckets = specialBuckets => ({
      x: specialBuckets.map((b, idx) =>
        [...permBuckets[idx], ...b].reverse().map(b => b.x)
      ),
      y: specialBuckets.map((b, idx) =>
        [...permBuckets[idx], ...b].reverse().map(b => b.y)
      ),
    });
    const dragBehaviors = sortBuckets(dragBuckets);
    const snapBehaviors = sortBuckets(snapBuckets);

    const shouldStop = lessThan(
      add(sq(obj.vx), sq(obj.vy)),
      REST_SPEED_THRESHOLD * REST_SPEED_THRESHOLD
    );

    const trans = (axis, vaxis) => {
      const dragging = new Value(0);
      const start = new Value(0);
      const x = target[axis];
      const vx = obj[vaxis];
      const anchor = dragAnchor[axis];
      const drag = gesture[axis];
      const update = set(x, add(x, multiply(vx, dt)));
      return cond(
        eq(state, State.ACTIVE),
        [
          cond(dragging, 0, [
            startClock(clock),
            set(dragging, 1),
            set(start, x),
          ]),
          set(anchor, add(start, drag)),
          cond(dt, dragBehaviors[axis]),
          update,
        ],
        [
          cond(dragging, [updateSnapTo, set(dragging, 0)]),
          cond(dt, snapBehaviors[axis]),
          cond(shouldStop, stopClock(clock)),
          update,
        ]
      );
    };

    this._transX = trans('x', 'vx');
    this._transY = trans('y', 'vy');
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
