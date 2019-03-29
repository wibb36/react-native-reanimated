import React from 'react';
import { Text, View, Button } from 'react-native';
import { Transitioning } from 'react-native-reanimated';

function shuffleArray(array) {
  for (let i = array.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [array[i], array[j]] = [array[j], array[i]];
  }
}

class ExampleApp extends React.Component {
  state = {
    size: 80,
    visible: false,
    rotate: '30deg',
    align: 'flex-start',
  };
  onClick = () => {
    this.setState({
      size: this.state.size + 20,
      visible: !this.state.visible,
      rotate: '75deg',
    });
  };
  render() {
    const { size, visible, rotate, snap } = this.state;
    const keys = ['red', 'blue'];
    shuffleArray(keys);
    return (
      <View>
        <Button title="show" onPress={() => this.setState({ visible: true })} />
        <Button
          title="hide"
          onPress={() => this.setState({ visible: false })}
        />
        <Button
          title="enlarge by 20px"
          onPress={() => this.setState({ size: size + 20 })}
        />
        <Button
          title="snap left"
          onPress={() => this.setState({ snap: 'flex-start' })}
        />
        <Button
          title="snap right"
          onPress={() => this.setState({ snap: 'flex-end' })}
        />
        {/* <Transitioning.View
          key={keys[0]}
          style={{ backgroundColor: keys[0], width: size, height: size }}
        /> */}
        {visible ? (
          <Transitioning.View
            key="dupa"
            inTransition={{ type: 'fade' }}
            outTransition={{
              type: 'slide-right',
              durationMs: 1000,
              interpolation: 'linear',
            }}
            changeTransition={{ durationMs: 2000, interpolation: 'linear' }}
            style={{
              backgroundColor: 'green',
              width: 80,
              height: 80,
              alignSelf: snap,
            }}
          />
        ) : null}
        {/* <Transitioning.View
          key={keys[1]}
          style={{ backgroundColor: keys[1], width: size, height: size }}
        /> */}
        {/* {visible ? (
          <Transitioning.View
            inTransition={{ type: 'slide-bottom', durationMs: 2000 }}
            outTransition={{ type: 'fade' }}
            style={{
              backgroundColor: 'green',
              width: size,
              height: size,
              alignItems: 'center',
            }}>
            <Transitioning.View>
              <Text>WAT!?</Text>
            </Transitioning.View>
          </Transitioning.View>
        ) : null} */}
      </View>
    );
  }
}

export default ExampleApp;
