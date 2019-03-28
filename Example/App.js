import React from 'react';
import { View, Button } from 'react-native';
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
  };
  onClick = () => {
    this.setState({ size: this.state.size + 20, visible: !this.state.visible });
  };
  render() {
    const { size, visible } = this.state;
    const keys = ['red', 'blue'];
    shuffleArray(keys);
    return (
      <View>
        <Button title="Click Me" onPress={this.onClick} />
        <Transitioning.View
          key={keys[0]}
          style={{ backgroundColor: keys[0], width: size, height: size }}
        />
        {visible ? (
          <Transitioning.View
            key="dupa"
            style={{ backgroundColor: 'green', width: size, height: size }}
          />
        ) : null}
        <Transitioning.View
          key={keys[1]}
          style={{ backgroundColor: keys[1], width: size, height: size }}
        />
      </View>
    );
  }
}

export default ExampleApp;
