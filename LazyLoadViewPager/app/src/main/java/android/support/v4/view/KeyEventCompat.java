/*
 * Copyright (C) 2011 The Android Open Source Project
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

package android.support.v4.view;

import android.view.KeyEvent;
import android.view.View;

/**
 * Helper for accessing features in {@link KeyEvent} introduced after
 * API level 4 in a backwards compatible fashion.
 */
public final class KeyEventCompat {
    /**
     * Interface for the full API.
     */
    interface KeyEventVersionImpl {
        int normalizeMetaState(int metaState);
        boolean metaStateHasModifiers(int metaState, int modifiers);
        boolean metaStateHasNoModifiers(int metaState);
        boolean isCtrlPressed(KeyEvent event);
    }

    /**
     * Interface implementation that doesn't use anything about v4 APIs.
     */
    static class BaseKeyEventVersionImpl implements KeyEventVersionImpl {
        private static final int META_MODIFIER_MASK =
                KeyEvent.META_SHIFT_ON | KeyEvent.META_SHIFT_LEFT_ON | KeyEvent.META_SHIFT_RIGHT_ON
                        | KeyEvent.META_ALT_ON | KeyEvent.META_ALT_LEFT_ON | KeyEvent.META_ALT_RIGHT_ON
                        | KeyEvent.META_SYM_ON;

        // Mask of all lock key meta states.
        private static final int META_ALL_MASK = META_MODIFIER_MASK;

        private static int metaStateFilterDirectionalModifiers(int metaState,
                                                               int modifiers, int basic, int left, int right) {
            final boolean wantBasic = (modifiers & basic) != 0;
            final int directional = left | right;
            final boolean wantLeftOrRight = (modifiers & directional) != 0;

            if (wantBasic) {
                if (wantLeftOrRight) {
                    throw new IllegalArgumentException("bad arguments");
                }
                return metaState & ~directional;
            } else if (wantLeftOrRight) {
                return metaState & ~basic;
            } else {
                return metaState;
            }
        }

        @Override
        public int normalizeMetaState(int metaState) {
            if ((metaState & (KeyEvent.META_SHIFT_LEFT_ON | KeyEvent.META_SHIFT_RIGHT_ON)) != 0) {
                metaState |= KeyEvent.META_SHIFT_ON;
            }
            if ((metaState & (KeyEvent.META_ALT_LEFT_ON | KeyEvent.META_ALT_RIGHT_ON)) != 0) {
                metaState |= KeyEvent.META_ALT_ON;
            }
            return metaState & META_ALL_MASK;
        }

        @Override
        public boolean metaStateHasModifiers(int metaState, int modifiers) {
            metaState = normalizeMetaState(metaState) & META_MODIFIER_MASK;
            metaState = metaStateFilterDirectionalModifiers(metaState, modifiers,
                    KeyEvent.META_SHIFT_ON, KeyEvent.META_SHIFT_LEFT_ON, KeyEvent.META_SHIFT_RIGHT_ON);
            metaState = metaStateFilterDirectionalModifiers(metaState, modifiers,
                    KeyEvent.META_ALT_ON, KeyEvent.META_ALT_LEFT_ON, KeyEvent.META_ALT_RIGHT_ON);
            return metaState == modifiers;
        }

        @Override
        public boolean metaStateHasNoModifiers(int metaState) {
            return (normalizeMetaState(metaState) & META_MODIFIER_MASK) == 0;
        }

        @Override
        public boolean isCtrlPressed(KeyEvent event) {
            return false;
        }
    }

    /**
     * Interface implementation for devices with at least v11 APIs.
     */
    static class HoneycombKeyEventVersionImpl extends BaseKeyEventVersionImpl {
        @Override
        public int normalizeMetaState(int metaState) {
            return KeyEventCompatHoneycomb.normalizeMetaState(metaState);
        }

        @Override
        public boolean metaStateHasModifiers(int metaState, int modifiers) {
            return KeyEventCompatHoneycomb.metaStateHasModifiers(metaState, modifiers);
        }

        @Override
        public boolean metaStateHasNoModifiers(int metaState) {
            return KeyEventCompatHoneycomb.metaStateHasNoModifiers(metaState);
        }

        @Override
        public boolean isCtrlPressed(KeyEvent event) {
            return KeyEventCompatHoneycomb.isCtrlPressed(event);
        }
    }

    /**
     * Select the correct implementation to use for the current platform.
     */
    static final KeyEventVersionImpl IMPL;
    static {
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            IMPL = new HoneycombKeyEventVersionImpl();
        } else {
            IMPL = new BaseKeyEventVersionImpl();
        }
    }

    // -------------------------------------------------------------------

    public static int normalizeMetaState(int metaState) {
        return IMPL.normalizeMetaState(metaState);
    }

    public static boolean metaStateHasModifiers(int metaState, int modifiers) {
        return IMPL.metaStateHasModifiers(metaState, modifiers);
    }

    public static boolean metaStateHasNoModifiers(int metaState) {
        return IMPL.metaStateHasNoModifiers(metaState);
    }

    public static boolean hasModifiers(KeyEvent event, int modifiers) {
        return IMPL.metaStateHasModifiers(event.getMetaState(), modifiers);
    }

    public static boolean hasNoModifiers(KeyEvent event) {
        return IMPL.metaStateHasNoModifiers(event.getMetaState());
    }

    /**
     * @deprecated Call {@link KeyEvent#startTracking()} directly. This method will be removed in a
     * future release.
     */
    @Deprecated
    public static void startTracking(KeyEvent event) {
        event.startTracking();
    }

    /**
     * @deprecated Call {@link KeyEvent#isTracking()} directly. This method will be removed in a
     * future release.
     */
    @Deprecated
    public static boolean isTracking(KeyEvent event) {
        return event.isTracking();
    }

    /**
     * @deprecated Call {@link View#getKeyDispatcherState()} directly. This method will be removed
     * in a future release.
     */
    @Deprecated
    public static Object getKeyDispatcherState(View view) {
        return view.getKeyDispatcherState();
    }

    /**
     * @deprecated Call
     * {@link KeyEvent#dispatch(KeyEvent.Callback, KeyEvent.DispatcherState, Object)} directly.
     * This method will be removed in a future release.
     */
    @Deprecated
    public static boolean dispatch(KeyEvent event, KeyEvent.Callback receiver, Object state,
                                   Object target) {
        return event.dispatch(receiver, (KeyEvent.DispatcherState)state, target);
    }

    public static boolean isCtrlPressed(KeyEvent event) {
        return IMPL.isCtrlPressed(event);
    }

    private KeyEventCompat() {}
}
