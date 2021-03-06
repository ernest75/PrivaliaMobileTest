package com.example.privaliamobiletest.sccreens.main;

import android.util.Log;

import com.example.privaliamobiletest.networking.apimodels.Movie;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter implements MainMVP.Presenter {

    private final String LOG_TAG = getClass().getSimpleName();

    public MainMVP.View mView;
    MainMVP.Model mModel;

    public List<Movie> mMovies = new ArrayList<>();

    public CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MainPresenter(MainMVP.Model mModel) {
        this.mModel = mModel;
    }

    public int mCurrentPageServer;

    private int mTotalPagesCurrentPetition;

    @Override
    public void setView(MainMVP.View view) {
        this.mView = view;
    }

    @Override
    public void loadData() {
        if (mCurrentPageServer > 1) {
            mView.showProgressbarPagination();
        }else{
            mView.showProgressbarBig();
        }
        compositeDisposable.add(
        mModel.getPopularMoviesFromServer(mCurrentPageServer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Movie>() {
                    @Override
                    public void onNext(Movie movie) {
                        mMovies.add(movie);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showErrorFromNetwork(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        handleOnComplete();
                    }
                }));

    }

    @Override
    public void loadSearchedData(CharSequence query) {
        if (mCurrentPageServer>1) {
            mView.showProgressbarPagination();
        }else{
            mView.hideProgressbarPagination();
            mView.showProgressbarBig();
        }
        compositeDisposable.add(
                mModel.getSearchedMovies(mCurrentPageServer,query.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Movie>() {
                    @Override
                    public void onNext(Movie movie) {
                        mMovies.add(movie);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showErrorFromNetwork(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        handleOnComplete();
                    }
                })
        );

    }

    public void handleOnComplete() {
        if(mMovies.isEmpty()){
            mView.showNoResultsFromSearchMessage();
        }
        mView.showData(mMovies);
        mView.hideProgressbarBig();
        mView.hideProgressbarPagination();
        mView.setLoadingToTrue();
        mTotalPagesCurrentPetition = mModel.getTotalPagesCurrentPetition();
        mMovies.clear();
    }

    public int getTotalPagesCurrentPetition() {
        return mTotalPagesCurrentPetition;
    }

    @Override
    public int incrementPageServer() {
        return mCurrentPageServer++;
    }

    @Override
    public int resetPageServer() {
        return mCurrentPageServer = 1;
    }

    @Override
    public int getCurrentPagerServer() {
        return mCurrentPageServer;
    }

    @Override
    public void rxJavaUnsubscribe() {
        compositeDisposable.clear();
    }


}
