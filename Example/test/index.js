import React, { Component } from 'react';
import { StyleSheet, View } from 'react-native';

import Animated, { Easing } from 'react-native-reanimated';
import Interactable from './Interactable';

import Wix from 'react-native-interactable';

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

export default class Example extends Component {
  render() {
    const props = {
      snapPoints: [{ x: 0 }, { x: -200 }],
      dragWithSpring: { tension: 2000, damping: 0.5 },
      style: styles.box,
      horizontalOnly: true,
    };
    return (
      <View style={styles.container}>
        <Wix.View
          springPoints={[{ x: 0, tension: 6000, damping: 0.5 }]}
          horizontalOnly
          dragWithSpring={{ tension: 2000, damping: 0.5 }}
          style={styles.box}
        />
        <Wix.View {...props} />
        <Interactable {...props} />
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
