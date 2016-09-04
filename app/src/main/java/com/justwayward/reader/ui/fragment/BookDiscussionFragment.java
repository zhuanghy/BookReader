package com.justwayward.reader.ui.fragment;

import com.justwayward.reader.R;
import com.justwayward.reader.base.BaseRVFragment;
import com.justwayward.reader.base.Constant;
import com.justwayward.reader.bean.DiscussionList;
import com.justwayward.reader.bean.support.SelectionEvent;
import com.justwayward.reader.component.AppComponent;
import com.justwayward.reader.component.DaggerCommunityComponent;
import com.justwayward.reader.ui.activity.BookDiscussionDetailActivity;
import com.justwayward.reader.ui.contract.BookDiscussionContract;
import com.justwayward.reader.ui.easyadapter.BookDiscussionAdapter;
import com.justwayward.reader.ui.presenter.BookDiscussionPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

/**
 * 综合讨论区Fragment
 *
 * @author yuyh.
 * @date 16/9/2.
 */
public class BookDiscussionFragment extends BaseRVFragment<DiscussionList.PostsBean> implements BookDiscussionContract.View{

    @Inject
    BookDiscussionPresenter mPresenter;

    private String sort = Constant.SortType.DEFAULT;
    private String distillate = Constant.Distillate.ALL;

    @Override
    public int getLayoutResId() {
        return R.layout.common_easy_recyclerview;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerCommunityComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        initAdapter(BookDiscussionAdapter.class, true, true);

        mPresenter.attachView(this);
        onRefresh();
    }

    @Override
    public void showBookDisscussionList(List<DiscussionList.PostsBean> list, boolean isRefresh) {
        if (isRefresh) {
            mAdapter.clear();
        }
        mAdapter.addAll(list);
        start = start + list.size();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void initCategoryList(SelectionEvent event) {
        sort = event.sort;
        distillate = event.distillate;
        start = 0;
        limit = 20;
        mPresenter.getBookDisscussionList(sort, distillate, start, limit);
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        mPresenter.getBookDisscussionList(sort, distillate, start, limit);
    }

    @Override
    public void onLoadMore() {
        super.onLoadMore();
        mPresenter.getBookDisscussionList(sort, distillate, start, limit);
    }

    @Override
    public void onItemClick(int position) {
        DiscussionList.PostsBean data = (DiscussionList.PostsBean) mAdapter.getItem(position);
        BookDiscussionDetailActivity.startActivity(activity, data._id);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

}
