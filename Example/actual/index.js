import React, { Component } from 'react';
import {
  StyleSheet,
  View,
  Dimensions,
  KeyboardAvoidingView,
} from 'react-native';
import { PanGestureHandler, State } from 'react-native-gesture-handler';
import Animated from 'react-native-reanimated';

import List from './List';
import Summary from './Summary';

const {
  set,
  cond,
  eq,
  add,
  multiply,
  lessThan,
  abs,
  modulo,
  round,
  interpolate,
  divide,
  sub,
  color,
  Value,
  event,
} = Animated;

export default class Example extends Component {
  static navigationOptions = {
    title: 'August 2018',
  };
  inset = new Value(0);
  dragY = new Value(0);
  draggingState = new Value(0);
  render() {
    const values = {
      inset: this.inset,
      dragY: this.dragY,
      draggingState: this.draggingState,
    };
    return (
      <KeyboardAvoidingView
        style={styles.container}
        behavior="padding"
        keyboardVerticalOffset={63}
        enabled>
        <List {...values} />
        <Summary {...values} />
      </KeyboardAvoidingView>
    );
  }
}

const CIRCLE_SIZE = 70;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5FCFF',
  },
});
