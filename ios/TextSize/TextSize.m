//  Created by react-native-create-bridge

#import <UIKit/UIKit.h>
#import "TextSize.h"
// import RCTBridge
#if __has_include(<React/RCTBridge.h>)
#import <React/RCTBridge.h>
#elif __has_include(“RCTBridge.h”)
#import “RCTBridge.h”
#else
#import “React/RCTBridge.h” // Required when used as a Pod in a Swift project
#endif

// import RCTEventDispatcher
#if __has_include(<React/RCTEventDispatcher.h>)
#import <React/RCTEventDispatcher.h>
#elif __has_include(“RCTEventDispatcher.h”)
#import “RCTEventDispatcher.h”
#else
#import “React/RCTEventDispatcher.h” // Required when used as a Pod in a Swift project
#endif



#define TELEMETRY_EVENT @"TelemetryEvent"
#define VIDEO_WATCH_DATA @"VideoWatchData"

@interface TextSize ()
{
    
}
@end

@implementation TextSize
@synthesize bridge = _bridge;

// Export a native module
// https://facebook.github.io/react-native/docs/native-modules-ios.html
RCT_EXPORT_MODULE();

// Export methods to a native module
// https://facebook.github.io/react-native/docs/native-modules-ios.html

RCT_EXPORT_METHOD(calculateSize:(NSString *) text fontName:(NSString *) fontName fontSize:(float ) fontSize resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {
    dispatch_async(dispatch_get_main_queue(), ^{


        UILabel *textLabel = [[UILabel alloc]init];
        textLabel.text = text;
        textLabel.numberOfLines = 0;
        [textLabel setFont:[UIFont fontWithName:fontName size:(CGFloat)fontSize]];
        [textLabel sizeToFit];
        
        NSNumber* width    =  [NSNumber numberWithFloat:textLabel.intrinsicContentSize.width];
        NSNumber* height   =  [NSNumber numberWithFloat:textLabel.intrinsicContentSize.height];
        NSDictionary* size =  [[NSDictionary alloc] initWithObjectsAndKeys:width,@"width",height,@"height",nil];
        
        resolve( size);
    });
}

- (NSArray<NSString *> *)supportedEvents
{
    return @[];
}
@end
