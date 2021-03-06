package edu.sjsu.ireportgrp8.interactiveWindow;

import android.support.v4.app.Fragment;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

/**
 * This class contains everything needed for an InfoWindow to be shown.
 * It also provides a state that shows whether the window is already shown/hidden
 * or is in the middle of showing/hiding.
 */
public class InfoWindow {
    private Marker marker;
    private MarkerSpecification markerSpec;

    private Fragment windowFragment;

    private WindowState windowState = WindowState.HIDDEN;

    /**
     * @param marker The marker which determines the window's position on the screen.
     * @param markerSpec Provides the marker's width and height.
     * @param windowFragment The actual window that is displayed on the screen.
     */
    public InfoWindow(
            Marker marker,
            MarkerSpecification markerSpec,
            Fragment windowFragment) {

        this.marker = marker;
        this.markerSpec = markerSpec;
        this.windowFragment = windowFragment;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public MarkerSpecification getMarkerSpec() {
        return markerSpec;
    }

    public void setMarkerSpec(MarkerSpecification markerSpec) {
        this.markerSpec = markerSpec;
    }

    public Fragment getWindowFragment() {
        return windowFragment;
    }

    public void setWindowFragment(Fragment windowFragment) {
        this.windowFragment = windowFragment;
    }

    /**
     * Get window's state which could be one of the following:
     * <br>
     * {@link WindowState#SHOWING}, {@link WindowState#SHOWN},
     * {@link WindowState#HIDING}, {@link WindowState#HIDDEN}
     *
     * @return The InfoWindow's state.
     */
    public WindowState getWindowState() {
        return windowState;
    }

    public void setWindowState(WindowState windowState) {
        this.windowState = windowState;
    }

    public enum WindowState {
        SHOWING, SHOWN, HIDING, HIDDEN
    }

    /**
     * Holds the width and height of the marker.
     */
    public static class MarkerSpecification implements Serializable {
        private int width;
        private int height;

        public MarkerSpecification(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        @Override
        public boolean equals(Object o) {

            if (o instanceof MarkerSpecification) {
                final MarkerSpecification markerSpecification = (MarkerSpecification) o;

                final boolean widthCheck = markerSpecification.getWidth() == width;
                final boolean heightCheck = markerSpecification.getHeight() == height;

                return widthCheck && heightCheck;
            }

            return super.equals(o);
        }
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof InfoWindow) {
            final boolean markerCheck = ((InfoWindow) o).getMarker().getPosition().equals(marker.getPosition());
            final boolean specCheck = ((InfoWindow) o).getMarkerSpec().equals(markerSpec);

            return markerCheck && specCheck;
        }

        return super.equals(o);
    }

}
