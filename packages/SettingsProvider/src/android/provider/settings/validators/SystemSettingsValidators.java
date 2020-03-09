/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.provider.settings.validators;

import static android.provider.settings.validators.SettingsValidators.ANY_STRING_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.BOOLEAN_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.COMPONENT_NAME_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.DATE_FORMAT_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.LENIENT_IP_ADDRESS_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.NON_NEGATIVE_INTEGER_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.URI_VALIDATOR;
import static android.provider.settings.validators.SettingsValidators.VIBRATION_INTENSITY_VALIDATOR;

import android.annotation.Nullable;
import android.compat.annotation.UnsupportedAppUsage;
import android.content.ComponentName;
import android.hardware.display.ColorDisplayManager;
import android.os.BatteryManager;
import android.provider.Settings.System;
import android.util.ArrayMap;
import android.text.TextUtils;

import java.util.Map;

/**
 * Validators for System settings
 */
public class SystemSettingsValidators {
    /**
     * These are all public system settings
     *
     * <p>All settings in {@link System.SETTINGS_TO_BACKUP} array *must* have a non-null validator,
     * otherwise they won't be restored.
     */
    @UnsupportedAppUsage
    public static final Map<String, Validator> VALIDATORS = new ArrayMap<>();

    static {
        VALIDATORS.put(
                System.STAY_ON_WHILE_PLUGGED_IN,
                value -> {
                    try {
                        int val = Integer.parseInt(value);
                        return (val == 0)
                                || (val == BatteryManager.BATTERY_PLUGGED_AC)
                                || (val == BatteryManager.BATTERY_PLUGGED_USB)
                                || (val == BatteryManager.BATTERY_PLUGGED_WIRELESS)
                                || (val
                                        == (BatteryManager.BATTERY_PLUGGED_AC
                                                | BatteryManager.BATTERY_PLUGGED_USB))
                                || (val
                                        == (BatteryManager.BATTERY_PLUGGED_AC
                                                | BatteryManager.BATTERY_PLUGGED_WIRELESS))
                                || (val
                                        == (BatteryManager.BATTERY_PLUGGED_USB
                                                | BatteryManager.BATTERY_PLUGGED_WIRELESS))
                                || (val
                                        == (BatteryManager.BATTERY_PLUGGED_AC
                                                | BatteryManager.BATTERY_PLUGGED_USB
                                                | BatteryManager.BATTERY_PLUGGED_WIRELESS));
                    } catch (NumberFormatException e) {
                        return false;
                    }
                });
        VALIDATORS.put(System.END_BUTTON_BEHAVIOR, new InclusiveIntegerRangeValidator(0, 3));
        VALIDATORS.put(System.WIFI_USE_STATIC_IP, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.BLUETOOTH_DISCOVERABILITY, new InclusiveIntegerRangeValidator(0, 2));
        VALIDATORS.put(System.BLUETOOTH_DISCOVERABILITY_TIMEOUT, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(
                System.NEXT_ALARM_FORMATTED,
                new Validator() {
                    private static final int MAX_LENGTH = 1000;

                    @Override
                    public boolean validate(String value) {
                        // TODO: No idea what the correct format is.
                        return value == null || value.length() < MAX_LENGTH;
                    }
                });
        VALIDATORS.put(System.FONT_SCALE, new InclusiveFloatRangeValidator(0.80f, 1.3f));
        VALIDATORS.put(System.DIM_SCREEN, BOOLEAN_VALIDATOR);
        VALIDATORS.put(
                System.DISPLAY_COLOR_MODE,
                new Validator() {
                    @Override
                    public boolean validate(@Nullable String value) {
                        // Assume the actual validation that this device can properly handle this
                        // kind of
                        // color mode further down in ColorDisplayManager / ColorDisplayService.
                        try {
                            final int setting = Integer.parseInt(value);
                            final boolean isInFrameworkRange =
                                    setting >= ColorDisplayManager.COLOR_MODE_NATURAL
                                            && setting <= ColorDisplayManager.COLOR_MODE_AUTOMATIC;
                            final boolean isInVendorRange =
                                    setting >= ColorDisplayManager.VENDOR_COLOR_MODE_RANGE_MIN
                                            && setting
                                                    <= ColorDisplayManager
                                                            .VENDOR_COLOR_MODE_RANGE_MAX;
                            return isInFrameworkRange || isInVendorRange;
                        } catch (NumberFormatException | NullPointerException e) {
                            return false;
                        }
                    }
                });
        VALIDATORS.put(System.SCREEN_OFF_TIMEOUT, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(System.SCREEN_BRIGHTNESS_FOR_VR, new InclusiveIntegerRangeValidator(0, 255));
        VALIDATORS.put(System.SCREEN_BRIGHTNESS_MODE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.ADAPTIVE_SLEEP, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.MODE_RINGER_STREAMS_AFFECTED, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(System.MUTE_STREAMS_AFFECTED, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(System.VIBRATE_ON, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.NOTIFICATION_VIBRATION_INTENSITY, VIBRATION_INTENSITY_VALIDATOR);
        VALIDATORS.put(System.RING_VIBRATION_INTENSITY, VIBRATION_INTENSITY_VALIDATOR);
        VALIDATORS.put(System.HAPTIC_FEEDBACK_INTENSITY, VIBRATION_INTENSITY_VALIDATOR);
        VALIDATORS.put(System.RINGTONE, URI_VALIDATOR);
        VALIDATORS.put(System.NOTIFICATION_SOUND, URI_VALIDATOR);
        VALIDATORS.put(System.ALARM_ALERT, URI_VALIDATOR);
        VALIDATORS.put(System.TEXT_AUTO_REPLACE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.TEXT_AUTO_CAPS, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.TEXT_AUTO_PUNCTUATE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.TEXT_SHOW_PASSWORD, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.AUTO_TIME, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.AUTO_TIME_ZONE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.SHOW_GTALK_SERVICE_STATUS, BOOLEAN_VALIDATOR);
        VALIDATORS.put(
                System.WALLPAPER_ACTIVITY,
                new Validator() {
                    private static final int MAX_LENGTH = 1000;

                    @Override
                    public boolean validate(String value) {
                        if (value != null && value.length() > MAX_LENGTH) {
                            return false;
                        }
                        return ComponentName.unflattenFromString(value) != null;
                    }
                });
        VALIDATORS.put(
                System.TIME_12_24, new DiscreteValueValidator(new String[] {"12", "24", null}));
        VALIDATORS.put(System.DATE_FORMAT, DATE_FORMAT_VALIDATOR);
        VALIDATORS.put(System.SETUP_WIZARD_HAS_RUN, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.ACCELEROMETER_ROTATION, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.USER_ROTATION, new InclusiveIntegerRangeValidator(0, 3));
        VALIDATORS.put(System.DTMF_TONE_WHEN_DIALING, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.SOUND_EFFECTS_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.HAPTIC_FEEDBACK_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.POWER_SOUNDS_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.DOCK_SOUNDS_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.SHOW_WEB_SUGGESTIONS, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.WIFI_USE_STATIC_IP, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.ADVANCED_SETTINGS, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.SCREEN_AUTO_BRIGHTNESS_ADJ, new InclusiveFloatRangeValidator(-1, 1));
        VALIDATORS.put(System.VIBRATE_INPUT_DEVICES, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.MASTER_MONO, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.MASTER_BALANCE, new InclusiveFloatRangeValidator(-1.f, 1.f));
        VALIDATORS.put(System.NOTIFICATIONS_USE_RING_VOLUME, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.VIBRATE_IN_SILENT, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.MEDIA_BUTTON_RECEIVER, COMPONENT_NAME_VALIDATOR);
        VALIDATORS.put(System.HIDE_ROTATION_LOCK_TOGGLE_FOR_ACCESSIBILITY, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.VIBRATE_WHEN_RINGING, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.DTMF_TONE_TYPE_WHEN_DIALING, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.HEARING_AID, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.TTY_MODE, new InclusiveIntegerRangeValidator(0, 3));
        VALIDATORS.put(System.NOTIFICATION_LIGHT_PULSE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.POINTER_LOCATION, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.SHOW_TOUCHES, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.WINDOW_ORIENTATION_LISTENER_LOG, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.LOCKSCREEN_SOUNDS_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.LOCKSCREEN_DISABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.SIP_RECEIVE_CALLS, BOOLEAN_VALIDATOR);
        VALIDATORS.put(
                System.SIP_CALL_OPTIONS,
                new DiscreteValueValidator(new String[] {"SIP_ALWAYS", "SIP_ADDRESS_ONLY"}));
        VALIDATORS.put(System.SIP_ALWAYS, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.SIP_ADDRESS_ONLY, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.SIP_ASK_ME_EACH_TIME, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.POINTER_SPEED, new InclusiveFloatRangeValidator(-7, 7));
        VALIDATORS.put(System.LOCK_TO_APP_ENABLED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(
                System.EGG_MODE,
                new Validator() {
                    @Override
                    public boolean validate(@Nullable String value) {
                        try {
                            return Long.parseLong(value) >= 0;
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }
                });
        VALIDATORS.put(System.WIFI_STATIC_IP, LENIENT_IP_ADDRESS_VALIDATOR);
        VALIDATORS.put(System.WIFI_STATIC_GATEWAY, LENIENT_IP_ADDRESS_VALIDATOR);
        VALIDATORS.put(System.WIFI_STATIC_NETMASK, LENIENT_IP_ADDRESS_VALIDATOR);
        VALIDATORS.put(System.WIFI_STATIC_DNS1, LENIENT_IP_ADDRESS_VALIDATOR);
        VALIDATORS.put(System.WIFI_STATIC_DNS2, LENIENT_IP_ADDRESS_VALIDATOR);
        VALIDATORS.put(System.SHOW_BATTERY_PERCENT, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.NOTIFICATION_LIGHT_PULSE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.BUTTON_BACKLIGHT_ONLY_WHEN_PRESSED, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.NAVIGATION_BAR_SHOW, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.NAVIGATION_BAR_MODE_OVERLAY, ANY_STRING_VALIDATOR);
        VALIDATORS.put(System.KEY_HOME_LONG_PRESS_ACTION, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(System.KEY_HOME_DOUBLE_TAP_ACTION, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(System.KEY_MENU_ACTION, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(System.KEY_MENU_LONG_PRESS_ACTION, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(System.KEY_ASSIST_ACTION, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(System.KEY_ASSIST_LONG_PRESS_ACTION, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(System.KEY_APP_SWITCH_ACTION, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(System.KEY_APP_SWITCH_LONG_PRESS_ACTION, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(System.HOME_WAKE_SCREEN, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.BACK_WAKE_SCREEN, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.MENU_WAKE_SCREEN, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.ASSIST_WAKE_SCREEN, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.APP_SWITCH_WAKE_SCREEN, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.CAMERA_WAKE_SCREEN, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.CAMERA_SLEEP_ON_RELEASE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.CAMERA_LAUNCH, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.TOUCHSCREEN_GESTURE_HAPTIC_FEEDBACK, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.HIGH_TOUCH_SENSITIVITY_ENABLE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.ACCELEROMETER_ROTATION_ANGLES, NON_NEGATIVE_INTEGER_VALIDATOR);
        VALIDATORS.put(System.VOLUME_KEY_CURSOR_CONTROL, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.VOLUME_WAKE_SCREEN, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.VOLBTN_MUSIC_CONTROLS, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.TORCH_LONG_PRESS_POWER_GESTURE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.TORCH_LONG_PRESS_POWER_TIMEOUT, new InclusiveIntegerRangeValidator(0, 3600));
        VALIDATORS.put(System.VOLUME_ANSWER_CALL, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.SWAP_VOLUME_KEYS_ON_ROTATION, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.NAVIGATION_BAR_MENU_ARROW_KEYS, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.STATUS_BAR_QUICK_QS_PULLDOWN, new InclusiveIntegerRangeValidator(0, 2));
        VALIDATORS.put(System.DOUBLE_TAP_SLEEP_GESTURE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.LOCKSCREEN_PIN_SCRAMBLE_LAYOUT, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.STATUS_BAR_CLOCK, new InclusiveIntegerRangeValidator(0, 2));
        VALIDATORS.put(System.STATUS_BAR_AM_PM, new InclusiveIntegerRangeValidator(0, 2));
        VALIDATORS.put(System.STATUS_BAR_BATTERY_STYLE, new InclusiveIntegerRangeValidator(0, 2));
        VALIDATORS.put(System.STATUS_BAR_SHOW_BATTERY_PERCENT, new InclusiveIntegerRangeValidator(0, 2));
        VALIDATORS.put(System.STATUS_BAR_BRIGHTNESS_CONTROL, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.CLICK_PARTIAL_SCREENSHOT, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.INCREASING_RING, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.INCREASING_RING_START_VOLUME, new InclusiveFloatRangeValidator(0, 1));
        VALIDATORS.put(System.INCREASING_RING_RAMP_UP_TIME, new InclusiveFloatRangeValidator(5, 60));
        VALIDATORS.put(System.DISPLAY_CUTOUT_HIDDEN, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.FORCE_FULLSCREEN_CUTOUT_APPS, ANY_STRING_VALIDATOR);
        VALIDATORS.put(System.SWIPE_TO_SCREENSHOT, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.INCALL_FEEDBACK_VIBRATE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.DISPLAY_TEMPERATURE_DAY, new InclusiveIntegerRangeValidator(0, 100000));
        VALIDATORS.put(System.DISPLAY_TEMPERATURE_NIGHT, new InclusiveIntegerRangeValidator(0, 100000));
        VALIDATORS.put(System.DISPLAY_TEMPERATURE_MODE, new InclusiveIntegerRangeValidator(0, 4));
        VALIDATORS.put(System.DISPLAY_AUTO_OUTDOOR_MODE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.DISPLAY_READING_MODE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.DISPLAY_CABC, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.DISPLAY_COLOR_ENHANCE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.DISPLAY_AUTO_CONTRAST, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.DISPLAY_COLOR_ADJUSTMENT, new Validator() {
            @Override
            public boolean validate(String value) {
                String[] colorAdjustment = value == null ?
                        null : value.split(" ");
                if (colorAdjustment != null && colorAdjustment.length != 3) {
                    return false;
                }
                Validator floatValidator = new InclusiveFloatRangeValidator(0, 1);
                return colorAdjustment == null ||
                        floatValidator.validate(colorAdjustment[0]) &&
                        floatValidator.validate(colorAdjustment[1]) &&
                        floatValidator.validate(colorAdjustment[2]);
            }
        });
        VALIDATORS.put(System.DISPLAY_PICTURE_ADJUSTMENT, new Validator() {
            @Override
            public boolean validate(String value) {
                if (TextUtils.isEmpty(value)) {
                    return true;
                }
                final String[] sp = TextUtils.split(value, ",");
                for (String s : sp) {
                    final String[] sp2 = TextUtils.split(s, ":");
                    if (sp2.length != 2) {
                        return false;
                    }
                }
                return true;
            }
        });
        VALIDATORS.put(System.LIVE_DISPLAY_HINTED, new InclusiveIntegerRangeValidator(-3, 1));
        VALIDATORS.put(System.NETWORK_TRAFFIC_LOCATION, new InclusiveIntegerRangeValidator(0, 2));
        VALIDATORS.put(System.NETWORK_TRAFFIC_AUTOHIDE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.NETWORK_TRAFFIC_UNIT_TYPE, new InclusiveIntegerRangeValidator(0, 1));
        VALIDATORS.put(System.QS_ROWS_PORTRAIT, new InclusiveIntegerRangeValidator(1, 5));
        VALIDATORS.put(System.QS_ROWS_LANDSCAPE, new InclusiveIntegerRangeValidator(1, 2));
        VALIDATORS.put(System.QS_COLUMNS_PORTRAIT, new InclusiveIntegerRangeValidator(1, 5));
        VALIDATORS.put(System.QS_COLUMNS_LANDSCAPE, new InclusiveIntegerRangeValidator(1, 7));
        VALIDATORS.put(System.POCKET_JUDGE, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.NAVIGATION_BAR_HINT, BOOLEAN_VALIDATOR);
        VALIDATORS.put(System.NAV_BAR_COMPACT_LAYOUT, BOOLEAN_VALIDATOR);
    }
}
