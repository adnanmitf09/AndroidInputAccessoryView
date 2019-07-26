package com.adnan.kavlibrary;

import android.app.Activity;
import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class InputAccessoryViewHandler {

    //===========================================================
    //                 Variables
    //===========================================================

    private static final int SOFT_KEY_BOARD_MIN_HEIGHT = 200; // should be a %
    private static long FADE_ANIMATION_DURATION = 180; // ms
    private ViewTreeObserver.OnGlobalLayoutListener layoutListener;

    private Context context;
    private View trigger;
    private View accessoryView;
    private View view;
    private Activity activity;
    private Fragment fragment;
    private EditText focus;


    //===========================================================
    //                 Constructor
    //===========================================================
    public InputAccessoryViewHandler(Context context) {
        this.context = context;
    }
    //===========================================================
    //                Public Methods
    //===========================================================

    public Config withFragment(@NonNull Fragment fragment) {
        this.view = fragment.getView();
        this.fragment = fragment;
        return config;
    }

    public Config withActivity(@NonNull Activity activity) {
        this.view = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        this.activity = activity;

        return config;
    }

    public Config withView(@NonNull View view) {
        this.view = view;
        return config;
    }

    //===========================================================
    //                 Config implementation
    //===========================================================
    private Config config = new Config() {
        @Override
        public Config trigger(@NonNull View trigger) {
            InputAccessoryViewHandler.this.trigger = trigger;
            return this;
        }

        @Override
        public Config accessoryView(@NonNull View accessoryView) {
            InputAccessoryViewHandler.this.accessoryView = accessoryView;
            return this;
        }

        @Override
        public Config focus(@NonNull EditText focus) {
            InputAccessoryViewHandler.this.focus = focus;
            return this;
        }

        @Override
        public Config animationDuration(@NonNull long animationTime) {
            FADE_ANIMATION_DURATION = animationTime;
            return this;
        }

        @Override
        public Handle handle() {
            setTriggerTouchListener();
            registerView();
            configureLifecycleCallback();
            return handle;
        }
    };


    //===========================================================
    //                 Handle implementation
    //===========================================================

    private Handle handle = new Handle() {

        @Override
        public void showAccessoryView() {
            if (accessoryView != null) {
                AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setDuration(FADE_ANIMATION_DURATION);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        accessoryView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                accessoryView.startAnimation(anim);

                if (focus != null) {
                    focus.requestFocus();
                    ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                }
            }
        }

        @Override
        public void hideAccessoryView() {
            if (accessoryView != null) {
                accessoryView.setVisibility(View.GONE);
            }
            if (focus != null) {
                focus.clearFocus();
            }
        }

        @Override
        public void destroy() {
            removeViewObserver();
        }
    };

    //===========================================================
    //                 Private Methods
    //===========================================================


    @SuppressWarnings("all")
    private void setTriggerTouchListener() {
        if (trigger != null) {
            trigger.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {

                        handle.showAccessoryView();

                    }
                    return false;
                }
            });
        }
    }

    private void registerView() {

        layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                view.getWindowVisibleDisplayFrame(r);

                int heightDiff = view.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > SOFT_KEY_BOARD_MIN_HEIGHT) { // if more than 100 pixels, its probably a keyboard...
                    handle.showAccessoryView();
                } else {

                    handle.hideAccessoryView();
                }
            }


        };
        view.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
    }

    private void configureLifecycleCallback() {
        if (this.fragment != null) {
            fragment.getLifecycle().addObserver(new LifecycleObserver() {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                public void onDestroy(LifecycleOwner source, Lifecycle.Event event) {
                    removeViewObserver();

                }
            });
        }

        if (this.activity != null) {
            // TODO add observer to activity

        }
    }


    private void removeViewObserver() {
        if (this.view != null) {
            this.view.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
        }
    }
    //===========================================================
    //                 Interfaces
    //===========================================================
    public interface Link {
        Config withActivity(@NonNull Activity activity);

        Config withFragment(@NonNull Fragment fragment);

        Config withView(@NonNull View view);

    }

    public interface Config {
        Config trigger(@NonNull View trigger);

        Config accessoryView(@NonNull View accessoryView);

        Config focus(@NonNull EditText focus);

        Config animationDuration(@NonNull long animationTime);

        Handle handle();

    }

    public interface Handle {
        void showAccessoryView();

        void hideAccessoryView();

        void destroy();


    }



}


