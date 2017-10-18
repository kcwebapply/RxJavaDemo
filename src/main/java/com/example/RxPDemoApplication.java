package com.example;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

@SpringBootApplication
public class RxPDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(RxPDemoApplication.class, args);

			Flowable<String> flowable = Flowable.create(emitter -> {
					   String[] datas = { "わざわざ見てくれて", "ありがとう！" };
					   int i = 0;
					   for (String data : datas) {
				            // 購読解除されている場合は処理をやめる
				            if (emitter.isCancelled()) {
				              return;
				            }
				            // データを通知する
				            emitter.onNext(data);
				            i+=1;
				          }
				          // 完了したことを通知する
				         emitter.onComplete();
			}, BackpressureStrategy.BUFFER);


			  flowable
			      // Subscriberの処理を別スレッドで行うようにする
			      .observeOn(Schedulers.computation())
			      // 購読する
			      .subscribe(new Subscriber<String>() {

			        /** データ数のリクエストおよび購読の解除を行うオブジェクト */
			        private Subscription subscription;
			        int i = 0;
			        // 購読が開始された際の処理
			        @Override
			        public void onSubscribe(Subscription subscription) {
			          // SubscriptionをSubscriber内で保持する
			          this.subscription = subscription;
			          // 受け取るデータ数をリクエストする
			          this.subscription.request(1L);
			        }

			        // データを受け取った際の処理
			        @Override
			        public void onNext(String item) {
			          // 実行しているスレッド名の取得
			          String threadName = Thread.currentThread().getName();
			          // 受け取ったデータを出力する
			          System.out.println(threadName + ": " + item);
			          // 次に受け取るデータ数をリクエストする
			          this.subscription.request(1L);
			          i+=1;
			        }

			        // 完了を通知された際の処理
			        @Override
			        public void onComplete() {
			          // 実行しているスレッド名の取得
			          String threadName = Thread.currentThread().getName();
			          System.out.println(threadName + ": 完了しました!"+i+"件の通知を受け取りました。");
			        }

			        // エラーを通知された際の処理
			        @Override
			        public void onError(Throwable error) {
			          error.printStackTrace();
			        }
			      });

			  // しばらく待つ
			try {
				Thread.sleep(5L);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}


	}
}
