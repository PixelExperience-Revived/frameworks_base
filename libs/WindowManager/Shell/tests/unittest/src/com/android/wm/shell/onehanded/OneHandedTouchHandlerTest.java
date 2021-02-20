/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.android.wm.shell.onehanded;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.spy;

import android.testing.AndroidTestingRunner;

import androidx.test.filters.SmallTest;

import com.android.wm.shell.common.ShellExecutor;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SmallTest
@RunWith(AndroidTestingRunner.class)
public class OneHandedTouchHandlerTest extends OneHandedTestCase {
    boolean mIsEventCallback = false;

    private OneHandedTouchHandler mTouchHandler;
    private OneHandedTimeoutHandler mSpiedTimeoutHandler;
    private OneHandedTouchHandler.OneHandedTouchEventCallback mTouchEventCallback =
            () -> mIsEventCallback = true;
    @Mock
    private ShellExecutor mMockShellMainExecutor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mSpiedTimeoutHandler = spy(new OneHandedTimeoutHandler(mMockShellMainExecutor));
        mTouchHandler = new OneHandedTouchHandler(mSpiedTimeoutHandler, mMockShellMainExecutor);
    }

    @Test
    public void testRegisterTouchEventListener() {
        OneHandedTouchHandler.OneHandedTouchEventCallback callback = () -> {
        };
        mTouchHandler.registerTouchEventListener(callback);

        assertThat(mIsEventCallback).isFalse();
    }

    @Test
    public void testOneHandedDisabled_shouldDisposeInputChannel() {
        mTouchHandler.onOneHandedEnabled(false);

        assertThat(mTouchHandler.mInputMonitor).isNull();
        assertThat(mTouchHandler.mInputEventReceiver).isNull();
    }

    @Ignore("b/167943723, refactor it and fix it")
    @Test
    public void testOneHandedEnabled_monitorInputChannel() {
        mTouchHandler.onOneHandedEnabled(true);

        assertThat(mTouchHandler.mInputMonitor).isNotNull();
        assertThat(mTouchHandler.mInputEventReceiver).isNotNull();
    }
}