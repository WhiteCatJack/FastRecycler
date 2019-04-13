package com.eric.school.fastrecycler.tools.base;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/4 0004 17:47
 */
public interface RpcCallback<T> {
    void onSuccess(T data);
    void onError(String message);
}
