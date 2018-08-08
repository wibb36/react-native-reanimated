import React, { Component } from 'react';
import {
  StyleSheet,
  View,
  Text,
  TextInput,
  FlatList,
  ScrollView,
} from 'react-native';
import {
  PanGestureHandler,
  NativeViewGestureHandler,
} from 'react-native-gesture-handler';
import Animated from 'react-native-reanimated';

const { event } = Animated;

const yellow = '#FEFEDD';

const DATA = [
  { name: 'Investments and Savings', amount: 0, color: yellow },
  { name: 'Health Insurance', amount: 999.1 },
  { name: 'Savings', amount: 0 },
  { name: 'Unusual', amount: 111, color: yellow },
  { name: 'Reimbursement', amount: 12.5 },
  { name: 'Business', amount: 18.3 },
  { name: 'Big Items', amount: 111 },
  { name: 'Big Projects', amount: 55 },
  { name: 'Travel', amount: 56 },
  { name: 'Usual Expenses', amount: 0.01, color: yellow },
  { name: 'Gifts', amount: 0.01 },
  { name: 'Bills', amount: 119 },
  { name: 'Bills (Flexible)', amount: 75 },
  { name: 'Food', amount: 7 },
  { name: 'Fun', amount: 0 },
];

const Item = ({ item }) => (
  <View style={[styles.item, { backgroundColor: item.color }]}>
    <Text>{item.name}</Text>
    <TextInput style={styles.input} value={item.amount.toFixed(2)} />
  </View>
);

const Separator = () => <View style={styles.separator} />;

/* We can't use animated value as an onScroll for flatlist, it is because flatlist
 * already registers onScroll function in order to handle incremental loading of
 * the content. In order for this to work we wrap flatlist in PanGestureHandler
 * that gives us similar data on when the view is panned. In that case we need
 * though for the PanGestureHandler and flatlist's ScrollView to work simultaniously.
 * To do that we wrap <ScrollView> with <NativeViewGestreHandler> wrapper that
 * allows use to use gesture-handler's `simultaneousHandlers` option that in turn
 * allow for such an interaction to be defined.
 */
const Scroller = ({ handlerRef, ...props }) => (
  <NativeViewGestureHandler ref={handlerRef}>
    <ScrollView {...props} />
  </NativeViewGestureHandler>
);

export default class List extends Component {
  constructor(props) {
    super(props);
    this.scroll = React.createRef();
    this.onPan = event([
      {
        nativeEvent: {
          translationY: props.dragY,
          state: props.draggingState,
        },
      },
    ]);
  }
  render() {
    return (
      <PanGestureHandler
        minDist={0}
        simultaneousHandlers={this.scroll}
        onGestureEvent={this.onPan}
        onHandlerStateChange={this.onPan}>
        <Animated.View style={styles.flex}>
          <FlatList
            data={DATA}
            renderItem={({ item }) => <Item item={item} />}
            keyboardDismissMode="on-drag"
            keyExtractor={item => item.name}
            renderScrollComponent={props => (
              <Scroller handlerRef={this.scroll} {...props} />
            )}
            ItemSeparatorComponent={Separator}
          />
          <Animated.View
            style={{
              height: this.props.inset,
              width: 50,
            }}
          />
        </Animated.View>
      </PanGestureHandler>
    );
  }
}

const styles = StyleSheet.create({
  item: {
    paddingVertical: 10,
    paddingHorizontal: 15,
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  separator: {
    height: StyleSheet.hairlineWidth,
    backgroundColor: '#a7a7a7',
  },
  input: {
    padding: 0,
  },
  flex: {
    flex: 1,
  },
});
