package test.fb.com.security;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import test.fb.com.R;

public class SecurityFragment extends Fragment {
    private static final String LOG_TAG = SecurityFragment.class.getSimpleName();
    private static final int BIOMETRIC_ENROLL_REQUEST_CODE = 1;
    private EditText mEditSecretData;
    private TextView mTextSecretData;
    private Executor mExecutor;
    private BiometricPrompt.PromptInfo mPromptInfo = new BiometricPrompt.PromptInfo.Builder()
            .setTitle("Enter biometric credential")
            .setSubtitle("Authenticate using your biometric credential")
            .setConfirmationRequired(false)
            .setDeviceCredentialAllowed(true)
            .build();

    private String mKeyAlias = MyKeyStore.KEY_ALIAS_TIMEOUT; // Default use timeout key

    public SecurityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static SecurityFragment newInstance() {
        Log.i(LOG_TAG, "newInstance");
        SecurityFragment fragment = new SecurityFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate");
        mExecutor = ContextCompat.getMainExecutor(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreateView");

        if (!canAuthenticate()) {
            return inflater.inflate(R.layout.fragment_auth_fail, container, false);
        } else {
            // Authenticate upfront to mimic regular login flow
            new BiometricPrompt(this, mExecutor, new BiometricAuthCallback()).authenticate(mPromptInfo);

            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_security, container, false);
            ToggleButton toggleKeyType = view.findViewById(R.id.toggle_key_type);
            toggleKeyType.setOnCheckedChangeListener((button, isChecked) -> {
                mKeyAlias = isChecked ? MyKeyStore.KEY_ALIAS_TIMEOUT : MyKeyStore.KEY_ALIAS_PER_USE;
                mTextSecretData.setText(R.string.text_secret_data); // Reset
                Log.i(LOG_TAG, "Key alias: " + mKeyAlias);
            });
            mEditSecretData = view.findViewById(R.id.edit_secret_data);
            mTextSecretData = view.findViewById(R.id.text_secret_data);

            Button buttonEncrypt = view.findViewById(R.id.button_encrypt);
            buttonEncrypt.setOnClickListener(this::encrypt);
            Button buttonDecrypt = view.findViewById(R.id.button_decrypt);
            buttonDecrypt.setOnClickListener(this::decrypt);
            return view;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BIOMETRIC_ENROLL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.i(LOG_TAG, "Biometric setting done.");
                // TODO: refresh fragment.
            } else {
                Toast.makeText(getContext(), "Biometric setting failed. " + resultCode, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void encrypt(View view) {
        String text = mEditSecretData.getText().toString();
        mEditSecretData.setText("");
        if (TextUtils.isEmpty(text)) return;
        try {
            String encryptedData = MyKeyStore.encryptData(mKeyAlias, text);
            mTextSecretData.setText(encryptedData);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException e) {
            Log.e(LOG_TAG, "Encrypt error.", e);
        } catch (InvalidKeyException e) {
            Log.i(LOG_TAG, "Authentication expired.", e);
            new BiometricPrompt(this, mExecutor, new BiometricAuthCallback(text, MyKeyStore::encryptData)).authenticate(mPromptInfo);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void decrypt(View view) {
        String encryptedData = mTextSecretData.getText().toString();
        if (TextUtils.isEmpty(encryptedData)) return;
        try {
            String plainText = MyKeyStore.decryptData(mKeyAlias, encryptedData);
            if (!TextUtils.isEmpty(plainText)) mTextSecretData.setText(plainText);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            Log.e(LOG_TAG, "Encrypt error.", e);
        } catch (InvalidKeyException e) {
            Log.i(LOG_TAG, "Authentication expired.", e);
            new BiometricPrompt(this, mExecutor, new BiometricAuthCallback(encryptedData, MyKeyStore::decryptData)).authenticate(mPromptInfo);
        }

    }

    private boolean canAuthenticate() {
        BiometricManager manager = BiometricManager.from(getContext());
        switch (manager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(getContext(), "Biometric features unavailable.", Toast.LENGTH_SHORT).show();
                return false;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED: {
                // Prompt user to create credentials.
                Intent intent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, BIOMETRIC_ENROLL_REQUEST_CODE);
                } else {
                    Toast.makeText(getContext(), "Not enrolled in Biometric.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Biometric Authentication Callbacks. Run in Main Thread.
     */
    class BiometricAuthCallback extends BiometricPrompt.AuthenticationCallback {
        String text;
        MyKeyStore.CryptoOperation op;

        public BiometricAuthCallback() { /* Will do nothing on auth success */ }

        public BiometricAuthCallback(String text, MyKeyStore.CryptoOperation op) {
            this.text = text;
            this.op = op;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            Log.i(LOG_TAG, "BiometricCallback: " + Thread.currentThread().toString());
            if (op == null || text == null) return;
            try {
                String content = op.apply(mKeyAlias, text);
                mTextSecretData.setText(content);
            } catch (Exception e) { e.printStackTrace(); }
        }

        @Override
        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            Toast.makeText(getContext(), "Authentication error " + errString, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }
}
