package com.aliex.hotfixlib;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

/**
 * Created by Sim.G on 2017/4/19 09:58
 */
public class ReflectUtil {

    public static Object getPathList(Object object)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(Class.forName("dalvik.system.BaseDexClassLoader"), "pathList", object);
    }

    public static Object getDexElements(Object object) throws NoSuchFieldException, IllegalAccessException {
        return getField(object.getClass(), "dexElements", object);
    }

    /**
     * 通过反射获取对象的属性值
     */

    public static Object getField(Class<?> cls, String fieldName, Object obj)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = cls.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    /**
     * 通过反射设置对象的属性值
     */
    public static void setField(Class<?> cl, String fieldName, Object object, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = cl.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    /**
     * 通过反射合并两个数组
     */
    public static Object combineArray(Object obj, Object obj2) {
        Class componentType = obj2.getClass().getComponentType();
        int length = Array.getLength(obj2);
        int length2 = Array.getLength(obj) + length;
        Object newInstance = Array.newInstance(componentType, length2);
        for (int i = 0; i < length2; i++) {
            if (i < length) {
                Array.set(newInstance, i, Array.get(obj2, i));
            } else {
                Array.set(newInstance, i, Array.get(obj, i - length));
            }
        }
        return newInstance;
    }

    public static Object appendArray(Object obj, Object obj2) {
        Class componentType = obj.getClass().getComponentType();
        int length = Array.getLength(obj);
        Object newInstance = Array.newInstance(componentType, length + 1);
        Array.set(newInstance, 0, obj2);
        for (int i = 1; i < length + 1; i++) {
            Array.set(newInstance, i, Array.get(obj, i - 1));
        }
        return newInstance;
    }

}
