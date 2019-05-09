package com.eric.school.fastrecycler.tools.bmobsync;

import com.eric.school.fastrecycler.tools.bean.GarbageCan;
import com.eric.school.fastrecycler.tools.bean.RecycleInstruction;
import com.eric.school.fastrecycler.tools.bean.RecyclerPlace;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/14 0014 16:41
 */
class SyncBmobQueryFindListenerFactory {

    @SuppressWarnings("unchecked")
    static <T> FindListener<T> getFindListener(Class<T> clazz, final FindListener<T> reflect) {
        if (classEquals(GarbageCan.class, clazz)) {
            return (FindListener<T>) new FindListener<GarbageCan>() {
                @Override
                public void done(List<GarbageCan> list, BmobException e) {
                    reflect.done((List<T>) list, e);
                }
            };
        } else if (classEquals(RecyclerPlace.class, clazz)) {
            return (FindListener<T>) new FindListener<RecyclerPlace>() {
                @Override
                public void done(List<RecyclerPlace> list, BmobException e) {
                    reflect.done((List<T>) list, e);
                }
            };
        } else if (classEquals(RecycleInstruction.class, clazz)) {
            return (FindListener<T>) new FindListener<RecycleInstruction>() {
                @Override
                public void done(List<RecycleInstruction> list, BmobException e) {
                    reflect.done((List<T>) list, e);
                }
            };
        }
        return null;
    }

    private static boolean classEquals(Class classA, Class classB) {
        return classA.getName().equals(classB.getName());
    }
}
