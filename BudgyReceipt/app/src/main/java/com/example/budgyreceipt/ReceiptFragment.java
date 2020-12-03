package com.example.budgyreceipt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ReceiptFragment extends DialogFragment {

    public interface OnReceiptCreatedListener {
        void onReceiptCreated(String receipt);
    }

    private OnReceiptCreatedListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstance) {
        final EditText receiptEditText = new EditText(requireActivity());
        receiptEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        receiptEditText.setMaxLines(1);

        return new AlertDialog.Builder(requireActivity())
                .setTitle("New Receipt?")
                .setView(receiptEditText)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String receipt = receiptEditText.getText().toString();
                        mListener.onReceiptCreated(receipt.trim());
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (OnReceiptCreatedListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
