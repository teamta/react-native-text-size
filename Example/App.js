/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */


import React, { Component } from 'react';

import {
  SafeAreaView,
  StyleSheet,
  Button,
  View,
  Text,
  StatusBar,
} from 'react-native';

import TextSize from "react-native-text-size"

export default class App extends Component {

  constructor() {
    super();
  }

  onButton = async ()=>{

    let valule = await TextSize.calculateSize("Hello","OstrichSans-Medium",30);
    console.warn(valule);
  }

  render() {
		return (
      <SafeAreaView>
        <View>
        <Button
            title="Show Size"
            onPress={() => this.onButton()}
        />
        </View>
      </SafeAreaView>
    )
  }

}
