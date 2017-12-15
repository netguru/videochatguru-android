package co.netguru.android.chatandroll.feature.main.video

import android.support.design.widget.CoordinatorLayout
import android.view.Gravity
import android.view.View


class MoveUpBehavior : CoordinatorLayout.Behavior<View>() {

    override fun onAttachedToLayoutParams(lp: CoordinatorLayout.LayoutParams) {
        if (lp.dodgeInsetEdges == Gravity.NO_GRAVITY) {
            lp.dodgeInsetEdges = Gravity.BOTTOM
        }
    }
}