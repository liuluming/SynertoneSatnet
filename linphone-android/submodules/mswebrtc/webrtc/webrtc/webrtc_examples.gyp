# Copyright (c) 2012 The WebRTC project authors. All Rights Reserved.
#
# Use of this source code is governed by a BSD-style license
# that can be found in the LICENSE file in the root of the source
# tree. An additional intellectual property rights grant can be found
# in the file PATENTS.  All contributing project authors may
# be found in the AUTHORS file in the root of the source tree.
{
  'includes': ['build/common.gypi'],
  'targets': [],
  'conditions': [
    ['OS=="android"', {
      'targets': [
        {
          'target_name': 'libwebrtcdemo-jni',
          'type': 'loadable_module',
          'dependencies': [
            '<(webrtc_root)/common.gyp:webrtc_common',
            '<(webrtc_root)/modules/modules.gyp:video_render_module_internal_impl',
            '<(webrtc_root)/system_wrappers/system_wrappers.gyp:system_wrappers_default',
            '<(webrtc_root)/test/test.gyp:channel_transport',
            '<(webrtc_root)/voice_engine/voice_engine.gyp:voice_engine',
          ],
          'sources': [
            'examples/android/media_demo/jni/jni_helpers.cc',
            'examples/android/media_demo/jni/on_load.cc',
            'examples/android/media_demo/jni/voice_engine_jni.cc',
          ],
          'variables': {
            # This library uses native JNI exports; tell GYP so that the
            # required symbols will be kept.
            'use_native_jni_exports': 1,
          },
          'link_settings': {
            'libraries': [
              '-llog',
              '-lGLESv2',
              '-lOpenSLES',
            ],
          }
        },
        {
          'target_name': 'WebRTCDemo',
          'type': 'none',
          'dependencies': [
            'libwebrtcdemo-jni',
            '<(modules_java_gyp_path):*',
          ],
          'actions': [
            {
              # TODO(yujie.mao): Convert building of the demo to a proper GYP
              # target so this action is not needed once chromium's
              # apk-building machinery can be used. (crbug.com/225101)
              'action_name': 'build_webrtcdemo_apk',
              'variables': {
                'android_webrtc_demo_root': '<(webrtc_root)/examples/android/media_demo',
                'ant_log': '../../../<(INTERMEDIATE_DIR)/ant.log', # ../../.. to compensate for the cd below.
              },
              'inputs' : [
                '<(PRODUCT_DIR)/lib.java/audio_device_module_java.jar',
                '<(PRODUCT_DIR)/libwebrtcdemo-jni.so',
                '<!@(find <(android_webrtc_demo_root)/src -name "*.java")',
                '<!@(find <(android_webrtc_demo_root)/res -type f)',
                '<(android_webrtc_demo_root)/AndroidManifest.xml',
                '<(android_webrtc_demo_root)/build.xml',
                '<(android_webrtc_demo_root)/project.properties',
              ],
              'outputs': ['<(PRODUCT_DIR)/WebRTCDemo-debug.apk'],
              'action': [
                'bash', '-ec',
                'rm -fr <(_outputs) <(android_webrtc_demo_root)/{bin,libs,gen,obj} && '
                'mkdir -p <(INTERMEDIATE_DIR) && ' # Must happen _before_ the cd below
                'mkdir -p <(android_webrtc_demo_root)/libs/<(android_app_abi) && '
                'cp <(PRODUCT_DIR)/lib.java/audio_device_module_java.jar <(android_webrtc_demo_root)/libs/ &&'
                '<(android_strip) -o <(android_webrtc_demo_root)/libs/<(android_app_abi)/libwebrtcdemo-jni.so <(PRODUCT_DIR)/libwebrtcdemo-jni.so && '
                'cd <(android_webrtc_demo_root) && '
                '{ ANDROID_SDK_ROOT=<(android_sdk_root) '
                'ant debug > <(ant_log) 2>&1 || '
                '  { cat <(ant_log) ; exit 1; } } && '
                'cd - > /dev/null && '
                'cp <(android_webrtc_demo_root)/bin/WebRTCDemo-debug.apk <(_outputs)'
              ],
            },
          ],
        },
      ],
    }],
  ],
}
