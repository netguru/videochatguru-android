package co.netguru.chatroulette.feature.main.video

import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.view.Gravity
import android.view.View


class MoveUpBehavior : CoordinatorLayout.Behavior<View>() {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is Snackbar.SnackbarLayout
    }

    override fun onAttachedToLayoutParams(lp: CoordinatorLayout.LayoutParams) {
        if (lp.dodgeInsetEdges == Gravity.NO_GRAVITY) {
            lp.dodgeInsetEdges = Gravity.BOTTOM
        }
    }
}