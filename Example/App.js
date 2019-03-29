import React from 'react';
import { Text, View, Button } from 'react-native';
import { Transitioning } from 'react-native-reanimated';

function shuffleArray(array) {
  for (let i = array.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [array[i], array[j]] = [array[j], array[i]];
  }
}

const SlideIn = ({ children, ...props }) => <View {...props}>{children}</View>;

const Hour = ({ hour, min, pm }) => (
  <View
    style={{
      flexDirection: 'row',
      alignItems: 'flex-end',
    }}>
    <SlideIn>
      <Text style={{ fontSize: 40, textAlignVertical: 'center' }}>
        {hour}:{min}
      </Text>
    </SlideIn>
    <SlideIn delay={20}>
      <Text style={{ color: 'gray', marginBottom: 7, marginLeft: 4 }}>
        {pm ? 'PM' : 'AM'}
      </Text>
    </SlideIn>
  </View>
);

const Location = ({ label, name, delay }) => (
  <>
    <Text style={{ color: 'gray' }}>{label}</Text>
    <Text style={{ fontSize: 26 }}>{name}</Text>
  </>
);

const Spacer = ({ height }) => <View style={{ flex: 1, maxHeight: height }} />;

const Wat = () => (
  <View style={{ marginHorizontal: 20, flexGrow: 1 }}>
    <Spacer height={20} />
    <View
      style={{
        flexDirection: 'row',
        alignItems: 'flex-end',
        justifyContent: 'space-between',
      }}>
      <Hour hour="2" min="20" pm />
      <SlideIn style={{ alignSelf: 'center' }}>
        <Text style={{ fontSize: 40 }}>✈</Text>
      </SlideIn>
      <Hour hour="4" min="55" pm />
    </View>
    <Spacer height={20} />
    <Location label="From" name="Chicago, IL (ORD)" delay={40} />
    <Spacer height={20} />
    <Location label="To" name="San Francisco, CA (SFO)" delay={80} />
    <Spacer height={20} />
    <Text style={{ color: 'gray' }}>Notes</Text>
    <Text>
      Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec tincidunt
      velit ipsum, ut scelerisque risus sodales at. Sed vehicula ligula diam.
      Sed viverra molestie lobortis. Pellentesque ornare lorem ut nisi molestie,
      at tempor est efficitur. Vivamus sem mi, ullamcorper sit amet dolor in,
      fermentum suscipit eros.
    </Text>
    <View style={{ flex: 2 }} />
  </View>
);

const Warning = () => (
  <Transitioning.View
    inTransition={{ type: 'fade' }}
    outTransition={{ type: 'fade' }}
    style={{ backgroundColor: 'red', padding: 10, paddingHorizontal: 20 }}>
    <Text style={{ color: 'white' }}>Lorem ipsum warning sit amet!</Text>
  </Transitioning.View>
);

class ExampleApp extends React.Component {
  state = {
    size: 80,
    visible: true,
    warning: false,
    rotate: '30deg',
    align: 'flex-start',
  };
  root = React.createRef();
  onClick = () => {
    this.setState({
      size: this.state.size + 20,
      visible: !this.state.visible,
      rotate: '75deg',
    });
  };
  setStateAnimated = nextState => {
    this.root.current.animateNextTransition();
    this.setState(nextState);
  };
  render() {
    const { visible, warning } = this.state;
    const keys = ['red', 'blue'];
    shuffleArray(keys);
    return (
      <View style={{ flex: 1 }}>
        <View style={{ flexDirection: 'row', justifyContent: 'space-around' }}>
          <Button
            style={{ width: 150 }}
            title="show/hide"
            onPress={() =>
              this.setStateAnimated({ visible: !this.state.visible })
            }
          />
          <Button
            style={{ flex: 1 }}
            title="warning on/off"
            onPress={() =>
              this.setStateAnimated({ warning: !this.state.warning })
            }
          />
        </View>
        {/* <Transitioning.View
          key={keys[0]}
          style={{ backgroundColor: keys[0], width: size, height: size }}
        /> */}
        <Transitioning.Root
          ref={this.root}
          style={{ flex: 1 }}
          outTransition={{ type: 'slide-top' }}
          inTransition={{ type: 'slide-bottom' }}>
          {warning ? <Warning /> : null}
          {visible ? <Wat /> : null}
        </Transitioning.Root>
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
