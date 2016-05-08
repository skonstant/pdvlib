package com.vol.pdvlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vol.pdvlib.dummy.DummyContent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * An activity representing a list of Drawables. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link DrawableDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class DrawableListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private MenuItem searchItem;
    private SearchView searchView;
    private SimpleItemRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable_list);

        Log.d("TAG", "package"  + getPackageName());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.drawable_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        TextView info = (TextView) findViewById(R.id.info);

        String sdpi = "";

        int dpi = getResources().getDisplayMetrics().densityDpi;
        if (dpi < DisplayMetrics.DENSITY_MEDIUM) {
            sdpi = "ldpi";
        } else if (dpi < DisplayMetrics.DENSITY_HIGH) {
            sdpi = "mdpi";
        } else if (dpi < DisplayMetrics.DENSITY_XHIGH) {
            sdpi = "hdpi";
        } else if (dpi <  DisplayMetrics.DENSITY_XXHIGH) {
            sdpi = "xhdpi";
        } else if (dpi < DisplayMetrics.DENSITY_XXXHIGH) {
            sdpi = "xxhdpi";
        } else {
            sdpi = "xxxhdpi";
        }

        info.setText("This device is: " + sdpi + "\nLanguage is: " + Locale.getDefault().getLanguage());

        if (findViewById(R.id.drawable_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        Field[] drawables = new Field[0];
        try {
            drawables = Class.forName(getPackageName() + ".R$drawable").getFields();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (Field f : drawables) {
            if (f.getType().equals(int.class)) {
                try {
                    ResourcesCompat.getDrawable(getResources(), f.getInt(null), getTheme());
                    DummyContent.addItem(new DummyContent.DummyItem(f.getName(), f.getInt(null)));
                }catch (Resources.NotFoundException e) {
                    Log.e("TAG",  "not found: " + f.getName(), e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        adapter = new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS);
        recyclerView.setAdapter(adapter);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.drawable_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setImageResource(mValues.get(position).drawable);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(DrawableDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        DrawableDetailFragment fragment = new DrawableDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.drawable_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, DrawableDetailActivity.class);
                        intent.putExtra(DrawableDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public void filter(String query) {
            if (!TextUtils.isEmpty(query)) {
                mValues = new ArrayList<>();
                for (DummyContent.DummyItem item : DummyContent.ITEMS) {
                    if (item.id.contains(query)) {
                        mValues.add(item);
                    }
                }
            } else {
                mValues = DummyContent.ITEMS;
            }

            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final AppCompatImageView mContentView;
            public DummyContent.DummyItem mItem;

            @SuppressLint("WrongViewCast")
            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (AppCompatImageView) view.findViewById(R.id.content);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });
        return true;
    }

}
