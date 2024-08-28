package com.example.todolist

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton

class SplashFragment : Fragment() {
    private lateinit var animatorSet: AnimatorSet
    private lateinit var signInButton: SignInButton
    private lateinit var logoImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.splash_login_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signInButton = view.findViewById(R.id.signInButton)
        logoImage = view.findViewById(R.id.image)

        setupAnimation()
        setupGoogleSignIn()
    }

    private fun setupAnimation() {
        animatorSet = AnimatorSet()

        val scaleX = ObjectAnimator.ofFloat(logoImage, View.SCALE_X, 0.8f, 1.2f)
        val scaleY = ObjectAnimator.ofFloat(logoImage, View.SCALE_Y, 0.8f, 1.2f)
        val rotateY = ObjectAnimator.ofFloat(logoImage, View.ROTATION_Y, 0f, 360f)

        scaleX.repeatMode = ObjectAnimator.REVERSE
        scaleX.repeatCount = ObjectAnimator.INFINITE
        scaleY.repeatMode = ObjectAnimator.REVERSE
        scaleY.repeatCount = ObjectAnimator.INFINITE
        rotateY.repeatCount = ObjectAnimator.INFINITE

        animatorSet.playTogether(scaleX, scaleY, rotateY)
        animatorSet.duration = 3000
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.start()
    }

    private fun setupGoogleSignIn() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("623130391962-kv3oj28lsct6gls5ckgs8miehebb1kb0.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())

        if (account == null) {
            showSignInButton()
        } else {
            (requireActivity() as OnAuthLaunch).showListFragment()
        }

        signInButton.setOnClickListener {
            (requireActivity() as OnAuthLaunch).launch(googleSignInClient.signInIntent)
        }
    }

    private fun showSignInButton() {
        signInButton.alpha = 0f
        signInButton.visibility = View.VISIBLE
        signInButton.animate()
            .alpha(1f)
            .setDuration(500)
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        animatorSet.cancel()
    }
}