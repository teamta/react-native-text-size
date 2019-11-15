import { NativeModules } from "react-native";

const { TextSize } = NativeModules;

export default {
  calculateSize(text,font,size) {
    return TextSize.calculateSize(text,font,size);
  }
};
