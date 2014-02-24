package com.diveboard.mobile.editdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;

import java.util.ArrayList;

import org.apache.commons.codec.digest.Md5Crypt;

import com.diveboard.model.Buddy;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.util.ExpandableHeightGridView;
import com.diveboard.util.ImageCache.ImageCacheParams;
import com.diveboard.util.ImageFetcher;
import com.diveboard.util.Utils;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class					TabEditBuddiesFragment extends Fragment
{
	private ExpandableHeightGridView			mGridView;
	private ExpandableHeightGridView			mGridViewBuddies;
	private int					mIndex;
	private DiveboardModel		mModel;
	private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageFetcher mImageFetcher;
    private ImageAdapter mAdapter;
    private ImageAdapterBuddies mAdapterBuddies;
    ArrayList<Buddy> mOldBuddies;
    ArrayList<Buddy> mBuddies;
    ArrayList<String> mListString = new  ArrayList<String>();
    private ViewGroup mRootView;
    
    public TabEditBuddiesFragment() {
    	
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
		if (AC.handleLowMemory() == true)
			return ;
		mModel = ((ApplicationController)getActivity().getApplicationContext()).getModel();
		mIndex = getActivity().getIntent().getIntExtra("index", -1);
		setHasOptionsMenu(false);
		mAdapter = new ImageAdapter(getActivity());
		mAdapterBuddies = new ImageAdapterBuddies(getActivity());
		mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
		mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

		ImageCacheParams cacheParams = new ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory
		// The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
    	ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
    	Dive dive = mModel.getDives().get(mIndex);
    	mOldBuddies = AC.getModel().getOldBuddies();
    	mBuddies = dive.getBuddies();
//    	for (int i = 0; i < mOldBuddies.size(); i++)
//    	{
//    		System.out.println("Add String " + mOldBuddies.get(i).getPicture()._urlDefault);
//    		mListString.add(mOldBuddies.get(i).getPicture()._urlDefault);
//    	}
//    	for (int i = 0; i < mOldBuddies.size(); i++)
//        {
//        	System.out.println("TEST: "+ mOldBuddies.get(i).getNickname() + " " + mOldBuddies.get(i).getId());
//        }
    	mRootView = (ViewGroup) inflater.inflate(R.layout.tab_edit_buddies, container, false);
    	mGridView = (ExpandableHeightGridView)mRootView.findViewById(R.id.gridView);
    	mGridViewBuddies = (ExpandableHeightGridView)mRootView.findViewById(R.id.gridViewBuddies);
    	mGridView.setAdapter(mAdapter);
    	mGridView.setExpanded(true);
    	mGridViewBuddies.setExpanded(true);
    	mGridViewBuddies.setAdapter(mAdapterBuddies);
    	mGridView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                        mImageFetcher.setPauseWork(true);
                    }
                } else {
                    mImageFetcher.setPauseWork(false);
                    
                }
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
    	mGridViewBuddies.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (!mOldBuddies.contains(mBuddies.get(position)))
					mOldBuddies.add(mBuddies.get(position));
				mBuddies.remove(position);
				mAdapter.notifyDataSetChanged();
				mAdapterBuddies.notifyDataSetChanged();
				mModel.getDives().get(mIndex).setBuddies(mBuddies);
			}
		});
    	mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				System.out.println(position);
				if (!mBuddies.contains(mOldBuddies.get(position)))
					mBuddies.add(mOldBuddies.get(position));
				mOldBuddies.remove(position);
				mAdapter.notifyDataSetChanged();
				mAdapterBuddies.notifyDataSetChanged();
				mModel.getDives().get(mIndex).setBuddies(mBuddies);
			}
		});
    	mGridViewBuddies.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                        mImageFetcher.setPauseWork(true);
                    }
                } else {
                    mImageFetcher.setPauseWork(false);
                    
                }
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
    	// This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
        // of each view so we get nice square thumbnails.
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout()
                    {
                    	
                        if (mAdapter.getNumColumns() == 0)
                        {
                            final int numColumns = (int) Math.floor(
                                    mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                                //mAdapter.setNumColumns(numColumns);
                                mAdapter.setItemHeight(columnWidth);
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
//                                }
                                if (Utils.hasJellyBean()) {
                                    mGridView.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                    mGridView.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }
                            }
                        }
                    }
                });
        mGridViewBuddies.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout()
                    {
                    	
                        if (mAdapter.getNumColumns() == 0)
                        {
                            final int numColumns = (int) Math.floor(
                            		mGridViewBuddies.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mGridViewBuddies.getWidth() / numColumns) - mImageThumbSpacing;
                                //mAdapter.setNumColumns(numColumns);
                                mAdapterBuddies.setItemHeight(columnWidth);
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
//                                }
                                if (Utils.hasJellyBean()) {
                                	mGridViewBuddies.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                	mGridViewBuddies.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }
                            }
                        }
                    }
                });
        Typeface faceB = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Bold.otf");
		Typeface faceR = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Regular.otf");
        ((TextView)mRootView.findViewById(R.id.myBuddies)).setTypeface(faceB);
        ((TextView)mRootView.findViewById(R.id.myOldBuddies)).setTypeface(faceB);
        ((TextView)mRootView.findViewById(R.id.search)).setTypeface(faceB);
        ((TextView)mRootView.findViewById(R.id.diveboard_text)).setTypeface(faceB);
        ((EditText)mRootView.findViewById(R.id.diveboard_edit)).setTypeface(faceR);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.buddy_short_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = ((Spinner)mRootView.findViewById(R.id.spinner));
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position == 0)
				{
//					EditText editText = new EditText(getActivity());
//					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//					params.addRule(RelativeLayout.BELOW, arg0.getId());
//					editText.setLayoutParams(params);
//					((RelativeLayout)mRootView.findViewById(R.id.parent_rl)).addView(editText);
				}
				else if (position == 1)
				{
					
				
				}
				else if (position == 2)
				{
					
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        return mRootView;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }
    
    /**
     * The main adapter that backs the GridView. This is fairly standard except the number of
     * columns in the GridView is used to create a fake top row of empty views as we use a
     * transparent ActionBar and don't want the real top row of images to start off covered by it.
     */
    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        //private int mActionBarHeight = 0;
        private GridView.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }

        @Override
        public int getCount() {
        	return mOldBuddies.size();
        }

        @Override
        public long getItemId(int position) {
            return position - mNumColumns;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {

            // Now handle the main ImageView thumbnails
            ImageView imageView;
            if (convertView == null) { // if it's not recycled, instantiate and initialize
            	System.out.println("convertview = null");
                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);
            } else { // Otherwise re-use the converted view
            	System.out.println("convertview != null");
            	imageView = (ImageView) convertView;
            }

            // Check the height matches our calculated column width
            if (imageView.getLayoutParams().height != mItemHeight) {
                imageView.setLayoutParams(mImageViewLayoutParams);
            }

            // Finally load the image asynchronously into the ImageView, this also takes care of
            // setting a placeholder image while the background thread runs
            ApplicationController AC = (ApplicationController)mContext.getApplicationContext();
            if (mOldBuddies.get(position).getPicture()._urlDefault.contains("no_picture"))
            {
            	imageView.setImageResource(R.drawable.no_picture);
            }
            else
            	mImageFetcher.loadImage(mOldBuddies.get(position).getPicture()._urlDefault, imageView);
            return imageView;
        }

        /**
         * Sets the item height. Useful for when we know the column width so the height can be set
         * to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams =
                    new GridView.LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);
            mImageFetcher.setImageSize(height);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }

		@Override
		public Object getItem(int position) {
			return mOldBuddies.get(position).getPicture()._urlDefault;
		}
    }
    
    
    private class ImageAdapterBuddies extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private GridView.LayoutParams mImageViewLayoutParams;

        public ImageAdapterBuddies(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }

        @Override
        public int getCount() {
        	return mBuddies.size();
        }

        @Override
        public long getItemId(int position) {
            return position - mNumColumns;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {

            // Now handle the main ImageView thumbnails
            ImageView imageView;
            if (convertView == null) { // if it's not recycled, instantiate and initialize
            	System.out.println("convertview = null");
                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);
            } else { // Otherwise re-use the converted view
            	System.out.println("convertview != null");
            	imageView = (ImageView) convertView;
            }

            // Check the height matches our calculated column width
            if (imageView.getLayoutParams().height != mItemHeight) {
                imageView.setLayoutParams(mImageViewLayoutParams);
            }

            // Finally load the image asynchronously into the ImageView, this also takes care of
            // setting a placeholder image while the background thread runs
            ApplicationController AC = (ApplicationController)mContext.getApplicationContext();
            if (mBuddies.get(position).getPicture()._urlDefault.contains("no_picture"))
            {
            	imageView.setImageResource(R.drawable.no_picture);
            }
            else
            	mImageFetcher.loadImage(mBuddies.get(position).getPicture()._urlDefault, imageView);
            return imageView;
        }

        /**
         * Sets the item height. Useful for when we know the column width so the height can be set
         * to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams =
                    new GridView.LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);
            mImageFetcher.setImageSize(height);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }

		@Override
		public Object getItem(int position) {
			return mBuddies.get(position).getPicture()._urlDefault;
		}
    }
}