package net.myacxy.squinch;

import android.annotation.SuppressLint;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

import static java.lang.String.format;

@SuppressLint("DefaultLocale")
public class RxJavaTest {

    @Test
    public void example1() throws Exception {
        Observable.range(1, 10)
                .flatMap(i -> {
                    if (i < 5) {
                        return Observable.just(i * i);
                    }
                    return Observable.error(new IOException(format("error: i >= 5 (i=%d)", i)));
                })
                .subscribe(System.out::println, System.out::println);
    }

    @Test
    public void example2() throws Exception {
        Observable.range(1, 10)
                .flatMap(v -> Observable.just(v).delay(10 - v, TimeUnit.SECONDS))
                .blockingSubscribe(System.out::println);
    }

    @Test
    public void example3() throws Exception {
        System.out.println("map");
        Observable.range(1, 10)
                .map(i -> i).doOnNext(System.out::println).doOnComplete(() -> System.out.println("\nflatMap"))
                .flatMap(i -> Observable.just(i).delay(250, TimeUnit.MILLISECONDS))
                .blockingSubscribe(System.out::println);

        System.out.println("\n\nmap");
        Observable.range(1, 10)
                .map(i -> i).doOnNext(System.out::println).doOnComplete(() -> System.out.println("\nconcatMap"))
                .concatMap(i -> Observable.just(i).delay(250, TimeUnit.MILLISECONDS))
                .blockingSubscribe(System.out::println);
    }

    @Test
    public void example5() throws Exception {
        System.out.println("first");
        ValueExample1 rxJava = new ValueExample1();
        rxJava.value = 0;
        rxJava.getValue().doOnSuccess(System.out::println).subscribe();
        rxJava.value += 1;
        rxJava.getValue().doOnSuccess(System.out::println).subscribe();

        System.out.println("\n\nsecond");
        rxJava.value = 0;
        Single<Integer> single = rxJava.getValue();
        rxJava.value += 1;
        single.doOnSuccess(System.out::println).subscribe();
        rxJava.value += 1;
        single.doOnSuccess(System.out::println).subscribe();
    }

    @Test
    public void example6() throws Exception {
        System.out.println("first");
        ValueExample2 rxJava = new ValueExample2();
        rxJava.getValue().subscribe(System.out::println);
        rxJava.setValue(0);
        rxJava.setValue(1);
        rxJava.setValue(2);
    }

    @Test
    public void example7() throws Exception {
        System.out.println("first");
        Observable.range(1, 3)
                .map(i -> i + ", ").doOnNext(s -> System.out.println("map=" + Thread.currentThread()))
                .flatMap(s -> Observable.just(s).doOnNext(s1 -> System.out.println("flatMap=" + Thread.currentThread())))
                .subscribe(s -> System.out.println("doOnNext=" + Thread.currentThread()));
        Thread.sleep(5000);

        System.out.println("\n\nsecond");
        Observable.range(1, 3)
                .map(i -> i + ", ").doOnNext(s1 -> System.out.println("map=" + Thread.currentThread()))
                .flatMap(s2 -> Observable.just(s2).doOnNext(s3 -> System.out.println("flatMap=" + Thread.currentThread())))
                .subscribeOn(Schedulers.single())
                .subscribe(s -> System.out.println("doOnNext=" + Thread.currentThread()));
        Thread.sleep(5000);

        System.out.println("\n\nthird");
        Observable.range(1, 3)
                .map(i -> i + ", ").doOnNext(s1 -> System.out.println("map=" + Thread.currentThread()))
                .flatMap(s2 -> Observable.just(s2).doOnNext(s3 -> System.out.println("flatMap=" + Thread.currentThread())))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.newThread())
                .subscribe(s -> System.out.println("doOnNext=" + Thread.currentThread()));
        Thread.sleep(5000);

        System.out.println("\n\nfourth");
        Observable.range(1, 3)
                .map(i -> i + ", ").doOnNext(s1 -> System.out.println("map=" + Thread.currentThread()))
                .flatMap(s2 -> Observable.just(s2).doOnNext(s3 -> System.out.println("flatMap=" + Thread.currentThread()))
                        .subscribeOn(Schedulers.single())
                )
                .doOnNext(s4 -> System.out.println("doOnNext=" + Thread.currentThread()))
                .subscribeOn(Schedulers.computation())
                .blockingSubscribe();

        System.out.println("\n\nfiveth");
        Observable.range(1, 3)
                .map(i -> i + ", ").doOnNext(s1 -> System.out.println("map=" + Thread.currentThread()))
                .flatMap(s2 -> Observable.just(s2).doOnNext(s3 -> System.out.println("flatMap=" + Thread.currentThread()))
                        .subscribeOn(Schedulers.computation())
                )
                .doOnNext(s4 -> System.out.println("doOnNext=" + Thread.currentThread()))
                .blockingSubscribe();

        Observable.just("Hello world")
                .doOnComplete(() -> System.out.println(Thread.currentThread()))
                .subscribe(); // current thread

        Observable.just("Hello world")
                .delay(5, TimeUnit.SECONDS)
                .doOnComplete(() -> System.out.println(Thread.currentThread()))
                .subscribe(); // computation thread pool

        Observable.timer(5, TimeUnit.SECONDS, Schedulers.io())
                .doOnComplete(() -> System.out.println(Thread.currentThread()))
                .subscribe(); // computation thread pool

        Observable.interval(5, TimeUnit.SECONDS, Schedulers.io())
                .doOnComplete(() -> System.out.println(Thread.currentThread()))
                .subscribe(); // computation thread pool
    }

    @Test
    public void example8() throws Exception {


    }

    public class ValueExample1 {
        Integer value;

        Single<Integer> getValue() {
            return Single.just(value);
        }
    }

    public static class ValueExample2 {
        private BehaviorSubject<Integer> subject = BehaviorSubject.create();
        private Integer value;

        Observable<Integer> getValue() {
            return subject.hide();
        }

        void setValue(int value) {
            this.value = value;
            subject.onNext(value);
        }
    }
}
