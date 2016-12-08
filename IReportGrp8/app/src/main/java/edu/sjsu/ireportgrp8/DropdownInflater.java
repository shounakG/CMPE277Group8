package edu.sjsu.ireportgrp8;

/**
 * Created by pnedunuri on 12/5/16.
 */
public class DropdownInflater {
    private static DropdownInflater instance;

    public static DropdownInflater getInstance() {
        if (instance == null) {
            instance = new DropdownInflater();
        }

        return instance;
    }

    public void toggleDropdown(Dropdown dropdown, int positionLeft) {

    }
}
