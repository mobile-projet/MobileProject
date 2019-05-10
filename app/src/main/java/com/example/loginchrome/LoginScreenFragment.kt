package com.example.loginchrome


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception


/**
 * A simple [Fragment] subclass.
 *
 */
class LoginScreenFragment : Fragment(), View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private lateinit var viewF : View;

    private lateinit var signIn : SignInButton;
    private lateinit var mGoogleSignInClient: GoogleSignInClient;

    private var model : OrderViewModel? = null;

    val REQ_CODE = 9001;

    val RC_SIGN_IN = 100;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewF = inflater.inflate(R.layout.fragment_login_screen, container, false);


        model = activity?.let { ViewModelProviders.of(it).get(OrderViewModel::class.java)  }

        signIn = viewF.findViewById(R.id.signIn);

        signIn.setOnClickListener(this);

        val signInOptions : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(context!!, signInOptions);


        // Inflate the layout for this fragment
        return viewF;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val account = GoogleSignIn.getLastSignedInAccount(context!!);

        if(account != null) {
            updateUI(account);
        }

    }

    //sign into the application and navigate to the main fragment
    fun signIn() {
        val intent : Intent = mGoogleSignInClient.signInIntent;
        startActivityForResult(intent, REQ_CODE);

        // Choose authentication providers
        /*val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)*/

    }

    fun handleResult(result : Task<GoogleSignInAccount>) {
        try {
            val account = result.getResult(ApiException::class.java);
            if(account != null) {
                updateUI(account);
            }
        } catch (e: ApiException) {
            Log.w("E", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    fun updateUI(account: GoogleSignInAccount) {
        model?.userName = account.displayName ?: throw Exception();
        model?.email = account.email ?: throw Exception();
        model?.picture = account.photoUrl.toString();
        Log.e("E", "signed in ${model?.userName} + ${model?.picture}");
        viewF.findNavController().navigate(R.id.action_loginFragment_to_viewOrdersFragment);


    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.signIn -> signIn();
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQ_CODE) {
            val result = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(result);
        } else if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                model?.firebaseUser = FirebaseAuth.getInstance().currentUser
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }

    }


}
