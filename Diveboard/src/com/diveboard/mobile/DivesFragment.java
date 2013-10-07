package com.diveboard.mobile;

import java.io.IOException;
import java.lang.ref.WeakReference;

import com.diveboard.model.Dive;
import com.diveboard.model.Picture;
import com.diveboard.model.ScreenSetup;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * The fragment that displays the "fragment_dives.xml" layout
 */
public class DivesFragment extends Fragment {
	private Dive mDive;
	private Bitmap mRoundedLayer;
	private ImageView mMainImage;
	private ProgressBar mProgressBarMain;
	private DownloadImageTask mDownloadImageTask;
	private LinearLayout mFragment;
	private RelativeLayout mFragmentBannerHeight;
	private ScreenSetup mScreenSetup;
	private ImageView mMainImageCache;
	private RelativeLayout mFragmentBodyTitle;
	
	public DivesFragment()
	{
		//System.out.println("Entre");
	}
	
	public DivesFragment(Dive dive, ScreenSetup screenSetup, Bitmap rounded_layer)
	{
		mDive = dive;
		mScreenSetup = screenSetup;
		mRoundedLayer = rounded_layer;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_dives, container, false);
		mFragment = (LinearLayout)rootView.findViewById(R.id.fragment);
		RelativeLayout.LayoutParams fragment_params = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListFragmentWidth(), mScreenSetup.getDiveListFragmentHeight());
		fragment_params.setMargins(0, mScreenSetup.getDiveListWhiteSpace1(), 0, 0);
		mFragment.setLayoutParams(fragment_params);
		// Resize the dimensions of each fragment
		mFragmentBannerHeight = (RelativeLayout)mFragment.findViewById(R.id.fragment_banner_height);
		mFragmentBannerHeight.setLayoutParams(new LinearLayout.LayoutParams(mScreenSetup.getDiveListFragmentWidth(), mScreenSetup.getDiveListFragmentBannerHeight()));
		
		mMainImage = (ImageView)mFragment.findViewById(R.id.main_image);	
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListFragmentOutCircleRadius(), mScreenSetup.getDiveListFragmentOutCircleRadius());
		params1.setMargins(0, mScreenSetup.getDiveListFragmentWhitespace1(), 0, 0);
		mMainImage.setLayoutParams(params1);
		
		mMainImageCache = (ImageView)mFragment.findViewById(R.id.main_image_cache);		
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListFragmentOutCircleRadius(), mScreenSetup.getDiveListFragmentOutCircleRadius());
		params2.setMargins(0, mScreenSetup.getDiveListFragmentWhitespace1(), 0, 0);
		mMainImageCache.setLayoutParams(params2);
		mMainImageCache.setImageBitmap(mRoundedLayer);
		
		LinearLayout.LayoutParams fragment_body_title_params = new LinearLayout.LayoutParams(mScreenSetup.getDiveListFragmentWidth() - 120, mScreenSetup.getDiveListFragmentBodyTitle());
		fragment_body_title_params.setMargins(0, mScreenSetup.getDiveListFragmentWhitespace2(), 0, 0);
		mFragmentBodyTitle = (RelativeLayout)rootView.findViewById(R.id.fragment_body_title);
		mFragmentBodyTitle.setLayoutParams(fragment_body_title_params);
		//Set the content of the fragments
		Typeface faceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Quicksand-Regular.otf");
		Typeface faceB = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Quicksand-Bold.otf");
		((TextView) mFragment.findViewById(R.id.dive_spot)).setText("DIVE SPOT");
		((TextView) mFragment.findViewById(R.id.dive_spot)).setTextSize((mScreenSetup.getDiveListFragmentBannerHeight() * 25 / 150));
		((TextView) mFragment.findViewById(R.id.dive_spot)).setTypeface(faceR);
		((TextView) mFragment.findViewById(R.id.name_spot)).setText(mDive.getSpot().getName().toUpperCase());
		((TextView) mFragment.findViewById(R.id.name_spot)).setTextSize((mScreenSetup.getDiveListFragmentBannerHeight() * 25 / 150));
		((TextView) mFragment.findViewById(R.id.name_spot)).setTypeface(faceB);
		((TextView) mFragment.findViewById(R.id.dive_name)).setText(mDive.getSpot().getName().toUpperCase());
		((TextView) mFragment.findViewById(R.id.dive_name)).setTypeface(faceB);
		((TextView) mFragment.findViewById(R.id.dive_name)).setTextSize((mScreenSetup.getDiveListFragmentBannerHeight() * 25 / 150));
		((TextView) mFragment.findViewById(R.id.dive_date)).setText(mDive.getDate());
		((TextView) mFragment.findViewById(R.id.dive_date)).setTypeface(faceR);
		((TextView) mFragment.findViewById(R.id.dive_date)).setTextSize((mScreenSetup.getDiveListFragmentBannerHeight() * 25 / 150));
		((TextView) mFragment.findViewById(R.id.dive_duration)).setText(String.valueOf(mDive.getDuration()) + "MINS");
		((TextView) mFragment.findViewById(R.id.dive_duration)).setTypeface(faceR);
		((TextView) mFragment.findViewById(R.id.dive_duration)).setTextSize((mScreenSetup.getDiveListFragmentBannerHeight() * 25 / 150));
		((TextView) mFragment.findViewById(R.id.dive_maxdepth)).setText(String.valueOf(mDive.getMaxdepth()) + " METERS");
		((TextView) mFragment.findViewById(R.id.dive_maxdepth)).setTypeface(faceR);
		((TextView) mFragment.findViewById(R.id.dive_maxdepth)).setTextSize((mScreenSetup.getDiveListFragmentBannerHeight() * 25 / 150));
		// Downloading the main picture in a new thread
		//mProgressBarMain = (ProgressBar)mContent.findViewById(R.id.progress_bar_main);
		//showProgress(true);
		mDownloadImageTask = new DownloadImageTask((ImageView)mFragment.findViewById(R.id.main_image));
		mDownloadImageTask.execute();
        return rootView;
    }
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mProgressBarMain.setVisibility(View.VISIBLE);
			mProgressBarMain.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mProgressBarMain.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mMainImage.setVisibility(View.VISIBLE);
			mMainImage.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mMainImage.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressBarMain.setVisibility(show ? View.VISIBLE : View.GONE);
			mMainImage.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap>
	{
		private final WeakReference<ImageView> imageViewReference;
		
		public DownloadImageTask(ImageView imageView)
		{
			imageViewReference = new WeakReference<ImageView>(imageView);
		}
		
		protected Bitmap doInBackground(Void... voids)
		{
			try {
				if (getActivity() != null && mDive.getFeaturedPicture() != null)
					return mDive.getFeaturedPicture().getPicture(getActivity().getApplicationContext(), Picture.Size.MEDIUM);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(Bitmap result)
		{
			//showProgress(false);
			//mFragment.setAlpha(1);
			if (imageViewReference != null && result != null)
			{
				final ImageView imageView = imageViewReference.get();
				if (imageView != null)
					imageView.setImageBitmap(result);
			}
		}
		
		@Override
		protected void onCancelled() {
			mDownloadImageTask = null;
			showProgress(false);
		}
	}
}
