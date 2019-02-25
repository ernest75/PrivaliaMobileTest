package com.example.privaliamobiletest.sccreens.main;

import com.example.privaliamobiletest.networking.apimodels.Movie;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter implements MainMVP.Presenter {


    public MainMVP.View mView;

    MainMVP.Model mModel;

    public List<Movie> mMovies = new ArrayList<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MainPresenter(MainMVP.Model mModel) {
        this.mModel = mModel;
    }

    private int mTotalPagesCurrentPetition;

    @Override
    public void setView(MainMVP.View view) {
        this.mView = view;
    }

    @Override
    public void loadData() {
        int currentPage = mView.getCurrentServerPage();
        if (currentPage > 1) {
            mView.showProgressbarPagination();
        }else{
            mView.showProgressbarBig();
        }
        compositeDisposable.add(
        mModel.getPopularMoviesFromServer(mView.getCurrentServerPage())
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
                       //show data amb les movies noves afegides
                        mView.showData(mMovies);
                        mView.hideProgressbarBig();
                        mView.hideProgressbarPagination();
                        mTotalPagesCurrentPetition = mModel.getTotalPagesCurrentPetition();
                        mView.setLoadingToTrue();
                        mMovies.clear();

                    }
                }));

    }

    @Override
    public void loadSearchedData(CharSequence query) {
        int currentPage = mView.getCurrentServerPage();
        if (currentPage>1) {
            mView.showProgressbarPagination();
        }else{
            mView.showProgressbarBig();
        }
        compositeDisposable.add(
                mModel.getSearchedMovies(mView.getCurrentServerPage(),query.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Movie>() {
                    @Override
                    public void onNext(Movie movie) {
                        mMovies.add(movie);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mView.showData(mMovies);
                        mView.hideProgressbarBig();
                        mView.hideProgressbarPagination();
                        mView.setLoadingToTrue();
                        mTotalPagesCurrentPetition = mModel.getTotalPagesCurrentPetition();
                        mMovies.clear();
                    }
                })
        );

    }

    public int getTotalPagesCurrentPetition() {
        return mTotalPagesCurrentPetition;
    }

    @Override
    public void rxJavaUnsubscribe() {
        compositeDisposable.clear();
    }


}
