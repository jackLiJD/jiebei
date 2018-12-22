package com.ald.ebei.ui.ref;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Email: hbh@erongdu.com
 * Created by hebihe on 4/18/16.
 */
public class EbeiRefObject {
    private EbeiRefObject parent;
    private Field field;
    private Object target;

    public EbeiRefObject(Object target) throws EbeiRefException {
        checkArgs(target, null, null, true);
        this.target = target;
        this.parent = null;
        this.field = null;
    }

    public EbeiRefObject(EbeiRefObject parent, Field field) throws EbeiRefException {
        checkArgs(null, parent, field, false);
        this.target = null;
        this.parent = parent;
        this.field = field;
    }

    private void checkArgs(Object target, EbeiRefObject parent, Field field, boolean isPublic) throws EbeiRefException {
        if (isPublic) {
            if (target instanceof EbeiRefObject) {
                throw new EbeiRefException("target must not be a instance of EbeiRefObject!");
            }
        } else {
            if (field == null) {
                throw new EbeiRefException("field must not be a null!");
            }

            if (parent == null) {
                throw new EbeiRefException("parent must not be a null!");
            }

            if (field.getDeclaringClass() == EbeiRefObject.class) {
                throw new EbeiRefException("target must not be a instance of EbeiRefObject!");
            }
        }
    }

    public EbeiRefObject getParent() {
        return this.parent;
    }

    public Class<?> getType() throws EbeiRefException {
        if (field != null) {
            return field.getType();
        }
        final Object obj = this.unwrap();
        if (obj == null) {
            return NULL.class;
        } else {
            return obj.getClass();
        }
    }

    public String getName() {
        if (this.field != null) {
            return this.field.getName();
        } else {
            return null;
        }
    }

    public boolean isClass() throws EbeiRefException {
        final Class<?> clazz = getType();
        return clazz == Class.class;
    }

    public boolean isArray() throws EbeiRefException {
        return getType().isArray();
    }

    public boolean isNull() throws EbeiRefException {
        return this.unwrap() == null;
    }

    /**
     * @param name
     *
     * @return 指定field 值
     *
     * @throws EbeiRefException
     */
    public EbeiRefObject get(String name) throws EbeiRefException {
        if (isTextEmpty(name)) {
            throw new EbeiRefException("");
        } else if (this.isClass()) {
            throw new EbeiRefException("");
        } else if (this.isNull()) {
            throw new EbeiRefException("");
        } else {
            final Field fd = getField(getType(), name);
            return new EbeiRefObject(this, fd);
        }
    }

    /**
     * @param value
     *
     * @return 设置field 值
     *
     * @throws EbeiRefException
     */
    public EbeiRefObject set(Object value) throws EbeiRefException {
        value = unwrap(value);

        if (parent == null) {
            target = value;
        } else {
            try {
                field.set(parent.unwrap(), value);
            } catch (IllegalAccessException e) {
                throw new EbeiRefException("", e);
            } catch (IllegalArgumentException e) {
                throw new EbeiRefException("", e);
            }
        }
        return this;
    }

    public EbeiRefObject set(String name, Object value) throws EbeiRefException {
        final EbeiRefObject refObj = this.get(name);
        refObj.set(value);
        return this;
    }

    private static boolean isTextEmpty(String name) {
        if (name == null || name.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param name
     *
     * @return 是否存在
     *
     * @throws EbeiRefException
     */
    public boolean contains(String name) throws EbeiRefException {
        final Field fd = getField(getType(), name);
        return fd != null;
    }

    public boolean isPrimitive() throws EbeiRefException {
        return EbeiRefWrapper.isPrimitive(unwrap());
    }

    public Class<?> getClazz() throws EbeiRefException {
        return EbeiRefWrapper.wrapClass(unwrap().getClass());
    }

    public int getIntClazz() throws EbeiRefException {
        return EbeiRefWrapper.intWrapClass(unwrap().getClass());
    }

    /**
     * 获取指定
     *
     * @param clazz
     * @param name
     *
     * @return
     */
    private static Field getField(Class<?> clazz, String name) {
        while (clazz != null) {

            try {
                return clazz.getField(name);
            } catch (NoSuchFieldException e) {
            }

            try {
                final Field fd = clazz.getDeclaredField(name);
                return accessible(fd);
            } catch (NoSuchFieldException e) {
            }

            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public List<EbeiRefObject> getAll() throws EbeiRefException {
        List<EbeiRefObject> list = new LinkedList<EbeiRefObject>();

        final List<Field> fdList = getFieldList(getType());

        for (Field fd : fdList) {
            list.add(new EbeiRefObject(this, fd));
        }
        return list;
    }

    public HashMap<String, Method> getPublicMethod() throws EbeiRefException {
        HashMap<String, Method> map = new HashMap<>();
        for(Method method :getMethods(getType())){
            map.put(method.getName(), method);
        }
        return map;
    }

    private static List<Field> getFieldList(Class<?> clazz) {
        final List<Field> fdList = new LinkedList<Field>();
        while (clazz != null) {

            final Field[] fds = clazz.getFields();
            if (fds != null) {
                for (Field fd : fds) {
                    fdList.add(fd);
                }
            }

            final Field[] dfds = clazz.getDeclaredFields();
            if (dfds != null) {
                for (Field fd : dfds) {
                    fdList.add(accessible(fd));
                }
            }

            clazz = clazz.getSuperclass();
        }

        return fdList;
    }

    private static List<Method> getMethods(Class<?> clazz) {
        final List<Method> methodList = new LinkedList<>();
        while (clazz != null) {

            final Method[] methods = clazz.getMethods();
            if (methods != null) {
                for (Method fd : methods) {
                    methodList.add(fd);
                }
            }
            clazz = clazz.getSuperclass();
        }

        return methodList;
    }

    /**
     * getMethod()方法返回的是public的Method对象，
     * getDeclaredMethod()返回的Method对象可以是非public的
     * Field的方法同理
     * 访问私有属性和方法，在使用前要通过AccessibleObject类
     * （Constructor、 Field和Method类的基类）中的setAccessible()方法来抑制Java访问权限的检查。
     */
    private static <T extends AccessibleObject> T accessible(T accessible) {
        if (accessible == null) {
            return null;
        }
        if (accessible instanceof Member) {
            Member member = (Member) accessible;
            if (Modifier.isPublic(member.getModifiers()) && Modifier.isPublic(member.getDeclaringClass().getModifiers())) {
                return accessible;
            }
        }
        if (!accessible.isAccessible()) {
            accessible.setAccessible(true);
        }
        return accessible;
    }

    public Object unwrap() throws EbeiRefException {
        try {
            return target != null ? target : field.get(parent.unwrap());
        } catch (IllegalAccessException e) {
            throw new EbeiRefException("", e);
        } catch (IllegalArgumentException e) {
            throw new EbeiRefException("", e);
        }
    }

    public static EbeiRefObject wrap(Object obj) throws EbeiRefException {
        return new EbeiRefObject(obj);
    }

    public static Object unwrap(Object obj) throws EbeiRefException {
        if (obj instanceof EbeiRefObject) {
            return ((EbeiRefObject) obj).unwrap();
        }
        return obj;
    }

    public static final class NULL {
    }

    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof EbeiRefObject) {
            final EbeiRefObject refO1 = (EbeiRefObject) obj;
            final EbeiRefObject refO2 = this;

            if (refO1.target == refO2.target && refO1.parent == refO2.parent && refO1.field == refO2.field) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("target:").append(target).append(",");

        if (parent == null) {
            sb.append("parent:null,");
        } else {
            sb.append("parent:");
            try {
                sb.append(unwrap(parent));
            } catch (EbeiRefException e) {
                sb.append(e.getMessage());
            }
            sb.append(",");
        }

        if (field == null) {
            sb.append("field:null");
        } else {
            sb.append("field:").append(field.getDeclaringClass().getName()).append("-").append(field.getName());
        }

        return sb.toString();
    }

    public int hashCode() {
        final int hash0 = System.identityHashCode(this.target);
        final int hash1 = System.identityHashCode(this.parent);
        final int hash2 = System.identityHashCode(this.field);
        return hash0 ^ hash1 ^ hash2;
    }
}
