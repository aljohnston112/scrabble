package io.fourth_finger.scrabble.views

import android.view.View
import android.view.ViewGroup

class ViewUtil {

    companion object {

        fun View.removeFromParent() {
            (this.parent as? ViewGroup)?.removeView(this)
        }

    }

}