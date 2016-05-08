package com.vol.pdvlib;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vol.pdvlib.dummy.DummyContent;

/**
 * A fragment representing a single Drawable detail screen.
 * This fragment is either contained in a {@link DrawableListActivity}
 * in two-pane mode (on tablets) or a {@link DrawableDetailActivity}
 * on handsets.
 */
public class DrawableDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DrawableDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.drawable_detail, container, false);

        if (mItem != null) {
            ((AppCompatImageView) rootView.findViewById(R.id.drawable_detail)).setImageResource(mItem.drawable);
            ((AppCompatImageView) rootView.findViewById(R.id.drawable_detail_full)).setImageResource(mItem.drawable);

            Drawable drawable = ResourcesCompat.getDrawable(getResources(), mItem.drawable, getContext().getTheme());
            String text = "Drawable name: " + mItem.id + "\n\n" +
                "Drawable type: " + drawable.getClass().getSimpleName() + "\n\n" +
                "Drawble size (w * h): " + drawable.getIntrinsicWidth() + " * " + drawable.getIntrinsicHeight();
            ((TextView) rootView.findViewById(R.id.type)).setText(text);

        }

        return rootView;
    }
}
