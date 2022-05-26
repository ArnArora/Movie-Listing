package com.example.samaritanmovie.movie_list;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class EndlessListener extends RecyclerView.OnScrollListener{
    //number of rows below position before data should load
    private int threshold = 1;
    //current JSON page
    private int curPage = 1;
    //total number of movies during last load
    private int prevCount = 0;
    //are we still loading data?
    private boolean isLoading = true;
    //starting JSON page for data
    private int startPage = 1;

    RecyclerView.LayoutManager manager;

    public EndlessListener(GridLayoutManager layoutManager) {
        manager = layoutManager;
        //number of columns*threshold
        //gives number of items below current position before data should start loading
        threshold = threshold * layoutManager.getSpanCount();
    }

    @Override
    public void onScrolled(RecyclerView rv, int dx, int dy) {
        //position of last visible item in recyclerview
        int lastVisible = 0;
        int totalItemCount = manager.getItemCount();

        lastVisible = ((GridLayoutManager) manager).findLastVisibleItemPosition();

        //reset list if current count is 0, but previous is not
        if (totalItemCount < prevCount) {
            this.curPage = this.startPage;
            this.prevCount = totalItemCount;
            if (totalItemCount == 0) {
                this.isLoading = true;
            }
        }
        //check if data has finished loading
        if (isLoading && (totalItemCount > prevCount)) {
            isLoading = false;
            prevCount = totalItemCount;
        }

        //have we passed the threshold?
        //if we have, load more data
        if (!isLoading && (lastVisible + threshold) > totalItemCount) {
            curPage++;
            onLoadMore(curPage);
            isLoading = true;
        }

    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState)
    {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE)
        {
            //check if scrolling down
            boolean canScrollDownMore = recyclerView.canScrollVertically(1);
            //check if end of list
            if (!canScrollDownMore)
            {
                onScrolled(recyclerView, 0, 1);
            }
        }
    }

    //reset state to original list
    public void resetState() {
        curPage = this.startPage;
        this.prevCount = 0;
        this.isLoading = true;
    }

    public abstract void onLoadMore(int page);
}
