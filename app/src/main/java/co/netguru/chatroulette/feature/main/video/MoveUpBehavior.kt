package co.netguru.chatroulette.feature.main.video

import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.view.View


class MoveUpBehavior : CoordinatorLayout.Behavior<View>() {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is Snackbar.SnackbarLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        val translationY = Math.min(0f, dependency.translationY - dependency.height)
        child.translationY = translationY
        return true
    }
}