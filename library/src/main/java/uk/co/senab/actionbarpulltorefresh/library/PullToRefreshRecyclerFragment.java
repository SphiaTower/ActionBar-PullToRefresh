package tower.sphia.pulltorefreshautopager.lib;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.ScrollYDelegate;

/**
 * Created by Rye on 7/16/2015.
 * <p>This fragment is based on Chris Barnes' ActionBar-PullToRefresh.</p>
 */
 public abstract class PullToRefreshRecyclerFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private PullToRefreshLayout mPullToRefreshLayout;

    /**
     * You can also use delegation to control the RecyclerView, but it looks verbose
     * @return the RecyclerView
     */
    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    protected PullToRefreshLayout getPullToRefreshLayout() {
        return mPullToRefreshLayout;
    }

    /**
     * Called when the PullToRefresh is triggered
     * @param view
     */
    protected abstract void onRefreshStarted(View view);


    /**
     * Init the RecyclerView and PullToRefreshLayout
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // This is the View which is created by ListFragment
        mRecyclerView = new RecyclerView(getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setItemDecoration();
        // We need to create a PullToRefreshLayout manually
        mPullToRefreshLayout = new PullToRefreshLayout(getActivity());
        mPullToRefreshLayout.addView(mRecyclerView);
        return mPullToRefreshLayout;
    }

    /**
     * Add default divider to RecyclerView
     * You can override this method to modify/delete the divider
     */
    protected void setItemDecoration() {
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
    }


    /**
     * Setup the PullToRefreshLayout
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // We can now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())

                /* use ScrollYDelegate to provide PullToRefresh support for RecyclerView,
                 which implements ScrollingView */
                .useViewDelegate(RecyclerView.class, new ScrollYDelegate() {

                    /* Override this method to fix the bug of staring refresh on scroll down at any position.
                     * A check of first child item top position of RecyclerView is added to help decide whether to
                     * start refreshing. */
                    @Override
                    public boolean isReadyForPull(View view, float x, float y) {
                        RecyclerView recyclerView = (RecyclerView) view;
                        /* get the position of the top of the 1st child */
                        View first = recyclerView.getChildAt(0);
                        if (first != null) {
                            int top = first.getTop();
                            /* MUST check if the 1st child is at top, or the PullToRefresh will be triggered
                             * regardless of the actual position of RecyclerView
                             * TODO: if the item has a layout_margin, top == 0 is no longer satisfied */
                            return super.isReadyForPull(view, x, y) && top == 0;
                        } else {
                            return super.isReadyForPull(view, x, y);
                        }
                    }
                })
                        // We need to mark the ListView and it's Empty View as pullable
                        // This is because they are not direct children of the ViewGroup
                .theseChildrenArePullable(mRecyclerView)


                        // We can now complete the setup as desired
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View v) {
                        PullToRefreshRecyclerFragment.this.onRefreshStarted(v);
                    }
                })
                .setup(mPullToRefreshLayout);
    }


}
