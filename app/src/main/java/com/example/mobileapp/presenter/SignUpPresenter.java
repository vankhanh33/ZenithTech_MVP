package com.example.mobileapp.presenter;

import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.mobileapp.constants.Constants;
import com.example.mobileapp.constract.ApiService;
import com.example.mobileapp.constract.SignUpConstract;
import com.example.mobileapp.model.Account;
import com.example.mobileapp.remote.RetrofitClient;
import com.example.mobileapp.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpPresenter implements SignUpConstract.IPresenter {
    private SignUpConstract.IView mView;
    private ApiService apiService;
    List<Account> accountList = new ArrayList<>();

    public SignUpPresenter(SignUpConstract.IView mView) {
        this.mView = mView;
        apiService = RetrofitClient.getClient(Constants.SERVICE_API).create(ApiService.class);
    }

    @Override
    public void register(String username, String password, String email, List<Account> mList) {
        mView.showProgress();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    mView.showErrorMessage("Không được để trống dữ liệu!");
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        mView.showErrorMessage("Địa chỉ email không hợp lệ!!!");
                    } else {
                        for (int i = 0; i < mList.size(); i++) {
                            if (mList.get(i).getName().equals(username) || mList.get(i).getEmail().equals(email)) {
                                mView.showErrorMessage("Tài khoản hoặc email đã tồn tại");
                                return;
                            }
                        }
                        String pass = Utils.sha256(password);
                        apiService.createAccount(username, email, pass).enqueue(new Callback<Account>() {
                            @Override
                            public void onResponse(Call<Account> call, Response<Account> response) {
                                if (response.isSuccessful())
                                    mView.showSuccessMessage("Đăng ký thành công!");
                            }

                            @Override
                            public void onFailure(Call<Account> call, Throwable t) {

                            }
                        });
                        mView.showSuccessMessage("Đăng ký thành công!");
                    }

                    mView.hideProgress();
                }
            }
        }, 3000);


    }


    @Override
    public void getListAccount() {
        apiService.getListAccount().enqueue(new Callback<List<Account>>() {
            @Override
            public void onResponse(Call<List<Account>> call, Response<List<Account>> response) {
                accountList = response.body();
                mView.setAccountList(accountList);
            }

            @Override
            public void onFailure(Call<List<Account>> call, Throwable t) {

            }
        });
    }

}
