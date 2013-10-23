package com.diveboard.mobile.editdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Weight;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class					EditWeightsDialogFragment extends DialogFragment implements OnEditorActionListener
{
	public interface			EditWeightsDialogListener
	{
        void					onWeightsEditComplete(DialogFragment dialog);
    }
	
	private DiveboardModel		mModel;
	private EditText			mWeights;
	private EditWeightsDialogListener	mListener;
	
	@Override
	 public void onAttach(Activity activity)
	 {
		 super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditWeightsDialogListener) activity;
		 }
		 catch (ClassCastException e)
		 {
			 // The activity doesn't implement the interface, throw exception
			 throw new ClassCastException(activity.toString() + " must implement onWeightsEditComplete");
		 }
	 }
	
	@Override
	public View					onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Typeface faceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Quicksand-Regular.otf");
		View view = inflater.inflate(R.layout.dialog_edit_weights, container);
		mModel = ((ApplicationController) getActivity().getApplicationContext()).getModel();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_weights_title));
		
		mWeights = (EditText) view.findViewById(R.id.weights);
		mWeights.setTypeface(faceR);
		mWeights.setText(Double.toString(mModel.getDives().get(getArguments().getInt("index")).getWeights().getWeight()));
		mWeights.requestFocus();
		
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		mWeights.setOnEditorActionListener(this);
        
		TextView weights_label = (TextView) view.findViewById(R.id.weights_label);
		weights_label.setTypeface(faceR);
		weights_label.setText(mModel.getDives().get(getArguments().getInt("index")).getWeights().getSmallName());
		
		Button cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setTypeface(faceR);
		cancel.setText(getResources().getString(R.string.cancel));
		cancel.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});
		
		Button save = (Button) view.findViewById(R.id.save);
		save.setTypeface(faceR);
		save.setText(getResources().getString(R.string.save));
		save.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				mModel.getDives().get(getArguments().getInt("index")).setWeights(new Weight(Double.parseDouble(mWeights.getText().toString())));
				mListener.onWeightsEditComplete(EditWeightsDialogFragment.this);
				dismiss();
			}
		});
		
        faceR = null;
		return view;
	}

	@Override
	public boolean					onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		if (EditorInfo.IME_ACTION_DONE == actionId)
		{
			mModel.getDives().get(getArguments().getInt("index")).setWeights(new Weight(Double.parseDouble(mWeights.getText().toString())));
			mListener.onWeightsEditComplete(EditWeightsDialogFragment.this);
			dismiss();
			return true;
		}
		return false;
	}
}
