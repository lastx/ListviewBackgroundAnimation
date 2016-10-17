package io.github.lastx.listviewbackgroundanimation;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int ANIMATION_LENGTH = 600;

    private RecyclerView mRecyclerView;
    // TODO 可以直接用 TransitionDrawable
    private TransitionDrawable mtransPre;
    private TransitionDrawable mtransPost;

    private List<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        mRecyclerView = (RecyclerView) findViewById(R.id.id_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new HomeAdapter());

        mtransPre = (TransitionDrawable) getResources().getDrawable(R.drawable.trans_pre);
        mtransPost = (TransitionDrawable) getResources().getDrawable(R.drawable.trans_post);
    }

    private void init() {
        mData = new ArrayList<String>();
        for (int i = 'A'; i < 'z'; i++)
        {
            mData.add("" + (char) i);
        }
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    MainActivity.this).inflate(R.layout.item, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position)
        {
            // 防止动画复用
            holder.cancelAnimation();
            holder.textView.setText(mData.get(position));
        }

        @Override
        public int getItemCount()
        {
            return mData.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder
        {
            View mView;
            TextView textView;
            TestRunnable preRunnable;

            public MyViewHolder(View view)
            {
                super(view);
                mView = view;
                textView = (TextView) view.findViewById(R.id.id_num);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        Drawable bgStart = MainActivity.this.getResources().getDrawable(R.drawable.bg_start);
                        Drawable bgEnd = MainActivity.this.getResources().getDrawable(R.drawable.bg_end);
                        Drawable[] transPre = {bgStart, bgEnd};
                        Drawable[] transPost = {bgEnd, bgStart};
                        TestRunnable trTmp = new TestRunnable(view, transPost);

                        cancelAnimation();
                        TransitionDrawable td1 = new TransitionDrawable(transPre);
                        view.setBackground(td1);
                        td1.startTransition(ANIMATION_LENGTH);

                        preRunnable = trTmp;
                        view.postDelayed(preRunnable, ANIMATION_LENGTH);
                    }
                });
            }

            public void cancelAnimation() {
                if (preRunnable != null) {
                    preRunnable.shutdown = true;
                }
                // 简单的用重置bg的方式结束动画
                mView.setBackgroundResource(R.drawable.bg_start);
                mView.removeCallbacks(preRunnable);
            }
        }

        class TestRunnable implements Runnable {
            View mView;
            Drawable[] mDrawable;
            // 为 true 时不运行进程
            volatile boolean shutdown = false;

            public TestRunnable(View view, Drawable[] drawable) {
                mView = view;
                mDrawable = drawable;
            }

            @Override
            public void run() {
                if (!shutdown) {
                    TransitionDrawable td2 = new TransitionDrawable(mDrawable);
                    mView.setBackground(td2);
                    td2.startTransition(ANIMATION_LENGTH);
                }
            }
        }
    }
}
