

package com.lianer.core.stuff;


import android.view.View;
import com.lianer.core.utils.SnackbarUtil;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 * Create by  :    L
 * Create Time:    2018-4-17
 * Brief Desc :
 * </pre>
 */
public class ScheduleCompat {

   public static <T> FlowableTransformer<T,T> apply(){
       return new FlowableTransformer<T, T>() {
           @Override
           public Publisher<T> apply(Flowable<T> upstream) {
               return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
           }
       };
   }

   public static void snackInMain(View view, String content){
       Flowable
               .just(content)
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(s -> SnackbarUtil.DefaultSnackbar(view,s).show());
   }

}
