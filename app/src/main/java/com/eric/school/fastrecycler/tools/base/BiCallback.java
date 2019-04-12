package com.eric.school.fastrecycler.tools.base;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/4 0004 17:47
 */
public interface BiCallback<T> {
    void done(T data);
    void error(String message);
}
