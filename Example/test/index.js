import React, { Component } from 'react';
import { StyleSheet, View } from 'react-native';

import Animated, { Easing } from 'react-native-reanimated';
import Interactable from './Interactable';

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

function interpolateSingle(value, inputRange, outputRange, offset) {
  const inS = inputRange[offset];
  const inE = inputRange[offset + 1];
  const outS = outputRange[offset];
  const outE = outputRange[offset + 1];
  const progress = divide(sub(value, inS), sub(inE, inS));
  return add(outS, multiply(progress, sub(outE, outS)));
}

function interpolate(value, inputRange, outputRange, offset = 0) {
  if (inputRange.length - offset === 2) {
    return interpolateSingle(value, inputRange, outputRange, offset);
  }
  return cond(
    lessThan(value, inputRange[offset + 1]),
    interpolateSingle(value, inputRange, outputRange, offset),
    interpolate(value, inputRange, outputRange, offset + 1)
  );
}

export default class Example extends Component {
  render() {
    return (
      <View style={styles.container}>
        <Interactable snapPoints={[{ x: 0 }, { x: -200 }]} style={styles.box} />
      </View>
    );
  }
}

const BOX_SIZE = 100;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  box: {
    width: BOX_SIZE,
    height: BOX_SIZE,
    borderColor: '#F5FCFF',
    alignSelf: 'center',
    backgroundColor: 'plum',
    margin: BOX_SIZE / 2,
  },
});
