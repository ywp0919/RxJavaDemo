package com.ywp.rxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;

public class MainActivity extends AppCompatActivity {

    private Observer<String> mObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();

    }

    /**
     * 先初始化一个观察者
     */
    private void initData() {
        // 创建一个观察者 方便重复观察下面的方法
        mObserver = new Observer<String>() {
            @Override
            public void onCompleted() {
                // 接收已经完成
                Log.i("onCompleted", "接收已经完成");
                Toast.makeText(MainActivity.this, "接收已经完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                // 接收发生错误
            }

            @Override
            public void onNext(String o) {
                // 接收到消息
                Log.i("onNext", o);
                Toast.makeText(MainActivity.this, o, Toast.LENGTH_SHORT).show();
            }
        };
    }

    /**
     * create方式
     * 按调用onNext()的顺序来发送消息
     *
     * @param view
     */
    public void create(View view) {
        // 创建一个被观察者
        Observable mObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                //  一条一条消息顺序发送
                subscriber.onNext("第一个create");
                subscriber.onNext("第二个create");
                subscriber.onNext("最后一个create");
                //  发送完成，会调用Observer的onCompleted方法
                subscriber.onCompleted();
            }
        });

        // 将被观察者与观察者关联起来
        mObservable.subscribe(mObserver);
    }

    /**
     * just方式
     * 按参数顺序来发送消息，不需要再调用onNext()方法
     * 消息发送完后会自动调用onCompleted()方法
     *
     * @param view
     */
    public void just(View view) {
        // 参数最多达到 10 个，源码里面有.
        // 发送完成后会自动调用onCompleted()方法，看效果就很清楚了
        Observable<String> mObservable = Observable.just("第一个just", "第二个just", "第三个just");
        mObservable.subscribe(mObserver);
    }

    /**
     * repeat方式
     * 这是重复发送消息的一个方法，可以create().repeat(),也可以just().repeat()这样使用
     * 当然待会要讲到的其他方法也是可以使用repeat()的
     *
     * @param view
     */
    public void repeat(View view) {
        //还是用上面这个例子吧
        // repeat()里面的参数代表重复的次数，但是只有重复完了所有消息的发送之后才会调用onCompleted()方法
        // onCompleted()方法只会在最后调用一次，同样看效果就很清楚了
        Observable<String> mObservable = Observable.just("第一个just", "第二个just", "第三个just").repeat(2);
        mObservable.subscribe(mObserver);
    }

    /**
     * timer()方式
     * 根据名字应该知道 这是一个定时器功能的方法
     * 可以设定一个特定的时间再发送消息
     *
     * @param view
     */
    public void timer(View view) {
        // 还是用上面这个例子吧
        // 2s 后发送,还有别的参数可以自行看下源码
        // 这里我弄了很久，最后发现它发送的是一个long型的值，为0，看Log日志就知道了
        // 在这里理解不是太深，大家有明白的可以留言指导一下我，多谢

        Observable mObservable = Observable.timer(2, TimeUnit.SECONDS);
        Observer mObserver = new Observer() {
            @Override
            public void onCompleted() {
                Log.i("onCompleted", "接收已经完成");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
                // 在这里 看到了这个值为long类型
                Log.i("onNext", o.getClass().toString());
                if (o instanceof Long) {
                    // 在这里 将这个值打印出来，为 0
                    Log.i("onNext", o.toString());
                }
            }
        };
        mObservable.subscribe(mObserver);
    }

    /**
     * from()方式
     * from 方法可以通过遍历集合 来发送每一个item
     * item可以是其他对象
     *
     * @param view
     */
    public void from(View view) {
        // 集合item也可以为其他的对象，Observer对应一下类型就可以了
        // 其实我觉得可以在Observer那里使用泛型，可以解决类型多样的问题
        List<String> mList = new ArrayList<>();
        mList.add("第一个from");
        mList.add("第二个from");
        mList.add("第三个from");
        Observable mObservable = Observable.from(mList);
        mObservable.subscribe(mObserver);
    }
}
