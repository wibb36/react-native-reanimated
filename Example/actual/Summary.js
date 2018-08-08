import React, { Component } from 'react';
import { StyleSheet, View, Text, TextInput, Platform } from 'react-native';
import { PanGestureHandler, State } from 'react-native-gesture-handler';
import Animated from 'react-native-reanimated';
import Interactable from '../Interactable';

const { cond, eq, set, neq, lessThan, sub, add, multiply, Value } = Animated;

const Line = ({ amount, name }) => (
  <View style={styles.summaryLine}>
    <Text style={styles.summaryAmount}>{amount.toFixed(2)}</Text>
    <Text style={styles.summaryName}>{name}</Text>
  </View>
);

const Header = ({ style }) => (
  <View style={styles.header}>
    <Text style={styles.headerText}>To Budget: -111.01</Text>
    <View style={styles.handle} />
  </View>
);

const CONTENT_HEIGHT = Platform.select({ ios: 94, android: 106 });

export default class Summary extends Component {
  constructor(props) {
    super(props);

    this.animatedValueY = new Value(0);

    this.interactionsEnabled = neq(props.draggingState, State.ACTIVE);
    const previousDragY = new Value(0);
    const accumulateY = new Value(0);
    this.beforeY = cond(
      eq(props.draggingState, State.ACTIVE),
      [
        cond(
          lessThan(previousDragY, props.dragY),
          set(
            this.animatedValueY,
            add(this.animatedValueY, sub(props.dragY, previousDragY))
          )
        ),
        set(previousDragY, props.dragY),
      ],
      [set(previousDragY, 0), set(accumulateY, 0)]
    );
    this.afterY = set(this.props.inset, multiply(-1, this.animatedValueY));
  }
  render() {
    return (
      <View style={styles.container}>
        <Header />
        <Interactable.View
          snapPoints={[{ y: 0 }, { y: -CONTENT_HEIGHT }]}
          verticalOnly
          boundaries={{ bottom: 0 }}
          style={styles.sheet}
          animatedValueY={this.animatedValueY}
          enabled={this.interactionsEnabled}
          beforeY={this.beforeY}
          afterY={this.afterY}>
          <Header />
          <View style={styles.summary}>
            <Line amount={-111} name="Available Funds" />
            <Line amount={0} name="Overspent in Invalid Date" />
            <Line amount={-0.01} name="Budgeted" />
            <Line amount={0} name="For Next Month" />
          </View>
        </Interactable.View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  header: {
    backgroundColor: '#8268F1',
    padding: 15,
    alignItems: 'center',
  },
  handle: {
    height: 4,
    width: 30,
    backgroundColor: '#E4E0FB',
    borderRadius: 2,
    position: 'absolute',
    top: 5,
  },
  headerText: {
    textAlign: 'center',
    fontWeight: 'bold',
    color: '#E4E0FB',
  },
  sheet: {
    position: 'absolute',
    left: 0,
    right: 0,
    top: 0,
  },
  container: {},
  summary: {
    padding: 15,
    paddingBottom: 500,
    backgroundColor: '#43404C',
  },
  summaryLine: {
    flexDirection: 'row',
  },
  summaryAmount: {
    color: 'white',
    fontWeight: 'bold',
    flex: 1,
    textAlign: 'right',
    marginRight: 10,
  },
  summaryName: {
    color: 'white',
    flex: 2,
  },
  hidden: {
    opacity: 0,
  },
});
